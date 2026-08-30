
---

### 1. What is a race condition?

"A race condition is a high-level architectural concurrency flaw where the correctness of a program depends on the non-deterministic relative execution timing, scheduling, or interleaving of multiple threads.

It fundamentally happens when multiple threads access shared, mutable state and attempt to perform compound operations—such as **check-then-act** (e.g., lazy initialization checks) or **read-modify-write** (e.g., `counter++`). Because the compound operation is not atomic, a thread can be preempted right after checking a condition or reading a value. Another thread modifies that same state in the interim, invalidating the first thread's assumptions. When the first thread resumes, it operates on stale state, leading to silent data corruption, lost updates, or broken invariants.

As a senior engineer, I distinguish between race conditions at the software design level and hardware timing. A race condition is not just a missing lock—it is an algorithmic bug where program correctness is tied to the scheduler's mercy."

---

### 2. Give a real-world example of a race condition.

"A classic real-world scenario is an **inventory overselling bug** in an e-commerce checkout service or a balance deduction in fintech.

Imagine a product with exactly 1 unit left in stock. Two customers click 'Buy' simultaneously on two separate worker threads:

1. **Thread 1** executes a check-then-act: `if (inventory.getStock() > 0)`. It reads 1, evaluates to `true`, and prepares to decrement.
2. Before Thread 1 can decrement, the OS scheduler preempts it and context-switches to **Thread 2**.
3. **Thread 2** checks `if (inventory.getStock() > 0)`. The stock is still 1, so it also evaluates to `true`.
4. Thread 2 decrements the stock to 0 and completes the purchase.
5. Context switches back to Thread 1. Thread 1 has already passed the check, so it proceeds to decrement the stock from 0 to -1 and confirms the purchase.

The result is overselling physical inventory to two different customers. In production, this happens because the check and the mutation were two separate non-atomic steps without synchronization or transactional locking."

---

### 3. How can you prevent race conditions?

"Preventing race conditions requires eliminating either the **shared state**, the **mutability**, or the **non-atomic interleaving**. In Java, I approach this across four tiers:

1. **Eliminate Shared Mutable State (Best Practice):** Use immutable objects, Java records, or thread-confinement techniques like `ThreadLocal` and actor/message-passing architectures where threads never share state.
2. **Mutual Exclusion via Synchronization & Locks:** Enclose critical sections inside `synchronized` blocks or `ReentrantLock` instances. This ensures compound operations (check-then-act) execute as an uninterruptible, mutually exclusive critical section.
3. **Atomic Primitives & Lock-Free Operations:** For state accumulators, state flags, or single references, replace locks with `java.util.concurrent.atomic` classes (`AtomicInteger`, `AtomicReference`, `LongAdder`). These rely on CPU-level Compare-And-Swap (`CAS`) instructions (`LOCK CMPXCHG` on x86) to eliminate software locking overhead.
4. **Concurrent Data Structures:** Replace manual locking on standard collections with production-grade collections like `ConcurrentHashMap` and leverage atomic composite operations like `computeIfAbsent()` or `putIfAbsent()`."

---

### 4. What is atomicity?

"Atomicity means an operation or a sequence of operations executes as a single, indivisible unit of work from the perspective of all other threads in the system. An atomic operation either completes entirely or does not happen at all; it can never be observed in an intermediate, half-finished, or corrupted state.

In the Java Memory Model, reads and writes to reference variables and primitive types (except non-volatile 64-bit `long` and `double` values on 32-bit JVMs) are guaranteed to be atomic at the single-instruction level. However, compound actions—like `i++` or checking a map and inserting—are **not** atomic because they compile down to multiple bytecode and machine instructions.

To achieve compound atomicity in Java, we use:

* Intrinsic locks (`synchronized`) and explicit locks (`Lock`).
* Hardware-supported atomic CPU instructions exposed through the `VarHandle` or `Unsafe` API and packaged into `Atomic*` classes.
* Database/transactional-level ACID atomicity primitives."

---

### 5. What is visibility?

"Visibility refers to the guarantee that when one thread modifies a shared variable, that updated value is immediately and correctly observed by other threads executing on different CPU cores.

In modern multi-core architectures, each CPU core has its own private L1/L2 caches and write buffers (store buffers). When Thread A modifies a variable, that update initially sits in its local cache or store buffer and is not immediately flushed to main memory (RAM). If Thread B running on another core reads that same variable, it reads from its own stale local cache, completely unaware of Thread A's write.

In Java, visibility issues are solved by establishing a formal **happens-before** relationship via the Java Memory Model (JMM):

* Marking a variable **`volatile`** enforces CPU memory fences/barriers, forcing writes to flush and invalidating stale caches on subsequent reads.
* Entering and exiting **`synchronized` blocks** or lock acquisition/release boundaries forces cache invalidation on enter and store-buffer draining on exit."

---

### 6. What is ordering?

"Ordering refers to the sequence in which program statements and memory operations are executed by the CPU and observed by other threads.

To maximize throughput and pipeline utilization, compilers (`javac` and JIT/C2), CPU execution units, and cache controllers perform aggressive **instruction reordering** and **out-of-order execution** (speculative execution, store buffering). Under single-threaded semantics (the *as-if-serial* rule), these reorderings are completely transparent because the final result is identical.

However, in multi-threaded environments, reordering can cause catastrophic bugs. A classic case is unsafe Double-Checked Locking in singletons: the bytecode instructions for `new Helper()` (allocate memory, initialize constructor, assign reference to field) can be reordered such that the reference is published to memory *before* the constructor finishes initializing fields. Another thread reading that reference sees a half-initialized object.

We control ordering in Java using `volatile` reads/writes, `final` field freeze semantics, and memory barriers (`Acquire`/`Release` fences) defined in the JMM."

---

### 7. What are the three main properties provided by synchronization?

"The three fundamental guarantees provided by Java synchronization (`synchronized` blocks/methods) are:

1. **Atomicity (Mutual Exclusion):** It guarantees that only one thread can execute within the synchronized block at a time, ensuring that compound read-modify-write or check-then-act operations are executed as an indivisible unit.
2. **Visibility:** Under the JMM happens-before rules, when a thread enters a synchronized block, it invalidates its local CPU cache and reads fresh data from main memory. When it releases the monitor, all memory writes made during that block are guaranteed to be flushed to main memory before another thread acquires the lock.
3. **Ordering (Instruction Constraints):** It prevents compiler and hardware instruction reordering from moving operations across the lock acquisition and lock release boundaries (establishing acquire-release semantics), guaranteeing that state prepared prior to exiting the lock is seen in correct order by the thread acquiring the lock next."

---

### 8. What is a data race?

"A data race is a specific, low-level technical memory conflict precisely defined by the Java Memory Model (JSR-133).

A data race occurs when:

1. Two or more threads concurrently access the exact same memory location (field or array element), AND
2. At least one of those accesses is a **write**, AND
3. The threads do not use any synchronization or JMM-recognized coordination (no `volatile`, locks, or `happens-before` edge) to order their accesses.

When a program contains a data race, the Java Memory Model loses its sequential consistency guarantees, and the JVM is legally allowed to exhibit bizarre behaviors, such as reading stale data, seeing out-of-order writes, or observing variable values appear out of thin air."

---

### 9. Difference between race condition and data race.

"Although often used interchangeably, they are fundamentally different concepts:

| Dimension | Data Race | Race Condition |
| --- | --- | --- |
| **Level** | Low-level memory access conflict (JMM / Hardware level). | High-level architectural or algorithmic design flaw. |
| **Definition** | Unsynchronized concurrent read/write to the same memory address. | Program correctness depends on the timing/interleaving of thread execution. |
| **Fix** | Add `volatile`, memory barriers, or synchronization. | Redesign business logic, enforce atomicity over critical sections. |
| **Relationship** | You can have a data race without a race condition, and a race condition **without a data race**. |  |

A critical senior-level distinction is that **you can have a race condition without a data race**. For example, if you use a thread-safe `ConcurrentHashMap` and execute:

```java
if (!map.containsKey(key)) {
    map.put(key, value);
}

```

There is **no data race** because every individual map method is internally synchronized and volatile. Yet there is a severe **race condition (check-then-act)** because another thread can insert the key between your `containsKey` check and `put` call. To fix the race condition, you must use `map.putIfAbsent()`."

---

### 10. What is a thread-safe class?

"A class is thread-safe if it encapsulates its state correctly and guarantees that its invariants, internal state, and public API behave correctly when accessed concurrently by multiple threads, without requiring the calling client code to perform external coordination, manual locking, or defensive synchronization.

According to Brian Goetz in *Java Concurrency in Practice*, a thread-safe class manages its own state such that no sequence of concurrent operations can place an instance of the class into an invalid, corrupted, or inconsistent state.

In practical terms, a thread-safe class guarantees:

* Correct handling of shared mutable fields via synchronization, atomic structures, or immutability.
* Safe publication of its internal references so callers cannot corrupt state from the outside.
* Clean composition of compound operations so multi-step workflows do not violate internal invariants."

---

### 11. How do you make a class thread-safe?

"To make a class thread-safe, I evaluate the class requirements and apply one of the following architectural strategies:

1. **Make the Class Fully Immutable:** Design the class as a `record` or `final` class with `private final` fields, defensive copying in constructors, and no mutators. Immutable objects are inherently thread-safe with zero synchronization overhead.
2. **Thread Confinement:** Ensure instances are never shared across threads. Use stack confinement (local variables inside methods) or manage thread-specific instances using `ThreadLocal`.
3. **Encapsulated Synchronization:** Encapsulate mutable state behind private locks. Use `synchronized` blocks locking on a `private final Object lock = new Object();` or `ReentrantLock` / `ReentrantReadWriteLock` to protect multi-step read-modify-write operations.
4. **Delegate to Concurrent Data Structures & Atomics:** Instead of raw primitives and `ArrayList`/`HashMap`, delegate state management to `AtomicInteger`, `AtomicReference`, `ConcurrentHashMap`, or `CopyOnWriteArrayList`.
5. **Safe Publication:** Ensure references to instances are published safely using `volatile`, `final` fields, or static initializers to prevent other threads from observing partially constructed state."

---

### 12. What is an immutable object?

"An immutable object is an object whose observable state cannot be modified in any way after its construction is completed. Once instantiated, its internal values, properties, and referenced graphs remain completely constant for its entire lifetime.

In Java, creating a strictly immutable object requires following five rules:

1. Declare the class as `final` so child classes cannot override methods and introduce mutable state.
2. Make all fields `private` and `final`.
3. Do not provide any setter or mutator methods.
4. Perform **deep defensive copies** in constructors when accepting mutable inputs (e.g., `Date`, collections, custom objects) or use `List.copyOf()`.
5. Return **defensive copies or unmodifiable views** in getters so callers cannot mutate internal structures directly.

Java 14+ `record`s provide a modern, concise foundation for immutable data carriers."

---

### 13. Why are immutable objects thread-safe?

"Immutable objects are inherently thread-safe because **thread safety issues only occur when you have concurrent mutation of shared state**. If state cannot be modified, data races, race conditions, and lock contention are mathematically eliminated.

At the Java Memory Model level, immutable objects benefit from **`final` field freeze semantics** (JSR-133):

* When an object is constructed with `final` fields, the JVM enforces a memory barrier at the end of the constructor (the freeze action).
* This guarantees that once the constructor completes and the object reference is published, all threads will immediately see the correctly initialized values of those `final` fields without requiring explicit synchronization, locks, or `volatile` markers.

Multiple threads can read, share, and pass around the same immutable instance across CPU cores simultaneously with zero lock overhead, zero cache-coherency ping-ponging, and guaranteed memory consistency."

---

### 14. How does immutability help in multithreading?

"Immutability provides massive architectural and performance advantages in multithreaded systems:

1. **Zero Synchronization Overhead:** Because the state never changes, threads can read data concurrently without acquiring locks (`synchronized` or `Lock`). This eliminates lock contention, context switching, and thread blocking.
2. **Immunity to Deadlocks & Starvation:** Without locks, you completely eliminate the possibility of deadlocks, live-locks, or thread starvation.
3. **Safe Sharing & Caching:** Immutable instances can be freely shared, cached globally, and reused across thousands of threads (e.g., `String` interning, `BigDecimal`, flyweight patterns) without defensive copying on read.
4. **Simplified Reasoning:** Multi-threaded bugs are notoriously difficult to reproduce. Immutability makes code deterministic and easy to reason about because an object's state cannot be corrupted by an unexpected background thread."

---

### 15. Can a final variable be changed?

"Under normal Java language semantics, **no—a `final` variable cannot be reassigned once initialized**. For primitive types, the value is constant. For object references, the reference pointer is locked and cannot point to another object on the heap.

However, from an advanced JVM perspective, there are two important technical nuances:

1. **The Referenced Object Can Still Be Mutated:** If a `final` variable points to a mutable object (like `final List<String> list = new ArrayList<>();`), the reference `list` cannot be reassigned, but the underlying list content **can** be modified via `list.add("data")`.
2. **Reflection / Unsafe Hacking:** Prior to Java 12, reflection (`Field.setAccessible(true)` modifying `modifiers`) or low-level `sun.misc.Unsafe` could technically mutate a `final` field in memory. However, in modern Java (LTS 17+, 21+ with strong encapsulation under Project Jigsaw), modifying `final` fields via reflection is blocked and throws exceptions.

Furthermore, the JIT compiler often aggressively inlines constants and `final` fields, meaning mutating a `final` field via reflection leads to undefined behavior where the CPU continues using the cached/inlined constant anyway."

---

### 16. Is `final` enough to make an object thread-safe?

"**No, marking fields as `final` is necessary but not sufficient on its own to make an object thread-safe.**

There are three major ways an object with `final` fields can still fail thread safety:

1. **Shallow Immutability (Mutable References):** If a `final` field points to a mutable object (e.g., `private final List<String> items`), external threads can still modify the contents of that list unless deep defensive copying is used.
2. **`this` Reference Escape During Construction:** If the `this` reference escapes the constructor before construction completes (for example, starting a thread inside the constructor, registering a listener, or passing `this` to a callback), another thread can observe the object **before** the JMM `final` freeze action completes, observing uninitialized fields.
3. **Multiple Related Fields (Invariant Inconsistency):** Even if individual fields are `final`, if business invariants depend on multiple state transitions that need atomic updates, an immutable snapshot must be replaced atomically (e.g., using an `AtomicReference<ImmutableState>`)."

---

### 17. Why is String thread-safe?

"In Java, `String` is completely thread-safe because it was designed from the ground up as a **strictly immutable class**:

1. **`final` Class:** The `String` class is declared `public final class String`, preventing subclasses from overriding methods to introduce mutable behavior.
2. **Private Final Backing Storage:** Internally, the string data is stored in a private final byte array (`private final byte[] value` in modern Java with Compact Strings, or `char[]` in older versions).
3. **No Mutators:** The `String` class exposes no setter or mutator methods. Any method that appears to modify a string (like `.replace()`, `.substring()`, or `.toUpperCase()`) creates and returns a brand-new `String` instance on the heap, leaving the original instance untouched.
4. **Thread-Safe Cached HashCode:** `String` caches its `hashCode()` in a private field `int hash`. Because the underlying byte array never changes, the hash calculation is deterministic, idempotent, and safely published across threads without synchronization."

---

### 18. Is StringBuilder thread-safe?

"**No, `StringBuilder` is explicitly NOT thread-safe.**

`StringBuilder` was introduced in Java 5 as a high-performance, single-threaded replacement for `StringBuffer`. Its internal methods (like `.append()`, `.insert()`, or `.delete()`) do not contain the `synchronized` keyword or any memory barriers.

If multiple threads concurrently call `.append()` on a shared `StringBuilder` instance, several failure modes occur:

* **Lost Updates:** Characters or strings from one thread overwrite those from another because the internal `count` index is updated non-atomically.
* **`ArrayIndexOutOfBoundsException`:** Multiple threads can check the buffer capacity simultaneously, pass the check, and write beyond the allocated array before the internal buffer expansion completes.
* **Corrupted String Output:** The resulting string will have missing, scrambled, or truncated characters.

`StringBuilder` should be used strictly within method-local scope (stack confinement) where it is accessed by only one thread."

---

### 19. Is StringBuffer thread-safe?

"**Yes, `StringBuffer` is thread-safe.**

`StringBuffer` achieves thread safety by declaring almost all of its public mutating and accessor methods (such as `append()`, `insert()`, `toString()`, and `length()`) as **`synchronized` instance methods**.

When a thread invokes any method on a `StringBuffer` instance, it acquires the intrinsic monitor lock (`this`) of that buffer instance. This serializes all concurrent accesses, ensuring that internal buffer resizing, character copying, and array index updates execute with mutual exclusion and proper memory visibility.

However, in modern enterprise Java development, `StringBuffer` is largely considered a legacy class. Thread-safe string concatenation across multiple threads is rarely needed; for single-threaded or method-local operations, `StringBuilder` is significantly faster because it avoids locking overhead."

---

### 20. Difference between StringBuilder and StringBuffer.

"The core differences between `StringBuilder` and `StringBuffer` span thread safety, performance, and historical design:

| Feature | `StringBuilder` | `StringBuffer` |
| --- | --- | --- |
| **Thread Safety** | **Not Thread-Safe** (no synchronization) | **Thread-Safe** (methods are `synchronized`) |
| **Performance / Speed** | **Fast & Lightweight:** Zero synchronization or lock acquisition overhead | **Slower:** Incurs lock acquisition/release and monitor overhead per method call |
| **Introduced In** | Java 5 (J2SE 5.0) | Java 1.0 |
| **Primary Use Case** | Method-local (stack-confined) string construction, loops, logging formatting | Legacy code or rare scenarios where a shared buffer is mutated across threads |
| **JVM Optimization** | Easily optimized by JIT; no lock-elision analysis required | Relies on JVM JIT **Lock Elision** / Escape Analysis to remove locks on local instances |

**Senior Production Recommendation:**
In modern applications, 99.9% of string construction happens locally within a method or thread. Therefore, `StringBuilder` should always be the default choice. If thread-safe accumulation across threads is truly required, modern architectures typically prefer lock-free logging appenders, thread-local buffers, or streaming pipelines rather than sharing a synchronized `StringBuffer`."