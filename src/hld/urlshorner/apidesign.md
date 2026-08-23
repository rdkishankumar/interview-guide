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