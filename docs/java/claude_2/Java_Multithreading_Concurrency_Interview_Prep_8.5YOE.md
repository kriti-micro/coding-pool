# Java Multithreading & Concurrency — Interview Prep (8.5 Years Backend Experience)

> Target level: Senior Java Backend Engineer / Lead. Expect deep-dives on the **Java Memory Model (JMM)**, **`java.util.concurrent` internals**, **lock-free algorithms**, and **real production deadlock/race-condition diagnosis** — not just "what is a thread."

---

## 1. Class / Interface Hierarchy Tree — Concurrency Building Blocks

### 1.1 Thread Creation & Lifecycle

```
java.lang.Object
      |
java.lang.Thread  (implements Runnable)

java.lang.Runnable (functional interface: void run())
java.util.concurrent.Callable<V> (functional interface: V call() throws Exception)

Thread States (Thread.State enum):
NEW -> RUNNABLE -> [BLOCKED | WAITING | TIMED_WAITING] -> TERMINATED
```

### 1.2 `java.util.concurrent` — Executor Framework

```
java.util.concurrent.Executor  (interface: void execute(Runnable))
      |
   ExecutorService  (interface: submit(), invokeAll(), shutdown(), Future<T> returns)
      |
      |------------------------------------------------
      |                        |                       |
ThreadPoolExecutor      ScheduledExecutorService   ForkJoinPool
      |                        |
      |                 ScheduledThreadPoolExecutor
      |
(created via Executors factory methods:
 newFixedThreadPool, newCachedThreadPool,
 newSingleThreadExecutor, newScheduledThreadPool,
 newWorkStealingPool, newVirtualThreadPerTaskExecutor [Java 21+])
```

### 1.3 Future / CompletableFuture

```
java.util.concurrent.Future<V>  (interface: get(), cancel(), isDone(), isCancelled())
      |
   CompletableFuture<T>  (implements Future<T>, CompletionStage<T>)

CompletionStage<T>  (interface: thenApply, thenCompose, thenCombine, exceptionally, handle...)
```

### 1.4 Locks & Synchronizers (`java.util.concurrent.locks`)

```
Lock  (interface: lock(), unlock(), tryLock(), lockInterruptibly(), newCondition())
      |
      |------------------------------
      |                             |
ReentrantLock                ReentrantReadWriteLock  (implements ReadWriteLock)
                                     |
                          .readLock() -> Lock   .writeLock() -> Lock

Condition (interface: await(), signal(), signalAll()) — bound to a Lock instance

StampedLock (NOT implementing Lock interface — optimistic-read variant, Java 8+)

AbstractQueuedSynchronizer (AQS) — the internal framework underpinning
   ReentrantLock, ReentrantReadWriteLock, Semaphore, CountDownLatch, FutureTask
```

### 1.5 High-Level Synchronizers

```
java.util.concurrent (utility synchronizer classes, no shared interface):
   |-- CountDownLatch       (one-time gate, count-down only)
   |-- CyclicBarrier        (reusable, all-parties-wait rendezvous)
   |-- Semaphore            (permit-based access control)
   |-- Phaser               (advanced, multi-phase, dynamic party barrier)
   |-- Exchanger<V>         (pairwise data exchange between two threads)
```

### 1.6 Concurrent Collections (cross-reference with Collections template)

```
ConcurrentMap<K,V>  (interface, extends Map<K,V>)
      |-- ConcurrentHashMap
      |-- ConcurrentSkipListMap  (implements ConcurrentNavigableMap)

BlockingQueue<E>  (interface, extends Queue<E>)
      |-- ArrayBlockingQueue
      |-- LinkedBlockingQueue
      |-- PriorityBlockingQueue
      |-- SynchronousQueue
      |-- DelayQueue
      |-- LinkedBlockingDeque (also implements BlockingDeque)

CopyOnWriteArrayList / CopyOnWriteArraySet
ConcurrentLinkedQueue / ConcurrentLinkedDeque  (lock-free, non-blocking, unbounded)
```

### 1.7 Atomic Variables (`java.util.concurrent.atomic`)

```
AtomicInteger / AtomicLong / AtomicBoolean / AtomicReference<V>
AtomicIntegerArray / AtomicLongArray / AtomicReferenceArray<E>
AtomicIntegerFieldUpdater / AtomicLongFieldUpdater / AtomicReferenceFieldUpdater
LongAdder / DoubleAdder / LongAccumulator / DoubleAccumulator  (Java 8+, striped counters)
```

---

## 2. Java Memory Model (JMM) — The Foundational Deep-Dive Topic

### Q1. What is the Java Memory Model, and why does it matter beyond just "threads share memory"?
The JMM (formally defined in **JLS Chapter 17**, overhauled by **JSR-133** in Java 5) specifies the rules for **how and when writes by one thread become visible to another thread**, and what reorderings the compiler/JIT/CPU are legally allowed to perform. Without a well-defined memory model, a thread might **never see** another thread's write to a shared variable (cached in a CPU register or store buffer), or see writes in an order different from program order due to compiler/CPU **instruction reordering** optimizations. The JMM defines the **"happens-before" relationship** — the core formal contract that says: if action A happens-before action B, then A's effects are guaranteed visible to B.

### Q2. What establishes a "happens-before" relationship? List the key sources.
1. **Program order rule** — within a single thread, each action happens-before every subsequent action in that thread (in *that thread's* view; doesn't say anything about other threads).
2. **Monitor lock rule** — an `unlock` on a monitor happens-before every subsequent `lock` on that **same** monitor (by any thread) — this is why `synchronized` provides both mutual exclusion **and** visibility.
3. **Volatile variable rule** — a write to a `volatile` field happens-before every subsequent read of that **same** field.
4. **Thread start rule** — `Thread.start()` happens-before any action in the started thread.
5. **Thread join rule** — all actions in a thread happen-before another thread successfully returns from a `join()` on it.
6. **Transitivity** — if A happens-before B, and B happens-before C, then A happens-before C.

### Q3. Why is `volatile` alone NOT sufficient for compound operations like `count++`? (One of the most important tricky questions)
`volatile` guarantees **visibility** (every read sees the latest write) and prevents certain **reorderings**, but does **NOT** guarantee **atomicity**. `count++` is actually **three separate operations**: read `count`, add 1, write `count` back. Two threads can both read the same value before either writes back, causing a **lost update** — even though the field is `volatile`.
```java
volatile int count = 0;
// Thread A and Thread B both execute count++ concurrently
// Possible result: count increases by only 1 instead of 2 — a classic race condition
```
**Fix:** use `AtomicInteger.incrementAndGet()` (CAS-based atomicity), or `synchronized`, for any read-modify-write operation.

### Q4. What exactly does the `volatile` keyword prevent at the CPU/compiler level?
- Prevents the compiler/JIT from **caching** the variable in a CPU register or thread-local cache — every read goes to main memory (or at least, is guaranteed cache-coherent), every write is immediately flushed.
- Prevents **instruction reordering** around the volatile access — specifically, the JMM disallows reordering a volatile write with any earlier read/write (a "StoreStore"/"StoreLoad" memory barrier is inserted), and disallows reordering a volatile read with any later read/write ("LoadLoad"/"LoadStore" barrier). This is precisely why the **Double-Checked Locking** singleton pattern requires `volatile` to work correctly (see Q22).

### Q5. `synchronized` — what does it actually provide, and what's the underlying mechanism?
Provides **both mutual exclusion (atomicity)** — only one thread can hold a given monitor at a time — **and visibility** (via the happens-before monitor lock rule, Q2.2). Internally, every Java object has an associated **monitor** (intrinsic lock), tracked via the object's header (**mark word**). Historically implemented as a heavyweight OS-level mutex; modern JVMs use **biased locking** (removed in Java 15+, JEP 374, due to complexity/marginal benefit vs. risk), **lightweight/thin locks** (CAS-based, for uncontended cases), and only escalate to a full OS mutex ("**lock inflation**") under real contention — a significant performance optimization path worth mentioning to show JVM-internals depth.

### Q6. `synchronized` method vs `synchronized` block — any real difference beyond syntax?
- `synchronized` **instance method** locks on `this`.
- `synchronized` **static method** locks on the `Class` object (`ClassName.class`) — a completely **different lock** from any instance-level lock, a common source of confusion/bugs (a static synchronized method and an instance synchronized method on the same class do NOT mutually exclude each other).
- `synchronized(someObject) { ... }` **block** lets you lock on a **specific, deliberately chosen object** — narrower scope (better performance, less contention) and avoids accidentally exposing your lock object to external code (see the String-as-lock anti-pattern from the String template, Q18).

### Q7. What is "false sharing," and how do you avoid it?
CPU caches operate on **cache lines** (typically 64 bytes). If two *unrelated* variables used by *different threads* happen to sit on the **same cache line** (e.g., adjacent fields in a class), a write by one thread invalidates the entire cache line for the other thread's CPU core — even though the threads aren't logically sharing data — causing severe, invisible performance degradation. **Fix:** padding (adding unused filler fields to push hot variables onto separate cache lines) or `@Contended` annotation (JDK internal/restricted use, `-XX:-RestrictContended` to enable broadly). `LongAdder`'s internal striped-counter design (Q4 in Collections template context) specifically uses padding to avoid this. A great "do you understand hardware-level concurrency costs" signal question.

---

## 3. `java.util.concurrent` — Executors & Thread Pools

### Q8. Why is `Executors.newFixedThreadPool()`/`newCachedThreadPool()` discouraged in production (a very common modern interview point)?
Both factory methods use **unbounded queues or unbounded thread creation** internally:
- `newFixedThreadPool(n)` → uses an **unbounded `LinkedBlockingQueue`** — under sustained overload, tasks queue up indefinitely, risking `OutOfMemoryError` rather than failing fast or applying backpressure.
- `newCachedThreadPool()` → uses a `SynchronousQueue` with **`Integer.MAX_VALUE` max pool size** — under load spikes, it can create **unbounded numbers of threads**, exhausting OS resources / crashing the process.
**Best practice (and explicitly recommended by Effective Java / JDK team guidance since):** construct `ThreadPoolExecutor` **directly**, explicitly specifying `corePoolSize`, `maximumPoolSize`, a **bounded** `BlockingQueue` (e.g., `ArrayBlockingQueue`), and a sensible `RejectedExecutionHandler` (e.g., `CallerRunsPolicy` for backpressure, or a custom one that logs+drops with metrics) — giving explicit, predictable behavior under load rather than silent unbounded growth.

### Q9. Explain `ThreadPoolExecutor`'s core parameters and exactly how task submission decides thread creation vs queueing.
```java
new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                        workQueue, threadFactory, rejectedExecutionHandler);
```
Submission order of decisions on `execute(task)`:
1. If **fewer than `corePoolSize`** threads are running, **start a new thread** immediately (even if other core threads are idle) — this is a commonly-missed nuance; it does NOT check idle core threads first.
2. Otherwise, try to **enqueue** the task into `workQueue`. If the queue accepts it (has capacity), the task waits.
3. If the queue is **full** (bounded queue at capacity) AND current pool size < `maximumPoolSize`, create a **new (non-core) thread** to handle it immediately.
4. If pool is already at `maximumPoolSize` AND queue is full → invoke the **`RejectedExecutionHandler`** (default: `AbortPolicy`, throws `RejectedExecutionException`).
**Tricky consequence:** with an **unbounded queue** (like `newFixedThreadPool`'s default), step 3 is **never reached** — `maximumPoolSize` becomes irrelevant/dead configuration, since the queue always "has room," which is exactly why unbounded queues are dangerous (Q8).

### Q10. `Future` vs `CompletableFuture` — what problem does `CompletableFuture` (Java 8) actually solve?
`Future.get()` is **blocking** — there's no way to attach a callback, chain dependent async operations, or combine multiple futures without blocking a thread to wait. `CompletableFuture` implements `CompletionStage`, enabling **non-blocking, composable async pipelines**:
```java
CompletableFuture.supplyAsync(() -> fetchUser(id))
    .thenApply(User::getEmail)
    .thenCompose(email -> sendNotificationAsync(email))
    .exceptionally(ex -> { log.error("failed", ex); return null; });
```
Key methods to know cold: `thenApply` (sync transform), `thenApplyAsync` (transform on a separate thread/executor), `thenCompose` (flatMap-style chaining of another `CompletableFuture`-returning operation), `thenCombine` (join two independent futures), `allOf`/`anyOf` (fan-in), `exceptionally`/`handle`/`whenComplete` (error handling — see String... actually Exception Handling template Q15 for exception-propagation nuances).

### Q11. `ForkJoinPool` and the **work-stealing** algorithm — how does it differ from a regular `ThreadPoolExecutor`?
Designed for **divide-and-conquer** recursive parallelism (`RecursiveTask<V>`/`RecursiveAction`, and it's also the pool backing **parallel streams** by default via the common pool). Each worker thread has its **own double-ended work queue (deque)**. A thread pushes/pops its own sub-tasks from the **head** of its own deque (LIFO, good cache locality for its own work), but when a thread runs out of work, it **steals** from the **tail** of another busy thread's deque (FIFO from the victim's perspective, minimizing contention with the victim thread which is working from its own head). This work-stealing design keeps all CPU cores busy with minimal central-queue contention — far more scalable for many small recursive tasks than a shared-queue `ThreadPoolExecutor`.

### Q12. What's the danger of using **blocking I/O calls inside a parallel stream** (`.parallel()`) or inside `CompletableFuture.supplyAsync()` without a custom executor?
Both default to the **common `ForkJoinPool`** (sized to `Runtime.availableProcessors() - 1` by default) — a **shared, process-wide resource**. Blocking that pool's threads on slow I/O (DB calls, HTTP calls) **starves every other unrelated parallel stream or CompletableFuture chain in the entire JVM**, since they all compete for the same limited thread pool. **Best practice:** always supply a **dedicated, appropriately-sized `Executor`** for I/O-bound async work (`CompletableFuture.supplyAsync(task, myIoExecutor)`), reserving the common `ForkJoinPool` for genuinely CPU-bound parallel computation. A very real, very common production incident pattern worth raising proactively.

---

## 4. Locks Beyond `synchronized`

### Q13. `ReentrantLock` vs `synchronized` — when would you actually choose `ReentrantLock`?
| | `synchronized` | `ReentrantLock` |
|---|---|---|
| Acquisition | Implicit (block/method scoped) | Explicit `lock()`/`unlock()` (must use try/finally) |
| Interruptible acquisition | No | Yes — `lockInterruptibly()` |
| Timed/non-blocking acquisition | No | Yes — `tryLock(timeout)` |
| Fairness policy | No (JVM decides) | Optional — `new ReentrantLock(true)` for FIFO fairness (at a throughput cost) |
| Condition variables | One implicit (`wait/notify/notifyAll`) | Multiple, via `newCondition()` — allows fine-grained wake-up groups |
| Performance (uncontended) | Comparable in modern JVMs (both highly optimized) | Comparable |

**Use `ReentrantLock`** when you need `tryLock()` (avoiding indefinite blocking/deadlock potential), interruptible lock acquisition, multiple wait-sets via multiple `Condition`s (e.g., a bounded buffer needing separate "not full"/"not empty" conditions — classic producer-consumer pattern), or non-block-scoped locking (acquire in one method, release in another — though this is generally risky/discouraged design).

### Q14. Why does `ReentrantLock` require a `try/finally` block religiously? What happens if you forget?
```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();  // MUST be here — if an exception is thrown in the try block and unlock() is missed, the lock is held FOREVER
}
```
Unlike `synchronized`, which **automatically releases the monitor** even on an exception (compiler-generated implicit `finally`-like cleanup), `ReentrantLock` is a plain object with no compiler-enforced release — an exception thrown before `unlock()` without a `finally` guard leaves the lock **permanently held**, causing every other thread waiting on it to block forever (a silent, catastrophic deadlock). This is a frequently-tested "do you know the real trade-off" question.

### Q15. `ReentrantReadWriteLock` — what's the concurrency model, and what's the classic pitfall?
Allows **multiple concurrent readers** OR **one exclusive writer**, never both simultaneously. Great for **read-heavy, write-rare** shared state (e.g., an in-memory cache/config refreshed occasionally). **Classic pitfall — lock downgrading vs upgrading:**
- **Downgrading** (write lock → read lock, **while still holding the write lock**) is explicitly **supported and safe**: acquire read lock before releasing write lock.
- **Upgrading** (read lock → write lock) is **NOT supported directly** — attempting to acquire the write lock while holding only the read lock will **deadlock** (since the write lock waits for all readers, including yourself, to release first). You must fully release the read lock first, then acquire the write lock (accepting that another thread may modify state in between — requires re-checking conditions, "optimistic-then-verify" style).

### Q16. `StampedLock` (Java 8+) — what problem does it solve that `ReentrantReadWriteLock` doesn't?
Adds a third mode: **optimistic reading** — `tryOptimisticRead()` returns a "stamp" **without actually acquiring any lock at all**, assuming no writer is concurrently active. The caller reads the data, then calls `validate(stamp)` to check if a write occurred concurrently; if invalidated, it falls back to a full pessimistic read lock. This avoids reader-lock overhead entirely in the common uncontended case — significantly higher throughput for read-heavy workloads than `ReentrantReadWriteLock`, at the cost of a notably trickier, error-prone API (not reentrant, doesn't support `Condition`s, easy to misuse) — mention this as a deliberate advanced/niche tool, not a default choice.

### Q17. What is `AbstractQueuedSynchronizer` (AQS), and why does knowing it matter?
AQS is the internal framework (a `volatile int state` field + an intrinsic FIFO wait queue of blocked threads, implemented via CAS operations) that **underlies** `ReentrantLock`, `Semaphore`, `CountDownLatch`, `ReentrantReadWriteLock`, and `FutureTask`. Understanding AQS demonstrates you grasp that these aren't independently reinvented — they're all built by subclassing AQS and defining what "acquire" and "release" mean for `state` (e.g., `ReentrantLock` uses `state` as a reentrancy count; `Semaphore` uses it as available-permits count; `CountDownLatch` uses it as a countdown counter that only decrements). A strong senior-level signal to bring this up unprompted when discussing any of these classes.

---

## 5. High-Level Synchronizers

### Q18. `CountDownLatch` vs `CyclicBarrier` — key difference (frequently confused)?
- `CountDownLatch`: **one-time use**, cannot be reset. One or more threads wait via `await()` until the count reaches zero via other threads calling `countDown()`. Classic use: "wait for N startup tasks to complete before proceeding" or "wait for all worker threads to finish before aggregating results."
- `CyclicBarrier`: **reusable** — once all N parties call `await()`, the barrier trips, **optionally runs a barrier action**, and automatically **resets** for the next round. Classic use: iterative/phased parallel algorithms where threads must all reach a checkpoint before any proceeds to the next phase (e.g., simulation time-steps).

### Q19. `Semaphore` — what's it actually for, beyond "a counter"?
Controls access to a **limited number of resources/permits** — `acquire()` blocks if no permits available, `release()` returns one. Classic use: limiting concurrent connections to an external resource (e.g., max 10 concurrent DB connections/API calls) even when using a larger thread pool. Can be used with **more releases than acquires** to model a resource pool of dynamically varying capacity, or as a **simple mutual-exclusion mechanism** with `Semaphore(1)` — though `ReentrantLock` is more idiomatic for pure mutex use.

---

## 6. Common Concurrency Bugs & Diagnosis

### Q20. What are the four necessary conditions for **deadlock** (Coffman conditions), and how do you break them in practice?
1. **Mutual exclusion** — resource can't be shared.
2. **Hold and wait** — thread holds one lock while waiting for another.
3. **No preemption** — a lock can't be forcibly taken from a thread.
4. **Circular wait** — a cycle of threads each waiting on a lock held by the next.
**Practical prevention:** the most common and effective real-world fix is breaking **circular wait** — enforce a **consistent global lock ordering** across the entire codebase (e.g., always acquire `lockA` before `lockB`, everywhere, documented and enforced by convention/code review). Alternatively, use `tryLock(timeout)` (breaks "no preemption" by allowing a thread to back off and retry rather than wait forever) — a classic **live-coding answer** to "how would you fix this deadlock-prone code."

### Q21. Classic deadlock code — identify the bug and fix it.
```java
// Thread 1: synchronized(accountA) { synchronized(accountB) { transfer(); } }
// Thread 2: synchronized(accountB) { synchronized(accountA) { transfer(); } }
```
Two threads acquire the same two locks in **opposite order** → classic circular-wait deadlock if both run concurrently on a transfer between the same two accounts in reverse direction. **Fix:** establish a **consistent ordering** — e.g., always lock the account with the **lower `System.identityHashCode()`** (or a stable unique ID like account number) first, regardless of transfer direction:
```java
Account first = accountA.getId() < accountB.getId() ? accountA : accountB;
Account second = accountA.getId() < accountB.getId() ? accountB : accountA;
synchronized(first) { synchronized(second) { transfer(); } }
```

### Q22. Double-Checked Locking (DCL) singleton — why is it broken without `volatile`, and how does `volatile` fix it? (Very common tricky question)
```java
private static Helper instance;
public static Helper getInstance() {
    if (instance == null) {                    // 1st check (no lock, fast path)
        synchronized (Helper.class) {
            if (instance == null) {             // 2nd check (locked)
                instance = new Helper();        // PROBLEM without volatile!
            }
        }
    }
    return instance;
}
```
**The problem:** `instance = new Helper()` is NOT a single atomic operation — it involves (a) allocate memory, (b) run the constructor to initialize fields, (c) assign the reference to `instance`. Without `volatile`, the JIT/CPU is **legally allowed to reorder (b) and (c)** — meaning another thread could see a **non-null but not-yet-fully-constructed** `instance` reference in the first (unlocked) check, and start using a **partially-initialized object**, reading default/garbage field values.
**Fix:** declare `private static volatile Helper instance;` — the volatile write's memory barrier (Q4) prevents this specific reordering, ensuring that if another thread sees a non-null `instance`, the constructor has fully completed (happens-before guarantee).
**Better modern alternative:** the **initialization-on-demand holder idiom** (nested static class, relies on JVM's guaranteed thread-safe, lazy, one-time class initialization) avoids DCL's subtlety entirely, or simply use an `enum` singleton (Joshua Bloch's recommended approach) — worth mentioning as the *actually* preferred modern solution rather than DCL at all.

### Q23. What is "livelock," and how is it different from deadlock?
In deadlock, threads are **blocked**, doing nothing. In **livelock**, threads are **actively running** but making no real progress — e.g., two threads each repeatedly try to back off and retry acquiring a lock in a way that perfectly keeps colliding with each other's retry (a poorly designed "politeness" retry algorithm, analogous to two people repeatedly stepping the same direction trying to let each other pass in a hallway). Fix typically involves adding **randomized backoff** (jitter) to retry logic, breaking the lockstep pattern.

### Q24. What is "thread starvation," and how does it differ from deadlock/livelock?
A thread is perpetually denied access to a needed resource because other threads are repeatedly favored (e.g., an unfair lock policy, or a low-priority thread that never gets CPU time because higher-priority threads dominate scheduling). Unlike deadlock (permanently stuck) or livelock (actively but fruitlessly working), a starved thread **could theoretically make progress eventually** but practically never does under sustained contention. Mitigated by using **fair locks** (`new ReentrantLock(true)`) — at a throughput cost — or by fixing skewed priority/scheduling design.

### Q25. Why is `wait()`/`notify()`/`notifyAll()` almost always called inside a `synchronized` block, and why must `wait()` be called in a **loop**, not an `if`?
- `wait()`/`notify()`/`notifyAll()` **must** be called while holding the object's monitor (`synchronized` on that object) — calling them otherwise throws `IllegalMonitorStateException`. This is because these methods atomically release the monitor (on `wait()`) and require re-acquiring it (after being woken) as part of the JMM's guarantees around the wait-set mechanism.
- **`wait()` in a loop, not `if`** — because of **spurious wakeups** (the JMM explicitly permits a thread to wake from `wait()` without any corresponding `notify()`, due to underlying OS-level implementation realities) AND because even a legitimate `notifyAll()` might wake multiple threads where the condition is only true for one of them by the time each actually re-acquires the lock. **Always re-check the condition in a `while` loop:**
```java
synchronized (lock) {
    while (!conditionMet) {   // NOT if()
        lock.wait();
    }
    // proceed safely
}
```

### Q26. Why is `notify()` risky compared to `notifyAll()`, and when is `notify()` actually safe to use?
`notify()` wakes **only one arbitrary** waiting thread — if multiple different threads are waiting for **different conditions** on the same monitor, `notify()` might wake the "wrong" thread (one whose condition still isn't true), while the thread that *should* have been woken remains asleep indefinitely — a subtle, hard-to-reproduce bug. `notify()` is only safe when you're **certain** all waiting threads are interchangeable (waiting for the exact same condition, and only one can usefully proceed at a time). **Default to `notifyAll()`** unless you have a specific, well-understood reason and performance need for `notify()` (waking all costs more CPU/context-switching but is far safer).

---

## 7. Modern Java Concurrency (Java 8–21+)

### Q27. What are **Virtual Threads** (Project Loom, JEP 444, finalized in Java 21)? Why do they matter for backend services?
Virtual threads are **lightweight, JVM-managed threads** (not 1:1 mapped to OS threads like traditional `Thread`s) — the JVM can run **millions** of them, multiplexed over a small number of OS "carrier" threads. When a virtual thread performs a **blocking** operation (I/O, `Thread.sleep()`, blocking on a lock), the JVM **unmounts** it from its carrier OS thread (instead of blocking the OS thread), freeing that carrier to run other virtual threads — then **remounts** it when the blocking operation completes. This means you can write simple, traditional **blocking, thread-per-request** code style (easy to read/debug — no `CompletableFuture` chains needed) while getting the **scalability of async/non-blocking I/O** under the hood. Created via `Thread.ofVirtual()` or `Executors.newVirtualThreadPerTaskExecutor()`. Massive relevance for high-concurrency backend services (e.g., handling 100k+ concurrent connections) without rewriting business logic into reactive/async style.

### Q28. Do Virtual Threads make `synchronized` problematic? (A nuanced, up-to-date gotcha worth knowing)
Yes — historically (pre-Java 24-ish), a virtual thread blocked inside a `synchronized` block **pins** its carrier OS thread (cannot be unmounted) because the JVM's monitor implementation is tied to the OS thread's identity, unlike `ReentrantLock`/other `java.util.concurrent` blocking operations which are Loom-aware and support proper unmounting. This meant **heavy use of `synchronized` around blocking I/O could defeat the scalability benefit of virtual threads** — a real, current (as of recent JDKs) migration consideration when adopting virtual threads in legacy `synchronized`-heavy codebases; check current JDK release notes, as this pinning behavior has been actively improved across recent versions.

### Q29. `parallel()` Streams — when are they actually beneficial, and what are the common misuses?
Beneficial for **CPU-bound**, large-dataset, **stateless, independent** operations with low per-element overhead relative to splitting cost (e.g., large numeric computations on an array-backed source like `ArrayList`/arrays, which split efficiently). **Common misuses:** (1) using `.parallel()` on I/O-bound operations (blocks shared `ForkJoinPool` threads — see Q12); (2) using it on **small collections** where the splitting/merging/thread-coordination overhead exceeds any parallelism benefit; (3) using it on **poorly-splittable sources** like `LinkedList` or `Iterator`-based sources (no efficient random-access splitting, degrades to sequential-like performance anyway); (4) using it with **stateful lambda operations** (mutating shared external state from within `.map()`/`.forEach()`) — reintroduces exactly the race conditions parallelism is supposed to abstract away from you. Rule of thumb often cited: benchmark, don't assume — `.parallel()` is not a free performance button.

### Q30. What's the difference between `ThreadLocal` and just using a local variable — and what's the classic memory-leak trap with `ThreadLocal` in thread-pool-based servers?
`ThreadLocal<T>` gives each **thread** its own independent copy of a variable, useful for per-thread context (e.g., a request's transaction context, user session, `SimpleDateFormat` instances which aren't thread-safe, MDC logging context). **Classic trap in thread-pool environments** (like servlet containers/Spring apps): pooled threads are **reused** across many requests/tasks. If you set a `ThreadLocal` value during request A's processing and **forget to `remove()` it** at the end, the *next* request B — reusing the same pooled thread — will see request A's **stale leftover value**, a genuinely nasty, hard-to-reproduce cross-request data leakage bug (and a memory leak, since the value stays referenced by the thread indefinitely). **Always `try/finally { threadLocal.remove(); }`** when using `ThreadLocal` in a pooled-thread environment — a must-know production gotcha for any senior backend engineer.

---

## 8. Tricky / Rapid-Fire Questions

**Q31. Is `ArrayList` thread-safe for **reads only**, if no thread ever writes to it after publication?**
Technically yes, **if and only if** the list was fully constructed and its reference was safely published (e.g., via a `final` field, or `volatile`, or a happens-before-establishing mechanism like passing it through a `synchronized` block or thread-start) — without safe publication, even pure reads across threads risk seeing a partially-constructed object due to reordering (same underlying issue as Q22's DCL problem, generalized).

**Q32. Can two threads execute the same `synchronized` **instance** method on **different objects** concurrently?**
Yes — `synchronized` instance methods lock on `this`, a **per-instance** lock. Two different object instances have two different monitors, so calls on different instances never block each other, only concurrent calls on the **same** instance do.

**Q33. Why does `Collections.synchronizedList()` still require **manual external synchronization during iteration**, even though individual method calls are already synchronized?**
Because iteration is a **compound, multi-step operation** (`hasNext()` + `next()` repeated) — even though each individual call is internally synchronized, another thread could modify the list **between** your `hasNext()` and `next()` calls, causing `ConcurrentModificationException` or inconsistent iteration. You must wrap the entire iteration block in `synchronized(list) { ... }` externally (this exact requirement is explicitly documented in the JDK Javadoc for `Collections.synchronizedList`).

**Q34. What does `Thread.sleep()` do to a held lock? Does sleeping release it?**
`Thread.sleep()` does **NOT release any locks** held by the thread — the thread just pauses execution while continuing to hold every lock it currently owns. This is a very common trap: `synchronized(lock) { Thread.sleep(10000); }` blocks every other thread waiting on `lock` for the full 10 seconds, even though the sleeping thread isn't doing any real work. Contrast with `Object.wait()`, which **does** release the monitor while waiting (Q25).

**Q35. Is `HashMap.get()` guaranteed to see updates made by another thread's `HashMap.put()` without any explicit synchronization?**
No — with plain `HashMap` (not `ConcurrentHashMap`), there is **no happens-before relationship** established between an unsynchronized `put()` on one thread and a `get()` on another thread, so visibility is **not guaranteed** — the reading thread might see a stale/empty map indefinitely (in addition to the structural-corruption risks discussed in the Collections template Q13). This is exactly why `ConcurrentHashMap` (whose internal implementation establishes proper happens-before relationships via its synchronization/CAS operations) is required for genuinely concurrent access, not just "it happens to work in my testing."

**Q36. What's wrong with this producer-consumer implementation, and what's the fix?**
```java
class Buffer {
    private Queue<Integer> queue = new LinkedList<>();  // NOT thread-safe
    public void produce(int val) { queue.add(val); }
    public int consume() { return queue.poll(); }
}
```
Plain `LinkedList` isn't thread-safe, and there's no blocking/coordination when the queue is empty (consumer would get `null`/NPE-prone unboxing) or unbounded-growing when production outpaces consumption. **Fix:** use a `BlockingQueue` implementation (e.g., `LinkedBlockingQueue` with a bounded capacity) — `put()`/`take()` handle both thread-safety AND blocking (producer blocks if full, consumer blocks if empty) automatically, eliminating manual `wait()`/`notify()` coordination entirely.

**Q37. `Runnable` vs `Callable<V>` — beyond "one returns a value," what's the other key difference?**
`Callable.call()` can throw **checked exceptions**; `Runnable.run()` cannot (its signature has no `throws` clause) — directly connects back to the Exception Handling template's Q14 discussion of functional interfaces and checked exceptions. This is exactly why `ExecutorService.submit(Callable<V>)` returns a `Future<V>` that surfaces exceptions via `get()` throwing `ExecutionException` (wrapping the original), while a submitted `Runnable`'s uncaught exception instead goes to the thread's `UncaughtExceptionHandler` (or is silently lost if using `submit(Runnable)`, since that path also returns a `Future` but any exception is likewise captured and only surfaces via `.get()` — a subtle point worth clarifying if asked).

**Q38. Why can't you restart a `Thread` once it has completed (`Thread.start()` called twice)?**
Throws `IllegalThreadStateException` — a `Thread` object is a **one-shot** wrapper around a single OS-thread lifecycle (`NEW → RUNNABLE → ... → TERMINATED`); once `TERMINATED`, the internal state can't be reset. If you need repeated execution, submit new `Runnable`/`Callable` tasks to a **reusable** `ExecutorService`/thread pool instead of managing raw `Thread` objects directly — another good opportunity to steer toward "use higher-level `java.util.concurrent` abstractions, not raw `Thread`" as a general best-practice theme.

---

## 9. Scenario / System-Design-Flavored Questions

**Q39.** *"Your Spring Boot service's thread pool is maxing out under load, and requests are timing out. Walk through your diagnosis approach."*
> Expected: check thread pool metrics (active count vs max, queue depth) via actuator/JMX; take thread dumps (`jstack`) to identify if threads are **blocked** (waiting on locks/DB connections — contention issue) vs **actually busy computing** (genuine capacity issue) vs **stuck in blocking I/O** (undersized connection pool, slow downstream dependency). Discuss distinguishing a **thread pool sizing problem** from a **downstream latency problem** — increasing pool size doesn't help if the bottleneck is a slow DB; it may even make things worse by increasing load on the already-struggling downstream system. Mention circuit breakers (Resilience4j) as a complementary mitigation.

**Q40.** *"How would you safely share a mutable configuration object that's refreshed periodically (e.g., every 60s from a remote config service) across many request-handling threads, minimizing contention?"*
> Expected: rather than locking on every read (expensive for a read-heavy, rarely-updated object), use the **immutable-object + volatile-reference-swap** pattern — build a **new immutable config object** on refresh, then **atomically swap** a `volatile` (or `AtomicReference`) pointer to it; readers just read the current reference with zero locking (a single volatile read), writers only synchronize among themselves during the (rare) refresh. This is a very common, very real senior-level pattern question — connects `volatile`'s visibility guarantee (Q4) with immutability principles (String/Collections templates) in a practical design.

---

## 10. Cheat-Sheet: Visibility & Atomicity At a Glance

| Mechanism | Guarantees Visibility? | Guarantees Atomicity? | Blocking? |
|---|---|---|---|
| `volatile` | Yes | No (not for compound ops) | No |
| `synchronized` | Yes | Yes (within the block) | Yes |
| `ReentrantLock` | Yes | Yes (within lock/unlock) | Yes (or tryLock/timed) |
| `AtomicInteger`/`AtomicReference` (CAS) | Yes | Yes (single operation) | No (lock-free, spin-retry) |
| Plain field (no modifier) | **No guarantee** | No | No |
| `final` field (properly published) | Yes (post-construction) | N/A | No |

---

### How to use this file
- Section 6 (Q20–Q26: deadlock, DCL, wait/notify) is the highest-yield **live-coding + whiteboard** area — expect to be asked to spot or fix a deadlock, or explain DCL, on the spot.
- Section 2 (JMM/happens-before) is the conceptual foundation interviewers use to gauge real depth vs memorized API trivia — be ready to explain **why**, not just **what**.
- Section 7 (Virtual Threads) signals current, up-to-date knowledge — proactively mention it if the conversation touches scalability/thread-per-request design.
