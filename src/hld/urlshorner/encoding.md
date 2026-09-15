Here are the detailed notes structured from the provided transcript, capturing all technical concepts, trade-offs, and implementation strategies without altering the original meaning.

---

## 1. Hashing Approach

### Concept

* Take a long URL and pass it through a hash function such as **MD5** (128 bits) or **SHA-256**.
* Truncate the resulting hash output to take the first **7 characters** to obtain a fixed-length short code.

### Trade-offs & Limitations

* **Hash Collisions:** Two different long URLs can produce the same initial 7 characters.
* **Math & Birthday Paradox:**
* 62 possible characters at 7 positions yields $62^7 \approx 3.5\text{ trillion combinations}$.
* Due to the **Birthday Paradox**, a 50% probability of collision occurs after generating just **2 million URLs**, making pure hashing unsuitable for systems handling billions of URLs.



### Collision Resolution Options

1. **Append a counter:** e.g., `abc123` becomes `abc123-1`, then `abc123-2`.
2. **Use more characters from the hash.**

* *Drawback:* Both solutions increase URL length, defeating the purpose of a URL shortener.

---

## 2. Counter-Based Approach (Preferred Strategy)

### Core Mechanism

* Maintain a global counter starting at 1 that increments with each new URL request.
* Convert the decimal integer (e.g., `125,938`) into **Base62** encoding (`0-9`, `a-z`, `A-Z`).
* Example: `125,938` converts to `W7C` in Base62.


* **Primary Advantage:** Uniqueness is guaranteed; zero risk of collisions.

### Scaling & Distributed Bottlenecks

A single global counter creates a throughput bottleneck when multiple servers try to request numbers simultaneously.

#### Solutions for Distributed Counters:

1. **Range-Based Allocation:**
* Server 1 is allocated range `1` to `1,000,000`.
* Server 2 is allocated range `1,000,001` to `2,000,000`, and so on.
* Servers generate codes independently within their range and request new batches from a central coordinator (e.g., **Apache ZooKeeper** or an atomic update database table) when exhausted.


2. **Distributed ID Generation (e.g., Twitter Snowflake):**
* Generates **64-bit IDs** structured as:
* **41 bits:** Timestamp in milliseconds (guarantees time ordering).
* **10 bits:** Machine ID (unique per server).
* **12 bits:** Sequence number (handles concurrent requests within the same millisecond).


* The resulting 64-bit number is then converted to Base62.



### Security, Obfuscation, & User Experience Optimizations

* **Starting Offset:** Start the counter at a large random offset (e.g., `1,000,000,000`) instead of `1` to avoid single-character codes (like `A` or `AB`) and ensure standard 6-to-7 character outputs immediately.
* **Preventing Enumeration:** Sequential IDs allow users to guess adjacent URLs (e.g., guessing `w7b` and `w7d` around `w7c`).
* **Solution:** Apply a bitwise **XOR** with a secret key to the counter value before Base62 encoding to make output strings appear random.


* **Base58 Alternative:**
* Base62 contains visually ambiguous characters (e.g., `0` vs `O`, `1` vs `l`).
* Using **Base58** (similar to Bitcoin address encoding) removes these visually confusing characters to improve user readability, despite a slight reduction in total available combinations.



---

## 3. Pre-generation Pool Strategy

### Concept

* A dedicated background service continuously generates unique short codes and stores them in a memory pool (e.g., **Redis**) containing around a million codes.
* When a URL creation request arrives, a short code is popped directly from the pool.

### Advantages & Disadvantages

* **Advantage:** Low latency / fast response times since generation logic is bypassed during the user request cycle.
* **Disadvantage:** Adds architectural complexity to manage, refill, and maintain the pool.
* **Uniqueness Assurance:** The background service uses a counter system or checks generated random codes against a uniqueness store before pushing to the pool.

---

## 4. Custom Short URL Handling

### Process Workflow

1. **Validation:** Check the requested custom alias for valid characters, reasonable length limits, and exclude reserved words (e.g., `API`, `Admin`).
2. **Database Lookup:** Check whether the requested alias is available in the database.
* If taken: Reject the request.
* If available: Save the custom URL directly without passing it through the encoding engine.



### Sequence Conflict Management

* If a custom URL matches a sequence that the generator might produce later (e.g., `abc123`), the system must flag it as used.
* **Verification:** Use a **Bloom Filter** for high-efficiency existence checks or perform a database check before assigning generated codes.

---

## 5. Security & Namespace Protection

* Implement **rate limiting** at both the API level and system generation level.
* Track unusual generation activity (e.g., a single source creating thousands of URLs per minute) to prevent bad actors from rapidly exhausting the 3.5 trillion short code namespace.

---

## Summary Matrix

| Approach | Advantages | Disadvantages / Challenges |
| --- | --- | --- |
| **Pure Hashing (MD5 / SHA-256)** | Simple initial setup. | High collision rate (Birthday Paradox); resolving collisions lengthens URLs. |
| **Counter + Base62 / Base58** | Collision-free; guaranteed uniqueness. | Requires distribution strategies (Ranges / Snowflake) to prevent server bottlenecks. |
| **Pre-generation Pool** | Near-instant URL creation latency. | Requires extra background services and pool management complexity. |
| **Custom URLs** | Highly personalized for end users. | Requires separate validation logic, DB checks, and collision prevention against sequence generators. |

-------
# Encoding - next NOTES

# System Design Interview: Generating Unique Short Codes (URL Shortener)

---

## 1. Capacity & Math Estimation

**Interviewer Prompt:** *"Before picking a generation algorithm, what are the mathematical constraints for a 7-character short URL?"*

**Candidate Answer:**

> "A standard short code uses alphanumeric characters: digits (`0–9`), lowercase (`a–z`), and uppercase (`A–Z`), giving a base of $10 + 26 + 26 = 62$ symbols (**Base62**).
> With a length of 7 characters, the total available namespace is:
>
> $$62^7 \approx 3.52 \times 10^{12} \quad (\approx 3.52\text{ trillion combinations})$$
>
>
> 3.5 trillion is massive—even at an aggressive ingestion rate of 1,000 URLs per second, the namespace lasts over a century. However, the exact generation algorithm determines whether we actually get to use this namespace or run into premature collisions."

---

## 2. Approach 1: Cryptographic Hashing (MD5 / SHA-256 + Base62)

```
[ Long URL ] ──► [ MD5 / SHA-256 ] ──► 128-bit hash ──► [ Base62 ] ──► Truncate first 7 chars
                                                                                │
                                                                                ▼
                                                                  Collision Check (DB Lookup)
                                                                  ├── No Match ──► Insert
                                                                  └── Match ─────► Append Salt / Retry

```

### How It Works

* Compute a cryptographic hash of the incoming long URL (MD5 yields 128 bits, SHA-256 yields 256 bits).
* Convert the raw binary hash to Base62 and truncate to the first 7 characters.

### Deep Dive: The Birthday Paradox Failure

**Interviewer Prompt:** *"If $62^7 \approx 3.5\text{ trillion}$, why can't we just use the first 7 characters of an MD5 hash?"*

**Candidate Answer:**

> "Because of the **Birthday Paradox**. Even though the full collision space is $3.5$ trillion, the probability of a collision reaches $50\%$ at approximately $\sqrt{N}$ items:
>
> $$\sqrt{3.5 \times 10^{12}} \approx 1.87 \times 10^6 \quad (\approx 2\text{ million records})$$
>
>
> In a system intended to store billions of links, hitting collisions after just 2 million records is unacceptable."

### Collision Resolution Bottlenecks

When a truncated hash collides with an existing database entry:

1. **Append a counter or salt:** `abc123` becomes `abc123-1`, `abc123-2`, etc.
* *Trade-off:* Defeats the primary purpose of the system—the URLs become longer.


2. **Increase character length:** Use 8 or 9 characters.
* *Trade-off:* Still forces the system into a **Check-Then-Insert** loop. Every link creation incurs an extra read against the database to confirm uniqueness, adding high database IOPS and read amplification on the write path.


3. *Verdict:* Pure hashing is unsuitable for high-scale URL shorteners.

---

## 3. Approach 2: Distributed Counter + Base62 (Recommended)

```
                       [ Central Coordinator (ZooKeeper / DB Table) ]
                                      │  Allocates ranges (e.g., 1M blocks)
                 ┌────────────────────┼────────────────────┐
                 ▼                    ▼                    ▼
          [ Worker Node 1 ]    [ Worker Node 2 ]    [ Worker Node 3 ]
          Range: 1M - 2M       Range: 2M - 3M       Range: 3M - 4M
          Local Atomic Counter Local Atomic Counter Local Atomic Counter
                 │                    │                    │
                 ▼                    ▼                    ▼
          Counter = 1,000,001  Counter = 2,000,001  Counter = 3,000,001
                 │                    │                    │
                 └──────────────┬─────┴────────────────────┘
                                ▼
                      [ Base62 Encoding ]
                                ▼
                       Unique 7-char Code (Zero Collisions)

```

### How It Works

* Maintain a monotonically increasing integer counter.
* Convert the integer into a Base62 string using division and modulo arithmetic ($0\text{--}9$, $a\text{--}z$, $A\text{--}Z$).
* *Example:* Decimal `125,938` becomes `wLg` in Base62.


* **Guaranteed Zero Collisions:** Since every number is globally unique, its Base62 representation is mathematically guaranteed to be unique. No duplicate checks or collision resolution loops are needed.

### Bottleneck: Single Global Counter

**Interviewer Prompt:** *"A single counter in MySQL (`AUTO_INCREMENT`) or Redis (`INCR`) guarantees uniqueness, but how does it hold up under 50,000 writes per second?"*

**Candidate Answer:**

> "A centralized counter becomes a severe **write bottleneck and single point of failure (SPOF)**. All app servers must coordinate over the network to lock and increment the same memory address or database row. We resolve this by decoupling the coordinator from the generation nodes using **Range-Based Allocation**."

### Solution: Range-Based Allocation

* A central coordinator (e.g., Apache ZooKeeper, etcd, or a lightweight transactional SQL table) maintains the master counter.
* Instead of handing out numbers one by one, it allocates large **ranges/blocks** (e.g., 1,000,000 numbers per block) to each application server:
* **Server 1:** Gets range `[1,000,000 – 1,999,999]`
* **Server 2:** Gets range `[2,000,000 – 2,999,999]`


* Each worker server increments its local counter in memory using an atomic CPU instruction (`AtomicLong.incrementAndGet()`), achieving millions of ops/sec with zero network latency.
* When a worker server nears the end of its allocated range, it makes a single background network call to the coordinator to claim the next available range block.
* *Fault-Tolerance:* If Server 1 crashes, the remaining numbers in its range are lost. This creates small, harmless gaps in the numeric sequence, but the 3.5-trillion namespace is large enough that minor range loss has no operational impact.

### Alternative: Distributed 64-Bit IDs (Twitter Snowflake)

* **Structure:** 41 bits (epoch timestamp) + 10 bits (machine/worker ID) + 12 bits (sequence number).
* **Trade-off:** Encoding a full 64-bit unsigned integer in Base62 can produce strings up to **11 characters long** ($\log_{62}(2^{64}-1) \approx 10.7$). If the requirement strictly limits codes to 7 characters, 64-bit Snowflake IDs exceed the length constraint. Range-allocated 43-bit integer counters must be used instead to stay within 7 characters ($62^7 \approx 2^{41.6}$).

---

## 4. Production Hardening: Security & UX Optimizations

### Mitigating Enumeration Attacks (Security)

**Interviewer Prompt:** *"If counters are sequential, an attacker can simply increment the Base62 code (`wLf` $\rightarrow$ `wLg` $\rightarrow$ `wLh`) and scrape private documents or unlisted campaigns. How do you prevent this?"*

**Candidate Answer:**

> "We prevent enumeration using two techniques:
> 1. **Non-Zero Base Offset:** Start the counter at a high base value (e.g., $1,000,000,000$) so the system never produces awkwardly short 1- or 2-character codes like `[http://short.url/b](http://short.url/b)`.
> 2. **Reversible Obfuscation (Bit-Shuffling / Feistel Cipher / XOR Mask):** Before converting the sequential counter to Base62, pass the integer through a deterministic, reversible bit-mask or variable-length permutation:
>
> $$\text{Obfuscated ID} = \text{Counter} \oplus \text{SecretKey}$$
>
>
>
> This produces pseudorandom, non-contiguous outputs that completely scramble sequential progression while maintaining a strict 1-to-1 bijection (zero collisions)."
>
>

### Character Ambiguity: Base62 vs. Base58 (User Experience)

* Certain alphanumeric characters appear identical in standard fonts, causing human transcription errors:
* Number `0` vs. Capital letter `O`
* Number `1` vs. Lowercase `l` vs. Capital `I`


* **Production Decision:** Many production systems adopt **Base58** (the same alphabet used in Bitcoin addresses), which explicitly strips out `0`, `O`, `I`, and `l`.
* *Trade-off:* With 58 characters, $58^7 \approx 2.2\text{ trillion}$ combinations. We sacrifice a fraction of the address space in exchange for clear, error-free legibility on printed media, SMS, and billboards.



---

## 5. Approach 3: Asynchronous Token Pre-Generation (Key Generation Service)

```
┌────────────────────────────────────────────────────────┐
│               Key Generation Service (KGS)             │
│  - Runs background sequential counter                  │
│  - Generates Base62 tokens                             │
│  - Verifies against Custom Alias reservations          │
└──────────────────────────┬─────────────────────────────┘
                           │ Batch writes unused tokens
                           ▼
┌────────────────────────────────────────────────────────┐
│                 Redis / Token Pool                     │
│  [ "x9A1b2c", "k7L9m0p", "q2W4e6r", ... 1M tokens ]    │
└──────────────────────────┬─────────────────────────────┘
                           │ O(1) Pop (LPOP / SPOP)
                           ▼
                 [ Shorten API Gateway ]

```

### How It Works

* An isolated offline service (Key Generation Service, or KGS) continuously mints unique short codes in advance and loads them into a fast memory structure (e.g., Redis `Set` or queue).
* When a user calls `POST /api/v1/shorten`, the API worker pops an already-minted token (`LPOP`).
* **Advantages:** URL creation latency is instantaneous ($O(1)$ memory pop); write requests do not block on distributed counters or database sequences.
* **Disadvantages:** Adds operational overhead of monitoring pool depth, managing failover of the token datastore, and recovering unallocated tokens if a consumer crashes after popping.

---

## 6. Custom Aliases & Namespace Collisions

**Interviewer Prompt:** *"How do we handle custom alias requests like `/summer-sale` or `abc123` without colliding with the automated generation pipeline?"*

**Candidate Answer:**

> "Custom aliases require a dedicated validation and isolation path:
> 1. **Validation & Filtering:**
> * Sanitize regex: restrict to `[a-zA-Z0-9-_]`, length 3–30 characters.
> * Block system keywords via reserved-word lists: `/api`, `/admin`, `/login`, `/metrics`, `/health`.
>
>
> 2. **Atomic Availability Check:**
> * Attempt an atomic insert into the database (`INSERT INTO urls ... ON CONFLICT DO NOTHING`). If the row exists, return an immediate `409 Conflict`.
>
>
> 3. **Cross-Contamination Protection:**
> * What if an enterprise user manually claims a 7-character string like `abc123` that belongs to the counter's future sequence?
> * **Option A (Separate Namespaces):** Route custom aliases under a dedicated prefix (e.g., `/c/summer-sale` vs. `/abc123`).
> * **Option B (Shared Namespace + Lookahead Check):** If a single root domain is required, the Key Generation Service checks incoming custom aliases against a **Bloom Filter** of the active counter range. If a user manually claims a code in the generation path, mark that code as `USED` in the allocation table so the sequence engine skips it when reached."
>
>
>
>

---

## 7. Rate Limiting & Namespace Depletion Safeguards

* Even with 3.5 trillion possible combinations, unthrottled automated scripts could exhaust namespace blocks or inflate database storage.
* **Multi-Layer Throttling:**
* **Network Edge (Cloudflare / WAF):** Rate limit link generation endpoints by IP address to catch unsophisticated bot scripts.
* **Identity Level (Token Bucket / Sliding Window):** Enforce strict caps per user account (e.g., max 100 links/hour for free users; custom SLA for enterprise accounts).
* **Anomaly Detection:** Trigger automated alerts on bursts of thousands of generations per minute to protect the namespace from rapid depletion.



---

## Comparative Architecture Matrix

| Strategy | Collision Risk | Latency / Complexity | Length Consistency | Primary Weakness |
| --- | --- | --- | --- | --- |
| **MD5 / SHA-256 (Truncated)** | High (Birthday paradox at ~2M entries) | High (Requires read-before-write DB check) | Variable if collision salts are appended | Frequent collisions and slow write throughput |
| **Range-Allocated Counter (Recommended)** | **Zero (Mathematically impossible)** | **Sub-millisecond (In-memory atomic increments)** | **Uniform (Padded/offset to 7 chars)** | Server crashes drop unused range numbers |
| **Twitter Snowflake (64-bit)** | Zero | Sub-millisecond (Worker-local clock) | Too Long ($\approx 11$ characters in Base62) | Violates strict 7-character short URL constraint |
| **Token Pre-Generation (KGS)** | Zero | Instantaneous ($O(1)$ memory pop) | Uniform | Operational overhead of maintaining token pools |