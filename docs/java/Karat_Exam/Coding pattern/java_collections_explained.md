# Java Collections – ArrayList, HashMap, LinkedHashMap, HashSet, PriorityQueue, and Deque

These are some of the most commonly used Java Collections. For interviews, understand **what each collection stores, how it works, its complexity, and when to use it**.

---

## 1. ArrayList — Ordered + Indexed Access

```java
List<String> list = new ArrayList<>();

list.add("a");
list.add("b");
list.add("c");

System.out.println(list.get(0));  // a

list.remove(0);

System.out.println(list);         // [b, c]
```

### How it works

Think of `ArrayList` like a **resizable array**:

```text
Index:    0      1      2
          ↓      ↓      ↓
        "a"    "b"    "c"
```

You can directly access an element using its index:

```java
list.get(1);
```

Result:

```text
"b"
```

### Important operations

| Operation | Example | Complexity |
|---|---|---|
| Add at end | `list.add("d")` | O(1) amortized |
| Get by index | `list.get(2)` | O(1) |
| Remove by index | `list.remove(1)` | O(n) |
| Search | `list.contains("a")` | O(n) |

### Why is remove O(n)?

Suppose:

```text
[a, b, c, d]
```

You do:

```java
list.remove(1);
```

`b` is removed:

```text
[a, c, d]
```

Elements after `b` have to shift left.

### When to use

Use `ArrayList` when:

- You need ordered elements.
- You frequently access elements by index.
- You mostly add elements at the end.
- You don't frequently insert/remove from the middle.

**Interview answer:**

> I would use ArrayList when I need ordered, indexed access and more reads than middle insertions/removals.

---

# 2. HashMap — Key → Value

`HashMap` stores data as **key-value pairs**.

```java
Map<String, Integer> map = new HashMap<>();

map.put("apple", 10);
map.put("banana", 20);
map.put("orange", 30);
```

Think of it like:

```text
Key       Value

apple  →   10
banana →   20
orange →   30
```

Now:

```java
System.out.println(map.get("apple"));
```

Output:

```text
10
```

---

## `getOrDefault()`

Suppose:

```java
Map<String, Integer> map = new HashMap<>();

map.put("apple", 10);

System.out.println(map.get("apple"));   // 10
System.out.println(map.get("mango"));   // null
```

If you don't know whether `"mango"` exists, you might write:

```java
Integer count = map.get("mango");

if (count == null) {
    count = 0;
}
```

Instead:

```java
int count = map.getOrDefault("mango", 0);
```

Result:

```text
0
```

This is particularly useful for **frequency counting**.

### Example — Count Characters

```java
String str = "banana";

Map<Character, Integer> frequency = new HashMap<>();

for (char ch : str.toCharArray()) {
    frequency.put(ch, frequency.getOrDefault(ch, 0) + 1);
}

System.out.println(frequency);
```

Conceptually:

```text
b → 1
a → 3
n → 2
```

This is a very common interview pattern.

---

## `containsKey()`

```java
if (map.containsKey("apple")) {
    System.out.println("Apple exists");
}
```

Useful when you only care whether the key exists.

### Complexity

Average:

```text
put()         → O(1)
get()         → O(1)
containsKey() → O(1)
remove()      → O(1)
```

Worst case can degrade because of hash collisions, but modern Java's `HashMap` can use tree bins for heavily collided buckets.

### When to use

Use `HashMap` when you need:

> **Fast lookup using a key.**

Examples:

```text
User ID      → User
Product ID   → Product
Character    → Frequency
Employee ID  → Employee
```

---

# 3. LinkedHashMap — HashMap + Order

```java
Map<String, Integer> map = new LinkedHashMap<>();

map.put("A", 1);
map.put("B", 2);
map.put("C", 3);
```

When you iterate:

```java
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey());
}
```

Output:

```text
A
B
C
```

It maintains **insertion order**.

### HashMap

```text
put A
put B
put C

Iteration order → not guaranteed
```

### LinkedHashMap

```text
put A
put B
put C

Iteration order → A B C
```

---

## Why is LinkedHashMap useful for LRU Cache?

LRU = **Least Recently Used**

Suppose cache capacity = 3:

```text
A B C
```

You access:

```text
A
```

Now A becomes recently used.

Conceptually:

```text
B C A
```

If you insert `D`, the least recently used item `B` should be removed:

```text
C A D
```

`LinkedHashMap` can maintain this access order.

```java
LinkedHashMap<Integer, String> cache =
        new LinkedHashMap<>(3, 0.75f, true);
```

The `true` means:

```text
accessOrder = true
```

So the map maintains **access order**, rather than just insertion order.

---

# 4. HashSet — Unique Elements

`Set` does not allow duplicates.

```java
Set<String> set = new HashSet<>();

set.add("apple");
set.add("banana");
set.add("apple");
```

Result:

```text
[banana, apple]
```

There is only **one `"apple"`**.

```java
System.out.println(set.size());
```

Output:

```text
2
```

---

## Why is HashSet useful for duplicate detection?

Suppose:

```java
int[] nums = {1, 2, 3, 2, 4};
```

We want to find whether there is a duplicate.

```java
Set<Integer> set = new HashSet<>();

for (int num : nums) {
    if (set.contains(num)) {
        System.out.println("Duplicate: " + num);
        break;
    }

    set.add(num);
}
```

When we reach the second `2`:

```text
set = [1, 2, 3]

current = 2

set.contains(2) → true
```

So we found a duplicate.

### Complexity

Average:

```text
add()      → O(1)
contains() → O(1)
remove()   → O(1)
```

### Important difference

```text
List → allows duplicates
Set  → does not allow duplicates
```

Example:

```java
List<Integer> list = List.of(1, 2, 2, 3);
// [1, 2, 2, 3]

Set<Integer> set = Set.of(1, 2, 3);
// [1, 2, 3]
```

---

# 5. PriorityQueue — Heap

This is very important for **DSA interviews**.

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();

pq.offer(3);
pq.offer(1);
pq.offer(5);
pq.offer(2);
```

By default, Java's `PriorityQueue` is a **min-heap**.

So:

```java
System.out.println(pq.poll());
```

Output:

```text
1
```

Then:

```java
System.out.println(pq.poll());
```

Output:

```text
2
```

Then the remaining values are:

```text
3
5
```

So elements come out in priority order.

---

## Important: PriorityQueue is NOT fully sorted

After:

```java
pq.offer(3);
pq.offer(1);
pq.offer(5);
pq.offer(2);
```

Don't assume the internal structure is:

```text
1 2 3 5
```

The heap only guarantees that the **highest-priority element is at the head**.

For a min-heap:

```text
peek() → smallest element
poll() → removes smallest element
```

### Example

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();

pq.offer(10);
pq.offer(3);
pq.offer(7);

System.out.println(pq.peek()); // 3
System.out.println(pq.poll()); // 3
System.out.println(pq.peek()); // 7
```

---

## Max-Heap

By default:

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
```

is a min-heap.

For a max-heap:

```java
PriorityQueue<Integer> pq =
        new PriorityQueue<>(Comparator.reverseOrder());
```

Now:

```java
pq.offer(3);
pq.offer(1);
pq.offer(5);

System.out.println(pq.poll());
```

Output:

```text
5
```

Example :

```java
//Min Heap
PriorityQueue p=new PriorityQueue();
        p.offer(2);
        p.offer(5);
        p.offer(3);
        System.out.println(p+" "+p.peek()+" "+p.poll());
        System.out.println(p+" "+p.peek()+" "+p.poll());
        System.out.println(p+" "+p.peek()+" "+p.poll());

//Max Heap
PriorityQueue p1=new PriorityQueue(Comparator.reverseOrder());
        p1.offer(2);
        p1.offer(5);
        p1.offer(3);
        System.out.println(p1+" "+p1.peek()+" "+p1.poll());
        System.out.println(p1+" "+p1.peek()+" "+p1.poll());
        System.out.println(p1+" "+p1.peek()+" "+p1.poll());
```

Output:

```text
[2, 5, 3] 2 2
[3, 5] 3 3
[5] 5 5
[5, 2, 3] 5 5
[3, 2] 3 3
[2] 2 2
```

### Common interview use cases

`PriorityQueue` is useful for:

- Top K elements
- Kth largest/smallest
- Merge K sorted lists
- Dijkstra's algorithm
- Scheduling problems

---

# 6. Deque — Can Work as Stack AND Queue

`Deque` means:

> **Double Ended Queue**

You can insert/remove from **both ends**.

```java
Deque<Integer> dq = new ArrayDeque<>();
```

Imagine:

```text
Front                    Back
 ↓                        ↓
[ 10 ][ 20 ][ 30 ][ 40 ]
```

You can work from either side.

---

## Use it as a Stack

Stack means:

> **LIFO — Last In, First Out**

```java
Deque<Integer> stack = new ArrayDeque<>();

stack.push(10);
stack.push(20);
stack.push(30);
```

Conceptually:

```text
TOP
 ↓
30
20
10
```

Now:

```java
System.out.println(stack.pop());
```

Output:

```text
30
```

Because `30` was added last.

---

## Use it as a Queue

Queue means:

> **FIFO — First In, First Out**

```java
Deque<Integer> queue = new ArrayDeque<>();

queue.offer(10);
queue.offer(20);
queue.offer(30);
```

Conceptually:

```text
Front                 Back
 ↓                      ↓
10 → 20 → 30
```

Now:

```java
System.out.println(queue.poll());
```

Output:

```text
10
```

Because `10` entered first.

---

# Why `ArrayDeque` instead of `Stack`?

Old Java code often uses:

```java
Stack<Integer> stack = new Stack<>();
```

But generally prefer:

```java
Deque<Integer> stack = new ArrayDeque<>();
```

because `Stack` is an older legacy class and extends `Vector`, which has synchronization overhead.

For normal single-threaded stack operations, `ArrayDeque` is usually the better choice.

---

# ⭐ Quick Interview Comparison

| Collection | Stores | Allows duplicates? | Ordered? | Main use |
|---|---|---:|---|---|
| `ArrayList` | Elements | Yes | Insertion order | Indexed access |
| `HashMap` | Key → Value | Keys: No | No guaranteed order | Fast lookup |
| `LinkedHashMap` | Key → Value | Keys: No | Insertion/access order | Ordered map, LRU |
| `HashSet` | Elements | No | No guaranteed order | Unique values |
| `PriorityQueue` | Elements | Yes | Priority order | Min/max/top-K |
| `ArrayDeque` | Elements | Yes | Ends matter | Stack/Queue |

---

# 🧠 Easy Way to Remember

Think about the **problem you're trying to solve**:

```text
Need index?
     ↓
ArrayList

Need Key → Value?
     ↓
HashMap

Need Key → Value + order?
     ↓
LinkedHashMap

Need only unique values?
     ↓
HashSet

Need smallest/largest element repeatedly?
     ↓
PriorityQueue

Need Stack or Queue?
     ↓
ArrayDeque
```

---

# ⭐ Combined Interview Example

Suppose you receive:

```text
"java is good and java is powerful"
```

You could use a `HashMap` for frequency counting:

```java
String[] words = {
    "java", "is", "good", "and",
    "java", "is", "powerful"
};

Map<String, Integer> frequency = new HashMap<>();

for (String word : words) {
    frequency.put(
        word,
        frequency.getOrDefault(word, 0) + 1
    );
}
```

Result conceptually:

```text
java     → 2
is       → 2
good     → 1
and      → 1
powerful → 1
```

If you want **unique words**:

```java
Set<String> uniqueWords =
        new HashSet<>(Arrays.asList(words));
```

If you want the **top 2 most frequent words**, you can combine the `HashMap` with a `PriorityQueue`.

This combination of collections is very common in Java backend and DSA interviews.
