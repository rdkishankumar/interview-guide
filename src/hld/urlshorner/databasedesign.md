# Solution Architecture Notes: URL Shortener Database & Caching Layer

This document synthesizes the database architecture, schema definitions, indexing strategies, sharding models, and caching mechanics into a production-grade system specification.

---

## 1. Storage Paradigm: Hybrid Database Architecture

To balance extreme write/read throughput with complex query capabilities, a **Polyglot Persistence Model** is implemented:

```text
┌──────────────────────────────────────────────────────────────────────────────────┐
│                               Polyglot Persistence                               │
└─────────────────────────┬───────────────────────────────┬────────────────────────┘
                          │                               │
                          ▼                               ▼
┌──────────────────────────────────────────────────┐  ┌──────────────────────────────────────────────────┐
│    NoSQL Database (DynamoDB / Cassandra)         │  │     Relational Database (PostgreSQL / MySQL)     │
├──────────────────────────────────────────────────┤  ├──────────────────────────────────────────────────┤
│ • Primary URL Mapping Store                      │  │ • User Accounts & Authentication                 │
│ • Key-Value Lookup (ShortCode -> LongURL)        │  │ • Structured Subscription & Tier Billing         │
│ • Horizontal Auto-Partitioning                   │  │ • Complex Aggregated Analytics & Reporting       │
│ • Low Latency at Scale                           │  │ • ACID Compliance for Financial/Account Data     │
└──────────────────────────────────────────────────┘  └──────────────────────────────────────────────────┘

```

---

## 2. Schema Definitions

### A. Primary URL Table (`URL_Mapping`) — *NoSQL Store*

Optimized strictly for read/write lookups by `short_code`. Click counters are kept **decoupled** from this table to prevent lock contention and hot partitions on viral URLs.

#### Attributes & Data Types

| Attribute | Type | Primary/Secondary Key | Description |
| --- | --- | --- | --- |
| `short_code` | String (UTF-8) | **Partition Key (PK)** | Unique 7-character Base62 string or custom alias. |
| `long_url` | String (UTF-8) | — | Destination target URL. |
| `user_id` | String (UUID) | **GSI Partition Key** | Links short code to the owning user account. |
| `is_custom` | Boolean | — | Flag denoting user-defined alias vs. auto-generated code. |
| `is_active` | Boolean | — | Soft-delete state flag (`true` = active, `false` = revoked/expired). |
| `created_at` | Timestamp (ISO-8601) | **GSI Sort Key** | Creation epoch timestamp. |
| `expires_at` | Timestamp (ISO-8601) | — | Configurable expiration epoch timestamp. |

---

### B. Raw Click Events Table (`Click_Analytics_Raw`) — *Time-Series / NoSQL Store*

Captures individual hit events asynchronously via message queues (e.g., Apache Kafka).

#### Attributes & Data Types

| Attribute | Type | Primary/Secondary Key | Description |
| --- | --- | --- | --- |
| `short_code` | String (UTF-8) | **Partition Key (PK)** | Short link identifier. |
| `clicked_at` | Timestamp (Unix ms) | **Sort Key (SK)** | Time click occurred (enables range queries). |
| `ip_address` | String | — | Raw client IP (for geo-processing). |
| `country` | String (ISO-2) | — | Derived ISO country code (e.g., `US`, `IN`). |
| `city` | String | — | Derived city name. |
| `user_agent` | String | — | Unparsed client HTTP User-Agent. |
| `device_type` | String | — | Derived category (`mobile`, `desktop`, `tablet`, `bot`). |
| `referrer` | String | — | HTTP Referrer header domain. |

---

### C. Daily Aggregated Analytics Table (`Click_Analytics_Daily`) — *SQL / Relational Store*

Raw event data generates millions of rows per viral link. Background rollups compress older entries into daily summaries to optimize historical queries.

```sql
CREATE TABLE click_analytics_daily (
    short_code VARCHAR(32) NOT NULL,
    click_date DATE NOT NULL,
    country_code VARCHAR(2) NOT NULL DEFAULT 'ZZ',
    device_type VARCHAR(16) NOT NULL DEFAULT 'unknown',
    click_count BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (short_code, click_date, country_code, device_type)
);

CREATE INDEX idx_analytics_lookup 
ON click_analytics_daily (short_code, click_date DESC);

```

---

## 3. Data Lifecycle, Archival, and Partition Strategy

```text
                  ┌────────────────────────────────────────┐
                  │          Click Telemetry Flow          │
                  └───────────────────┬────────────────────┘
                                      │
                                      ▼
                      ┌────────────────────────────────┐
                      │  Raw Clicks (NoSQL/Kafka)      │  <-- Retention: 0 to 30 Days
                      └───────────────┬────────────────┘
                                      │
                                      ▼ [Nightly Aggregation Job]
                      ┌────────────────────────────────┐
                      │  Daily Summaries (Relational)  │  <-- Retention: 30 Days to 2 Years
                      └───────────────┬────────────────┘
                                      │
                                      ▼ [Archival Engine]
                      ┌────────────────────────────────┐
                      │  Cold Storage (S3 / Glacier)   │  <-- Long-Term Historical Storage
                      └────────────────────────────────┘

```

### Soft Deletes & Cold Storage Strategy

* **Soft Deletes:** Expired or revoked URLs are retained with `is_active = false`. This guarantees data recovery capability and maintains historical integrity for analytics.
* **Archival Engine:** Items with `is_active = false` or `expires_at > 5 years` are purged from the primary database cluster and archived to **Amazon S3 / S3 Glacier** in Parquet format for long-term analytical queries via Amazon Athena.

---

## 4. Sharding & Hot Partition Mitigation

### NoSQL Partitioning

* **Default Partitioning:** Partitioned by hash of `short_code`.
* **Hot Partition Problem:** A viral short link (e.g., posted by a major public figure) can cause read/write throughput hot-spotting on a single physical partition.

#### Mitigation Options

```text
┌──────────────────────────────────────────────────────────────────────────────────┐
│                             Hot Partition Mitigation                             │
└─────────────────────────┬───────────────────────────────┬────────────────────────┘
                          │                               │
                          ▼                               ▼
┌──────────────────────────────────────────────────┐  ┌──────────────────────────────────────────────────┐
│     Option 1: Distributed Read Caching           │  │    Option 2: Partition Key Salting               │
├──────────────────────────────────────────────────┤  ├──────────────────────────────────────────────────┤
│ • Offload >99% of read volume to Redis/Memcached │  │ • Append random suffix (e.g., shortcode-0..9)    │
│ • Recommended for high Read-to-Write ratios      │  │ • Spreads single shortcode across 10 partitions  │
│ • Simple read path; no scatter-gather queries    │  │ • Increases read query complexity (scatter-gather│
└──────────────────────────────────────────────────┘  └──────────────────────────────────────────────────┘

```

---

## 5. Caching Layer Architecture

Since URL shortener access patterns exhibit a high Read-to-Write ratio (typically $\ge 100:1$), aggressive caching is critical to shield the database layer.

### Cache-Aside (Lazy Loading) Strategy

```text
[ Client Redirect Request ]
           │
           ▼
┌──────────────────────┐   CACHE HIT (90%+ of traffic)
│  Redis Cache Cluster │ ───────────────────────────────────┐
└──────────┬───────────┘                                   │
           │                                               │
           │ CACHE MISS                                    │
           ▼                                               ▼
┌──────────────────────┐  Populate Cache (TTL: 24h)   ┌─────────────────┐
│ Primary NoSQL DB     │ ───────────────────────────> │ Return Long URL │
└──────────────────────┘                              └─────────────────┘

```

### Implementation Details

* **Storage Engine:** Redis Cluster (In-Memory, Master-Replica across Availability Zones).
* **Key-Value Mapping:** `Key = short_code`, `Value = long_url`.
* **Eviction Policy:** **Allkeys-LRU** (Least Recently Used). Ensures popular links remain cached indefinitely, while inactive links expire out.
* **Default TTL:** **24 Hours** (balances memory consumption with stale data invalidation).

---

## 6. Architecture Summary Matrix

| Architectural Subsystem | Choice | Justification |
| --- | --- | --- |
| **Primary Data Store** | NoSQL (DynamoDB / Cassandra) | Horizontal scaling, low latency, key-value lookup performance. |
| **User & Billing Store** | RDBMS (PostgreSQL) | ACID compliance, relational joins for user accounts and pricing tiers. |
| **Analytics Engine** | Hybrid (Kafka + Raw NoSQL + SQL Aggregates) | Non-blocking redirect path with query-optimized aggregated reporting. |
| **Primary Lookup Index** | `short_code` Hash Index | Direct $O(1)$ routing lookup for link redirection. |
| **Cache Pattern** | Cache-Aside via Redis Cluster | Offloads $\ge 90\%$ of read traffic from the database tier. |
| **Deletion Policy** | Soft Delete + S3 Glacier Archival | Protects historical analytics while managing main database size. |

---

# Notes

# URL Shortener Database Design Notes

## Access Patterns

* **Writes:** Write once upon creating a short URL.
* **Reads:** Read many times during redirects.
* **Lookups:** Primary lookup by short code (primary key); occasional queries by User ID for analytics.

---

## SQL vs. NoSQL Comparison

### SQL (e.g., PostgreSQL, MySQL)

* **Pros:**
* ACID guarantees (ensures no duplicate short codes).
* Flexible querying for analytics.
* Easy joins with user tables.
* High developer familiarity.


* **Cons / Trade-offs:**
* Harder to scale horizontally (requires read replicas and sharding).
* Overkill for simple key-value lookups.



### NoSQL (e.g., DynamoDB, Cassandra)

* **Pros:**
* Easy horizontal scaling from day one.
* Optimized for high-volume key-value lookups (handles millions of requests/sec).
* Built-in geographic replication.


* **Cons / Trade-offs:**
* Loss of strict ACID guarantees.
* Complex queries are harder to execute.
* Often requires separate systems for analytics.



### Final Choice: Hybrid Approach

* **NoSQL as Primary DB:** For high-volume URL lookups (DynamoDB/Cassandra) due to key-value access patterns and extreme scale requirements.
* **SQL as Secondary DB:** For user management and complex analytics queries (PostgreSQL).

---

## Schema Design

### Main URL Table

* `short_code` (Primary Key / Partition Key)
* `long_url`
* `created_at`
* `expires_at`
* `user_id` (Optional)
* `is_custom` (Boolean flag to distinguish custom vs. auto-generated codes)
* `is_active` (Boolean flag for soft deletes)

### Click Count Trade-off

* **Storing in Same Table:**
* **Pros:** Fast reads (single database call).
* **Cons:** Causes hot partitions on every click for popular URLs due to frequent row updates.


* **Storing in Separate Analytics Table (Chosen Approach):**
* **Pros:** Keeps main URL table read-optimized; avoids hot partitions.
* **Cons:** Requires separate asynchronous processing; eventual consistency.



### Analytics Table

* `short_code` (Foreign Key)
* `clicked_at` (Timestamp)
* `ip_address` (For geolocation: country, city)
* `user_agent` (For device detection)
* `referrer` (Traffic source)
* `country` & `city`
* **Data Aggregation Strategy:** Keep raw clicks for 30 days for granular metrics, then aggregate older data into daily summary records to prevent handling billions of rows.

---

## Indexing Strategy

* **Primary Index:** On `short_code` (Partition Key in NoSQL / Primary Key in SQL) for redirection lookups.
* **Global Secondary Index (GSI):** On `user_id` in NoSQL to query all URLs belonging to a user.
* **Compound Index:** On (`short_code`, `clicked_at`) in the analytics table for efficient time-range queries.

---

## Expired URLs & Archival Strategy

* **Soft Deletes:** Use an `is_active` flag set to `false` upon expiration instead of hard deleting records, preserving data for reactivation or historical analysis.
* **Data Archival:** Move inactive records older than 5 years to cold storage (e.g., Amazon Glacier) to manage database size.

---

## Sharding Strategy

* **NoSQL Sharding:** Automatic based on the `short_code` partition key.
* **Hot Partition Mitigation:** Append random suffixes (`abc123-0` through `abc123-9`) to spread celebrity/viral link load across 10 partitions.
* **SQL Sharding:** Range-based sharding on short codes (e.g., `A-F` on Shard 1, `G-M` on Shard 2), requiring periodic rebalancing as data grows.

---

## Data Consistency Model

* **URL Creation:** Requires **Strong Consistency** to prevent duplicate short code collisions.
* **Analytics:** Accepts **Eventual Consistency** to prioritize performance over real-time accuracy.

---

## Backup & Disaster Recovery

* **Backups:** Daily automated backups with Point-In-Time Recovery (PITR).
* **Geographic Redundancy:** Multi-region replication (e.g., DynamoDB Global Tables) for automatic cross-region failover.

---

## Caching Strategy

* **Pattern:** Cache-Aside pattern using Redis or Memcached.
* **Data Structure:** Key = `short_code`, Value = `long_url`.
* **Expiration:** TTL set to 24 hours (popular URLs remain cached, unpopular links naturally expire).
* **Impact:** Offloads up to 90%+ of read traffic from the primary database.

---

## URL Deduplication Trade-off

* **Checking Existing Long URLs:**
* **Pros:** Saves storage space.
* **Cons:** Expensive indexing on large text fields (`long_url`); merges analytics across different users.


* **Decision (No Deduplication):** Allow duplicate long URLs to generate unique short codes, preserving independent analytics per request.

---

## Summary

* **Primary DB:** NoSQL for scalable URL lookups.
* **Secondary DB:** SQL for user data and analytics.
* **Architecture:** Decoupled analytics table, soft deletes with cold storage archival, multi-region replication, and Redis cache-aside layer.

---

**Database Design for a URL Shortener**

### Core Decision: SQL vs NoSQL

Access patterns for a URL shortener:
- Write once (on short URL creation)
- Read many times (on redirects)
- Primary lookup by short code
- Secondary queries by user ID (for analytics / user’s list of URLs)

Both SQL and NoSQL can support these patterns, which makes the choice non-obvious.

**SQL (PostgreSQL / MySQL) pros:**
- ACID guarantees → no duplicate short codes
- Flexible querying and joins (especially useful for analytics + user tables)
- Familiar to most developers

**SQL cons:**
- Harder to scale horizontally (requires read replicas + eventual sharding)
- Can be overkill for simple key-value lookups

**NoSQL (DynamoDB / Cassandra) pros:**
- Easy horizontal scaling from day one
- Optimized for key-value lookups
- Handles millions of requests per second
- Built-in geographic replication

**NoSQL cons:**
- Weaker consistency guarantees (no full ACID)
- Complex queries become harder
- May need separate systems for analytics

**Chosen approach:**  
NoSQL (DynamoDB / Cassandra style) as the **primary** store for URL data, because:
- Dominant access pattern is simple key-value lookup
- Need to scale to billions of URLs
- Read-heavy workload fits NoSQL well

Use **SQL (e.g., PostgreSQL)** for user management and analytics data. This hybrid gives the best of both worlds: NoSQL for high-volume lookups + SQL for flexible analytics queries.

### Main URL Table Schema

Fields:
- `short_code` — primary key / partition key
- `long_url` — original URL
- `created_at` — timestamp
- `expires_at` — timestamp
- `user_id` — optional (if logged in)
- `click_count` — counter (see trade-off below)
- `is_custom` — boolean flag (custom vs auto-generated)
- `is_active` — boolean for soft deletes

**Click-count trade-off:**
- Storing count in the same row → fast reads, but every click updates the row → hot partitions on popular URLs.
- Separate analytics table + asynchronous updates → main URL table stays read-optimized; analytics can be eventually consistent.

**Recommendation:** Use a separate analytics table and update counts asynchronously.

### Analytics / Clicks Table

Capture per-click (or aggregated) data:
- `short_code` (foreign-key style reference)
- `clicked_at` timestamp
- IP address (for geolocation)
- User agent (device detection)
- Referrer (traffic source)
- Country / city (derived from IP)

**Scale concern:** A popular URL with 1M clicks produces 1M rows.

**Mitigation:**
- Keep raw click events for the last ~30 days (detailed analytics)
- Periodically aggregate older data into daily summaries (e.g., “1000 clicks from USA on 2025-01-15”)

### Indexing Strategy

- **Primary index / partition key:** `short_code` (for redirect lookups)
- **Secondary index:** `user_id` (to list a user’s shortened URLs)  
  → Global Secondary Index (GSI) in NoSQL
- **Analytics compound index:** `(short_code, clicked_at)` for efficient time-range queries (e.g., last 7 days for a given short code)

### Custom URLs / Aliases

Custom aliases are simply special short codes stored in the same table.  
Enforce uniqueness across both generated and custom codes.  
`is_custom` flag helps with analytics and pricing tiers.

### Handling Expiration & Soft Deletes

- URLs expire after ~2 years.
- Prefer **soft deletes** (`is_active = false`) over hard deletes:
    - Allows reactivation if needed
    - Preserves historical analytics
- Database still grows forever → archive very old inactive data (e.g., inactive > 5 years) to cold storage such as Amazon Glacier. Data remains recoverable but is removed from the hot path.

### Sharding Strategy

**NoSQL (DynamoDB-style):**  
Sharding is automatic on the partition key (`short_code`). Codes distribute naturally.

**Hot-partition risk:** A celebrity-shared short URL can hammer one partition.  
**Mitigation:** Add randomness to the partition key (e.g., `abc123-0` … `abc123-9`). Spreads load across multiple partitions at the cost of slightly more complex reads.

**SQL:** Shard by short-code ranges (A–F → shard 1, G–M → shard 2, etc.). Requires careful rebalancing as data grows.

### Consistency Model

- **URL creation:** Strong consistency required (no two users may receive the same short code). Collision checks during generation are critical.
- **Analytics / click counts:** Eventual consistency is acceptable. A count that is off by a few for a minute is fine. Optimize for performance over strict accuracy.

### Backup, Disaster Recovery & Geographic Redundancy

- Regular (daily) backups of the URL table + point-in-time recovery.
- Multi-region replication so that failure of one region (e.g., US East) can fail over to another (US West).
- NoSQL solutions such as DynamoDB Global Tables make this relatively straightforward.

### Caching Strategy

Even with a fast NoSQL store, cache heavily because of the extreme read/write ratio (~100:1).

- Use Redis or Memcached.
- Key = short code, Value = long URL.
- TTL ≈ 24 hours (popular URLs stay warm; unpopular ones expire naturally).
- **Cache-aside pattern:**
    1. Check cache
    2. On miss → read from DB → populate cache
- Expected result: dramatic reduction in database load (often 90 %+).

### URL Deduplication

Should the system check whether a long URL already exists before creating a new short code?

**Recommendation:** Do **not** deduplicate at the database level.
- Would require an expensive index on the (often long) URL field.
- Storage is cheap.
- Users generally prefer separate short codes (and therefore separate analytics).

### Summary of Database Design Decisions

- **NoSQL** for primary high-volume URL storage (scalability + key-value performance)
- **SQL** for user data and analytics (query flexibility)
- Careful indexing on `short_code` and `user_id`
- Separate tables for URL metadata vs. click analytics
- Soft deletes + archival of very old data
- Heavy caching (Redis/Memcached) with cache-aside
- Strong consistency only where required (creation); eventual consistency for analytics
- Automatic or range-based sharding with hot-partition mitigations

This design prioritizes the dominant access patterns (massive scale, read-heavy key-value lookups) while retaining flexibility for analytics and user management.