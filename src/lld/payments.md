Here is a production-grade architecture design for a **Spring Boot Loan Disbursement Service** built to handle idempotency, distributed timeouts, out-of-order asynchronous webhooks, and provider pluggability.

---

## 1. System Architecture & State Machine

To guarantee that a loan is never disbursed twice and that webhooks/retries are handled safely, we use a **State Machine** backed by database-level pessimistic locking.

```
                  ┌──────────────┐
                  │   PENDING    │
                  └──────┬───────┘
                         │
                 ┌───────┴───────┐
                 ▼               ▼
          ┌─────────────┐ ┌─────────────┐
          │ IN_PROGRESS │ │   FAILED    │
          └──────┬──────┘ └─────────────┘
                 │
        ┌────────┴────────┬───────────────┐
        ▼                 ▼               ▼
┌──────────────┐  ┌──────────────┐  ┌───────────┐
│   SUCCESS    │  │   FAILED     │  │ TIMED_OUT │
└──────────────┘  └──────────────┘  └─────┬─────┘
                                          │ (Webhook/Reconciliation)
                                   ┌──────┴──────┐
                                   ▼             ▼
                            ┌───────────┐ ┌───────────┐
                            │  SUCCESS  │ │  FAILED   │
                            └───────────┘ └───────────┘

```

### State Transitions

* **`PENDING`**: Request received, validated, and persisted.
* **`IN_PROGRESS`**: Acquire DB lock; payload dispatched to provider API.
* **`SUCCESS` / `FAILED**`: Final terminal states.
* **`TIMED_OUT`**: Intermediate state when HTTP call times out. Awaiting webhook or scheduled reconciliation.

---

## 2. Database Schema (PostgreSQL)

To handle idempotency and out-of-order events, we split state into two main tables and an audit trail table.

```sql
-- Core Disbursement Table
CREATE TABLE disbursements (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(64) NOT NULL UNIQUE,      -- Idempotency Key (Client API level)
    loan_id VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) NOT NULL,
    beneficiary_account_id VARCHAR(64) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    provider_type VARCHAR(32) NOT NULL,         -- e.g., RAZORPAY
    provider_reference_id VARCHAR(128) UNIQUE,  -- Provider's transaction ID
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Unique Constraint to prevent multiple SUCCESSFUL disbursements per Loan
CREATE UNIQUE INDEX idx_unique_successful_loan 
ON disbursements (loan_id) 
WHERE status = 'SUCCESS';

-- Webhook Deduplication Table
CREATE TABLE webhook_events (
    event_id VARCHAR(128) PRIMARY KEY,          -- Provider Event ID (e.g., Razorpay Event ID)
    provider VARCHAR(32) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Audit Log Table
CREATE TABLE disbursement_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    disbursement_id BIGINT NOT NULL REFERENCES disbursements(id),
    from_status VARCHAR(32),
    to_status VARCHAR(32) NOT NULL,
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

```

---

## 3. Java Components & Design Patterns

We use the **Strategy Pattern** for payment providers and an **Idempotent Service Framework** for execution control.

### Provider Strategy Abstraction

```java
public enum ProviderType {
    RAZORPAY, CASHFREE
}

public enum DisbursementStatus {
    PENDING, IN_PROGRESS, SUCCESS, FAILED, TIMED_OUT
}

public record DisbursementResponse(
    String providerReferenceId, 
    DisbursementStatus status, 
    String rawResponse
) {}

public interface PaymentProviderStrategy {
    ProviderType getProviderType();
    DisbursementResponse disburse(DisbursementRequest request);
    DisbursementResponse checkStatus(String providerReferenceId);
}

@Service
public class RazorpayProviderStrategy implements PaymentProviderStrategy {
    @Override
    public ProviderType getProviderType() { return ProviderType.RAZORPAY; }

    @Override
    public DisbursementResponse disburse(DisbursementRequest request) {
        try {
            // Execute HTTP call to Razorpay API
            return new DisbursementResponse("payout_12345", DisbursementStatus.SUCCESS, "OK");
        } catch (ResourceAccessException ex) { // Catch Socket Timeout
            return new DisbursementResponse(null, DisbursementStatus.TIMED_OUT, ex.getMessage());
        } catch (Exception ex) {
            return new DisbursementResponse(null, DisbursementStatus.FAILED, ex.getMessage());
        }
    }

    @Override
    public DisbursementResponse checkStatus(String providerReferenceId) {
        // Query Razorpay status API
        return new DisbursementResponse(providerReferenceId, DisbursementStatus.SUCCESS, "OK");
    }
}

@Component
public class ProviderStrategyFactory {
    private final Map<ProviderType, PaymentProviderStrategy> strategies;

    public ProviderStrategyFactory(List<PaymentProviderStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentProviderStrategy::getProviderType, Function.identity()));
    }

    public PaymentProviderStrategy getStrategy(ProviderType providerType) {
        return Optional.ofNullable(strategies.get(providerType))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported Provider: " + providerType));
    }
}

```

---

## 4. Transaction Boundaries & Idempotency Execution Flow

### Core Service Implementation

```java
@Service
@RequiredArgsConstructor
public class LoanDisbursementService {

    private final DisbursementRepository repository;
    private final WebhookEventRepository webhookRepository;
    private final AuditLogRepository auditLogRepository;
    private final ProviderStrategyFactory providerFactory;

    /**
     * Entry Point for Initial Request & Client Retries
     */
    public DisbursementDto processDisbursement(DisbursementRequest req) {
        // Step 1: Pre-check for existing disbursement by RequestId
        Optional<DisbursementEntity> existing = repository.findByRequestId(req.requestId());
        if (existing.isPresent()) {
            DisbursementEntity entity = existing.get();
            // If already completed or in progress, return current state safely
            if (entity.getStatus() == DisbursementStatus.SUCCESS || entity.getStatus() == DisbursementStatus.IN_PROGRESS) {
                return DisbursementDto.from(entity);
            }
        }

        // Step 2: Acquire DB Lock and Initialize Record
        DisbursementEntity entity = executeInNewTransaction(() -> initializeOrFetchRecord(req));

        // Step 3: If terminal state reached in another thread, return early
        if (entity.getStatus() == DisbursementStatus.SUCCESS) {
            return DisbursementDto.from(entity);
        }

        // Step 4: Call External Payment Provider OUTSIDE DB Transaction
        PaymentProviderStrategy provider = providerFactory.getStrategy(req.paymentProvider());
        DisbursementResponse providerResult = provider.disburse(req);

        // Step 5: Update state inside a fresh transaction
        DisbursementEntity updatedEntity = executeInNewTransaction(() -> 
            updateDisbursementState(entity.getId(), providerResult)
        );

        return DisbursementDto.from(updatedEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DisbursementEntity initializeOrFetchRecord(DisbursementRequest req) {
        // Check if loan was ALREADY successfully disbursed
        if (repository.existsByLoanIdAndStatus(req.loanId(), DisbursementStatus.SUCCESS)) {
            throw new IllegalStateException("Loan ID " + req.loanId() + " has already been successfully disbursed.");
        }

        return repository.findByRequestIdForUpdate(req.requestId())
                .orElseGet(() -> {
                    DisbursementEntity record = DisbursementEntity.builder()
                            .requestId(req.requestId())
                            .loanId(req.loanId())
                            .customerId(req.customerId())
                            .beneficiaryAccountId(req.beneficiaryAccountId())
                            .amount(req.amount())
                            .providerType(req.paymentProvider())
                            .status(DisbursementStatus.IN_PROGRESS)
                            .build();
                    record = repository.save(record);
                    recordAuditLog(record.getId(), null, DisbursementStatus.IN_PROGRESS, "Initial Request Received");
                    return record;
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DisbursementEntity updateDisbursementState(Long id, DisbursementResponse response) {
        DisbursementEntity entity = repository.findByIdForUpdate(id).orElseThrow();

        // Terminal state protection
        if (entity.getStatus() == DisbursementStatus.SUCCESS) {
            return entity; 
        }

        DisbursementStatus oldStatus = entity.getStatus();
        entity.setStatus(response.status());
        entity.setProviderReferenceId(response.providerReferenceId());
        
        repository.save(entity);
        recordAuditLog(entity.getId(), oldStatus, response.status(), response.rawResponse());

        return entity;
    }

    /**
     * Webhook Processing - Handles duplicate & out-of-order events
     */
    @Transactional
    public void processWebhook(String eventId, String providerReferenceId, DisbursementStatus newStatus, String provider) {
        // 1. Idempotency Check for Webhook Event ID
        if (webhookRepository.existsById(eventId)) {
            return; // Duplicate webhook, drop execution safely
        }
        webhookRepository.save(new WebhookEvent(eventId, provider, LocalDateTime.now()));

        // 2. Fetch Disbursement Record with Lock
        Optional<DisbursementEntity> entityOpt = repository.findByProviderReferenceIdForUpdate(providerReferenceId);
        if (entityOpt.isEmpty()) {
            // Webhook arrived BEFORE initial API call persisted referenceId (Out-of-Order)
            // Log for delayed retry or manual reconciliation
            return;
        }

        DisbursementEntity entity = entityOpt.get();
        
        // 3. Prevent backwards or duplicate transitions
        if (entity.getStatus() == DisbursementStatus.SUCCESS) {
            return; // Ignore late failure/success webhooks if already SUCCESS
        }

        DisbursementStatus oldStatus = entity.getStatus();
        entity.setStatus(newStatus);
        repository.save(entity);

        recordAuditLog(entity.getId(), oldStatus, newStatus, "Webhook Processed: " + eventId);
    }

    private void recordAuditLog(Long disbursementId, DisbursementStatus from, DisbursementStatus to, String reason) {
        auditLogRepository.save(new DisbursementAuditLog(disbursementId, from, to, reason, LocalDateTime.now()));
    }
}

```

---

## 5. API Layer Design

### POST `/api/v1/disbursements`

```json
// Request
{
  "requestId": "REQ-89032-X821",
  "loanId": "LOAN-100452",
  "customerId": "CUST-9921",
  "beneficiaryAccountId": "ACC-551122",
  "amount": 50000.00,
  "paymentProvider": "RAZORPAY"
}

// Response (200 OK / 202 ACCEPTED)
{
  "disbursementId": 1042,
  "requestId": "REQ-89032-X821",
  "loanId": "LOAN-100452",
  "status": "IN_PROGRESS",
  "providerReferenceId": "payout_9812739182"
}

```

### GET `/api/v1/disbursements/{requestId}/status`

```json
// Response
{
  "requestId": "REQ-89032-X821",
  "loanId": "LOAN-100452",
  "status": "SUCCESS",
  "updatedAt": "2026-07-29T23:30:00Z"
}

```

---

## 6. How System Requirements are Satisfied

| Requirement | Solution Strategy |
| --- | --- |
| **API Retries** | Handled via `requestId` DB unique index and initial lookup check. Returns current state for identical retries. |
| **Single Success Per Loan** | Guaranteed via PostgreSQL Partial Unique Index: `CREATE UNIQUE INDEX ... WHERE status = 'SUCCESS'`. |
| **Provider Timeouts** | Caught in strategy implementation; record transitions to `TIMED_OUT`. Scheduled background worker polls `checkStatus()` for resolving `TIMED_OUT` transactions. |
| **Asynchronous Webhooks** | `webhook_events` table tracks processed `event_id`s. Row-level `FOR UPDATE` lock guarantees state machine transitions occur sequentially. |
| **Auditing** | Every transition triggers an insert into `disbursement_audit_logs`. |

---

## 7. Testing Strategy

### 1. Unit Tests (`JUnit 5` + `Mockito`)

* Mock provider strategies to simulate HTTP time-outs (`ResourceAccessException`), 5xx errors, and successes.
* Verify strategy factory picks correct provider based on enum.

### 2. Integration Tests (`Testcontainers` + PostgreSQL)

* **Concurrency Test**: Fire 10 simultaneous threads using `CountDownLatch` with identical `requestId`s to verify only **one** DB transaction executes the payment.
* **Double Disbursement Prevention**: Fire 2 requests with different `requestId`s but the *same* `loanId`. Ensure the second request fails with `409 Conflict` or business exception.

### 3. Fault-Tolerance Tests (`WireMock`)

* Configure WireMock to introduce artificial latency (e.g., 5 seconds) to trigger timeouts.
* Verify entity moves to `TIMED_OUT`.
* Simulate a late-arriving asynchronous webhook post-timeout and verify the entity transitions smoothly to `SUCCESS`.

---
Switching providers dynamically during a failure requires a **Fallback Strategy Pattern**.

When switching from Razorpay to Cashfree, the critical rule is: **You must NEVER retry with a new provider if Razorpay might have actually processed or is currently processing the payment.** Doing so creates a high risk of double-disbursement.

Here is the complete architectural strategy, state machine update, and Spring Boot implementation to achieve safe automatic/manual provider switching.

---

## 1. When is it Safe to Switch Providers?

You must classify Razorpay errors into **Recoverable/Uncertain** vs. **Deterministic Failures** before attempting a switch.

```
                    ┌───────────────────────────────┐
                    │  Razorpay Attempt (Primary)   │
                    └───────────────┬───────────────┘
                                    │
           ┌────────────────────────┴────────────────────────┐
           ▼                                                 ▼
┌──────────────────────┐                          ┌──────────────────────┐
│ Deterministic Fail   │                          │ Uncertain / Timeout  │
│ (e.g., Invalid Acc,  │                          │ (e.g., HTTP 504,     │
│  Provider Down 503)  │                          │  Socket Timeout)     │
└──────────┬───────────┘                          └──────────┬───────────┘
           │                                                 │
           ▼                                                 ▼
┌──────────────────────┐                          ┌──────────────────────┐
│  SAFE TO SWITCH      │                          │  UNSAFE TO SWITCH    │
│  Trigger Cashfree    │                          │  Set status =        │
│                      │                          │  TIMED_OUT           │
└──────────────────────┘                          │  Wait for Reconciliation/│
                                                  │  Webhook first!      │
                                                  └──────────────────────┘

```

1. **Safe to Switch Immediately (Deterministic Failure):**
* Razorpay API returned an explicit error before generating a payout ID (e.g., `503 Service Unavailable`, `401 Unauthorized`, `Account validation failure`).
* **Action:** Immediately fail the Razorpay attempt and route request to **Cashfree**.


2. **UNSAFE to Switch Immediately (Uncertain/Timeout):**
* Socket read timeout, HTTP `504 Gateway Timeout`, network disconnect after sending the request.
* **Action:** Mark the status as `TIMED_OUT` (or `REQUIRES_RECONCILIATION`). **DO NOT** trigger Cashfree yet.
* A background worker or webhook must poll/confirm that Razorpay **definitely failed** or **cancelled** the request before releasing the record for a Cashfree attempt.



---

## 2. Updated Database Schema

To support multiple attempts with different providers under the same parent loan disbursement request, introduce an `attempts` table or track attempts in the main record.

```sql
-- Track provider attempts per disbursement
CREATE TABLE disbursement_attempts (
    id BIGSERIAL PRIMARY KEY,
    disbursement_id BIGINT NOT NULL REFERENCES disbursements(id),
    provider_type VARCHAR(32) NOT NULL,          -- RAZORPAY, CASHFREE
    provider_reference_id VARCHAR(128),
    attempt_number INT NOT NULL,
    status VARCHAR(32) NOT NULL,                 -- IN_PROGRESS, SUCCESS, FAILED, TIMED_OUT
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index to quickly look up attempts
CREATE INDEX idx_disbursement_attempts ON disbursement_attempts(disbursement_id);

```

---

## 3. Implementation: Circuit Breaker & Fallback Chain Pattern

### Step 1: Interface Update with Non-Retryable Exception Classification

```java
public class ProviderExecutionException extends RuntimeException {
    private final boolean canFallback; // true = deterministic failure, safe to switch

    public ProviderExecutionException(String message, boolean canFallback) {
        super(message);
        this.canFallback = canFallback;
    }

    public boolean isCanFallback() {
        return canFallback;
    }
}

```

### Step 2: Implementation of Provider Fallback Chain

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class LoanDisbursementService {

    private final DisbursementRepository repository;
    private final DisbursementAttemptRepository attemptRepository;
    private final ProviderStrategyFactory providerFactory;

    /**
     * Executes disbursement with automatic provider fallback (Razorpay -> Cashfree)
     */
    public DisbursementDto processDisbursementWithFallback(DisbursementRequest req) {
        // 1. Initial lookup and locking
        DisbursementEntity entity = executeInNewTransaction(() -> initializeOrFetchRecord(req));

        if (entity.getStatus() == DisbursementStatus.SUCCESS) {
            return DisbursementDto.from(entity);
        }

        // 2. Define primary and fallback providers
        List<ProviderType> providerChain = List.of(ProviderType.RAZORPAY, ProviderType.CASHFREE);

        for (int i = 0; i < providerChain.size(); i++) {
            ProviderType currentProvider = providerChain.get(i);
            int attemptNumber = i + 1;

            try {
                log.info("Attempting disbursement for loan {} using provider {}", req.loanId(), currentProvider);
                
                // Execute provider call
                DisbursementResponse response = executeProviderAttempt(entity.getId(), currentProvider, attemptNumber, req);

                if (response.status() == DisbursementStatus.SUCCESS) {
                    return DisbursementDto.from(
                        executeInNewTransaction(() -> finalizeSuccess(entity.getId(), currentProvider, response))
                    );
                }
            } catch (ProviderExecutionException ex) {
                log.warn("Provider {} failed for loan {}. Reason: {}", currentProvider, req.loanId(), ex.getMessage());

                if (!ex.isCanFallback()) {
                    // Timeout or ambiguous error: STOP IMMEDIATELY. Do NOT proceed to Cashfree.
                    executeInNewTransaction(() -> markAsTimedOut(entity.getId(), currentProvider, ex.getMessage()));
                    throw new SystemException("Disbursement state uncertain with " + currentProvider + ". Awaiting reconciliation.");
                }

                // If this is the last provider in chain, raise error
                if (i == providerChain.size() - 1) {
                    executeInNewTransaction(() -> markAsFailed(entity.getId(), "All providers exhausted. Last error: " + ex.getMessage()));
                    throw new SystemException("Disbursement failed across all payment providers.");
                }

                log.info("Safe deterministic failure detected. Switching from {} to next provider...", currentProvider);
                // Loop continues to next provider (e.g., CASHFREE)
            }
        }

        throw new SystemException("Unable to process disbursement.");
    }

    private DisbursementResponse executeProviderAttempt(
            Long disbursementId, 
            ProviderType providerType, 
            int attemptNumber, 
            DisbursementRequest req) {
        
        // Save Attempt Log in Database
        DisbursementAttemptEntity attempt = new DisbursementAttemptEntity(
                disbursementId, providerType, attemptNumber, DisbursementStatus.IN_PROGRESS
        );
        executeInNewTransaction(() -> attemptRepository.save(attempt));

        PaymentProviderStrategy provider = providerFactory.getStrategy(providerType);

        try {
            DisbursementResponse response = provider.disburse(req);
            
            // Check if response indicates deterministic provider failure
            if (response.status() == DisbursementStatus.FAILED) {
                throw new ProviderExecutionException("Provider rejected payout: " + response.rawResponse(), true);
            }
            
            return response;
        } catch (ResourceAccessException ex) {
            // Network Socket Timeout -> UNSAFE to fallback
            throw new ProviderExecutionException("Network timeout communicating with " + providerType, false);
        } catch (Exception ex) {
            // General 5xx / connection refused before request sent -> SAFE to fallback
            throw new ProviderExecutionException("System failure with " + providerType + ": " + ex.getMessage(), true);
        }
    }
}

```

---

## 4. Resilience4j Circuit Breaker Integration (Automatic Switching)

Instead of manually trying Razorpay on every request when Razorpay is experiencing an outage, use a **Circuit Breaker** to auto-route traffic to Cashfree immediately.

```yaml
# application.yml
resilience4j.circuitbreaker:
  instances:
    razorpayDisbursement:
      slidingWindowSize: 10
      minimumNumberOfCalls: 5
      failureRateThreshold: 50 # Switch to OPEN if 50% requests fail
      waitDurationInOpenState: 60000ms # Stay OPEN for 60s before retrying Razorpay
      permittedNumberOfCallsInHalfOpenState: 3

```

```java
@Service
@RequiredArgsConstructor
public class ResilientDisbursementService {

    private final RazorpayProviderStrategy razorpayStrategy;
    private final CashfreeProviderStrategy cashfreeStrategy;

    @CircuitBreaker(name = "razorpayDisbursement", fallbackMethod = "cashfreeFallback")
    public DisbursementResponse disburse(DisbursementRequest request) {
        return razorpayStrategy.disburse(request);
    }

    // Triggered automatically when Razorpay Circuit is OPEN or throws a deterministic exception
    public DisbursementResponse cashfreeFallback(DisbursementRequest request, Throwable t) {
        log.warn("Razorpay circuit is OPEN or failing. Routing request directly to Cashfree. Root Cause: {}", t.getMessage());
        return cashfreeStrategy.disburse(request);
    }
}

```

---

## 5. Summary Matrix for Switching Logic

| Failure Scenario | Is Razorpay State Clear? | Action to Take | Next Step |
| --- | --- | --- | --- |
| **Razorpay HTTP 503 / 502** | Yes (Not Received) | Fail Attempt 1 | **Switch instantly to Cashfree** |
| **Razorpay Circuit Breaker OPEN** | Yes (Bypassed) | Skip Razorpay | **Route directly to Cashfree** |
| **Razorpay Invalid Beneficiary IFSC** | Yes (Rejected) | Mark Request FAILED | **Do NOT switch** (Will fail on Cashfree too) |
| **Razorpay HTTP Read Timeout** | **NO (Uncertain)** | Mark `TIMED_OUT` | **Do NOT switch**. Run scheduled worker to query Razorpay `/payouts/{id}` status first. Switch to Cashfree only if Razorpay confirms "REJECTED/CANCELLED". |

You hit on the exact conflict that plagues fintech architecture: **UX wants instant completion, but Financial Accounting strictly forbids double-disbursement.**

If a customer hits "Retry" while the primary attempt is stuck in `IN_PROGRESS` or `TIMED_OUT`, simply letting them trigger a second payout through Cashfree will eventually lead to two real transfers leaving your account for the same loan.

Here is how high-scale payout platforms solve this exact dilemma using **Fast Sync Verification**, **UI State Locks**, and **Pre-Routing**.

---

## The Solution: Active Fast Verification Pattern

Instead of putting the request into a passive background queue and telling the customer to wait hours, you execute an **Active Sync Verification** before making the decision to switch or return.

```
Customer Clicks "Disburse"
           │
           ▼
┌──────────────────────────────┐
│  Try Razorpay (Timeout 3s)   │
└──────────┬───────────────────┘
           │
     [ HTTP Timeout ]
           │
           ▼
┌──────────────────────────────┐
│ FAST SYNC VERIFICATION       │
│ Call Razorpay GET /payouts   │
│ with tight timeout (1.5s)    │
└──────────┬───────────────────┘
           │
     ┌─────┴──────────────────────────────┐
     ▼                                    ▼
[ State: NOT_FOUND / FAILED ]      [ State: PROCESSING / SUCCESS ]
     │                                    │
     ▼                                    ▼
┌──────────────────────────┐       ┌──────────────────────────┐
│ SAFE TO SWITCH           │       │ DO NOT SWITCH            │
│ Instantly try Cashfree   │       │ Return 202 Accepted      │
│ (User gets money in <5s) │       │ UI Polling Window        │
└──────────────────────────┘       └──────────────────────────┘

```

---

## 1. Fast Sync Verification (Java Code)

When Razorpay's **creation API** times out, do not immediately fail. Make an immediate, synchronous **fetch call** using your `requestId` or `referenceId`.

```java
public DisbursementResponse handleTimeoutAndAttemptFallback(
        DisbursementRequest req, 
        DisbursementEntity entity) {

    log.warn("Razorpay payout call timed out. Executing Fast Sync Verification for loan {}", req.loanId());

    // Step 1: Active Sync Poll to Razorpay (1.5-second hard timeout)
    PaymentProviderStrategy razorpay = providerFactory.getStrategy(ProviderType.RAZORPAY);
    
    // Query provider by idempotency/reference key
    Optional<DisbursementResponse> statusCheck = razorpay.fetchStatusByRequestId(req.requestId());

    if (statusCheck.isPresent() && statusCheck.get().status() == DisbursementStatus.FAILED) {
        // Razorpay explicitly confirms the payout FAILED or was NEVER CREATED
        log.info("Razorpay verified as FAILED. Executing fast fallback to Cashfree...");
        
        // Step 2: Safe to switch immediately in the SAME HTTP request!
        PaymentProviderStrategy cashfree = providerFactory.getStrategy(ProviderType.CASHFREE);
        return cashfree.disburse(req);
    } 

    if (statusCheck.isPresent() && statusCheck.get().status() == DisbursementStatus.SUCCESS) {
        // Razorpay actually succeeded! Return success to user.
        return statusCheck.get();
    }

    // Step 3: State is STILL uncertain (Razorpay API down or processing). 
    // We MUST lock the loan and return a "PROCESSING" response to UI.
    return new DisbursementResponse(null, DisbursementStatus.IN_PROGRESS, "PAYMENT_UNDER_PROCESSING");
}

```

---

## 2. Handling the Customer's "Immediate Retry" (Frontend + API Contract)

When a payment is genuinely in an uncertain state, how do you handle an impatient customer hitting "Retry"?

### A. Strict Idempotency Response (HTTP 202 Accepted)

When the status is `IN_PROGRESS` or `TIMED_OUT`, any subsequent retry with the **same `requestId` or `loanId**` must **NOT** trigger a new disbursement. It must return the existing status immediately.

```json
// HTTP 202 Accepted (Returned on Retry)
{
  "status": "PROCESSING",
  "message": "Your transfer is being confirmed with the bank. Please do not close this window.",
  "loanId": "LOAN-100452",
  "pollUrl": "/api/v1/disbursements/LOAN-100452/status",
  "retryAllowed": false
}

```

### B. The 30-Second UI "Optimistic Progress Bar" Pattern

Instead of showing an error or a generic spinner, the mobile app / web app displays an active verification screen:

1. **Disable the Retry Button** during the 30-second window.
2. **Poll the Backend**: The UI polls `/api/v1/disbursements/{loanId}/status` every 2 seconds.
3. **Automatic Resolution**: Within 5–10 seconds, either the Razorpay webhook arrives, or a background reconciliation job resolves it to `SUCCESS` or `FAILED`.
4. If it resolves to `FAILED`, the UI automatically enables the **"Retry via Alternate Network"** button (which now safely calls Cashfree with a *new attempt*).

---

## 3. Preventive Health Pre-Routing (Avoiding the Timeout Entirely)

The best way to deliver an instant customer experience is to **never hit Razorpay in the first place** if its latency is spiking.

```java
@Service
@RequiredArgsConstructor
public class SmartProviderRouter {

    private final RedisTemplate<String, String> redis;

    public ProviderType selectBestProvider() {
        // Check real-time provider health metrics stored in Redis/Prometheus
        String razorpayStatus = redis.opsForValue().get("HEALTH:PROVIDER:RAZORPAY"); // HEALTHY, DEGRADED, DOWN
        
        if ("DEGRADED".equals(razorpayStatus) || "DOWN".equals(razorpayStatus)) {
            log.info("Pre-routing traffic to Cashfree due to Razorpay latency/degradation.");
            return ProviderType.CASHFREE; // Zero customer impact
        }

        return ProviderType.RAZORPAY;
    }
}

```

---

## Architectural Comparison

| Approach | Customer UX | Financial Safety | Complexity |
| --- | --- | --- | --- |
| **Naive Fallback** (Try Cashfree instantly on Razorpay timeout) | **Fastest** | **UNSAFE** (High risk of double disbursement) | Low |
| **Passive Waiting** (Mark Timed Out, wait for webhooks) | **Poor** (User left hanging for hours) | **SAFE** | Low |
| **Fast Sync Verification + UI Polling** | **High** (< 5s resolution in 99% of cases) | **SAFE** | Medium |
| **Smart Health Pre-Routing** | **Optimal** (Avoids bad provider before hitting it) | **SAFE** | High |

To handle loan disbursements stuck in a `TIMED_OUT` or `IN_PROGRESS` state without forcing the customer to wait infinitely, you need a resilient **Reconciliation Engine**.

This system uses a **Spring Scheduled Worker** with an **Exponential Backoff Strategy** and pessimistic locking to safely poll providers, resolve state, and—if safe—trigger an immediate fallback to Cashfree.

---

## 1. Reconciliation Strategy & Logic Workflow

When the worker picks up a `TIMED_OUT` disbursement record, it follows this strict state verification pipeline:

```
                      ┌─────────────────────────────────┐
                      │  Scheduled Worker Executed      │
                      │  (Picks TIMED_OUT records)      │
                      └────────────────┬────────────────┘
                                       │
                                       ▼
                      ┌─────────────────────────────────┐
                      │ Lock Record (SELECT ... FOR UPDATE)
                      └────────────────┬────────────────┘
                                       │
                                       ▼
                      ┌─────────────────────────────────┐
                      │ Call Provider GET /status API    │
                      └────────────────┬────────────────┘
                                       │
           ┌───────────────────────────┼───────────────────────────┐
           ▼                           ▼                           ▼
 ┌───────────────────┐       ┌───────────────────┐       ┌───────────────────┐
 │ Status: SUCCESS   │       │ Status: FAILED /  │       │ Status: STILL     │
 │                   │       │         NOT_FOUND │       │         PROCESSING│
 └─────────┬─────────┘       └─────────┬─────────┘       └─────────┬─────────┘
           │                           │                           │
           ▼                           ▼                           ▼
 ┌───────────────────┐       ┌───────────────────┐       ┌───────────────────┐
 │ Update DB State:  │       │ 1. Mark attempt   │       │ Increments retry  │
 │ SUCCESS           │       │    FAILED         │       │ count.            │
 │ (Reconciliation   │       │ 2. Trigger Auto   │       │ Keep TIMED_OUT.   │
 │ Complete)         │       │    Fallback to    │       │ (Wait for next    │
 └───────────────────┘       │    Cashfree       │       │  cron cycle)      │
                             └───────────────────┘       └───────────────────┘

```

---

## 2. Supporting Repository Query

To avoid processing records that are actively being modified by webhooks or user retries, fetch records using a **Pessimistic Skip Locked** query.

```java
public interface DisbursementRepository extends JpaRepository<DisbursementEntity, Long> {

    // Fetch records stuck in TIMED_OUT that haven't exceeded max retry count
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // SKIP LOCKED
    @Query("""
        SELECT d FROM DisbursementEntity d 
        WHERE d.status = 'TIMED_OUT' 
          AND d.reconciliationRetryCount < :maxRetries 
          AND d.nextReconciliationAt <= :now
    """)
    List<DisbursementEntity> findStuckDisbursementsForUpdate(
        @Param("maxRetries") int maxRetries, 
        @Param("now") LocalDateTime now, 
        Pageable pageable
    );
}

```

---

## 3. Implementation of the Scheduled Reconciliation Worker

The worker runs on a configurable cron schedule, uses exponential backoff to avoid hammering provider APIs, and executes state updates inside dedicated transaction boundaries.

```java
package com.example.disbursement.scheduler;

import com.example.disbursement.entity.DisbursementEntity;
import com.example.disbursement.enums.DisbursementStatus;
import com.example.disbursement.enums.ProviderType;
import com.example.disbursement.repository.DisbursementRepository;
import com.example.disbursement.service.LoanDisbursementService;
import com.example.disbursement.strategy.PaymentProviderStrategy;
import com.example.disbursement.strategy.ProviderStrategyFactory;
import com.example.disbursement.dto.DisbursementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReconciliationWorker {

    private final DisbursementRepository repository;
    private final ProviderStrategyFactory providerFactory;
    private final LoanDisbursementService disbursementService;

    private static final int MAX_RECON_RETRIES = 5;
    private static final int BATCH_SIZE = 50;

    /**
     * Cron runs every 30 seconds to pick up TIMED_OUT records
     */
    @Scheduled(cron = "${app.reconciliation.cron:*/30 * * * * *}")
    public void processTimedOutDisbursements() {
        List<DisbursementEntity> stuckRecords = repository.findStuckDisbursementsForUpdate(
                MAX_RECON_RETRIES, 
                LocalDateTime.now(), 
                PageRequest.of(0, BATCH_SIZE)
        );

        if (stuckRecords.isEmpty()) {
            return;
        }

        log.info("Found {} stuck disbursements needing reconciliation.", stuckRecords.size());

        for (DisbursementEntity entity : stuckRecords) {
            try {
                reconcileSingleRecord(entity);
            } catch (Exception e) {
                log.error("Error reconciling disbursement record ID: {}", entity.getId(), e);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reconcileSingleRecord(DisbursementEntity entity) {
        log.info("Reconciling Loan ID: {} with Provider: {}", entity.getLoanId(), entity.getProviderType());

        PaymentProviderStrategy provider = providerFactory.getStrategy(entity.getProviderType());

        // 1. Query the external provider's GET /status API
        DisbursementResponse statusResponse = provider.checkStatus(entity.getProviderReferenceId());

        if (statusResponse.status() == DisbursementStatus.SUCCESS) {
            log.info("Provider confirmed SUCCESS for loan {}", entity.getLoanId());
            entity.setStatus(DisbursementStatus.SUCCESS);
            entity.setUpdatedAt(LocalDateTime.now());
            repository.save(entity);
            
            disbursementService.recordAuditLog(
                entity.getId(), DisbursementStatus.TIMED_OUT, DisbursementStatus.SUCCESS, 
                "Resolved via Reconciliation Worker"
            );

        } else if (statusResponse.status() == DisbursementStatus.FAILED) {
            log.warn("Provider confirmed FAILED for loan {}. Triggering Fallback to Cashfree...", entity.getLoanId());
            
            // Mark primary attempt as failed
            entity.setStatus(DisbursementStatus.FAILED);
            repository.save(entity);

            disbursementService.recordAuditLog(
                entity.getId(), DisbursementStatus.TIMED_OUT, DisbursementStatus.FAILED, 
                "Primary provider confirmed failure during reconciliation"
            );

            // Trigger safe automated fallback attempt to Cashfree
            disbursementService.triggerAutomatedFallback(entity, ProviderType.CASHFREE);

        } else {
            // State is still PROCESSING or UNKNOWN at provider side -> Schedule next retry with Exponential Backoff
            int nextRetryCount = entity.getReconciliationRetryCount() + 1;
            entity.setReconciliationRetryCount(nextRetryCount);
            
            // Exponential Backoff: 1 min, 2 min, 4 min, 8 min, 16 min...
            long backoffMinutes = (long) Math.pow(2, nextRetryCount - 1);
            entity.setNextReconciliationAt(LocalDateTime.now().plusMinutes(backoffMinutes));

            if (nextRetryCount >= MAX_RECON_RETRIES) {
                log.error("Exhausted maximum reconciliation retries for loan {}. Requires manual intervention.", entity.getLoanId());
                entity.setStatus(DisbursementStatus.REQUIRES_MANUAL_INTERVENTION);
            }

            repository.save(entity);
            log.info("Postponed next reconciliation for loan {} to {}", entity.getLoanId(), entity.getNextReconciliationAt());
        }
    }
}

```

---

## 4. Entity Schema Extensions

Add operational tracking fields to your core entity mapping:

```java
@Entity
@Table(name = "disbursements")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DisbursementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String loanId;
    private String customerId;
    private String beneficiaryAccountId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    private String providerReferenceId;

    @Enumerated(EnumType.STRING)
    private DisbursementStatus status;

    // Reconciliation Metadata
    @Builder.Default
    private Integer reconciliationRetryCount = 0;

    @Builder.Default
    private LocalDateTime nextReconciliationAt = LocalDateTime.now();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

```

---

## 5. Distributed Locking (ShedLock) for Multi-Node Deployments

In a production environment running multiple instances of your Spring Boot microservice, running `@Scheduled` tasks on every node can cause database contention. Use **ShedLock** to ensure only one pod runs the reconciliation job at a time.

### Dependency Configuration (`pom.xml`)

```xml
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-spring</artifactId>
    <version>5.10.0</version>
</dependency>
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-provider-jdbc-template</artifactId>
    <version>5.10.0</version>
</dependency>

```

### ShedLock Configuration & Method Annotation

```java
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime()
                .build()
        );
    }
}

// In ReconciliationWorker.java
@Scheduled(cron = "${app.reconciliation.cron:*/30 * * * * *}")
@SchedulerLock(name = "DisbursementReconciliationTask", lockAtMostFor = "2m", lockAtLeastFor = "15s")
public void processTimedOutDisbursements() {
    // Execution logic remains identical
}

```

---

## 6. How This Architecture Solves Key Production Edge Cases

| Scenario | Solution |
| --- | --- |
| **Multiple Microservice Instances** | ShedLock ensures only **one worker node** executes the reconciliation batch cycle. |
| **Concurrent Webhook & Worker Arrival** | `PESSIMISTIC_WRITE` (Row Lock) + `SKIP LOCKED` prevents race conditions between incoming asynchronous webhooks and the worker thread. |
| **Provider API Outage** | Exponential backoff ($2^n$ minutes) prevents hammering a failing provider's status endpoints. |
| **Permanent Ambiguity** | After reaching `MAX_RECON_RETRIES`, the entity enters `REQUIRES_MANUAL_INTERVENTION` and triggers a Slack/PagerDuty alert to ops teams. |