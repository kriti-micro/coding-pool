# 📦 Java Collections — Complete Interview Guide
> Level: 8.5 Years Experience | MNC / Product Companies
> Topics: Hierarchy · List · Set · Map · Queue · Internals · Tricky Questions · FAQ

---

## 📌 Table of Contents

1. [Collection Hierarchy — Full Tree](#1-collection-hierarchy--full-tree)
2. [List — ArrayList, LinkedList, Vector, Stack](#2-list--arraylist-linkedlist-vector-stack)
3. [Set — HashSet, LinkedHashSet, TreeSet](#3-set--hashset-linkedhashset-treeset)
4. [Map — HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap](#4-map--hashmap-linkedhashmap-treemap-concurrenthashmap)
5. [Queue & Deque — PriorityQueue, ArrayDeque](#5-queue--deque--priorityqueue-arraydeque)
6. [Concurrent Collections](#6-concurrent-collections)
7. [Tricky Interview Questions — With Deep Explanations](#7-tricky-interview-questions--with-deep-explanations)
8. [Collections Utility Class — FAQ](#8-collections-utility-class--faq)
9. [Java 8+ Streams with Collections](#9-java-8-streams-with-collections)
10. [Interview Q&A — All FAQs with Answers](#10-interview-qa--all-faqs-with-answers)
11. [Quick Reference Cheat Sheet](#11-quick-reference-cheat-sheet)

---

## 1. Collection Hierarchy — Full Tree

```
java.lang.Iterable<T>
│
└── java.util.Collection<T>
    │
    ├── java.util.List<T>                          (ordered, index-based, duplicates allowed)
    │   ├── ArrayList<T>                           ← resizable array, O(1) get, O(n) insert
    │   ├── LinkedList<T>                          ← doubly linked, O(1) add/remove at ends
    │   ├── Vector<T>                              ← synchronized ArrayList (legacy, avoid)
    │   │   └── Stack<T>                           ← LIFO, extends Vector (legacy, use Deque)
    │   └── CopyOnWriteArrayList<T>                ← thread-safe, writes copy entire array
    │
    ├── java.util.Set<T>                           (no duplicates)
    │   ├── HashSet<T>                             ← backed by HashMap, no order, O(1) ops
    │   ├── LinkedHashSet<T>                       ← insertion order maintained
    │   ├── TreeSet<T>                             ← sorted (natural/Comparator), O(log n)
    │   ├── EnumSet<E>                             ← for enum types, ultra-fast bit vector
    │   └── CopyOnWriteArraySet<T>                 ← thread-safe Set
    │
    └── java.util.Queue<T>                         (FIFO ordering)
        ├── PriorityQueue<T>                       ← heap-based, natural/custom ordering
        ├── ArrayDeque<T>                          ← resizable array deque, faster than Stack
        └── java.util.Deque<T>                     (double-ended queue)
            ├── ArrayDeque<T>                      ← also implements Deque
            └── LinkedList<T>                      ← also implements Deque


java.util.Map<K,V>                                 (NOT a Collection — key-value pairs)
│
├── HashMap<K,V>                                   ← no order, O(1) ops, 1 null key
├── LinkedHashMap<K,V>                             ← insertion / access order
├── TreeMap<K,V>                                   ← sorted by key, O(log n) ops
├── Hashtable<K,V>                                 ← synchronized, no null (legacy, avoid)
├── EnumMap<K extends Enum, V>                     ← optimized for enum keys
├── IdentityHashMap<K,V>                           ← uses == instead of equals()
├── WeakHashMap<K,V>                               ← keys GC-eligible when no other refs
└── java.util.concurrent.ConcurrentHashMap<K,V>   ← thread-safe, no full lock


─── Interfaces Summary ───────────────────────────────────────────────────────
  Iterable          → enables for-each loop
  Collection        → base: add, remove, size, iterator
  List              → ordered, index-based: get(i), set(i), indexOf
  Set               → no duplicates: add returns false for duplicate
  SortedSet         → extends Set: first(), last(), headSet(), tailSet()
  NavigableSet      → extends SortedSet: floor(), ceiling(), higher(), lower()
  Queue             → FIFO: offer(), poll(), peek()
  Deque             → double-ended: offerFirst(), offerLast(), pollFirst(), pollLast()
  Map               → key-value: put(), get(), remove(), entrySet()
  SortedMap         → sorted keys: firstKey(), lastKey(), headMap(), tailMap()
  NavigableMap      → extends SortedMap: floorKey(), ceilingKey()
  Comparable        → natural ordering: compareTo()
  Comparator        → external ordering: compare()
```

---

## 2. List — ArrayList, LinkedList, Vector, Stack

### ArrayList Internals

```java
// Default capacity = 10
// Growth factor = 1.5x (newCapacity = oldCapacity + oldCapacity >> 1)

ArrayList<String> list = new ArrayList<>();        // capacity = 10
ArrayList<String> list = new ArrayList<>(50);      // pre-sized — avoids resizing ✅

// When size exceeds capacity:
// 1. New array created with size = old * 1.5
// 2. All elements copied (System.arraycopy)
// 3. Old array becomes GC eligible
// Time cost of resize: O(n) but amortized O(1) per add
```

### ArrayList vs LinkedList — Decision Table

| Operation | ArrayList | LinkedList | Winner |
|---|---|---|---|
| `get(index)` | O(1) — direct index | O(n) — traverse | ArrayList ✅ |
| `add(end)` | O(1) amortized | O(1) | Tie |
| `add(middle)` | O(n) — shift right | O(n) — traverse to pos | Tie |
| `add(front)` | O(n) — shift all | O(1) | LinkedList ✅ |
| `remove(index)` | O(n) — shift left | O(n) — traverse | Tie |
| `remove(front)` | O(n) — shift all | O(1) | LinkedList ✅ |
| Memory | Less (array) | More (node overhead) | ArrayList ✅ |
| Cache friendly | ✅ contiguous memory | ❌ scattered nodes | ArrayList ✅ |

> 💡 **Rule for interviews:** Use `ArrayList` by default. Use `LinkedList` only when you frequently add/remove from **both ends** as a Deque. For stack/queue use `ArrayDeque` instead.

### Common ArrayList Methods

```java
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));

list.get(2);                          // "c"
list.set(2, "X");                     // replace at index 2
list.add("e");                        // add at end
list.add(1, "Z");                     // insert at index 1 (shifts right)
list.remove(0);                       // remove by index
list.remove("b");                     // remove by object (first occurrence)
list.indexOf("c");                    // first index of "c"
list.lastIndexOf("c");               // last index
list.contains("a");                  // true/false
list.subList(1, 3);                  // view [index 1 to 2] — backed by original!
list.size();                         // element count
list.isEmpty();                      // true if size == 0
Collections.sort(list);              // sort in-place
Collections.reverse(list);           // reverse in-place
list.sort(Comparator.reverseOrder()); // Java 8 sort
```

### Fail-Fast Iterator

```java
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));

// ❌ WRONG — ConcurrentModificationException!
for (String s : list) {
    if (s.equals("b")) list.remove(s);
}

// ✅ CORRECT — use Iterator.remove()
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("b")) it.remove();   // safe removal
}

// ✅ ALSO CORRECT — Java 8 removeIf
list.removeIf(s -> s.equals("b"));
```

---

## 3. Set — HashSet, LinkedHashSet, TreeSet

### HashSet Internals

```java
// HashSet is backed by a HashMap internally!
// Values you add become KEYS in the HashMap
// The value stored is a dummy constant: private static final Object PRESENT = new Object()

// Source (simplified):
public class HashSet<E> {
    private HashMap<E, Object> map = new HashMap<>();
    private static final Object PRESENT = new Object();

    public boolean add(E e) {
        return map.put(e, PRESENT) == null;   // returns false if key already existed
    }

    public boolean contains(Object o) {
        return map.containsKey(o);
    }
}
```

### HashSet vs LinkedHashSet vs TreeSet

| Feature | HashSet | LinkedHashSet | TreeSet |
|---|---|---|---|
| Order | No order | Insertion order | Sorted (natural/Comparator) |
| Null | 1 null allowed | 1 null allowed | ❌ No null (NPE) |
| Performance | O(1) add/remove/contains | O(1) + linked list overhead | O(log n) all ops |
| Backed by | HashMap | LinkedHashMap | TreeMap (Red-Black Tree) |
| Use when | Fast lookup, no order needed | Predictable iteration order | Sorted unique elements |

### TreeSet — NavigableSet Methods

```java
TreeSet<Integer> set = new TreeSet<>(Arrays.asList(10, 20, 30, 40, 50));

set.first();          // 10  — smallest
set.last();           // 50  — largest
set.floor(25);        // 20  — largest element ≤ 25
set.ceiling(25);      // 30  — smallest element ≥ 25
set.lower(20);        // 10  — largest element strictly < 20
set.higher(20);       // 30  — smallest element strictly > 20
set.headSet(30);      // [10, 20]     — elements < 30
set.tailSet(30);      // [30, 40, 50] — elements ≥ 30
set.subSet(20, 40);   // [20, 30]     — elements ≥ 20 and < 40
set.descendingSet();  // [50, 40, 30, 20, 10] — reverse order view
set.pollFirst();      // 10  — retrieves and removes smallest
set.pollLast();       // 50  — retrieves and removes largest
```

### Custom Object in TreeSet

```java
// TreeSet uses compareTo() for ordering AND for duplicate detection
// If compareTo() returns 0 → treated as duplicate, NOT added

class Employee implements Comparable<Employee> {
    int id;
    String name;

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.id, other.id);  // sort by id
    }
}

TreeSet<Employee> empSet = new TreeSet<>();
empSet.add(new Employee(3, "Raj"));
empSet.add(new Employee(1, "Kriti"));
empSet.add(new Employee(2, "Amit"));
// Iteration order: id=1, id=2, id=3 (sorted)

// With Comparator (without modifying class):
TreeSet<Employee> byName = new TreeSet<>(Comparator.comparing(e -> e.name));
```

---

## 4. Map — HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap

### HashMap Internals — Complete Picture

```
Initial state:
  capacity = 16 (default)
  loadFactor = 0.75 (default)
  threshold = 16 * 0.75 = 12  ← resize when 12 entries added

put("key", value):
  1. hash = hash(key.hashCode())     ← spreads bits to reduce collisions
  2. index = hash & (capacity - 1)   ← fast modulo using bitwise AND
  3. bucket[index]:
     - empty    → create Node(hash, key, value, null)
     - occupied → check each node: same hash AND equals?
                  yes → update value
                  no  → chain as linked list (or tree if size > 8)

Resize (rehashing):
  When size > threshold (12 for initial capacity 16):
  1. New array of double capacity (32)
  2. All existing entries re-hashed into new positions
  3. Expensive: O(n) — but amortized cost is small
```

### HashMap — Key Methods

```java
Map<String, Integer> map = new HashMap<>();

map.put("a", 1);
map.get("a");                         // 1
map.getOrDefault("z", 0);            // 0 (no NPE if key missing)
map.putIfAbsent("a", 99);            // ignored — "a" already exists
map.containsKey("a");                // true
map.containsValue(1);                // true (O(n) scan!)
map.remove("a");                     // removes entry, returns old value
map.replace("b", 10);               // replaces only if key exists

// Java 8 — compute methods
map.compute("a", (k, v) -> v == null ? 1 : v + 1);   // create or update
map.computeIfAbsent("x", k -> k.length());            // only if absent
map.computeIfPresent("a", (k, v) -> v * 2);           // only if present
map.merge("a", 1, Integer::sum);                      // merge with BiFunction

// Iteration
map.forEach((k, v) -> System.out.println(k + "=" + v));
map.entrySet().forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));

// Views (backed by map — changes reflect both ways)
Set<String> keys = map.keySet();
Collection<Integer> values = map.values();
Set<Map.Entry<String,Integer>> entries = map.entrySet();
```

### LinkedHashMap — LRU Cache Implementation

```java
// LinkedHashMap with accessOrder=true → moves accessed entry to end
// Override removeEldestEntry to auto-evict oldest

class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true);   // accessOrder = true
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;   // evict oldest when over capacity
    }
}

LRUCache<Integer, String> cache = new LRUCache<>(3);
cache.put(1, "one");
cache.put(2, "two");
cache.put(3, "three");
cache.get(1);          // access 1 → moves to end: [2, 3, 1]
cache.put(4, "four");  // evicts eldest (2): [3, 1, 4]
```

### TreeMap — NavigableMap Methods

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(10, "ten"); map.put(20, "twenty"); map.put(30, "thirty");

map.firstKey();           // 10
map.lastKey();            // 30
map.floorKey(25);         // 20 — largest key ≤ 25
map.ceilingKey(25);       // 30 — smallest key ≥ 25
map.lowerKey(20);         // 10 — largest key strictly < 20
map.higherKey(20);        // 30 — smallest key strictly > 20
map.headMap(25);          // {10=ten, 20=twenty} — keys < 25
map.tailMap(20);          // {20=twenty, 30=thirty} — keys ≥ 20
map.subMap(10, 30);       // {10=ten, 20=twenty} — keys ≥ 10 and < 30
map.descendingMap();      // reverse order view
map.pollFirstEntry();     // removes and returns {10=ten}
```

### ConcurrentHashMap — Thread Safety Without Full Lock

```java
// Java 7: Segment locking (16 segments by default)
// Java 8: CAS (Compare-And-Swap) + synchronized on individual bin heads
//         → much finer granularity than Hashtable (which locks entire map)

ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Thread-safe atomic operations:
map.putIfAbsent("key", 1);
map.replace("key", 1, 2);                   // conditional replace
map.computeIfAbsent("key", k -> 0);
map.merge("counter", 1, Integer::sum);      // atomic increment

// Java 8 bulk operations (parallelism threshold):
map.forEach(1, (k, v) -> System.out.println(k + "=" + v));
map.reduce(1, (k, v) -> v, Integer::sum);   // sum all values in parallel

// ❌ NOT allowed in ConcurrentHashMap:
map.put(null, 1);    // NullPointerException
map.put("k", null);  // NullPointerException
// Why? In concurrent context, null is ambiguous: does get() returning null
// mean "key not present" or "value was null"?
```

---

## 5. Queue & Deque — PriorityQueue, ArrayDeque

### Queue Interface — Two Sets of Methods

| Operation | Throws Exception | Returns null/false |
|---|---|---|
| Insert | `add(e)` | `offer(e)` |
| Remove head | `remove()` | `poll()` |
| Examine head | `element()` | `peek()` |

```java
// Always prefer offer/poll/peek — they don't throw on empty queue
Queue<String> q = new LinkedList<>();
q.offer("a");   q.offer("b");   q.offer("c");
q.peek();       // "a" — head, not removed
q.poll();       // "a" — head, removed
q.size();       // 2
```

### PriorityQueue — Min-Heap by Default

```java
// Natural ordering = min-heap (smallest element at head)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5); minHeap.offer(1); minHeap.offer(3);
minHeap.poll();   // 1 (smallest)
minHeap.poll();   // 3
minHeap.poll();   // 5

// Max-heap using Comparator
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
maxHeap.offer(5); maxHeap.offer(1); maxHeap.offer(3);
maxHeap.poll();   // 5 (largest)

// Custom object ordering
PriorityQueue<Employee> byPriority = new PriorityQueue<>(
    Comparator.comparingInt(e -> e.priority)
);

// PriorityQueue is NOT sorted — only the HEAD (peek/poll) is smallest
// Iterating a PriorityQueue does NOT give sorted order!
// To drain in sorted order: while(!pq.isEmpty()) System.out.println(pq.poll());
```

### ArrayDeque — The Swiss Army Knife

```java
// ArrayDeque = resizable array implementing Deque
// Faster than Stack for LIFO, faster than LinkedList for FIFO
// No null elements allowed

Deque<String> deque = new ArrayDeque<>();

// Stack operations (LIFO):
deque.push("a");      // addFirst
deque.push("b");
deque.peek();         // "b" — top of stack
deque.pop();          // "b" — removes top

// Queue operations (FIFO):
deque.offer("x");     // addLast
deque.offer("y");
deque.poll();         // "x" — removes head (FIFO)

// Deque-specific:
deque.offerFirst("first");
deque.offerLast("last");
deque.peekFirst();
deque.peekLast();
deque.pollFirst();
deque.pollLast();

// Use ArrayDeque instead of Stack class:
Deque<Integer> stack = new ArrayDeque<>();   // ✅ modern way
Stack<Integer> old = new Stack<>();          // ❌ avoid (extends Vector, synchronized)
```

---

## 6. Concurrent Collections

### Overview — When to Use Which

| Class | Based On | Thread Safe? | Null Key/Value? | Use When |
|---|---|---|---|---|
| `HashMap` | Array+LinkedList | ❌ No | Key: 1 null, Value: yes | Single thread |
| `Hashtable` | Array+LinkedList | ✅ Full lock | ❌ Neither | Legacy — avoid |
| `ConcurrentHashMap` | Segment/CAS | ✅ Fine-grained | ❌ Neither | Multi-thread map |
| `Collections.synchronizedMap()` | Wraps any Map | ✅ Full lock | Depends | Legacy — avoid |
| `CopyOnWriteArrayList` | Array copy on write | ✅ Write = new copy | ✅ Yes | Read-heavy, rare writes |
| `CopyOnWriteArraySet` | COW ArrayList | ✅ Write = new copy | ✅ Yes | Read-heavy unique set |
| `LinkedBlockingQueue` | Linked nodes | ✅ Two locks | ❌ No | Producer-consumer |
| `ArrayBlockingQueue` | Bounded array | ✅ One lock | ❌ No | Bounded producer-consumer |
| `ConcurrentLinkedQueue` | Lock-free CAS | ✅ Lock-free | ❌ No | High-concurrency queue |

### CopyOnWriteArrayList — Fail-Safe Iterator

```java
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a","b","c"));

// Iteration SAFE during modification:
for (String s : list) {
    list.add("d");   // ✅ no ConcurrentModificationException!
    // Iterator works on a snapshot taken at start of loop
    // Modification creates a new copy of the backing array
}

// ❌ Not good for write-heavy workloads:
// Every write (add/remove/set) creates a full copy of the array → O(n) per write
// Best for: event listeners, observer lists, config read many times, rarely updated
```

---

## 7. Tricky Interview Questions — With Deep Explanations

---

### 🔥 TRICK Q1. What is the output?

```java
List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
list.remove(2);
System.out.println(list);
```

**Most people answer:** `[1, 2, 4, 5]` — thinking it removes the value `2`.

**Actual output:** `[1, 2, 4, 5]`... wait — but for the wrong reason!

Actually this removes **index 2** (value `3`), not value `2`.

```java
list.remove(2);         // int 2 → remove by INDEX → removes element at index 2 (value=3)
list.remove(Integer.valueOf(2));  // Integer → remove by VALUE → removes element 2
```

```
Input:  [1, 2, 3, 4, 5]

list.remove(2)              → removes index 2 (value=3) → [1, 2, 4, 5]
list.remove(Integer.valueOf(2)) → removes value 2       → [1, 3, 4, 5]
```

> 💡 **Rule:** `remove(int)` = remove by index. `remove(Object)` = remove by value.
> Always use `Integer.valueOf(n)` when you want to remove by value from a `List<Integer>`.

---

### 🔥 TRICK Q2. What happens here?

```java
List<String> list = Arrays.asList("a", "b", "c");
list.add("d");
```

**Answer:** `UnsupportedOperationException` at runtime!

`Arrays.asList()` returns a **fixed-size** list backed by the original array.
- `set()` is allowed (modifies value in place)
- `add()` and `remove()` are NOT allowed (can't change size)

```java
// ❌ Fixed-size — structural modification forbidden:
List<String> fixed = Arrays.asList("a", "b", "c");
fixed.add("d");      // UnsupportedOperationException
fixed.remove("a");   // UnsupportedOperationException
fixed.set(0, "X");   // ✅ OK — replaces value, size unchanged

// ✅ Truly immutable (Java 9+):
List<String> immutable = List.of("a", "b", "c");
immutable.set(0, "X");   // UnsupportedOperationException (even set!)

// ✅ Mutable copy from Arrays.asList:
List<String> mutable = new ArrayList<>(Arrays.asList("a", "b", "c"));
mutable.add("d");        // OK ✅
```

---

### 🔥 TRICK Q3. Why does this print `true`?

```java
Set<String> set = new HashSet<>(Arrays.asList("a", "b", "c"));
System.out.println(set.add("a"));  // true or false?
```

**Answer:** `false` — `HashSet.add()` returns `false` if the element already exists.

This comes straight from `HashMap.put()` — returns the old value (not null = already existed).

```java
// HashSet.add() internally:
public boolean add(E e) {
    return map.put(e, PRESENT) == null;
    // put returns null if key was NEW → add returns true
    // put returns PRESENT if key EXISTED → add returns false
}
```

> 💡 **Interview use:** You can detect duplicates elegantly:
> ```java
> if (!set.add(element)) {
>     // element is a duplicate
> }
> ```

---

### 🔥 TRICK Q4. What is the output?

```java
Map<String, Integer> map = new HashMap<>();
map.put("one", 1);
map.put("two", 2);
map.put("one", 3);

System.out.println(map.size());
System.out.println(map.get("one"));
```

**Answer:**
```
2
3
```

`put()` with an existing key **replaces** the value and returns the **old value** (not throws).
Map size stays 2 because "one" is not a new key.

```java
Integer old = map.put("one", 3);
System.out.println(old);    // 1 — the old value that was replaced
```

---

### 🔥 TRICK Q5. HashMap with null key — what prints?

```java
HashMap<String, Integer> map = new HashMap<>();
map.put(null, 1);
map.put(null, 2);

System.out.println(map.size());
System.out.println(map.get(null));
```

**Answer:**
```
1
2
```

- `HashMap` allows exactly **one** null key
- Second `put(null, 2)` **replaces** the first value
- Null key is always stored at **bucket 0** (hash of null = 0)

---

### 🔥 TRICK Q6. What is the output of iterating a HashMap?

```java
Map<String, Integer> map = new HashMap<>();
map.put("banana", 2);
map.put("apple", 1);
map.put("cherry", 3);

for (String key : map.keySet()) {
    System.out.println(key);
}
```

**Answer:** Order is **not guaranteed** — could print in any order.

```
// Possible output (depends on hashCode and bucket placement):
apple
banana
cherry
// OR
banana
cherry
apple
// OR any other order!
```

- Use `LinkedHashMap` to preserve **insertion order**
- Use `TreeMap` to iterate in **sorted key order**

---

### 🔥 TRICK Q7. PriorityQueue — does poll() give sorted output?

```java
PriorityQueue<Integer> pq = new PriorityQueue<>(Arrays.asList(5, 2, 8, 1, 9));

System.out.println(pq);         // print queue
while (!pq.isEmpty()) {
    System.out.print(pq.poll() + " ");
}
```

**Answer:**
```
[1, 2, 8, 5, 9]      ← internal heap array (NOT sorted visually)
1 2 5 8 9            ← poll() always gives SMALLEST — sorted output!
```

- **Printing a PriorityQueue does NOT show sorted order** — it shows the internal heap array
- **Polling one-by-one DOES give sorted order** — each poll() extracts the minimum

---

### 🔥 TRICK Q8. Modifying a Map while iterating

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1); map.put("b", 2); map.put("c", 3);

for (Map.Entry<String, Integer> entry : map.entrySet()) {
    if (entry.getValue() == 2) {
        map.remove(entry.getKey());   // ❌ what happens?
    }
}
```

**Answer:** `ConcurrentModificationException` at runtime.

```java
// ✅ Fix 1: Use Iterator
Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
while (it.hasNext()) {
    Map.Entry<String, Integer> entry = it.next();
    if (entry.getValue() == 2) it.remove();   // safe
}

// ✅ Fix 2: Java 8 removeIf equivalent for Map
map.entrySet().removeIf(e -> e.getValue() == 2);

// ✅ Fix 3: Collect keys to remove, then remove separately
List<String> toRemove = map.entrySet().stream()
    .filter(e -> e.getValue() == 2)
    .map(Map.Entry::getKey)
    .collect(Collectors.toList());
toRemove.forEach(map::remove);
```

---

### 🔥 TRICK Q9. What is the output? (TreeSet with Comparator)

```java
TreeSet<String> set = new TreeSet<>(Comparator.comparingInt(String::length));
set.add("banana");
set.add("apple");
set.add("fig");
set.add("kiwi");     // same length as "fig"? No — kiwi=4, fig=3
set.add("pear");     // length 4 — same as kiwi!

System.out.println(set);
System.out.println(set.size());
```

**Answer:**
```
[fig, pear, apple, banana]
4
```

Wait — where is `kiwi`?

`TreeSet` uses the `Comparator` for **both ordering AND duplicate detection**.
`"kiwi".length() == "pear".length() == 4` → `compare("pear", "kiwi") == 0` → treated as **duplicate** → NOT added!

> 💡 **Key rule:** In TreeSet, if `compareTo()` or `compare()` returns 0, elements are considered duplicates — even if `equals()` would return `false`!

---

### 🔥 TRICK Q10. What happens? (HashMap capacity and resize)

```java
HashMap<Integer, String> map = new HashMap<>(4);
// loadFactor default = 0.75
// threshold = 4 * 0.75 = 3

map.put(1, "a");   // size=1, no resize
map.put(2, "b");   // size=2, no resize
map.put(3, "c");   // size=3 = threshold → RESIZE!
```

**Answer:** When the 3rd entry is added, `size (3) > threshold (3)` triggers resize:
- New capacity = 8
- New threshold = 8 * 0.75 = 6
- All 3 entries rehashed into new positions
- This is why small initial capacity with many entries = poor performance

> 💡 **Best practice for interviews:** If you know approximate size in advance:
> ```java
> // Formula: initialCapacity = (expectedSize / loadFactor) + 1
> // For 100 entries: (100 / 0.75) + 1 ≈ 135
> Map<K, V> map = new HashMap<>(135);   // avoids any resize
> // Or use Guava: Maps.newHashMapWithExpectedSize(100)
> ```

---

### 🔥 TRICK Q11. Sublist trap

```java
List<Integer> original = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
List<Integer> sub = original.subList(1, 4);   // [2, 3, 4]

sub.set(0, 99);
System.out.println(original);   // ?

sub.add(100);
System.out.println(original);   // ?

original.add(6);
System.out.println(sub.size()); // ?
```

**Answer:**
```
[1, 99, 3, 4, 5]         // sub.set() modifies original — it's a VIEW!
[1, 99, 3, 4, 100, 5]    // sub.add() also modifies original
ConcurrentModificationException  // original structurally modified → sub is invalid
```

`subList()` returns a **view** backed by the original list.
Any structural change to `original` (add/remove) while using `sub` throws `ConcurrentModificationException`.

```java
// ✅ To get an independent copy:
List<Integer> copy = new ArrayList<>(original.subList(1, 4));
```

---

### 🔥 TRICK Q12. Why is `Vector` considered legacy?

```java
// Vector: every method is synchronized at the method level
public synchronized boolean add(E e) { ... }
public synchronized E get(int index) { ... }
public synchronized int size() { ... }

// Problem: even read-only get() is locked!
// Thread 1 calls get() → acquires lock
// Thread 2 calls get() → BLOCKED (even though reads are safe to parallelize)

// CopyOnWriteArrayList: reads never blocked, writes create a new copy
// ConcurrentHashMap: reads never blocked, writes use fine-grained locking

// Even Vector is NOT truly thread-safe for compound operations:
if (!vector.isEmpty()) {         // lock acquired and released
    vector.get(0);               // separate lock — another thread could clear between!
}
// → Must still manually synchronize compound operations
```

---

## 8. Collections Utility Class — FAQ

```java
List<Integer> list = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));

// Sort
Collections.sort(list);                              // natural order: [1,1,2,3,4,5,6,9]
Collections.sort(list, Comparator.reverseOrder());   // reverse: [9,6,5,4,3,2,1,1]

// Search (list must be sorted first)
Collections.binarySearch(list, 4);     // returns index (or negative insertion point)

// Min / Max
Collections.min(list);                 // 1
Collections.max(list);                 // 9

// Shuffle / Reverse / Fill
Collections.shuffle(list);            // random order
Collections.reverse(list);            // reverses in-place
Collections.fill(list, 0);           // fills all elements with 0

// Frequency / Disjoint
Collections.frequency(list, 1);       // 2 (count of value 1)
Collections.disjoint(list1, list2);   // true if no common elements

// Unmodifiable wrappers (throws on mutation)
List<String>  ul  = Collections.unmodifiableList(list);
Set<String>   us  = Collections.unmodifiableSet(set);
Map<K,V>      um  = Collections.unmodifiableMap(map);

// Synchronized wrappers (avoid — prefer ConcurrentHashMap instead)
List<String>  sl  = Collections.synchronizedList(list);
Map<K,V>      sm  = Collections.synchronizedMap(map);

// Singleton (1-element immutable collection)
List<String> one = Collections.singletonList("only");
Set<String>  oneSet = Collections.singleton("only");
Map<K,V> oneMap = Collections.singletonMap("k", "v");

// Empty (immutable empty collection — shared instance, no allocation)
List<String>  empty = Collections.emptyList();
Set<Integer>  emptySet = Collections.emptySet();
Map<K,V>      emptyMap = Collections.emptyMap();

// nCopies
List<String> copies = Collections.nCopies(5, "hello");   // ["hello","hello","hello","hello","hello"]

// Swap
Collections.swap(list, 0, list.size()-1);   // swap first and last

// Rotate
Collections.rotate(list, 2);   // rotate right by 2 positions
```

---

## 9. Java 8+ Streams with Collections

### Most Asked Stream + Collection Combinations

```java
List<Employee> employees = Arrays.asList(
    new Employee(1, "Kriti",  "IT",  85000),
    new Employee(2, "Raj",    "HR",  60000),
    new Employee(3, "Amit",   "IT",  90000),
    new Employee(4, "Priya",  "HR",  70000),
    new Employee(5, "Neha",   "IT",  75000)
);

// Group by department
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

// Count per department
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

// Average salary per department
Map<String, Double> avgSalary = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment,
             Collectors.averagingDouble(Employee::getSalary)));

// Max salary employee per department
Map<String, Optional<Employee>> maxByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment,
             Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))));

// Filter + collect to Map
Map<Integer, String> idToName = employees.stream()
    .filter(e -> e.getSalary() > 70000)
    .collect(Collectors.toMap(Employee::getId, Employee::getName));

// Sort by salary descending, collect names
List<String> sorted = employees.stream()
    .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
    .map(Employee::getName)
    .collect(Collectors.toList());

// Partition by condition (true/false groups)
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getSalary() > 75000));
// {true=[Kriti, Amit], false=[Raj, Priya, Neha]}

// Joining names
String names = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", ", "[", "]"));
// [Kriti, Raj, Amit, Priya, Neha]

// Flat map — all department names from multiple teams
List<String> allDepts = teams.stream()
    .flatMap(team -> team.getDepartments().stream())
    .distinct()
    .collect(Collectors.toList());

// toUnmodifiableList (Java 10+)
List<String> immutable = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.toUnmodifiableList());

// Collectors.toMap with merge function (handles duplicate keys)
Map<String, Integer> wordCount = words.stream()
    .collect(Collectors.toMap(
        w -> w,
        w -> 1,
        Integer::sum   // merge function: sum values for duplicate keys
    ));
```

---

## 10. Interview Q&A — All FAQs with Answers

### ❓ Q1. What is the difference between Collection and Collections?

> "`Collection` (singular) is a **Java interface** — the root of the collection hierarchy. It declares methods like `add()`, `remove()`, `size()`, `iterator()`. `Collections` (plural) is a **utility class** with only `static` methods — like `Collections.sort()`, `Collections.shuffle()`, `Collections.unmodifiableList()`. Same relationship as `Array` vs `Arrays`."

---

### ❓ Q2. Why Map doesn't extend Collection?

> "Map is a fundamentally different data structure — it stores **key-value pairs**, while `Collection` deals with **individual elements**. A `Collection<E>` has `add(E e)` but a Map would need `put(K key, V value)` — incompatible interface. Also, a `Map` has two types of elements (keys and values) with different semantics, so it has its own hierarchy. The JDK designers deliberately kept them separate."

---

### ❓ Q3. Difference between Iterator and ListIterator?

| Feature | Iterator | ListIterator |
|---|---|---|
| Direction | Forward only | Forward AND backward |
| Available for | All Collections | List only |
| Add element | ❌ No | ✅ `add()` |
| Replace element | ❌ No | ✅ `set()` |
| Get index | ❌ No | ✅ `nextIndex()`, `previousIndex()` |

```java
ListIterator<String> it = list.listIterator();
while (it.hasNext()) {
    String s = it.next();
    it.set(s.toUpperCase());   // replace current element
}
// Iterate backwards:
while (it.hasPrevious()) {
    System.out.println(it.previous());
}
```

---

### ❓ Q4. What is the difference between poll() and remove() in Queue?

> "Both remove the head element. `remove()` throws `NoSuchElementException` if the queue is empty. `poll()` returns `null` if empty — no exception. In production code, always prefer `poll()` because it's safer and lets you check for null. Same pattern: `peek()` vs `element()` for examining head without removal."

---

### ❓ Q5. How does EnumSet work? Why is it faster?

```java
enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

// EnumSet uses a single long as a bit vector internally
// Each enum constant maps to a bit position
// Operations are pure bitwise AND/OR — O(1) and extremely fast

EnumSet<Day> weekdays = EnumSet.of(Day.MON, Day.TUE, Day.WED, Day.THU, Day.FRI);
EnumSet<Day> weekend  = EnumSet.complementOf(weekdays);
EnumSet<Day> all      = EnumSet.allOf(Day.class);
EnumSet<Day> none     = EnumSet.noneOf(Day.class);
EnumSet<Day> range    = EnumSet.range(Day.MON, Day.FRI);

// Fastest possible Set for enums — use whenever keys are enum values
```

---

### ❓ Q6. What is WeakHashMap? When to use it?

> "`WeakHashMap` uses **weak references** for its keys. When a key has no other strong references elsewhere in the program, the GC can collect it — and the corresponding map entry is automatically removed. Use it for **caches where you don't want the map to prevent garbage collection** of the keys. Example: caching metadata for objects you don't own (like parsed class info). If only the map holds the key, the GC is free to collect it."

```java
WeakHashMap<Object, String> cache = new WeakHashMap<>();
Object key = new Object();
cache.put(key, "metadata");

System.out.println(cache.size());  // 1
key = null;                         // remove strong reference
System.gc();                        // suggest GC
System.out.println(cache.size());  // 0 — entry removed automatically!
```

---

### ❓ Q7. How do you make a Collection thread-safe? What are the options?

```java
// Option 1: Collections.synchronizedXxx() — full lock, legacy, avoid
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
// Must manually synchronize when iterating:
synchronized(syncList) {
    for (String s : syncList) { ... }
}

// Option 2: CopyOnWriteArrayList — best for read-heavy, rare writes
List<String> cowList = new CopyOnWriteArrayList<>();

// Option 3: ConcurrentHashMap — best for concurrent maps
Map<K,V> concMap = new ConcurrentHashMap<>();

// Option 4: BlockingQueue — for producer-consumer patterns
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(100);
queue.put(task);    // blocks if full
queue.take();       // blocks if empty
```

---

### ❓ Q8. What is the difference between Comparable and Comparator?

```java
// Comparable — natural ordering, defined IN the class itself
class Employee implements Comparable<Employee> {
    int salary;

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.salary, other.salary);
    }
}
Collections.sort(employees);   // uses natural order

// Comparator — external ordering, defined OUTSIDE the class
// Multiple orderings possible without changing the class
Comparator<Employee> byName   = Comparator.comparing(Employee::getName);
Comparator<Employee> bySalary = Comparator.comparingInt(Employee::getSalary);
Comparator<Employee> multi    = byName.thenComparingInt(Employee::getSalary);

employees.sort(byName);
employees.sort(bySalary.reversed());
employees.sort(Comparator.comparing(Employee::getDept)
                          .thenComparing(Employee::getName));
```

| | Comparable | Comparator |
|---|---|---|
| Package | `java.lang` | `java.util` |
| Method | `compareTo(T o)` | `compare(T o1, T o2)` |
| Location | Inside the class | External class / lambda |
| Single/Multiple | One natural ordering | Unlimited orderings |
| Modifies class | Yes | No |

---

### ❓ Q9. What is fail-fast vs fail-safe? Name examples of each.

| | Fail-Fast | Fail-Safe |
|---|---|---|
| Behaviour | Throws `ConcurrentModificationException` | Never throws — works on copy or snapshot |
| Detection | Uses `modCount` counter | No structural check |
| Examples | `ArrayList`, `HashMap`, `HashSet`, `TreeMap` | `CopyOnWriteArrayList`, `ConcurrentHashMap` |
| Consistency | Always sees latest data | May see stale snapshot |
| Performance | Faster (no copy overhead) | Slower writes (copy or concurrent data structure) |

---

### ❓ Q10. How to sort a Map by value?

```java
Map<String, Integer> scores = Map.of("Kriti",90, "Raj",70, "Amit",85);

// Sort by value ascending
Map<String, Integer> sortedAsc = scores.entrySet().stream()
    .sorted(Map.Entry.comparingByValue())
    .collect(Collectors.toLinkedHashMap(
        Map.Entry::getKey, Map.Entry::getValue,
        (e1, e2) -> e1, LinkedHashMap::new));

// Simpler Java 8:
scores.entrySet().stream()
    .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
    .forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));
```

---

## 11. Quick Reference Cheat Sheet

### Collection Interface Method Summary

```
add(E e)              → returns true if collection changed
addAll(Collection c)  → adds all elements
remove(Object o)      → removes first occurrence, returns true if found
removeAll(Collection) → removes all matching elements
retainAll(Collection) → keeps only matching elements (intersection)
contains(Object o)    → true if element present
containsAll(c)        → true if all elements of c present
size()                → count
isEmpty()             → true if size == 0
clear()               → removes all elements
toArray()             → Object[] of all elements
iterator()            → Iterator<E>
stream()              → Stream<E>  (Java 8)
forEach(Consumer)     → Java 8 iteration
```

### Complexity Summary

| Collection | add | remove | get/contains | Notes |
|---|---|---|---|---|
| `ArrayList` | O(1)* | O(n) | O(1) | *amortized, resize is O(n) |
| `LinkedList` | O(1) ends | O(1) ends / O(n) middle | O(n) | O(1) only at head/tail |
| `HashSet` | O(1)* | O(1)* | O(1)* | *amortized, assumes good hashCode |
| `TreeSet` | O(log n) | O(log n) | O(log n) | Red-Black Tree |
| `HashMap` | O(1)* | O(1)* | O(1)* | *amortized |
| `TreeMap` | O(log n) | O(log n) | O(log n) | Red-Black Tree |
| `PriorityQueue` | O(log n) | O(log n) | O(n) peek=O(1) | Min-heap |
| `ArrayDeque` | O(1)* | O(1)* | O(n) | No index access |

### When to Use Which?

```
Need fast lookup by key?                  → HashMap
Need sorted keys?                         → TreeMap
Need insertion-order iteration?           → LinkedHashMap
Need LRU Cache?                           → LinkedHashMap (accessOrder=true)
Need fast unique elements?                → HashSet
Need sorted unique elements?              → TreeSet
Need insertion-order unique elements?     → LinkedHashSet
Need FIFO queue?                          → ArrayDeque (or LinkedList)
Need priority ordering?                   → PriorityQueue
Need stack (LIFO)?                        → ArrayDeque (NOT Stack class)
Need thread-safe list, mostly reads?      → CopyOnWriteArrayList
Need thread-safe map?                     → ConcurrentHashMap
Need bounded blocking queue?              → ArrayBlockingQueue
Need enum keys?                           → EnumMap
Need enum unique values?                  → EnumSet
Need cache, keys GC-able?                → WeakHashMap
```

### One-Liners

```
ArrayList    = dynamic array, index fast, insert slow at middle
LinkedList   = doubly linked, fast add/remove at ends, slow random access
Vector       = synchronized ArrayList (legacy, avoid — use CopyOnWriteArrayList)
Stack        = LIFO on top of Vector (legacy, avoid — use ArrayDeque)
HashSet      = backed by HashMap, no order
LinkedHashSet= HashSet + doubly linked list for order
TreeSet      = sorted HashSet using Red-Black tree
HashMap      = hash table, O(1) ops, one null key
LinkedHashMap= HashMap + linked list for insertion/access order
TreeMap      = sorted HashMap using Red-Black tree
Hashtable    = synchronized HashMap (legacy, avoid — use ConcurrentHashMap)
ConcurrentHashMap = thread-safe HashMap with fine-grained locking (CAS in Java 8)
PriorityQueue= min-heap by default, poll() always gives smallest
ArrayDeque   = resizable array deque, better than Stack AND LinkedList for most uses
WeakHashMap  = keys GC-eligible when no strong refs elsewhere
EnumMap/Set  = ultra-fast bit-vector implementation for enum types
```

---

*Prepared from Claude AI session | Kriti Singh | 8.5 YOE Java Developer*
*Topics: Collection Hierarchy · List · Set · Map · Queue · Concurrent · Tricky Questions · FAQ · Streams*
