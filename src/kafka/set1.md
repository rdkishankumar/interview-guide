An **offset** in Apache Kafka is a unique, monotonically increasing 64-bit integer (`0, 1, 2, 3...`) assigned to every record written to a topic partition. It acts as a immutable position marker or logical address for a message within that specific partition.

```text
Topic: "order-events" | Partition 0
+------------+------------+------------+------------+------------+
| Offset 0   | Offset 1   | Offset 2   | Offset 3   | Offset 4   | ...
| {id: 101}  | {id: 102}  | {id: 103}  | {id: 104}  | {id: 105}  |
+------------+------------+------------+------------+------------+
                                             ^
                                             |
                              Current Consumer Read Position

```

### Key Characteristics of Offsets

1. **Partition-Scoped Only:** Offsets are unique only within a single partition. (e.g., Partition 0 has an Offset 5, and Partition 1 also has its own independent Offset 5).
2. **Immutable:** Once written to an offset position, a message never changes or gets re-ordered.
3. **Consumer State Tracking:** Kafka brokers do not track which consumer has read which message. Instead, consumers track their own read position by saving (**committing**) the last processed offset to an internal Kafka topic named `__consumer_offsets`.

---

## Follow-up Questions & Answers

### Q1: What is the difference between an Uncommitted Offset, Committed Offset, and Consumer Position?

* **Consumer Position:** The offset of the *next record* the consumer is about to read during its polling cycle.
* **Committed Offset:** The highest offset that the consumer group has successfully processed and saved to the `__consumer_offsets` topic. If a consumer crashes, the replacement consumer will start reading from this committed offset.
* **Uncommitted Offset:** Messages that have been polled and processed by the application in memory, but whose offset numbers have not yet been sent/saved to `__consumer_offsets`.

---

### Q2: What happens if a consumer crashes before committing its offset?

If a consumer processes a batch of messages (e.g., offsets 10 to 15) but crashes before committing offset 15, Kafka triggers a rebalance and assigns that partition to another consumer.

The new consumer fetches the last known **Committed Offset** (which was 10) and re-reads messages 10 to 15. This causes **duplicate processing** (at-least-once delivery), which is why downstream applications should be designed to be **idempotent**.

---

### Q3: What does `auto.offset.reset` do, and what are its values?

When a new consumer group joins, or when a consumer requests an offset that no longer exists on the broker (e.g., deleted due to retention policies), `auto.offset.reset` tells the consumer where to start reading:

* **`earliest`:** Rewinds to the beginning of the partition (reads all historical data available).
* **`latest`** *(default)*: Skips all historical data and starts reading only newly arriving messages.
* **`none`:** Throws an `OffsetOutOfRangeException` if no previous offset commit is found for the group.

---

### Q4: How do Automatic Commits (`enable.auto.commit=true`) differ from Manual Commits?

* **Automatic Commits:** The Kafka client automatically sends offset commits periodically at fixed intervals defined by `auto.commit.interval.ms` (default 5 seconds).
* *Pros:* Simple to configure.
* *Cons:* Higher risk of data loss or duplicates if a crash happens mid-interval.


* **Manual Commits (`enable.auto.commit=false`):** The application explicitly calls `commitSync()` or `commitAsync()` in code after processing records.
* *Pros:* Precise control over exact processing boundaries.
* *Cons:* Requires developer logic to handle commit failures and retries.