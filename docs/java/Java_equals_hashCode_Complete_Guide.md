# ☕ Java equals() & hashCode() — Complete Interview Guide
> Topic: Contract · HashMap internals · Collision · Integer Cache · Autoboxing · Mutable keys · Best practices
> Level: 8 Years Experience | MNC / Product Company Interviews

---

## 📌 Table of Contents

1. [The Two Rules — The Contract](#1-the-two-rules--the-contract)
2. [Why the Contract Exists — HashMap Internals](#2-why-the-contract-exists--hashmap-internals)
3. [Rule 1 — put(e1) + get(e2) Deep Dive](#3-rule-1--pute1--gete2-deep-dive)
4. [Rule 2 — Hash Collision Explained](#4-rule-2--hash-collision-explained)
5. [All 4 Broken Scenarios — What Happens When You Violate the Contract](#5-all-4-broken-scenarios)
6. [Mutable Key Trap — Senior-Level Danger](#6-mutable-key-trap--senior-level-danger)
7. [Correct Implementation — Best Practices](#7-correct-implementation--best-practices)
8. [equals() Contract — 5 Properties You Must Know](#8-equals-contract--5-properties-you-must-know)
9. [Real-World Use Cases](#9-real-world-use-cases)
10. [Tricky Code — What Does This Print?](#10-tricky-code--what-does-this-print)
    - Question 1: String Pool
    - Question 2: Integer Cache — Autoboxing Deep Dive + JVM Cache + All Wrapper Types
    - Question 3: HashSet without overrides
    - Question 4: Mutable List as key — Step-by-Step Bucket Breakdown
11. [Interview Q&A — All Questions with Answers](#11-interview-qa--all-questions-with-answers)
12. [Quick Reference Cheat Sheet](#12-quick-reference-cheat-sheet)

---

## 1. The Two Rules — The Contract

### Rule 1 — The Mandatory Direction

```
IF:   a.equals(b) == true
THEN: a.hashCode() == b.hashCode()   ← MUST be true
```

**In plain English:**
> If two objects are logically equal, they MUST produce the same hashCode.

### Rule 2 — The Non-Guarantee Direction

```
IF:   a.hashCode() == b.hashCode()
THEN: a.equals(b)  MAY be true OR false   ← NOT guaranteed
```

**In plain English:**
> Two objects having the same hashCode does NOT mean they are equal.
> This is called a **hash collision** and is perfectly legal and expected.

### The One-Way Arrow Mental Model

```
equals() true   ──────────────────►  hashCode() SAME    (guaranteed, Rule 1)

hashCode() SAME ──────────────────►  equals() true      (NOT guaranteed, Rule 2)
                                      ↓
                                     could be a collision
                                     equals() still called to verify
```

---

## 2. Why the Contract Exists — HashMap Internals

HashMap uses a **two-step key lookup**:

```
Step 1: hashCode()  →  "Which bucket to look in?"  (fast, O(1))
Step 2: equals()    →  "Is this the right key?"    (precise verification)
```

```
map.put(key, value):
  1. key.hashCode()  →  compute bucket index
  2. Go to that bucket
  3. Store [key, value] there

map.get(key):
  1. key.hashCode()  →  compute bucket index
  2. Go to that bucket
  3. For each entry in bucket: entry.key.equals(key)?
  4. If true → return value
  5. If false → keep checking (collision case)
  6. If nothing matches → return null
```

**Why does the contract matter?**

- If `equals()` says two objects are the same but `hashCode()` is different → they land in **different buckets** → `equals()` is **never even called** → lookup fails silently
- If `hashCode()` is the same but `equals()` uses `==` → same bucket is found → but `==` fails because different references → lookup still fails

**Both must be overridden together. Always.**

---

## 3. Rule 1 — put(e1) + get(e2) Deep Dive

### The Setup

```java
class Employee {
    int id;

    Employee(int id) { this.id = id; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee e = (Employee) obj;
        return this.id == e.id;   // logical equality: same id = same employee
    }

    @Override
    public int hashCode() {
        return id;   // same id = same hashCode (contract satisfied)
    }
}
```

### Create Two Separate Objects

```java
Employee e1 = new Employee(101);
Employee e2 = new Employee(101);
```

In memory (Heap):
```
e1  ──►  Employee@A1 { id = 101 }    ← object at address A1
e2  ──►  Employee@B2 { id = 101 }    ← object at address B2 (different!)
```

```java
System.out.println(e1 == e2);       // false  — different memory addresses
System.out.println(e1.equals(e2));  // true   — same id = logically equal
System.out.println(e1.hashCode());  // 101
System.out.println(e2.hashCode());  // 101    — same! contract satisfied
```

### Store e1, Retrieve with e2

```java
Map<Employee, String> map = new HashMap<>();
map.put(e1, "John");

String result = map.get(e2);   // "John"  ← works!
```

### Step-by-Step Flow

```
map.put(e1, "John"):
  1. e1.hashCode()  →  101
  2. index = 101 % 16  →  bucket 5
  3. Store:  bucket 5 → [key=e1, value="John"]

map.get(e2):
  1. e2.hashCode()  →  101          ← same as e1 (Rule 1 satisfied)
  2. index = 101 % 16  →  bucket 5  ← same bucket!
  3. Found entry: [key=e1, value="John"]
  4. e1.equals(e2)  →  true         ← ids match
  5. Return "John"  ✅
```

> 💡 **Key insight:** HashMap NEVER asks `e1 == e2`. It asks `e1.equals(e2)`.
> This is why different object instances representing the same business entity work correctly.

---

## 4. Rule 2 — Hash Collision Explained

### What Is a Collision?

A collision occurs when two **different** keys produce the **same hashCode** (same bucket index).

```java
String s1 = "FB";
String s2 = "Ea";

System.out.println(s1.hashCode());  // 2236  — same!
System.out.println(s2.hashCode());  // 2236  — same! (real Java example)
System.out.println(s1.equals(s2));  // false — NOT equal (different strings)
```

This is Rule 2 in action: same hashCode ≠ equal objects.

### How HashMap Handles Collisions

```
Both "FB" and "Ea" go to the same bucket:

bucket[X]:
  ┌────────────────────────┐
  │ [key="FB", value=100]  │  ← first entry
  │          ↓             │
  │ [key="Ea", value=200]  │  ← chained (linked list)
  └────────────────────────┘

On map.get("FB"):
  1. hashCode("FB") = 2236 → bucket X
  2. Check first entry: "FB".equals("FB") → true → return 100 ✅

On map.get("Ea"):
  1. hashCode("Ea") = 2236 → bucket X
  2. Check first entry: "FB".equals("Ea") → false → next
  3. Check second entry: "Ea".equals("Ea") → true → return 200 ✅
```

### Java 8 Treeification

```
When one bucket has more than 8 entries (TREEIFY_THRESHOLD):
  Linked list  →  Red-Black Tree

Lookup in bucket:
  Before: O(n)  — linear scan through list
  After:  O(log n)  — tree traversal

Reverts back to linked list when size drops below 6 (UNTREEIFY_THRESHOLD).
```

---

## 5. All 4 Broken Scenarios

### Scenario A: Neither overridden (default Object behaviour)

```java
class Employee {
    int id;
    Employee(int id) { this.id = id; }
    // NO equals() override → uses Object's == (reference comparison)
    // NO hashCode() override → uses Object's memory-address-based hashCode
}

Employee e1 = new Employee(101);
Employee e2 = new Employee(101);

map.put(e1, "John");
map.get(e2);  // null ✗

// Why: e2.hashCode() ≠ e1.hashCode() (different addresses)
// → different buckets → equals() never even called
```

### Scenario B: Only equals() overridden — hashCode() NOT overridden ❌ MOST DANGEROUS

```java
class Employee {
    int id;
    @Override
    public boolean equals(Object obj) {
        Employee e = (Employee) obj;
        return this.id == e.id;  // correct logic
    }
    // hashCode() NOT overridden → still uses memory address
}

Employee e1 = new Employee(101);  // hashCode = 2018699554 (address-based)
Employee e2 = new Employee(101);  // hashCode = 1311053135 (different address)

map.put(e1, "John");
// stored in bucket: 2018699554 % 16 = bucket 14

map.get(e2);
// looks in bucket: 1311053135 % 16 = bucket 7 ← WRONG BUCKET
// returns null ✗
// equals() is never called because the bucket is wrong
```

**This is the most common mistake and the most common interview question.**

### Scenario C: Only hashCode() overridden — equals() NOT overridden ❌

```java
class Employee {
    int id;
    @Override
    public int hashCode() {
        return id;  // correct
    }
    // equals() NOT overridden → uses Object's ==
}

Employee e1 = new Employee(101);
Employee e2 = new Employee(101);

map.put(e1, "John");
map.get(e2);
// hashCode: both → 101 → same bucket ✓ (bucket found!)
// equals:   e1 == e2 → false ✗ (different references)
// returns null ✗
```

### Scenario D: Both correctly overridden ✅

```java
class Employee {
    int id;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Employee)) return false;
        return this.id == ((Employee) obj).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

Employee e1 = new Employee(101);
Employee e2 = new Employee(101);

map.put(e1, "John");
map.get(e2);  // "John" ✅

// hashCode: 101 → same bucket ✓
// equals:   id == id → true ✓
// returns "John" ✅
```

### Summary Table

| Scenario | hashCode same? | equals matches? | get() works? |
|---|---|---|---|
| Neither overridden | ❌ Different (address) | ❌ Uses == | ❌ Fails |
| Only equals() overridden | ❌ Different (address) | ✅ Logic works | ❌ Fails (wrong bucket) |
| Only hashCode() overridden | ✅ Same (by id) | ❌ Uses == | ❌ Fails (== check) |
| **Both overridden correctly** | ✅ Same | ✅ Logic works | **✅ Works** |

---

## 6. Mutable Key Trap — Senior-Level Danger

### The Question (Very Commonly Asked for 8 YOE)

```java
Employee e1 = new Employee(101);
map.put(e1, "John");

e1.id = 999;   // ← mutate the key AFTER putting it

String result = map.get(e1);
System.out.println(result);  // What prints?
```

### Answer: `null` — and here's exactly why

```
At put() time:
  e1.id = 101
  e1.hashCode() = 101
  bucket = 101 % 16 = 5
  Entry stored: bucket 5 → [key=e1(id=101), value="John"]

After e1.id = 999:
  e1.id = 999              ← the object in bucket 5 is now mutated!
  e1.hashCode() = 999      ← hashCode has changed

At get() time:
  e1.hashCode() = 999
  bucket = 999 % 16 = 7   ← DIFFERENT bucket!
  bucket 7 is empty
  returns null ✗
```

### Visualized

```
BEFORE mutation:             AFTER mutation:
  Bucket 5: [e1,"John"]       Bucket 5: [e1,"John"]  ← still here (orphaned!)
  (e1.id = 101)               (e1.id = 999, stuck in wrong bucket)
                               Bucket 7: EMPTY
                               get(e1) looks in bucket 7 → null ✗
```

**The entry is still in the map but can NEVER be retrieved or removed. This is a memory leak.**

### How to Prevent It

```java
// Option 1: Use immutable types as keys (ALWAYS preferred)
Map<String, String>  map1 = new HashMap<>();   // String is immutable ✅
Map<Integer, String> map2 = new HashMap<>();   // Integer is immutable ✅
Map<Long, String>    map3 = new HashMap<>();   // Long is immutable ✅

// Option 2: Make key fields final
class Employee {
    private final int id;   // final — can't be changed after construction ✅
    Employee(int id) { this.id = id; }
}

// Option 3: Use Java 16+ record (auto-generates correct equals/hashCode + immutable)
record EmployeeKey(int id) {}   // immutable, equals/hashCode auto-generated ✅

map.put(new EmployeeKey(101), "John");
map.get(new EmployeeKey(101));  // "John" ✅ — always works
```

---

## 7. Correct Implementation — Best Practices

### Using Objects.hash() — Clean and Null-safe

```java
import java.util.Objects;

class Employee {
    private final int id;
    private final String name;
    private final String department;

    Employee(int id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;                    // 1. same reference check
        if (obj == null) return false;                   // 2. null check
        if (getClass() != obj.getClass()) return false;  // 3. type check
        Employee e = (Employee) obj;
        return id == e.id &&                             // 4. field comparisons
               Objects.equals(name, e.name) &&           //    null-safe for objects
               Objects.equals(department, e.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, department);   // combines multiple fields cleanly
    }
}
```

### Using Java 16+ Record (Best for Keys)

```java
// record auto-generates: equals(), hashCode(), toString(), getters
// ALL fields are final → immutable → safe HashMap key
record EmployeeKey(int id, String department) {}

// Usage:
Map<EmployeeKey, EmployeeDetails> cache = new HashMap<>();
cache.put(new EmployeeKey(101, "IT"), details);

EmployeeDetails d = cache.get(new EmployeeKey(101, "IT"));  // ✅ always works
```

### Using IDE-Generated (IntelliJ/Eclipse)

```java
// IntelliJ: Alt+Insert → equals() and hashCode()
// Always generates both together, correct contract guaranteed

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Employee employee = (Employee) o;
    return id == employee.id &&
           Objects.equals(name, employee.name);
}

@Override
public int hashCode() {
    return Objects.hash(id, name);   // IDE always generates this alongside equals
}
```

### Using Lombok (Common in enterprise projects)

```java
@EqualsAndHashCode       // generates both based on all fields
class Employee {
    int id;
    String name;
}

// Or specific fields only:
@EqualsAndHashCode(of = {"id"})   // only use id field for equality
class Employee {
    int id;
    String name;  // excluded from equals/hashCode
}
```

---

## 8. equals() Contract — 5 Properties You Must Know

Java requires `equals()` to satisfy these five properties. Violating any of them breaks collections, sets, and maps.

```java
// 1. REFLEXIVE
x.equals(x) == true   // always

// 2. SYMMETRIC
if x.equals(y) == true
then y.equals(x) == true   // must work both ways

// 3. TRANSITIVE
if x.equals(y) == true
and y.equals(z) == true
then x.equals(z) == true   // chain must hold

// 4. CONSISTENT
x.equals(y)  // must return same result on every call
             // as long as x and y don't change

// 5. NON-NULL
x.equals(null) == false   // always, never throws NPE
```

### Common Violation: Symmetric broken with inheritance

```java
class Point {
    int x, y;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) return false;
        Point p = (Point) obj;
        return x == p.x && y == p.y;
    }
}

class ColorPoint extends Point {
    String color;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ColorPoint)) return false;   // ← broken!
        ColorPoint cp = (ColorPoint) obj;
        return super.equals(cp) && color.equals(cp.color);
    }
}

Point p      = new Point(1, 2);
ColorPoint cp = new ColorPoint(1, 2, "red");

p.equals(cp)   // true  (Point.equals uses instanceof Point — cp IS a Point)
cp.equals(p)   // false (ColorPoint.equals uses instanceof ColorPoint — p is NOT)
// SYMMETRIC VIOLATED!
```

**Fix:** Use `getClass() != obj.getClass()` instead of `instanceof` for exact type match.

---

## 9. Real-World Use Cases

### Banking — Account Lookup Cache

```java
record AccountKey(String accountNumber, String accountType) {}

Map<AccountKey, BigDecimal> transferLimits = new HashMap<>();

// Load from DB at startup
transferLimits.put(new AccountKey("ACC001", "SAVINGS"), new BigDecimal("100000"));
transferLimits.put(new AccountKey("ACC001", "CURRENT"), new BigDecimal("500000"));

// Later — different service, different request:
AccountKey key = new AccountKey("ACC001", "SAVINGS");
BigDecimal limit = transferLimits.get(key);   // 100000 ✅
// Works because record generates equals()+hashCode() using accountNumber+accountType
```

### Session Cache — Employee Details

```java
// In-memory cache — different request, same employee id
Map<Employee, EmployeeDetails> sessionCache = new HashMap<>();

// Request 1 loads and caches
sessionCache.put(new Employee(101), loadFromDB(101));

// Request 2 checks cache — new Employee object, same id
EmployeeDetails details = sessionCache.get(new Employee(101));   // ✅ cache hit
// Works because equals/hashCode use id field
```

### Deduplication with HashSet

```java
// HashSet uses the same hashCode + equals logic internally
Set<Employee> uniqueEmployees = new HashSet<>();

uniqueEmployees.add(new Employee(101, "Kriti"));
uniqueEmployees.add(new Employee(101, "Kriti"));   // duplicate — same id, name
uniqueEmployees.add(new Employee(102, "Raj"));

System.out.println(uniqueEmployees.size());  // 2  (not 3) ✅
// Without equals/hashCode override → size would be 3 (no dedup)
```

---

## 10. Tricky Code — What Does This Print?

### Question 1 — String Pool

```java
String s1 = "hello";
String s2 = new String("hello");

Map<String, Integer> map = new HashMap<>();
map.put(s1, 1);

System.out.println(map.get(s2));   // ?
System.out.println(s1 == s2);      // ?
```

**Answer:**
```
map.get(s2)  →  1       ← String.equals() compares content, not reference
s1 == s2     →  false   ← different references (s2 is on heap, s1 in pool)
```

---

### Question 2 — Integer Cache (127 vs 128 trap)

```java
Integer a = 127;
Integer b = 127;
System.out.println(a == b);       // ?

Integer c = 128;
Integer d = 128;
System.out.println(c == d);       // ?
System.out.println(c.equals(d));  // ?
```

**Answer:**
```
a == b        →  true   ← Integer cache: -128 to 127 → same cached object
c == d        →  false  ← outside cache range → new Integer objects → different refs
c.equals(d)   →  true   ← Integer.equals() compares int value, not reference
```

---

#### 🧠 Why This Happens — The Java Integer Cache (Deep Dive)

**Step 1 — Autoboxing under the hood**

When you write `Integer a = 127`, Java does NOT directly create an Integer object.
The compiler converts this to:

```java
Integer a = Integer.valueOf(127);   // compiler does this automatically
```

This process of converting a primitive `int` to an `Integer` object is called **autoboxing**.

**Step 2 — The Integer Cache**

`Integer.valueOf()` is NOT simply `new Integer(value)`. It has a built-in optimization:

```java
// Simplified source of Integer.valueOf() inside JDK:
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high) {
        return IntegerCache.cache[i + (-IntegerCache.low)];  // return CACHED object
    }
    return new Integer(i);   // create NEW object only if outside range
}
```

Java pre-creates and caches `Integer` objects for the range **-128 to 127** at JVM startup.
These cached objects live in a special array: `IntegerCache.cache[]`.

**Step 3 — Why `a == b` is `true` (value 127)**

```
Integer a = Integer.valueOf(127);
  → 127 is in cache range [-128, 127]
  → returns IntegerCache.cache[255]   ← same object reference

Integer b = Integer.valueOf(127);
  → 127 is in cache range
  → returns IntegerCache.cache[255]   ← SAME object reference!

a == b   →  compares references  →  same object  →  true ✅
```

Memory picture:
```
Stack:    a ──────────────────┐
                               ▼
Heap:               IntegerCache.cache[255] { value=127 }
                               ▲
Stack:    b ──────────────────┘
```
Both `a` and `b` point to the EXACT same object.

**Step 4 — Why `c == d` is `false` (value 128)**

```
Integer c = Integer.valueOf(128);
  → 128 is OUTSIDE cache range
  → returns new Integer(128)    ← creates a new heap object at address @X1

Integer d = Integer.valueOf(128);
  → 128 is OUTSIDE cache range
  → returns new Integer(128)    ← creates ANOTHER new heap object at address @X2

c == d   →  compares references  →  @X1 ≠ @X2  →  false ✗
```

Memory picture:
```
Stack:    c ──►  Heap: Integer@X1 { value=128 }

Stack:    d ──►  Heap: Integer@X2 { value=128 }
```
Two completely separate objects, even though their values are identical.

**Step 5 — Why `c.equals(d)` is `true`**

```java
// Integer.equals() source (simplified):
public boolean equals(Object obj) {
    if (obj instanceof Integer) {
        return value == ((Integer)obj).intValue();  // compares int VALUES, not refs
    }
    return false;
}
```

```
c.equals(d)
  → c.intValue() = 128
  → d.intValue() = 128
  → 128 == 128  →  true ✅
```

`equals()` does not care about memory location — it only checks the actual integer value.

---

#### 📊 Cache Range Visualization

```
JVM Integer Cache (loaded at startup):

Index:    0      1      2    ...  127    128   ...  254   255
Value:  -128   -127  -126   ...   -1      0   ...  126   127
         ↑                                                  ↑
       cache[0]                                       cache[255]

All 256 objects pre-created. Any valueOf() call in [-128,127] returns from here.
Outside this range → always a fresh new Integer object on heap.
```

#### 🔧 Advanced: Can You Change the Cache Upper Limit?

Yes — using a JVM flag at startup:

```bash
# Extend cache upper limit to 1000
java -XX:AutoBoxCacheMax=1000 MyApp

# Now Integer values up to 1000 are also cached:
Integer x = 1000;
Integer y = 1000;
System.out.println(x == y);   // true (if JVM flag applied)
```

> Note: The lower bound is always -128 and cannot be changed. Only the upper bound is configurable.

#### Does This Apply to Other Wrapper Types?

| Wrapper | Cache Range | Notes |
|---|---|---|
| `Integer` | -128 to 127 | Configurable upper limit via JVM flag |
| `Long` | -128 to 127 | Same range, same behaviour |
| `Short` | -128 to 127 | Same range |
| `Byte` | -128 to 127 | Entire Byte range is cached (always) |
| `Character` | 0 to 127 | ASCII range cached |
| `Boolean` | `true`, `false` | Both values always cached (only 2 possible values) |
| `Double`, `Float` | ❌ No cache | Floating point — no caching at all |

```java
// Long behaves identically to Integer:
Long x = 127L;
Long y = 127L;
System.out.println(x == y);       // true  (cached)

Long p = 128L;
Long q = 128L;
System.out.println(p == q);       // false (not cached)
System.out.println(p.equals(q));  // true  (same value)

// Boolean — always same object:
Boolean t1 = true;
Boolean t2 = true;
System.out.println(t1 == t2);     // true  (Boolean.TRUE is a constant)
```

---

#### 💡 Best Practice

```java
// ❌ WRONG — never use == for Integer/Long/Short comparison
if (a == b) { ... }       // works for -128..127, silently breaks outside

// ✅ CORRECT — always use equals() for wrapper types
if (a.equals(b)) { ... }  // works for ALL values, always correct

// ✅ ALSO CORRECT — unbox to primitive
if (a.intValue() == b.intValue()) { ... }

// ✅ CLEANEST — let Java unbox automatically in comparison
int x = a;
int y = b;
if (x == y) { ... }   // primitives — == always compares values
```

> 🔑 **Golden Rule:** Always use `.equals()` when comparing `Integer`, `Long`, `Short`, `Byte`, `Character` wrapper objects. The `==` operator compares object references, not values — and the cache makes it work "accidentally" for small numbers, creating bugs that only appear in production with larger values.

---

### Question 3 — What Happens in HashSet?

```java
class Box {
    int size;
    Box(int size) { this.size = size; }
    // NO equals(), NO hashCode() overridden
}

Set<Box> boxes = new HashSet<>();
boxes.add(new Box(10));
boxes.add(new Box(10));
boxes.add(new Box(10));

System.out.println(boxes.size());  // ?
```

**Answer:**
```
3  ← each new Box() has different memory address → different hashCode → no dedup
```

With proper equals/hashCode overriding → size would be `1`.

---

### Question 4 — Mutable Key (List as HashMap key)

```java
List<Integer> key1 = new ArrayList<>(Arrays.asList(1, 2, 3));
Map<List<Integer>, String> map = new HashMap<>();
map.put(key1, "value");

key1.add(4);  // mutate the key!

System.out.println(map.get(key1));                           // ?
System.out.println(map.get(Arrays.asList(1, 2, 3)));         // ?
System.out.println(map.get(Arrays.asList(1, 2, 3, 4)));      // ?
```

**Answer:**
```
map.get(key1)                        →  null  (hashCode changed after mutation)
map.get(Arrays.asList(1,2,3))        →  null  (old hashCode, bucket mismatch)
map.get(Arrays.asList(1,2,3,4))      →  null  (new hashCode, bucket is empty)
```

**All three return null — the entry is permanently orphaned and unreachable.**

---

#### 🧠 Why This Happens — The Broken Hash Contract (Deep Dive)

A `HashMap` relies on two things working together at ALL times:
- `hashCode()` → finds the correct storage bucket
- `equals()` → finds the exact key inside that bucket

A `List` in Java calculates its `hashCode()` based **entirely on its elements**. The moment you add or remove an element, the `hashCode()` changes — breaking the contract while the key is inside the map.

#### 🔍 Step-by-Step Bucket Breakdown

**Step 1 — `map.put(key1, "value")` (key1 = [1, 2, 3])**

```
key1 = [1, 2, 3]
key1.hashCode() = 30817   ← computed from elements 1, 2, 3
bucket index    = 30817 % 16 = Bucket A

HashMap state:
  Bucket A → [key=[1,2,3], value="value"]   ✅ stored here
```

**Step 2 — `key1.add(4)` — the mutation**

```
key1 is now [1, 2, 3, 4]
key1.hashCode() = 955331  ← completely different! (element 4 added)

BUT the entry is still physically sitting in Bucket A!
HashMap has NO idea the key changed — it doesn't watch keys.

HashMap state (now corrupted):
  Bucket A → [key=[1,2,3,4], value="value"]   ← key mutated, wrong bucket!
  Bucket B → EMPTY
```

**Step 3 — `map.get(key1)` — why null?**

```
key1 is currently [1, 2, 3, 4]
key1.hashCode() = 955331
bucket index    = Bucket B   ← looks in NEW bucket based on current hashCode

Bucket B is EMPTY → returns null ✗

(The entry is sitting in Bucket A, but we're looking in Bucket B)
```

**Step 4 — `map.get(Arrays.asList(1, 2, 3))` — why null?**

```
lookup list = [1, 2, 3]
hashCode()  = 30817    ← correct original hashCode
bucket      = Bucket A ← goes to correct original bucket ✓

Inside Bucket A, finds the stored entry.
Calls equals():
  [1, 2, 3].equals([1, 2, 3, 4])   ← stored key is NOW [1,2,3,4] (mutated!)
  → false ✗

equals() fails because the key mutated → returns null ✗
```

**Step 5 — `map.get(Arrays.asList(1, 2, 3, 4))` — why null?**

```
lookup list = [1, 2, 3, 4]
hashCode()  = 955331   ← new hashCode
bucket      = Bucket B

Bucket B is completely EMPTY → returns null ✗

(Entry is in Bucket A with old hashCode, this lookup goes to Bucket B)
```

#### 📊 State Summary Table

| Operation | hashCode used | Bucket searched | What's found | Result |
|---|---|---|---|---|
| `put(key1=[1,2,3])` | 30817 | Bucket A | — | Stored ✅ |
| `key1.add(4)` | — | — | Key mutated in-place | Map corrupted |
| `get(key1=[1,2,3,4])` | 955331 | Bucket B | Empty | `null` ✗ |
| `get([1,2,3])` | 30817 | Bucket A | key=[1,2,3,4], equals fails | `null` ✗ |
| `get([1,2,3,4])` | 955331 | Bucket B | Empty | `null` ✗ |

#### 💥 The Orphaned Entry Problem

```
The entry [key=[1,2,3,4], value="value"] is still in Bucket A.
It can NEVER be retrieved → because its hashCode no longer matches its bucket.
It can NEVER be removed   → map.remove() has the same problem.
It holds memory forever   → this is a MEMORY LEAK.
```

#### ✅ How to Fix — Use Immutable Keys

```java
// Fix 1: List.of() — immutable list (Java 9+)
List<Integer> key = List.of(1, 2, 3);
map.put(key, "value");
// key.add(4);  ← throws UnsupportedOperationException — mutation prevented ✅

// Fix 2: Collections.unmodifiableList()
List<Integer> key = Collections.unmodifiableList(Arrays.asList(1, 2, 3));
map.put(key, "value");
// key.add(4);  ← throws UnsupportedOperationException ✅

// Fix 3: Custom immutable wrapper as key
record ListKey(List<Integer> items) {
    ListKey {
        items = List.copyOf(items);   // defensive copy, immutable
    }
}
ListKey key = new ListKey(Arrays.asList(1, 2, 3));
map.put(key, "value");
// The internal list can never change → hashCode always stable ✅

// Retrieve safely — always works:
map.get(new ListKey(Arrays.asList(1, 2, 3)));   // "value" ✅
```

#### ⚠️ The Golden Rule of Map Keys

```
NEVER use mutable objects as HashMap / HashSet keys.

Safe keys:    String, Integer, Long, record, List.of(), any final-fields class
Unsafe keys:  ArrayList, HashMap, any class with non-final fields,
              any object you might modify after inserting into the map
```

---

## 11. Interview Q&A — All Questions with Answers

### ❓ Q1. What happens if you only override equals() and not hashCode()?

> "If I override `equals()` but not `hashCode()`, two logically equal objects will have different hashCodes because `Object.hashCode()` is based on memory address. When I `put()` with one object and `get()` with another equal object, they go to different buckets — `equals()` is never even called — and `get()` returns null. This violates the HashMap contract and causes silent data loss. That's why Java's documentation explicitly says: if you override `equals()`, you MUST override `hashCode()`."

---

### ❓ Q2. Can two unequal objects have the same hashCode?

> "Yes — this is called a hash collision and is perfectly legal. Rule 2 of the contract says `hashCode()` equality does NOT guarantee `equals()` equality. When two different keys land in the same bucket, HashMap uses a linked list (or Red-Black Tree in Java 8+ when size > 8) to store both. On lookup, it calls `equals()` on each entry in the bucket to find the exact key. Collisions reduce performance — from O(1) towards O(log n) — but never cause incorrect results."

---

### ❓ Q3. What is the default hashCode() of Object?

> "The default `Object.hashCode()` is a native method that typically returns a number derived from the object's memory address. It's guaranteed to be different for different objects (usually), which is correct for reference equality but wrong for logical/value equality. That's why we must override it when we want logical equality to work in hash-based collections."

---

### ❓ Q4. Why should you use immutable objects as HashMap keys?

> "If a key's hashCode changes after being put into a HashMap, the key lands in a different bucket on the next lookup — so `get()` returns null even though the entry exists. The entry becomes orphaned — it can't be retrieved or removed, causing a memory leak. Immutable objects like `String`, `Integer`, and `record` types can never change their state, so their hashCode is always stable. That's why they're the safest HashMap key types."

---

### ❓ Q5. What is the difference between `instanceof` and `getClass()` in equals()?

```java
// instanceof approach — allows subclass to equal superclass
if (!(obj instanceof Employee)) return false;
// Problem: symmetric contract can break with inheritance
// e.equals(coloredE) may differ from coloredE.equals(e)

// getClass() approach — exact type match required
if (getClass() != obj.getClass()) return false;
// Safe: always symmetric, but rejects subclasses
```

> "For most cases, `getClass()` is safer because it enforces symmetry. `instanceof` can violate the symmetric contract with inheritance hierarchies. However, if you specifically want subtypes to be considered equal (like an abstract base class), `instanceof` is appropriate — but you must be careful with symmetry."

---

### ❓ Q6. How does String compute its hashCode?

```java
// Java's String.hashCode() formula:
s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]

// Why 31?
// 1. Odd prime — reduces collisions
// 2. 31 * i == (i << 5) - i — JVM can optimize multiplication to bit shift
// 3. Empirically found to give good distribution

// String caches its hashCode in a field (lazy init, then cached):
// private int hash;  // defaults to 0
// hashCode() computed once, stored, reused
```

---

### ❓ Q7. Can null be a key in HashMap?

```java
Map<String, Integer> map = new HashMap<>();
map.put(null, 42);
System.out.println(map.get(null));   // 42 ✅

// null key is always stored at bucket 0
// HashMap handles null specially: hash(null) = 0
```

| Collection | null key | null value |
|---|---|---|
| `HashMap` | ✅ One null key | ✅ Multiple null values |
| `Hashtable` | ❌ NPE | ❌ NPE |
| `ConcurrentHashMap` | ❌ NPE | ❌ NPE |
| `TreeMap` | ❌ NPE (needs compareTo) | ✅ Allowed |
| `LinkedHashMap` | ✅ One null key | ✅ Allowed |

---

### ❓ Q8. What is IdentityHashMap? When to use it?

> "`IdentityHashMap` uses `==` (reference equality) instead of `equals()` for key comparison, and `System.identityHashCode()` instead of `hashCode()`. Use it when you specifically need reference-based comparison — for example, in object graph traversal (like Java serialization) where you want to track which exact objects you've visited, not which 'equal' objects."

```java
IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
// Treats two equal objects as different keys because they're different references
// Used internally by Java's serialization and deep copy utilities
```

---

## 12. Quick Reference Cheat Sheet

### The Contract in One Diagram

```
equals(true)  ──────────────────────────────────────►  hashCode SAME
                                                        (Rule 1: mandatory)

hashCode SAME ──────────────────────────────────────►  equals TRUE or FALSE
                                                        (Rule 2: collision OK)
```

### HashMap Two-Step Lookup

```
hashCode()  →  WHICH bucket   (navigation, fast)
equals()    →  IS THIS KEY     (verification, precise)

Miss at hashCode step  →  equals never called → null
Miss at equals step    →  wrong key in bucket → null
Both match             →  value returned ✅
```

### Safe Key Types

| Key Type | Immutable? | equals/hashCode? | Production safe? |
|---|---|---|---|
| `String` | ✅ | ✅ Content-based, cached | ✅ Best choice |
| `Integer`, `Long`, `Double` | ✅ | ✅ Value-based | ✅ Best choice |
| `record` (Java 16+) | ✅ | ✅ Auto-generated from all fields | ✅ Best choice |
| `UUID` | ✅ | ✅ Correct | ✅ Good |
| Custom class with final fields | ✅ | ✅ If overridden | ✅ If done right |
| Custom mutable class | ❌ | ⚠️ Risky | ❌ Avoid |
| `List`, `Map`, `Set` | ❌ | ✅ Content-based | ❌ Mutable — never use |
| Array `int[]`, `String[]` | ❌ | ❌ Uses `==` | ❌ Never use |

### Objects.hash() vs Manual hashCode

```java
// Manual (verbose, error-prone):
@Override
public int hashCode() {
    int result = 17;
    result = 31 * result + id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
}

// Objects.hash() (clean, null-safe, preferred):
@Override
public int hashCode() {
    return Objects.hash(id, name, department);
}

// record (best — auto-generated, immutable):
record Employee(int id, String name) {}
// equals() and hashCode() auto-generated using id and name ✅
```

### Checklist Before Using a Custom Object as HashMap Key

```
☐ equals() overridden — uses field values, NOT ==
☐ hashCode() overridden — same fields as equals()
☐ Both in same class — never one without the other
☐ Fields used are immutable (final) or never mutated after creation
☐ equals() is reflexive, symmetric, transitive, consistent, non-null
☐ hashCode() returns same value for logically equal objects
☐ Consider using record (Java 16+) for auto-correctness
```

---

### Memory Tip

```
hashCode  →  which AREA of the city      (neighbourhood/zip code)
equals    →  is this the EXACT HOUSE     (street address + door number)

Good hashCode: spreads objects across different buckets (good distribution)
Good equals:   precisely identifies the exact key match
```

---

*Prepared from Claude AI session | Kriti Singh | 8 YOE Java Developer*
*Topics: equals() · hashCode() · Contract · HashMap internals · Collision · Mutable keys · Best practices*
