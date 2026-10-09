Below is a comprehensive **Java 8 Threading & Multithreading interview FAQ** aimed at an **8-year-experience Java/backend engineer**. I’ve arranged it from fundamentals → synchronization → concurrency utilities → thread pools → `CompletableFuture` → advanced interview scenarios.

# Java 8 Threading & Multithreading – Interview FAQ

## Table of Contents

1. [What is a Thread?](#1-what-is-a-thread)
2. [Process vs Thread](#2-process-vs-thread)
3. [Why do we need Multithreading?](#3-why-do-we-need-multithreading)
4. [How do you create a Thread in Java?](#4-how-do-you-create-a-thread-in-java)
5. [Thread Lifecycle](#5-thread-lifecycle)
6. [start() vs run()](#6-start-vs-run)
7. [Can we call start() twice?](#7-can-we-call-start-twice)
8. [Thread.sleep()](#8-threadsleep)
9. [sleep() vs wait()](#9-sleep-vs-wait)
10. [wait(), notify(), notifyAll()](#10-wait-notify-notifyall)
11. [Why wait(), notify(), notifyAll() belong to Object](#11-why-wait-notify-notifyall-belong-to-object)
12. [join()](#12-join)
13. [Daemon vs Non-Daemon Threads](#13-daemon-vs-non-daemon-threads)
14. [Thread Priority](#14-thread-priority)
15. [Race Condition](#15-race-condition)
16. [Thread Safety](#16-thread-safety)
17. [Critical Section](#17-critical-section)
18. [synchronized keyword](#18-synchronized-keyword)
19. [Synchronized Method vs Block](#19-synchronized-method-vs-block)
20. [Object Lock vs Class Lock](#20-object-lock-vs-class-lock)
21. [Can two synchronized methods run simultaneously?](#21-can-two-synchronized-methods-run-simultaneously)
22. [static synchronized](#22-static-synchronized)
23. [volatile](#23-volatile)
24. [volatile vs synchronized](#24-volatile-vs-synchronized)
25. [Atomic Classes](#25-atomic-classes)
26. [CAS – Compare And Set](#26-cas--compare-and-set)
27. [Lock Interface](#27-lock-interface)
28. [synchronized vs Lock](#28-synchronized-vs-lock)
29. [ReentrantLock](#29-reentrantlock)
30. [tryLock()](#30-trylock)
31. [ReadWriteLock](#31-readwritelock)
32. [ReentrantReadWriteLock](#32-reentrantreadwritelock)
33. [StampedLock](#33-stampedlock)
34. [Thread Interruption](#34-thread-interruption)
35. [Deadlock](#35-deadlock)
36. [How to prevent Deadlock](#36-how-to-prevent-deadlock)
37. [Livelock](#37-livelock)
38. [Starvation](#38-starvation)
39. [Executor Framework](#39-executor-framework)
40. [Executor vs ExecutorService](#40-executor-vs-executorservice)
41. [submit() vs execute()](#41-submit-vs-execute)
42. [Callable vs Runnable](#42-callable-vs-runnable)
43. [Future](#43-future)
44. [ThreadPoolExecutor](#44-threadpoolexecutor)
45. [Core Pool Size vs Maximum Pool Size](#45-core-pool-size-vs-maximum-pool-size)
46. [Queue in ThreadPoolExecutor](#46-queue-in-threadpoolexecutor)
47. [Types of Thread Pools](#47-types-of-thread-pools)
48. [shutdown() vs shutdownNow()](#48-shutdown-vs-shutdownnow)
49. [ScheduledExecutorService](#49-scheduledexecutorservice)
50. [ForkJoinPool](#50-forkjoinpool)
51. [Work Stealing](#51-work-stealing)
52. [Concurrent Collections](#52-concurrent-collections)
53. [HashMap vs ConcurrentHashMap](#53-hashmap-vs-concurrenthashmap)
54. [ConcurrentHashMap internals](#54-concurrenthashmap-internals)
55. [CopyOnWriteArrayList](#55-copyonwritearraylist)
56. [BlockingQueue](#56-blockingqueue)
57. [Producer-Consumer Problem](#57-producer-consumer-problem)
58. [CountDownLatch](#58-countdownlatch)
59. [CyclicBarrier](#59-cyclicbarrier)
60. [Semaphore](#60-semaphore)
61. [CountDownLatch vs CyclicBarrier](#61-countdownlatch-vs-cyclicbarrier)
62. [Semaphore vs CountDownLatch](#62-semaphore-vs-countdownlatch)
63. [ThreadLocal](#63-threadlocal)
64. [InheritableThreadLocal](#64-inheritablethreadlocal)
65. [CompletableFuture](#65-completablefuture)
66. [thenApply vs thenCompose vs thenCombine](#66-thenapply-vs-thencompose-vs-thencombine)
67. [thenAccept vs thenRun](#67-thenaccept-vs-thenrun)
68. [exceptionally vs handle vs whenComplete](#68-exceptionally-vs-handle-vs-whencomplete)
69. [CompletableFuture Exception Handling](#69-completablefuture-exception-handling)
70. [Common ForkJoinPool and CompletableFuture](#70-common-forkjoinpool-and-completablefuture)
71. [Parallel Streams](#71-parallel-streams)
72. [Parallel Stream vs CompletableFuture](#72-parallel-stream-vs-completablefuture)
73. [Memory Visibility](#73-memory-visibility)
74. [Happens-Before Relationship](#74-happens-before-relationship)
75. [Atomicity vs Visibility vs Ordering](#75-atomicity-vs-visibility-vs-ordering)
76. [False Sharing](#76-false-sharing)
77. [Context Switching](#77-context-switching)
78. [Thread Dump](#78-thread-dump)
79. [How to troubleshoot Deadlock](#79-how-to-troubleshoot-deadlock)
80. [How to design a thread-safe class](#80-how-to-design-a-thread-safe-class)
81. [Common 8-Year Interview Scenarios](#81-common-8-year-interview-scenarios)
82. [Rapid-Fire Interview Questions](#82-rapid-fire-interview-questions)

---

# 1. What is a Thread?

A **thread is the smallest unit of execution within a process**.

A Java application can have multiple threads executing concurrently.

### Example

```java
public class Demo {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            System.out.println("Running in: "
                    + Thread.currentThread().getName());
        });

        thread.start();
    }
}
```

Possible output:

```text
Running in: Thread-0
```

### Important

Threads inside the same process share:

* Heap memory
* Static variables
* Objects

But each thread has its own:

* Stack
* Program counter
* Local variables

### Diagram

```text
                 Java Process
                     |
        +------------+------------+
        |            |            |
     Thread-1     Thread-2     Thread-3
        |            |            |
     Stack        Stack        Stack
        |            |            |
        +------------+------------+
                     |
                    Heap
              Shared Objects
```

---

# 2. Process vs Thread

| Process                            | Thread                            |
| ---------------------------------- | --------------------------------- |
| Independent execution unit         | Lightweight execution unit        |
| Has its own memory space           | Shares process memory             |
| More expensive                     | Less expensive                    |
| Process communication is expensive | Thread communication is easier    |
| Failure generally isolated         | One bad thread can affect process |

Example:

```text
Browser
  |
  +-- Process
       |
       +-- UI Thread
       +-- Network Thread
       +-- Rendering Thread
```

---

# 3. Why do we need Multithreading?

Multithreading allows multiple tasks to execute concurrently.

Example backend request:

```text
User Request
     |
     +---- Call DB
     |
     +---- Call Payment API
     |
     +---- Call Notification API
```

Sequential:

```text
DB        2 sec
Payment   3 sec
Email     1 sec

Total = 6 sec
```

Parallel:

```text
DB        2 sec ─────┐
Payment   3 sec ─────┤
Email     1 sec ─────┘

Total ≈ 3 sec
```

This is especially useful for independent I/O operations.

---

# 4. How do you create a Thread in Java?

There are two traditional approaches.

## Approach 1: Extend Thread

```java
class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("Task running");
    }
}

public class Main {

    public static void main(String[] args) {

        MyThread thread = new MyThread();

        thread.start();
    }
}
```

## Approach 2: Implement Runnable

```java
class MyTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Task running");
    }
}

public class Main {

    public static void main(String[] args) {

        Thread thread = new Thread(new MyTask());

        thread.start();
    }
}
```

### Which is preferred?

Usually:

```java
Runnable
```

because Java supports single inheritance.

Better separation:

```text
Task
 |
 | Runnable
 |
Thread
 |
Execution
```

The task describes **what to do**.

The thread describes **where/how it executes**.

---

# 5. Thread Lifecycle

Java thread states are:

```text
NEW
 |
 | start()
 v
RUNNABLE
 |
 +----------------+
 |                |
 v                |
BLOCKED           |
 |                |
 v                |
RUNNABLE <--------+
 |
 +--> WAITING
 |
 +--> TIMED_WAITING
 |
 v
TERMINATED
```

Official states:

```java
Thread.State
```

include:

```text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

### Important interview point

Java does not have a separate `RUNNING` state in `Thread.State`.

A Java thread executing or ready to execute is represented by:

```text
RUNNABLE
```

---

# 6. start() vs run()

This is a very common interview question.

## start()

```java
thread.start();
```

Creates a new thread of execution.

## run()

```java
thread.run();
```

Simply executes the method in the **current thread**.

### Example

```java
Thread t = new Thread(() ->
        System.out.println(Thread.currentThread().getName())
);

t.run();
```

Output:

```text
main
```

But:

```java
t.start();
```

Output:

```text
Thread-0
```

### Diagram

```text
start()
  |
  v
JVM creates/schedules new thread
  |
  v
run()
  |
  v
Task executes


run() directly
  |
  v
Current thread
  |
  v
Task executes
```

---

# 7. Can we call start() twice?

No.

```java
Thread t = new Thread(() -> {
    System.out.println("Hello");
});

t.start();
t.start();
```

Second call throws:

```text
java.lang.IllegalThreadStateException
```

A Thread object cannot be restarted once terminated.

---

# 8. Thread.sleep()

```java
Thread.sleep(1000);
```

Pauses the current thread for approximately 1 second.

Important:

```text
sleep()
```

does **not release an intrinsic lock**.

Example:

```java
synchronized(lock) {

    Thread.sleep(5000);

}
```

The thread sleeps while still holding `lock`.

---

# 9. sleep() vs wait()

| sleep()                                  | wait()                             |
| ---------------------------------------- | ---------------------------------- |
| Thread method                            | Object method                      |
| Does not release lock                    | Releases lock                      |
| Used for delay                           | Used for coordination              |
| Can be called without synchronized block | Must own object's monitor          |
| Wakes after specified time               | Wakes via notify/notifyAll/timeout |

Example:

```java
Thread.sleep(1000);
```

versus:

```java
synchronized (lock) {
    lock.wait();
}
```

### Key interview sentence

> `sleep()` is generally used for timing, while `wait()` is used for thread coordination.

---

# 10. wait(), notify(), notifyAll()

These are used for communication between threads.

Example:

```java
synchronized (lock) {

    lock.wait();

}
```

Another thread:

```java
synchronized (lock) {

    lock.notify();

}
```

### Diagram

```text
Thread A
   |
   | wait()
   v
WAITING
   |
   | notify()
   v
Runnable
```

### Important

`wait()` releases the object's monitor.

`notify()` does not immediately transfer the lock.

The notified thread must first reacquire the lock.

---

# 11. Why wait(), notify(), notifyAll() belong to Object?

Because the lock belongs to an object.

Every Java object has an intrinsic monitor.

```text
Object
 |
 +-- Monitor / Lock
 |
 +-- wait()
 +-- notify()
 +-- notifyAll()
```

Example:

```java
synchronized (lock) {
    lock.wait();
}
```

The thread waits on the monitor associated with `lock`.

---

# 12. join()

`join()` makes one thread wait for another thread to complete.

```java
Thread t = new Thread(() -> {
    System.out.println("Task running");
});

t.start();

t.join();

System.out.println("Main continues");
```

Flow:

```text
Main
 |
 | start()
 v
Worker ----------------> finishes
 |
 | join()
 |
 v
Main continues
```

Without `join()`:

```text
Main
 |
 +---------------------> continues
 |
Worker -----------------> finishes
```

---

# 13. Daemon vs Non-Daemon Threads

A daemon thread is a background thread.

Example:

```java
Thread t = new Thread(() -> {

    while (true) {
        System.out.println("Monitoring...");
    }

});

t.setDaemon(true);
t.start();
```

JVM does not wait for daemon threads when all user threads finish.

Example:

```text
Main Thread
    |
    v
Terminates
    |
    v
No user threads remain
    |
    v
JVM exits
```

Daemon threads are commonly used for background support activities.

---

# 14. Thread Priority

Java provides:

```java
Thread.MIN_PRIORITY
Thread.NORM_PRIORITY
Thread.MAX_PRIORITY
```

Values:

```text
1
5
10
```

Example:

```java
thread.setPriority(Thread.MAX_PRIORITY);
```

Important:

> Thread priority is a scheduling hint, not a guarantee.

Do not build correctness logic around priority.

---

# 15. Race Condition

A race condition happens when multiple threads access shared mutable state and the result depends on execution timing.

Example:

```java
class Counter {

    int count = 0;

    void increment() {
        count++;
    }
}
```

Suppose:

```text
Thread A: read count = 0
Thread B: read count = 0

Thread A: count = 1
Thread B: count = 1
```

Expected:

```text
2
```

Actual:

```text
1
```

because:

```java
count++;
```

is not one atomic operation.

Conceptually:

```text
read
 |
modify
 |
write
```

---

# 16. Thread Safety

A class is thread-safe when its behavior remains correct when accessed concurrently by multiple threads.

Example:

```java
class Counter {

    private int count;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```

Only one thread at a time can execute the synchronized methods for the same object.

---

# 17. Critical Section

A critical section is code that accesses shared mutable state and therefore needs synchronization.

Example:

```java
synchronized (lock) {

    balance = balance - amount;

}
```

The critical section is:

```java
balance = balance - amount;
```

because multiple threads can modify `balance`.

---

# 18. synchronized keyword

`synchronized` provides:

1. Mutual exclusion
2. Visibility guarantees around monitor operations

Example:

```java
public synchronized void increment() {
    count++;
}
```

Equivalent conceptually to:

```java
public void increment() {

    synchronized (this) {
        count++;
    }
}
```

---

# 19. Synchronized Method vs Block

## Method

```java
public synchronized void process() {

}
```

Locks:

```text
this
```

for an instance method.

## Block

```java
public void process() {

    synchronized (lock) {

    }
}
```

Locks:

```text
lock
```

### Why use synchronized block?

It allows smaller critical sections.

Bad:

```java
public synchronized void process() {

    // DB call

    // external API call

    // calculation
}
```

Better:

```java
public void process() {

    // expensive operation

    synchronized (lock) {

        // only shared state modification
    }
}
```

---

# 20. Object Lock vs Class Lock

## Object Lock

```java
public synchronized void method() {
}
```

Locks:

```text
this object
```

Example:

```java
MyClass obj1 = new MyClass();
MyClass obj2 = new MyClass();
```

`obj1` and `obj2` have different locks.

Therefore:

```text
Thread A -> obj1.method()
Thread B -> obj2.method()
```

can execute simultaneously.

## Class Lock

```java
public static synchronized void method() {
}
```

Locks:

```java
MyClass.class
```

Only one thread can execute static synchronized methods for that class at a time.

---

# 21. Can two synchronized methods run simultaneously?

Depends on whether they use the same lock.

```java
class Test {

    public synchronized void method1() {
    }

    public synchronized void method2() {
    }
}
```

Same object:

```text
Thread A -> method1()
Thread B -> method2()

       same this lock

          |
          v

       One at a time
```

Different objects:

```java
Test a = new Test();
Test b = new Test();
```

They can execute concurrently because:

```text
a -> lock A
b -> lock B
```

---

# 22. static synchronized

```java
public static synchronized void update() {
}
```

The lock is:

```java
ClassName.class
```

Example:

```java
class Counter {

    private static int count;

    public static synchronized void increment() {
        count++;
    }
}
```

All objects share the same class-level lock.

---

# 23. volatile

`volatile` ensures visibility of changes between threads.

Example:

```java
private volatile boolean running = true;
```

Thread A:

```java
while (running) {
    // work
}
```

Thread B:

```java
running = false;
```

Without `volatile`, Thread A may not observe the update promptly due to memory visibility/caching effects.

With `volatile`:

```text
Thread B
   |
   | running = false
   v
Shared memory
   |
   v
Thread A sees false
```

### Important

`volatile` does NOT make compound operations atomic.

This is unsafe:

```java
volatile int count;

count++;
```

because:

```text
read
modify
write
```

are still separate operations.

---

# 24. volatile vs synchronized

| volatile                       | synchronized                  |
| ------------------------------ | ----------------------------- |
| Visibility                     | Visibility + mutual exclusion |
| No blocking lock               | Uses monitor                  |
| Good for state flags           | Good for compound operations  |
| Does not make `count++` atomic | Can make `count++` atomic     |
| Lightweight                    | Potential blocking            |

Example suitable for volatile:

```java
volatile boolean shutdown;
```

Example suitable for synchronized:

```java
synchronized void increment() {
    count++;
}
```

---

# 25. Atomic Classes

Java provides:

```java
AtomicInteger
AtomicLong
AtomicBoolean
AtomicReference
```

Example:

```java
AtomicInteger counter = new AtomicInteger();

counter.incrementAndGet();
```

No explicit `synchronized` required.

Example:

```java
AtomicInteger counter = new AtomicInteger(0);

counter.incrementAndGet();

System.out.println(counter.get());
```

---

# 26. CAS – Compare And Set

CAS means:

> Update the value only if it is still equal to the expected value.

Example:

```java
AtomicInteger value = new AtomicInteger(10);

boolean result =
        value.compareAndSet(10, 20);
```

Conceptually:

```text
Current value = 10
Expected      = 10
New value     = 20

10 == 10
   |
   v
Update to 20
```

If current value is 15:

```text
Expected = 10
Actual   = 15

10 != 15

No update
```

CAS is a foundation of many lock-free/concurrent algorithms.

---

# 27. Lock Interface

Java provides:

```java
java.util.concurrent.locks.Lock
```

Example:

```java
Lock lock = new ReentrantLock();

lock.lock();

try {

    // critical section

} finally {

    lock.unlock();
}
```

Always unlock in `finally`.

---

# 28. synchronized vs Lock

| synchronized                      | Lock                       |
| --------------------------------- | -------------------------- |
| Simple                            | More flexible              |
| Automatic unlock                  | Manual unlock              |
| No tryLock                        | Supports tryLock           |
| No interruptible lock acquisition | Supports lockInterruptibly |
| Monitor based                     | Explicit lock              |

---

# 29. ReentrantLock

A `ReentrantLock` allows the same thread to acquire the same lock multiple times.

```java
Lock lock = new ReentrantLock();

lock.lock();

try {

    lock.lock();

    try {

        // work

    } finally {
        lock.unlock();
    }

} finally {
    lock.unlock();
}
```

The same thread can acquire it twice.

It must release it twice.

---

# 30. tryLock()

Instead of waiting indefinitely:

```java
if (lock.tryLock()) {

    try {
        // work
    } finally {
        lock.unlock();
    }

} else {

    System.out.println("Could not acquire lock");
}
```

This can be useful for avoiding indefinite blocking.

---

# 31. ReadWriteLock

Useful when:

```text
Many readers
Few writers
```

Example:

```text
Reader 1 ----\
Reader 2 -----+--> shared data
Reader 3 ----/

Writer --------> shared data
```

Multiple readers can execute simultaneously.

Writer requires exclusive access.

---

# 32. ReentrantReadWriteLock

```java
ReentrantReadWriteLock lock =
        new ReentrantReadWriteLock();

lock.readLock().lock();

try {

    // read

} finally {

    lock.readLock().unlock();
}
```

Write:

```java
lock.writeLock().lock();

try {

    // update

} finally {

    lock.writeLock().unlock();
}
```

---

# 33. StampedLock

`StampedLock` provides:

* Read lock
* Write lock
* Optimistic read

Example:

```java
StampedLock lock = new StampedLock();

long stamp = lock.tryOptimisticRead();

try {

    // read data

} finally {

}
```

Optimistic read allows reading without immediately acquiring a traditional read lock.

You then validate:

```java
if (!lock.validate(stamp)) {

    // data changed
}
```

This is an advanced topic generally asked for experienced Java developers.

---

# 34. Thread Interruption

Interrupting a thread does not forcibly kill it.

```java
thread.interrupt();
```

It sends an interruption request.

Example:

```java
while (!Thread.currentThread().isInterrupted()) {

    // work
}
```

For methods such as:

```java
sleep()
wait()
join()
```

an interruption can result in:

```java
InterruptedException
```

Best practice:

```java
catch (InterruptedException e) {

    Thread.currentThread().interrupt();
}
```

Why?

Because catching the exception clears the interrupted status, so restoring it preserves the cancellation signal.

---

# 35. Deadlock

Deadlock occurs when threads wait forever for locks held by each other.

Example:

```text
Thread A                    Thread B

holds Lock A                holds Lock B
     |                           |
     v                           v
waits for Lock B            waits for Lock A
     |                           |
     +------------+--------------+
                  |
               DEADLOCK
```

Code:

```java
synchronized (lockA) {

    synchronized (lockB) {

    }
}
```

Another thread:

```java
synchronized (lockB) {

    synchronized (lockA) {

    }
}
```

---

# 36. How to prevent Deadlock?

### Strategy 1: Consistent lock ordering

Always acquire:

```text
Lock A
then
Lock B
```

Never:

```text
Lock B
then
Lock A
```

### Strategy 2: tryLock()

```java
if (lockA.tryLock()) {

    try {

        if (lockB.tryLock()) {

            try {
                // work
            } finally {
                lockB.unlock();
            }

        }

    } finally {
        lockA.unlock();
    }
}
```

### Strategy 3

Keep lock scope small.

Avoid:

```java
synchronized(lock) {

    callExternalAPI();
}
```

because the external API can take an unpredictable amount of time.

---

# 37. Livelock

In livelock, threads are active but make no progress.

Example:

```text
Thread A -> gives way to B
Thread B -> gives way to A
Thread A -> gives way
Thread B -> gives way
...
```

Unlike deadlock:

```text
Deadlock = blocked

Livelock = running but no progress
```

---

# 38. Starvation

Starvation happens when a thread never gets enough CPU time or access to a resource.

Example:

```text
Thread A
Thread B
Thread C
   |
   v
One thread repeatedly gets resource
   |
   v
Thread D waits indefinitely
```

Possible causes:

* Unfair locking
* Poor scheduling
* Long critical sections
* Excessive priority differences

---

# 39. Executor Framework

Instead of manually creating threads:

```java
new Thread(...).start();
```

Java provides:

```text
Executor Framework
```

Diagram:

```text
Tasks
 |
 +---- Task 1
 +---- Task 2
 +---- Task 3
 |
 v
ExecutorService
 |
 v
Thread Pool
 |
 +---- Worker 1
 +---- Worker 2
 +---- Worker 3
```

Benefits:

* Thread reuse
* Thread lifecycle management
* Task scheduling
* Better resource control

---

# 40. Executor vs ExecutorService

## Executor

Basic interface:

```java
void execute(Runnable command);
```

## ExecutorService

Provides additional functionality:

```java
submit()
shutdown()
shutdownNow()
invokeAll()
invokeAny()
```

Example:

```java
ExecutorService executor =
        Executors.newFixedThreadPool(5);

executor.submit(() -> {
    System.out.println("Task");
});

executor.shutdown();
```

---

# 41. submit() vs execute()

## execute()

```java
executor.execute(task);
```

Accepts:

```java
Runnable
```

Does not return a result.

## submit()

```java
Future<?> future =
        executor.submit(task);
```

Returns:

```java
Future
```

Exceptions can be captured through the Future.

---

# 42. Callable vs Runnable

### Runnable

```java
Runnable task = () -> {
    System.out.println("Running");
};
```

Returns:

```text
nothing
```

Cannot directly throw checked exceptions.

### Callable

```java
Callable<Integer> task = () -> {

    return 100;
};
```

Returns:

```text
Integer
```

Can throw checked exceptions.

---

# 43. Future

A `Future` represents the result of an asynchronous computation.

```java
ExecutorService executor =
        Executors.newFixedThreadPool(2);

Future<Integer> future =
        executor.submit(() -> {

            Thread.sleep(1000);

            return 100;
        });

Integer result = future.get();
```

Important:

```java
future.get();
```

is blocking.

Diagram:

```text
Main Thread
    |
 submit()
    |
    v
Future <---------------- Worker
    |                      |
 get()                     |
    |                      |
 BLOCKS <------------------+
    |
    v
Result
```

---

# 44. ThreadPoolExecutor

The core implementation is:

```java
ThreadPoolExecutor
```

Important parameters:

```java
new ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    threadFactory,
    rejectedExecutionHandler
);
```

Example:

```java
ThreadPoolExecutor executor =
    new ThreadPoolExecutor(
        5,
        10,
        60,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(100)
    );
```

---

# 45. Core Pool Size vs Maximum Pool Size

Suppose:

```text
corePoolSize = 5
maximumPoolSize = 10
queue = 100
```

Task flow:

```text
Tasks arrive
    |
    v
Create threads up to 5
    |
    v
Additional tasks go to queue
    |
    v
Queue becomes full
    |
    v
Create threads up to 10
    |
    v
Still full?
    |
    v
RejectedExecutionHandler
```

This is a very important interview topic.

---

# 46. Queue in ThreadPoolExecutor

The queue stores tasks waiting for worker threads.

Example:

```text
              Thread Pool
             /     |     \
          Worker Worker Worker
             |
             v
          Work Queue
        +---+---+---+
        | T | T | T |
        +---+---+---+
```

Common queues:

```java
LinkedBlockingQueue
ArrayBlockingQueue
SynchronousQueue
```

Queue choice strongly affects thread-pool behavior.

---

# 47. Types of Thread Pools

Java provides:

```java
Executors.newFixedThreadPool()
Executors.newCachedThreadPool()
Executors.newSingleThreadExecutor()
Executors.newScheduledThreadPool()
```

### Fixed

```java
Executors.newFixedThreadPool(5);
```

Fixed number of workers.

### Single

```java
Executors.newSingleThreadExecutor();
```

One worker.

### Cached

```java
Executors.newCachedThreadPool();
```

Creates threads as needed and reuses idle ones.

### Scheduled

```java
Executors.newScheduledThreadPool(2);
```

Used for delayed/periodic tasks.

---

# 48. shutdown() vs shutdownNow()

## shutdown()

Stops accepting new tasks.

Existing tasks are allowed to complete.

```java
executor.shutdown();
```

## shutdownNow()

Attempts to stop active tasks and returns tasks that were waiting.

```java
executor.shutdownNow();
```

Important:

`shutdownNow()` is cooperative; interruption does not guarantee that a task immediately stops.

---

# 49. ScheduledExecutorService

Example:

```java
ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(2);

scheduler.schedule(
        () -> System.out.println("Hello"),
        5,
        TimeUnit.SECONDS
);
```

Periodic:

```java
scheduler.scheduleAtFixedRate(
        () -> System.out.println("Running"),
        0,
        10,
        TimeUnit.SECONDS
);
```

---

# 50. ForkJoinPool

Designed for divide-and-conquer tasks.

Example:

```text
Large Task
    |
    +---- Task A
    |       |
    |       +-- A1
    |       +-- A2
    |
    +---- Task B
            |
            +-- B1
            +-- B2
```

Tasks recursively split into smaller tasks.

Java 8 introduced the use of Fork/Join heavily in:

```java
parallelStream()
```

and:

```java
CompletableFuture
```

through the common pool in applicable async operations.

---

# 51. Work Stealing

Each worker can have its own deque.

```text
Worker 1 -> [Task][Task][Task]

Worker 2 -> [Task]

Worker 3 -> []
```

Worker 3 has no work.

It can steal work from another worker.

```text
Worker 3
   |
   | steal
   v
Worker 1
```

This helps keep workers busy.

---

# 52. Concurrent Collections

Important Java concurrent collections:

```text
ConcurrentHashMap
CopyOnWriteArrayList
BlockingQueue
ConcurrentLinkedQueue
ConcurrentLinkedDeque
```

They are designed for concurrent access.

---

# 53. HashMap vs ConcurrentHashMap

`HashMap` is not thread-safe.

```java
Map<String, Integer> map =
        new HashMap<>();
```

Multiple threads modifying it concurrently can cause incorrect behavior.

Use:

```java
ConcurrentHashMap<String, Integer> map =
        new ConcurrentHashMap<>();
```

for concurrent access.

---

# 54. ConcurrentHashMap internals

Java 8 `ConcurrentHashMap` uses:

* CAS
* synchronized locking for specific bins when needed
* highly concurrent access

It does not use one global lock for the entire map.

Conceptually:

```text
ConcurrentHashMap
 |
 +-- Bucket 1
 +-- Bucket 2
 +-- Bucket 3
 +-- Bucket 4
```

Different threads can work on different portions concurrently.

### Important Java 8 interview point

Older Java versions used segmented locking.

Java 8 removed the `Segment`-based architecture and uses a combination of CAS and synchronized bin-level mechanisms.

---

# 55. CopyOnWriteArrayList

Useful when:

```text
Many reads
Very few writes
```

Example:

```java
CopyOnWriteArrayList<String> list =
        new CopyOnWriteArrayList<>();

list.add("Java");
```

On modification, a new underlying array is created.

Conceptually:

```text
Old Array
[A][B][C]

       write

New Array
[A][B][C][D]
```

Reads can happen without locking the same way normal mutation does.

### Disadvantage

Writes are expensive.

Avoid it for frequently modified large lists.

---

# 56. BlockingQueue

A `BlockingQueue` supports producer-consumer scenarios.

Important methods:

```java
put()
take()
offer()
poll()
```

`put()` can block when the queue is full.

`take()` can block when the queue is empty.

---

# 57. Producer-Consumer Problem

```text
Producer
   |
   | put()
   v
+----------------+
| BlockingQueue  |
+----------------+
   |
   | take()
   v
Consumer
```

Example:

```java
BlockingQueue<String> queue =
        new ArrayBlockingQueue<>(10);
```

Producer:

```java
queue.put("Order-101");
```

Consumer:

```java
String order = queue.take();
```

This is much cleaner than manually implementing:

```text
wait()
notify()
```

for many producer-consumer cases.

---

# 58. CountDownLatch

A `CountDownLatch` allows one or more threads to wait until a count reaches zero.

Example:

```java
CountDownLatch latch =
        new CountDownLatch(3);
```

Three workers:

```java
latch.countDown();
```

Main:

```java
latch.await();
```

Diagram:

```text
Worker 1 ---- countDown()
Worker 2 ---- countDown()
Worker 3 ---- countDown()
                     |
                     v
                  count=0
                     |
                     v
                  Main
                 proceeds
```

Important:

A latch cannot be reset.

---

# 59. CyclicBarrier

A `CyclicBarrier` allows multiple threads to wait for each other at a common point.

```java
CyclicBarrier barrier =
        new CyclicBarrier(3);
```

Each thread:

```java
barrier.await();
```

Flow:

```text
Thread A ----\
Thread B -----+---- Barrier
Thread C ----/       |
                     v
              All continue
```

Unlike CountDownLatch, a CyclicBarrier can be reused.

---

# 60. Semaphore

A semaphore controls access to a limited number of resources.

Example:

```java
Semaphore semaphore =
        new Semaphore(3);
```

At most three threads can enter:

```java
semaphore.acquire();

try {

    // resource usage

} finally {

    semaphore.release();
}
```

Diagram:

```text
Semaphore = 3 permits

Thread A -> permit
Thread B -> permit
Thread C -> permit

Thread D -> waits
```

Common use:

```text
DB connection limit
API rate/concurrency limit
Limited resource pool
```

---

# 61. CountDownLatch vs CyclicBarrier

| CountDownLatch               | CyclicBarrier               |
| ---------------------------- | --------------------------- |
| One-time synchronization     | Reusable                    |
| Count reaches zero           | Threads wait for each other |
| `countDown()`                | `await()`                   |
| Often main waits for workers | Workers wait for workers    |
| Cannot reset                 | Can reuse                   |

---

# 62. Semaphore vs CountDownLatch

### Semaphore

Controls:

```text
How many threads can access resource
```

### CountDownLatch

Controls:

```text
When threads can proceed
```

Example:

```text
Semaphore:
Only 5 API calls at once.

Latch:
Wait until 5 startup tasks complete.
```

---

# 63. ThreadLocal

`ThreadLocal` provides thread-specific data.

Example:

```java
ThreadLocal<String> user =
        new ThreadLocal<>();

user.set("Kriti");

System.out.println(user.get());
```

Each thread gets its own value.

```text
Thread A -> ThreadLocal -> "A"

Thread B -> ThreadLocal -> "B"

Thread C -> ThreadLocal -> "C"
```

Common use cases:

* Request context
* Correlation ID
* User context
* Date formatter in older Java code
* Transaction/request-related state

---

# 64. InheritableThreadLocal

Allows child threads to inherit values from a parent thread.

```java
InheritableThreadLocal<String> context =
        new InheritableThreadLocal<>();

context.set("parent-context");
```

Child thread may inherit:

```text
parent
  |
  +-- child
       |
       +-- inherited value
```

### Important

With thread pools, context propagation is more complicated because worker threads may already exist.

---

# 65. CompletableFuture

Java 8 introduced:

```java
CompletableFuture
```

for asynchronous programming.

Example:

```java
CompletableFuture<String> future =
        CompletableFuture.supplyAsync(() -> {

            return "Hello";

        });
```

Then:

```java
future.thenApply(result -> result + " World")
      .thenAccept(System.out::println);
```

Flow:

```text
supplyAsync()
     |
     v
"Hello"
     |
     v
thenApply()
     |
     v
"Hello World"
     |
     v
thenAccept()
```

---

# 66. thenApply vs thenCompose vs thenCombine

## thenApply

Transforms a result.

```java
future.thenApply(result -> result.toUpperCase());
```

Think:

```text
A -> B
```

## thenCompose

Used when the next operation itself returns a `CompletableFuture`.

```java
getUser()
    .thenCompose(user -> getOrders(user));
```

Without `thenCompose`:

```text
CompletableFuture
    |
    v
CompletableFuture
    |
    v
Nested Future
```

With `thenCompose`:

```text
CompletableFuture<A>
        |
        v
CompletableFuture<B>
```

## thenCombine

Combines two independent futures.

```java
CompletableFuture<String> user =
        getUser();

CompletableFuture<String> orders =
        getOrders();

CompletableFuture<String> result =
        user.thenCombine(
                orders,
                (u, o) -> u + " : " + o
        );
```

Diagram:

```text
       User API
          |
          v
       Future A
          \
           \
            +---- thenCombine ----> Result
           /
          /
       Future B
          ^
          |
      Orders API
```

---

# 67. thenAccept vs thenRun

## thenAccept

Receives previous result.

```java
future.thenAccept(result ->
        System.out.println(result));
```

## thenRun

Does not receive result.

```java
future.thenRun(() ->
        System.out.println("Completed"));
```

Think:

```text
thenAccept -> I need the result

thenRun    -> I only care that it completed
```

---

# 68. exceptionally vs handle vs whenComplete

## exceptionally

Used for fallback/error recovery.

```java
future.exceptionally(ex -> {

    log.error("Failed", ex);

    return "default";
});
```

## handle

Receives both result and exception.

```java
future.handle((result, ex) -> {

    if (ex != null) {
        return "fallback";
    }

    return result;
});
```

## whenComplete

Used mainly for observing completion.

```java
future.whenComplete((result, ex) -> {

    if (ex != null) {
        log.error("Failed", ex);
    }
});
```

### Easy memory trick

```text
exceptionally -> recover

handle        -> transform success/error

whenComplete  -> observe/log
```

---

# 69. CompletableFuture Exception Handling

Example:

```java
CompletableFuture<String> future =
    CompletableFuture
        .supplyAsync(() -> callExternalApi())
        .thenApply(result -> processResult(result))
        .exceptionally(ex -> {

            System.out.println(
                "API failed: " + ex.getMessage()
            );

            return "default-value";
        });
```

Flow:

```text
callExternalApi()
       |
       v
processResult()
       |
       +------ success ------> result
       |
       +------ exception ----> exceptionally()
                                  |
                                  v
                               fallback
```

---

# 70. Common ForkJoinPool and CompletableFuture

When using:

```java
CompletableFuture.supplyAsync(...)
```

without specifying an executor, Java commonly uses the:

```text
ForkJoinPool.commonPool()
```

Example:

```java
CompletableFuture
    .supplyAsync(() -> callAPI());
```

For blocking I/O, blindly using the common pool can be problematic because blocking tasks can occupy worker threads.

You can provide your own executor:

```java
ExecutorService executor =
        Executors.newFixedThreadPool(10);

CompletableFuture
    .supplyAsync(
        () -> callAPI(),
        executor
    );
```

This is a very useful backend interview point.

---

# 71. Parallel Streams

Java 8:

```java
list.parallelStream()
    .map(this::process)
    .collect(Collectors.toList());
```

Parallel streams use the ForkJoin common pool by default.

Conceptually:

```text
List
 |
 +---- Partition 1 -> Worker
 +---- Partition 2 -> Worker
 +---- Partition 3 -> Worker
 +---- Partition 4 -> Worker
 |
 v
Combine
```

### Do not automatically use parallel streams.

Consider:

* Dataset size
* CPU vs I/O work
* Thread pool contention
* Ordering requirements
* Side effects
* Task granularity

---

# 72. Parallel Stream vs CompletableFuture

### Parallel Stream

Good abstraction for:

```text
Parallel data processing
```

Example:

```java
list.parallelStream()
    .map(...)
    .collect(...);
```

### CompletableFuture

Good for:

```text
Asynchronous workflows
API calls
Combining independent operations
Error handling
```

Example:

```text
API A ----\
           \
            CompletableFuture
           /
API B ----/
```

---

# 73. Memory Visibility

Each thread may have local CPU/cache state.

Conceptually:

```text
Thread A
   |
   v
CPU Cache A

Thread B
   |
   v
CPU Cache B

       |
       v
   Main Memory
```

Synchronization mechanisms establish visibility guarantees.

Examples:

```java
synchronized
volatile
AtomicInteger
Lock
```

---

# 74. Happens-Before Relationship

The Java Memory Model defines **happens-before** relationships.

Important examples:

### Unlock happens-before subsequent lock

```text
Thread A
unlock
  |
  v
Thread B
lock
```

### volatile write happens-before subsequent volatile read

```text
Thread A
volatile write
      |
      v
Thread B
volatile read
```

### Thread start

Actions before:

```java
thread.start();
```

happen-before actions in the started thread.

### Thread completion and join

Actions in a thread happen-before another thread successfully returns from:

```java
thread.join();
```

---

# 75. Atomicity vs Visibility vs Ordering

This is an important senior-level interview concept.

## Atomicity

Operation happens as one indivisible unit.

Example:

```java
AtomicInteger.incrementAndGet()
```

## Visibility

One thread sees another thread's changes.

Example:

```java
volatile boolean flag;
```

## Ordering

Controls/restricts how operations can be observed/reordered.

Synchronization mechanisms establish ordering guarantees.

### Remember

```text
Atomicity != Visibility != Ordering
```

A variable can have visibility without making compound operations atomic.

Example:

```java
volatile int count;

count++;
```

Visibility:

YES

Atomicity:

NO

---

# 76. False Sharing

False sharing is a CPU-cache performance problem.

Suppose two independent variables are located on the same cache line:

```text
Cache Line
+---------------------------+
| variable A | variable B   |
+---------------------------+
       |             |
    Thread A      Thread B
```

Thread A modifies A.

Thread B modifies B.

Even though logically unrelated, cache-line invalidation can cause performance degradation.

This is an advanced concurrency/performance topic.

---

# 77. Context Switching

When CPU switches between threads:

```text
Thread A
   |
   | context switch
   v
Thread B
   |
   | context switch
   v
Thread A
```

CPU must preserve/restore execution state.

Too many threads can cause excessive context switching.

Therefore:

> More threads does not automatically mean more performance.

---

# 78. Thread Dump

A thread dump shows:

* Thread names
* Thread states
* Stack traces
* Locks
* Waiting threads
* Blocked threads

Typical commands:

```bash
jstack <pid>
```

or:

```bash
jcmd <pid> Thread.print
```

Useful states:

```text
BLOCKED
WAITING
TIMED_WAITING
RUNNABLE
```

---

# 79. How to Troubleshoot Deadlock

Suppose production has requests hanging.

### Step 1

Take a thread dump.

```bash
jstack <pid>
```

### Step 2

Look for:

```text
BLOCKED
```

### Step 3

Look for circular lock dependencies.

Example:

```text
Thread-1 waiting for Lock-B
Thread-2 waiting for Lock-A
```

### Step 4

Identify lock ordering.

### Step 5

Fix:

```text
consistent lock ordering
smaller critical sections
tryLock()
avoid nested locks
```

---

# 80. How to design a thread-safe class

Consider:

```java
class Counter {

    private int count;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```

Things to consider:

### 1. Shared mutable state

Identify it.

### 2. Visibility

Will all threads see updates?

### 3. Atomicity

Can multiple operations interleave?

### 4. Synchronization

Use:

```text
synchronized
Lock
Atomic classes
Concurrent collections
```

### 5. Immutability

Prefer immutable objects when possible.

---

# 81. Common 8-Year Interview Scenarios

## Scenario 1: Counter used by 100 threads

Question:

> 100 threads increment a counter. How do you make it thread-safe?

Options:

```java
synchronized
```

or:

```java
AtomicInteger
```

Example:

```java
AtomicInteger counter = new AtomicInteger();

counter.incrementAndGet();
```

---

## Scenario 2: Multiple APIs need to be called

Suppose:

```text
User API       2 sec
Portfolio API  3 sec
Payment API    1 sec
```

Sequential:

```text
2 + 3 + 1 = 6 sec
```

Using independent asynchronous operations:

```text
User       2 sec ───┐
Portfolio  3 sec ───┤
Payment    1 sec ───┘
                     |
                     v
                 Combine
```

Potential solution:

```java
CompletableFuture<User> user =
        CompletableFuture.supplyAsync(
                () -> getUser());

CompletableFuture<Portfolio> portfolio =
        CompletableFuture.supplyAsync(
                () -> getPortfolio());

CompletableFuture<Payment> payment =
        CompletableFuture.supplyAsync(
                () -> getPayment());

CompletableFuture.allOf(
        user,
        portfolio,
        payment
).join();
```

---

## Scenario 3: API has maximum 10 concurrent requests

Use:

```java
Semaphore semaphore =
        new Semaphore(10);
```

Example:

```java
semaphore.acquire();

try {

    callAPI();

} finally {

    semaphore.release();
}
```

This controls concurrency.

---

## Scenario 4: 5 services must initialize before application starts

Use:

```java
CountDownLatch latch =
        new CountDownLatch(5);
```

Each service:

```java
initialize();

latch.countDown();
```

Startup thread:

```java
latch.await();
```

---

## Scenario 5: Three threads must reach a point together

Use:

```java
CyclicBarrier barrier =
        new CyclicBarrier(3);
```

Each:

```java
barrier.await();
```

---

## Scenario 6: Many reads, very few writes

Possible option:

```java
CopyOnWriteArrayList
```

or:

```java
ReentrantReadWriteLock
```

depending on the access pattern.

---

## Scenario 7: Producer generates orders faster than consumer

Use:

```java
BlockingQueue<Order>
```

Example:

```java
BlockingQueue<Order> queue =
        new ArrayBlockingQueue<>(100);
```

Producer:

```java
queue.put(order);
```

Consumer:

```java
Order order = queue.take();
```

---

## Scenario 8: Thread pool becomes exhausted

Investigate:

```text
Thread count
Queue size
Task duration
Blocking calls
Rejected tasks
CPU utilization
Database connection pool
External API latency
```

Important senior-level observation:

> Increasing the thread pool size is not automatically the solution.

For example:

```text
Thread Pool = 100
DB Connection Pool = 10
```

If all 100 threads need DB connections:

```text
100 threads
     |
     v
10 DB connections
     |
     v
90 threads wait
```

This can increase contention without improving throughput.

---

# 82. Rapid-Fire Interview Questions

### Q1. What is the difference between concurrency and parallelism?

**Concurrency:**

Multiple tasks make progress during overlapping periods.

**Parallelism:**

Multiple tasks execute simultaneously on different CPU cores.

```text
Concurrency:

Task A -> -- -- --
Task B   -- -- --


Parallelism:

Core 1 -> Task A
Core 2 -> Task B
```

---

### Q2. Is `count++` atomic?

No.

```java
count++;
```

is effectively:

```text
read
modify
write
```

---

### Q3. Does volatile make increment atomic?

No.

```java
volatile int count;

count++;
```

is still not atomic.

---

### Q4. Does synchronized guarantee visibility?

Yes, monitor unlock/lock establishes the relevant happens-before relationship.

---

### Q5. Does sleep release a lock?

No.

---

### Q6. Does wait release a lock?

Yes, the monitor associated with the object on which `wait()` is called.

---

### Q7. Can wait() be called outside synchronized?

Normally no.

It requires the calling thread to own the object's monitor, otherwise:

```text
IllegalMonitorStateException
```

---

### Q8. Can notify() be called outside synchronized?

Same rule: the thread must own that object's monitor.

---

### Q9. Difference between `notify()` and `notifyAll()`?

```text
notify()
    -> wakes one waiting thread

notifyAll()
    -> wakes all waiting threads
```

The awakened threads still need to reacquire the monitor.

---

### Q10. Is HashMap thread-safe?

No.

---

### Q11. Is ConcurrentHashMap thread-safe?

Yes, for its supported concurrent operations.

---

### Q12. Can ConcurrentHashMap contain null keys?

No.

```text
null key    -> not allowed
null value  -> not allowed
```

---

### Q13. What is a daemon thread?

A background thread that does not keep the JVM alive when no user threads remain.

---

### Q14. What happens if a thread throws an unchecked exception?

That thread terminates unless the exception is handled.

The whole JVM does not necessarily terminate.

---

### Q15. Can we restart a Java Thread?

No.

Calling `start()` again on the same Thread object results in:

```text
IllegalThreadStateException
```

---

### Q16. What is the difference between Runnable and Callable?

```text
Runnable
    -> no return value

Callable<T>
    -> returns T
    -> can throw checked exceptions
```

---

### Q17. What is Future.get()?

It waits for the asynchronous computation to complete and returns its result.

It can therefore block.

---

### Q18. Why use ExecutorService instead of creating threads manually?

Because it provides:

```text
Thread reuse
Thread pool management
Task submission
Future
Shutdown
Resource control
```

---

### Q19. What happens if ExecutorService is not shut down?

Its worker threads may keep the application alive and resources may not be released as expected.

---

### Q20. What is deadlock?

Threads wait forever for resources held by each other.

---

### Q21. What is starvation?

A thread continuously fails to obtain the resources/CPU time it needs.

---

### Q22. What is livelock?

Threads continue executing but fail to make useful progress.

---

### Q23. What is ThreadLocal?

A mechanism for storing thread-specific values.

---

### Q24. Why is ThreadLocal dangerous with thread pools?

Because worker threads are reused.

If a value is not removed:

```java
threadLocal.remove();
```

the value can remain associated with the reused worker thread longer than intended.

Typical pattern:

```java
try {

    threadLocal.set(value);

    // work

} finally {

    threadLocal.remove();
}
```

---

### Q25. What is CompletableFuture?

A Java 8 API for composing asynchronous computations.

---

### Q26. Difference between `thenApply()` and `thenCompose()`?

```text
thenApply
A -> B

thenCompose
A -> Future<B>
```

`thenCompose()` avoids nested futures.

---

### Q27. Difference between `thenCombine()` and `thenCompose()`?

```text
thenCompose:
A -> async B

thenCombine:
A + B -> C
```

---

### Q28. What is `allOf()`?

Waits for multiple `CompletableFuture`s to complete.

```java
CompletableFuture.allOf(f1, f2, f3)
```

returns:

```java
CompletableFuture<Void>
```

It does not directly return a list of results.

---

### Q29. What is `anyOf()`?

Completes when any one of the supplied futures completes.

```java
CompletableFuture.anyOf(f1, f2, f3)
```

---

### Q30. What is the common pool?

The shared:

```java
ForkJoinPool.commonPool()
```

used by several Java APIs, including applicable asynchronous `CompletableFuture` operations and parallel streams.

---

# Most Important Topics for an 8-Year Java Interview

If your interview is for a **Java backend/product-company role**, prioritize these in this order:

```text
                    THREADING
                       |
        +--------------+--------------+
        |                             |
   CORE CONCEPTS                CONCURRENCY
        |                             |
 Thread lifecycle              synchronized
 start vs run                  volatile
 sleep vs wait                 Atomic*
 join                           Lock
 daemon                         ReentrantLock
                               ReadWriteLock
        |                             |
        +--------------+--------------+
                       |
                  EXECUTORS
                       |
             +---------+---------+
             |                   |
       ExecutorService      ThreadPoolExecutor
             |                   |
          Future             Queue/Pool
             |                   |
          Callable           Rejection
                       |
                       v
              CONCURRENT UTILITIES
                       |
        +--------------+--------------+
        |              |              |
   ConcurrentHashMap BlockingQueue  ThreadLocal
        |              |              |
   CopyOnWrite       Producer/      Context
                     Consumer
                       |
                       v
                 SYNCHRONIZATION
                       |
       +---------------+----------------+
       |               |                |
 CountDownLatch   CyclicBarrier    Semaphore
       |
       v
               COMPLETABLE FUTURE
                       |
       +---------------+----------------+
       |               |                |
  thenApply       thenCompose      thenCombine
       |               |                |
 exceptionally     handle          allOf/anyOf
                       |
                       v
                ADVANCED TOPICS
                       |
       +---------------+----------------+
       |               |                |
   Deadlock        JMM/Happens       ForkJoin
   Livelock        Before/CAS       Work Stealing
   Starvation      False Sharing
```

# 8-Year Interview "Must Know" Questions

Before an interview, make sure you can explain these **without memorizing definitions**:

1. `start()` vs `run()`
2. Thread lifecycle
3. `sleep()` vs `wait()`
4. `wait()` / `notify()` / `notifyAll()`
5. Object lock vs class lock
6. `synchronized` method vs block
7. `volatile`
8. Why `volatile count++` is not thread-safe
9. `AtomicInteger` and CAS
10. `synchronized` vs `ReentrantLock`
11. Deadlock and prevention
12. Deadlock vs livelock vs starvation
13. ExecutorService
14. `execute()` vs `submit()`
15. Runnable vs Callable
16. Future and why `get()` can block
17. ThreadPoolExecutor internals
18. Core pool vs maximum pool
19. Work queue
20. RejectedExecutionHandler
21. `shutdown()` vs `shutdownNow()`
22. ConcurrentHashMap
23. BlockingQueue
24. Producer-consumer
25. CountDownLatch
26. CyclicBarrier
27. Semaphore
28. ThreadLocal
29. CompletableFuture
30. `thenApply()` vs `thenCompose()`
31. `thenCombine()`
32. `exceptionally()` vs `handle()` vs `whenComplete()`
33. `allOf()` / `anyOf()`
34. CompletableFuture executor choice
35. Parallel streams
36. ForkJoinPool
37. Happens-before
38. Atomicity vs visibility
39. Thread dump analysis
40. Production thread-pool troubleshooting

# Interview-Level Rule of Thumb

For an experienced Java developer, don't stop at:

> "What does this class/method do?"

Be prepared for:

> "Why would you use it?"

> "What happens internally?"

> "What are the trade-offs?"

> "What happens under high load?"

> "What happens if the operation blocks?"

> "How would you troubleshoot this in production?"

For example, don't answer only:

```text
CompletableFuture is used for asynchronous programming.
```

A stronger 8-year-level explanation is:

```text
CompletableFuture allows asynchronous computations to be
composed without manually managing individual threads.

For independent I/O operations, I can start multiple futures
and combine them using allOf() or thenCombine().

However, I need to consider the executor being used. Blocking
operations should not blindly consume the common ForkJoinPool,
so for blocking workloads I may use a dedicated ExecutorService.

I also need explicit exception handling using exceptionally(),
handle(), or whenComplete(), depending on whether I want
recovery, transformation, or observation.
```

That difference—**definition + internal behavior + use case + trade-off + production consideration**—is what generally separates a basic Java concurrency answer from an experienced backend-engineer answer.

If you want, I can next turn this into a **Java 8 Threading & Multithreading interview `.md` file with a proper clickable Table of Contents**, plus **50 scenario-based coding questions and answers** specifically at the **8-year/backend-engineer level**.
