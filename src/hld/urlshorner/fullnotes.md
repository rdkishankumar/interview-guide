Section 3 Handout - (Exercises, FAQs and more)
Section 3 Handout Pack
Case Study Assignment 1 — URL Shortener


Section purpose
This section is the first real system design assignment in the course. The goal is to take the fundamentals from the previous sections and apply them to a practical, interview-style problem: designing a URL shortener like Bitly or TinyURL.

This case study is intentionally simple on the surface but deep underneath. It forces students to think about requirements, API design, database design, short code generation, uniqueness, collisions, caching, analytics, scaling, global latency, abuse prevention, rate limiting, and cost optimization.

By the end of this section, students should not just know one possible URL shortener architecture. They should understand how to break down a design problem, justify trade-offs, and evolve a simple design into a production-ready system.





1. Section overview
   This section starts with an assignment-style challenge. Students are asked to design a URL shortener before watching the solution. This is important because system design is not learned by passively watching architecture diagrams. It is learned by struggling through trade-offs, making assumptions, and then comparing those choices with a stronger design.

The problem is to convert long URLs into short, shareable links. For example, a long e-commerce URL can be converted into something like short.url/abc123. But at scale, this simple feature becomes a serious architecture problem.

The system needs to create short links, redirect users quickly, support custom aliases, handle link expiration, and provide analytics. It also needs to satisfy important non-functional requirements such as low-latency redirects, high availability, horizontal scalability, short code compactness, and abuse prevention.

The solution is then developed in stages.

First, the API design defines how users create short URLs, how redirects work, how analytics are exposed, how custom aliases are handled, how errors are returned, how rate limiting protects the system, and how authentication supports premium features.

Next, the database design explores SQL versus NoSQL. The solution chooses NoSQL for the high-volume redirect lookup path and SQL for user management and analytics flexibility. It also discusses indexes, soft deletes, archival, analytics tables, hot partitions, consistency, backups, replication, and caching.

Then, the encoding strategy explains how short codes are generated. It compares hashing, counter-based generation, distributed ID generation, pre-generated code pools, custom alias handling, confusing characters, namespace exhaustion, and collision prevention.

Finally, the scaling and caching lecture puts everything together into a production-grade architecture. It introduces multi-level caching, CDN caching, Redis/application caching, separate read and write infrastructure, hot-key handling, geographic replication, streaming analytics, centralized rate limiting, DDoS protection, monitoring, tracing, graceful degradation, and cost optimization.





2. Detailed concept summary


URL shortener problem
A URL shortener maps a long URL to a short code. When users visit the short URL, the system redirects them to the original long URL.

The basic idea looks easy, but the system becomes interesting because reads dominate writes heavily. A URL might be created once but clicked hundreds or millions of times. That makes redirect latency, caching, availability, and hot-key handling central to the design.



Functional requirements
The system should:

create short URLs from long URLs

redirect short URLs to original URLs

support custom aliases

expire links after a defined time

provide analytics such as clicks, countries, devices, referrers, and time breakdowns

reject invalid or unsafe URLs

support anonymous and authenticated usage

support rate limits and premium quotas



Non-functional requirements
The system should:

redirect in under 100 milliseconds

maintain high availability because broken short links affect campaigns and shared content

scale to billions of stored URLs

support a very read-heavy workload

generate short codes with minimal length

prevent collisions

prevent abuse and malicious URL creation

support global users with low latency



API design
The API design is built around core user actions.

Users need to create short URLs, visit short URLs, and view analytics. The creation endpoint can accept a long URL and optional custom alias. The redirect endpoint uses the short code directly. Analytics endpoints expose click data and traffic breakdowns.

The design uses 302 redirects instead of 301 redirects because analytics matter. A 301 redirect may be cached by browsers, causing repeat visits to bypass the URL shortener. A 302 redirect keeps the shortener in the request path so clicks can be tracked.

Error handling is also important. Invalid URLs return 400. Alias conflicts return 409. Rate limit violations return 429. Missing or expired short codes return 404.



Database design
The database must support extremely fast lookup by short code. This is the hot path for redirects.

NoSQL works well for the main URL mapping because the access pattern is mostly key-value lookup: short code to long URL. It scales horizontally and handles large read traffic well.

SQL can still be used for user management and analytics because those areas may need richer queries, relationships, reports, and flexible aggregations.

The main URL table includes short code, long URL, created timestamp, expiry timestamp, user ID, and status flags. Analytics should be separated from the main URL table because updating click count on every redirect can create hot partitions.



Encoding strategy
The key challenge is generating short codes that are unique, short, and scalable.

Hashing the long URL sounds simple, but taking only part of the hash creates collision risk. Collision resolution can make URLs longer and complicate the design.

A counter-based approach gives guaranteed uniqueness. Each new URL gets a unique number, and that number is encoded into Base62. This creates compact short codes without collisions.

A single global counter can become a bottleneck, so the design can use range allocation, distributed ID generation like Snowflake, or pre-generated code pools.

Custom aliases need separate validation and uniqueness checks. They should reject reserved words and already-used aliases.



Scaling and caching
Because the read-to-write ratio is around 100:1, caching is critical.

The redirect path should be aggressively optimized:
CDN cache → load balancer → redirect server → Redis cache → database.

Most redirect requests should be answered before hitting the database.

Popular URLs can create hot keys, so hot-key spreading or edge caching may be needed. Analytics should be handled asynchronously using queues or streaming systems like Kafka/Kinesis and processed separately from redirects.

The architecture should separate redirect servers from URL creation servers so read and write paths can scale independently.





3. Key concepts at a glance
   A URL shortener is a read-heavy system.

Redirect latency is more important than URL creation latency.

Short code lookup is the core hot path.

302 redirects preserve analytics visibility.

NoSQL fits high-volume key-value URL lookups.

SQL can still be useful for users, billing, and analytics.

Click analytics should be processed asynchronously.

Hashing can create collisions when codes are truncated.

Counter-based Base62 encoding gives predictable uniqueness.

Distributed ID generation avoids global counter bottlenecks.

Custom aliases need conflict handling and reserved-word checks.

Caching is essential because redirects dominate traffic.

CDN and edge caching can reduce origin load dramatically.

Rate limiting prevents namespace exhaustion and abuse.

Popular links can create hot partitions.

Analytics can be eventually consistent.

URL creation needs stronger uniqueness guarantees.





4. Glossary
   URL shortener: A system that maps a long URL to a short URL and redirects visitors from the short URL to the original URL.

Short code: The unique identifier part of a shortened URL, such as abc123.

Custom alias: A user-chosen short code, such as summer-sale instead of a random generated code.

Redirect: An HTTP response that tells the browser to visit another URL.

301 redirect: Permanent redirect. Browsers may cache it aggressively.

302 redirect: Temporary redirect. Usually preferred for URL shorteners when analytics tracking is required.

Read-to-write ratio: The relationship between read requests and write requests. URL shorteners are usually heavily read-dominated.

Base62 encoding: Encoding using 62 characters: 0–9, A–Z, and a–z.

Collision: When two different inputs produce or try to use the same short code.

Birthday paradox: A probability concept showing that collisions appear sooner than expected in a large key space.

Hot key: A key that receives extremely high traffic compared to others.

Hot partition: A database partition overloaded because one or a few keys receive too much traffic.

Cache-aside pattern: The application checks cache first, reads from database on cache miss, then stores the result in cache.

Streaming analytics: Processing events continuously through systems like Kafka, Kinesis, Spark, or Flink.

Soft delete: Marking data as inactive instead of physically deleting it immediately.

Rate limiting: Restricting how many requests a user/IP/API key can make within a time window.

DDoS protection: Protection against large-scale malicious traffic floods.





5. What students must understand
   A URL shortener is simple in functionality but deep in system design trade-offs.

The redirect path is the most critical path because it handles most traffic.

URL creation and URL redirection have different performance and consistency needs.

Analytics should not slow down redirects.

Short code generation must guarantee uniqueness at scale.

Hashing is not automatically safe when short codes are truncated.

Counter-based generation is reliable, but the counter itself must be scaled.

Caching is not optional in a read-heavy URL shortener.

Custom aliases create conflict and abuse problems.

Global scale introduces replication, latency, and consistency trade-offs.





6. What students usually misunderstand
   Students often think URL shorteners are just CRUD apps. They are not. The redirect path is an extremely high-throughput read path.

Students often think hashing the long URL is enough. It is not enough unless collision handling is carefully designed.

Students often think 301 redirect is better because it is faster. It may be faster for browsers, but it destroys analytics visibility for repeat visits.

Students often store click count directly in the URL row. That can create hot partitions for popular URLs.

Students often forget custom alias conflicts.

Students often forget malicious URL abuse.

Students often process analytics synchronously during redirect, which increases latency.

Students often forget expiry, archival, and long-term storage cost.

Students often assume cache solves everything, but viral links can still create hot keys and CDN/origin pressure.

Students often forget that global writes can create conflicts, especially for custom aliases.





7. Concept map
   URL Shortener
   → functional requirements
   → shorten URL
   → redirect
   → custom aliases
   → expiry
   → analytics



API Design
→ POST shorten
→ GET redirect
→ GET stats
→ error handling
→ rate limiting
→ authentication



Database Design
→ URL mapping table
→ analytics events table
→ user data
→ indexes
→ NoSQL for lookups
→ SQL for analytics/user management



Encoding Strategy
→ hashing
→ collisions
→ counter-based IDs
→ Base62
→ distributed counters
→ pre-generated pool
→ custom alias validation



Scaling
→ CDN cache
→ Redis cache
→ read/write separation
→ hot-key handling
→ streaming analytics
→ geo distribution
→ monitoring
→ cost optimization





8. Architect mental model for this case study
   Do not start by drawing servers.



Start with the core product behavior:

users create short URLs

visitors click short URLs

the system redirects quickly

owners view analytics



Then identify the dominant workload:

writes are URL creation

reads are redirects

redirects dominate by a huge margin



Then isolate the hot path:

short code lookup

redirect response

minimal latency

avoid heavy synchronous work



Then separate concerns:

URL mapping is operational data

analytics is event data

user accounts and billing are relational/business data

abuse prevention is policy/control logic



Then scale each part separately:

redirect path optimized for speed

creation path optimized for uniqueness

analytics path optimized for asynchronous processing

admin/user path optimized for query flexibility



This is the architect-level approach: separate workloads, optimize the hot path, and choose trade-offs deliberately.








9. Functional and non-functional requirements sheet
   Functional requirements
   The system should allow users to:

submit a long URL and receive a short URL

optionally request a custom alias

visit a short URL and get redirected

view analytics for their URLs

have links expire after a defined time

use anonymous or authenticated access depending on feature level



Non-functional requirements
The system should provide:

redirect latency under 100 ms

at least 99.9% availability

horizontal scalability

support for billions of stored URLs

short code length of 7 characters or less where possible

read-heavy optimization

abuse prevention

global low-latency access

asynchronous analytics processing





10. API design sheet
    Create short URL
    Endpoint:
    POST /api/v1/shorten

Request:

long_url

optional custom_alias

optional expiry settings



Response:

short_url

long_url

created_at

expires_at



Key design decision:
Generate a new short URL for each request, even if the long URL was previously shortened, because separate analytics per user/request are valuable.



Redirect
Endpoint:
GET /{shortCode}



Response:

302 redirect to original long URL

404 if not found or expired

Key design decision:
Use 302 instead of 301 to preserve analytics visibility.





Analytics
Endpoint:
GET /api/v1/stats/{shortCode}



Response may include:

total clicks

unique clicks

clicks by country

clicks by device

clicks by time

referrers



Key design decision:
Capture analytics during redirect but process asynchronously.








11. API error handling sheet
    Use clear status codes:



400 Bad Request:
Invalid or malformed URL.



404 Not Found:
Short code does not exist, expired, or removed.



409 Conflict:
Custom alias already exists.



429 Too Many Requests:
Rate limit exceeded.



403 Forbidden:
Blocked or unsafe URL.



500 Internal Server Error:
Unexpected server-side failure.



Important principle:
Errors should be helpful enough for developers and friendly enough for end users.





12. Redirect decision sheet: 301 vs 302
    301 permanent redirect
    Advantages:

browser may cache redirect

fewer future requests to shortener

better raw performance for repeated visits

Disadvantages:

repeat clicks may bypass shortener

analytics becomes incomplete

harder to change destination later



302 temporary redirect
Advantages:

every click can pass through shortener

analytics remains accurate

destination can be changed or expired more easily

Disadvantages:

more requests hit the shortener

higher infrastructure load



Decision:
Use 302 when analytics and control matter. For most URL shorteners, 302 is the better default.





13. Rate limiting and abuse prevention sheet
    Why rate limiting matters
    Without rate limiting, attackers or bots can:

create millions of URLs

exhaust namespace

increase infrastructure cost

shorten malicious links

spam the platform



Suggested rate limits
Anonymous users:
10 shortenings per hour by IP.

Registered users:
100 shortenings per hour.

Premium users:
10,000 shortenings per hour.



Additional abuse controls
validate URL format

allow only HTTP/HTTPS

block known phishing domains

check suspicious creation patterns

use CAPTCHA or bot checks if needed

apply quotas per user/API key

use DDoS protection at the edge

reject reserved aliases like api, admin, login, support





14. Database design overview
    The system has two very different data workloads:

Operational redirect lookup:

extremely high volume

simple key-value access

short code to long URL

must be very fast

Analytics and user management:

richer queries

reports and aggregations

filtering by time, country, device, referrer

user-specific dashboards

A strong design can use NoSQL for the main URL mapping and SQL or analytical stores for user and analytics data.








15. SQL vs NoSQL decision sheet


SQL for URL shortener
Advantages:

ACID guarantees

familiar querying

joins with users

easier analytics queries

strong uniqueness constraints

Disadvantages:

harder horizontal scaling

sharding complexity

may be overkill for simple short code lookup



NoSQL for URL shortener
Advantages:

excellent key-value lookup

horizontal scaling

high throughput

built-in replication options

good fit for billions of URL mappings

Disadvantages:

complex analytics queries are harder

uniqueness and consistency need careful design

may require separate analytics systems

Decision:
Use NoSQL for the high-volume URL mapping path. Use SQL or analytical storage for users and analytics where richer querying matters.





16. Main URL table design
    Main URL mapping fields:

short_code

long_url

created_at

expires_at

user_id

is_custom

is_active

metadata if needed



Primary access pattern:
Find long_url by short_code.

Primary key:
short_code.



Important design note:
Avoid updating this row on every click if the URL becomes popular. That can create hot partitions and write pressure.





17. Analytics table design
    Raw click event fields:

short_code

clicked_at

IP address

user agent

referrer

country

city

device type

Indexes:

short_code + clicked_at for time range queries

user_id if user dashboards require it

Retention strategy:

keep raw clicks for recent period, for example 30 days

aggregate older clicks into daily summaries

move historical analytics to data warehouse

Important principle:
Analytics should be eventually consistent and should not slow down redirects.





18. Indexing strategy sheet
    Main URL table:

primary index on short_code

secondary index on user_id if users need to list their URLs

optional index on expires_at for cleanup/archival jobs

Analytics table:

compound index on short_code + clicked_at

optional indexes on user_id, country, device, or referrer depending on dashboard needs

Custom alias lookup:

custom aliases are stored as short codes

uniqueness enforced on short_code

Important warning:
Do not index long_url heavily just to deduplicate unless deduplication is truly required. Long text indexing can become expensive.





19. Expiry, soft delete, and archival sheet
    Expiry
    Links may expire after a default time, such as two years.



Soft delete
Instead of deleting immediately, mark links inactive using is_active = false.

Advantages:

safer recovery

preserves historical analytics

allows reactivation if needed

Disadvantage:

database keeps growing



Archival
Very old inactive URLs can be moved to cold storage.

Example strategy:

active URLs in main database

inactive old URLs in cheaper storage

very old analytics in data warehouse or archive

Architect insight:
Deletion is not just a storage decision. It affects support, analytics, compliance, and user trust.





20. Encoding strategy comparison
    Hashing long URL
    Simple idea:
    Hash the long URL and use part of the hash as short code.

Problem:
Truncating the hash increases collision risk.



Counter + Base62
Simple idea:
Use a unique number and encode it using Base62.

Advantage:
Collision-free if every number is unique.

Problem:
A single global counter can become a bottleneck.



Distributed ID generation
Simple idea:
Use timestamp, machine ID, and sequence number to generate unique IDs.

Advantage:
Scales across machines and avoids central bottleneck.



Pre-generated code pool
Simple idea:
Generate unique codes in advance and store them in a pool.

Advantage:
Fast URL creation.

Problem:
More operational complexity.








21. Hashing strategy deep notes
    Hashing feels attractive because it converts any long URL into a fixed-size output.

But a URL shortener does not use the full hash. It uses a small short code, such as 7 characters. Once the hash is truncated, collisions become possible.



Collision resolution options include:

use more hash characters

append extra suffix

retry with salt

check database before accepting code



But all of these add complexity or increase short code length.

Architect insight:
Hashing is not wrong, but pure truncated hashing is not the cleanest solution for a massive URL shortener.





22. Counter-based Base62 strategy deep notes
    Counter-based generation is reliable because each counter value is unique.

Steps:

Get the next unique number.

Convert it to Base62.

Use the result as short code.

Store mapping in database.



Benefits:

no collision if counter uniqueness is guaranteed

compact output

simple lookup

easy to reason about



Challenges:

global counter can bottleneck

sequential codes may be guessable

custom aliases can conflict with generated namespace



Solutions:

range allocation per server

distributed ID generation

XOR or permutation with secret key to make codes look random

skip reserved/custom-used codes





23. Base62 capacity sheet
    Base62 uses:

0–9

A–Z

a–z

That gives 62 possible characters per position.

With 7 characters:
62^7 ≈ 3.5 trillion combinations.

This is enough for extremely large systems, but it does not mean abuse can be ignored. Bots can still waste namespace and create cost.

Practical note:
Some systems use Base58 instead of Base62 to avoid confusing characters like 0/O and 1/l.








24. Distributed ID generation sheet
    A distributed ID generator avoids depending on one global counter.

Snowflake-style IDs typically combine:

timestamp

machine ID

sequence number

Why it works:

timestamp moves forward

machine ID avoids cross-server conflict

sequence number handles multiple IDs in the same millisecond



Then the numeric ID is encoded into Base62.

Benefits:

scalable

unique

roughly time ordered

no central counter bottleneck

Trade-off:

more complex than a single database counter

requires careful machine ID management

clock issues must be considered





25. Custom alias handling sheet
    Custom aliases are user-chosen short codes.

Validation rules:

allowed characters only

length limits

no reserved words

no offensive or unsafe terms if policy requires it

no collision with existing generated or custom codes

Conflict handling:
If the alias is already taken, return 409 Conflict.

Important edge case:
A user might choose a code that the generator could produce later. The generation system must check used codes or maintain a used-code filter so it never assigns an already-taken custom alias.





26. Scaling architecture sheet
    High-level architecture:

clients

CDN/edge layer

load balancers

redirect servers

shortening servers

Redis/Memcached cache

NoSQL URL mapping database

SQL/user database

analytics queue/stream

stream processors

data warehouse

monitoring and tracing



Read path:
CDN → load balancer → redirect server → Redis → NoSQL database → 302 redirect

Write path:
load balancer → shortening server → ID generator → database write → response

Analytics path:
redirect event → queue/stream → processing workers → aggregated store/data warehouse








27. Multi-level caching sheet


Browser caching
Limited because 302 redirects are preferred for analytics.



CDN caching
Useful for popular URLs and global low latency.
Can serve redirects closer to users.
Can reduce origin traffic dramatically.



Application cache
Redis or Memcached stores short_code → long_url mapping.
Uses cache-aside pattern.



Database
Source of truth for URL mapping.
Only hit when CDN/cache misses.

Architect insight:
The best redirect request is the one that never reaches the database.





28. Cache-aside flow sheet
    Redirect request flow:

User visits short URL.

CDN checks if redirect response is cached.

If CDN miss, request reaches redirect server.

Redirect server checks Redis using short code.

If Redis hit, return 302.

If Redis miss, read from database.

Store mapping in Redis.

Return 302 redirect.

Emit analytics event asynchronously.

Benefits:

simple

database load reduced

popular URLs stay hot in cache

unpopular URLs expire naturally

Risk:
Cache stampede can happen if a viral URL expires from cache. Use TTL jitter, request coalescing, or hot-key protection.








29. Hot key and hot partition handling sheet
    A viral short URL can receive millions of clicks very quickly.

Problems:

one cache key gets hammered

one database partition gets overloaded

analytics event volume spikes

origin servers can be overwhelmed



Solutions:

CDN/edge caching

replicate hot keys

shard hot keys using suffixes like abc123-0 to abc123-9

use local in-memory cache for ultra-hot mappings

process analytics asynchronously

temporarily degrade analytics if needed

use rate limiting and DDoS protection



Architect insight:
Average traffic is not the danger. Viral hot spots are the danger.





30. Analytics pipeline sheet
    Do not process heavy analytics during redirect.

Recommended flow:

Redirect server captures minimal click event.

Event is written to Kafka/Kinesis or similar stream.

Stream processors enrich the event with country, device, and referrer details.

Aggregated results are written to analytics database or warehouse.

Analytics API reads from aggregated stores.

Benefits:

redirect stays fast

analytics can scale independently

event processing can be retried

rich reporting does not overload operational database








31. Geographic distribution sheet
    For global users, redirect latency depends heavily on distance.

Strategies:

CDN/edge caching for popular links

regional application servers

regional cache layers

replicated URL mapping database

global traffic routing to nearest region



Challenge:
Writes create consistency issues, especially custom aliases.

Options:

primary write region

regional reads with replication

consensus for globally unique custom aliases

accept rare conflicts and resolve

Architect insight:
Reads can be globally distributed more easily than writes. Custom alias creation is the tricky global operation.





32. Graceful degradation sheet
    During extreme traffic spikes, not every feature has equal importance.

Highest priority:

redirects must keep working

Lower priority:

detailed analytics

device breakdown

referrer processing

dashboard freshness

If overloaded:

continue redirects

skip synchronous analytics work

write raw logs for later processing

reduce dashboard freshness

use cached analytics summaries

Architect insight:
In a URL shortener, redirect availability is more important than real-time analytics accuracy.





33. Monitoring and alerting sheet
    Track application metrics:

redirect QPS

URL creation QPS

p95/p99 latency

error rate

404 rate

429 rate

cache hit ratio

queue lag



Track infrastructure metrics:

CPU usage

memory usage

network throughput

Redis latency

database latency

partition hot spots



Track business metrics:

URLs created per minute

redirects per second

active links

premium usage

suspicious URL creation patterns



Alert examples:

error rate > 1%

redirect latency > 100 ms

database latency > 100 ms

cache hit ratio drops suddenly

queue lag grows quickly

sudden spike in blocked URLs





34. Cost optimization sheet
    Cost drivers:

storage of billions of URLs

redirect traffic bandwidth

CDN cost

Redis/cache memory

analytics storage

stream processing

database read/write capacity



Optimization ideas:

cache popular URLs heavily

use CDN/edge redirects for viral links

archive inactive URLs

aggregate old analytics

move cold data to cheaper storage

use spot instances for batch analytics

separate hot, warm, and cold storage tiers

avoid storing unnecessary raw click data forever



Architect insight:
A URL shortener looks cheap per request, but at billions of requests, tiny inefficiencies become expensive.





35. Security and safety sheet
    Important controls:

validate URL protocol

allow only HTTP/HTTPS

block suspicious domains

blacklist known malware/phishing domains

scan links asynchronously if needed

restrict custom alias abuse

prevent reserved keyword usage

rate limit shortening APIs

monitor bot-like creation behavior

allow reporting abusive links

disable malicious short links quickly

Security challenge:
URL shorteners can hide malicious destinations, so abuse prevention is part of core system design, not an optional feature.





36. Trade-off matrix
    301 vs 302
    301 improves client-side caching but weakens analytics.
    302 preserves analytics but increases infrastructure load.



Hashing vs counter
Hashing is simple but collision-prone when truncated.
Counter-based generation is collision-free but needs distributed scaling.



SQL vs NoSQL
SQL is better for relational queries and strong constraints.
NoSQL is better for massive key-value lookup scale.



Synchronous vs asynchronous analytics
Synchronous analytics is immediate but slows redirects.
Asynchronous analytics is scalable but eventually consistent.



Deduplication vs separate analytics
Deduplication saves storage.
Separate short URLs preserve per-user/per-campaign analytics.



Global writes vs primary-region writes
Global writes reduce regional latency but create conflict complexity.
Primary-region writes simplify uniqueness but add write latency for distant users.





37. Decision framework: designing a URL shortener
    Use this sequence:

Clarify functional requirements.

Clarify non-functional requirements.

Estimate read/write ratio.

Identify redirect as the hot path.

Decide redirect status code.

Design API endpoints.

Choose storage for URL mappings.

Choose analytics storage separately.

Choose short code generation strategy.

Handle custom aliases and conflicts.

Add caching layers.

Separate read and write paths.

Add asynchronous analytics pipeline.

Add rate limiting and abuse prevention.

Add global distribution strategy.

Add monitoring, degradation, and cost optimization.





38. Bottlenecks and failure points sheet
    Potential bottlenecks:

database lookup on every redirect

Redis hot keys

popular URL hot partitions

analytics processing during redirect

global counter bottleneck

custom alias conflict checks

CDN cache miss storms

queue lag during viral spikes

database write pressure from click counters



Potential failures:

ID generator unavailable

Redis unavailable

database region outage

analytics stream overloaded

malicious URL abuse

DDoS attack on popular short links

invalid cache entries

expired link mishandling

custom alias race condition





39. Red flags and anti-patterns
    Doing heavy analytics processing inside the redirect request.

Updating click count synchronously on every click in the main URL row.

Using 301 redirects while promising accurate analytics.

Using truncated hashes without collision strategy.

Relying on one global counter without scaling plan.

Ignoring custom alias conflicts.

Not validating destination URLs.

Not rate limiting URL creation.

Not planning for viral links.

Storing every raw click forever in the main database.

Treating analytics consistency as equally critical as redirect correctness.

Not separating read-heavy redirect path from write-heavy creation/analytics paths.





40. Practical scenario mapping
    Marketing campaign
    Needs custom aliases, analytics, high availability, and accurate click tracking.



Viral celebrity link
Needs CDN caching, hot-key handling, autoscaling, and graceful analytics degradation.



Enterprise bulk shortening
Needs authentication, higher rate limits, bulk API, quotas, and abuse monitoring.



Malicious spammer
Needs rate limiting, domain blacklists, suspicious pattern detection, and fast link disabling.



Global campaign
Needs CDN/edge routing, regional caches, replicated URL mapping, and clear write conflict handling for custom aliases.





41. Design review questions
    What is the expected read-to-write ratio?

What is the redirect latency target?

Are analytics required for every click?

Should redirects use 301 or 302?

Should duplicate long URLs get the same short code or different short codes?

How do we guarantee short code uniqueness?

What happens when custom alias is already taken?

What database is used for URL lookup and why?

How do we avoid database lookup on every redirect?

How do we process analytics without slowing redirect?

How do we handle viral links?

How do we prevent abuse?

What happens if Redis is down?

What happens if the ID generator is down?

How do we serve users globally with low latency?





42. Mini case-study exercise 1
    Prompt
    Design a URL shortener for a startup that expects 10 million URLs in the first year and moderate traffic. They care more about speed of launch than massive global scale.



Your task
Decide:

SQL or NoSQL for the main URL mapping?

301 or 302 redirect?

Hashing or counter-based short code generation?

What caching is needed initially?

What can be deferred until later?



Expected thinking direction
A strong answer can choose a simpler architecture initially. SQL may be acceptable at this scale if the team wants speed and strong constraints. A counter-based approach is still cleaner for uniqueness. Redis can be added for popular URLs. Global replication and complex streaming analytics can wait unless requirements demand them.





43. Mini case-study exercise 2
    Prompt
    Design a URL shortener for a global platform expecting 100,000 redirect requests per second and occasional viral spikes.



Your task
Decide:

Full high-level architecture.

Redirect path optimization.

Short code generation strategy.

Caching layers.

Analytics pipeline.

Hot-key protection.

Global distribution strategy.

Abuse prevention.



Expected thinking direction
A strong answer should include CDN, load balancers, stateless redirect servers, Redis, NoSQL mapping store, asynchronous analytics stream, separate analytics warehouse, distributed ID generation, rate limiting, DDoS protection, monitoring, and graceful degradation.





44. Guided worksheet for this case study
    Use this sequence:

Define core users and actions.

Clarify functional requirements.

Clarify non-functional requirements.

Estimate writes per second.

Estimate redirects per second.

Identify read/write ratio.

Design API endpoints.

Decide 301 vs 302.

Design main URL mapping schema.

Design analytics data flow.

Choose SQL/NoSQL split.

Choose short code generation approach.

Handle collisions/custom aliases.

Add caching layers.

Add scaling strategy.

Add abuse prevention.

Add monitoring and degradation.

Review trade-offs.





45. Interview preparation: conceptual questions
    Why is a URL shortener a good system design problem?

Why is the redirect path the hot path?

Why is the read-to-write ratio important?

Why might 302 be better than 301?

Why can hashing cause collisions?

Why is Base62 useful?

How does a counter-based short code generator work?

How do you scale a global counter?

Why should analytics be asynchronous?

Why should click count not always be updated in the main URL row?

Why is caching critical in a URL shortener?

What is a hot key?

How do custom aliases create consistency problems?

Why is abuse prevention important in URL shorteners?

How would you support global low-latency redirects?





46. Interview preparation: scenario questions
    A celebrity shares a short link and traffic spikes 100x. What happens in your design?

Two users request the same custom alias at the same time. How do you handle it?

Redis is down. Should redirects stop working?

Analytics pipeline is delayed by 10 minutes. Is that acceptable?

A user shortens a phishing URL. How does the system respond?

Your generated short codes are predictable. Is that a problem?

Your database partition for one short code is overloaded. What do you do?

Your CDN cache is serving an expired URL. How do you avoid this?

A user wants to shorten 10,000 URLs for a campaign. How should the API support this?

You need to reduce infrastructure cost. What optimizations would you make?





47. Interview preparation: trade-off questions
    301 vs 302 redirect?

Hashing vs counter-based generation?

SQL vs NoSQL for URL mappings?

Shared short URL for duplicate long URL vs unique short URL per request?

Synchronous analytics vs asynchronous analytics?

Strong consistency vs eventual consistency for analytics?

Global writes vs single primary write region?

Store raw clicks forever vs aggregate older data?

Custom aliases for all users vs premium users only?

CDN caching redirects vs always hitting origin for full analytics?





48. Self-test sheet
    Can you explain these without notes?

URL shortener functional requirements

URL shortener non-functional requirements

why redirects should be under 100 ms

why 302 is preferred for analytics

main API endpoints

main URL mapping table

analytics event table

SQL vs NoSQL choice

hashing collision problem

counter + Base62 approach

distributed ID generation

custom alias conflict handling

multi-level caching

hot-key handling

asynchronous analytics pipeline

graceful degradation during spikes





49. FAQ sheet
    Is a URL shortener just a simple CRUD app?
    No. At scale, redirect traffic, caching, uniqueness, analytics, hot keys, and abuse prevention make it a serious system design problem.



Why not always use 301 redirects?
Because browsers can cache 301 redirects, causing repeat clicks to bypass the shortener and reducing analytics accuracy.



Why not use hashing directly?
Full hashes are too long. Truncated hashes can collide. You need collision handling.



Is NoSQL always better for URL shorteners?
Not always. But for massive key-value lookup workloads, NoSQL is a strong fit. SQL may still be useful for users and analytics.



Should the same long URL always return the same short URL?
Not necessarily. Separate short URLs allow separate analytics per user or campaign.



Can analytics be delayed?
Yes. Analytics can usually be eventually consistent as long as redirects remain fast and correct.



What is the most important performance optimization?
Avoid hitting the database on every redirect. Use CDN and Redis/application caching.





50. Myth vs reality sheet
    Myth: URL shorteners are easy because they only store two columns.
    Reality: At scale, redirect traffic, analytics, abuse, and global latency make the design complex.

Myth: Hashing solves short code generation.
Reality: Truncated hashes introduce collision problems.

Myth: 301 is always better because it is faster.
Reality: 301 can hurt analytics visibility.

Myth: Click count can simply be updated on every redirect.
Reality: Popular links can create write hot spots.

Myth: Analytics must be perfectly real-time.
Reality: Redirect correctness matters more. Analytics can often be delayed.

Myth: Caching one layer is enough.
Reality: Large systems often use CDN, application cache, database cache, and edge optimization.





51. Quick revision sheet
    A URL shortener converts long URLs into short URLs and redirects users back to the original URL.

The main workload is read-heavy. A URL is created once but clicked many times. That makes the redirect path the most important path in the system.

The main functional requirements are URL shortening, redirection, custom aliases, expiry, and analytics.

The main non-functional requirements are low latency, high availability, scalability, short code compactness, uniqueness, abuse prevention, and global performance.

Use 302 redirects when analytics are important because 301 redirects may be cached by browsers.

Use NoSQL for high-volume short code lookup and SQL or analytical storage for users and analytics where complex queries matter.

Use counter-based Base62 or distributed ID generation for reliable short code generation. Avoid pure truncated hashing unless collision handling is well designed.

Use multi-level caching: CDN, Redis/application cache, and database. Do not hit the database on every redirect.

Process analytics asynchronously through a queue or streaming pipeline. Do not slow down redirects for analytics.

Handle hot keys, rate limiting, abuse prevention, global distribution, monitoring, and graceful degradation for production readiness.

Core idea:
A good URL shortener design is not about shortening strings. It is about optimizing a massive read-heavy redirect system while preserving uniqueness, analytics, safety, and scalability.





52. Top 10 points to remember
    URL shorteners are read-heavy systems.

Redirect latency is the most critical performance requirement.

302 redirects preserve analytics better than 301 redirects.

Short code uniqueness is a core design challenge.

Hashing can create collisions when truncated.

Counter-based Base62 generation is reliable and compact.

Custom aliases need validation and conflict handling.

Click analytics should be asynchronous.

Caching is essential to reduce database load.

Viral links create hot-key and hot-partition problems.





53. Section conclusion
    This case study is the first practical test of the student’s system design thinking.

The important lesson is that even a simple product like a URL shortener forces serious architectural decisions when scale is introduced. Students must think about requirements, API design, data models, uniqueness, caching, analytics, abuse prevention, global latency, and cost.

A strong answer is not about memorizing one architecture. It is about understanding why each decision was made and what trade-off it creates.

That is exactly the skill this course is trying to build.