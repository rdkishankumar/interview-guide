# Spring Boot Interview Questions: Basic to Advanced

For a **5+ years Java Backend Developer**, prepare Spring Boot in this order:

**Spring fundamentals → Spring Boot basics → Configuration → REST APIs → Spring Data JPA → Transactions → Security → Actuator → Testing → Microservices → Production → Advanced internals.**

---

# 1. Spring Framework Fundamentals

1. What is Spring Framework?
2. Why was Spring introduced?
3. What problems does Spring solve?
4. What are the major modules of Spring?
5. What is IoC?
6. What is Dependency Injection?
7. Difference between IoC and DI.
8. What are the types of Dependency Injection?
9. Constructor injection vs setter injection.
10. Why is constructor injection preferred?
11. What is a Spring Bean?
12. How does Spring create a Bean?
13. What is ApplicationContext?
14. Difference between `BeanFactory` and `ApplicationContext`.
15. What is component scanning?
16. What is `@Component`?
17. What is `@Service`?
18. What is `@Repository`?
19. What is `@Controller`?
20. Difference between `@Component`, `@Service`, `@Repository`, and `@Controller`.
21. What is `@Autowired`?
22. How does `@Autowired` work?
23. What happens when multiple beans of the same type exist?
24. What is `@Qualifier`?
25. What is `@Primary`?
26. Difference between `@Qualifier` and `@Primary`.
27. Can Spring inject a private field?
28. What is Bean lifecycle?
29. What are `@PostConstruct` and `@PreDestroy`?
30. What is Bean scope?

---

# 2. Spring Bean Scopes

31. What is Singleton scope?
32. What is Prototype scope?
33. What is Request scope?
34. What is Session scope?
35. What is Application scope?
36. What is WebSocket scope?
37. Difference between Singleton and Prototype.
38. Is Spring Singleton the same as Singleton Design Pattern?
39. How does Spring manage Singleton Beans?
40. What happens when a Prototype Bean is injected into a Singleton Bean?
41. How can you inject a Prototype Bean into a Singleton Bean?
42. What is `ObjectProvider`?
43. What is scoped proxy?

---

# 3. Spring Boot Fundamentals

44. What is Spring Boot?
45. Why was Spring Boot introduced?
46. Difference between Spring and Spring Boot.
47. What are the advantages of Spring Boot?
48. What is auto-configuration?
49. How does Spring Boot auto-configuration work?
50. What is `@SpringBootApplication`?
51. What annotations are included inside `@SpringBootApplication`?
52. What is `@EnableAutoConfiguration`?
53. What is `@ComponentScan`?
54. What is `@Configuration`?
55. What is Spring Boot Starter?
56. What are starter dependencies?
57. What is `spring-boot-starter-web`?
58. What is `spring-boot-starter-data-jpa`?
59. What is `spring-boot-starter-security`?
60. Why are starters useful?
61. What is an embedded server?
62. Which embedded servers are supported?
63. Why does Spring Boot use embedded Tomcat?
64. How do you change the embedded server?
65. How do you change the server port?
66. How do you run a Spring Boot application?
67. What happens when a Spring Boot application starts?
68. What is `SpringApplication.run()`?
69. What is the Spring Boot startup lifecycle?
70. What is the difference between executable JAR and WAR?

---

# 4. Configuration

71. What is `application.properties`?
72. What is `application.yml`?
73. Difference between properties and YAML.
74. How do you define custom properties?
75. How do you read properties using `@Value`?
76. What is `@ConfigurationProperties`?
77. Difference between `@Value` and `@ConfigurationProperties`.
78. Why is `@ConfigurationProperties` preferred for grouped configuration?
79. What are Spring Profiles?
80. What is `application-dev.yml`?
81. What is `application-prod.yml`?
82. How do you activate a profile?
83. How do you manage environment-specific configuration?
84. How do you override configuration values?
85. What is externalized configuration?
86. What is property precedence in Spring Boot?
87. How do environment variables override application properties?
88. How do you securely manage passwords and secrets?
89. How do you use AWS Secrets Manager with Spring Boot?
90. How do you prevent secrets from being committed to Git?

---

# 5. REST API

91. How do you create a REST API using Spring Boot?
92. What is `@RestController`?
93. Difference between `@Controller` and `@RestController`.
94. What is `@RequestMapping`?
95. Difference between `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping`.
96. What is `@PathVariable`?
97. What is `@RequestParam`?
98. What is `@RequestBody`?
99. What is `@ResponseBody`?
100. What is `@ResponseStatus`?
101. How do you return HTTP status codes?
102. What is `ResponseEntity`?
103. Difference between ResponseEntity and returning an object directly.
104. How do you handle request validation?
105. What is `@Valid`?
106. What is `@Validated`?
107. What is `@NotNull`?
108. What is `@NotBlank`?
109. What is `@Size`?
110. How do you create custom validation?
111. How do you handle exceptions globally?
112. What is `@ControllerAdvice`?
113. What is `@RestControllerAdvice`?
114. What is `@ExceptionHandler`?
115. How do you design a standard error response?
116. How do you handle validation errors?
117. How do you implement pagination?
118. How do you implement sorting?
119. How do you implement filtering?
120. How do you version REST APIs?

---

# 6. Spring Data JPA

121. What is Spring Data JPA?
122. Difference between JPA and Hibernate.
123. What is an Entity?
124. What is `@Entity`?
125. What is `@Id`?
126. What is `@GeneratedValue`?
127. What is `JpaRepository`?
128. Difference between `CrudRepository`, `PagingAndSortingRepository`, and `JpaRepository`.
129. How does Spring Data generate repository implementations?
130. What are derived query methods?
131. Example of `findByEmail()`.
132. What is `@Query`?
133. JPQL vs native SQL.
134. What is `@Modifying`?
135. What is `@Transactional`?
136. What is entity mapping?
137. What is `@OneToOne`?
138. What is `@OneToMany`?
139. What is `@ManyToOne`?
140. What is `@ManyToMany`?
141. What is `mappedBy`?
142. What is `cascade`?
143. What is `orphanRemoval`?
144. What is lazy loading?
145. What is eager loading?
146. Difference between Lazy and Eager fetching.
147. What is the N+1 query problem?
148. How do you solve N+1?
149. What is `JOIN FETCH`?
150. What is EntityGraph?
151. What is Hibernate first-level cache?
152. What is second-level cache?
153. What is dirty checking?
154. What is the persistence context?
155. What are managed, detached, transient, and removed entities?

---

# 7. Transactions

156. What is a transaction?
157. What is `@Transactional`?
158. How does `@Transactional` work?
159. What is transaction propagation?
160. Explain `REQUIRED`.
161. Explain `REQUIRES_NEW`.
162. Explain `NESTED`.
163. Explain `SUPPORTS`.
164. Explain `MANDATORY`.
165. Explain `NOT_SUPPORTED`.
166. Explain `NEVER`.
167. What is transaction isolation?
168. Explain READ_UNCOMMITTED.
169. Explain READ_COMMITTED.
170. Explain REPEATABLE_READ.
171. Explain SERIALIZABLE.
172. What is transaction rollback?
173. Which exceptions trigger rollback by default?
174. Why doesn't `@Transactional` always roll back checked exceptions?
175. How do you configure rollback for checked exceptions?
176. What is `readOnly = true`?
177. Can `@Transactional` be applied to private methods?
178. Can `@Transactional` work on self-invocation?
179. What is the Spring transaction proxy?
180. What happens if a transactional method calls another transactional method?
181. What happens when an exception is caught inside a transactional method?
182. How do you handle transactions across multiple services?
183. Why is distributed transaction difficult in microservices?
184. What is Saga?
185. What is Transactional Outbox?

---

# 8. Spring AOP

186. What is AOP?
187. Why do we need AOP?
188. What is a cross-cutting concern?
189. What is an Aspect?
190. What is a Join Point?
191. What is a Pointcut?
192. What is Advice?
193. What is `@Before`?
194. What is `@After`?
195. What is `@AfterReturning`?
196. What is `@AfterThrowing`?
197. What is `@Around`?
198. Difference between Join Point and Pointcut.
199. How does Spring AOP work internally?
200. What is a proxy?
201. JDK dynamic proxy vs CGLIB proxy.
202. Why can self-invocation cause AOP problems?
203. How does `@Transactional` use AOP?
204. How would you implement logging using AOP?
205. How would you implement execution-time monitoring using AOP?

---

# 9. Spring Security

206. What is Spring Security?
207. How does Spring Security work?
208. What is authentication?
209. What is authorization?
210. Difference between authentication and authorization.
211. What is `SecurityFilterChain`?
212. What is a security filter?
213. What is `UserDetails`?
214. What is `UserDetailsService`?
215. What is `PasswordEncoder`?
216. Why should passwords be hashed?
217. What is BCrypt?
218. What is JWT?
219. How does JWT authentication work?
220. Where should JWT be validated?
221. What is an access token?
222. What is a refresh token?
223. How do you implement JWT authentication in Spring Boot?
224. What is OAuth 2.0?
225. Difference between OAuth2 and JWT.
226. What is role-based authorization?
227. What is method-level security?
228. What is `@PreAuthorize`?
229. What is CORS?
230. What is CSRF?
231. Should CSRF be disabled for REST APIs?
232. How do you secure internal microservice APIs?
233. How do you handle token expiration?
234. How do you revoke JWT tokens?
235. How do you implement refresh token rotation?

---

# 10. Spring Boot Actuator

236. What is Spring Boot Actuator?
237. Why is Actuator used?
238. What are Actuator endpoints?
239. What is `/health`?
240. What is `/info`?
241. What is `/metrics`?
242. What is `/env`?
243. What is `/beans`?
244. What is `/mappings`?
245. What is `/loggers`?
246. How do you expose Actuator endpoints?
247. How do you secure Actuator endpoints?
248. What is liveness?
249. What is readiness?
250. How can Actuator help Kubernetes health checks?
251. How do you create a custom health indicator?
252. How do you expose custom metrics?

---

# 11. Spring Boot Testing

253. How do you test Spring Boot applications?
254. What is `@SpringBootTest`?
255. What is `@WebMvcTest`?
256. What is `@DataJpaTest`?
257. What is `@MockBean`?
258. Difference between `@Mock` and `@MockBean`.
259. What is Mockito?
260. How do you unit test a Service?
261. How do you test a Controller?
262. How do you test a Repository?
263. How do you test REST APIs?
264. What is MockMvc?
265. What is TestRestTemplate?
266. Difference between MockMvc and TestRestTemplate.
267. What is integration testing?
268. Unit test vs integration test.
269. How do you test database operations?
270. How do you test Kafka consumers/producers?
271. How do you test external API integrations?
272. What is Testcontainers?
273. Why use Testcontainers?
274. How do you test with a real MySQL/PostgreSQL container?
275. How do you mock external services?

---

# 12. Spring Boot Microservices

276. What is a microservice?
277. Why use Spring Boot for microservices?
278. How do Spring Boot services communicate?
279. REST vs messaging between services.
280. What is service discovery?
281. What is client-side load balancing?
282. What is API Gateway?
283. What is Circuit Breaker?
284. What is Resilience4j?
285. What is retry?
286. What is timeout?
287. What is rate limiting?
288. What is bulkhead pattern?
289. What is fallback?
290. How do you prevent cascading failures?
291. How do you handle distributed transactions?
292. What is Saga pattern?
293. What is Event-Driven Architecture?
294. How does Kafka integrate with Spring Boot?
295. How do you handle Kafka consumer failures?
296. How do you implement retry and DLQ?
297. How do you handle duplicate Kafka messages?
298. What is idempotency?
299. How do you implement idempotent REST APIs?
300. What is Transactional Outbox Pattern?

---

# 13. Spring Boot Performance

301. How do you improve Spring Boot application performance?
302. How do you identify slow APIs?
303. How do you identify slow database queries?
304. How do you reduce database calls?
305. How do you solve N+1 queries?
306. How do you implement caching?
307. What is Spring Cache?
308. What is `@Cacheable`?
309. What is `@CachePut`?
310. What is `@CacheEvict`?
311. What is cache-aside?
312. How do you integrate Redis with Spring Boot?
313. How do you configure connection pools?
314. What is HikariCP?
315. How do you tune HikariCP?
316. What happens when the database connection pool is exhausted?
317. How do you configure HTTP connection pools?
318. How do you handle high traffic?
319. How do you make a Spring Boot service horizontally scalable?
320. How do you investigate high CPU?
321. How do you investigate high memory?
322. How do you investigate frequent GC?
323. How do you investigate thread-pool exhaustion?

---

# 14. Spring Boot Advanced Internals

324. Explain Spring Boot startup flow.
325. What happens internally inside `SpringApplication.run()`?
326. What is `SpringApplication`?
327. What is an ApplicationContext?
328. How does component scanning work internally?
329. How does dependency injection happen internally?
330. How does Spring resolve dependencies?
331. How does auto-configuration work internally?
332. What is `spring.factories`?
333. What is `AutoConfiguration.imports`?
334. What is conditional auto-configuration?
335. What is `@ConditionalOnClass`?
336. What is `@ConditionalOnMissingBean`?
337. What is `@ConditionalOnProperty`?
338. What is `@ConditionalOnBean`?
339. How do you create custom auto-configuration?
340. What is a BeanPostProcessor?
341. What is BeanFactoryPostProcessor?
342. Difference between BeanPostProcessor and BeanFactoryPostProcessor.
343. What is `BeanDefinition`?
344. How does Spring register beans?
345. What is the Bean lifecycle in detail?
346. What is circular dependency?
347. How does Spring handle circular dependencies?
348. Why can constructor-based circular dependencies fail?
349. What is lazy initialization?
350. What is `@Lazy`?

---

# 15. Advanced Spring Boot Production Questions

351. How would you design a production-ready Spring Boot service?
352. How would you handle centralized configuration?
353. How would you implement service-to-service authentication?
354. How would you secure internal APIs?
355. How would you implement centralized logging?
356. How would you implement distributed tracing?
357. How would you implement metrics?
358. How would you monitor Spring Boot applications?
359. How would you implement health checks?
360. How would you implement graceful shutdown?
361. What happens during Spring Boot graceful shutdown?
362. How do you prevent requests from being lost during deployment?
363. How do you handle database connection exhaustion?
364. How do you handle thread-pool exhaustion?
365. How do you handle Kafka consumer lag?
366. How do you handle slow downstream services?
367. How do you implement retries without causing retry storms?
368. How do you prevent cascading failures?
369. How do you handle partial failures?
370. How do you implement idempotency?
371. How do you implement distributed locking?
372. How do you handle duplicate requests?
373. How do you handle duplicate Kafka events?
374. How do you handle schema changes between microservices?
375. How do you implement backward-compatible APIs?

---

# 16. Scenario-Based Questions

376. **Your Spring Boot application starts successfully but the endpoint returns 404. How would you troubleshoot?**

377. **Spring cannot find a Bean and throws `NoSuchBeanDefinitionException`. What would you check?**

378. **Spring finds multiple beans of the same type. How would you fix it?**

379. **Your application has a circular dependency. How would you identify and fix it?**

380. **`@Transactional` is not working. What could be the reasons?**

381. **A transaction commits even though you expected rollback. What would you investigate?**

382. **A database query is slow in production. How would you troubleshoot it?**

383. **Your service is making hundreds of SQL queries for one API request. What could be happening?**

384. **Your application is throwing `LazyInitializationException`. Why?**

385. **Your API suddenly starts returning 500 errors. How would you investigate?**

386. **Your service has high CPU usage. What steps would you take?**

387. **Your service has high memory usage. How would you investigate?**

388. **Your application has frequent Full GC. What would you check?**

389. **Your database connection pool is exhausted. What could cause it?**

390. **Your REST API depends on three downstream services. One is slow. How would you prevent the entire API from becoming slow?**

391. **A downstream service is unavailable. How would you design your service to remain available?**

392. **Your Kafka consumer is processing messages slowly. How would you troubleshoot consumer lag?**

393. **The same Kafka event is processed twice. How would you make the consumer idempotent?**

394. **Two microservices update their databases as part of one business transaction. How would you maintain consistency?**

395. **Your service deployment causes dropped requests. How would you implement graceful shutdown and zero-downtime deployment?**

---

# 🔥 Coding/Design Questions

396. Create a CRUD REST API using Spring Boot.

397. Create global exception handling using `@RestControllerAdvice`.

398. Implement request validation using `@Valid`.

399. Implement pagination and sorting.

400. Implement custom Spring Boot configuration using `@ConfigurationProperties`.

401. Implement JWT authentication.

402. Implement role-based authorization.

403. Implement a custom authentication filter.

404. Implement a custom `HandlerInterceptor`.

405. Implement an AOP-based execution-time logger.

406. Implement Spring Cache with Redis.

407. Implement retry and fallback using Resilience4j.

408. Implement a circuit breaker for an external API.

409. Implement an idempotency mechanism for a payment API.

410. Implement a Transactional Outbox Pattern.

411. Implement Kafka producer/consumer using Spring Kafka.

412. Implement Kafka retry and DLQ.

413. Implement a custom Actuator health check.

414. Implement graceful shutdown.

415. Design a production-ready Spring Boot microservice.

---

# ⭐ Highest Priority for Your 5-Year Interview

Since your background is **Java + Spring Boot + Microservices + Kafka + MySQL/MongoDB + AWS**, I'd prioritize these topics:

### Tier 1: Must Know

**Spring Core**

* IoC / DI
* Bean lifecycle
* Bean scopes
* `@Component` / `@Service` / `@Repository`
* `@Autowired`
* `@Qualifier` / `@Primary`
* Constructor injection

**Spring Boot**

* `@SpringBootApplication`
* Auto-configuration
* Starters
* Profiles
* Configuration
* `@Value` vs `@ConfigurationProperties`

**REST**

* `@RestController`
* Request mapping
* Validation
* Exception handling
* `ResponseEntity`
* Pagination
* API versioning

**JPA**

* Entity lifecycle
* Lazy vs Eager
* N+1
* `JOIN FETCH`
* EntityGraph
* Dirty checking
* Persistence context

### Tier 2: Very Important

**Transactions**

* `@Transactional`
* Propagation
* Isolation
* Rollback
* Proxy/self-invocation
* Distributed transactions

**Security**

* JWT
* OAuth2
* Authentication vs Authorization
* Security filters
* CORS/CSRF
* Method-level security

**Microservices**

* REST vs Kafka
* Circuit breaker
* Retry
* Timeout
* Rate limiting
* Idempotency
* Saga
* Transactional Outbox

### Tier 3: Advanced

* Spring Boot startup internals
* Auto-configuration internals
* BeanPostProcessor
* AOP proxies
* Circular dependencies
* Actuator
* Graceful shutdown
* Connection pool tuning
* Thread-pool tuning
* Distributed tracing
* Production troubleshooting

### 🔥 Top 20 Questions to Master

1. **Explain Spring IoC and Dependency Injection.**
2. **Explain the complete Spring Bean lifecycle.**
3. **How does `@SpringBootApplication` work internally?**
4. **How does Spring Boot auto-configuration work?**
5. **`@Component` vs `@Service` vs `@Repository`?**
6. **Why is constructor injection preferred?**
7. **How does `@Transactional` work internally?**
8. **Why doesn't `@Transactional` work during self-invocation?**
9. **Explain transaction propagation and isolation.**
10. **Explain JPA persistence context and dirty checking.**
11. **What is the N+1 problem and how do you solve it?**
12. **Lazy vs Eager loading?**
13. **How would you secure a Spring Boot REST API using JWT?**
14. **How would you handle exceptions globally?**
15. **How would you handle a slow downstream microservice?**
16. **How would you implement retry, timeout, and circuit breaker?**
17. **How would you make a REST API idempotent?**
18. **How would you maintain consistency across microservices?**
19. **Explain Transactional Outbox with Kafka.**
20. **Your Spring Boot service is slow in production. How would you troubleshoot it from API → thread pool → DB → Kafka → JVM/GC?**
