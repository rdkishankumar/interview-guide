# Java Collections Interview Questions: Basic to Advanced

For a **5+ years Java Backend Developer interview**, prepare Collections in this order:

**Collection basics → List → Set → Map → Queue/Deque → internal implementation → sorting → concurrent collections → Java 8 → performance → production scenarios.**

---

## 1. Collections Fundamentals

1. What is the Java Collections Framework?
2. Why do we need the Collections Framework?
3. What are the main interfaces in the Collections Framework?
4. Difference between `Collection` and `Collections`.
5. Difference between `Collection` and `Map`.
6. What is the difference between `Iterable` and `Collection`?
7. What is the Collection hierarchy?
8. What are the major interfaces in Collections?
9. Difference between `List`, `Set`, `Queue`, and `Map`.
10. When would you use List vs Set vs Map?
11. What is a generic collection?
12. Why should we use generics with collections?
13. What is type safety?
14. Can a collection store primitive types?
15. Why do collections use wrapper classes?
16. What is autoboxing?
17. What is unboxing?
18. What is fail-fast behavior?
19. What is fail-safe behavior?
20. Difference between Iterator and ListIterator.
21. What is `Iterable`?
22. How does enhanced `for` loop work internally?
23. What is `Iterator`?
24. What methods does Iterator provide?
25. What is `remove()` in Iterator?
26. What happens if you modify a collection while iterating?
27. What is `ConcurrentModificationException`?
28. Why does ConcurrentModificationException occur?
29. How can you safely remove elements while iterating?
30. What is the difference between `Iterator.remove()` and `Collection.remove()`?

---

# 2. List

31. What is a List?
32. What are the implementations of List?
33. Difference between `ArrayList` and `LinkedList`.
34. Difference between `ArrayList` and `Vector`.
35. Difference between `ArrayList` and `CopyOnWriteArrayList`.
36. How does ArrayList work internally?
37. What is the default capacity of ArrayList?
38. How does ArrayList grow?
39. What happens when ArrayList reaches its capacity?
40. What is the resizing mechanism of ArrayList?
41. What is the time complexity of ArrayList operations?
42. Why is random access fast in ArrayList?
43. Why is insertion in the middle expensive in ArrayList?
44. Why is deletion in the middle expensive?
45. When should you use ArrayList?
46. When should you avoid ArrayList?
47. How does LinkedList work internally?
48. What data structure does LinkedList use?
49. What is a doubly linked list?
50. Why is insertion efficient in LinkedList?
51. Is LinkedList always faster for insertion?
52. Why can LinkedList be slower than ArrayList in practice?
53. What is the time complexity of LinkedList operations?
54. Can ArrayList contain null?
55. Can LinkedList contain null?
56. Is ArrayList thread-safe?
57. Is LinkedList thread-safe?
58. How can you make an ArrayList thread-safe?
59. What is `Collections.synchronizedList()`?
60. What is the difference between `synchronizedList()` and CopyOnWriteArrayList?

---

# 3. Set

61. What is a Set?
62. Why does Set not allow duplicates?
63. What are the implementations of Set?
64. Difference between HashSet, LinkedHashSet, and TreeSet.
65. How does HashSet work internally?
66. What data structure does HashSet use internally?
67. Why does HashSet use HashMap internally?
68. What happens when you call `HashSet.add()`?
69. How does HashSet determine duplicates?
70. What is the role of `hashCode()` in HashSet?
71. What is the role of `equals()` in HashSet?
72. What happens if `equals()` and `hashCode()` are inconsistent?
73. Can HashSet contain null?
74. Can HashSet contain multiple null values?
75. Does HashSet maintain insertion order?
76. When should you use LinkedHashSet?
77. How does LinkedHashSet maintain insertion order?
78. What is TreeSet?
79. How does TreeSet maintain sorted order?
80. What data structure does TreeSet use?
81. What is a Red-Black Tree?
82. What is the time complexity of TreeSet?
83. Can TreeSet contain null?
84. Difference between Comparable and Comparator in TreeSet.
85. What happens if Comparator considers two objects equal?
86. Can TreeSet store custom objects?
87. What happens if custom objects don't implement Comparable?

---

# 4. Map Fundamentals

88. What is a Map?
89. Why doesn't Map extend Collection?
90. What are the major Map implementations?
91. Difference between HashMap, LinkedHashMap, and TreeMap.
92. Difference between HashMap and Hashtable.
93. Difference between HashMap and ConcurrentHashMap.
94. Can HashMap contain null keys?
95. Can HashMap contain null values?
96. Can Hashtable contain null keys or values?
97. Can ConcurrentHashMap contain null keys or values?
98. Is HashMap thread-safe?
99. Is LinkedHashMap thread-safe?
100. Is TreeMap thread-safe?
101. Is Hashtable thread-safe?
102. Is ConcurrentHashMap thread-safe?
103. When would you use HashMap?
104. When would you use LinkedHashMap?
105. When would you use TreeMap?
106. When would you use ConcurrentHashMap?

---

# 5. HashMap Internals

107. How does HashMap work internally?
108. What happens when you call `put()`?
109. What happens when you call `get()`?
110. What is a bucket?
111. What is hashing?
112. How is the bucket index calculated?
113. What is hash collision?
114. How does HashMap handle collisions?
115. What is separate chaining?
116. What is a linked list bucket?
117. What is treeification?
118. When does HashMap convert a bucket into a tree?
119. What is `TREEIFY_THRESHOLD`?
120. What is `UNTREEIFY_THRESHOLD`?
121. What is `MIN_TREEIFY_CAPACITY`?
122. Why was Red-Black Tree introduced into HashMap?
123. What is the worst-case complexity of HashMap lookup?
124. What is the average complexity of HashMap lookup?
125. What is load factor?
126. What is the default load factor of HashMap?
127. Why is the default load factor 0.75?
128. What is initial capacity?
129. Difference between initial capacity and load factor.
130. What happens when HashMap reaches its threshold?
131. What is rehashing?
132. What is resizing?
133. Why is resizing expensive?
134. Can you avoid unnecessary HashMap resizing?
135. Why should capacity sometimes be initialized in advance?
136. What happens if HashMap has too many collisions?
137. How does Java 8 improve HashMap collision handling?
138. Can mutable objects be used as HashMap keys?
139. Why are immutable objects preferred as HashMap keys?
140. What happens if a key is modified after insertion?

---

# 6. equals() and hashCode()

141. What is the contract between equals() and hashCode()?
142. Why must equal objects have the same hashCode?
143. Can two unequal objects have the same hashCode?
144. What is a hash collision?
145. What happens if you override equals() but not hashCode()?
146. What happens if you override hashCode() but not equals()?
147. How do equals() and hashCode() work in HashMap?
148. How do equals() and hashCode() work in HashSet?
149. Why should HashMap keys ideally be immutable?
150. What happens if hashCode changes after insertion?
151. How would you implement equals() and hashCode() for an Employee class?
152. Difference between `==` and `equals()`.
153. What is the equals contract?
154. What are symmetry, transitivity, reflexivity, consistency, and null comparison?

---

# 7. LinkedHashMap

155. What is LinkedHashMap?
156. How does LinkedHashMap maintain insertion order?
157. Difference between insertion order and access order.
158. How do you create an access-order LinkedHashMap?
159. What is the use of access-order?
160. How can LinkedHashMap be used to implement LRU cache?
161. What is `removeEldestEntry()`?
162. How would you implement an LRU cache using LinkedHashMap?
163. Is LinkedHashMap thread-safe?

---

# 8. TreeMap

164. What is TreeMap?
165. How does TreeMap work internally?
166. What data structure does TreeMap use?
167. What is a Red-Black Tree?
168. What is the complexity of TreeMap operations?
169. Difference between TreeMap and HashMap.
170. Difference between TreeMap and LinkedHashMap.
171. How does TreeMap sort keys?
172. What is natural ordering?
173. What is custom ordering?
174. What happens when Comparator returns zero?
175. Can TreeMap contain null keys?
176. Can TreeMap contain null values?
177. How do you sort a Map by keys?
178. How do you sort a Map by values?

---

# 9. Queue

179. What is Queue?
180. What are common Queue implementations?
181. Difference between Queue and List.
182. Difference between `add()` and `offer()`.
183. Difference between `remove()` and `poll()`.
184. Difference between `element()` and `peek()`.
185. What is PriorityQueue?
186. How does PriorityQueue work internally?
187. What data structure does PriorityQueue use?
188. What is a heap?
189. Difference between min heap and max heap.
190. What is the default ordering of PriorityQueue?
191. How do you create a max heap using PriorityQueue?
192. Can PriorityQueue contain null?
193. Does PriorityQueue maintain sorted order during iteration?
194. What is the complexity of PriorityQueue operations?
195. When should you use PriorityQueue?

---

# 10. Deque

196. What is Deque?
197. Difference between Queue and Deque.
198. What is ArrayDeque?
199. Difference between ArrayDeque and LinkedList.
200. Can ArrayDeque contain null?
201. When should you use ArrayDeque?
202. How can ArrayDeque implement a stack?
203. Why is ArrayDeque preferred over Stack?
204. What is Stack?
205. Why is Stack considered a legacy class?
206. Difference between Stack and Deque.

---

# 11. Comparable & Comparator

207. What is Comparable?
208. What is Comparator?
209. Difference between Comparable and Comparator.
210. When should you use Comparable?
211. When should you use Comparator?
212. Can a class have multiple Comparators?
213. Can you sort without modifying the original class?
214. What is `compareTo()`?
215. What does a negative return value mean?
216. What does zero mean?
217. What does a positive value mean?
218. What happens if Comparator violates its contract?
219. How do you sort objects by multiple fields?
220. How do you sort employees by salary descending?
221. How do you sort by salary and then name?
222. What is `Comparator.comparing()`?
223. What is `thenComparing()`?
224. What is `Comparator.reversed()`?
225. How do you handle null values while sorting?

---

# 12. Collections Utility Class

226. What is the `Collections` class?
227. Difference between Collections and Collection.
228. What does `Collections.sort()` do?
229. What is `Collections.reverse()`?
230. What is `Collections.shuffle()`?
231. What is `Collections.frequency()`?
232. What is `Collections.max()`?
233. What is `Collections.min()`?
234. What is `Collections.binarySearch()`?
235. What is `Collections.unmodifiableList()`?
236. What is `Collections.synchronizedList()`?
237. Difference between synchronized and unmodifiable collections.
238. What are immutable collections?
239. What is `Collections.emptyList()`?
240. What is `Collections.singletonList()`?

---

# 13. Java 8 Collections

241. What changes were introduced in Collections with Java 8?
242. What is `forEach()`?
243. What is `removeIf()`?
244. What is `replaceAll()`?
245. What is `sort()` on List?
246. What is `compute()` in Map?
247. What is `computeIfAbsent()`?
248. What is `computeIfPresent()`?
249. What is `putIfAbsent()`?
250. What is `merge()`?
251. Difference between `putIfAbsent()` and `computeIfAbsent()`.
252. Difference between `computeIfAbsent()` and `computeIfPresent()`.
253. How can `computeIfAbsent()` simplify frequency counting?
254. How can `merge()` be used for counting?
255. How do you iterate over a Map using Java 8?
256. How do you filter a Map using Streams?
257. How do you sort a Map using Streams?
258. How do you convert a List into a Map using `Collectors.toMap()`?
259. What happens if duplicate keys occur in `toMap()`?
260. How do you handle duplicate keys?

---

# 14. Concurrent Collections

261. What is ConcurrentHashMap?
262. How is ConcurrentHashMap different from HashMap?
263. How is ConcurrentHashMap different from Hashtable?
264. How does ConcurrentHashMap work internally in Java 8?
265. What is CAS in ConcurrentHashMap?
266. Does ConcurrentHashMap lock the entire Map?
267. What is bucket-level synchronization?
268. Can multiple threads read ConcurrentHashMap simultaneously?
269. Can multiple threads write ConcurrentHashMap simultaneously?
270. What is weakly consistent iteration?
271. What is CopyOnWriteArrayList?
272. When should you use CopyOnWriteArrayList?
273. Why is CopyOnWriteArrayList expensive for writes?
274. What is CopyOnWriteArraySet?
275. What is BlockingQueue?
276. What is ArrayBlockingQueue?
277. What is LinkedBlockingQueue?
278. Difference between ArrayBlockingQueue and LinkedBlockingQueue.
279. What is PriorityBlockingQueue?
280. What is DelayQueue?
281. What is SynchronousQueue?
282. When should you use BlockingQueue?
283. How would you implement Producer-Consumer using BlockingQueue?

---

# 15. Performance & Complexity

284. What is the time complexity of ArrayList get?
285. What is the time complexity of ArrayList add?
286. What is the time complexity of ArrayList remove?
287. What is the time complexity of LinkedList get?
288. What is the time complexity of HashMap get?
289. What is the time complexity of HashMap put?
290. What is the time complexity of HashSet add?
291. What is the time complexity of TreeMap get?
292. What is the time complexity of TreeSet add?
293. What is the time complexity of PriorityQueue add?
294. Which collection provides O(1) average lookup?
295. Which collection provides sorted data?
296. Which collection preserves insertion order?
297. Which collection is best for frequent random access?
298. Which collection is best for frequent insertions/removals at both ends?
299. Which collection should you use for unique sorted elements?
300. How does collection choice affect application performance?

---

# 16. Advanced Internal Questions

301. Explain HashMap internals in Java 8 step by step.
302. Explain ArrayList internals in Java 8.
303. Explain LinkedList internals.
304. Explain HashSet internals.
305. Explain LinkedHashMap internals.
306. Explain TreeMap internals.
307. Explain TreeSet internals.
308. Explain PriorityQueue internals.
309. Explain ConcurrentHashMap internals.
310. What happens inside HashMap when two keys have the same hash?
311. What happens when two keys have the same hash and are not equal?
312. How does HashMap treeification work?
313. Why does HashMap capacity use powers of two?
314. Why does HashMap use bitwise operations to calculate bucket indexes?
315. What happens during HashMap resize?
316. Why can poor hashCode implementation hurt performance?
317. Why should hashCode be evenly distributed?
318. What happens if every key has the same hashCode?
319. How does Java 8 prevent HashMap from becoming a linked-list performance bottleneck?
320. What is the difference between capacity, size, threshold, and load factor?

---

# 17. Memory & Collections

321. How much memory does an ArrayList consume?
322. Why does LinkedList consume more memory than ArrayList?
323. Why does HashMap consume more memory?
324. What is object overhead in collections?
325. How can excessive collections cause memory problems?
326. How can an unbounded HashMap cause a memory leak?
327. How can caches cause memory leaks?
328. How do you prevent unbounded collection growth?
329. What happens when an ArrayList grows repeatedly?
330. How can you optimize collection memory usage?

---

# 18. Collection Coding Questions

331. Remove duplicates from a List.
332. Find duplicates in a List.
333. Find unique elements in a List.
334. Find the frequency of each element.
335. Find the first non-repeating element.
336. Find the first repeating element.
337. Find common elements between two Lists.
338. Find union of two Lists.
339. Find intersection of two Lists.
340. Find missing numbers from a List.
341. Find the second-highest number.
342. Find the Kth largest element.
343. Find top K frequent elements.
344. Sort a List without using `Collections.sort()`.
345. Sort employees by salary.
346. Sort employees by multiple fields.
347. Group employees by department.
348. Find highest salary per department.
349. Find second-highest salary per department.
350. Convert List to Map.
351. Handle duplicate keys while converting List to Map.
352. Find duplicate objects based on an ID.
353. Merge two Maps.
354. Find common keys between two Maps.
355. Find keys present in one Map but not another.
356. Sort Map by keys.
357. Sort Map by values.
358. Find the most frequent character in a String.
359. Find top K frequent characters.
360. Implement an LRU cache using LinkedHashMap.

---

# 19. Production Scenario Questions

361. Your HashMap contains millions of records and memory usage is very high. What would you investigate?

362. Your application has frequent HashMap lookups but performance is poor. What could be wrong?

363. A HashMap key is mutable and lookup sometimes fails. Explain why.

364. You need a thread-safe cache. Which collection would you choose?

365. You need a cache with LRU eviction. How would you implement it?

366. Multiple threads are reading and writing the same Map. Which implementation would you use?

367. Your application has many reads but very few writes to a List. Which collection would you consider?

368. You need to maintain insertion order while preventing duplicates. Which collection would you choose?

369. You need sorted unique values. Which collection would you choose?

370. You need to process tasks based on priority. Which collection would you choose?

371. Your producer is faster than your consumer. How would you prevent unlimited memory growth?

372. You need to limit the number of tasks waiting in memory. Which collection would you use?

373. You see `ConcurrentModificationException` in production. How would you investigate?

374. A developer uses `ArrayList` for frequent insertions at the beginning. What would you recommend?

375. A developer uses `LinkedList` because "insertion is O(1)." Is that always correct?

376. A developer uses `parallelStream()` on a large collection. What problems could occur?

377. Your application has a `HashMap` shared across multiple threads. What problems can occur?

378. Why might `Collections.synchronizedMap()` not be enough for complex concurrent operations?

379. When would you use ConcurrentHashMap instead of synchronizedMap?

380. Your application needs a thread-safe list with 99% reads and 1% writes. What would you choose?

---

# 🔥 Very Advanced Questions

381. Explain why HashMap capacity is generally a power of two.

382. Explain HashMap's hash spreading function.

383. Explain why `n - 1 & hash` is used for bucket indexing.

384. Explain HashMap treeification and untreeification.

385. Explain the difference between Java 7 and Java 8 HashMap implementation.

386. Explain the Java 7 HashMap infinite-loop problem during concurrent resize.

387. Why doesn't HashMap guarantee thread safety?

388. Explain ConcurrentHashMap's Java 8 implementation.

389. Why doesn't ConcurrentHashMap allow null keys and values?

390. Explain weakly consistent iterators.

391. Explain fail-fast vs weakly consistent iteration.

392. Explain ArrayList resizing internally.

393. Why does ArrayList use an array internally?

394. Explain LinkedHashMap's doubly linked list.

395. Explain how LinkedHashMap can implement an LRU cache.

396. Explain TreeMap's Red-Black Tree operations.

397. Explain how TreeSet uses TreeMap internally.

398. Explain PriorityQueue's heap implementation.

399. Explain why PriorityQueue iteration isn't sorted.

400. Explain the performance trade-offs between ArrayList, LinkedList, HashSet, TreeSet, HashMap, TreeMap, and ConcurrentHashMap.

---

# ⭐ Highest Priority for Your 5-Year Java Interview

I recommend focusing heavily on these:

### Must Know

**ArrayList**
→ Internal structure
→ Resizing
→ Complexity
→ ArrayList vs LinkedList

**HashMap**
→ `put()` / `get()` internals
→ Hashing
→ Collision
→ `equals()` / `hashCode()`
→ Load factor
→ Capacity
→ Resize
→ Java 8 treeification

**HashSet**
→ Internal HashMap
→ Duplicate detection
→ `equals()` / `hashCode()`

**TreeMap / TreeSet**
→ Red-Black Tree
→ Comparable vs Comparator
→ Complexity

**Queue**
→ PriorityQueue
→ ArrayDeque
→ BlockingQueue

**Concurrent Collections**
→ ConcurrentHashMap
→ CopyOnWriteArrayList
→ BlockingQueue

**Java 8**
→ `computeIfAbsent()`
→ `putIfAbsent()`
→ `merge()`
→ `compute()`
→ `toMap()`
→ `groupingBy()`
→ Stream-based collection processing

### 🔥 Most likely scenario questions

For your backend interviews, be ready to explain:

> **"Why would you choose ArrayList over LinkedList?"**

> **"Explain HashMap internally in Java 8."**

> **"How does HashMap handle collisions?"**

> **"Why do we need equals() and hashCode()?"**

> **"What happens if a HashMap key is mutable?"**

> **"HashMap vs ConcurrentHashMap?"**

> **"How would you implement an LRU cache?"**

> **"Why doesn't ConcurrentHashMap allow null?"**

> **"How would you choose a collection for a production use case?"**

> **"What happens when millions of objects are stored in a HashMap?"**

These questions are especially important because at the **5+ year level**, interviewers usually care less about simply naming collections and more about **internal implementation, complexity, thread safety, memory usage, and choosing the right collection for a production scenario**.
