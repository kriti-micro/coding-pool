Below is a comprehensive **Java 8 Threading & Multithreading interview FAQ** aimed at an **8-year-experience Java/backend engineer**. I’ve arranged it from fundamentals → synchronization → concurrency utilities → thread pools → `CompletableFuture` → advanced interview scenarios.

# Java 8 Threading & Multithreading – Interview FAQ

# Table of Contents

1. [Level 1 – Thread Basics & Synchronization](#level-1--thread-basics--synchronization)

    * [1. Thread-Safe Counter](#1-thread-safe-counter)
    * [2. Fix a Race Condition](#2-fix-a-race-condition)
    * [3. Bank Account Withdrawal](#3-bank-account-withdrawal)
    * [4. Thread-Safe Ticket Booking](#4-thread-safe-ticket-booking)
    * [5. Print Numbers Alternately](#5-print-numbers-alternately)
    * [6. Print ABC Using Three Threads](#6-print-abc-using-three-threads)
    * [7. Thread-Safe Singleton](#7-thread-safe-singleton)
    * [8. Lazy Initialization](#8-lazy-initialization)
    * [9. Thread-Safe Cache](#9-thread-safe-cache)
    * [10. Thread-Safe Lazy Cache Loading](#10-thread-safe-lazy-cache-loading)

2. [Level 2 – ExecutorService & Thread Pools](#level-2--executorservice--thread-pools)

    * [11. Execute 100 Tasks Using Only 5 Threads](#11-execute-100-tasks-using-only-5-threads)
    * [12. Return Results From Multiple Tasks](#12-return-results-from-multiple-tasks)
    * [13. Find the Maximum From Multiple Tasks](#13-find-the-maximum-from-multiple-tasks)
    * [14. Handle Task Exceptions](#14-handle-task-exceptions)
    * [15. execute() vs submit() Coding Scenario](#15-execute-vs-submit-coding-scenario)
    * [16. Graceful Executor Shutdown](#16-graceful-executor-shutdown)
    * [17. Custom ThreadPoolExecutor](#17-custom-threadpoolexecutor)
    * [18. Custom RejectedExecutionHandler](#18-custom-rejectedexecutionhandler)
    * [19. Prevent Thread Pool Exhaustion](#19-prevent-thread-pool-exhaustion)
    * [20. Separate CPU and I/O Thread Pools](#20-separate-cpu-and-io-thread-pools)

3. [Level 3 – Concurrent Collections](#level-3--concurrent-collections)

    * [21. Word Frequency Counter](#21-word-frequency-counter)
    * [22. Concurrent URL Counter](#22-concurrent-url-counter)
    * [23. Thread-Safe Queue](#23-thread-safe-queue)
    * [24. Bounded Producer-Consumer](#24-bounded-producer-consumer)
    * [25. Multiple Producers and Consumers](#25-multiple-producers-and-consumers)
    * [26. CopyOnWriteArrayList Scenario](#26-copyonwritearraylist-scenario)
    * [27. ConcurrentHashMap Atomic Update](#27-concurrenthashmap-atomic-update)
    * [28. Thread-Safe Cache With Expiration](#28-thread-safe-cache-with-expiration)

4. [Level 4 – Synchronization Utilities](#level-4--synchronization-utilities)

    * [29. Application Startup Dependency](#29-application-startup-dependency)
    * [30. Parallel Service Initialization](#30-parallel-service-initialization)
    * [31. Cyclic Processing](#31-cyclic-processing)
    * [32. Limited Database Connections](#32-limited-database-connections)
    * [33. API Concurrency Limiter](#33-api-concurrency-limiter)
    * [34. Timeout While Waiting for Resource](#34-timeout-while-waiting-for-resource)

5. [Level 5 – CompletableFuture](#level-5--completablefuture)

    * [35. Parallel API Calls](#35-parallel-api-calls)
    * [36. Combine Two API Results](#36-combine-two-api-results)
    * [37. Sequential Dependent API Calls](#37-sequential-dependent-api-calls)
    * [38. Transform API Result](#38-transform-api-result)
    * [39. CompletableFuture Exception Handling](#39-completablefuture-exception-handling)
    * [40. Different Handling for Success and Failure](#40-different-handling-for-success-and-failure)
    * [41. Logging Completion](#41-logging-completion)
    * [42. Timeout for CompletableFuture](#42-timeout-for-completablefuture)
    * [43. Combine 5 Independent APIs](#43-combine-5-independent-apis)
    * [44. First Successful API Response](#44-first-successful-api-response)
    * [45. Dedicated Executor for Blocking APIs](#45-dedicated-executor-for-blocking-apis)

6. [Level 6 – Advanced Senior-Level Scenarios](#level-6--advanced-senior-level-scenarios)

    * [46. Deadlock Detection / Prevention](#46-deadlock-detection--prevention)
    * [47. ThreadLocal Request Context](#47-threadlocal-request-context)
    * [48. Build a Simple Rate Limiter](#48-build-a-simple-rate-limiter)
    * [49. Design a Concurrent Task Processor](#49-design-a-concurrent-task-processor)
    * [50. Production Incident – Thread Pool Exhaustion](#50-production-incident--thread-pool-exhaustion)

7. [Interview Practice Format](#interview-practice-format)

    * [Example](#example)

8. [Priority for 8-Year Java Interviews](#priority-for-8-year-java-interviews)

    * [🔴 Must Practice](#-must-practice)
    * [🟠 Important](#-important)
    * [🟢 Advanced / Follow-Up](#-advanced--follow-up)

9. [The 10 Most Important Coding Patterns to Memorize](#the-10-most-important-coding-patterns-to-memorize)

    * [Pattern 1 – Atomic Counter](#pattern-1--atomic-counter)
    * [Pattern 2 – Synchronized Critical Section](#pattern-2--synchronized-critical-section)
    * [Pattern 3 – ReentrantLock](#pattern-3--reentrantlock)
    * [Pattern 4 – ExecutorService](#pattern-4--executorservice)
    * [Pattern 5 – BlockingQueue](#pattern-5--blockingqueue)
    * [Pattern 6 – Semaphore](#pattern-6--semaphore)
    * [Pattern 7 – CountDownLatch](#pattern-7--countdownlatch)
    * [Pattern 8 – CompletableFuture](#pattern-8--completablefuture)
    * [Pattern 9 – Combine Futures](#pattern-9--combine-futures)
    * [Pattern 10 – ThreadLocal Cleanup](#pattern-10--threadlocal-cleanup)



# 50 Java 8 Threading & Multithreading Scenario-Based Coding Questions

## Level 1 – Thread Basics & Synchronization

### 1. Thread-Safe Counter

**Scenario:**
100 threads concurrently increment a shared counter 1,000 times each.

**Requirement:**
The final count must be exactly `100000`.

**Question:**
Write the code using:

1. `synchronized`
2. `AtomicInteger`

**Concepts:**
Race condition, atomicity, synchronization, CAS.

---

### 2. Fix a Race Condition

Given:

```java
class Counter {

    private int count = 0;

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
```

Multiple threads call `increment()`.

**Question:**
Why can the final result be incorrect? Fix the code without using `AtomicInteger`.

**Concepts:**
Race condition, critical section, `synchronized`.

---

### 3. Bank Account Withdrawal

Multiple threads are withdrawing money from:

```java
class Account {
    private int balance = 10000;
}
```

Two threads simultaneously attempt:

```text
withdraw(7000)
withdraw(7000)
```

**Requirement:**
Balance must never become negative.

**Question:**
Implement a thread-safe `withdraw()` method.

**Concepts:**
Atomicity, synchronization.

---

### 4. Thread-Safe Ticket Booking

A movie theater has:

```text
100 seats
```

Multiple threads attempt to book seats simultaneously.

**Question:**
Implement:

```java
bookSeat(int seatNumber)
```

such that two threads cannot book the same seat.

**Follow-up:**
Would you synchronize the entire method or only part of it? Why?

**Concepts:**
Critical section, lock granularity.

---

### 5. Print Numbers Alternately

Two threads should print:

```text
1 2 3 4 5 6 7 8 9 10
```

Thread A prints odd numbers.

Thread B prints even numbers.

Expected:

```text
Thread-A -> 1
Thread-B -> 2
Thread-A -> 3
Thread-B -> 4
...
```

**Question:**
Implement using:

```text
wait()
notify()
```

**Concepts:**
Thread coordination, monitor, `wait/notify`.

---

### 6. Print ABC Using Three Threads

Three threads should print:

```text
ABCABCABCABC
```

Thread 1:

```text
A
```

Thread 2:

```text
B
```

Thread 3:

```text
C
```

**Question:**
Implement using synchronization.

**Follow-up:**
Implement using `Semaphore`.

---

### 7. Thread-Safe Singleton

Create a Singleton that is safe when multiple threads call:

```java
Singleton.getInstance();
```

simultaneously.

**Question:**
Implement using:

1. `synchronized`
2. Double-checked locking
3. `enum`

**Concepts:**
Synchronization, `volatile`, singleton pattern.

---

### 8. Lazy Initialization

You have:

```java
class ConfigManager {

    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        // implement
    }
}
```

**Question:**
Make initialization lazy and thread-safe.

**Follow-up:**
Why is `volatile` required in double-checked locking?

---

### 9. Thread-Safe Cache

Implement:

```java
class Cache {

    void put(String key, String value);

    String get(String key);
}
```

Multiple threads can read/write simultaneously.

**Question:**
Implement using:

```java
ConcurrentHashMap
```

**Follow-up:**
Why is `HashMap` not sufficient?

---

### 10. Thread-Safe Lazy Cache Loading

Implement:

```java
String getUser(String userId)
```

If user is not in cache:

```text
Call database
Store result
Return result
```

**Problem:**
Multiple threads requesting the same user should not unnecessarily load the same user simultaneously.

**Question:**
Use `ConcurrentHashMap.computeIfAbsent()`.

---

# Level 2 – ExecutorService & Thread Pools

### 11. Execute 100 Tasks Using Only 5 Threads

You have:

```text
100 tasks
```

Each task takes approximately 1 second.

**Requirement:**

```text
Maximum 5 worker threads
```

**Question:**
Implement using `ExecutorService`.

---

### 12. Return Results From Multiple Tasks

Submit 10 `Callable<Integer>` tasks.

Each returns a number.

**Question:**
Collect all results using:

```java
Future<Integer>
```

**Follow-up:**
What happens when you call `future.get()`?

---

### 13. Find the Maximum From Multiple Tasks

Split a large array into 4 parts.

Each thread finds the maximum of its part.

Then combine the results.

Example:

```text
Array:
[10, 4, 20, 7, 30, 2, 50, 1]
```

Expected:

```text
50
```

**Concepts:**
Callable, Future, ExecutorService.

---

### 14. Handle Task Exceptions

Submit 10 tasks.

One task throws:

```java
RuntimeException
```

**Question:**
How do you detect which task failed?

Implement using:

```java
Future
```

---

### 15. `execute()` vs `submit()` Coding Scenario

Create a task that throws:

```java
throw new RuntimeException("Failed");
```

Run it using:

```java
executor.execute(task);
```

Then run it using:

```java
executor.submit(task);
```

**Question:**
Observe and explain the difference in exception handling.

---

### 16. Graceful Executor Shutdown

Create a service that processes tasks continuously.

When shutdown is requested:

1. Stop accepting new tasks.
2. Finish existing tasks.
3. Wait for termination.
4. Force shutdown if timeout expires.

**Question:**
Implement this correctly.

**Concepts:**

```java
shutdown()
awaitTermination()
shutdownNow()
```

---

### 17. Custom ThreadPoolExecutor

Create a thread pool with:

```text
corePoolSize = 2
maximumPoolSize = 5
queue capacity = 10
```

**Question:**
Implement using `ThreadPoolExecutor`.

**Follow-up:**
What happens when more than 15 tasks arrive?

---

### 18. Custom RejectedExecutionHandler

A thread pool becomes overloaded.

**Requirement:**
Instead of silently losing the task, log:

```text
Task rejected
```

and store the rejected task somewhere.

**Question:**
Implement a custom `RejectedExecutionHandler`.

---

### 19. Prevent Thread Pool Exhaustion

You have:

```text
Thread pool = 20
```

Each task calls an external API.

The API sometimes takes 60 seconds.

**Question:**
Design the code so one slow API doesn't permanently occupy threads.

Discuss:

* Timeout
* Cancellation
* Separate executor
* Queue size

---

### 20. Separate CPU and I/O Thread Pools

Your application performs:

```text
CPU-intensive calculation
External API calls
```

**Question:**
Create separate executors:

```text
CPU executor
I/O executor
```

and explain why using one pool for both can be problematic.

---

# Level 3 – Concurrent Collections

### 21. Word Frequency Counter

Multiple threads process millions of words.

Calculate:

```text
Java -> 100
Spring -> 75
Cloud -> 50
```

**Question:**
Implement using:

```java
ConcurrentHashMap
```

**Follow-up:**
How would you atomically update the count?

Hint:

```java
merge()
```

---

### 22. Concurrent URL Counter

Multiple worker threads process web logs.

Count how many times each URL appears.

Example:

```text
/api/user
/api/order
/api/user
```

Expected:

```text
/api/user -> 2
/api/order -> 1
```

**Question:**
Implement using `ConcurrentHashMap`.

---

### 23. Thread-Safe Queue

Implement a producer-consumer application.

Producer generates:

```text
Order
```

Consumer processes orders.

**Question:**
Use:

```java
BlockingQueue<Order>
```

---

### 24. Bounded Producer-Consumer

Queue capacity:

```text
100
```

If queue is full, producer must wait.

If queue is empty, consumer must wait.

**Question:**
Implement without manually using `wait()` / `notify()`.

**Expected:**
Use `ArrayBlockingQueue`.

---

### 25. Multiple Producers and Consumers

Create:

```text
3 producers
5 consumers
```

using a shared:

```java
BlockingQueue
```

**Question:**
Ensure safe processing without losing orders.

---

### 26. CopyOnWriteArrayList Scenario

A list contains registered event listeners.

Characteristics:

```text
10,000 reads/sec
1 write/minute
```

**Question:**
Which collection would you choose?

Implement using:

```java
CopyOnWriteArrayList
```

**Follow-up:**
Why isn't it suitable for frequent writes?

---

### 27. ConcurrentHashMap Atomic Update

Given:

```java
ConcurrentHashMap<String, Integer> map;
```

Implement:

```java
increment("Java");
```

without:

```java
get()
put()
```

because that sequence is not atomic.

**Expected:**

```java
map.merge("Java", 1, Integer::sum);
```

---

### 28. Thread-Safe Cache With Expiration

Implement a cache:

```java
put(key, value, expiryTime)

get(key)
```

Multiple threads may access it.

**Question:**
Design the synchronization strategy.

**Follow-up:**
Would you use `ConcurrentHashMap` alone?

---

# Level 4 – Synchronization Utilities

### 29. Application Startup Dependency

Your application has 5 components:

```text
Database
Cache
Kafka
Config
External API
```

Application should start only after all 5 are initialized.

**Question:**
Implement using:

```java
CountDownLatch
```

---

### 30. Parallel Service Initialization

Start:

```text
Service A
Service B
Service C
```

in parallel.

Main thread must wait until all complete.

**Question:**
Implement using `CountDownLatch`.

---

### 31. Cyclic Processing

Three workers process data in stages.

They must all finish Stage 1 before any begins Stage 2.

```text
Worker A -> Stage 1
Worker B -> Stage 1
Worker C -> Stage 1

          Barrier

Worker A -> Stage 2
Worker B -> Stage 2
Worker C -> Stage 2
```

**Question:**
Implement using `CyclicBarrier`.

---

### 32. Limited Database Connections

Your application allows only:

```text
5 concurrent database-heavy operations
```

There may be 100 worker threads.

**Question:**
Use:

```java
Semaphore
```

to limit concurrent operations to 5.

---

### 33. API Concurrency Limiter

An external API allows a maximum of:

```text
10 concurrent requests
```

**Question:**
Implement an API wrapper using `Semaphore`.

---

### 34. Timeout While Waiting for Resource

A thread should wait at most:

```text
2 seconds
```

for a resource.

**Question:**
Use:

```java
tryAcquire(2, TimeUnit.SECONDS)
```

Implement the complete code.

---

# Level 5 – CompletableFuture

### 35. Parallel API Calls

You need:

```text
User API
Account API
Portfolio API
```

All are independent.

**Question:**
Call all three in parallel using `CompletableFuture`.

---

### 36. Combine Two API Results

Call:

```text
getUser()
getAccount()
```

Then create:

```text
UserAccountResponse
```

containing both results.

**Question:**
Use:

```java
thenCombine()
```

---

### 37. Sequential Dependent API Calls

First call:

```text
getUser()
```

Then using the returned user ID:

```text
getOrders(userId)
```

**Question:**
Implement using:

```java
thenCompose()
```

---

### 38. Transform API Result

Call:

```java
getUser()
```

which returns:

```java
User
```

Transform it into:

```java
UserDTO
```

**Question:**
Use:

```java
thenApply()
```

---

### 39. CompletableFuture Exception Handling

External API sometimes fails.

Return:

```text
default response
```

instead of failing the entire operation.

**Question:**
Implement using:

```java
exceptionally()
```

---

### 40. Different Handling for Success and Failure

You need:

```text
Success -> transform response
Failure -> return fallback
```

**Question:**
Implement using:

```java
handle()
```

---

### 41. Logging Completion

You don't want to change the result.

You only want to log:

```text
SUCCESS
FAILED
```

**Question:**
Use:

```java
whenComplete()
```

---

### 42. Timeout for CompletableFuture

Call:

```text
external API
```

If it doesn't complete within a specified timeout:

```text
return fallback
```

**Question:**
For Java 8 specifically, how would you implement timeout behavior when `orTimeout()` is unavailable?

**Expected discussion:**

```text
ScheduledExecutorService
CompletableFuture
completeExceptionally()
```

---

### 43. Combine 5 Independent APIs

Call:

```text
Customer API
Account API
Transaction API
Card API
Investment API
```

in parallel.

**Requirement:**
Continue only after all are complete.

**Question:**
Use:

```java
CompletableFuture.allOf()
```

and collect all results.

---

### 44. First Successful API Response

You have:

```text
Server A
Server B
Server C
```

All provide the same data.

**Requirement:**
Return the first completed response.

**Question:**
Implement using:

```java
CompletableFuture.anyOf()
```

**Follow-up:**
What is the difference between "first completed" and "first successful"?

---

### 45. Dedicated Executor for Blocking APIs

You have:

```java
CompletableFuture.supplyAsync(() -> callBlockingAPI());
```

The API takes several seconds.

**Question:**
Why might using the default executor be problematic?

Modify the code to use:

```java
ExecutorService
```

dedicated to blocking I/O.

---

# Level 6 – Advanced Senior-Level Scenarios

### 46. Deadlock Detection / Prevention

Two services acquire:

```text
Lock A
Lock B
```

Current code:

```java
Thread 1:
Lock A -> Lock B

Thread 2:
Lock B -> Lock A
```

**Question:**

1. Explain how deadlock occurs.
2. Fix the code.
3. Implement consistent lock ordering.
4. Explain how `tryLock()` can help.

---

### 47. ThreadLocal Request Context

Each incoming request has:

```text
requestId
userId
```

You want these values available throughout service methods without passing them as parameters.

**Question:**
Implement using:

```java
ThreadLocal<RequestContext>
```

**Important follow-up:**
How do you clean it when using a thread pool?

Expected:

```java
try {
    context.set(...);
    // request processing
} finally {
    context.remove();
}
```

---

### 48. Build a Simple Rate Limiter

Requirement:

```text
Maximum 5 concurrent operations
```

Additional requirement:

```text
Requests waiting longer than 2 seconds should fail.
```

**Question:**
Implement using:

```java
Semaphore
tryAcquire()
```

---

### 49. Design a Concurrent Task Processor

You receive:

```text
10,000 jobs
```

Each job:

```text
1. Reads database
2. Calls external API
3. Updates database
```

Requirements:

```text
Maximum 20 concurrent jobs
Maximum 5 concurrent DB operations
Maximum 10 concurrent API calls
```

**Question:**
Design the concurrency architecture.

Expected components:

```text
                 Job Producer
                      |
                      v
                BlockingQueue
                      |
                      v
               Worker Pool (20)
                 /          \
                /            \
       DB Semaphore       API Semaphore
          (5)                (10)
           |                  |
           v                  v
       Database          External API
```

Explain:

* Thread pool size
* Queue capacity
* Semaphore placement
* Timeouts
* Retry strategy
* Graceful shutdown
* Exception handling

---

### 50. Production Incident – Thread Pool Exhaustion

Your Spring Boot application suddenly becomes slow.

Metrics show:

```text
CPU: 40%
Memory: 55%

Thread pool:
Active = 100
Queue = 5000

Database:
Connections = 20/20

External API:
Average latency = 15 sec
```

Requests are timing out.

**Question:**
What would you investigate?

Your answer should cover:

```text
1. Thread dump
2. Blocked/waiting threads
3. Database connection pool
4. External API latency
5. Thread pool configuration
6. Queue size
7. Timeout configuration
8. Retry configuration
9. Connection leaks
10. Backpressure
11. Circuit breaker
12. Separate CPU/I/O pools
```

### Expected Architecture

```text
                 Incoming Requests
                         |
                         v
                  API / Controller
                         |
                         v
                  Bounded Queue
                         |
                         v
              +----------------------+
              | Worker Thread Pool   |
              |        20            |
              +----------------------+
                   /            \
                  /              \
                 v                v
          DB Semaphore       API Semaphore
             (5)                (10)
              |                   |
              v                   v
          Database          External API
              |                   |
              +---------+---------+
                        |
                        v
                   Response
```

---

# Interview Practice Format

For each of these questions, practice answering in this order:

```text
1. Identify the concurrency problem
          ↓
2. Identify shared state
          ↓
3. Decide whether you need
   visibility / atomicity / ordering
          ↓
4. Select the mechanism
          ↓
5. Write the code
          ↓
6. Explain why it is thread-safe
          ↓
7. Discuss failure scenarios
          ↓
8. Discuss performance
          ↓
9. Discuss production concerns
```

## Example

For:

> "100 threads increment a counter."

Don't immediately say:

```text
Use synchronized.
```

Think:

```text
Shared mutable state?
        YES
           ↓
Race condition?
        YES
           ↓
Need atomic increment?
        YES
           ↓
Possible solutions:
   synchronized
   AtomicInteger
           ↓
Which is simpler here?
   AtomicInteger
```

Then explain the trade-off.

---

# Priority for 8-Year Java Interviews

## 🔴 Must Practice

Questions:

```text
1   Counter
3   Bank Account
4   Ticket Booking
5   Odd/Even
11  ExecutorService
12  Future
17  ThreadPoolExecutor
21  ConcurrentHashMap
23  BlockingQueue
24  Producer-Consumer
29  CountDownLatch
32  Semaphore
35  Parallel APIs
36  thenCombine
37  thenCompose
39  exceptionally
43  allOf
45  Dedicated Executor
46  Deadlock
47  ThreadLocal
49  Concurrent Task Processor
50  Production Thread Pool
```

## 🟠 Important

```text
6   ABC printing
7   Singleton
10  computeIfAbsent
18  RejectedExecutionHandler
26  CopyOnWriteArrayList
31  CyclicBarrier
33  API limiter
40  handle
41  whenComplete
42  CompletableFuture timeout
44  anyOf
48  Rate limiter
```

## 🟢 Advanced / Follow-Up

```text
8   Double-checked locking
19  Thread-pool exhaustion
20  CPU vs I/O pools
22  Concurrent URL counter
25  Multiple producers/consumers
27  Atomic merge
28  Expiring cache
30  Parallel initialization
34  Timed resource acquisition
38  thenApply
```

# The 10 Most Important Coding Patterns to Memorize

### Pattern 1 – Atomic Counter

```java
AtomicInteger counter = new AtomicInteger();

counter.incrementAndGet();
```

### Pattern 2 – Synchronized Critical Section

```java
synchronized (lock) {
    // shared state
}
```

### Pattern 3 – ReentrantLock

```java
lock.lock();

try {
    // critical section
} finally {
    lock.unlock();
}
```

### Pattern 4 – ExecutorService

```java
ExecutorService executor =
        Executors.newFixedThreadPool(10);

executor.submit(task);

executor.shutdown();
```

### Pattern 5 – BlockingQueue

```java
queue.put(item);

Item item = queue.take();
```

### Pattern 6 – Semaphore

```java
semaphore.acquire();

try {
    // limited resource
} finally {
    semaphore.release();
}
```

### Pattern 7 – CountDownLatch

```java
latch.countDown();

latch.await();
```

### Pattern 8 – CompletableFuture

```java
CompletableFuture
    .supplyAsync(() -> getData())
    .thenApply(data -> process(data))
    .exceptionally(ex -> fallback());
```

### Pattern 9 – Combine Futures

```java
future1.thenCombine(
    future2,
    (a, b) -> combine(a, b)
);
```

### Pattern 10 – ThreadLocal Cleanup

```java
try {

    threadLocal.set(context);

    // process request

} finally {

    threadLocal.remove();
}
```

These 10 patterns cover a large portion of the practical Java concurrency coding questions you are likely to encounter at an experienced backend-engineer level.
