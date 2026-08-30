### 1. What is a race condition?

"A race condition is a concurrency bug that occurs when two or more threads access shared, mutable memory concurrently, and the final outcome of the execution depends on the exact order, timing, or interleaving in which the operating system schedules those threads.

The classic example is a non-atomic read-modify-write operation, like incrementing a variable with `count++`. At the CPU and bytecode level, `count++` is not a single instruction—it involves three distinct operations: reading the value into a CPU register, incrementing that value, and writing the updated value back to main memory. If Thread A reads `count` as 5, and context-switches to Thread B before writing, Thread B also reads 5, increments it to 6, and writes it back. When Thread A resumes, it blindly writes its calculated 6 back to memory, overwriting Thread B's update and silently losing an increment.

In production systems, race conditions often manifest as subtle data corruption, non-deterministic bugs, inconsistent database states, or rare financial discrepancies that cannot be reproduced under unit test loads. To eliminate race conditions, you must enforce mutual exclusion over the critical section using locks, synchronization primitives, or lock-free atomic structures like `AtomicInteger`."

---

### 2. What is a critical section?

"A critical section refers to any piece of code that accesses shared, mutable resources—such as in-memory state, shared data structures, file handles, network sockets, or database connections—that must never be executed by more than one thread concurrently.

The core principle behind managing a critical section is guaranteeing **mutual exclusion**. If Thread A enters the critical section, all other threads attempting to execute that same block must be paused or queued until Thread A completes its execution and exits.

From an engineering design perspective, the primary goal is to keep the critical section as small and tight as possible. If you include long-running or blocking operations—such as disk I/O, network RPC calls, or heavy serialization—inside a critical section, you hold up the lock for extended durations, which creates thread contention, degrades throughput, and can cause cascading thread pool exhaustion. We isolate only the exact memory reads and writes inside the critical section and execute non-shared preparation work outside."

---

### 3. What is thread safety?

"A class, component, or method is considered thread-safe if it behaves correctly, maintains all of its internal invariants, and produces consistent results regardless of the runtime execution scheduling, interleaving, or concurrent access by multiple threads, without requiring calling code to implement extra external synchronization.

Thread safety addresses two fundamental challenges in multi-threaded programming defined by the Java Memory Model: **atomicity** and **visibility**. Atomicity ensures that operations on shared state cannot be observed in an incomplete, halfway state by other threads. Visibility ensures that when one thread updates shared state in memory or CPU cache, that modification is immediately flushed and made visible to all other threads running on different CPU cores.

In Java, thread safety can be achieved through several distinct design patterns:

1. **Immutability:** Creating immutable objects (like `String` or Java `record`s) that have no mutable state to corrupt.
2. **Confinement:** Ensuring data is never shared across threads, such as using local stack variables or `ThreadLocal`.
3. **Synchronization & Locks:** Using the `synchronized` keyword, `ReentrantLock`, or read-write locks.
4. **Lock-Free Concurrency:** Using atomic variables (`AtomicReference`, `LongAdder`) and concurrent data structures from `java.util.concurrent`."

---

### 4. What is the `synchronized` keyword?

"In Java, `synchronized` is a language-level concurrency primitive built directly into the language and JVM to prevent race conditions. It is used to enforce **mutual exclusion** around critical sections while providing explicit memory visibility guarantees under the Java Memory Model (JMM).

When you mark a method or a block of code with `synchronized`, it tells the JVM that only one thread is permitted to execute that block at any given time. Any other thread that attempts to execute the same protected section is automatically transitioned into the `BLOCKED` state until the executing thread finishes and exits.

Beyond locking and mutual exclusion, `synchronized` establishes a formal **happens-before** relationship:

* When a thread enters a synchronized block, it invalidates its local CPU cache and reads the latest state from main memory.
* When a thread exits a synchronized block, all modifications made during that block are guaranteed to be flushed to main memory before the lock is released.

This guarantees both execution atomicity and cross-core memory visibility without requiring explicit volatile markers."

---

### 5. How does `synchronized` work internally?

"Under the hood, `synchronized` is implemented directly at the bytecode and JVM level using **monitors** associated with every Java object.

When you compile Java code:

* For a **synchronized block**, the `javac` compiler emits explicit `monitorenter` and `monitorexit` bytecode instructions. The JVM ensures that `monitorenter` attempts to increment the object's monitor lock counter. To guarantee that locks are released even if an unhandled runtime exception or error occurs, the compiler generates an internal `try-finally` exception table containing multiple `monitorexit` instructions.
* For a **synchronized method**, the compiler does not insert explicit enter/exit instructions; instead, it sets the `ACC_SYNCHRONIZED` flag in the method's access flags in the `.class` file. When the JVM invokes the method, it checks for this flag, implicitly acquires the target monitor before entering, and releases it upon method return.

At the HotSpot JVM level, object locks use the **Mark Word** in the object's memory header. The JVM applies lock optimization strategies dynamically: it starts with **Biased Locking** (optimized for single-thread re-acquisition), escalates to **Lightweight Locking** using CAS operations in user space when low contention occurs, and finally inflates to a **Heavyweight Monitor** backed by OS-level mutexes (futexes) when multiple threads actively contend for the lock."

---

### 6. What is an intrinsic lock?

"An intrinsic lock—often referred to as an **implicit lock** or **built-in lock**—is a lock mechanism that is intrinsically built into every single object instance created in the Java Virtual Machine.

In Java, you do not need to instantiate a specialized lock class to use basic locking. Every instance of `java.lang.Object` inherently carries the state necessary to act as a lock. This lock state is stored directly inside the object's header memory (the Mark Word).

When a thread enters a synchronized section, it acquires that object's intrinsic lock. The intrinsic lock acts as a single-owner token: only the thread that successfully claims ownership can proceed through the synchronized boundary. Other threads attempting to claim that same intrinsic lock must wait in an entry queue.

Because intrinsic locks are integrated directly into the language, lock acquisition and release are implicit and automatic: the lock is acquired before the block begins and released when execution exits the block, eliminating accidental lock leaks common with unclosed explicit locks."

---

### 7. What is a monitor?

"A monitor is a high-level concurrency synchronization construct originally conceptualized by Tony Hoare and Per Brinch Hansen, which bundles **mutual exclusion** and **thread signaling/cooperation** into a single cohesive structure.

A monitor consists of three core components:

1. **A Mutex (Mutual Exclusion Lock):** Ensures that at most one thread can execute within the monitor's boundary at any instant.
2. **An Entry Set (Wait-for-Lock Queue):** A queue holding threads that are blocked and waiting to acquire the monitor lock so they can enter the protected code.
3. **A Wait Set (Condition Variable / Signaling Queue):** A secondary holding area for threads that already acquired the lock, but voluntarily gave it up and suspended themselves by calling `wait()`, waiting for a specific business condition to be signaled via `notify()` or `notifyAll()`.

In computer science and operating systems, a monitor prevents developers from making low-level semaphore and mutex sequencing mistakes by tying condition variables and mutual exclusion together."

---

### 8. What is an object's monitor?

"In Java, an object's monitor is the concrete JVM-level realization of the monitor concept for a specific Java object on the heap.

Whenever an object is instantiated, its object header contains metadata that allows the JVM to allocate or bind an `ObjectMonitor` structure (in C++ within the HotSpot JVM). This `ObjectMonitor` structure tracks:

* `_owner`: A pointer to the specific `Thread` that currently holds the lock.
* `_count` / `_recursions`: An integer recording how many times the owning thread has re-entered the lock.
* `_EntryList`: The set of threads in the `BLOCKED` state competing to acquire the monitor.
* `_WaitSet`: The set of threads in the `WAITING` or `TIMED_WAITING` state that called `obj.wait()`.

When we say a thread 'acquires the object's monitor', it means the JVM has updated the `_owner` field of that object's `ObjectMonitor` to point to the executing thread. Every single object in Java can act as its own independent monitor."

---

### 9. What is a synchronized instance method?

"A synchronized instance method is a non-static method declared with the `synchronized` keyword in its signature:

```java
public synchronized void updateAccount(double amount) {
    this.balance += amount;
}

```

When a thread invokes a synchronized instance method, it is required to acquire the intrinsic lock of the **specific instance (`this`)** on which the method is called before it can execute any of the method's code.

If you have two separate instances of the class—say `accountA` and `accountB`—Thread 1 can execute `accountA.updateAccount()` concurrently with Thread 2 executing `accountB.updateAccount()`, because they are locking on two different object instances. However, if two threads attempt to call synchronized instance methods on the *exact same* instance `accountA`, one thread will acquire `accountA`'s monitor and the second thread will block until the first thread exits."

---

### 10. What is a synchronized static method?

"A synchronized static method is a static (class-level) method declared with the `synchronized` keyword:

```java
public static synchronized void incrementGlobalCounter() {
    globalCounter++;
}

```

Because static methods belong to the class rather than to any specific instance, there is no `this` reference available to lock on. Instead, when a thread invokes a synchronized static method, it locks the intrinsic monitor of the **`java.lang.Class` object** associated with that class (for example, `MyService.class`).

Because there is exactly one `Class` object per class loader in the JVM, a synchronized static method creates a **global lock across all instances** of that class within that class loader. Even if your application instantiates a million objects of that class, only one thread in the entire JVM can execute any synchronized static method of that class at any given time."

---

### 11. Difference between synchronized instance and static methods.

"The core difference between synchronized instance methods and synchronized static methods comes down to **what object monitor is locked** and the resulting **concurrency scope**:

| Dimension | Synchronized Instance Method | Synchronized Static Method |
| --- | --- | --- |
| **Locked Target** | Current object instance (`this`) | `Class` object (`ClassName.class`) |
| **Scope of Lock** | Per-instance isolation | Global across the entire class/ClassLoader |
| **Interference** | Does NOT block other instances | Blocks ALL threads calling static synchronized methods across all instances |
| **Execution with Each Other** | Can run concurrently with a static synchronized method | Can run concurrently with an instance synchronized method |

Because an instance method locks `this` and a static method locks `Class.class`, these are two completely separate objects in memory with two distinct monitors. Therefore, Thread A executing a synchronized instance method and Thread B executing a synchronized static method on the same class will **not block each other**."

---

### 12. What object is locked by an instance synchronized method?

"An instance synchronized method locks the **current object instance itself**, referenced by the keyword **`this`**.

When a thread calls `obj.myMethod()`, the JVM checks the `ACC_SYNCHRONIZED` flag and attempts to acquire the intrinsic monitor of `obj`.

Writing:

```java
public synchronized void doWork() {
    // code
}

```

is functionally and semantically identical to writing:

```java
public void doWork() {
    synchronized (this) {
        // code
    }
}

```

If two threads call synchronized instance methods on the exact same object reference, they compete for the same `this` monitor."

---

### 13. What object is locked by a static synchronized method?

"A static synchronized method locks the **`java.lang.Class` object instance** representing that class in the JVM heap memory.

For example, in a class named `OrderProcessor`, declaring:

```java
public static synchronized void processBatch() {
    // code
}

```

is functionally and semantically equivalent to writing:

```java
public static void processBatch() {
    synchronized (OrderProcessor.class) {
        // code
    }
}

```

Every loaded class in the Java runtime has an associated `Class` object managed by the ClassLoader. Locking on this `Class` object serializes access across all callers across the entire application context, regardless of how many instances of `OrderProcessor` exist."

---

### 14. What is a synchronized block?

"A synchronized block is a statement-level synchronization construct in Java that allows a developer to synchronize only a specific block of statements inside a method, while explicitly specifying which object's monitor to lock:

```java
public void processTransaction(Transaction tx) {
    // Non-critical operations run concurrently
    validateTransaction(tx);

    synchronized (this) { // Or synchronized(explicitLockObject)
        // Critical section: only shared state modifications are locked
        this.balance += tx.getAmount();
    }

    // Logging runs concurrently
    auditLog(tx);
}

```

Inside the parentheses of `synchronized(...)`, you can pass any non-null Java object reference. The thread acquires that specified object's monitor upon entering the opening brace `{` and automatically releases it upon exiting the closing brace `}`."

---

### 15. Why prefer synchronized blocks sometimes?

"In production engineering, synchronized blocks are preferred over synchronized methods for two primary reasons: **performance granularity** and **lock encapsulation**.

1. **Performance & Reduced Contention:**
   Synchronizing an entire method locks the object for the whole duration of that method. If the method performs database queries, JSON serialization, logging, or input validation, the lock is held unnecessarily long. With a synchronized block, you limit lock acquisition strictly to the 2–3 lines of code that mutate shared state, drastically reducing lock hold time and thread contention.
2. **Encapsulation & Avoiding Accidental Deadlocks:**
   When you synchronize a method, you implicitly expose the `this` monitor to outside callers. If external code or client libraries also synchronize on your object instance (`synchronized(myServiceInstance)`), they can accidentally create lock contention, resource starvation, or circular deadlocks. By using a private synchronized block:

```java
private final Object lock = new Object();

public void update() {
    synchronized (lock) {
        // Safe: lock is completely private and cannot be locked by external code
    }
}

```

you hide your lock mechanism entirely from the outside world."

---

### 16. Can two threads execute two synchronized methods of the same object?

"**No, two threads cannot execute two different synchronized instance methods on the same object simultaneously.**

Both synchronized instance methods on that object require acquiring the same intrinsic monitor: the `this` reference.

When Thread 1 enters `methodA()`, it acquires ownership of the object's monitor. When Thread 2 attempts to enter `methodB()` on that same object instance, the JVM checks the monitor, sees that it is already owned by Thread 1, and forces Thread 2 into the `BLOCKED` state. Thread 2 remains blocked until Thread 1 finishes executing `methodA()` and releases the monitor lock.

*(Note: If one of the methods is NOT synchronized, Thread 2 can execute that non-synchronized method without blocking, because non-synchronized methods do not attempt to acquire the monitor.)*"

---

### 17. Can two threads execute synchronized methods of different objects?

"**Yes, two threads can execute synchronized methods of different objects concurrently without blocking each other.**

Synchronization in Java is tied directly to individual object instances on the heap, not to the method code itself.

If you have two distinct instances, `obj1` and `obj2`:

* Thread 1 calls `obj1.doSomething()` and acquires the monitor for `obj1`.
* Thread 2 calls `obj2.doSomething()` and acquires the monitor for `obj2`.

Because `obj1` and `obj2` have completely separate object headers and distinct `ObjectMonitor` structures in JVM memory, there is zero lock contention. Both threads execute simultaneously across different CPU cores."

---

### 18. Can a synchronized method call another synchronized method?

"**Yes, a synchronized method can freely call another synchronized method on the same object or on different objects.**

When calling another synchronized method on the **same object**, the thread already owns the object's monitor. Because Java synchronization is **reentrant**, the thread is allowed to enter the second synchronized method immediately without blocking or deadlocking itself. The JVM simply increments the lock acquisition count.

When calling a synchronized method on a **different object**, the thread retains the lock on the first object while attempting to acquire the lock on the second object. While this is valid, developers must ensure a consistent lock acquisition order across the application to prevent **circular wait deadlocks** (e.g., Thread 1 holding Lock A waiting for Lock B, while Thread 2 holds Lock B waiting for Lock A)."

---

### 19. Is `synchronized` reentrant?

"**Yes, `synchronized` in Java is fully reentrant.**

Reentrancy means that if a thread already holds an intrinsic lock on an object, it can re-acquire that exact same lock as many times as it needs without blocking or suspending itself.

Without reentrant locking, recursive calls to synchronized methods or calling another synchronized method from within a synchronized method on the same object would result in **self-deadlock**—the thread would block forever waiting for a lock that it already holds. In Java, reentrancy is built into both intrinsic locks (`synchronized`) and explicit locks (`ReentrantLock`)."

---

### 20. What is reentrant synchronization?

"Reentrant synchronization is a locking mechanism where a lock is bound to the **owning thread** rather than to an individual invocation.

The JVM implements reentrancy by maintaining two pieces of state inside the object's monitor:

1. An **owner thread identifier** (`_owner`).
2. A **lock recursion counter** (`_recursions` or hold count).

Here is how it works step-by-step:

* When a thread acquires an unowned monitor for the first time, the JVM records that thread as the owner and sets the counter to 1.
* If that same thread encounters another synchronized block or method protected by the same monitor, the JVM sees that the calling thread is already the owner. It skips blocking and increments the counter to 2.
* When the thread exits the inner synchronized block, the counter decrements to 1.
* When the thread exits the outermost synchronized block, the counter decrements to 0.
* Only when the counter reaches 0 does the JVM release the lock and make it available for other competing threads to claim."

---

### 21. Can a constructor be synchronized?

"**No, a constructor cannot be declared with the `synchronized` keyword.** Doing so results in a compilation error (`modifier synchronized not allowed here`).

The architectural reason is that during object construction, the object is still being created on the heap. Under normal, proper object construction practices, the object reference is private to the thread that invoked `new` and has not yet been published to other threads. Therefore, locking the object during constructor execution is redundant because no other thread can possibly have a reference to it yet.

If you have shared resources that must be coordinated *inside* a constructor (for example, registering the new instance into a static shared registry), you can place a **synchronized block** inside the constructor body that locks on a static lock object (e.g., `synchronized(Registry.class) { ... }`). Additionally, you must be careful never to let the `this` reference escape the constructor before initialization completes."

---

### 22. Can an interface method be synchronized?

"**No, an interface method cannot have the `synchronized` keyword in its declaration.** Attempting to declare one will cause a compiler error.

The reason is rooted in clean object-oriented design principles:

* An **interface defines a behavioral contract and public API specification**—what actions a class can perform.
* The **`synchronized` keyword is an internal implementation detail** describing *how* a specific method executes concurrently.

How a class chooses to fulfill thread safety (whether using `synchronized`, `ReentrantLock`, atomics, or thread-confinement) is entirely the responsibility of the implementing class. Therefore, the interface cannot dictate synchronization modifiers. However, a concrete class implementing that interface is completely free to mark its overridden implementation as `synchronized`."

---

### 23. Can a static method and an instance method be synchronized at the same time?

"**Yes, a static method and an instance method can both be marked `synchronized` and can execute simultaneously without blocking each other.**

The reason they do not interfere with one another is that they lock on two completely distinct object monitors in memory:

* The synchronized **instance method** locks the monitor of the specific object instance (`this`).
* The synchronized **static method** locks the monitor of the `Class` object (`ClassName.class`).

Because `this` and `ClassName.class` are two separate objects with independent `ObjectMonitor` structures on the JVM heap, Thread 1 can execute the synchronized static method while Thread 2 executes the synchronized instance method at the exact same millisecond with zero lock contention.

They will only serialize execution if you explicitly synchronize both methods on a shared, common lock object using synchronized blocks."