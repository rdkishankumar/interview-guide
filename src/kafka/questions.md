# Kafka Interview Questions: Basic to Advanced

For a **5+ years Java + Spring Boot + Microservices developer**, I’d prepare Kafka in this sequence:

**Fundamentals → Architecture → Topics/Partitions → Producers → Consumers → Consumer Groups → Offsets → Delivery Semantics → Replication → Performance → Spring Kafka → Failure Handling → Transactions → Security → Monitoring → Production Scenarios.**

---

## 1. Kafka Fundamentals

1. What is Apache Kafka?
2. Why was Kafka created?
3. What problems does Kafka solve?
4. Is Kafka a message queue or an event-streaming platform?
5. What are the main components of Kafka?
6. What is a Kafka cluster?
7. What is a Kafka broker?
8. What is a Kafka topic?
9. What is a Kafka partition?
10. What is an offset?
11. What is a Kafka record?
12. What is a producer?
13. What is a consumer?
14. What is a consumer group?
15. What is ZooKeeper?
16. What is KRaft?
17. Why did Kafka move from ZooKeeper to KRaft?
18. Difference between ZooKeeper and KRaft.
19. Is Kafka push-based or pull-based?
20. Why does Kafka use a pull-based consumer model?
21. What are common Kafka use cases?
22. Kafka vs RabbitMQ?
23. Kafka vs traditional JMS?
24. Kafka vs database polling?
25. Why is Kafka suitable for microservices?

---

# 2. Kafka Architecture

26. Explain Kafka architecture.
27. How do producers communicate with brokers?
28. How do consumers communicate with brokers?
29. Where are Kafka messages stored?
30. Does Kafka store messages in memory?
31. Why is Kafka fast despite writing data to disk?
32. What is sequential disk I/O?
33. What is an append-only log?
34. What is a log segment?
35. Why does Kafka divide partitions into segments?
36. What is the active segment?
37. What is log retention?
38. What is time-based retention?
39. What is size-based retention?
40. What is log compaction?
41. Difference between log retention and log compaction.
42. What is an immutable log?
43. How does Kafka achieve scalability?
44. How does Kafka achieve fault tolerance?
45. How does Kafka achieve high throughput?
46. How does Kafka achieve durability?
47. How does Kafka handle broker failure?
48. What is a leader partition?
49. What is a follower replica?
50. What is partition replication?

---

# 3. Topics and Partitions

51. What is a topic?
52. What is a partition?
53. Why does Kafka use partitions?
54. How do partitions provide parallelism?
55. How do partitions improve throughput?
56. Can a topic have only one partition?
57. Can you increase the number of partitions?
58. Can you decrease the number of partitions?
59. What happens to existing messages when partitions are increased?
60. Does increasing partitions affect ordering?
61. Is message ordering guaranteed?
62. Is ordering guaranteed across partitions?
63. Is ordering guaranteed within a partition?
64. How does Kafka determine which partition receives a message?
65. What is a message key?
66. How does the key affect partitioning?
67. What happens when a producer sends a message without a key?
68. What is a partitioner?
69. What is a custom partitioner?
70. When would you use a custom partitioner?
71. What is partition skew?
72. What causes partition skew?
73. How do you detect partition skew?
74. How do you fix partition skew?
75. How do you decide the number of partitions for a topic?

---

# 4. Producer

76. What is a Kafka producer?
77. How does a producer send messages?
78. What is `ProducerRecord`?
79. What is `bootstrap.servers`?
80. Why do producers need bootstrap servers?
81. What is `acks`?
82. Explain `acks=0`.
83. Explain `acks=1`.
84. Explain `acks=all`.
85. Which `acks` setting provides the strongest durability?
86. What is `min.insync.replicas`?
87. How do `acks=all` and `min.insync.replicas` work together?
88. What is producer batching?
89. What is `batch.size`?
90. What is `linger.ms`?
91. Difference between `batch.size` and `linger.ms`.
92. What is producer compression?
93. Which compression types does Kafka support?
94. How does compression improve Kafka performance?
95. What is `buffer.memory`?
96. What happens when the producer buffer becomes full?
97. What is `max.block.ms`?
98. What is `send()`?
99. What is asynchronous producer sending?
100. How do producer callbacks work?
101. How do you send messages synchronously?
102. Synchronous vs asynchronous producer.
103. What is producer retry?
104. What is `retries`?
105. What is `delivery.timeout.ms`?
106. What is `request.timeout.ms`?
107. What happens when the broker is unavailable?
108. How do you guarantee producer ordering?
109. Can retries cause duplicate messages?
110. What is an idempotent producer?
111. How does Kafka producer idempotence work?
112. What is `enable.idempotence`?
113. What is `max.in.flight.requests.per.connection`?
114. How does it affect ordering?
115. How would you configure a producer for high throughput?
116. How would you configure a producer for high durability?

---

# 5. Consumer Basics

117. What is a Kafka consumer?
118. How does a consumer read messages?
119. What is `poll()`?
120. Why must a consumer continuously call `poll()`?
121. What is `max.poll.records`?
122. What is `max.poll.interval.ms`?
123. What is `session.timeout.ms`?
124. What is `heartbeat.interval.ms`?
125. What is a consumer heartbeat?
126. What happens when a consumer stops polling?
127. What happens when a consumer stops sending heartbeats?
128. What is consumer rebalance?
129. What causes a rebalance?
130. Why can rebalancing be expensive?
131. What is eager rebalancing?
132. What is cooperative rebalancing?
133. Difference between eager and cooperative rebalancing.
134. What is static consumer membership?
135. What is `group.instance.id`?
136. How does static membership reduce unnecessary rebalances?
137. Can one consumer consume multiple partitions?
138. Can multiple consumers consume the same partition within one group?
139. What happens when consumers are more than partitions?
140. What happens when partitions are more than consumers?

---

# 6. Consumer Groups

141. What is a consumer group?
142. Why do we need consumer groups?
143. How does Kafka distribute partitions among consumers?
144. Can two consumer groups consume the same topic?
145. Do different consumer groups have independent offsets?
146. Can two consumers in the same group consume the same partition?
147. What happens when a consumer joins a group?
148. What happens when a consumer leaves a group?
149. What is partition assignment?
150. What are consumer group assignors?
151. What is RangeAssignor?
152. What is RoundRobinAssignor?
153. What is StickyAssignor?
154. What is CooperativeStickyAssignor?
155. How do you choose a partition assignment strategy?
156. What happens when a consumer crashes?
157. How quickly does Kafka detect a failed consumer?
158. How does consumer group rebalancing work?
159. How do you minimize consumer rebalances?
160. What is consumer group lag?
161. How do you monitor consumer lag?
162. What causes consumer lag?
163. How do you reduce consumer lag?

---

# 7. Offsets

164. What is a Kafka offset?
165. Where is the offset stored?
166. What is `__consumer_offsets`?
167. What is offset committing?
168. What is auto commit?
169. What is manual commit?
170. Difference between automatic and manual offset commits.
171. What is `enable.auto.commit`?
172. What is `auto.commit.interval.ms`?
173. What is synchronous offset commit?
174. What is asynchronous offset commit?
175. Difference between `commitSync()` and `commitAsync()`.
176. When should you use `commitSync()`?
177. When should you use `commitAsync()`?
178. What happens if a consumer crashes before committing?
179. What happens if a consumer commits before processing?
180. What happens if a consumer processes before committing?
181. What is duplicate processing?
182. What is message loss due to incorrect offset management?
183. How do you achieve at-least-once processing?
184. How do you achieve effectively-once business processing?
185. What is offset reset?
186. What is `auto.offset.reset`?
187. Difference between `earliest`, `latest`, and `none`.
188. When would you use `earliest`?
189. When would you use `latest`?
190. What happens if a consumer group has no committed offset?

---

# 8. Delivery Semantics

191. What are Kafka delivery guarantees?
192. What is at-most-once delivery?
193. What is at-least-once delivery?
194. What is exactly-once delivery?
195. Difference between at-most-once and at-least-once.
196. How do duplicate messages occur?
197. How do you handle duplicate messages?
198. What is idempotent message processing?
199. How do you make a Kafka consumer idempotent?
200. What is exactly-once semantics?
201. What is EOS?
202. How does Kafka support exactly-once semantics?
203. What is an idempotent producer?
204. What are Kafka transactions?
205. Difference between producer idempotence and transactions.
206. Can Kafka guarantee exactly-once processing of an external database operation?
207. How would you implement Kafka + database consistency?

---

# 9. Replication & Fault Tolerance

208. What is replication factor?
209. Why do Kafka partitions have replicas?
210. What is ISR?
211. What is an In-Sync Replica?
212. What causes a replica to leave ISR?
213. What happens when a follower falls behind?
214. What happens when a leader broker fails?
215. How is a new leader elected?
216. What is leader election?
217. What is preferred leader?
218. What is unclean leader election?
219. Why can unclean leader election cause data loss?
220. What is `unclean.leader.election.enable`?
221. What is `min.insync.replicas`?
222. How does replication factor affect durability?
223. What happens if replication factor is 3 and two brokers fail?
224. How does Kafka survive broker failures?
225. What is rack awareness?
226. Why is rack awareness important?
227. How do you distribute replicas across availability zones?
228. How would you design Kafka for high availability?

---

# 10. Kafka Performance

229. Why is Kafka highly performant?
230. What is zero-copy?
231. How does sequential I/O improve performance?
232. What is batching?
233. What is compression?
234. How does batching affect latency?
235. How does compression affect CPU?
236. How does partition count affect throughput?
237. How does consumer parallelism affect throughput?
238. What is producer throughput?
239. What is consumer throughput?
240. How do you increase producer throughput?
241. How do you increase consumer throughput?
242. What causes high producer latency?
243. What causes high consumer latency?
244. What causes consumer lag?
245. What causes broker CPU spikes?
246. What causes broker disk I/O spikes?
247. What causes network saturation?
248. How would you tune Kafka for high throughput?
249. How would you tune Kafka for low latency?
250. What is the trade-off between throughput and latency?

---

# 11. Kafka Consumer Lag

251. What is consumer lag?
252. How is consumer lag calculated?
253. Why does consumer lag increase?
254. How do you monitor consumer lag?
255. What tools can monitor Kafka lag?
256. How do you troubleshoot increasing lag?
257. What happens if consumer processing is slower than producer throughput?
258. How can you increase consumer throughput?
259. How does increasing partitions help consumer lag?
260. Why can't you solve lag just by adding consumers?
261. What happens when consumers exceed partition count?
262. What is consumer processing time?
263. How does `max.poll.interval.ms` affect slow consumers?
264. How can long processing cause consumer rebalances?
265. How do you handle long-running consumer processing?
266. How do you separate polling from processing?
267. How would you troubleshoot a consumer that suddenly starts lagging?

---

# 12. Kafka Retention & Log Compaction

268. What is Kafka retention?
269. Why doesn't Kafka delete a message immediately after consumption?
270. How long can Kafka retain messages?
271. What is `retention.ms`?
272. What is `retention.bytes`?
273. What is log compaction?
274. How does log compaction work?
275. What is a compacted topic?
276. When should you use log compaction?
277. Can a compacted topic contain duplicate keys?
278. What is a tombstone record?
279. How do tombstones work with compaction?
280. Difference between delete retention and compaction.
281. Can a topic use both retention and compaction?

---

# 13. Kafka Transactions

282. What is a Kafka transaction?
283. Why are Kafka transactions needed?
284. What is `transactional.id`?
285. What is `read_committed`?
286. What is `read_uncommitted`?
287. Difference between `read_committed` and `read_uncommitted`.
288. How does Kafka transaction processing work?
289. What is exactly-once processing?
290. How do transactions work across multiple Kafka partitions?
291. Can Kafka transactions span multiple topics?
292. Can Kafka transactions span multiple consumer groups?
293. What happens if a transaction fails?
294. What happens if a producer crashes during a transaction?
295. What is transaction timeout?
296. What are the limitations of Kafka transactions?
297. Kafka transaction vs database transaction?
298. How would you implement Kafka-to-Kafka exactly-once processing?

---

# 14. Kafka + Database

299. How do you consume Kafka messages and update a database?
300. What happens if DB update succeeds but Kafka offset commit fails?
301. What happens if Kafka processing succeeds but DB transaction fails?
302. How do you avoid duplicate database updates?
303. What is the Transactional Outbox Pattern?
304. Why is Transactional Outbox useful?
305. What is CDC?
306. How does Debezium work?
307. Kafka + Outbox vs distributed transactions.
308. How do you guarantee database and Kafka consistency?
309. How do you make database writes idempotent?
310. How would you design payment processing using Kafka and a database?

---

# 15. Spring Kafka

311. What is Spring Kafka?
312. How do you create a Kafka producer in Spring Boot?
313. What is `KafkaTemplate`?
314. How do you send messages using `KafkaTemplate`?
315. What is `@KafkaListener`?
316. How does `@KafkaListener` work?
317. What is a listener container?
318. What is `ConcurrentKafkaListenerContainerFactory`?
319. How do you configure Kafka consumers in Spring Boot?
320. How do you configure Kafka producers in Spring Boot?
321. How do you configure consumer concurrency?
322. What does `concurrency` mean in `@KafkaListener`?
323. How does Spring Kafka handle acknowledgments?
324. What is `AckMode`?
325. Explain `RECORD`.
326. Explain `BATCH`.
327. Explain `MANUAL`.
328. Explain `MANUAL_IMMEDIATE`.
329. How do you manually acknowledge a Kafka message?
330. How do you handle exceptions in Spring Kafka?
331. What is `DefaultErrorHandler`?
332. How do you configure retry?
333. What is `DeadLetterPublishingRecoverer`?
334. What is a Dead Letter Topic?
335. How do you implement retry topics?
336. How do you configure Kafka transactions in Spring Boot?
337. How do you implement Kafka producer idempotence?
338. How do you handle deserialization errors?
339. How do you handle poison messages?
340. How do you implement concurrency in Spring Kafka?
341. How do you pause and resume a Kafka listener?
342. How do you control consumer acknowledgment?
343. How do you monitor Spring Kafka consumers?

---

# 16. Retry & Dead Letter Topics

344. What is Kafka retry?
345. Why should consumers retry failed messages?
346. What is exponential backoff?
347. What is fixed backoff?
348. What is a retry topic?
349. What is a Dead Letter Topic?
350. Difference between retry topic and DLT.
351. What is a poison message?
352. Why can infinite retries be dangerous?
353. How do you prevent retry storms?
354. How do you decide the maximum retry count?
355. What happens after all retries fail?
356. How do you replay DLT messages?
357. How do you prevent duplicate processing during replay?
358. How would you design retry for a payment event?

---

# 17. Kafka Security

359. How do you secure Kafka?
360. What is authentication?
361. What is authorization?
362. What is SSL/TLS?
363. What is SASL?
364. Difference between SSL and SASL.
365. What is SASL/SCRAM?
366. What is SASL/OAUTHBEARER?
367. What are Kafka ACLs?
368. How do ACLs work?
369. How do you restrict a producer to specific topics?
370. How do you restrict a consumer to specific consumer groups?
371. How do you encrypt Kafka traffic?
372. How do you secure Kafka credentials?
373. How do you rotate Kafka credentials?
374. How would you secure Kafka in production?

---

# 18. Kafka Schema & Serialization

375. What is serialization?
376. What is deserialization?
377. What serializers does Kafka provide?
378. What is JSON serialization?
379. What is Avro?
380. What is Protobuf?
381. What is a Schema Registry?
382. Why do we need Schema Registry?
383. What is schema evolution?
384. What is backward compatibility?
385. What is forward compatibility?
386. What is full compatibility?
387. What happens if producer and consumer schemas differ?
388. Why is Avro commonly used with Kafka?
389. Avro vs JSON?
390. Avro vs Protobuf?
391. How do you handle breaking schema changes?
392. How would you version Kafka events?

---

# 19. Kafka Monitoring

393. How do you monitor Kafka?
394. What Kafka metrics are important?
395. What is consumer lag?
396. What is UnderReplicatedPartitions?
397. What is OfflinePartitionsCount?
398. What is ActiveControllerCount?
399. What is RequestLatency?
400. What is BytesInPerSec?
401. What is BytesOutPerSec?
402. What is MessagesInPerSec?
403. What is ISR shrink/expand?
404. What broker metrics would you monitor?
405. What consumer metrics would you monitor?
406. What producer metrics would you monitor?
407. How do you detect broker failure?
408. How do you detect partition imbalance?
409. How do you detect consumer lag?
410. How do you troubleshoot Kafka using JMX metrics?
411. What tools can you use for Kafka monitoring?
412. How would you set alerts for Kafka?

---

# 20. Kafka CLI Questions

413. How do you create a topic?
414. How do you list topics?
415. How do you describe a topic?
416. How do you alter a topic?
417. How do you delete a topic?
418. How do you produce messages from the command line?
419. How do you consume messages from the command line?
420. How do you consume from the beginning?
421. How do you specify a consumer group?
422. How do you list consumer groups?
423. How do you describe a consumer group?
424. How do you check consumer lag?
425. How do you reset consumer offsets?
426. How do you move offsets to the beginning?
427. How do you move offsets to the latest position?
428. How do you move offsets to a specific timestamp?
429. How do you inspect partition assignments?

---

# 21. Advanced Architecture

430. How would you design Kafka for millions of messages per second?
431. How would you choose partition count?
432. How would you choose replication factor?
433. How would you distribute brokers across availability zones?
434. How would you design Kafka for disaster recovery?
435. How would you replicate Kafka across regions?
436. What is MirrorMaker 2?
437. How does cross-region Kafka replication work?
438. Active-active vs active-passive Kafka architecture.
439. How do you handle regional failure?
440. How do you prevent duplicate events during failover?
441. How do you handle schema evolution across teams?
442. How do you enforce Kafka topic governance?
443. How do you decide topic naming conventions?
444. How do you control topic creation?
445. How do you handle thousands of Kafka topics?
446. What is the impact of too many partitions?
447. What is the impact of too many consumer groups?
448. How do you design Kafka for multi-tenancy?
449. How do you isolate workloads between teams?
450. How would you design Kafka for a payment system?

---

# 22. Production Troubleshooting

451. **Consumer lag is continuously increasing. What would you check?**

452. **Consumer lag suddenly jumps from 1,000 to 1 million. How would you troubleshoot?**

453. **Consumer processing is slow. How would you improve throughput?**

454. **Consumers are frequently rebalancing. What could be the reason?**

455. **A consumer keeps getting kicked out of the group. What would you investigate?**

456. **Kafka broker CPU is very high. What could cause it?**

457. **Kafka broker disk usage is increasing rapidly. What would you check?**

458. **Kafka disk is almost full. What would you do?**

459. **Under-replicated partitions are increasing. What does that mean?**

460. **A broker goes down. What happens to its partitions?**

461. **A producer is getting `TimeoutException`. How would you troubleshoot it?**

462. **Producer throughput is too low. How would you tune it?**

463. **Producer messages are appearing out of order. Why?**

464. **Consumers are processing duplicate messages. Why?**

465. **Messages appear to be lost. How would you investigate?**

466. **A consumer commits an offset but the DB transaction fails. What happens?**

467. **The DB transaction succeeds but offset commit fails. What happens?**

468. **A Kafka consumer takes 5 minutes to process one message. What Kafka settings would you investigate?**

469. **A single partition has much more traffic than the others. How would you solve it?**

470. **A Kafka topic has millions of messages and consumers need to replay them. How would you do it?**

471. **A poison message keeps failing. How would you prevent it from blocking processing?**

472. **How would you safely replay messages from a DLT?**

473. **How would you recover after accidental consumer offset reset?**

474. **How would you investigate duplicate payment events?**

475. **How would you design Kafka so that duplicate payment messages do not result in duplicate payments?**

---

# 🔥 Expert-Level Kafka Questions

476. Explain Kafka's complete message lifecycle from producer to consumer.

477. Explain how a Kafka producer discovers the partition leader.

478. Explain how Kafka handles leader election.

479. Explain ISR in detail.

480. Explain how `acks=all` works with `min.insync.replicas`.

481. Explain how producer idempotence prevents duplicates.

482. Explain Kafka transactions internally.

483. Explain exactly-once semantics.

484. Explain why exactly-once Kafka processing does not automatically mean exactly-once database processing.

485. Explain consumer group rebalancing.

486. Explain eager vs cooperative rebalancing.

487. Explain how consumer lag is calculated.

488. Explain why increasing consumers doesn't always reduce lag.

489. Explain why partitions determine consumer parallelism.

490. Explain how Kafka achieves high throughput.

491. Explain zero-copy in Kafka.

492. Explain batching and compression trade-offs.

493. Explain log segments and retention.

494. Explain log compaction and tombstones.

495. Explain Kafka replication and fault tolerance.

496. Explain how Kafka handles broker failure.

497. Explain Kafka's KRaft architecture.

498. Explain how you would design a highly available Kafka cluster across three availability zones.

---

# 💻 Kafka Coding Questions

499. Write a Kafka producer using Java.

500. Write a Kafka consumer using Java.

501. Write a Spring Boot Kafka producer using `KafkaTemplate`.

502. Write a Spring Boot consumer using `@KafkaListener`.

503. Implement manual acknowledgment.

504. Implement retry with exponential backoff.

505. Implement a Dead Letter Topic.

506. Implement idempotent Kafka message processing.

507. Implement a Kafka consumer that updates a database safely.

508. Implement Kafka producer transactions.

509. Implement Kafka-to-Kafka exactly-once processing.

510. Implement a consumer that processes messages concurrently.

511. Implement a custom Kafka partitioner.

512. Implement consumer retry without blocking the main partition indefinitely.

513. Implement an idempotency table for payment events.

514. Implement Transactional Outbox with Spring Boot and Kafka.

515. Implement a Kafka consumer that handles poison messages.

---

# ⭐ Top Questions for Your 5+ Year Interview

If your interview time is limited, focus heavily on these:

### Must Know

1. **What is Kafka and why use it?**
2. **Topic vs partition vs offset**
3. **How Kafka achieves high throughput**
4. **How partitioning works**
5. **How ordering works**
6. **Consumer vs consumer group**
7. **Consumer group rebalancing**
8. **Offset management**
9. **Auto vs manual commit**
10. **Consumer lag and how to fix it**
11. **At-most-once vs at-least-once vs exactly-once**
12. **Idempotent producer**
13. **Kafka transactions**
14. **Replication factor**
15. **ISR**
16. **Leader and follower**
17. **`acks=0/1/all`**
18. **`min.insync.replicas`**
19. **Producer retries and duplicates**
20. **Partition skew**

### Spring Boot + Kafka

21. `KafkaTemplate`
22. `@KafkaListener`
23. Consumer concurrency
24. Ack modes
25. Error handling
26. Retry and backoff
27. DLT
28. Poison messages
29. Deserialization errors
30. Kafka transactions
31. Idempotent consumer
32. Kafka + database consistency
33. Transactional Outbox
34. Schema Registry
35. Avro/Protobuf

### Production Scenarios

36. **Consumer lag is increasing. How do you troubleshoot and fix it?**
37. **Consumers are constantly rebalancing. Why?**
38. **Messages are duplicated. Why and how do you prevent it?**
39. **Messages appear to be lost. How do you investigate?**
40. **A producer is slow. How do you tune it?**
41. **One partition is overloaded. How do you fix partition skew?**
42. **A broker fails. What happens?**
43. **Under-replicated partitions are increasing. What do you do?**
44. **A consumer takes several minutes to process a message. How do you handle it?**
45. **A database update succeeds but Kafka offset commit fails. What happens?**
46. **Kafka processing succeeds but database update fails. What happens?**
47. **How do you design exactly-once payment processing?**
48. **How do you implement retry + DLT?**
49. **How do you replay failed messages safely?**
50. **Design a highly available Kafka architecture for a production microservices system.**

These last **50 questions** are the ones I'd put at the top of your preparation list for a senior Java/Spring Boot interview.
