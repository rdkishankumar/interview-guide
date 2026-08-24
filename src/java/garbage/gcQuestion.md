For a **5+ years Java backend interview**, prepare Garbage Collection in this order: **JVM basics → GC fundamentals → collectors → tuning → troubleshooting → production scenarios**.

# Java Garbage Collector Interview Questions: Basic to Advanced

## 1. JVM & Memory Basics

1. What is Garbage Collection in Java?
2. Why does Java need Garbage Collection?
3. How does Garbage Collection work at a high level?
4. What is the JVM heap?
5. What is the difference between Heap and Stack memory?
6. What is stored in Heap memory?
7. What is stored in Stack memory?
8. What is Metaspace?
9. What was PermGen?
10. Why was PermGen replaced by Metaspace?
11. What is native memory?
12. What is the difference between JVM memory and OS memory?
13. What is an object in Java memory?
14. How is an object allocated on the heap?
15. What happens when you create an object using `new`?
16. What happens when an object becomes unreachable?
17. What makes an object eligible for GC?
18. Can an object become eligible for GC while the application is running?
19. Can you force Garbage Collection?
20. What does `System.gc()` actually do?
21. Does `System.gc()` guarantee Garbage Collection?
22. What is `-XX:+DisableExplicitGC`?
23. What is a memory leak in Java?
24. Can Java have memory leaks even with Garbage Collection?
25. What is an `OutOfMemoryError`?

---

# 2. Garbage Collection Fundamentals

26. What is Mark and Sweep?
27. What is Mark and Compact?
28. What is Copying Garbage Collection?
29. What is Stop-the-World?
30. Why does Stop-the-World happen?
31. What is a GC pause?
32. What is GC throughput?
33. What is GC latency?
34. What is GC overhead?
35. What is the difference between throughput and latency?
36. What is a Minor GC?
37. What is a Major GC?
38. What is a Full GC?
39. Are Minor GC and Young GC the same thing?
40. What happens during a Young GC?
41. What happens during a Full GC?
42. Why are Young GCs usually faster?
43. Why are Full GCs expensive?
44. What is object reachability?
45. What are GC roots?
46. What objects are considered GC roots?
47. How does GC determine whether an object is alive?
48. What is the difference between reachable and unreachable objects?
49. What is object promotion?
50. Why are objects promoted to Old Generation?

---

# 3. Generational Garbage Collection

51. What is Generational Garbage Collection?
52. Why is Java heap divided into generations?
53. What is Young Generation?
54. What is Old Generation?
55. What is Eden Space?
56. What are Survivor Spaces?
57. Why are there two Survivor Spaces?
58. What happens when an object is created?
59. What happens when Eden becomes full?
60. Explain the lifecycle of an object through generations.
61. What is object aging?
62. What is the age of an object in GC?
63. What is promotion?
64. What is premature promotion?
65. What causes premature promotion?
66. What is `MaxTenuringThreshold`?
67. How does object age affect promotion?
68. What happens if an object survives multiple Young GCs?
69. What happens when Old Generation becomes full?
70. What is promotion failure?
71. What is allocation failure?
72. What is survivor overflow?
73. How can excessive object creation affect GC?

---

# 4. Java GC Collectors

74. What Garbage Collectors are available in modern Java?
75. What is Serial GC?
76. What is Parallel GC?
77. What is CMS?
78. Why was CMS deprecated?
79. Why was CMS removed from newer Java versions?
80. What is G1 Garbage Collector?
81. Why was G1 introduced?
82. What is ZGC?
83. What is Shenandoah GC?
84. What is Epsilon GC?
85. Difference between Serial GC and Parallel GC.
86. Difference between Parallel GC and G1.
87. Difference between CMS and G1.
88. Difference between G1 and ZGC.
89. Difference between ZGC and Shenandoah.
90. Which collector is suitable for a low-latency application?
91. Which collector is suitable for high-throughput applications?
92. When would you use Serial GC?
93. When would you use Parallel GC?
94. When would you use G1?
95. When would you consider ZGC?
96. What is the default GC in modern Java versions?
97. How do you check which GC your JVM is using?
98. How do you enable a specific GC?

---

# 5. G1 Garbage Collector

99. What is G1 GC?
100. Why is G1 called a Garbage-First collector?
101. How does G1 divide the heap?
102. What is a G1 region?
103. Are G1 regions equivalent to Eden, Survivor, and Old Generation?
104. How does G1 manage Young and Old objects?
105. What is a G1 Young GC?
106. What is a G1 Mixed GC?
107. What is a Mixed Collection?
108. What is concurrent marking in G1?
109. What are G1 GC phases?
110. What is the G1 Initial Mark phase?
111. What is Root Region Scanning?
112. What is Concurrent Mark?
113. What is Remark?
114. What is Cleanup?
115. What is evacuation in G1?
116. What is evacuation failure?
117. What are humongous objects in G1?
118. What is a G1 humongous allocation?
119. How does G1 handle humongous objects?
120. Why can humongous objects cause GC problems?
121. What is `-XX:MaxGCPauseMillis`?
122. Does `MaxGCPauseMillis` guarantee the specified pause time?
123. How does G1 try to meet pause-time goals?
124. What is `InitiatingHeapOccupancyPercent`?
125. What is `G1ReservePercent`?
126. What is `G1HeapRegionSize`?
127. How would you tune G1 for a latency-sensitive application?

---

# 6. ZGC

128. What is ZGC?
129. Why was ZGC introduced?
130. What problem does ZGC solve?
131. What is the main advantage of ZGC?
132. What does "low pause time" mean in ZGC?
133. How does ZGC perform concurrent GC?
134. What is colored pointer/reference information in ZGC?
135. What are load barriers?
136. How does ZGC handle object relocation?
137. How does ZGC differ from G1?
138. When would you choose ZGC instead of G1?
139. Is ZGC always faster than G1?
140. What kind of application benefits from ZGC?
141. What are the trade-offs of ZGC?

---

# 7. GC Algorithms & Internals

142. Explain Mark-Sweep.
143. Explain Mark-Compact.
144. Explain Copying GC.
145. Explain Generational GC.
146. What is fragmentation?
147. How does Mark-Compact solve fragmentation?
148. Why does Copying GC require additional memory?
149. What is compaction?
150. What is evacuation?
151. What is concurrent marking?
152. What is concurrent sweeping?
153. What is concurrent compaction?
154. What is a write barrier?
155. What is a read barrier?
156. What is a card table?
157. What is remembered set?
158. Why does GC need a remembered set?
159. What is cross-generational reference?
160. What is the purpose of a write barrier?
161. What is TLAB?
162. What is Thread Local Allocation Buffer?
163. Why does JVM use TLAB?
164. How does TLAB improve object allocation?
165. What happens when a TLAB is full?

---

# 8. GC Roots & Object Reachability

166. What are GC roots?
167. What are the different types of GC roots?
168. Are local variables GC roots?
169. Are static variables GC roots?
170. Are active threads GC roots?
171. Can a static collection cause a memory leak?
172. What is strong reference?
173. What is SoftReference?
174. What is WeakReference?
175. What is PhantomReference?
176. Difference between strong, soft, weak, and phantom references.
177. When would you use WeakReference?
178. How can WeakHashMap help with memory management?
179. What happens when an object is strongly reachable?
180. What happens when an object is weakly reachable?

---

# 9. Memory Leaks & GC Problems

181. Can Java applications have memory leaks?
182. What are common causes of memory leaks?
183. How can static collections cause memory leaks?
184. How can ThreadLocal cause memory leaks?
185. How can listeners/callbacks cause memory leaks?
186. How can caches cause memory leaks?
187. How can unbounded collections cause memory problems?
188. How can classloaders cause memory leaks?
189. What is a classloader leak?
190. How can database connections contribute to memory problems?
191. How can large object creation cause GC pressure?
192. What is object churn?
193. What is excessive allocation rate?
194. How does object churn affect GC?
195. How do you identify a memory leak?
196. How do you distinguish a memory leak from high allocation rate?
197. What is retained memory?
198. What is shallow heap?
199. What is retained heap?
200. What is a dominator tree?

---

# 10. GC Monitoring & Troubleshooting

201. How do you monitor Garbage Collection in production?
202. What GC metrics do you monitor?
203. What is GC frequency?
204. What is GC pause duration?
205. What is allocation rate?
206. What is promotion rate?
207. What is heap utilization?
208. What is Old Generation utilization?
209. What is GC throughput?
210. How do you identify excessive GC?
211. How do you identify long GC pauses?
212. How do you identify memory leaks?
213. What tools do you use for GC analysis?
214. What is JVisualVM?
215. What is JConsole?
216. What is Java Flight Recorder?
217. What is Java Mission Control?
218. What is `jstat`?
219. What is `jmap`?
220. What is `jcmd`?
221. How do you capture a heap dump?
222. How do you analyze a heap dump?
223. How do you generate a thread dump?
224. How can GC logs help troubleshooting?
225. How do you enable GC logging?
226. What information should you look for in GC logs?

---

# 11. GC Tuning

227. What is GC tuning?
228. When should you tune GC?
229. Should you tune GC before measuring the problem?
230. What is `-Xms`?
231. What is `-Xmx`?
232. Difference between `-Xms` and `-Xmx`.
233. What happens if `Xms` and `Xmx` are different?
234. Why might you set `Xms` and `Xmx` to the same value?
235. What is `-Xmn`?
236. Is manually configuring Young Generation always recommended?
237. What is `MaxGCPauseMillis`?
238. What is `GCTimeRatio`?
239. What is `MaxTenuringThreshold`?
240. What is `SurvivorRatio`?
241. What is `NewRatio`?
242. What is `InitiatingHeapOccupancyPercent`?
243. What is `ParallelGCThreads`?
244. What is `ConcGCThreads`?
245. How do you choose heap size?
246. Is giving the JVM more heap always better?
247. What happens if the heap is too small?
248. What happens if the heap is too large?
249. How does CPU count affect GC?
250. How does application allocation rate affect GC tuning?

---

# 12. Production Scenario Questions

251. **Your application is experiencing frequent Young GCs. How would you investigate?**

252. **Your application is experiencing frequent Full GCs. What could be the reasons?**

253. **GC pauses suddenly increased from 100 ms to 5 seconds. How would you troubleshoot?**

254. **Heap usage continuously increases even after Full GC. What does this indicate?**

255. **Heap usage increases and then suddenly drops after GC. Is that necessarily a memory leak?**

256. **CPU usage is 100% and GC activity is very high. How would you investigate?**

257. **Your service is getting `OutOfMemoryError: Java heap space`. What steps would you take?**

258. **Your application gets `OutOfMemoryError: Metaspace`. What could be the cause?**

259. **Your application gets `OutOfMemoryError: GC overhead limit exceeded`. What does it mean?**

260. **The application is slow only during Full GC. How would you fix it?**

261. **You have a 16 GB heap and G1 is causing long pauses. What would you check?**

262. **An application creates millions of short-lived objects. Which GC behavior would you expect?**

263. **An application creates many large objects. What GC problems could occur?**

264. **A large number of objects are getting promoted to Old Generation. Why?**

265. **Survivor spaces are filling quickly. What would you investigate?**

266. **Old Generation is continuously increasing. What could be wrong?**

267. **GC is running frequently even though the application isn't processing much traffic. What would you investigate?**

268. **After increasing heap size, GC pauses became worse. Why?**

269. **You have a latency-sensitive REST API. Which GC would you consider and why?**

270. **You have a batch-processing application where throughput is more important than latency. Which GC would you consider?**

271. **Your application uses G1 but has many humongous allocations. How would you investigate?**

272. **Your application uses `parallelStream()` and CPU usage increases dramatically. Could GC be involved?**

273. **A cache is causing heap usage to continuously grow. How would you diagnose and fix it?**

274. **A production service crashes after several hours with OutOfMemoryError. What is your investigation process?**

275. **How would you prove that a production issue is caused by GC rather than application code?**

---

# 🔥 Expert-Level Questions

276. Explain the complete lifecycle of an object from allocation to reclamation.

277. Explain how G1 decides which regions to collect.

278. Explain G1's concurrent marking cycle in detail.

279. Explain the difference between Young GC, Mixed GC, and Full GC in G1.

280. Explain how remembered sets work in G1.

281. Explain how write barriers help generational GC.

282. Explain TLAB allocation and how it reduces allocation contention.

283. Explain why object allocation can be extremely fast in Java.

284. Explain how escape analysis can affect object allocation.

285. What is scalar replacement?

286. How can JIT optimization affect GC behavior?

287. How does JVM determine the live set?

288. What happens when the JVM cannot evacuate an object?

289. What is promotion failure?

290. What is evacuation failure?

291. What is floating garbage?

292. What is fragmentation and how do different collectors handle it?

293. How does concurrent GC differ from Stop-the-World GC?

294. Why can't all GC work be performed concurrently?

295. How do GC threads interact with application threads?

296. How does GC affect application throughput?

297. How does allocation rate influence GC frequency?

298. How would you tune GC for a high-throughput Kafka consumer?

299. How would you tune GC for a low-latency Spring Boot REST service?

300. How would you investigate a JVM that shows **high CPU + high GC + low throughput**?

---

## ⭐ Most Important for Your Interview

Given your **Java + Spring Boot + Microservices** background, I would prioritize these first:

**Must know:**

`Heap → Stack → Young/Old Generation → Eden → Survivor → Minor GC → Full GC → Stop-the-World → GC Roots → Mark/Sweep/Compact → G1 → GC logs → Heap Dump → Memory Leak → Xms/Xmx`

**Then go deep into:**

`G1 → Mixed GC → Concurrent Marking → Remembered Set → TLAB → Write Barrier → Promotion → Humongous Objects`

**Production scenarios:**

`High GC → Long pauses → Full GC → OOM → Memory leak → High allocation rate → CPU spike → GC tuning → Heap dump analysis`

These are the areas most likely to distinguish a **5-year Java developer** from someone who has only studied GC definitions.
