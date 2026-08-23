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