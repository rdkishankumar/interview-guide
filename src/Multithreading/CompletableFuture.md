Here are 1-to-2-minute, spoken-style interview responses for each question, packed with production-level architectural context and JVM mechanics.

---

### 1. What is `CompletableFuture`?

"`CompletableFuture` is a powerful concurrency class introduced in Java 8 that implements both the `Future` and `CompletionStage` interfaces.

It fundamentally shifts Java from **blocking synchronization to reactive, event-driven pipelines**. Instead of submitting a task and blocking a thread waiting for the result, `CompletableFuture` allows you to define a non-blocking pipeline of callbacks—'when this data arrives, apply this transformation, then send it to this other service.' It provides over 50 methods for composing, combining, and handling errors in asynchronous asynchronous workflows."

---

### 2. Difference between `Future` and `CompletableFuture`.

| Feature | `Future` (Java 5) | `CompletableFuture` (Java 8) |
| --- | --- | --- |
| **Result Retrieval** | **Blocking:** You must call `.get()`, which halts the calling thread until the task finishes. | **Non-blocking / Reactive:** You attach callbacks (like `thenApply`) that execute automatically upon completion. |
| **Chaining & Composition** | Cannot be chained. You cannot say 'when Future A is done, start Future B'. | Fully chainable using a fluent API (`thenCompose`, `thenCombine`). |
| **Manual Completion** | Cannot manually force a result into a `Future`. | Can be explicitly completed using `.complete(value)` or `.completeExceptionally()`. |
| **Exception Handling** | Exceptions are thrown wrapped in `ExecutionException` only at the `.get()` boundary. | Built-in functional error handling via `exceptionally()` and `handle()`. |

---

### 3. What is `supplyAsync()`?

"`supplyAsync()` is a static factory method used to kick off a new asynchronous computation that **returns a result**.

It takes a `Supplier<T>` functional interface. For example, `CompletableFuture.supplyAsync(() -> fetchUserData())` dispatches the fetch operation to a background worker thread and immediately returns a `CompletableFuture<User>` to the main thread. It acts as the origin point of an asynchronous pipeline."

---

### 4. What is `runAsync()`?

"`runAsync()` is a static factory method used to kick off an asynchronous computation that **does not return a result**.

It takes a `Runnable`. For example, `CompletableFuture.runAsync(() -> sendAnalyticsEvent())` executes the side-effect on a background thread and returns a `CompletableFuture<Void>`. You use this when you only care about knowing when the task is finished, not extracting data from it."

---

### 5. Difference between `thenApply()` and `thenCompose()`.

"This is the exact same concept as `map()` vs `flatMap()` in Java Streams:

* **`thenApply()` (like `map`):** Used for **synchronous transformations**. You pass a function that takes the previous result and returns a standard value (e.g., `User` $\rightarrow$ `String`).
* **`thenCompose()` (like `flatMap`):** Used for **asynchronous chaining**. If your transformation function *itself* returns a `CompletableFuture` (e.g., taking a `User` and calling an async `fetchOrderHistory(userId)`), using `thenApply` would result in a nested `CompletableFuture<CompletableFuture<Order>>`. `thenCompose` flattens it, returning a single, clean `CompletableFuture<Order>`."

---

### 6. Difference between `thenApply()` and `thenAccept()`.

* **`thenApply(Function<T, R>)`:** Takes the output of the previous stage, modifies or transforms it, and **passes a new result forward** down the pipeline.
* **`thenAccept(Consumer<T>)`:** Takes the output of the previous stage, consumes it (e.g., printing to logs or saving to a database), and **returns `Void**`. It terminates the data flow; downstream stages will not receive a value.

---

### 7. Difference between `thenAccept()` and `thenRun()`.

* **`thenAccept(Consumer<T>)`:** Executes a callback that **has access** to the result of the previous stage. (e.g., `user -> System.out.println(user.getName())`).
* **`thenRun(Runnable)`:** Executes a callback that **does not care** about the result of the previous stage. It just runs a side-effect after the previous stage finishes (e.g., `() -> metrics.incrementCompletionCounter()`).

---

### 8. What is `thenCombine()`?

"`thenCombine()` is a coordination method used to execute **two independent CompletableFutures concurrently** and then merge their results once both finish.

You provide it with the second `CompletableFuture` and a `BiFunction` that dictates how to merge the two results. For example:
`futureA.thenCombine(futureB, (resultA, resultB) -> new AggregatedResponse(resultA, resultB))`.
This is the standard pattern for parallel scatter-gather operations."

---

### 9. What is `allOf()`?

"`allOf()` is a static method that takes an array (varargs) of `CompletableFuture` instances and returns a single `CompletableFuture<Void>` that completes **only when every single future in the array has finished**.

It is used to wait for a dynamic batch of parallel tasks. If any of the underlying futures fail, the `allOf()` future will complete exceptionally. Because it returns `<Void>`, you typically chain a `.thenApply()` afterward to manually extract the results from the original futures using `.join()`."

---

### 10. What is `anyOf()`?

"`anyOf()` takes an array of futures and returns a `CompletableFuture<Object>` that completes **as soon as the first (fastest) future in the array completes**.

This is highly useful for redundancy and high-availability patterns. For example, if you send the same query to three redundant replica databases, you can use `anyOf()` to capture the result of whichever database responds first, safely ignoring the slower ones."

---

### 11. What is `exceptionally()`?

"`exceptionally()` is the `catch` block of the CompletableFuture pipeline.

If any stage upstream throws an exception (or completes exceptionally), the pipeline skips all subsequent success stages and jumps directly to `exceptionally()`. It takes a `Function<Throwable, T>`, allowing you to log the error and **return a safe fallback or default value**, thereby recovering the pipeline back to a successful state."

---

### 12. What is `handle()`?

"`handle()` is the `finally` block of the CompletableFuture pipeline, but it allows you to manipulate the result.

It takes a `BiFunction<T, R Throwable,>`. Regardless of whether the previous stage succeeded or failed, `handle()` will execute. You check if `throwable == null` to determine success. It is used when you need to execute a unified piece of logic (like closing a resource or formatting an API response) regardless of the outcome."

---

### 13. What is `whenComplete()`?

"`whenComplete()` is a side-effect callback that executes regardless of success or failure.

It takes a `BiConsumer<T, Throwable>`. Unlike `handle()`, **it cannot change or recover the result**. Whatever value or exception entered `whenComplete()` will pass through to the next stage unmodified. It is purely designed for passive side-effects like logging, auditing, or metric recording."

---

### 14. How do you handle exceptions in `CompletableFuture`?

"In production pipelines, I handle exceptions using three primary strategies:

1. **Recovery:** Use `.exceptionally(ex -> fallbackValue)` to catch network or database errors and provide a degraded but valid response to the client.
2. **Global Translation:** Use `.handle((res, ex) -> ...)` at the very end of the pipeline to translate arbitrary backend exceptions into standardized HTTP error objects.
3. **Timeouts:** Use `.orTimeout(3, TimeUnit.SECONDS)` (Java 9+) to ensure the pipeline fails fast with a `TimeoutException` if downstream services hang."

---

### 15. What Executor does `CompletableFuture` use by default?

"If you do not explicitly provide a custom `Executor`, `CompletableFuture` defaults to the **`ForkJoinPool.commonPool()`** (assuming the machine has more than one CPU core).

This is a globally shared daemon thread pool sized to the number of logical CPU cores minus one (`Runtime.getRuntime().availableProcessors() - 1`). It is highly optimized for CPU-bound, non-blocking tasks."

---

### 16. How do you provide a custom Executor?

"Every async method in `CompletableFuture` has an overloaded version that accepts an `Executor` as its second argument:

```java
ExecutorService ioPool = Executors.newFixedThreadPool(50);
CompletableFuture.supplyAsync(() -> fetchFromDb(), ioPool)
                 .thenApplyAsync(data -> process(data), cpuPool);

```

In enterprise apps, we **always** provide a custom thread pool for I/O operations to avoid starving the globally shared ForkJoinPool."

---

### 17. How do you execute multiple REST APIs concurrently?

"I use the **Scatter-Gather pattern** via `allOf()`:

1. Map a list of requests to a list of `CompletableFuture`s using `supplyAsync(..., ioPool)`.
2. Convert that list into an array: `CompletableFuture<?>[] futuresArray = futuresList.toArray(new CompletableFuture[0])`.
3. Pass the array to `CompletableFuture.allOf(futuresArray)`.
4. This ensures all REST calls execute in parallel on the custom I/O pool, minimizing total latency to the duration of the single slowest API call."

---

### 18. How do you combine multiple API results?

"To combine exactly two results, use `future1.thenCombine(future2, (res1, res2) -> merge(res1, res2))`.

For dynamic arrays (3+ results), after calling `allOf()`, you chain a `.thenApply()` where you iterate over your original list of futures and call `.join()` on each one:

```java
CompletableFuture<List<String>> combined = CompletableFuture.allOf(futureArray)
    .thenApply(v -> futuresList.stream()
        .map(CompletableFuture::join) // Safe, because allOf guarantees they are done
        .collect(Collectors.toList()));
```"

---

### 19. How do you handle timeout in an async operation?
"Prior to Java 9, handling timeouts was notoriously difficult and required custom `ScheduledExecutorService` hacks. 

In Java 9+, it is a built-in one-liner:
* **Fail Fast:** `.orTimeout(5, TimeUnit.SECONDS)` completes the future exceptionally with a `TimeoutException` if 5 seconds elapse.
* **Graceful Degradation:** `.completeOnTimeout(defaultCacheData, 5, TimeUnit.SECONDS)` forces the future to successfully complete with a fallback value if the timer expires."

---

### 20. How do you cancel a `CompletableFuture`?
"You cancel it by calling `future.cancel(true)`. 

This immediately completes the future exceptionally with a `CancellationException`, which propagates down the pipeline. However, there is a critical caveat: **calling `cancel(true)` does NOT interrupt the underlying running thread** by default in `CompletableFuture`. If the thread is blocked on a socket read, it will remain blocked. To achieve true thread interruption, you must implement custom interruption logic or rely on modern Java Virtual Threads with structural concurrency (`StructuredTaskScope`)."

---

### 21. What happens when one stage of a `CompletableFuture` fails?
"CompletableFuture handles errors similarly to a try-catch block. 

When a stage throws a `RuntimeException`, that stage immediately transitions to a failed state. The pipeline instantly bypasses all subsequent success callbacks (like `thenApply` or `thenAccept`) and propagates the exception downwards until it hits the first error-handling callback (like `exceptionally` or `handle`). If no error handler exists, the terminal future completes exceptionally, and calling `.join()` will throw a `CompletionException`."

---

### 22. How can `CompletableFuture` cause thread-pool exhaustion?
"The most dangerous anti-pattern in `CompletableFuture` is performing **blocking I/O operations (like HTTP calls or JDBC queries) using the default `ForkJoinPool.commonPool()`**.

Because the common pool is sized based on CPU cores (e.g., 4 to 8 threads on a standard container), if 8 concurrent requests arrive and block waiting for a slow database, **the entire common pool is instantly exhausted**. This starves every other parallel stream or async task in the JVM, causing cascading application failure. You must always pass a dedicated I/O thread pool to `supplyAsync()`."

---

### 23. How do you design a reliable asynchronous workflow?
"To build bulletproof reactive pipelines, I enforce four architectural rules:

1. **Strict Thread Pool Segregation:** Never use the default ForkJoin pool for I/O. Pass a custom `ExecutorService` (or Virtual Thread executor in Java 21) to every async boundary.
2. **Mandatory Timeouts:** Every network/database boundary must be wrapped in `.orTimeout()` to prevent hanging threads.
3. **Async Handoffs:** Use the `*Async()` variants of callbacks (e.g., `thenApplyAsync`) when transitioning from heavy I/O back to CPU-bound processing, preventing I/O threads from being hijacked for compute work.
4. **Terminal Error Handling:** Every pipeline must end with a `.exceptionally()` or `.handle()` block to safely translate unhandled backend exceptions into clean client responses."

```