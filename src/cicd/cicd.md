# CI/CD Interview Questions: Basic to Advanced

For a **5+ years Java Backend Developer**, prepare CI/CD in this order:

**CI/CD basics → Git → Build tools → Testing → Jenkins/GitHub Actions → Docker → Deployment → AWS → Kubernetes → Security → Observability → Production scenarios.**

---

# 1. CI/CD Fundamentals

1. What is CI?
2. What is CD?
3. What is the difference between Continuous Integration, Continuous Delivery, and Continuous Deployment?
4. Why do we need CI/CD?
5. What problems does CI/CD solve?
6. What are the benefits of CI/CD?
7. What does a typical CI/CD pipeline look like?
8. What are the common stages of a CI/CD pipeline?
9. What happens when a developer pushes code?
10. What is a build pipeline?
11. What is a deployment pipeline?
12. What is an artifact?
13. What is an artifact repository?
14. What is a build server?
15. What is a CI server?
16. What is a CD server?
17. What is a pipeline?
18. What is a pipeline stage?
19. What is a pipeline job?
20. What is a pipeline step?
21. What is a runner/agent?
22. What is a webhook?
23. What is a build trigger?
24. What is a manual trigger?
25. What is an automated trigger?
26. What is a pipeline failure?
27. What should happen when a pipeline fails?
28. What is a quality gate?
29. What is a deployment gate?
30. What is a rollback?

---

# 2. CI/CD Workflow

31. Explain a complete CI/CD workflow from developer commit to production.
32. What happens after a Git push?
33. How does a CI server detect a new commit?
34. How is source code checked out?
35. How is the application compiled?
36. How are unit tests executed?
37. How are integration tests executed?
38. How is a Docker image created?
39. How is an artifact stored?
40. How is an artifact promoted between environments?
41. How is deployment triggered?
42. How do you verify a deployment?
43. How do you rollback a deployment?
44. What is the difference between build and deployment?
45. Should you build the application separately for every environment?
46. Why should the same artifact be promoted across environments?
47. What is immutable deployment?
48. What is environment parity?
49. What is configuration drift?
50. How do you prevent configuration drift?

---

# 3. Git & CI/CD

51. Why is Git important for CI/CD?
52. What is a Git branch?
53. What is Git merge?
54. What is Git rebase?
55. Difference between merge and rebase.
56. What is a pull request?
57. What is a branch protection rule?
58. What is a required status check?
59. What is a code review?
60. What is trunk-based development?
61. What is GitFlow?
62. Difference between GitFlow and trunk-based development.
63. What branching strategy would you recommend for CI/CD?
64. What is a feature branch?
65. What is a release branch?
66. What is a hotfix branch?
67. What is a Git tag?
68. How do you version releases?
69. What is Semantic Versioning?
70. How can Git tags trigger a deployment?
71. How do you prevent direct pushes to production branches?
72. How do you handle merge conflicts in CI/CD?
73. How do you handle failed pull-request builds?
74. How do you enforce code quality before merging?

---

# 4. Maven & Java CI/CD

75. What is Maven?
76. What is `pom.xml`?
77. What is a Maven lifecycle?
78. Explain Maven phases.
79. Difference between `compile`, `test`, `package`, `verify`, and `install`.
80. What is `mvn clean install`?
81. Difference between `mvn package` and `mvn install`.
82. What is Maven dependency management?
83. What is a Maven plugin?
84. What is Surefire?
85. What is Failsafe?
86. Difference between Surefire and Failsafe.
87. How do you run unit tests in CI?
88. How do you skip tests in Maven?
89. Should you skip tests in a production pipeline?
90. How do you generate code coverage?
91. What is JaCoCo?
92. How do you fail a build if code coverage is below a threshold?
93. How do you manage Maven dependencies securely?
94. How do you cache Maven dependencies in CI?
95. Why is dependency caching useful?

---

# 5. Testing in CI/CD

96. What types of tests should run in CI?
97. Difference between unit, integration, functional, and end-to-end tests.
98. Which tests should run first?
99. Why should unit tests run before integration tests?
100. What is the test pyramid?
101. What is smoke testing?
102. What is regression testing?
103. What is contract testing?
104. What is performance testing?
105. What is security testing?
106. What is static code analysis?
107. What is SonarQube?
108. What is a quality gate in SonarQube?
109. How do you prevent bad code from reaching production?
110. How do you handle flaky tests?
111. Should flaky tests be retried?
112. What are the risks of retrying tests?
113. How do you parallelize tests?
114. How do you reduce pipeline execution time?
115. How do you test database-dependent applications in CI?

---

# 6. Jenkins

116. What is Jenkins?
117. Why is Jenkins used in CI/CD?
118. What is a Jenkins pipeline?
119. What is a Jenkinsfile?
120. Difference between Declarative and Scripted Pipeline.
121. What is a Jenkins agent?
122. What is a Jenkins controller?
123. What is a Jenkins node?
124. What is a Jenkins executor?
125. What is a Jenkins workspace?
126. What is a Jenkins credential?
127. How do you securely store secrets in Jenkins?
128. What is a Jenkins plugin?
129. What is a freestyle project?
130. What is a pipeline job?
131. What is a multibranch pipeline?
132. What is a webhook in Jenkins?
133. How do you trigger Jenkins from GitHub?
134. How do you trigger Jenkins periodically?
135. What is polling SCM?
136. Difference between webhook and polling.
137. How do you archive artifacts in Jenkins?
138. How do you publish test reports?
139. How do you implement approval before production deployment?
140. How do you rollback using Jenkins?
141. How do you run Jenkins builds in Docker?
142. How do you scale Jenkins agents?
143. How do you handle Jenkins agent failures?
144. How do you optimize a slow Jenkins pipeline?
145. How do you restrict production deployment access?

---

# 7. GitHub Actions

146. What is GitHub Actions?
147. What is a workflow?
148. What is a job?
149. What is a step?
150. What is an action?
151. What is a runner?
152. What is `.github/workflows`?
153. What is a workflow YAML file?
154. What triggers a GitHub Actions workflow?
155. What is `push`?
156. What is `pull_request`?
157. What is `workflow_dispatch`?
158. What is a matrix strategy?
159. What is a job dependency?
160. What is `needs`?
161. How do you pass data between jobs?
162. What are GitHub Actions artifacts?
163. What are GitHub Actions caches?
164. Difference between artifact and cache.
165. What are GitHub Secrets?
166. How do you access secrets in a workflow?
167. How do you prevent secrets from being exposed?
168. What is an environment in GitHub Actions?
169. How do environment approvals work?
170. How do you deploy to AWS using GitHub Actions?
171. How do you build and push a Docker image?
172. How do you tag Docker images?
173. How do you deploy only after tests pass?
174. How do you run jobs in parallel?
175. How do you reuse workflows?
176. What are reusable workflows?
177. What are composite actions?
178. How do you debug a failed GitHub Actions workflow?
179. How do you reduce GitHub Actions execution time?
180. How do you implement deployment approvals?

---

# 8. Docker & CI/CD

181. What is Docker?
182. Why is Docker useful in CI/CD?
183. What is a Docker image?
184. What is a Docker container?
185. Difference between Docker image and container.
186. What is a Dockerfile?
187. What is a Docker registry?
188. What is Docker Hub?
189. What is Amazon ECR?
190. What is a Docker layer?
191. How does Docker image caching work?
192. What is a multi-stage Docker build?
193. Why use multi-stage builds?
194. How do you build a Java application Docker image?
195. How do you optimize a Java Docker image?
196. How do you reduce Docker image size?
197. How do you tag Docker images?
198. Why should you avoid using `latest` in production?
199. How do you scan Docker images for vulnerabilities?
200. How do you push an image to a registry?
201. How does CI/CD deploy a Docker image?
202. How do you rollback to an older Docker image?
203. How do you handle Docker secrets?
204. How do you pass environment variables to containers?

---

# 9. AWS & CI/CD

205. How would you implement CI/CD on AWS?
206. What is AWS CodePipeline?
207. What is AWS CodeBuild?
208. What is AWS CodeDeploy?
209. Difference between CodeBuild, CodeDeploy, and CodePipeline.
210. How would you deploy a Spring Boot application to EC2?
211. How would you deploy a Dockerized Spring Boot application to ECS?
212. What is Amazon ECR?
213. How do you authenticate CI/CD with AWS?
214. What is IAM?
215. What is an IAM role?
216. Why should you avoid storing AWS access keys in CI/CD?
217. What is OIDC?
218. How can GitHub Actions authenticate with AWS without long-lived credentials?
219. How do you manage environment-specific configuration?
220. How do you deploy to multiple AWS environments?
221. How do you perform a blue-green deployment on AWS?
222. How do you perform a rolling deployment?
223. How do you rollback an AWS deployment?

---

# 10. Deployment Strategies

224. What is rolling deployment?
225. What is blue-green deployment?
226. What is canary deployment?
227. What is recreate deployment?
228. Difference between rolling, blue-green, and canary deployments.
229. When would you use blue-green deployment?
230. When would you use canary deployment?
231. How do you implement zero-downtime deployment?
232. What is a health check?
233. What is a readiness check?
234. What is a liveness check?
235. How do health checks help deployment?
236. What happens if the new version fails health checks?
237. How do you automatically rollback a failed deployment?
238. How do you perform database migrations during deployment?
239. What is backward-compatible database migration?
240. Why can database migrations make rollback difficult?

---

# 11. Kubernetes & CI/CD

241. Why is Kubernetes used in CI/CD?
242. What is a Kubernetes Deployment?
243. What is a Pod?
244. What is a Service?
245. What is a ConfigMap?
246. What is a Secret?
247. What is a ReplicaSet?
248. How does Kubernetes perform rolling deployment?
249. How do you update a Docker image in Kubernetes?
250. What is `kubectl rollout`?
251. How do you rollback a Kubernetes deployment?
252. What is `readinessProbe`?
253. What is `livenessProbe`?
254. What is `startupProbe`?
255. Difference between readiness and liveness.
256. How does Kubernetes handle failed containers?
257. How do you deploy different configurations to dev/staging/prod?
258. What is Helm?
259. Why use Helm in CI/CD?
260. What is GitOps?
261. What is Argo CD?
262. Difference between traditional CI/CD and GitOps.

---

# 12. Secrets & Security

263. Why should secrets not be stored in Git?
264. How do you manage secrets in CI/CD?
265. What are environment variables?
266. What are GitHub Secrets?
267. What is AWS Secrets Manager?
268. What is HashiCorp Vault?
269. How do you rotate secrets?
270. How do you prevent secrets from appearing in logs?
271. What is dependency vulnerability scanning?
272. What is SAST?
273. What is DAST?
274. Difference between SAST and DAST.
275. What is Software Composition Analysis?
276. How do you scan Docker images?
277. How do you prevent vulnerable dependencies from being deployed?
278. How do you secure CI/CD runners?
279. What is least privilege?
280. Why should production deployment permissions be restricted?

---

# 13. Advanced CI/CD Concepts

281. What is Continuous Delivery vs Continuous Deployment?
282. What is trunk-based development?
283. What is GitOps?
284. What is Infrastructure as Code?
285. What is Terraform?
286. How does Terraform fit into CI/CD?
287. What is configuration management?
288. What is immutable infrastructure?
289. What is artifact promotion?
290. What is build once, deploy many?
291. What is environment promotion?
292. What is deployment orchestration?
293. What is pipeline-as-code?
294. What is policy-as-code?
295. What is infrastructure drift?
296. What is deployment freeze?
297. What is change management?
298. What is feature flagging?
299. How do feature flags help CI/CD?
300. What is progressive delivery?

---

# 14. Pipeline Optimization

301. How do you reduce CI/CD pipeline execution time?
302. How do you cache Maven dependencies?
303. How do you cache Docker layers?
304. How do you run independent jobs in parallel?
305. How do you avoid rebuilding unchanged components?
306. What is incremental build?
307. How do you identify the slowest pipeline stage?
308. How do you optimize Docker builds?
309. How do you parallelize tests?
310. How do you avoid unnecessary deployments?
311. How do you implement path-based pipeline triggers?
312. How do you handle monorepo CI/CD?
313. How do you handle large repositories?
314. How do you prevent unnecessary CI runs?

---

# 15. Monitoring & Observability

315. How do you monitor a deployment?
316. What metrics should you monitor after deployment?
317. What is application health monitoring?
318. What is deployment health?
319. What is MTTR?
320. What is MTBF?
321. What is deployment frequency?
322. What is change failure rate?
323. What are DORA metrics?
324. What is lead time for changes?
325. What is mean time to restore?
326. How can CI/CD improve DORA metrics?
327. How do you detect a failed deployment automatically?
328. How do logs help troubleshoot deployment failures?
329. How do metrics help troubleshoot deployments?
330. How does distributed tracing help after deployment?

---

# 16. Production Scenario Questions

331. **Your pipeline takes 30 minutes. How would you reduce it to 10 minutes?**

332. **Your deployment succeeded, but the application is returning 500 errors. What would you check?**

333. **The Docker build works locally but fails in CI. How would you troubleshoot?**

334. **Tests pass locally but fail in CI. What could be the reasons?**

335. **A deployment partially succeeds and then fails. How would you recover?**

336. **The new version causes high CPU after deployment. What would you do?**

337. **The new version causes database connection exhaustion. How would you rollback safely?**

338. **You need zero-downtime deployment for a Spring Boot microservice. How would you design it?**

339. **You have 20 microservices. How would you design their CI/CD pipelines?**

340. **One microservice changes its API contract. How would you prevent breaking other services?**

341. **How would you implement automated rollback?**

342. **How would you implement blue-green deployment?**

343. **How would you implement canary deployment?**

344. **How would you deploy database changes without breaking the previous application version?**

345. **How would you handle secrets across dev, staging, and production?**

346. **A production deployment needs manual approval. How would you implement it?**

347. **A developer accidentally exposes an AWS secret in Git. What would you do?**

348. **Your CI runner is compromised. What steps would you take?**

349. **How would you prevent unauthorized production deployments?**

350. **How would you design a CI/CD pipeline for a Java Spring Boot microservices application running on AWS?**

---

# 🔥 Advanced Architecture Questions

351. Design CI/CD for **10 Spring Boot microservices**.

352. Design CI/CD for **100 microservices**.

353. Design a pipeline using:

```text
GitHub
   ↓
GitHub Actions
   ↓
Maven
   ↓
Unit Tests
   ↓
SonarQube
   ↓
Docker Build
   ↓
ECR
   ↓
AWS ECS/EKS
```

354. How would you implement automated rollback in this architecture?

355. How would you implement blue-green deployment?

356. How would you implement canary deployment?

357. How would you handle database migrations?

358. How would you handle backward compatibility?

359. How would you implement secrets management?

360. How would you secure the complete CI/CD pipeline?

361. How would you implement approval gates?

362. How would you implement artifact versioning?

363. How would you ensure the same artifact is deployed to staging and production?

364. How would you prevent a developer from modifying production deployment configuration?

365. How would you handle a failed deployment after database migration?

366. How would you design CI/CD for a monorepo?

367. How would you design CI/CD for multiple repositories?

368. How would you implement centralized reusable pipeline templates?

369. How would you handle dependencies between microservices?

370. How would you monitor the health of deployments automatically?

---

# ⭐ Highest Priority for Your Java Backend Interview

Given your **Java + Spring Boot + Microservices + AWS + GitHub Actions** background, I would focus first on these:

### Tier 1: Must Know

**CI/CD**

* CI vs CD
* Continuous Delivery vs Deployment
* Pipeline stages
* Build vs deployment
* Artifacts
* Environment promotion
* Rollback

**Git**

* Branching strategies
* PR
* Merge vs Rebase
* Branch protection
* Tags
* Release versioning

**Maven**

* Lifecycle
* `clean`
* `compile`
* `test`
* `package`
* `verify`
* Dependency management
* JaCoCo

**GitHub Actions / Jenkins**

* Workflow
* Job
* Step
* Runner/Agent
* Secrets
* Artifacts
* Cache
* Environment
* Approval
* Pipeline triggers

### Tier 2: Very Important

**Docker**

* Dockerfile
* Image vs container
* Layers
* Multi-stage build
* Registry
* ECR
* Image tagging
* Image scanning

**Deployment**

* Rolling
* Blue-Green
* Canary
* Zero downtime
* Health checks
* Rollback
* Database migration

### Tier 3: Advanced

* AWS CI/CD
* ECS/EKS
* Kubernetes deployments
* Helm
* GitOps
* Argo CD
* Terraform
* Secrets Manager
* OIDC
* SAST/DAST
* Dependency scanning
* Progressive delivery

### 🔥 Top 15 Scenario Questions

If you have limited time, practice these especially:

1. **Explain your complete CI/CD pipeline from Git commit to production.**
2. **How would you build and deploy a Spring Boot application using GitHub Actions?**
3. **How would you build a Docker image and push it to AWS ECR?**
4. **How would you deploy that image to AWS ECS/EKS?**
5. **How would you implement zero-downtime deployment?**
6. **Rolling vs blue-green vs canary deployment?**
7. **How would you implement automatic rollback?**
8. **How do you handle database migrations?**
9. **How do you manage secrets securely?**
10. **How do you prevent unauthorized production deployments?**
11. **Pipeline is taking 30 minutes. How would you optimize it?**
12. **Tests pass locally but fail in CI. How would you debug it?**
13. **Deployment succeeds but production returns 500. What do you check?**
14. **How would you design CI/CD for multiple Spring Boot microservices?**
15. **How would you secure the complete CI/CD pipeline?**
