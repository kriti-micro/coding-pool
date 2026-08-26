# 🧠 Java JVM Memory & Garbage Collection — Complete Interview Guide
> Level: 8.5 Years Experience | MNC / Product Companies
> Topics: JVM Architecture · Memory Areas · GC Algorithms · G1GC · Tuning · OOM · Tricky Q&A · FAQ

---

## 📌 Table of Contents

1. [JVM Architecture — Full Picture](#1-jvm-architecture--full-picture)
2. [Runtime Memory Areas — Deep Dive](#2-runtime-memory-areas--deep-dive)
3. [Heap Memory — Young, Old, Metaspace](#3-heap-memory--young-old-metaspace)
4. [Garbage Collection — How It Works](#4-garbage-collection--how-it-works)
5. [GC Algorithms — Serial, Parallel, CMS, G1GC, ZGC, Shenandoah](#5-gc-algorithms)
6. [G1GC — Default GC (Java 9+) Deep Dive](#6-g1gc--default-gc-java-9-deep-dive)
7. [GC Tuning — JVM Flags](#7-gc-tuning--jvm-flags)
8. [Memory Leaks — Causes, Detection, Fix](#8-memory-leaks--causes-detection-fix)
9. [OutOfMemoryError — All Variants](#9-outofmemoryerror--all-variants)
10. [ClassLoader — Architecture & Delegation](#10-classloader--architecture--delegation)
11. [Tricky Interview Questions — Deep Explanations](#11-tricky-interview-questions--deep-explanations)
12. [Interview Q&A — All FAQs with Answers](#12-interview-qa--all-faqs-with-answers)
13. [Quick Reference Cheat Sheet](#13-quick-reference-cheat-sheet)

---

## 1. JVM Architecture — Full Picture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         JVM ARCHITECTURE                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │               CLASS LOADER SUBSYSTEM                        │   │
│  │  Bootstrap CL → Extension/Platform CL → Application CL     │   │
│  │  Loading → Linking (Verify+Prepare+Resolve) → Initialization│   │
│  └─────────────────────────┬────────────────────────────────────┘   │
│                            │ loads .class into memory               │
│  ┌─────────────────────────▼────────────────────────────────────┐   │
│  │               RUNTIME DATA AREAS                            │   │
│  │                                                              │   │
│  │  ┌────────────────────────────────────────────────────────┐  │   │
│  │  │         SHARED across ALL threads                     │  │   │
│  │  │  ┌──────────────┐   ┌─────────────────────────────┐  │  │   │
│  │  │  │  METHOD AREA │   │          HEAP               │  │  │   │
│  │  │  │  (Metaspace) │   │  Young Gen  │  Old Gen      │  │  │   │
│  │  │  │  Class meta  │   │  Eden+S0+S1 │  Tenured      │  │  │   │
│  │  │  │  Static vars │   │             │               │  │  │   │
│  │  │  │  Const pool  │   └─────────────────────────────┘  │  │   │
│  │  │  └──────────────┘                                     │  │   │
│  │  └────────────────────────────────────────────────────────┘  │   │
│  │                                                              │   │
│  │  ┌────────────────────────────────────────────────────────┐  │   │
│  │  │         PRIVATE per thread                            │  │   │
│  │  │  ┌──────────────┐  ┌───────────┐  ┌───────────────┐  │  │   │
│  │  │  │  JVM STACK   │  │PC REGISTER│  │NATIVE METHOD  │  │  │   │
│  │  │  │  Frames      │  │Current    │  │STACK          │  │  │   │
│  │  │  │  Local vars  │  │instruction│  │For native     │  │  │   │
│  │  │  │  Operand stk │  │pointer    │  │method calls   │  │  │   │
│  │  │  └──────────────┘  └───────────┘  └───────────────┘  │  │   │
│  │  └────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                            │                                        │
│  ┌─────────────────────────▼────────────────────────────────────┐   │
│  │               EXECUTION ENGINE                               │   │
│  │  ┌──────────────┐  ┌─────────────┐  ┌────────────────────┐  │   │
│  │  │ INTERPRETER  │  │ JIT COMPILER│  │ GARBAGE COLLECTOR  │  │   │
│  │  │ Executes     │  │ Hot methods │  │ Mark-Sweep-Compact │  │   │
│  │  │ bytecode     │  │ compiled to │  │ GC algorithms      │  │   │
│  │  │ line by line │  │ native code │  │ Minor/Major GC     │  │   │
│  │  └──────────────┘  └─────────────┘  └────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                            │                                        │
│  ┌─────────────────────────▼────────────────────────────────────┐   │
│  │  JNI (Java Native Interface) + Native Method Libraries       │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. Runtime Memory Areas — Deep Dive

### Method Area (Metaspace in Java 8+)

```java
// Stores: class structures, method bytecode, constant pool, static variables
// Shared: all threads
// Java 7 and before: PermGen (fixed size, common OOM source)
// Java 8+: Metaspace (native memory — grows dynamically by default)

// What lives in Metaspace:
// ✓ Class metadata (method names, field names, superclass info)
// ✓ Runtime constant pool (class-level constants)
// ✓ Static variables (references stored here, objects on heap)
// ✓ Method bytecode

// JVM Flags:
// -XX:MetaspaceSize=256m         (initial Metaspace size)
// -XX:MaxMetaspaceSize=512m      (cap it — without this, grows until native OOM)
// -XX:+PrintClassHistogram       (see what classes are loaded)

// OOM:
// OutOfMemoryError: Metaspace  ← too many classes loaded (dynamic proxies, code gen)
```

### JVM Stack (Thread Stack)

```java
// Private per thread
// Contains: stack frames — one frame per method call

// Each stack frame holds:
// ✓ Local variable array (primitives + references)
// ✓ Operand stack (working area for bytecode instructions)
// ✓ Frame data (reference to constant pool, exception table)

void methodA() {     // Frame 3 pushed
    methodB();
}
void methodB() {     // Frame 2 pushed
    methodC();
}
void methodC() {     // Frame 1 pushed
    int x = 5;       // local variable in Frame 1
    // Frame 1 popped when methodC returns
}

// Stack = ordered list of frames
// Push: method called  |  Pop: method returns or throws exception

// Default stack size: ~512KB (client) to ~1MB (server)
// -Xss4m  → set stack size to 4MB

// StackOverflowError: too many frames (deep recursion, no base case)
```

### PC Register (Program Counter)

```java
// Private per thread
// Holds: address of the CURRENT JVM instruction being executed
// For native methods: undefined (null)
// Used by: JVM execution engine to track what to execute next
// Size: enough to hold a reference (platform-dependent)
```

### Heap

```java
// Shared by ALL threads
// Stores: ALL objects (new MyClass(), arrays)
// Managed by: Garbage Collector

// What is ON the heap:
// ✓ Object instances (new Employee(), new ArrayList(), etc.)
// ✓ Arrays (int[], String[], Object[])
// ✓ String objects (String Pool since Java 7 also in heap)

// What is NOT on the heap:
// ✗ Local variables of primitive types (in Stack)
// ✗ Method code (in Metaspace)
// ✗ Class metadata (in Metaspace)

// JVM Flags:
// -Xms512m   → initial heap size
// -Xmx2g     → max heap size
// Recommendation: set Xms == Xmx to avoid heap resizing during runtime
```

---

## 3. Heap Memory — Young, Old, Metaspace

### Traditional Heap Layout (Serial/Parallel/CMS GC)

```
HEAP MEMORY
├── Young Generation (default ~1/3 of heap)
│     ├── Eden Space       (~80% of Young Gen)  ← new objects born here
│     ├── Survivor 0 (S0)  (~10% of Young Gen)  ← survived 1+ Minor GCs
│     └── Survivor 1 (S1)  (~10% of Young Gen)  ← surviving objects alternate here
│
└── Old Generation / Tenured (default ~2/3 of heap)
      └── Objects that survived many Minor GCs live here

NOTE: G1GC doesn't have fixed Young/Old regions — uses flexible region model
```

### Object Lifecycle in Heap

```
Step 1: Object created → goes to EDEN
  Employee e = new Employee(101);
  → e lives in Eden

Step 2: Minor GC triggered (Eden full)
  → Live objects in Eden → moved to S0
  → S0 objects age++ → stay in S0 or move to S1
  → Dead objects in Eden → collected (freed)

Step 3: After several Minor GCs (age threshold = 15 by default)
  → Object in S0/S1 with age >= 15 → PROMOTED to Old Gen
  → JVM flag: -XX:MaxTenuringThreshold=15

Step 4: Old Gen fills up → Major GC / Full GC triggered
  → More expensive (larger area, stop-the-world pause)

Step 5: Large objects (> -XX:PretenureSizeThreshold bytes)
  → Go DIRECTLY to Old Gen (bypass Young Gen)
  → Avoids copying overhead for large arrays etc.
```

### TLAB — Thread Local Allocation Buffer

```java
// Each thread has a private chunk of Eden called TLAB
// New objects allocated in TLAB without synchronization!
// When TLAB full → thread gets new TLAB from Eden
// When Eden full → Minor GC

// Why TLAB matters:
// ✓ Thread-safe object allocation without locks (each thread has own TLAB)
// ✓ Very fast: just pointer bump in TLAB
// ✓ Typical allocation rate: millions of small objects per second

// JVM flag:
// -XX:+UseTLAB (default: on)
// -XX:TLABSize=512k
```

---

## 4. Garbage Collection — How It Works

### GC Roots — Where Object Reachability Starts

```
GC Roots (always considered reachable — never collected):
  ✓ Local variables and parameters in all active stack frames
  ✓ Static variables (class-level)
  ✓ Active threads
  ✓ JNI references (native code holding Java objects)
  ✓ System class loader
  ✓ Synchronization monitors

Mark phase: starting from GC roots, traverse ALL references
            mark every reachable object as "live"

Sweep phase: any object NOT marked = garbage → free its memory

Compact phase (some GCs): move live objects together
                           eliminates fragmentation
```

### Minor GC vs Major GC vs Full GC

| GC Type | Area Collected | Trigger | Pause |
|---|---|---|---|
| **Minor GC** | Young Gen only | Eden full | Short (< 100ms typical) |
| **Major GC** | Old Gen | Old Gen threshold reached | Longer |
| **Full GC** | Young + Old + Metaspace | Explicit `System.gc()`, concurrent mode failure | Longest — avoid! |

```java
// System.gc() is a HINT — JVM may ignore it
// Never rely on System.gc() in production
// Never call System.gc() in application code

// -XX:+DisableExplicitGC  → ignore System.gc() calls (good for prod)
// -XX:+ExplicitGCInvokesConcurrent  → make System.gc() use concurrent GC
```

### Reference Types — Controlling GC Behavior

```java
// Strong Reference (default) — object NEVER collected while ref exists
Employee e = new Employee(101);   // strong ref — e kept alive

// Weak Reference — collected when GC runs (even if memory available)
WeakReference<Employee> weak = new WeakReference<>(new Employee(101));
Employee e = weak.get();   // null if GC collected it
// Use: WeakHashMap (cache, prevents memory leak)

// Soft Reference — collected ONLY when memory is low
SoftReference<byte[]> cache = new SoftReference<>(new byte[1024 * 1024]);
byte[] data = cache.get();   // null only if JVM desperate for memory
// Use: memory-sensitive caches

// Phantom Reference — collected AFTER finalize(), for cleanup actions
PhantomReference<MyResource> phantom = new PhantomReference<>(res, queue);
// Cannot call .get() — always returns null
// Use: resource cleanup without finalizers (Java 9+ Cleaner API)

// Reference strength order (from strongest to weakest):
// Strong > Soft > Weak > Phantom
```

---

## 5. GC Algorithms

### Comparison Table — All GC Algorithms

| GC | Java Version | Young | Old | Pause | Best For |
|---|---|---|---|---|---|
| **Serial GC** | All | Copy | Mark-Compact | STW (long) | Single CPU, small heap |
| **Parallel GC** | Java 7 default | Parallel Copy | Parallel Mark-Compact | STW (shorter) | Multi-CPU throughput |
| **CMS** | Deprecated Java 9, removed 14 | Parallel Copy | Concurrent Mark-Sweep | Short STW + concurrent | Low-pause latency |
| **G1GC** | Java 9+ default | Region-based | Region-based | Predictable short pause | Large heaps, balanced |
| **ZGC** | Java 15+ production | Concurrent | Concurrent | < 1ms | Ultra-low latency |
| **Shenandoah** | Java 15+ (OpenJDK) | Concurrent | Concurrent | < 10ms | Low latency |

### Serial GC

```
-XX:+UseSerialGC

Young: Stop-The-World, single-threaded copy from Eden → S0/S1
Old:   Stop-The-World, single-threaded mark-sweep-compact

When to use:
  ✓ Single CPU machine (no benefit from parallel)
  ✓ Very small heap (< 100MB)
  ✓ Client applications where throughput > latency
  ✗ Server applications — avoid
```

### Parallel GC (Throughput GC)

```
-XX:+UseParallelGC   (was default Java 7/8)

Young: Multiple GC threads — faster Minor GC
Old:   Multiple GC threads — faster Major GC
Goal:  Maximize throughput (total work per unit time)

Pauses: Stop-The-World but short due to parallelism
Flags:
  -XX:ParallelGCThreads=N   (default: num CPUs)
  -XX:GCTimeRatio=99        (1% time in GC, 99% in app)
  -XX:MaxGCPauseMillis=200  (target max pause — best effort)

Best for: batch processing, data pipelines, non-interactive workloads
```

### ZGC (Java 15+ production)

```
-XX:+UseZGC

Completely concurrent — almost NO stop-the-world pauses
Pause time: < 1ms regardless of heap size (even 16TB heaps!)
Handles: object relocation concurrently using colored pointers

Flags:
  -XX:+UseZGC
  -Xms4g -Xmx4g
  -XX:ConcGCThreads=4

Best for: real-time systems, trading platforms, gaming servers
         where any pause > 10ms is unacceptable
```

---

## 6. G1GC — Default GC (Java 9+) Deep Dive

### G1GC Region Model

```
G1GC divides the heap into EQUAL-SIZED regions (default ~2MB each)
Regions are assigned roles dynamically:

┌──────────────────────────────────────────────────────────────────┐
│                    G1GC HEAP (e.g., 4GB)                        │
│                                                                  │
│  [E][E][E][E][S0][S1][O][O][O][O][O][H][H][E][E][O][O][E][E]  │
│   ↑Eden  ↑Surv ↑Survivor ↑Old    ↑Humongous ↑free to assign   │
│                                                                  │
│  E  = Eden region    (Young Gen — assigned dynamically)         │
│  S0 = Survivor 0 region                                         │
│  S1 = Survivor 1 region                                         │
│  O  = Old region     (long-lived objects)                       │
│  H  = Humongous region (objects > 50% of region size)           │
│  free = unassigned — can become any type                        │
│                                                                  │
│  Total regions: heap / region_size  (e.g., 4GB/2MB = 2048)     │
└──────────────────────────────────────────────────────────────────┘
```

### G1GC Collection Phases

```
Phase 1: YOUNG-ONLY GC (like Minor GC — frequent, short pauses)
  ✓ Collects ALL Eden regions
  ✓ Live objects → Survivor or promoted to Old
  ✓ Stop-The-World but parallel, short (<< 200ms target)

Phase 2: CONCURRENT MARKING (runs alongside application)
  2a. Initial Mark (STW — very short, piggybacks on Young GC)
       Mark all objects directly reachable from GC roots
  2b. Root Region Scan (concurrent)
       Scan Survivor regions for references into Old Gen
  2c. Concurrent Mark (concurrent — runs WITH your app)
       Traverse object graph, mark live objects across all regions
  2d. Remark (STW — very short)
       Handle objects modified during concurrent mark (SATB algorithm)
  2e. Cleanup (STW + concurrent)
       Account live objects per region
       Reclaim completely empty regions immediately
       Sort regions by collection efficiency (garbage-first!)

Phase 3: MIXED GC (extends Young GC to include some Old regions)
  ✓ Collects all Young regions + selected Old regions
  ✓ G1 picks Old regions with MOST garbage first ("Garbage First" = G1!)
  ✓ Continues until Old Gen has been sufficiently collected

Emergency: Full GC (avoid!)
  If G1 can't keep up with allocation rate → falls back to Full GC
  Serial stop-the-world — very long pause
  Fix: increase heap, tune G1 threads, find allocation hotspots
```

### G1GC Key Tuning Flags

```bash
# Core flags
-XX:+UseG1GC                        # enable G1 (default Java 9+)
-Xms4g -Xmx4g                       # heap size (set equal — no resize)
-XX:MaxGCPauseMillis=200             # target max pause (default: 200ms, best-effort)

# Region size (auto-calculated but can override)
-XX:G1HeapRegionSize=4m             # 1-32MB, power of 2 (default: heap/2048)

# Concurrency
-XX:ConcGCThreads=4                 # threads for concurrent marking
-XX:ParallelGCThreads=8             # threads for STW phases

# Triggering
-XX:InitiatingHeapOccupancyPercent=45  # start concurrent marking at 45% heap full
                                        # (default 45%) lower = more frequent GC

# Humongous objects
-XX:G1HeapRegionSize=32m            # increase if you have many large objects
                                     # humongous = > 50% of region size

# Logging (Java 9+)
-Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=10,filesize=50m
-Xlog:gc+heap=debug                 # detailed heap stats

# Useful diagnostics
-XX:+PrintGCDetails (Java 8)
-XX:+HeapDumpOnOutOfMemoryError      # dump heap when OOM (always enable in prod)
-XX:HeapDumpPath=/var/log/app/heap.hprof
```

---

## 7. GC Tuning — JVM Flags

### Complete JVM Flag Reference

```bash
# ── HEAP SIZING ──────────────────────────────────────────────────
-Xms2g                    # initial heap size (set = Xmx to avoid resize)
-Xmx4g                    # max heap size
-Xmn1g                    # young gen size (explicit — usually let GC decide)
-XX:NewRatio=3            # Old:Young ratio = 3:1  (default for Parallel)
-XX:SurvivorRatio=8       # Eden:Survivor ratio = 8:1:1 (default)

# ── STACK ────────────────────────────────────────────────────────
-Xss512k                  # thread stack size (default ~512KB-1MB)
# Reduce if thousands of threads; increase for deep recursion

# ── METASPACE ────────────────────────────────────────────────────
-XX:MetaspaceSize=256m             # initial Metaspace (default: ~21MB — too small!)
-XX:MaxMetaspaceSize=512m          # cap Metaspace (default: unlimited!)
-XX:CompressedClassSpaceSize=128m  # class pointers (subset of Metaspace)

# ── GC SELECTION ─────────────────────────────────────────────────
-XX:+UseSerialGC           # single-threaded (small apps)
-XX:+UseParallelGC         # throughput GC (Java 8 default)
-XX:+UseG1GC               # G1 (Java 9+ default)
-XX:+UseZGC                # ultra-low pause (Java 15+)
-XX:+UseShenandoahGC       # low pause (OpenJDK 15+)

# ── G1GC TUNING ──────────────────────────────────────────────────
-XX:MaxGCPauseMillis=200           # pause target (default: 200ms)
-XX:G1HeapRegionSize=4m            # region size (1-32MB)
-XX:InitiatingHeapOccupancyPercent=45  # concurrent marking threshold
-XX:G1ReservePercent=10            # emergency buffer (default 10%)

# ── THROUGHPUT ───────────────────────────────────────────────────
-XX:GCTimeRatio=99         # 99% app, 1% GC time (Parallel GC)
-XX:ParallelGCThreads=8    # GC threads during STW phases

# ── OBJECT PROMOTION ─────────────────────────────────────────────
-XX:MaxTenuringThreshold=15  # max age before promotion to Old Gen (default 15)
-XX:PretenureSizeThreshold=1m  # objects > 1m go directly to Old Gen

# ── DIAGNOSTICS ──────────────────────────────────────────────────
-XX:+HeapDumpOnOutOfMemoryError          # always enable in production!
-XX:HeapDumpPath=/var/logs/heap.hprof
-Xlog:gc*:file=gc.log:time:filecount=5,filesize=20m  # GC logging Java 9+
-XX:+PrintGCDetails -XX:+PrintGCDateStamps  # Java 8 GC logging
-XX:+PrintGCApplicationStoppedTime         # show pause times
-XX:+DisableExplicitGC                     # ignore System.gc() calls

# ── CONTAINER AWARENESS (Java 8u191+, Java 10+) ──────────────────
-XX:+UseContainerSupport          # use container CPU/memory limits (default: on Java 10+)
-XX:MaxRAMPercentage=75.0         # use 75% of container memory as max heap
-XX:InitialRAMPercentage=50.0     # initial heap = 50% of container memory
# Without this, JVM reads HOST memory not container limits → heap too large!
```

---

## 8. Memory Leaks — Causes, Detection, Fix

### Common Memory Leak Patterns

#### 1. Static Collections Holding References

```java
// ❌ LEAK: static field — lives for entire JVM lifetime
class UserCache {
    private static final Map<Integer, User> cache = new HashMap<>();

    public static void add(User user) {
        cache.put(user.getId(), user);   // users never removed!
        // cache grows indefinitely → OOM eventually
    }
}

// ✅ FIX: use bounded cache with eviction
private static final Map<Integer, User> cache =
    Collections.synchronizedMap(
        new LinkedHashMap<Integer, User>(1000, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry e) {
                return size() > 1000;   // cap at 1000 entries
            }
        });
// Or: use Guava Cache / Caffeine with TTL and size limits
```

#### 2. Unclosed Resources

```java
// ❌ LEAK: Connection/Stream never closed
public void processFile(String path) throws IOException {
    FileInputStream fis = new FileInputStream(path);
    // use fis
    // if exception occurs → fis never closed → file handle leaked
}

// ✅ FIX: try-with-resources
public void processFile(String path) throws IOException {
    try (FileInputStream fis = new FileInputStream(path)) {
        // fis auto-closed even on exception
    }
}
```

#### 3. ThreadLocal Not Removed

```java
// ❌ LEAK in thread pool: ThreadLocal not cleaned up
// Thread pool reuses threads → ThreadLocal values accumulate
ThreadLocal<UserContext> userContext = new ThreadLocal<>();
userContext.set(new UserContext(userId));
// ... process request
// Thread returns to pool with UserContext still set!
// Next request on same thread gets wrong UserContext!

// ✅ FIX: always remove in finally
try {
    userContext.set(new UserContext(userId));
    processRequest();
} finally {
    userContext.remove();   // MUST remove when using thread pools
}
```

#### 4. Listeners / Callbacks Not Removed

```java
// ❌ LEAK: anonymous listener holds reference to outer class
class MyActivity {
    EventBus.register(new EventListener() {
        @Override
        public void onEvent(Event e) {
            MyActivity.this.handleEvent(e);  // keeps MyActivity alive!
        }
    });
    // If MyActivity is "closed" but listener not deregistered:
    // EventBus → listener → MyActivity → all MyActivity's fields → LEAK
}

// ✅ FIX:
@Override
public void onDestroy() {
    EventBus.unregister(this);   // always deregister
}
```

#### 5. Inner Class Holding Outer Class Reference

```java
// ❌ Non-static inner class holds implicit reference to outer
class Outer {
    private byte[] bigData = new byte[10_000_000];

    class Inner {
        void doWork() { }
        // Inner implicitly holds: Outer.this
        // If Inner object lives longer than intended, bigData can't be GC'd
    }
}

// ✅ FIX: use static nested class if outer reference not needed
static class Inner {
    void doWork() { }  // no implicit outer reference
}
```

### Memory Leak Detection Tools

```bash
# 1. Heap dump — take a snapshot of heap
jmap -dump:format=b,file=heap.hprof <pid>
# Or: -XX:+HeapDumpOnOutOfMemoryError (auto on OOM)

# 2. Analyze with Eclipse MAT (Memory Analyzer Tool)
# Open heap.hprof → "Leak Suspects Report" → shows biggest retained objects

# 3. jstat — live GC stats
jstat -gcutil <pid> 1000   # every 1 second
# S0  S1  E    O    M     CCS  YGC YGCT  FGC  FGCT  GCT
# 0.0 95.5 72.3 45.2 96.1 93.5  15  0.512  2  0.356  0.868

# 4. jvisualvm or JMC (Java Mission Control)
# Visual heap, GC activity, thread dump

# 5. -verbose:gc (basic GC logging)
# Or Java 9+: -Xlog:gc*:file=gc.log

# 6. Flight Recorder (low overhead profiling)
-XX:+FlightRecorder
jcmd <pid> JFR.start duration=60s filename=recording.jfr
```

---

## 9. OutOfMemoryError — All Variants

### Variant 1: Java Heap Space (Most Common)

```
OutOfMemoryError: Java heap space
  Cause:   Too many live objects, heap too small
  Fix:
    1. Increase heap: -Xmx4g
    2. Find memory leak (heap dump + MAT analysis)
    3. Reduce object creation in hot paths
    4. Use off-heap storage for large data (Redis, EhCache)
```

### Variant 2: GC Overhead Limit Exceeded

```
OutOfMemoryError: GC overhead limit exceeded
  Cause:   GC running > 98% of time but freeing < 2% heap
           JVM decides it's spending more time GC-ing than working
  Fix:
    1. Increase heap
    2. Find memory leak (usually similar to heap space issue)
    3. Tune GC: -XX:GCOverheadLimitThreshold=90
    4. Disable this check: -XX:-UseGCOverheadLimit (NOT recommended)
```

### Variant 3: Metaspace

```
OutOfMemoryError: Metaspace
  Cause:   Too many classes loaded (code generation, dynamic proxies)
           Spring, Hibernate, CGLIB load many classes
  Fix:
    1. Increase: -XX:MaxMetaspaceSize=512m
    2. Find class loader leaks (class loaded but never unloaded)
    3. Check for: Reflection, dynamic code gen, OSGi bundle issues
    4. Use: jmap -clstats <pid> to see class loaders
```

### Variant 4: Unable to Create Native Thread

```
OutOfMemoryError: unable to create new native thread
  Cause:   OS limit on threads reached (not heap issue!)
           ulimit -u on Linux limits per-user threads
  Fix:
    1. Reduce thread count: use thread pools, not new Thread() per task
    2. Reduce stack size: -Xss256k (smaller per-thread stack)
    3. Increase OS limits: ulimit -u 65536
    4. Use virtual threads (Java 21): millions of threads possible
```

### Variant 5: Direct Buffer Memory

```
OutOfMemoryError: Direct buffer memory
  Cause:   NIO direct ByteBuffers exhausted off-heap memory
           ByteBuffer.allocateDirect() uses native memory, not heap
  Fix:
    1. Increase: -XX:MaxDirectMemorySize=512m
    2. Ensure ByteBuffers are released: buffer.clear() + System.gc()
    3. Use Cleaner API (Java 9+) for explicit release
    4. Check Netty / NIO code for buffer leaks
```

---

## 10. ClassLoader — Architecture & Delegation

### ClassLoader Hierarchy

```
Bootstrap ClassLoader (built into JVM, written in C++)
  ↑ parent
  Loads: java.lang.*, java.util.*, java.io.* etc. (JDK core classes)
  Location: $JAVA_HOME/lib/  (rt.jar pre-Java 9, jdk modules post-Java 9)

Extension/Platform ClassLoader
  ↑ parent
  Loads: javax.*, extensions in $JAVA_HOME/lib/ext/
  Java 9+: Platform ClassLoader (loads platform modules)

Application/System ClassLoader
  ↑ parent
  Loads: YOUR application classes + classpath JARs

Custom ClassLoader (optional)
  Extends ClassLoader
  Used for: plugins, hot reload, app servers, OSGi
```

### Parent Delegation Model

```
When AppClassLoader is asked to load "com.example.MyClass":

1. AppClassLoader → delegates to ExtClassLoader
2. ExtClassLoader → delegates to BootstrapClassLoader
3. Bootstrap tries: "com.example.MyClass" — NOT in JDK → fail
4. Ext tries: "com.example.MyClass" — NOT in ext → fail
5. App loads it: finds it in classpath → loads → returns Class object

WHY parent delegation?
  ✓ Security: can't hijack java.lang.String with your own version
             Bootstrap loads it first → your fake String never tried
  ✓ Uniqueness: same class loaded by same classloader = same Class object
  ✓ Core classes always from JDK, not overridden by app code
```

### Custom ClassLoader

```java
// Use case: hot reload, plugin system, app servers
class PluginClassLoader extends ClassLoader {
    private final Path pluginJar;

    PluginClassLoader(Path jarPath, ClassLoader parent) {
        super(parent);   // set parent for delegation
        this.pluginJar = jarPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 1. Parent delegation happens before findClass is called
        // 2. Only reach here if parent couldn't load the class
        try {
            byte[] classBytes = readClassBytesFromJar(name);
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}

// Hot reload: create new ClassLoader → load new version of class
// Old ClassLoader eligible for GC once no more references
// → class metadata in Metaspace freed
```

---

## 11. Tricky Interview Questions — Deep Explanations

---

### 🔥 TRICK Q1. What is the output? (finalize and GC)

```java
class Resource {
    @Override
    protected void finalize() {
        System.out.println("finalize called");
    }
}

Resource r = new Resource();
r = null;
System.gc();
System.out.println("after gc");
```

**Answer — Non-deterministic, but typically:**
```
after gc
finalize called
```
OR sometimes:
```
finalize called
after gc
```
OR even just:
```
after gc
// finalize may not be called at all!
```

**Why?**
- `System.gc()` is a **hint** — JVM may ignore it
- `finalize()` is called by the **Finalizer thread** asynchronously
- No guarantee on WHEN or WHETHER finalize() runs
- `finalize()` is **deprecated** since Java 9, **removed** in Java 18
- **Never rely on finalize() for cleanup** — use try-with-resources or `Cleaner` API

---

### 🔥 TRICK Q2. Can an object resurrect itself?

```java
class Phoenix {
    static Phoenix instance;

    @Override
    protected void finalize() {
        instance = this;   // RE-ASSIGN to static field — object is alive again!
        System.out.println("I'm back!");
    }
}

Phoenix p = new Phoenix();
p = null;
System.gc();
Thread.sleep(100);

System.out.println(Phoenix.instance);   // not null!
```

**Answer:** Yes! An object can "resurrect" itself in `finalize()` by assigning `this` to a reachable reference.

**But:**
- The next time the object is unreachable, `finalize()` is **NOT called again** (only once per object)
- This is extremely bad practice — unpredictable, confusing, deprecated
- JVM calls finalize() only **once** per object lifetime regardless

---

### 🔥 TRICK Q3. What is Stop-The-World (STW) pause?

```
Stop-The-World pause = ALL application threads are SUSPENDED while GC works

Why? Because GC needs to:
  ✓ Traverse object graph (objects mustn't move while traversing)
  ✓ Move objects during compaction (all references must be updated atomically)
  ✓ Ensure consistent view of heap

Duration determines which GC is best:
  Serial/Parallel: full collection is STW (long pauses)
  G1GC: most work concurrent, short STW pauses
  ZGC/Shenandoah: nearly all concurrent, < 1ms STW

Impact: during STW pause:
  ✓ HTTP requests hang
  ✓ DB transactions pause
  ✓ Real-time systems miss deadlines
  → That's why latency-sensitive apps need G1/ZGC
```

---

### 🔥 TRICK Q4. Difference between Minor GC, Major GC, Full GC

```java
// Minor GC:
//   Scope: Young Gen only (Eden + Survivors)
//   Trigger: Eden is full
//   Pause: Short (Young Gen is small, fast to collect)
//   Side effect: some objects promoted to Old Gen

// Major GC (Old Gen GC):
//   Scope: Old Gen only
//   Trigger: Old Gen fills up
//   Pause: Longer (Old Gen is large)
//   Often preceded by Minor GC (collection cycle)

// Full GC:
//   Scope: ENTIRE heap (Young + Old + Metaspace)
//   Trigger: System.gc(), concurrent mode failure in CMS/G1,
//            explicit trigger from JMX/JConsole
//   Pause: Longest (stops everything)
//   AVOID in production! Log it, investigate cause

// How to detect Full GC from logs (Java 9+):
// [GC pause (G1 Humongous Allocation)] [Full GC (Ergonomics)]
// [Full GC (System.gc())]  ← someone called System.gc()!
```

---

### 🔥 TRICK Q5. Why is String Pool in Heap (Java 7+)?

```
Java 6: String Pool in PermGen
  Problem: PermGen is fixed size (default 64MB)
           intern() or too many string literals → OutOfMemoryError: PermGen space
           Can't GC strings in PermGen during runtime

Java 7+: String Pool moved to Heap
  Benefits:
  ✓ GC can collect unused interned strings (subject to normal GC)
  ✓ Pool size limited only by heap size (-Xmx)
  ✓ -XX:StringTableSize controls number of buckets (default: 65536)
  ✓ No more PermGen OOM from string accumulation

Java 8: PermGen removed entirely → replaced by Metaspace
  String Pool still in Heap
  Class metadata moved to Metaspace (native memory)
```

---

### 🔥 TRICK Q6. What causes StackOverflowError vs OutOfMemoryError?

```java
// StackOverflowError — Stack memory exhausted
void infiniteRecurse() {
    infiniteRecurse();  // each call pushes a frame onto stack
}                       // default ~500-1000 frames before overflow

// Can recover? Sometimes — thread's stack is cleared after SOE
try {
    infiniteRecurse();
} catch (StackOverflowError e) {
    System.out.println("Recovered");  // may work IF stack has room for catch
}

// OutOfMemoryError — Heap memory exhausted
List<byte[]> sink = new ArrayList<>();
while (true) {
    sink.add(new byte[1024 * 1024]);  // keep adding to heap
}
// Eventually: OutOfMemoryError: Java heap space

// KEY DIFFERENCE:
//   StackOverflowError → Stack memory (per-thread, small, default ~1MB)
//   OutOfMemoryError   → Heap memory (shared, large, -Xmx controlled)
//
//   SOE: caused by recursion depth
//   OOM: caused by too many/large live objects in heap
```

---

### 🔥 TRICK Q7. What is the Metaspace and why was PermGen removed?

```
PermGen (Java 7 and before):
  ✓ Part of JVM heap (counted in -Xmx)
  ✗ Fixed size: -XX:MaxPermSize=256m  (easy to OOM)
  ✗ Class metadata + interned strings + class statics all crammed in
  ✗ GC rarely touched PermGen → hard to clean up leaked classloaders
  ✗ Caused famous "OutOfMemoryError: PermGen space" in app servers

Metaspace (Java 8+):
  ✓ Native memory (NOT part of heap, not counted in -Xmx)
  ✓ Grows dynamically by default (no fixed limit like PermGen)
  ✓ Cleaned up when ClassLoader is GC'd (better for plugin/hot-deploy scenarios)
  ✓ String pool moved to Heap (no longer in Metaspace)
  ✓ Removed MaxPermSize flag (use MaxMetaspaceSize instead)

  Warning: unlimited by default → can consume all native memory!
  Always set: -XX:MaxMetaspaceSize=512m in production
```

---

### 🔥 TRICK Q8. How does JVM handle object creation on the heap atomically?

```
Without optimisation: object creation needs:
  1. Allocate memory on heap (needs CAS or lock — expensive!)
  2. Initialise fields to defaults
  3. Run constructor

With TLAB (Thread-Local Allocation Buffer):
  Each thread gets a private "chunk" of Eden (TLAB)
  Allocation in TLAB = just a POINTER BUMP (no CAS, no lock!)
  
  thread.tlab.top += objectSize;  ← atomic from thread's perspective (private!)
  
  When TLAB full:
    → Thread requests new TLAB from Eden (one CAS for the new TLAB)
  When Eden full:
    → Minor GC triggered

Result:
  ✓ Object allocation is effectively O(1) and lock-free per thread
  ✓ JVM can allocate millions of small objects per second
  ✓ "new" in Java is VERY cheap (much cheaper than malloc in C!)
```

---

## 12. Interview Q&A — All FAQs with Answers

### ❓ Q1. What is the difference between JDK, JRE, and JVM?

```
JVM (Java Virtual Machine):
  → Abstract machine that executes Java bytecode
  → Platform-specific implementation (Windows JVM, Linux JVM)
  → Components: Class Loader, Runtime Data Areas, Execution Engine

JRE (Java Runtime Environment):
  → JVM + standard Java class libraries (java.lang, java.util, etc.)
  → What end-users need to RUN Java applications
  → Does NOT include compiler (javac)

JDK (Java Development Kit):
  → JRE + development tools
  → Includes: javac (compiler), javadoc, jar, jdb, jmap, jstack, jvisualvm
  → What DEVELOPERS need to BUILD Java applications

Relationship:
  JDK ⊃ JRE ⊃ JVM
  JDK = JRE + dev tools
  JRE = JVM + class libraries
```

---

### ❓ Q2. What is JIT compilation? How does it improve performance?

```java
// JVM execution modes:
// 1. Interpreted mode: bytecode executed line-by-line
//    Slow but immediate — used initially

// 2. JIT (Just-In-Time) compiled mode:
//    "Hot" methods detected (called many times)
//    JIT compiles hot bytecode → native machine code
//    Subsequent calls execute native code → 10-100x faster

// How HotSpot JIT works:
// C1 (Client) compiler: fast compilation, moderate optimization
// C2 (Server) compiler: slower compilation, heavy optimization (tiered)

// Tiered compilation (default):
// Level 0: Interpreted
// Level 1-3: C1 compiled (progressively more optimized)
// Level 4: C2 compiled (fully optimized)

// JIT optimizations:
// ✓ Inlining (eliminate method call overhead for small methods)
// ✓ Dead code elimination
// ✓ Loop unrolling
// ✓ Escape analysis (allocate short-lived objects on stack instead of heap!)
// ✓ Intrinsics (map Java methods to CPU instructions directly)

// Flag: -server → always use C2 (server mode, default on modern JVMs)
// Flag: -XX:+PrintCompilation → see what JIT is compiling
```

---

### ❓ Q3. What is escape analysis? When does JVM allocate on stack?

```java
// Escape analysis: JIT determines if an object "escapes" the method
// If object doesn't escape → can allocate on STACK (not heap)
// Stack allocation: freed when method returns — no GC needed!

public int sum(int a, int b) {
    Point p = new Point(a, b);   // Does Point escape this method?
    return p.x + p.y;            // NO — p used only locally
    // JIT may allocate p on stack → no heap allocation, no GC!
}

// Object escapes if:
// ✓ Stored in a field (instance or static)
// ✓ Passed to another method that stores it
// ✓ Returned from the method

// Practical impact:
// ✓ Reduces GC pressure for small, short-lived objects
// ✓ Makes Java's "new" even cheaper than it already is
// ✓ Works best with final/effectively-final fields

// See what escape analysis is doing:
// -XX:+DoEscapeAnalysis (default: on)
// -XX:+PrintEscapeAnalysis
```

---

### ❓ Q4. How do you troubleshoot a memory leak in production?

```
Step 1: Detect
  Monitor heap usage over time (Prometheus + Grafana, JMX)
  Pattern: heap usage grows and doesn't drop after GC → leak!

Step 2: Capture heap dump
  jmap -dump:format=b,file=heap.hprof <pid>
  Or: jcmd <pid> GC.heap_dump heap.hprof
  Or: triggered automatically: -XX:+HeapDumpOnOutOfMemoryError

Step 3: Analyze with Eclipse MAT
  Open heap.hprof → "Leak Suspects Report"
  Look for:
    ✓ Largest retained heap (object keeping most memory alive)
    ✓ Objects with high instance count (thousands of same class)
    ✓ Reference chains leading to leak root

Step 4: Common culprits to check
  ✓ Static Maps/Lists growing unbounded
  ✓ ThreadLocal not removed
  ✓ Listener/callback not deregistered
  ✓ Cache with no eviction policy
  ✓ ClassLoader leak in dynamic environments

Step 5: Verify fix
  Monitor heap after fix — confirm steady-state usage
  Run load test — heap should plateau, not grow indefinitely
```

---

### ❓ Q5. What is the difference between -Xms and -Xmx? What happens if they're different?

```
-Xms: initial heap size (minimum, allocated at JVM startup)
-Xmx: maximum heap size (cap — JVM never exceeds this)

If Xms < Xmx (e.g., -Xms512m -Xmx4g):
  ✓ JVM starts with 512MB heap
  ✓ As load increases, JVM expands heap up to 4GB
  ✗ Heap expansion triggers Full GC!
  ✗ CPU spike and pause when heap grows
  ✗ Unpredictable latency under load ramp-up

Best practice for production:
  Set Xms == Xmx: -Xms4g -Xmx4g
  ✓ Heap pre-allocated at startup — no expansion
  ✓ Predictable performance
  ✓ JVM requests full 4GB from OS upfront
  ✗ Startup time slightly longer
  ✗ More RSS memory even when app is idle

Container environments:
  Use -XX:MaxRAMPercentage=75.0 instead of -Xmx
  JVM calculates from container memory limit automatically
```

---

## 13. Quick Reference Cheat Sheet

### JVM Memory Areas Summary

```
Area         | Shared?       | Stores                          | Error
-------------|---------------|----------------------------------|---------------------------
Heap         | All threads   | Objects, arrays, String pool     | OutOfMemoryError: heap space
Metaspace    | All threads   | Class metadata, static vars      | OutOfMemoryError: Metaspace
Stack        | Per thread    | Frames, local vars, references   | StackOverflowError
PC Register  | Per thread    | Current instruction address      | (none)
Native Stack | Per thread    | Native method frames             | (depends on OS)
```

### GC Selection Guide

```
App type                              → Recommended GC
─────────────────────────────────────────────────────
Small app, single CPU, client         → SerialGC (-XX:+UseSerialGC)
Batch, throughput matters most        → ParallelGC (-XX:+UseParallelGC)
General server, balanced              → G1GC (-XX:+UseG1GC) [default Java 9+]
Large heap (>4GB), latency sensitive  → G1GC with tuned MaxGCPauseMillis
Sub-millisecond pauses required       → ZGC (-XX:+UseZGC) [Java 15+]
OpenJDK, low latency alternative      → Shenandoah (-XX:+UseShenandoahGC)
```

### OOM Quick Diagnosis

```
"Java heap space"              → increase -Xmx or fix memory leak
"GC overhead limit exceeded"   → heap too small or leak — increase -Xmx
"Metaspace"                    → set -XX:MaxMetaspaceSize, check class loader leak
"unable to create native thread" → reduce thread count, lower -Xss, raise ulimit
"Direct buffer memory"         → increase -XX:MaxDirectMemorySize, fix NIO buffer leak
```

### Production JVM Startup Template

```bash
java \
  -server \
  -XX:+UseG1GC \
  -Xms4g -Xmx4g \
  -XX:MaxGCPauseMillis=200 \
  -XX:G1HeapRegionSize=8m \
  -XX:MetaspaceSize=256m \
  -XX:MaxMetaspaceSize=512m \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/app/heap.hprof \
  -Xlog:gc*:file=/var/log/app/gc.log:time:filecount=10,filesize=20m \
  -XX:+DisableExplicitGC \
  -Djava.awt.headless=true \
  -jar app.jar
```

---

*Prepared from Claude AI session | Kriti Singh | 8.5 YOE Java Developer*
*Topics: JVM Architecture · Memory Areas · Heap · GC Algorithms · G1GC · ZGC · Tuning · Memory Leaks · OOM · ClassLoader*
