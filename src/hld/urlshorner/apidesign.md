# Solution Architecture Notes: URL Shortener API Design

This document synthesizes the architectural decisions, API contracts, design trade-offs, and operational safeguards outlined in the transcript into a production-grade system specification.

---

## 1. Core Architectural Philosophy

When designing system APIs, start with **User Actions** (Use-Case Driven Design):

1. **Creation:** Users want to shorten long URLs (with optional custom branding).
2. **Redirection:** Users/visitors navigate to short URLs and must reach the target destination.
3. **Observability:** Link owners want detailed metrics on traffic, demographics, and devices.

---

## 2. API Endpoint Specification & Contracts

### Base Pathing & Versioning Strategy

* **Strategy:** **Explicit Path Versioning** (`/api/v1/...`) over Header-based versioning.
* **Rationale:** Higher visibility, easier curl/Postman testing, and prevents breaking clients when API structures evolve over time.

---

### Endpoint 1: Create Short URL

* **HTTP Method & Path:** `POST /api/v1/shorten`
* **Authentication:** Optional (Anonymous allowed for basic usage; Auth required for custom aliases/higher limits).

#### Request Headers

```http
Content-Type: application/json
X-API-Key: <OPTIONAL_API_KEY>      -- For programmatic access
Authorization: Bearer <JWT_TOKEN> -- For web user sessions

```

#### Request Payload

```json
{
  "long_url": "https://www.amazon.com/dp/B08N5WRWNW?ref_=ast_sto_dp",
  "custom_alias": "summer-sale"
}

```

#### Response Payload (`201 Created`)

```json
{
  "short_url": "https://short.ly/summer-sale",
  "long_url": "https://www.amazon.com/dp/B08N5WRWNW?ref_=ast_sto_dp",
  "created_at": "2026-08-12T19:27:00Z",
  "expires_at": "2027-08-12T19:27:00Z"
}

```

> **Architectural Note:** Returning the `long_url` in the response provides payload completeness. The client can verify what got stored without maintaining local state.

---

### Endpoint 2: Bulk Shorten URLs (Enterprise Feature)

* **HTTP Method & Path:** `POST /api/v1/bulk-shorten`
* **Authentication:** Required
* **Constraint:** Capped at **100 URLs per payload** to prevent server memory exhaustion.

#### Request Payload

```json
{
  "urls": [
    { "long_url": "https://example.com/item1", "custom_alias": "alias-1" },
    { "long_url": "https://example.com/item2" }
  ]
}

```

#### Response Payload (`207 Multi-Status` / `200 OK`)

```json
{
  "data": [
    {
      "short_url": "https://short.ly/alias-1",
      "long_url": "https://example.com/item1",
      "status": "CREATED"
    },
    {
      "short_url": "https://short.ly/xyz987",
      "long_url": "https://example.com/item2",
      "status": "CREATED"
    }
  ]
}

```

---

### Endpoint 3: Redirect Link (Core Path)

* **HTTP Method & Path:** `GET /{shortCode}`
* **Authentication:** None (Public)
* **Status Code:** `302 Found` (Temporary Redirect)

#### Response Headers

```http
HTTP/1.1 302 Found
Location: https://www.amazon.com/dp/B08N5WRWNW?ref_=ast_sto_dp
Cache-Control: no-cache, no-store, must-revalidate

```

#### Error Response (`404 Not Found`)

```json
{
  "error": "RESOURCE_NOT_FOUND",
  "message": "The requested short link does not exist, has expired, or was removed."
}

```

---

### Endpoint 4: Link Analytics

* **HTTP Method & Path:** `GET /api/v1/stats/{shortCode}`
* **Authentication:** Required (Owner of the link)

#### Response Payload (`200 OK`)

```json
{
  "short_code": "summer-sale",
  "total_clicks": 15420,
  "unique_clicks": 11200,
  "clicks_by_country": {
    "US": 8200,
    "IN": 4100,
    "UK": 3120
  },
  "clicks_by_device": {
    "mobile": 10500,
    "desktop": 4920
  },
  "referrers": {
    "twitter.com": 9100,
    "direct": 4000,
    "google.com": 2320
  }
}

```

---

## 3. Key Architectural Trade-Off Decisions

```text
                                  Key Architectural Decisions
                                               │
             ┌─────────────────────────────────┴─────────────────────────────────┐
             ▼                                                                   ▼
┌─────────────────────────────────────────┐                         ┌─────────────────────────────────────────┐
│     301 Permanent vs 302 Temporary      │                         │  Duplicate URL Deduplication Strategy   │
├─────────────────────────────────────────┤                         ├─────────────────────────────────────────┤
│ • 301: Browser caches redirect          │                         │ • Deduplicate: Saves DB storage space   │
│   --> Reduces server traffic            │                         │   --> Shared analytics across users     │
│   --> Destroys click analytics tracking │                         │ • Always Unique: Generates new code     │
│                                         │                         │   --> Isolated analytics per owner      │
│ • 302: Forces requests to hit server    │                         │   --> Storage is cheap (Selected)       │
│   --> Enables 100% telemetry capture    │                         │                                         │
│   --> Selected as optimal choice        │                         │                                         │
└─────────────────────────────────────────┘                         └─────────────────────────────────────────┘

```

### Decision A: `301 Permanent` vs `302 Temporary` Redirects

| Criteria | `301 Moved Permanently` | `302 Found` (Chosen) |
| --- | --- | --- |
| **Browser Behavior** | Caches target address locally. Subsequent clicks bypass edge/origin server. | Never caches permanently. Queries server on every click. |
| **Server Load** | **Extremely Low** (Server only sees initial request per user). | **Higher** (Server sees every click). |
| **Analytics Accuracy** | **Poor** (Misses repeat visits entirely). | **100% Accurate** (Captures every event). |
| **Verdict** | Unsuitable for commercial link management. | **Standard Choice** for analytics-driven shorteners. |

---

### Decision B: Duplicate Long URL Handling (Deduplication)

* **Option 1 (Chosen): Always Generate a Unique Short Code**
* *Pros:* Preserves strict analytics isolation per user/campaign.
* *Cons:* Higher storage utilization.
* *Justification:* Storage costs are minimal compared to the business value of accurate, isolated campaign attribution.


* **Option 2: Re-use Existing Short Code**
* *Pros:* Saves database space.
* *Cons:* Blends analytics across unrelated users creating the same destination link.



---

### Decision C: Custom Alias Validation Pipeline

Rather than adding an explicit `/api/v1/check-availability` endpoint (which creates extra round-trips), perform **Inline Validation** inside `POST /api/v1/shorten`.

* If available $\rightarrow$ `201 Created`.
* If taken $\rightarrow$ `409 Conflict` (Client handles retries).

---

## 4. Asynchronous Data Pipeline & Non-Blocking Telemetry

Performing geo-ip lookup, user-agent parsing, and database writes synchronously inside the `302 Redirect` execution path introduces unacceptable latency ($>200\text{ms}$).

### High-Throughput Processing Architecture

```text
[ Client Request ]
       │
       ▼
┌──────────────┐      1. Quick DB/Cache Lookup      ┌──────────────┐
│  API Server  │ ─────────────────────────────────> │ Redis Cache  │
└──────┬───────┘                                    └──────────────┘
       │
       ├─ 2. Immediate Response: HTTP 302 Redirect
       │
       └─ 3. Non-Blocking Event Push
               │
               ▼
   ┌───────────────────────┐
   │ Kafka / Message Queue │ (Payload: IP, User-Agent, Referrer, Timestamp)
   └───────────┬───────────┘
               │
               ▼
   ┌───────────────────────┐
   │ Async Stream Workers  │ (Parses GeoIP & Device Types)
   └───────────┬───────────┘
               │
               ▼
   ┌───────────────────────┐
   │ Analytics Store DB    │ (ClickHouse / Cassandra / Redis HyperLogLog)
   └───────────────────────┘

```

---

## 5. Security, Validation & Rate Limiting

### I. Input Validation & Filtering

* **Protocol Enforcement:** Restrict target URLs strictly to `http://` and `https://`. Block malicious protocols (`file://`, `ftp://`, `javascript:` execution strings).
* **Domain Check & DNS Validation:** Validate domain structure and execute asynchronous DNS lookup to confirm host existence.
* **Blocklists:** Enforce malicious domain filtering against phishing lists (Google Safe Browsing) and system domain reserved words (`/admin`, `/api`, `/login`, `/stats`).

---

### II. Multi-Tiered Rate Limiting

Enforce rate limits per unit time using standard response headers to prevent abuse and denial-of-service attempts.

```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 98
X-RateLimit-Reset: 1770924420

```

| User Category | Identification Mechanism | Rate Limit Target |
| --- | --- | --- |
| **Anonymous** | Client IP Address | 10 requests / hour |
| **Registered User** | JWT Bearer Token | 100 requests / hour |
| **Premium / Enterprise** | API Key (`X-API-Key`) | 10,000 requests / hour |

---

## 6. HTTP Error Handling Matrix

| Scenario | HTTP Status Code | Response Payload Message |
| --- | --- | --- |
| **Malformed Long URL** | `400 Bad Request` | `"URL string is invalid or uses an unsupported protocol."` |
| **Invalid Auth Token/Key** | `401 Unauthorized` | `"Missing or invalid authentication credentials."` |
| **Short Code Not Found** | `404 Not Found` | `"Short URL does not exist or has expired."` |
| **Custom Alias Taken** | `409 Conflict` | `"The requested custom alias is already in use."` |
| **Rate Limit Exceeded** | `429 Too Many Requests` | `"Rate limit exceeded. Retry after timestamp in header."` |
| **Internal Server Fault** | `500 Internal Error` | `"An unexpected error occurred on our servers."` |

---
Here is a detailed, interview-ready breakdown of the URL shortener API design. It is structured the way a Senior Backend / Systems Design engineer would present and justify their choices in an actual technical interview.

---

# System Design Interview: URL Shortener API Layer

## 1. High-Level Requirements & Design Philosophy

**Interviewer Prompt:** *"How do you begin designing the API for a high-scale URL shortener like Bitly?"*

**Candidate Answer:**

> "I start by anchoring on the core user actions—what people actually need to achieve—rather than jumping straight to database schemas. For a URL shortener, there are three primary operations:
> 1. **Shorten:** Creating a compact alias for a long destination URL.
> 2. **Redirect:** Resolving the short alias back to the original destination with minimal latency.
> 3. **Analyze:** Fetching engagement metrics for a given link.
>
>
> Once these operations are defined, every API contract, status code, and header must balance two competing concerns: **client simplicity** and **system performance under scale**."

---

## 2. Endpoint: URL Creation

### API Contract

* **Route:** `POST /api/v1/shorten`
* **Request Body:**
```json
{
  "long_url": "https://www.amazon.com/dp/B08N5WRWNW?ref=my_deal",
  "custom_alias": "summer-sale"
}

```


* **Response Body (`201 Created` / `200 OK`):**
```json
{
  "short_url": "https://short.url/summer-sale",
  "long_url": "https://www.amazon.com/dp/B08N5WRWNW?ref=my_deal",
  "created_at": "2026-09-14T08:15:30Z",
  "expires_at": "2028-09-14T08:15:30Z"
}

```



### Deep Dive & Trade-offs

#### Q: Why `POST /api/v1/shorten` instead of standard REST `POST /api/v1/urls`?

* **Interview Explanation:**
  *"While strict REST practitioners advocate for noun-based resource naming like `/urls`, an endpoint named `/shorten` is an action-oriented RPC-style naming convention. In real-world API design, clarity and developer ergonomics often outweigh dogmatic REST purity. `/shorten` makes the system's intent completely unambiguous."*

#### Q: Why make `custom_alias` optional?

* **Interview Explanation:**
  *"Over 90% of end users just want a short link generated quickly without extra inputs. Custom aliases (e.g., `nike.link/summer-sale`) are primarily requested by marketing teams, enterprise accounts, and branding campaigns. Making it optional optimizes for the common case while supporting power users."*

#### Q: Why return `long_url` back in the response if the client just sent it?

* **Interview Explanation:**
  *"It provides idempotency verification and state completeness. The client application can confirm exactly how the backend sanitized and parsed their input without maintaining secondary state in memory or making a subsequent verification call."*

#### Q: Critical Decision: What happens if two users shorten the identical long URL?

There are two architectural choices:

* **Option A: De-duplicate (Return existing short code).**
* *Pros:* Saves database row storage.
* *Cons:* Multiple independent users end up sharing the exact same analytics dashboard. User A's marketing campaign clicks would contaminate User B's link data.


* **Option B: Always generate a new unique short code.**
* *Pros:* True per-user ownership and clean, isolated analytics pipelines.
* *Cons:* Mild database duplication.


* **Senior Engineer Verdict:**
  *"Go with **Option B**. Storage is cheap; analytics integrity is not. Users pay for accurate click data. Companies like Bitly choose distinct short codes per user request for this exact reason."*

---

## 3. Endpoint: Core Redirection

### API Contract

* **Route:** `GET /{short_code}` (e.g., `GET /abc123`)
* **Response Headers:**
* `HTTP/1.1 302 Found`
* `Location: [https://www.amazon.com/dp/B08N5WRWNW](https://www.amazon.com/dp/B08N5WRWNW)`



### Deep Dive & Trade-offs

#### Q: Why HTTP 302 Temporary Redirect instead of HTTP 301 Permanent Redirect?

* **Interview Explanation:**
  *"This is one of the most critical design decisions in a URL shortener:
* **301 Moved Permanently:** The client's browser caches the target URL locally. On repeat visits, the browser bypasses our server entirely and navigates straight to the destination. While this minimizes server load and network round-trips, **it permanently blinds our analytics engine** to repeat visits.
* **302 Found (Temporary Redirect):** The browser is forced to send every subsequent click back to our gateway before being redirected.
* **Verdict:** We choose **302** because URL shorteners monetize or justify their value through accurate click and conversion analytics. 302 ensures 100% click visibility."*



#### Q: How should missing or expired links be handled?

* **Interview Explanation:**
  *"Return an `HTTP 404 Not Found`. Never return an empty white page. Instead, deliver a user-friendly HTML error page that clarifies whether the link was deleted, expired, or mistyped, and provide a fallback link to the service homepage."*

---

## 4. Endpoint: Analytics & Click Tracking

### API Contract

* **Route:** `GET /api/v1/stats/{short_code}`
* **Response Body (`200 OK`):**
```json
{
  "short_code": "abc123",
  "total_clicks": 14250,
  "unique_clicks": 9800,
  "clicks_by_country": {
    "US": 6200,
    "IN": 4100,
    "UK": 1850,
    "Other": 2100
  },
  "clicks_by_device": {
    "mobile": 8550,
    "desktop": 5700
  },
  "referrers": {
    "twitter.com": 7200,
    "linkedin.com": 4300,
    "direct": 2750
  },
  "timeline_breakdown": {
    "granularity": "hourly",
    "data": [
      {"timestamp": "2026-09-14T06:00:00Z", "count": 340},
      {"timestamp": "2026-09-14T07:00:00Z", "count": 890}
    ]
  }
}

```



### Systems Engineering Bottleneck: Asynchronous Ingestion

* **The Problem:**
  On every redirect, we must extract the user's IP (for GeoIP lookup), `User-Agent` (for device parsing), `Referer` header, and exact timestamp. Performing DNS/GeoIP resolution and writing to an analytical datastore synchronously during the redirect would add 50–150ms of latency to the user.
* **The Solution:**
  *"Decouple ingestion from processing. During the `GET /{short_code}` flow, extract the raw headers, publish a lightweight event to a streaming message broker (e.g., Apache Kafka or AWS Kinesis), and immediately return the `302` response. Background worker nodes consume from the queue, execute GeoIP resolutions, parse device fingerprints, and aggregate statistics into a write-heavy time-series database (e.g., ClickHouse, Cassandra)."*

---

## 5. Custom Alias Collision Handling

#### Q: Should we provide a separate `GET /api/v1/check-availability?alias=foo` endpoint?

* **Interview Explanation:**
  "No. A separate check endpoint introduces an inherent race condition known as **Time-of-Check to Time-of-Use (TOCTOU)**. A user might check if `sale2026` is free, get a `true` response, but by the time they submit the creation payload 2 seconds later, someone else has acquired it.
  Instead, handle collision checking atomically in `POST /api/v1/shorten`. If a unique constraint fails in the database, return an **`HTTP 409 Conflict`** with an error message: `{"error": "Alias already in use"}`. This keeps the API atomic and pushes retry handling cleanly to the client."

---

## 6. Defensive API Design: Validation & Security

#### Q: How do we prevent malicious URLs and schema abuse?

1. **Scheme Whitelisting:** Strictly permit only `http://` and `https://`. Explicitly reject schemes like `file://`, `ftp://`, or `javascript:`, which could lead to Local File Inclusion (LFI) or Cross-Site Scripting (XSS).
2. **Domain Syntax & Resolution:** Verify standard domain formatting. An optional DNS lookup can verify if the target host exists, but should have a tight timeout (e.g., 50ms) to avoid latency penalties.
3. **Domain Blacklisting & Security Scanners:** Reject destinations matching known phishing lists (e.g., Google Safe Browsing API), explicit adult domains (if operating under a family-friendly SLA), or recursive loops (preventing users from shortening our own shortening domain).

---

## 7. Rate Limiting Strategy

#### Q: How do you protect the service from namespace exhaustion and denial-of-service?

* **Interview Explanation:**
  *"Without rate limiting, a script could easily burn through our 7-character base62 namespace (which holds $\approx 3.5\text{ trillion}$ combinations) or overwhelm storage with junk links. We enforce tiered rate limits using a Token Bucket or Sliding Window algorithm implemented in Redis:"*

| User Tier | Limit | Identification Mechanism |
| --- | --- | --- |
| **Anonymous** | 10 requests / hour | Client IP Address |
| **Registered (Free)** | 100 requests / hour | User ID via JWT |
| **Enterprise / Paid** | 10,000+ requests / hour | Dedicated API Key |

* **Standardized Response Headers:**
  Every response includes RFC-standard rate-limiting headers:
* `X-RateLimit-Limit: 100`
* `X-RateLimit-Remaining: 23`
* `X-RateLimit-Reset: 1726305600` (Unix epoch timestamp when the window resets)
* When exceeded: Return **`HTTP 429 Too Many Requests`** along with a `Retry-After: 120` header.



---

## 8. Enterprise Extensibility: Batch / Bulk Operations

#### Q: How does this API support corporate customers running large marketing blasts?

* **Interview Explanation:**
  *"If a marketing automation system wants to generate 1,000 customized tracking links for an email campaign, sending 1,000 separate HTTP requests incurs unacceptable TCP/TLS handshake overhead and network serialization costs."*
* **Batch Endpoint:** `POST /api/v1/bulk-shorten`
* Accepts an array of URL objects.
* Returns an array of created short URLs.
* **Safety Guardrail:** Strict payload limit capped at **100 URLs per batch**. This prevents unbounded payload processing from blocking application threads or causing heap exhaustion.



---

## 9. API Governance: Versioning & Auth

* **Path Versioning (`/api/v1/...`):**
* *Why over Header versioning:* URLs are explicit, easily debuggable in server access logs, and straightforward to test with tools like `curl` and Postman without configuring custom headers.


* **Authentication Split:**
* **Browser UI Sessions:** JSON Web Tokens (JWT) stored in HTTP-only, secure cookies or passed via `Authorization: Bearer <token>`.
* **Programmatic / Server-to-Server:** API Keys passed via dedicated custom header: `X-API-Key: sec_live_xxx`.



---

## Quick Summary Matrix for Interview Prep

```
┌─────────────────────────┬──────────────┬───────────────┬──────────────────────────────────────────┐
│ Endpoint                │ HTTP Method  │ Success Code  │ Key Engineering Consideration            │
├─────────────────────────┼──────────────┼───────────────┼──────────────────────────────────────────┤
│ /api/v1/shorten         │ POST         │ 201 Created   │ Handles 409 Conflict; creates unique IDs │
│ /{short_code}           │ GET          │ 302 Found     │ 302 enables complete click tracking      │
│ /api/v1/stats/{code}    │ GET          │ 200 OK        │ Async Kafka event processing             │
│ /api/v1/bulk-shorten    │ POST         │ 200 OK        │ Capped at 100 items/call to prevent DoS  │
└─────────────────────────┴──────────────┴───────────────┴──────────────────────────────────────────┘

```