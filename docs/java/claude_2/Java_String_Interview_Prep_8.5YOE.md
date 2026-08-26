# Java String — Interview Prep (8.5 Years Backend Experience)

> Target level: Senior Java Backend Engineer / Lead. Expect deep-dive on **memory model (String Pool vs Heap)**, **immutability rationale**, **performance trade-offs**, and **live-coding traps** — not "what is a String" basics.

---

## 1. String Class — Structure Overview (not a hierarchy like Collections, but know the class relationships)

```
java.lang.Object
      |
java.lang.String   (final class, implements Serializable, Comparable<String>, CharSequence)

CharSequence (interface)
      |------------------------------------------
      |              |              |            |
    String      StringBuilder   StringBuffer   (also implemented by CharBuffer, Segment, etc.)
                (final, not      (final, thread-
                 thread-safe)     safe, synchronized)

Both StringBuilder and StringBuffer:
      extend AbstractStringBuilder (package-private, holds the mutable char[]/byte[] buffer + count)
```

**Key structural facts:**
- `String` is `final` → cannot be subclassed.
- Internally backed by `private final byte[] value;` since **Java 9** (Compact Strings — see Q6), was `private final char[] value;` pre-Java-9.
- Implements `Comparable<String>` (lexicographic `compareTo`) and `CharSequence` (generic read-only sequence abstraction shared with `StringBuilder`, `StringBuffer`).

---

## 2. Foundational Questions

### Q1. Why is `String` immutable in Java? (Always asked — give the full multi-reason answer)
1. **String Pool / Interning:** Since `String` is immutable, the JVM can safely **share a single instance** across multiple references via the String Constant Pool, saving significant heap memory — mutation would corrupt every reference sharing that pooled instance.
2. **Security:** Strings are used for class names in `Class.forName()`, file paths, network connections, DB URLs, usernames/passwords. If `String` were mutable, a malicious caller could pass a `String` reference to a security-sensitive API, then mutate it *after* the security check but *before* actual use — a classic **TOCTOU (time-of-check-to-time-of-use)** attack. Immutability closes this hole entirely.
3. **Thread-safety:** Immutable objects are inherently thread-safe with no synchronization needed — `String` can be freely shared across threads.
4. **Hashcode caching:** Because content never changes, `String` computes `hashCode()` once and caches it (`private int hash;`) — massively speeds up repeated use as `HashMap`/`HashSet` keys (see Java Collections template Q33).
5. **Safe for use as `HashMap` keys generally** — an immutable key can never become "orphaned" in the wrong bucket (contrast with the mutable-key `HashMap` bug covered in the Collections FAQ).

### Q2. How is immutability actually enforced in the `String` class implementation?
- Class declared `final` → no subclass can override behavior.
- Backing array declared `private final` → reference can't be reassigned.
- **Crucially**, no method exposes the backing array directly (unlike a naive `getBytes()`-style leak) — all mutator-looking methods (`concat()`, `replace()`, `substring()`, `toUpperCase()`, etc.) return a **brand-new `String` object**, leaving the original untouched.
- Pre-Java-9, one subtlety: reflection *could* technically mutate the private `char[] value` via `setAccessible(true)` — immutability is a language-level contract, not an absolute JVM guarantee against reflection abuse.

### Q3. `String s = "hello"` vs `String s = new String("hello")` — what's the actual difference?
- `String s = "hello";` → JVM checks the **String Constant Pool (SCP)** first. If `"hello"` already exists there, `s` just references the existing pooled instance. If not, it creates one in the pool.
- `String s = new String("hello");` → **Always** creates a new object on the **heap** (outside the pool), even if `"hello"` already exists in the pool. The literal `"hello"` argument itself still gets pooled/reused, but the `new String(...)` wrapper object is a distinct heap object.
- **Result:** `new String("hello") == "hello"` → **`false`** (different objects), but `new String("hello").equals("hello")` → **`true`** (same content).

### Q4. Where does the String Constant Pool live in memory (JVM internals)?
- **Pre-Java 7:** String Pool was part of **PermGen** (Permanent Generation), a fixed-size space — a common source of `OutOfMemoryError: PermGen space` when apps `intern()`'d too many unique strings.
- **Java 7 onward:** String Pool was **moved to the Heap** (specifically, it's GC-able heap memory now, not PermGen/Metaspace) — this allows pooled strings to be garbage collected when no longer referenced, and removed the PermGen-overflow risk category for this specific cause.
- (Related but separate fact: PermGen itself was replaced by **Metaspace** in Java 8 for class metadata — often conflated with the Java 7 string pool move in interviews; keep them distinct.)

### Q5. What does `String.intern()` do? Give a real use case.
`intern()` looks up the string's content in the SCP. If an equal string already exists there, it returns **that pooled reference**; otherwise, it adds the current string's content to the pool and returns a reference to it.
```java
String a = new String("test").intern();
String b = "test";
System.out.println(a == b); // true — a now points to the pooled instance
```
**Real use case:** Deduplicating memory for a large number of repeated string values (e.g., parsing a huge CSV/log file where many rows repeat the same `"status": "ACTIVE"` string) — interning avoids thousands of duplicate heap objects. **Caveat:** Excessive interning of high-cardinality/unique strings can itself bloat the pool and hurt performance/GC — use judiciously, and prefer `Set`-based deduplication or `Map.computeIfAbsent`-style manual pools for structured data if you need more control.

### Q6. What are "Compact Strings" (Java 9, JEP 254)?
Pre-Java-9, `String` internally used `char[]` — **always 2 bytes per character (UTF-16)**, even for plain ASCII/Latin-1 content that only needs 1 byte. Java 9 changed the backing store to `byte[]` plus a `coder` flag:
- If all characters fit in **Latin-1 (ISO-8859-1)**, store as 1 byte/char (`LATIN1` coder).
- Otherwise, fall back to 2 bytes/char (`UTF16` coder).
Since most real-world strings (English text, JSON keys, identifiers) are Latin-1-representable, this **roughly halves memory usage** for typical applications with no source-code changes required.

---

## 3. Mutable String Classes: StringBuilder vs StringBuffer

### Q7. `StringBuilder` vs `StringBuffer` vs `String` — full comparison.
| | String | StringBuilder | StringBuffer |
|---|---|---|---|
| Mutability | Immutable | Mutable | Mutable |
| Thread-safety | Yes (inherently, via immutability) | **No** | Yes (every method `synchronized`) |
| Performance | N/A | Fast | Slower (sync overhead) |
| Introduced | JDK 1.0 | **JDK 5** | JDK 1.0 |
| Use case | Fixed/rarely-changing text, HashMap keys | Single-threaded string building (loops, concatenation) | Multi-threaded shared string building (rare in modern code — prefer local `StringBuilder` + explicit synchronization or `StringBuffer` only if truly needed) |

**Practical guidance for 2024+ codebases:** Almost always use `StringBuilder`. `StringBuffer`'s per-method synchronization is a legacy design (JDK 1.0) that predates the `java.util.concurrent` package and better concurrency primitives — genuinely shared mutable string-building across threads is a rare and usually poor design in modern services anyway.

### Q8. Why is string concatenation in a loop (`s += x`) a performance anti-pattern?
```java
String result = "";
for (int i = 0; i < 10000; i++) {
    result += i;   // creates a NEW String object every iteration!
}
```
Each `+=` creates a brand new `String` (since `String` is immutable) — this is O(n²) overall for `n` concatenations because each concat copies the entire accumulated content into a new array. **Fix:** use `StringBuilder.append()` in a loop → O(n) amortized (thanks to StringBuilder's own array-doubling growth, same 1.5x-2x growth strategy as ArrayList/AbstractStringBuilder).
**Important nuance:** For a **fixed, small number** of compile-time-constant concatenations (e.g., `String s = "a" + "b" + "c";`), the **javac compiler itself optimizes this into a single constant** at compile time (or uses `StringBuilder` internally for runtime-variable concatenation, or `invokedynamic` + `StringConcatFactory` since Java 9/JEP 280) — so a *single* `s1 + s2 + s3` line isn't the concern; it's concatenation **inside a loop** that's the real anti-pattern.

### Q9. How does `StringBuilder`'s internal capacity growth work?
Default initial capacity = 16 (or `input.length() + 16` if constructed from a `String`). On overflow: new capacity = `(oldCapacity << 1) + 2` (roughly 2x + 2), then `Arrays.copyOf()`. If you know the final size upfront, pass it to the constructor (`new StringBuilder(expectedSize)`) to avoid repeated resize+copy — identical performance-tuning principle to `ArrayList` (see Collections template Q5).

---

## 4. `==` vs `.equals()` vs `.compareTo()` — Tricky String Comparison Questions

### Q10. Classic trap: explain the output of each line.
```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");
String s4 = new String("hello");
String s5 = "hel" + "lo";              // compile-time constant expression
String s6 = "hel";
String s7 = s6 + "lo";                 // runtime concatenation (s6 is a variable)

System.out.println(s1 == s2);          // true  -> both from pool, same reference
System.out.println(s1 == s3);          // false -> s3 is a new heap object
System.out.println(s3 == s4);          // false -> two distinct new String() objects
System.out.println(s3.equals(s4));     // true  -> same content
System.out.println(s1 == s5);          // true  -> compiler folds "hel"+"lo" into "hello" at COMPILE TIME, pooled
System.out.println(s1 == s7);          // false -> runtime concat via StringBuilder produces a NEW heap object, NOT pooled automatically
System.out.println(s1 == s7.intern()); // true  -> intern() forces pool lookup/registration
```
**This is arguably the single most common String interview trick** — the distinction between **compile-time constant folding** (pooled automatically) vs **runtime concatenation** (heap object, not auto-pooled) trips up even experienced developers.

### Q11. Why does `String` override `equals()` to do content comparison, but `==` still does reference comparison?
`==` is a language-level operator that **always** compares references for non-primitive types — it's not overridable, unlike `equals()`. `String` overrides `Object.equals()` (which by default is `==`) to instead compare **character-by-character content**, following the Collections-Framework-wide convention that `equals()` should represent **logical/value equality**. This is exactly why `.equals()` (or `Objects.equals()` for null-safety) should always be used for content comparison, never `==`, except when you specifically want identity comparison (e.g., checking if two references point to the exact pooled instance, a niche case).

### Q12. `equalsIgnoreCase()` vs `.toLowerCase().equals()` — any difference / why prefer one?
`equalsIgnoreCase()` is preferred: it does a **locale-independent**, character-by-character case-insensitive comparison without allocating any intermediate `String` objects. `.toLowerCase().equals()` **allocates two new String objects** (wasteful) AND is **locale-sensitive** — famously, in the Turkish locale, `"I".toLowerCase()` produces `"ı"` (dotless i), not `"i"`, causing subtle bugs (the infamous **"Turkish locale bug"**, a real production incident pattern at companies with international deployments). Always use `equalsIgnoreCase()`, or explicitly pass `Locale.ROOT`/`Locale.ENGLISH` if you must lower/upper-case manually.

### Q13. `compareTo()` vs `compareToIgnoreCase()` — what does the return value actually mean?
Returns the **difference between the first differing character's Unicode values** (not just -1/0/1 — many candidates wrongly assume tri-state only). E.g., `"apple".compareTo("apply")` returns a negative number equal to `('e' - 'y')`. Zero means the strings are lexicographically identical (equivalent to `.equals()` for `String` specifically, though this is a `Comparable` contract point worth connecting to the Collections template's Q24 "consistent with equals" discussion).

---

## 5. Memory & Performance Deep-Dives

### Q14. The old (Java 6) `substring()` memory leak — what was it, and how was it fixed?
- **Pre-Java-7 (specifically fixed in Java 7u6):** `substring()` created a new `String` object that **shared the same backing `char[]` array** as the original string, just with different `offset`/`count` fields. This meant if you extracted a tiny substring from a huge string (e.g., a 1-character substring from a 1MB string) and discarded the original reference, **the entire 1MB char array stayed in memory** (pinned alive by the small substring) — a classic, hard-to-diagnose memory leak.
- **Java 7u6+ fix:** `substring()` now **copies** the required characters into a brand-new, appropriately-sized array — no more shared-array leak, at the cost of an O(n) copy on every substring call (a deliberate space-for-time trade-off reversal). **This is a genuinely tricky "gotcha" question for candidates who learned Java pre-2012 or come from older textbooks.**

### Q15. Is `String.format()` expensive? When should you avoid it in hot paths?
Yes, relatively — it uses **reflection-based parsing of the format string on every call** (parsing `%s`, `%d`, etc. patterns each time) plus `Locale`-aware formatting machinery, and internally builds via `Formatter`/`StringBuilder`. In hot/tight loops (e.g., high-throughput logging or serialization paths), prefer simple `StringBuilder.append()` concatenation, or better, use parameterized logging (e.g., SLF4J's `log.info("value={}", x)`) which **avoids string construction entirely if the log level is disabled** (lazy evaluation) — a real, commonly-tested performance-awareness question at senior level.

### Q16. Why does `String.split()` sometimes surprise people with trailing empty strings, and how is it related to regex compilation cost?
```java
"a,b,c,,".split(",");        // → ["a","b","c"] — trailing empty strings are REMOVED by default
"a,b,c,,".split(",", -1);    // → ["a","b","c","",""] — negative limit preserves trailing empties
```
Also: `split()` compiles the delimiter as a **regex** internally (via `Pattern.compile()`) on **every call** unless you pre-compile and reuse a `Pattern` object — a hidden performance cost in loops. For hot paths splitting on a fixed delimiter repeatedly, cache a compiled `Pattern` (or, for simple literal-character delimiters, consider `StringTokenizer` or manual `indexOf`-based splitting, which avoids regex engine overhead entirely).

### Q17. Text Blocks (Java 15+, JEP 378) — any interview-relevant subtleties?
```java
String json = """
    {
      "name": "%s"
    }
    """.formatted(name);
```
- Automatically strips **incidental leading whitespace** based on the *least-indented* line (including the closing `"""`).
- Trailing whitespace on each line is stripped by default (use `\s` to preserve an intentional trailing space).
- Still produces a regular immutable `String` at compile time — **behaves identically to any other String literal** for pooling purposes (a text block with the same final content as an existing pooled literal will still be pool-deduplicated by the compiler).

---

## 6. Tricky / Rapid-Fire Questions

**Q18. Why can't `String` be used safely as a `synchronized` lock object in production code (even though it technically compiles)?**
```java
private final String lock = "myLock"; // BAD
synchronized(lock) { ... }
```
Because string literals are **pooled and potentially shared across the entire JVM** (any other unrelated code using the literal `"myLock"` shares the exact same object), you can accidentally create **unintended cross-module lock contention or deadlocks** with completely unrelated code that happens to use an identical string literal as its own lock. **Always use a dedicated `private final Object lock = new Object();`** for explicit monitor locks.

**Q19. Is `String` a primitive type? What's the actual memory layout difference vs primitives like `int`?**
No — `String` is a reference type (object), even though it behaves specially (literal syntax, `+` operator overload, pooling) that makes it *feel* primitive-like. Unlike primitives (stored directly on the stack for locals, or inline in object headers for fields), a `String` variable holds a **reference** to a heap (or pool) object containing the actual `byte[]`/`char[]` data, length, hash cache, and object header overhead (~16 bytes minimum object header + array overhead) — meaningfully more memory per instance than, say, an `int`.

**Q20. What happens when you call `.intern()` on a string that already exists in the pool via a literal — does it create a duplicate?**
No — `intern()` performs a canonical lookup; if the content already exists in the pool (from a literal or a prior `intern()` call), it returns the **existing** reference, never duplicating pool entries. The pool is a `Map`-like structure (historically implemented as a native hash table in the JVM) guaranteeing one canonical instance per unique content.

**Q21. Are `String` objects eligible for garbage collection if they're in the String Pool?**
Yes, since Java 7 moved the pool to the heap — a pooled string with **zero remaining references anywhere** (including from the pool's own internal weak-reference-like tracking) becomes GC-eligible. This wasn't true when the pool lived in PermGen pre-Java-7 in the same way (PermGen GC was more limited/infrequent, contributing to the historical `PermGen space` OOM risk).

**Q22. `StringBuilder.toString()` — does it return a pooled string?**
No — it always allocates a **fresh** `String` object on the heap by copying the builder's internal char/byte array; it does NOT check or register with the String Pool automatically. You'd need to explicitly call `.intern()` on the result if pooling is desired.

**Q23. What's the output, and why?**
```java
String a = "abc";
String b = "ab" + getSuffix();  // getSuffix() returns "c" at runtime
System.out.println(a == b);
```
> `false` — because `getSuffix()` is a **runtime method call**, the compiler **cannot** constant-fold this expression (unlike pure literal concatenation), so `b` is built via `StringBuilder`/`invokedynamic` at runtime and is a fresh, non-pooled heap object, even though its final content ("abc") happens to match the pooled literal `a`.

**Q24. Why does `String` implement `Comparable<String>` but not `Cloneable`? Should you ever need to "clone" a String?**
`String` implements `Comparable` to support natural lexicographic ordering (needed for `TreeSet<String>`, `Collections.sort()`, etc.). It deliberately does **not** implement `Cloneable` because, being immutable, **cloning is meaningless and unnecessary** — you can always safely share the same reference instead of copying, since nothing can mutate it. This is a good conceptual question testing whether candidates truly understand *why* immutability obviates the need for defensive copying.

**Q25. What's wrong with this null-check pattern, and what's the fix?**
```java
if (userInput.equals("admin")) { ... }   // NPE risk if userInput is null
```
If `userInput` can be `null`, calling `.equals()` on it throws `NullPointerException`. **Fix:** reverse the comparison — `"admin".equals(userInput)` (calling `.equals()` on the known-non-null literal is always safe and short-circuits correctly to `false` for a null argument), or use `Objects.equals(userInput, "admin")` for full null-safety in both directions. This is a very common, very real defensive-coding habit interviewers explicitly probe for at senior level (does the candidate write this reflexively?).

**Q26. Difference between `String.valueOf(obj)` and `obj.toString()` — when does one fail where the other doesn't?**
`obj.toString()` throws `NullPointerException` if `obj` is `null`. `String.valueOf(obj)` internally null-checks and returns the literal string `"null"` instead of throwing. Similarly, `"" + obj` (concatenation) also safely handles null by producing `"null"` (uses `String.valueOf()` under the hood via the generated `StringBuilder`/`StringConcatFactory` code). Good defensive-null-handling knowledge check.

**Q27. Why is `hashCode()` on `String` computed as `s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]`? Why 31 specifically?**
31 is an **odd prime** — using a prime reduces the likelihood of systematic hash collisions across similar strings, and being **odd** means the multiplication `31 * i` can be efficiently optimized by the JVM/JIT into a bit-shift: `31 * i == (i << 5) - i`, which is faster than a full multiply on older hardware (a still-cited-but-now-mostly-historical performance rationale; modern JITs handle either form efficiently, but the algorithmic collision-reduction rationale for using a prime still holds).

---

## 7. Rapid Complexity / Behavior Cheat-Sheet

| Operation | Complexity / Behavior Note |
|---|---|
| `length()` | O(1) — length stored as a field, not computed |
| `charAt(i)` | O(1) |
| `substring()` | O(n) since Java 7u6 (copies array); was O(1) pre-7u6 (shared array — leak risk) |
| `concat()` / `+` (single expr) | O(n) — new array allocation each call |
| `equals()` | O(n) worst case, but often short-circuits on length mismatch first |
| `hashCode()` | O(n) first call, **O(1) every call after** (cached) |
| `indexOf()` | O(n*m) naive (n=haystack, m=needle) in worst case |
| `toCharArray()` | O(n) — always copies, never exposes internal array directly |
| `intern()` | O(1) average (native hash table lookup) |

---

## 8. Scenario Questions (8+ YOE flavor)

**Q28.** *"Your service does heavy string concatenation for building SQL queries dynamically in a loop, and you're seeing high GC pressure/CPU under load. Diagnose and fix."*
> Point to: (1) loop-based `+=` creating O(n²) garbage — switch to `StringBuilder`; (2) if pre-sized, use `new StringBuilder(estimatedLength)`; (3) longer-term, prefer parameterized/prepared statements (`PreparedStatement`) over string-built SQL anyway — both for performance (query plan caching) **and** SQL-injection safety, a good chance to pivot into a security-mindedness discussion.

**Q29.** *"You're deduplicating millions of repeated string values coming from a Kafka stream (e.g., country codes, status enums-as-strings) to reduce heap pressure. Would you use `String.intern()`? What are the risks?"*
> Discuss: `intern()` works but the native/global string pool is a shared, JVM-wide resource — over-interning **high-cardinality** data can itself cause pool bloat and, in older JVMs (pre-7), risked `PermGen` OOM; even on modern heap-based pools, an unbounded pool of "mostly unique" strings defeats the purpose and adds lookup overhead. **Better modern approach** for a bounded, known-cardinality set (like country codes/status enums): maintain your **own manually-managed `Map<String,String>`** (or, better yet, use actual Java `enum` types instead of raw Strings for closed sets) rather than relying on the global intern pool.

---

### How to use this file
- Section 6 (Tricky/Rapid-Fire) and Section 2 Q10 (the `==` trap) are the highest-yield areas for live-coding/whiteboard rounds — review these last, right before the interview.
- Be ready to **draw the memory diagram**: stack frame → reference variables → heap objects vs. String Constant Pool (inside heap since Java 7) — interviewers at this level frequently ask you to sketch this.
