# Java Collections Framework — Interview Prep (8.5 Years Backend Experience)

> Target level: Senior Java Backend Engineer / Lead. Interviewers at this level expect **internal working knowledge**, **complexity analysis**, **thread-safety trade-offs**, and **production war-stories** — not textbook definitions.

---

## 1. Collections Framework — Interface & Class Hierarchy Tree

### 1.1 Core `Collection` Hierarchy

```
java.lang.Iterable
        |
   java.util.Collection
        |
  ------------------------------------------------------
  |                |                    |
 List             Set                 Queue
  |                |                    |
  |         --------------        -------------
  |         |     |      |        |           |
  |      HashSet  |  SortedSet   Deque    PriorityQueue
  |         |     |      |        |
  |   LinkedHashSet | NavigableSet |
  |                 |      |    ArrayDeque
  |             TreeSet <--       LinkedList (implements Deque too)
  |
  --------------------------------------------------
  |            |            |            |
ArrayList   LinkedList   Vector       CopyOnWriteArrayList
                |
             Stack (extends Vector)
```

**Concrete implementation class tree (who extends/implements what):**

```
AbstractCollection
   |-- AbstractList
   |       |-- ArrayList
   |       |-- AbstractSequentialList
   |               |-- LinkedList  (implements List, Deque)
   |-- AbstractSet
   |       |-- HashSet            (backed internally by HashMap)
   |       |       |-- LinkedHashSet
   |       |-- TreeSet            (implements NavigableSet -> SortedSet, backed by TreeMap)
   |-- AbstractQueue
           |-- PriorityQueue
           |-- ArrayDeque

Vector (legacy, synchronized)
   |-- Stack

CopyOnWriteArrayList   (implements List directly, NOT AbstractList)
CopyOnWriteArraySet    (implements Set directly)
ConcurrentLinkedQueue / ConcurrentLinkedDeque
LinkedBlockingQueue / ArrayBlockingQueue / PriorityBlockingQueue / DelayQueue / SynchronousQueue
```

### 1.2 `Map` Hierarchy (NOT a `Collection` subtype — separate root)

```
java.util.Map
   |-- AbstractMap
   |       |-- HashMap
   |       |       |-- LinkedHashMap
   |       |-- TreeMap        (implements NavigableMap -> SortedMap)
   |       |-- WeakHashMap
   |       |-- IdentityHashMap (does NOT extend AbstractMap, but implements Map)
   |
   |-- Hashtable (legacy, synchronized)
   |       |-- Properties
   |
   |-- ConcurrentMap (interface)
   |       |-- ConcurrentHashMap
   |       |-- ConcurrentNavigableMap (interface)
   |               |-- ConcurrentSkipListMap
```

### 1.3 Iterator Hierarchy

```
Iterator
   |-- ListIterator (bidirectional, only for List)

Enumeration (legacy — Vector, Hashtable, Stack)
```

### 1.4 Utility / Helper Classes (not part of the hierarchy but frequently asked)

- `Collections` — static utility methods (`synchronizedX()`, `unmodifiableX()`, `emptyList()`, `sort()`, `binarySearch()`, `Collections.singletonList()`)
- `Arrays` — `Arrays.asList()`, `Arrays.stream()`
- `Comparator` / `Comparable` — sorting contracts
- `Objects` — `Objects.equals()`, `Objects.hash()`

---

## 2. Foundational / Warm-up Questions (Expect these first — answer crisply)

### Q1. Why doesn't `Map` extend `Collection`?
A `Map` stores **key-value pairs**, whereas `Collection` is designed for a **group of single elements**. The method contracts differ fundamentally — e.g., `add(Object)` in `Collection` vs `put(K,V)` in `Map`; iterating a `Map` doesn't make sense element-wise, you iterate `entrySet()`, `keySet()`, or `values()` instead. Forcing `Map` into `Collection` would create semantic and API mismatches (Josh Bloch, the Collections Framework designer, explicitly called this out).

### Q2. Difference between `Collection` and `Collections`?
`Collection` is a root interface. `Collections` is a **utility class** (like `Arrays`) with static helper methods — sorting, synchronization wrappers, immutability wrappers, `max()/min()`, etc.

### Q3. `Iterable` vs `Iterator`?
`Iterable` has a single method `iterator()` that returns an `Iterator`. Implementing `Iterable` allows an object to be used in a for-each loop. `Iterator` is the actual cursor object with `hasNext()`, `next()`, `remove()`.

---

## 3. List Implementations

### Q4. `ArrayList` vs `LinkedList` — when would you actually pick one in production?
| Aspect | ArrayList | LinkedList |
|---|---|---|
| Backing structure | Dynamic (resizable) array | Doubly linked list |
| Random access `get(i)` | O(1) | O(n) |
| Insert/delete at **middle** | O(n) — array shift | O(n) — traversal dominates, but O(1) once positioned |
| Insert/delete at **head** | O(n) | O(1) |
| Memory overhead | Lower (contiguous array, some unused capacity) | Higher (each node stores 2 pointers + object header, ~40 bytes overhead per element) |
| Cache locality | Good (contiguous memory) | Poor (nodes scattered on heap) |

**Reality check for interviewers:** In practice, `ArrayList` outperforms `LinkedList` even for head insertions in most JVM benchmarks once you account for cache-miss penalty and pointer-chasing cost — **`LinkedList` is rarely the right choice** today. It's mainly useful when you need `Deque` semantics (use `ArrayDeque` instead, which is faster) or true O(1) mid-list splicing when you already hold the node reference (rare in Java since you can't hold raw node references outside the JDK internals).

### Q5. How does `ArrayList` grow internally? What is the growth factor?
- Default initial capacity = 10 (lazily initialized on first `add()` since Java 8, not at construction).
- On overflow, new capacity = `oldCapacity + (oldCapacity >> 1)` → **1.5x growth**, not 2x (common wrong answer).
- Growth: `Arrays.copyOf()` is called → O(n) copy operation. Amortized cost of `add()` is still O(1).
- **Tricky follow-up:** If you know the size upfront, always use `new ArrayList<>(expectedSize)` to avoid repeated resize+copy — a real performance issue in hot paths.

### Q6. Is `Arrays.asList()` a `List`? What breaks if you call `.add()` on it?
`Arrays.asList()` returns a **fixed-size list backed by the array itself** (an inner class `Arrays.ArrayList`, not `java.util.ArrayList`). It supports `set()` (mutates the backing array) but `add()`/`remove()` throw `UnsupportedOperationException` because it doesn't implement the resizing logic. **Classic tricky question** — many candidates assume it's a mutable `ArrayList`.

### Q7. `CopyOnWriteArrayList` — internal working and when to use it?
- Every mutating operation (`add`, `set`, `remove`) creates a **new copy of the underlying array**, then swaps the reference (via `ReentrantLock`, not full sync on read).
- Reads are **lock-free** and never blocked — they iterate over a fixed snapshot array.
- Iterator is **fail-safe** (won't throw `ConcurrentModificationException`) but reflects a **stale snapshot** — it will NOT see concurrent modifications.
- **Use case:** read-heavy, write-rare scenarios (e.g., list of event listeners, cached config lists).
- **Trap:** Writing frequently to a large list is expensive — O(n) copy on every write. Don't use it for write-heavy workloads.

---

## 4. Set Implementations

### Q8. How is `HashSet` internally implemented?
`HashSet` is backed internally by a `HashMap` where every element you add becomes a **key**, and the value is a constant dummy object:
```java
private static final Object PRESENT = new Object();
public boolean add(E e) {
    return map.put(e, PRESENT) == null;
}
```
So `HashSet` inherits all `HashMap` characteristics: O(1) average add/contains/remove, no ordering guarantee, allows one `null` element (since one null key is allowed in `HashMap`).

### Q9. `HashSet` vs `LinkedHashSet` vs `TreeSet`?
- `HashSet`: no order guarantee, O(1) avg ops, uses `hashCode()`/`equals()`.
- `LinkedHashSet`: maintains **insertion order** via an internal doubly-linked list threading through `HashMap` entries — O(1) avg ops with slightly higher memory overhead (~extra 2 pointers per entry).
- `TreeSet`: maintains **sorted order** (natural ordering or via `Comparator`), backed by a `TreeMap` (Red-Black Tree) — O(log n) for add/remove/contains.

### Q10. Can a `TreeSet`/`TreeMap` store `null`?
- `TreeSet`/`TreeMap` with **natural ordering** → throws `NullPointerException` on inserting null (can't call `compareTo()` on null).
- If you supply a **custom `Comparator`** that explicitly handles null, it's technically possible — but strongly discouraged.
- `HashMap`/`HashSet` → allow **one null key**. `Hashtable` → allows **no null key or value** (throws NPE) — a classic FAQ contrast.

---

## 5. HashMap — The Most Important Topic at This Level

### Q11. Explain `HashMap` internal working in Java 8+ in detail.
1. Backed by an array of `Node<K,V>[] table` (called **buckets**).
2. `hash(key)` is computed, then **spread** using: `(h = key.hashCode()) ^ (h >>> 16)` — this XOR-folds the upper 16 bits into the lower 16 bits to reduce collision when table size is small (better bit distribution).
3. Bucket index = `(n - 1) & hash` where `n` is table length (always a power of 2, enabling this cheap modulo via bitmasking instead of `%`).
4. Each bucket holds a **linked list** of entries with the same bucket index (collision chain).
5. **Java 8 improvement:** if a single bucket's chain length exceeds `TREEIFY_THRESHOLD (8)` **AND** table capacity ≥ `MIN_TREEIFY_CAPACITY (64)`, the linked list is converted to a **Red-Black Tree** for that bucket — degrading worst-case lookup from O(n) to O(log n). If capacity < 64, it resizes instead of treeifying.
6. Default initial capacity = 16, default load factor = 0.75. Resize (**rehashing**) triggers when `size > capacity * loadFactor`. New capacity = `oldCapacity * 2`.
7. On resize, Java 8 uses a clever trick: because capacity always doubles, each old bucket's entries split into exactly two new buckets (`same index` or `index + oldCapacity`) based on one extra bit of the hash — avoiding a full `hashCode()`/`equals()` recomputation for every entry (unlike Java 7).

### Q12. Why does `HashMap` use `capacity` as a power of 2?
So that `(n - 1) & hash` is a valid substitute for `hash % n`, since bitwise AND is significantly faster than modulo, AND `(n-1)` in binary is all 1-bits only when `n` is a power of 2 (e.g., 16 → 15 → `0b1111`), guaranteeing uniform bit-masking across the hash's lower bits.

### Q13. What was the infamous `HashMap` infinite loop bug in Java 7 under concurrent access?
In Java 7, resizing used a **head-insertion** strategy when rehashing linked list buckets — during a concurrent resize by two threads, this could create a **circular reference** in the linked list (a node pointing back to a previous node), causing `get()` to loop forever and spike CPU to 100%. This was a real production incident pattern (a well-known Discord/LinkedIn-style outage cause pre-Java8).
**Java 8 fix:** Resize now preserves relative order (splits into "lo" and "hi" lists using tail-insertion), which structurally cannot form a cycle — but **`HashMap` is still NOT thread-safe** even in Java 8 (you can lose updates or see `ConcurrentModificationException`); it just no longer infinite-loops.
**Correct answer if asked "is this fixed, so is HashMap now safe for multithreading?"** → **No.** The infinite loop is fixed, but data corruption/lost writes are still possible. Never use plain `HashMap` under concurrent writes — use `ConcurrentHashMap`.

### Q14. What's the contract between `equals()` and `hashCode()`? What breaks if you violate it?
- If `a.equals(b) == true`, then `a.hashCode() == b.hashCode()` **must** hold.
- The reverse is NOT required — two unequal objects CAN share a hash code (collision), that's expected and handled.
- **If you override `equals()` without overriding `hashCode()`:** two "equal" objects can land in different buckets → `HashMap`/`HashSet` will treat them as **different keys**, allowing duplicate "equal" entries and causing `get()` to fail to find a key that "should" match by `equals()`. This is one of the most common real bugs in production code (e.g., using a mutable POJO with only `equals()` overridden as a `HashMap` key or `HashSet` element).
- **Related trap:** If you use a **mutable object as a `HashMap` key** and later mutate a field involved in `hashCode()`, the entry becomes "lost" — `get()` will look in the wrong bucket (computed from the new hash), even though the object still `.equals()` a lookup key logically. Always use immutable keys (String, wrapper types, or immutable custom classes).

### Q15. What is load factor and why 0.75?
Load factor determines the density trade-off: `threshold = capacity * loadFactor`.
- Higher load factor (closer to 1) → less memory wasted, but more collisions → slower lookups.
- Lower load factor → faster lookups, but wastes memory + resizes more often.
- **0.75 is the empirically chosen sweet spot** (per JDK docs) balancing time and space cost per the standard statistical analysis of hash collisions (Poisson distribution of bucket occupancy).

### Q16. `HashMap` vs `Hashtable` vs `ConcurrentHashMap` vs `Collections.synchronizedMap()`?
| | HashMap | Hashtable | Collections.synchronizedMap(HashMap) | ConcurrentHashMap |
|---|---|---|---|---|
| Thread-safe | No | Yes (every method `synchronized`) | Yes (wrapper synchronizes every method on one lock) | Yes (fine-grained) |
| Null key/value | 1 null key, multiple null values | No nulls allowed at all | Depends on backing map | **No null keys or values allowed** (deliberate design choice — see Q17) |
| Performance under contention | N/A | Poor (single lock for entire table) | Poor (single lock for entire table + external iteration needs manual sync) | Good — Java 8 uses **per-bin (bucket) CAS + synchronized blocks**, not a single global lock |
| Iterator | Fail-fast | Fail-fast (Enumeration is not) | Fail-fast | **Weakly consistent** (fail-safe, doesn't throw CME, may or may not reflect concurrent updates) |

### Q17. Why does `ConcurrentHashMap` disallow `null` keys/values, but `HashMap` allows them?
Doug Lea's (java.util.concurrent author) reasoning: in a concurrent map, `map.get(key) == null` is ambiguous — it could mean "the key isn't present" OR "the key is present but mapped to null." In a single-threaded `HashMap`, you can disambiguate with `containsKey()` safely because nothing else can mutate the map between your two calls. In a **concurrent** map, another thread could remove the key between your `get()` and your `containsKey()` check — a **race condition (TOCTOU)** — so the ambiguity is unresolvable safely. Hence nulls are banned outright to prevent this class of bug.

### Q18. Explain `ConcurrentHashMap`'s internal locking evolution (Java 7 vs Java 8).
- **Java 7:** Used **Segment-based locking** — the map was divided into (default 16) `Segment`s, each an independently lockable mini hash table (`Segment extends ReentrantLock`). Only threads writing to the *same* segment blocked each other → default concurrency level of 16 concurrent writers.
- **Java 8:** Removed segments entirely. Now locks at the **individual bucket (bin) level** using `synchronized` on the **first node of that bin**, combined with `CAS` (`Unsafe.compareAndSwapObject`) operations for inserting into empty bins. This gives much finer-grained concurrency (as fine as per-bucket, not per-segment), lower memory footprint, and better performance under high thread counts. Treeification (Q11) applies here too, for long chains.
- Size is tracked via a striped counter (`CounterCell[]`) similar to `LongAdder`, to avoid `size()` becoming a contention hotspot.

### Q19. Why does `HashMap` need to "spread" the hash with `(h ^ (h >>> 16))` instead of using `hashCode()` directly?
Many `hashCode()` implementations (especially default `Object.hashCode()` or simple user ones) vary mostly in the **lower bits**. Since bucket index only uses `(n-1) & hash` (i.e., only the lowest bits when `n` is small, e.g., 16 → uses only 4 bits), high-order bits would otherwise never influence bucket placement, worsening collision rates for keys whose hash codes differ mainly in high bits. XOR-folding the top 16 bits into the bottom 16 bits mixes that entropy in cheaply (single XOR, no multiplication) — a good perf-vs-quality trade-off Sun/Oracle engineers chose deliberately.

---

## 6. Fail-Fast vs Fail-Safe Iterators

### Q20. What is `ConcurrentModificationException` (CME) and when exactly does it occur?
`ArrayList`, `HashMap`, `HashSet`, etc. maintain a `modCount` field incremented on every structural modification (add/remove, NOT `set()`). The iterator captures `expectedModCount` at creation; every `next()` call checks `modCount == expectedModCount` — if a mismatch is found, it throws `ConcurrentModificationException`.
**Key nuance:** This is **not primarily a concurrency detection mechanism** — it fires even in **single-threaded** code if you modify a collection directly while iterating it (e.g., `list.remove(x)` inside a for-each loop) — this is the classic tricky trap.

```java
List<String> list = new ArrayList<>(List.of("a","b","c"));
for (String s : list) {
    if (s.equals("b")) list.remove(s); // throws CME on next() call
}
```
**Correct fix:** use `Iterator.remove()`, or `list.removeIf(s -> s.equals("b"))`, or iterate a copy.

### Q21. Is CME guaranteed to be thrown? Why or why not?
**No — it's a "best-effort" mechanism**, explicitly documented as such. It's meant to catch bugs early, not to guarantee correctness. There are timing windows where a modification isn't detected (e.g., removing the second-to-last element sometimes doesn't trigger it because the iterator's `hasNext()` check can short-circuit before the mismatch is observed). **You should never rely on it as a concurrency-safety mechanism.**

### Q22. What's a "fail-safe" iterator? Give examples.
Iterators that operate on a **cloned/snapshot copy of the data** (`CopyOnWriteArrayList`) or use **weakly consistent traversal** (`ConcurrentHashMap`, `ConcurrentLinkedQueue`) — they never throw CME. Trade-off: they might not reflect the most recent updates (stale view) — you trade strict consistency for availability & safety.

---

## 7. Comparable vs Comparator

### Q23. `Comparable` vs `Comparator` — differences and use-cases?
| | Comparable | Comparator |
|---|---|---|
| Package | `java.lang` | `java.util` |
| Method | `compareTo(T o)` | `compare(T o1, T o2)` |
| Modifies class? | Yes, class implements it itself (defines **natural ordering**, single sort order) | No, external — you can define **multiple** sort strategies without touching the original class |
| Used by | `Collections.sort(list)`, `TreeMap`/`TreeSet` default | `Collections.sort(list, comparator)`, `list.sort(comparator)` |

### Q24. Tricky: `TreeMap`/`TreeSet` uses `compareTo`/`compare` for equality, NOT `equals()`. What breaks because of this?
If your `Comparator`/`compareTo` implementation says two objects are "equal" (`compare() == 0`) but your `equals()` says they're different, a `TreeSet`/`TreeMap` will treat them as **duplicate keys and silently discard/overwrite one**, even though `.equals()` would say they're distinct — this is called **"inconsistent with equals"** and is explicitly warned about in the JDK docs. This is a favorite gotcha question at senior levels.

```java
TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
set.add("Apple");
set.add("apple"); // NOT added! compare() == 0, treated as duplicate — even though equals() differs
System.out.println(set.size()); // prints 1, not 2
```

### Q25. How do you sort by multiple fields cleanly (Java 8+)?
```java
list.sort(Comparator.comparing(Employee::getDept)
                     .thenComparing(Employee::getSalary, Comparator.reverseOrder())
                     .thenComparing(Employee::getName));
```
Discuss `Comparator.comparing()`, `.thenComparing()`, `.reversed()`, `.nullsFirst()/.nullsLast()` — expect a live-coding variant of this.

---

## 8. Queue / Deque

### Q26. `Queue` vs `Deque` vs `Stack`?
- `Queue`: FIFO — `offer()/poll()/peek()`.
- `Deque` (Double Ended Queue): supports insertion/removal at **both ends** — `offerFirst/offerLast/pollFirst/pollLast`. Can be used as both a **Queue** (FIFO) and a **Stack** (LIFO).
- `Stack` (legacy, extends `Vector`) is **synchronized** (slow) and considered obsolete — **`ArrayDeque` is the recommended replacement for stack use-cases** (`push()/pop()` are defined on `Deque` too).

### Q27. Why is `ArrayDeque` preferred over `LinkedList` for stack/queue operations?
`ArrayDeque` is backed by a **resizable circular array** — better cache locality, no per-node object overhead, generally faster for both stack and queue operations, and it disallows `null` elements (helps catch bugs early, since `null` in a queue is ambiguous with "queue is empty" from `poll()`'s return value contract).

### Q28. `PriorityQueue` internals?
Backed by a **binary heap** stored in an array (min-heap by default, based on natural ordering or a supplied `Comparator`). `offer()`/`poll()` are O(log n); `peek()` is O(1). **Trap:** iterating a `PriorityQueue` with a plain iterator does **NOT** give you sorted order — only `poll()` repeatedly does. This surprises many candidates.

---

## 9. Immutability & Java 9+ Factory Methods

### Q29. `Collections.unmodifiableList()` vs `List.of()` (Java 9+) — any difference?
- `Collections.unmodifiableList(list)` returns a **view wrapper** — if the underlying original list is mutated, the "unmodifiable" view reflects those changes too (it's not truly immutable, just write-protected from that reference).
- `List.of(...)` (Java 9+) creates a **truly immutable**, fixed-size collection with **no backing mutable reference** — and it explicitly **disallows null elements** (throws NPE), unlike `Arrays.asList()` which allows nulls.
- Both throw `UnsupportedOperationException` on mutation attempts, but for different underlying reasons.

### Q30. What happens if you call `.add()` on a list returned by a Stream `.collect(Collectors.toList())`?
**Implementation-dependent, not guaranteed immutable** — the javadoc explicitly does not promise mutability *or* immutability for `Collectors.toList()`; in current JDKs it typically returns a mutable `ArrayList`, but relying on this is fragile. If you need a guaranteed immutable result, use `Collectors.toUnmodifiableList()` (Java 10+) or `.toList()` (Java 16+, which **is** immutable, unlike `Collectors.toList()`).

---

## 10. Rapid-Fire "Gotcha" / Tricky Questions with Explanations

**Q31. Can you modify a list while iterating with `Iterator.remove()`?**
Yes — `Iterator.remove()` (and `ListIterator.add()/set()`) is explicitly safe because it updates `expectedModCount` in sync with `modCount` internally. Only *direct* collection mutation during iteration is unsafe.

**Q32. What's the time complexity of `contains()` on `ArrayList` vs `HashSet` vs `TreeSet`?**
`ArrayList`: O(n) linear scan. `HashSet`: O(1) average, O(log n) worst-case (Java 8+ treeified buckets) or O(n) worst case pre-Java-8. `TreeSet`: O(log n) guaranteed (Red-Black tree).

**Q33. Why is `String` a popular `HashMap` key, and how does `String` optimize repeated `hashCode()` calls?**
`String` caches its computed `hashCode()` in a private field after first computation (since `String` is immutable, the hash can never change) — so repeated `hashCode()` calls after the first are O(1) lookups, not recomputation. This is why interning/using `String` keys in hot-path `HashMap`s is cheap.

**Q34. What happens if `hashCode()` is not overridden — what's the default behavior?**
Default `Object.hashCode()` is typically derived from the object's memory address / an internal JVM identity hash — meaning two logically "equal" objects (by field values) will very likely land in different buckets unless you override both `equals()` and `hashCode()` consistently.

**Q35. Difference between `size()` and `capacity()` for `ArrayList`? Is there even a `capacity()` method?**
There's **no public `capacity()` method** on `ArrayList` — this is a trick question. `size()` gives the number of actual elements; internal array capacity is a private implementation detail (you can only influence it via the constructor or indirectly via `ensureCapacity()`/`trimToSize()`).

**Q36. Can two unequal objects have the same `hashCode()`? Is that a bug?**
No, it's not a bug — it's an expected and unavoidable phenomenon called a **hash collision** (pigeonhole principle: infinite possible objects, finite 32-bit hash space). Well-designed hash functions minimize collisions but cannot eliminate them; `HashMap`/`HashSet` are explicitly designed to handle collisions via chaining/treeification.

**Q37. Why shouldn't you use a mutable object as a `HashMap` key? (deeper than Q14)**
Beyond "losing" the entry, consider: you insert key `k` (hash = H1), later mutate a field so `hashCode()` now returns H2. The `Entry` **physically still lives in the bucket for H1**, but `map.get(k)` now computes H2 and looks in the wrong bucket → returns `null` even though the object is "in" the map. `containsKey()` similarly fails. The entry is essentially **orphaned / permanently unreachable** except by iterating all buckets manually. This is a genuinely nasty production bug class (seen with mutable `equals()/hashCode()` DTOs used as cache keys).

**Q38. `WeakHashMap` — what's the use case?**
Keys are held via `WeakReference` — if a key has no other strong references anywhere in the application, it becomes eligible for GC and its entry is **automatically removed from the map** on the next GC cycle. Classic use case: **memory-sensitive caches** keyed by objects whose lifecycle you don't control (e.g., class-metadata caches, listener registries) — avoids memory leaks from "forgotten" cache entries.

**Q39. `IdentityHashMap` — how is it different, and why does it exist?**
Uses **reference equality (`==`)** instead of `.equals()`/`.hashCode()` for key comparisons — internally uses `System.identityHashCode()`. Used in scenarios needing strict object-identity semantics regardless of overridden `equals()` — e.g., **object graph traversal / serialization frameworks** (detecting cycles by object identity, not logical equality), or topology-preserving deep-clone algorithms.

**Q40. What's the difference between `remove(int index)` and `remove(Object o)` on `List<Integer>`? Classic autoboxing trap.**
```java
List<Integer> list = new ArrayList<>(List.of(10, 20, 30));
list.remove(1);        // removes element AT INDEX 1 → removes "20"
list.remove(Integer.valueOf(1)); // removes the Integer OBJECT "1" (not present, no-op)
```
`remove(int)` is overload-resolved to the index-based method due to Java's preference for exact primitive match over autoboxing. **Very common live-coding trick question.**

**Q41. Can you put a `HashMap` inside a `HashSet`, or use it as a `Set` element? What could go wrong?**
Technically legal (compiles fine), but `HashMap` doesn't override `equals()`/`hashCode()` meaningfully for content-based comparison beyond identity in a way that's safe if mutated post-insertion — same mutable-key problem as Q37, just one level up. Generally an anti-pattern.

**Q42. Explain why `Vector`/`Stack`/`Hashtable` are considered "legacy" and generally avoided today.**
They predate the Collections Framework (JDK 1.0) and were retrofitted to implement `List`/`Map` in JDK 1.2. They use **coarse-grained `synchronized` on every method**, even in single-threaded contexts, causing unnecessary performance overhead. Modern alternatives: `ArrayList` (+ `Collections.synchronizedList` or `CopyOnWriteArrayList` if needed), `ArrayDeque`, `ConcurrentHashMap`.

**Q43. What is the `Enumeration` interface, and how does it differ from `Iterator`?**
Legacy (JDK 1.0) equivalent of `Iterator`, used by `Vector`/`Hashtable`. Has `hasMoreElements()`/`nextElement()`. **No `remove()` method** — read-only traversal, and **not fail-fast**. Superseded by `Iterator` in JDK 1.2.

**Q44. `Collections.sort()` — what sorting algorithm does it use, and is it stable?**
For `List` (Object references): **TimSort** (a hybrid of merge sort + insertion sort, adaptive to already-sorted runs) — O(n log n) worst case, and **stable** (equal elements retain relative order — important when doing multi-key sort via `thenComparing`).
For primitive arrays (`Arrays.sort(int[])`): **Dual-Pivot QuickSort** — faster on primitives but **NOT stable** (doesn't matter since primitives have no "identity" beyond value).

**Q45. Why can't you create a generic array in Java, e.g. `new T[10]`, and how does this relate to `ArrayList` internals?**
Due to **type erasure**, generic type info isn't available at runtime, so the JVM can't verify array element types (arrays are *reifiable* / covariant and enforce runtime type checks via `ArrayStoreException`, which generics can't support safely). This is exactly why `ArrayList` internally stores elements as `Object[]` and casts on retrieval — a frequently asked "why" question tied to generics + collections together.

**Q46. What does `Collections.emptyList()` return, and why prefer it over `new ArrayList<>()`?**
Returns a **shared singleton immutable empty list instance** — avoids unnecessary object allocation for a common no-op case, and its immutability communicates intent clearly (caller shouldn't try to mutate it). Similarly `Collections.emptyMap()`, `Collections.emptySet()`.

**Q47. `size()` on `ConcurrentHashMap` — is it always accurate?**
Not necessarily at the exact instant of the call under concurrent modification — it's a **best-effort estimate** (though generally quite accurate due to the striped `CounterCell` mechanism from Q18). For an exact-at-a-point-in-time count under heavy concurrent mutation, no lock-free structure can give a perfectly consistent snapshot without external synchronization — a good discussion point on CAP-style trade-offs even within a single JVM.

**Q48. What's the difference between `HashMap.remove()` behavior on structural modification and its effect on iterators of `keySet()`/`values()`/`entrySet()`?**
These three views are **backed by the same map** — removing via the view's iterator (`entrySet().iterator().remove()`) or via `map.remove(key)` directly both correctly update `modCount`, but modifying the `Map` object directly *while* iterating any of these views (without going through the iterator) triggers CME on the view's iterator — same underlying `modCount` mechanism.

---

## 11. Performance & Complexity Cheat-Sheet (memorize for rapid-fire rounds)

| Operation | ArrayList | LinkedList | HashMap | TreeMap | HashSet | TreeSet | ArrayDeque | PriorityQueue |
|---|---|---|---|---|---|---|---|---|
| get/access by index | O(1) | O(n) | — | — | — | — | O(n)* | — |
| get by key | — | — | O(1) avg | O(log n) | — | — | — | — |
| add (end/tail) | O(1) amortized | O(1) | O(1) avg | O(log n) | O(1) avg | O(log n) | O(1) amortized | O(log n) |
| add (beginning/head) | O(n) | O(1) | — | — | — | — | O(1) amortized | — |
| contains | O(n) | O(n) | O(1) avg | O(log n) | O(1) avg | O(log n) | O(n) | O(n) |
| remove (by value) | O(n) | O(n) | O(1) avg | O(log n) | O(1) avg | O(log n) | O(n) | O(log n) |

*ArrayDeque doesn't support O(1) index access; listed for completeness only.

---

## 12. Scenario / System-Design-Flavored Questions (common at 8+ YOE)

**Q49.** *"You have a `HashMap<String, List<Order>>` shared across threads in a Spring Boot service. Under load, you're seeing missing entries / occasional `NullPointerException` on reads. Diagnose and fix."*
> Expected discussion: plain `HashMap` is not thread-safe for concurrent writes → data races, lost updates, or (rarely, pre-Java-8-style corruption). Fix options ranked: (1) `ConcurrentHashMap` with `computeIfAbsent()` for atomic get-or-create of the list (avoid the check-then-act race of `if (!map.containsKey(k)) map.put(k, new ArrayList<>())`), (2) ensure the inner `List` is also either `CopyOnWriteArrayList` or externally synchronized if mutated concurrently, (3) consider `ConcurrentHashMap.merge()` for atomic aggregation.

**Q50.** *"How would you design a thread-safe LRU cache using Java Collections?"*
> Expected: `LinkedHashMap` with `accessOrder=true` constructor flag + override `removeEldestEntry()` to cap size, wrapped with `Collections.synchronizedMap()` **or**, better, guarded manually with a `ReentrantReadWriteLock` since `LinkedHashMap`'s access-order reordering on `get()` is itself a *structural* mutation requiring exclusive locking even for reads — a subtle but important point (this is why `Collections.synchronizedMap` isn't fully sufficient here without also synchronizing the `get()` calls at the caller side, since the internal reordering isn't atomic with the read for compound operations).

**Q51.** *"Would you use `ConcurrentHashMap` or `Collections.synchronizedMap(new HashMap<>())` in a high-throughput read/write cache, and why?"*
> `ConcurrentHashMap` — fine-grained locking (per-bucket) vs. `synchronizedMap`'s single global lock which serializes ALL access including reads, becoming a severe bottleneck under concurrency. Also mention: `synchronizedMap` requires **manual external synchronization during iteration** (`synchronized(map) { for(...) }`) even though individual method calls are thread-safe, because iteration is inherently a multi-step compound operation.

**Q52.** *"How do you choose the initial capacity of a `HashMap` if you know you'll insert exactly 1000 entries?"*
> To avoid resizing: `capacity ≥ expectedSize / loadFactor`, so for 1000 entries at default 0.75 load factor → `1000 / 0.75 ≈ 1334` → round up to next power of 2 → **2048**. In practice: `new HashMap<>(1024)` triggers one resize around 768 entries; `new HashMap<>(2048)` avoids any resize for 1000 entries. This is a real perf-tuning question for hot-path code (e.g., request-scoped maps built per API call).

---

## 13. "Explain Like You're Whiteboarding" — Be Ready to Draw/Describe

1. A `HashMap` bucket array with 2-3 collision chains, showing how `hash & (n-1)` maps keys to buckets.
2. Java 8 resize splitting: show how bucket `i` splits into `i` (lo) and `i + oldCap` (hi) based on one extra hash bit.
3. `ConcurrentHashMap` bucket-level `synchronized` + CAS insertion into an empty bin.
4. Red-Black tree treeification of a bucket once it crosses the threshold of 8, and untreeify back to list below 6 (hysteresis to avoid thrashing).
5. `CopyOnWriteArrayList` write path: lock → copy array → mutate copy → publish new reference (volatile array field) → unlock.

---

## 14. Final Round: One-Liners You Must Get Right Instantly

- Default `HashMap` capacity: **16**. Default load factor: **0.75**.
- `TREEIFY_THRESHOLD` = **8**, `UNTREEIFY_THRESHOLD` = **6**, `MIN_TREEIFY_CAPACITY` = **64**.
- `ArrayList` growth factor: **1.5x** (not 2x).
- `HashMap` allows **1 null key**, unlimited null values. `Hashtable`/`ConcurrentHashMap` allow **none**.
- `TreeMap`/`TreeSet` (natural order) → **null throws NPE**.
- `Collectors.toList()` mutability → **unspecified/implementation detail**; use `.toList()` (Java 16+) for guaranteed immutability.
- Fail-fast: `ArrayList`, `HashMap`, `HashSet`. Fail-safe/weakly-consistent: `CopyOnWriteArrayList`, `ConcurrentHashMap`, `ConcurrentLinkedQueue`.
- `String.hashCode()` is **cached** after first computation (immutability enables this).
- `Arrays.asList()` → fixed-size, backed by array, `set()` works, `add()`/`remove()` throw `UnsupportedOperationException`.

---

### How to use this file
- Do a first pass reading top to bottom.
- Second pass: cover the answers and self-quiz using only the **Q-numbers**.
- Third pass (day before interview): only review **Section 10 (Tricky), Section 11 (Complexity table), Section 14 (One-liners)**.
