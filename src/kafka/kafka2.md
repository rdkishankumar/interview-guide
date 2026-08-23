# Kafka Interview Questions & Answers

Here are structured notes based on the transcript:

---

#### Q1: Consumer Crash Before Offset Commit

**Scenario:** A topic has 1 partition with messages M1, M2, M3. A consumer starts processing M1, hits an exception, and crashes **without committing the offset**.

**Q: Which message will the consumer receive first on restart?**

**A:** M1 again. Kafka tracks consumer progress purely by **committed offset**. Since no offset was committed before the crash, the last committed offset is -1 (nothing consumed). On restart, Kafka resumes from offset 0, delivering M1 again.

> **Key concept:** Kafka does not track mid-processing state, only committed offsets.

---

#### Q2: Offset Committed Before Processing (At-Most-Once)

**Scenario:** Consumer commits the offset for M1 **before** processing it, then crashes during processing.

**Q1: What happens when the consumer restarts?**

**A:** The consumer resumes at **M2**. Since the offset for M1 was already committed, Kafka considers M1 done. M1 is never reprocessed even though it was never successfully handled. This is **at-most-once semantics** — message loss is possible, no duplicates.

**Q2: Is M1 lost permanently?**

**A:** No. M1 still physically exists on the broker at offset 0. Kafka's default **retention policy is 7 days**, so the message remains. It is only "lost" from the consumer group's perspective because the offset pointer moved past it.

**Q3: How can you consume M1 again?**

**A:** Two ways:
- Call `consumer.seek(partition, 0)` before polling to rewind to offset 0
- Use the CLI command `kafka-consumer-groups.sh --reset-offsets` to reset the group offset

> **Important:** Rewinding offsets may cause **duplicate processing** of already-processed messages (e.g., M2, M3). To handle this safely, make the consumer **idempotent** and track processed event IDs in the database.

---

#### Q3: More Consumers Than Partitions

**Scenario:** A topic has **3 partitions** and a consumer group has **6 consumers**.

**Q1: What happens to the 6 consumers?**

**A:** Only 3 consumers are active (one per partition). The remaining **3 consumers stay idle**. If partitions increase in the future, idle consumers will become active.

**Q2: Can multiple consumers in the same group read the same partition?**

**A:** No. Within a single consumer group, **one partition is owned by exactly one consumer** at a time. This guarantees per-partition ordering and avoids duplicate processing.

> **Note:** Two different consumer groups CAN both read the same partition independently.

**Partition assignment algorithms:** Range, Round Robin, Sticky, Cooperative Sticky.

---

#### Q4: More Partitions Than Consumers

**Scenario:** A topic has **10 partitions** and a consumer group has **3 consumers**.

**Q1: How are partitions assigned?**

**A:** Partitions are distributed as evenly as possible. Example: Consumer 1 gets 4 partitions, Consumer 2 gets 3, Consumer 3 gets 3.

**Q2: Can a single consumer consume from multiple partitions?**

**A:** Yes. A single consumer instance can be assigned multiple partitions and processes messages from each in turn.

**Q3: How do you scale consumers to improve throughput?**

**A:** Add more consumer instances up to a **maximum equal to the number of partitions** (10 in this case). Beyond that, extra consumers remain idle. To scale further, **increase the partition count first**, then add more consumers.

---

#### Q5: Message Ordering

**Scenario:** A topic has 5 partitions receiving a continuous stream of messages.

**Q1: Does Kafka guarantee message ordering?**

**A:** Yes, but **conditionally**. Kafka preserves the order in which messages are appended to a **given partition**. There is **no global ordering** across partitions.

**Q2: Is order guaranteed topic-wide or only per partition?**

**A:** Only **per partition**. Messages across different partitions can be consumed in any interleaved order.

**Q3: How do you keep related messages in order?**

**A:** Use a **message key**. Kafka's default partitioner hashes the key and routes all messages with the same key to the same partition, preserving their relative order.

Formula: `hash(key) % number_of_partitions`

> **Hot Partition Warning:** Avoid using low-cardinality keys like customer name (e.g., "John"). If one customer generates millions of messages, that partition becomes a **hot partition** — overloaded while others sit idle. Use high-cardinality keys like `user_id`, `order_id`, or `product_id` instead.

---

#### Q6: Consumer Rebalancing

**Scenario:** A consumer group with 2 consumers (C1, C2) is processing 3 partitions. A new consumer C3 joins.

**Q1: What is a consumer rebalance?**

**A:** A rebalance occurs when a consumer **joins, leaves, or crashes**. The group coordinator redistributes partition ownership across all active members.

**Q2: What happens to partition ownership during rebalancing?**

**A:** Partitions are revoked from current owners and reassigned across all consumers (including the new one) following the group assignment strategy.

**Q3: What happens to consumption during rebalancing?**

**A:** Depends on the protocol:
- **Eager protocol (default):** All consumers stop processing until reassignment completes ("stop the world")
- **Incremental Cooperative Rebalancing (Cooperative Sticky):** Only partitions that actually move are paused, minimizing disruption. Other consumers continue unaffected.

---

#### Q7: Duplicate Processing

**Scenario:** A consumer successfully writes to the database but crashes **before committing the Kafka offset**. On restart, the same message is redelivered, causing a duplicate DB update.

**Q1: What happens when the consumer restarts?**

**A:** The uncommitted message is redelivered, resulting in the database update being applied a **second time** — a duplicate entry.

**Q2: How do you prevent duplicate database updates?**

**A:** Two-layer approach:
1. **Consumer side:** Make the consumer **idempotent** — send a unique `event_id` with each message and check if it was already processed before acting
2. **Database side:** Store processed `event_id` values. Use `event_id` as a primary key or unique constraint so duplicate inserts are rejected automatically

Other patterns: deduplication table, transactional outbox pattern.

**Q3: What is idempotency and why does it matter?**

**A:** An idempotent operation produces the **same result no matter how many times it is applied**. It matters because Kafka's default delivery guarantee is **at-least-once** — the same message may arrive more than once. Consumers must safely handle redelivery without side effects.

> **Analogy:** HTTP `PUT` and `DELETE` are idempotent; `POST` is not. Calling `PUT` multiple times gives the same result, while repeated `POST` calls can create duplicates.

---

#### Q8: Producer Partitioning

**Scenario:** A producer sends a message to a topic with multiple partitions.

**Q1: How does Kafka decide the target partition?**

**A:** In priority order:
1. **Explicit partition number** set by the producer — message goes directly to that partition
2. **Message key provided** — Kafka hashes the key: `hash(key) % numPartitions` and routes to the resulting partition
3. **No key or partition** — Kafka uses **sticky partitioning** (since v2.4): batches messages onto one partition at a time, then rotates. Distributes load evenly over time (previously round-robin)

**Q2: Why does the message key matter?**

**A:** The key controls two things:
- **Ordering:** Same key → same partition → preserved order
- **Load distribution:** A poorly chosen key (low cardinality) creates a **hot partition** that overloads one consumer while others sit idle

---

#### Q9: Broker Failure & Leader Election

**Scenario:** The broker that is the current leader for a partition crashes unexpectedly.

**Q1: How is a new leader elected?**

**A:** The **cluster controller** detects the failure and promotes one of the partition's **In-Sync Replicas (ISR)** to become the new leader.
- Older Kafka: coordinated via **ZooKeeper**
- Modern Kafka (KRaft mode): uses a built-in **Raft-based controller** — no ZooKeeper needed

**Q2: Can producers and consumers keep working during election?**

**A:** Yes. Clients refresh metadata, discover the new leader, and reconnect automatically. There may be **brief retries or a latency spike**, but not an outage — as long as failover completes quickly.

**Q3: What if no in-sync replicas are available?**

**A:** Controlled by the `unclean.leader.election.enable` property:
- **`false` (default/safe):** Partition becomes **unavailable** until an ISR replica recovers. No data loss.
- **`true`:** An out-of-sync replica can be elected leader to restore availability, but **recent uncommitted messages may be lost**.

---

#### Quick Summary Table

| Concept | Key Takeaway |
|---|---|
| Offset commit | Commit **after** processing to avoid message loss |
| At-most-once | Commit before processing — loss possible, no duplicates |
| At-least-once | Commit after processing — duplicates possible, no loss |
| Idempotency | Same operation applied multiple times = same result |
| Partition ordering | Guaranteed **within** a partition only, not topic-wide |
| Message key | Routes same key to same partition; choose high-cardinality keys |
| Hot partition | Caused by low-cardinality keys; avoid customer names, use IDs |
| Consumer rebalance | Triggered by join/leave/crash; Cooperative Sticky minimizes disruption |
| Max useful consumers | Equal to number of partitions; extras stay idle |
| Leader election | ISR replica promoted; KRaft replaces ZooKeeper in modern Kafka |