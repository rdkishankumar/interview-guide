For a **5+ years Java backend interview**, prepare Multithreading in this order: **Thread basics → synchronization → concurrency utilities → executors → locks → concurrent collections → CompletableFuture → JVM internals → debugging → production scenarios**.

# Java Multithreading Interview Questions: Basic to Advanced

## 1. Thread Basics

1. What is a thread?
2. What is multithreading?
3. Why do we need multithreading?
4. Difference between process and thread.
5. Difference between concurrency and parallelism.
6. What is the main thread?
7. How do you create a thread in Java?
8. What are the different ways to create a thread?
9. Difference between extending `Thread` and implementing `Runnable`.
10. What is `Callable`?
11. Difference between `Runnable` and `Callable`.
12. What is `Future`?
13. How do you start a thread?
14. Difference between `start()` and `run()`.
15. What happens if you call `run()` directly?
16. Can you start the same thread twice?
17. What happens if you call `start()` twice?
18. What is a thread lifecycle?
19. What are the states of a Java thread?
20. Explain `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, and `TERMINATED`.
21. How do you get the current thread?
22. How do you assign a name to a thread?
23. How do you get a thread's ID?
24. What is thread priority?
25. Does thread priority guarantee execution order?
26. What is a daemon thread?
27. Difference between daemon and user threads.
28. How do you create a daemon thread?
29. What happens when all user threads finish?
30. What is `Thread.sleep()`?

---

# 2. Thread Control

31. What does `sleep()` do?
32. Does `sleep()` release a lock?
33. What does `join()` do?
34. Difference between `sleep()` and `join()`.
35. What does `yield()` do?
36. Is `yield()` guaranteed to pause a thread?
37. What is thread interruption?
38. What does `interrupt()` do?
39. Does `interrupt()` kill a thread?
40. How do you check whether a thread was interrupted?
41. Difference between `isInterrupted()` and `interrupted()`.
42. What happens when a sleeping thread is interrupted?
43. What happens when a waiting thread is interrupted?
44. How should interruption be handled?
45. Why should we preserve the interrupt status?

---

# 3. Synchronization Basics

46. What is synchronization?
47. Why do we need synchronization?
48. What is a race condition?
49. What is a critical section?
50. What is thread safety?
51. What is the `synchronized` keyword?
52. How does `synchronized` work?
53. What is an intrinsic lock?
54. What is a monitor?
55. What is an object's monitor?
56. What is a synchronized instance method?
57. What is a synchronized static method?
58. Difference between synchronized instance and static methods.
59. What object is locked by an instance synchronized method?
60. What object is locked by a static synchronized method?
61. What is a synchronized block?
62. Why prefer synchronized blocks sometimes?
63. Can two threads execute two synchronized methods of the same object?
64. Can two threads execute synchronized methods of different objects?
65. Can a synchronized method call another synchronized method?
66. Is `synchronized` reentrant?
67. What is reentrant synchronization?
68. Can a constructor be synchronized?
69. Can an interface method be synchronized?
70. Can a static method and instance method be synchronized at the same time?

---

# 4. Race Conditions & Thread Safety

71. What is a race condition?
72. Give a real-world example of a race condition.
73. How can you prevent race conditions?
74. What is atomicity?
75. What is visibility?
76. What is ordering?
77. What are the three main properties provided by synchronization?
78. What is a data race?
79. Difference between race condition and data race.
80. What is a thread-safe class?
81. How do you make a class thread-safe?
82. What is immutable object?
83. Why are immutable objects thread-safe?
84. How does immutability help in multithreading?
85. Can a `final` variable be changed?
86. Is `final` enough to make an object thread-safe?
87. Why is `String` thread-safe?
88. Is `StringBuilder` thread-safe?
89. Is `StringBuffer` thread-safe?
90. Difference between `StringBuilder` and `StringBuffer`.

---

# 5. Volatile

91. What is `volatile`?
92. Why do we use `volatile`?
93. What guarantees does volatile provide?
94. Does volatile provide atomicity?
95. Difference between `volatile` and `synchronized`.
96. When should you use volatile?
97. When should you not use volatile?
98. Explain the visibility problem using a shared variable.
99. Can `volatile int count` safely handle `count++`?
100. Why is `count++` not atomic even when `count` is volatile?
101. What is a happens-before relationship?
102. How does volatile establish happens-before?
103. Can volatile solve all concurrency problems?

---

# 6. Atomic Classes

104. What are atomic classes?
105. What is `AtomicInteger`?
106. What is `AtomicLong`?
107. What is `AtomicBoolean`?
108. What is `AtomicReference`?
109. Why use atomic classes?
110. Difference between `AtomicInteger` and synchronized integer updates.
111. How does `AtomicInteger.incrementAndGet()` work conceptually?
112. What is CAS?
113. What is Compare-And-Swap?
114. How does CAS work?
115. What is optimistic locking?
116. Difference between CAS and locking.
117. What is the ABA problem?
118. How can `AtomicStampedReference` solve the ABA problem?
119. When would you prefer Atomic classes over synchronized?
120. What are the limitations of CAS?

---

# 7. wait(), notify(), notifyAll()

121. What is `wait()`?
122. What is `notify()`?
123. What is `notifyAll()`?
124. Difference between `wait()` and `sleep()`.
125. Does `wait()` release the lock?
126. Does `sleep()` release the lock?
127. Why must `wait()` be called inside a synchronized block/method?
128. Why must `notify()` be called while holding the monitor?
129. Difference between `notify()` and `notifyAll()`.
130. What is a spurious wakeup?
131. Why should `wait()` generally be used inside a loop?
132. What happens when a thread calls `wait()`?
133. What happens when another thread calls `notify()`?
134. Can `wait()` be called on any object?
135. What happens if you call `wait()` without owning the monitor?
136. What is the classic Producer-Consumer problem?
137. How would you implement Producer-Consumer using `wait()` and `notify()`?

---

# 8. Lock API

138. What is `Lock`?
139. Why was the Lock API introduced?
140. Difference between `synchronized` and `Lock`.
141. What is `ReentrantLock`?
142. Why is it called reentrant?
143. What is `lock()`?
144. What is `unlock()`?
145. Why should `unlock()` usually be inside `finally`?
146. What is `tryLock()`?
147. What is timed `tryLock()`?
148. What is interruptible locking?
149. What is `lockInterruptibly()`?
150. When would you use `ReentrantLock` instead of synchronized?
151. What is fairness in `ReentrantLock`?
152. What is a fair lock?
153. What is the performance difference between synchronized and ReentrantLock?
154. Can ReentrantLock cause deadlock?
155. How do you prevent deadlock with `tryLock()`?

---

# 9. ReadWriteLock

156. What is `ReadWriteLock`?
157. What is `ReentrantReadWriteLock`?
158. Why use ReadWriteLock?
159. Difference between read lock and write lock.
160. Can multiple threads hold a read lock?
161. Can a reader and writer hold locks simultaneously?
162. When is ReadWriteLock useful?
163. What is lock downgrading?
164. Can you upgrade a read lock to a write lock?
165. What are the disadvantages of ReadWriteLock?

---

# 10. StampedLock

166. What is `StampedLock`?
167. Why was StampedLock introduced?
168. What are optimistic reads?
169. Difference between `ReentrantReadWriteLock` and `StampedLock`.
170. What is a stamp?
171. How does optimistic read work?
172. What happens when optimistic validation fails?
173. When should you use StampedLock?
174. What are the disadvantages of StampedLock?
175. Is StampedLock reentrant?

---

# 11. Executor Framework

176. What is Executor Framework?
177. Why do we need Executor Framework?
178. What is `Executor`?
179. What is `ExecutorService`?
180. Difference between `Executor` and `ExecutorService`.
181. What is `ScheduledExecutorService`?
182. What is `ThreadPoolExecutor`?
183. What is a thread pool?
184. Why use thread pools?
185. What happens when you create too many threads?
186. What is thread creation overhead?
187. How does ThreadPoolExecutor work?
188. What are the core components of ThreadPoolExecutor?
189. What is `corePoolSize`?
190. What is `maximumPoolSize`?
191. What is `keepAliveTime`?
192. What is `workQueue`?
193. What is `ThreadFactory`?
194. What is `RejectedExecutionHandler`?
195. What happens when the thread pool is full?
196. What are the built-in rejection policies?
197. Difference between `AbortPolicy`, `CallerRunsPolicy`, `DiscardPolicy`, and `DiscardOldestPolicy`.
198. How do you choose thread pool size?
199. Difference between CPU-bound and I/O-bound thread pools.
200. What happens if the queue is unbounded?
201. What happens if the queue is bounded?
202. Why can `Executors.newFixedThreadPool()` be dangerous?
203. Why can `Executors.newCachedThreadPool()` be dangerous?
204. Why is creating ThreadPoolExecutor explicitly sometimes preferred?
205. How do you properly shut down an ExecutorService?
206. Difference between `shutdown()` and `shutdownNow()`.
207. What happens to submitted tasks after `shutdown()`?
208. What is `awaitTermination()`?

---

# 12. Callable & Future

209. What is Callable?
210. Difference between Runnable and Callable.
211. What is Future?
212. How do you submit a Callable?
213. What does `Future.get()` do?
214. Does `Future.get()` block?
215. What is `Future.cancel()`?
216. What is `isDone()`?
217. What is `isCancelled()`?
218. What are the limitations of Future?
219. How does CompletableFuture improve upon Future?

---

# 13. Concurrent Collections

220. What is ConcurrentHashMap?
221. Why was ConcurrentHashMap introduced?
222. Difference between HashMap and ConcurrentHashMap.
223. Difference between Hashtable and ConcurrentHashMap.
224. Can ConcurrentHashMap contain null keys?
225. Can ConcurrentHashMap contain null values?
226. How does ConcurrentHashMap achieve thread safety?
227. Is ConcurrentHashMap completely lock-free?
228. What is weakly consistent iteration?
229. Can you modify ConcurrentHashMap while iterating?
230. What is CopyOnWriteArrayList?
231. When should you use CopyOnWriteArrayList?
232. What are the disadvantages of CopyOnWriteArrayList?
233. What is BlockingQueue?
234. What is ArrayBlockingQueue?
235. What is LinkedBlockingQueue?
236. Difference between ArrayBlockingQueue and LinkedBlockingQueue.
237. What is PriorityBlockingQueue?
238. What is DelayQueue?
239. What is SynchronousQueue?
240. When would you use BlockingQueue?
241. How does BlockingQueue help implement Producer-Consumer?

---

# 14. ConcurrentHashMap Advanced

242. How does ConcurrentHashMap work internally?
243. How did ConcurrentHashMap work in Java 7 vs Java 8?
244. What is bucket-level locking?
245. What is CAS in ConcurrentHashMap?
246. What is treeification?
247. When does a bucket become a tree?
248. What is `computeIfAbsent()`?
249. Difference between `putIfAbsent()` and `computeIfAbsent()`.
250. What is `merge()` in ConcurrentHashMap?
251. Can `computeIfAbsent()` cause performance problems?
252. How would you implement a thread-safe cache using ConcurrentHashMap?

---

# 15. Synchronizers

253. What is CountDownLatch?
254. How does CountDownLatch work?
255. Can CountDownLatch be reused?
256. What is CyclicBarrier?
257. Can CyclicBarrier be reused?
258. Difference between CountDownLatch and CyclicBarrier.
259. What is Phaser?
260. Difference between Phaser and CyclicBarrier.
261. What is Semaphore?
262. How does Semaphore control concurrency?
263. Difference between Semaphore and Lock.
264. What is Exchanger?
265. When would you use Semaphore?
266. Give a real-world example of CountDownLatch.
267. Give a real-world example of CyclicBarrier.

---

# 16. CompletableFuture & Async Programming

268. What is CompletableFuture?
269. Difference between Future and CompletableFuture.
270. What is `supplyAsync()`?
271. What is `runAsync()`?
272. Difference between `thenApply()` and `thenCompose()`.
273. Difference between `thenApply()` and `thenAccept()`.
274. Difference between `thenAccept()` and `thenRun()`.
275. What is `thenCombine()`?
276. What is `allOf()`?
277. What is `anyOf()`?
278. What is `exceptionally()`?
279. What is `handle()`?
280. What is `whenComplete()`?
281. How do you handle exceptions in CompletableFuture?
282. What Executor does CompletableFuture use by default?
283. How do you provide a custom Executor?
284. How do you execute multiple REST APIs concurrently?
285. How do you combine multiple API results?
286. How do you handle timeout in an async operation?
287. How do you cancel a CompletableFuture?
288. What happens when one stage of a CompletableFuture fails?
289. How can CompletableFuture cause thread-pool exhaustion?
290. How do you design a reliable asynchronous workflow?

---

# 17. Deadlock

291. What is deadlock?
292. What are the four necessary conditions for deadlock?
293. What is circular wait?
294. Give a real-world example of deadlock.
295. How can you prevent deadlock?
296. How can lock ordering prevent deadlock?
297. How can `tryLock()` help prevent deadlock?
298. How do you detect deadlock in Java?
299. What is `ThreadMXBean`?
300. How do you analyze a deadlock using a thread dump?
301. Difference between deadlock and starvation.
302. Difference between deadlock and livelock.

---

# 18. Starvation & Livelock

303. What is starvation?
304. What causes thread starvation?
305. How can unfair locks cause starvation?
306. What is livelock?
307. Difference between livelock and deadlock.
308. Give an example of livelock.
309. How can you prevent livelock?
310. How does thread priority contribute to starvation?

---

# 19. Java Memory Model

311. What is Java Memory Model?
312. Why is JMM important?
313. What is main memory?
314. What is thread-local memory?
315. What is visibility?
316. What is atomicity?
317. What is ordering?
318. What is happens-before?
319. Explain happens-before with synchronized.
320. Explain happens-before with volatile.
321. Explain happens-before with Thread.start().
322. Explain happens-before with Thread.join().
323. What is instruction reordering?
324. Why can instruction reordering cause concurrency bugs?
325. What is memory visibility?
326. What is safe publication?
327. What is unsafe publication?
328. What is double-checked locking?
329. Why does double-checked locking require volatile?
330. How does immutable object help safe publication?

---

# 20. ThreadLocal

331. What is ThreadLocal?
332. Why use ThreadLocal?
333. How does ThreadLocal work internally?
334. What is ThreadLocalMap?
335. Why are ThreadLocal keys weak references?
336. What happens to ThreadLocal values?
337. Why can ThreadLocal cause memory leaks?
338. Why should `ThreadLocal.remove()` be called?
339. How is ThreadLocal used in web applications?
340. Can ThreadLocal values be shared between threads?
341. What is `InheritableThreadLocal`?
342. What are the risks of using ThreadLocal with thread pools?

---

# 21. Advanced JVM & Thread Concepts

343. How does JVM schedule threads?
344. Does Java guarantee thread execution order?
345. What is context switching?
346. What is the cost of context switching?
347. What causes excessive context switching?
348. How does thread pool size affect context switching?
349. What is false sharing?
350. How can false sharing affect performance?
351. What is lock contention?
352. How do you identify lock contention?
353. What is biased locking?
354. What is lightweight locking?
355. What is heavyweight locking?
356. How does JVM optimize synchronized blocks?
357. What is lock inflation?
358. What is CAS spinning?
359. What is busy spinning?
360. When is spinning better than blocking?

---

# 22. Debugging & Monitoring

361. How do you debug a multithreading issue?
362. What is a thread dump?
363. How do you generate a thread dump?
364. What information does a thread dump provide?
365. How do you identify BLOCKED threads?
366. How do you identify WAITING threads?
367. How do you identify deadlocks from a thread dump?
368. What is `jstack`?
369. What is `jcmd Thread.print`?
370. How do you monitor thread count?
371. What is thread pool monitoring?
372. Which metrics do you monitor for a ThreadPoolExecutor?
373. How do you identify thread pool exhaustion?
374. How do you identify queue buildup?
375. How do you identify slow tasks?
376. How do you troubleshoot high CPU caused by threads?
377. How do you find which thread is consuming CPU?
378. How do you troubleshoot blocked threads in production?

---

# 23. Production Scenario Questions

379. **Your API response time suddenly increases. Thread count is also increasing. How would you investigate?**

380. **Your thread pool is completely exhausted. What could be the reasons?**

381. **Tasks are continuously waiting in the executor queue. What would you check?**

382. **CPU is 100%, but application throughput is low. What could be happening?**

383. **Your application has thousands of BLOCKED threads. How would you troubleshoot?**

384. **Your application has many WAITING threads. Is this necessarily a problem?**

385. **Two services are deadlocking each other. How would you identify and fix it?**

386. **A database call takes 10 seconds and you have 100 application threads. What happens?**

387. **How would you design a thread pool for a REST API making external API calls?**

388. **How would you design separate thread pools for CPU-bound and I/O-bound work?**

389. **A CompletableFuture application is exhausting its thread pool. What would you investigate?**

390. **A ThreadLocal value remains in memory after a request completes. Why?**

391. **A shared HashMap is causing inconsistent data in production. Why?**

392. **A synchronized method is becoming a performance bottleneck. How would you improve it?**

393. **You have a read-heavy cache. Would you use synchronized, ReentrantReadWriteLock, or ConcurrentHashMap? Why?**

394. **Multiple consumers need to process tasks safely. How would you implement it using BlockingQueue?**

395. **You need to limit concurrent calls to a third-party API to 20. How would you implement it?**

396. **You need to wait until five services finish initialization before accepting traffic. Which concurrency utility would you use?**

397. **You need multiple threads to reach a synchronization point before continuing. Which utility would you use?**

398. **One slow task should not block other tasks in an executor. How would you design the system?**

399. **How would you gracefully shut down a Spring Boot application's thread pools?**

400. **How would you investigate intermittent concurrency bugs that you cannot reproduce locally?**

---

# 🔥 Coding Questions

For interviews, practice these hands-on problems:

1. Create two threads using `Runnable`.
2. Print odd and even numbers using two threads.
3. Print numbers sequentially using multiple threads.
4. Implement Producer-Consumer using `wait()`/`notify()`.
5. Implement Producer-Consumer using `BlockingQueue`.
6. Implement a thread-safe Singleton.
7. Implement a thread-safe counter.
8. Implement a counter using `AtomicInteger`.
9. Implement a custom thread pool.
10. Implement a bounded blocking queue.
11. Implement a simple semaphore.
12. Implement a rate limiter.
13. Implement a thread-safe cache.
14. Implement a deadlock example.
15. Fix a deadlock.
16. Implement two threads where one waits for another using `join()`.
17. Use CountDownLatch to wait for multiple tasks.
18. Use CyclicBarrier for multiple threads.
19. Execute multiple tasks using ExecutorService.
20. Execute multiple APIs concurrently using CompletableFuture.
21. Combine multiple CompletableFuture results.
22. Handle CompletableFuture exceptions.
23. Implement timeout for asynchronous tasks.
24. Implement a retry mechanism using CompletableFuture.
25. Implement a thread-safe LRU cache.
26. Implement a concurrent counter using `ConcurrentHashMap`.
27. Build a task-processing system using `ThreadPoolExecutor`.
28. Implement a fixed-size worker pool.
29. Implement a producer-consumer system with multiple producers and consumers.
30. Implement a concurrent rate limiter using Semaphore.

## ⭐ Highest Priority for Your 5-Year Java Interview

Focus heavily on:

**Thread basics**
→ `Runnable` → `Callable` → `Future` → lifecycle → `start()` vs `run()`

**Core synchronization**
→ `synchronized` → monitor → race condition → thread safety → `volatile`

**Concurrency**
→ Atomic classes → CAS → `wait/notify` → `Lock` → `ReentrantLock` → `ReadWriteLock`

**Executors**
→ `Executor` → `ExecutorService` → `ThreadPoolExecutor` → queue → rejection policies → shutdown → CPU vs I/O thread pools

**Concurrent collections**
→ `ConcurrentHashMap` → `BlockingQueue` → `CopyOnWriteArrayList`

**Advanced**
→ `CompletableFuture` → JMM → happens-before → deadlock → starvation → livelock → ThreadLocal

**Production**
→ Thread dump → deadlock detection → thread-pool exhaustion → high CPU → blocked threads → queue buildup → lock contention → graceful shutdown.
---
