# 🔤 Java String — Complete Interview Guide
> Level: 8.5 Years Experience | MNC / Product Companies
> Topics: Immutability · String Pool · StringBuilder · Methods · Streams · Tricky Q&A · FAQ

---

## 📌 Table of Contents

1. [String Class Hierarchy & Key Facts](#1-string-class-hierarchy--key-facts)
2. [Immutability — Why & How](#2-immutability--why--how)
3. [String Pool — intern() & Memory](#3-string-pool--intern--memory)
4. [String vs StringBuilder vs StringBuffer](#4-string-vs-stringbuilder-vs-stringbuffer)
5. [Important String Methods — All with Examples](#5-important-string-methods--all-with-examples)
6. [Java 8+ String with Streams](#6-java-8-string-with-streams)
7. [Java 11+ New String Methods](#7-java-11-new-string-methods)
8. [Tricky Interview Questions — Deep Explanations](#8-tricky-interview-questions--deep-explanations)
9. [String Coding Problems — MNC Favourites](#9-string-coding-problems--mnc-favourites)
10. [Interview Q&A — All FAQs with Answers](#10-interview-qa--all-faqs-with-answers)
11. [Quick Reference Cheat Sheet](#11-quick-reference-cheat-sheet)

---

## 1. String Class Hierarchy & Key Facts

```
java.lang.Object
    └── java.lang.String
          implements: Serializable, Comparable<String>, CharSequence

java.lang.CharSequence  (interface)
    ├── String          ← immutable, final class
    ├── StringBuilder   ← mutable, NOT thread-safe, faster
    ├── StringBuffer    ← mutable, thread-safe (synchronized), slower
    └── CharBuffer      ← NIO buffer

Key Facts About String:
  ✓ final class        → cannot be subclassed
  ✓ immutable          → internal char[] is private final (or byte[] in Java 9+)
  ✓ String Pool        → literal strings cached in heap (PermGen pre-Java 7)
  ✓ hashCode cached    → computed once, stored in private int hash field
  ✓ Serializable       → can be written to streams
  ✓ Comparable         → lexicographic natural ordering via compareTo()
  ✓ CharSequence       → works with any CharSequence API (regex, StringBuilder)
```

### Java 9+ Compact Strings

```
Before Java 9:
  String internally used char[] (2 bytes per character — UTF-16)

Java 9+ Compact Strings:
  String uses byte[] + encoding flag
  For Latin-1 (ASCII) strings: 1 byte per char → 50% less memory
  For non-Latin strings: 2 bytes per char (UTF-16) — same as before
  Completely transparent — no API change

  private final byte[] value;
  private final byte coder;   // LATIN1 = 0, UTF16 = 1

  Result: ~10-15% less memory for typical English text
```

---

## 2. Immutability — Why & How

### How String Achieves Immutability

```java
// Java String source (simplified):
public final class String implements Serializable, Comparable<String>, CharSequence {

    private final byte[] value;    // Java 9+  (char[] before Java 9)
    private final byte coder;      // encoding
    private int hash;              // cached hashCode (0 = not computed)

    // No setter for value — once set in constructor, NEVER changed
    // value is private + final → array reference can't be reassigned
    // BUT: final only prevents reassigning the reference, not mutating the array!
    // Immutability is enforced by making value private + no mutation methods exposed
}
```

### Why Immutability Matters — 5 Reasons

```
1. STRING POOL (Memory optimization)
   "hello" + "hello" → same object in pool, not two copies
   Safe to share because no one can change it

2. THREAD SAFETY
   Immutable objects are inherently thread-safe
   Multiple threads can read the same String without synchronization

3. HASHCODE CACHING
   hashCode computed once, cached in 'hash' field
   Safe because value never changes
   Makes Strings fast as HashMap keys (hashCode not recomputed)

4. SECURITY
   Class names, DB URLs, file paths passed as String
   If String were mutable: validate "admin" → mutate to "attacker" before use
   Immutability prevents this attack

5. CLASSLOADER SAFETY
   ClassLoader uses String class names to load classes
   Mutable class names could cause security vulnerabilities
```

---

## 3. String Pool — intern() & Memory

### How String Pool Works

```java
// String literal → goes to pool (JVM checks pool first)
String s1 = "hello";   // "hello" added to pool
String s2 = "hello";   // "hello" found in pool → returns SAME reference
System.out.println(s1 == s2);   // true — same pool object

// new String() → ALWAYS creates new heap object (bypasses pool)
String s3 = new String("hello");   // new object on heap, "hello" also in pool
System.out.println(s1 == s3);      // false — different objects
System.out.println(s1.equals(s3)); // true  — same content

// intern() → moves to pool (or returns existing pool reference)
String s4 = s3.intern();
System.out.println(s1 == s4);      // true — both point to pool object
```

### Pool Memory Location History

```
Java 6 and before:
  String Pool in PermGen (Permanent Generation)
  Fixed size → prone to OutOfMemoryError: PermGen space
  intern() could cause memory issues in Java 6

Java 7+:
  String Pool moved to HEAP
  Benefits:
  ✓ No PermGen OutOfMemoryError for strings
  ✓ GC can collect unused pool strings
  ✓ intern() is now safe to use
  ✓ Pool size can grow dynamically

Java 8:
  PermGen completely removed → replaced by Metaspace
  String pool remains in Heap
```

### String Concatenation Internals

```java
// Compile-time constant folding:
String s = "hello" + " " + "world";
// Compiler converts to: String s = "hello world"  (at compile time)
// Only ONE string in pool

// Runtime concatenation (Java 8 and below):
String a = "hello";
String b = " world";
String c = a + b;
// Compiler converts to:
// String c = new StringBuilder(a).append(b).toString()
// Creates: StringBuilder object + new String object
// Old String objects become GC eligible

// Java 9+ — invokedynamic for string concatenation
// Uses StringConcatFactory — more efficient than StringBuilder in many cases
// Still avoid + in loops!

// NEVER do this:
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;   // creates 1000 intermediate String objects! O(n²) total work
}

// ALWAYS do this in loops:
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();   // only ONE final String object
```

---

## 4. String vs StringBuilder vs StringBuffer

### Internal Structure

```java
// StringBuilder internal state:
class StringBuilder {
    char[] value;      // mutable internal array
    int count;         // current length (< value.length)
    // default capacity = 16
    // grows by: (capacity * 2) + 2 when full
}

// StringBuilder example:
StringBuilder sb = new StringBuilder();        // capacity=16, count=0
sb.append("hello");                            // capacity=16, count=5
sb.append(" world!!!!!!!!!!!");                // triggers resize: (16*2)+2=34
```

### Comparison Table

| Feature | String | StringBuilder | StringBuffer |
|---|---|---|---|
| Mutability | Immutable | Mutable | Mutable |
| Thread-safe | ✅ Yes (immutable) | ❌ No | ✅ Yes (synchronized) |
| Performance | Slow for concat | ⚡ Fastest | Slower (lock overhead) |
| Storage | String Pool + Heap | Heap only | Heap only |
| hashCode | Cached | Not applicable | Not applicable |
| Use when | Fixed/constant value | Single-thread manipulation | Multi-thread string building |
| Java recommendation | Default | Preferred for loops | Only when truly multi-threaded |

### StringBuilder Methods

```java
StringBuilder sb = new StringBuilder("Hello");

sb.append(" World");        // "Hello World"
sb.insert(5, ",");          // "Hello, World"
sb.replace(7, 12, "Java");  // "Hello, Java"   ← [7..11] = "World" replaced
sb.delete(5, 7);            // "HelloJava"      ← removes ", " at indices 5-6
sb.reverse();               // "avaJolleH"
sb.charAt(0);               // 'a'
sb.setCharAt(0, 'J');       // "JvaJolleH"
sb.deleteCharAt(4);         // "JvaJlleH"       ← removes char at index 4
sb.indexOf("ole");          // index of "ole" in current buffer
sb.length();                // current length
sb.capacity();              // internal array capacity
sb.toString();              // convert to String
sb.substring(0, 4);         // "JvaJ"           ← first 4 chars of current buffer
sb.lastIndexOf("a");        // last index of 'a'
sb.ensureCapacity(100);     // ensure at least 100 capacity

// Chaining (returns 'this'):
String result = new StringBuilder()
    .append("Hello")
    .append(", ")
    .append("World")
    .append("!")
    .toString();
```

---

## 5. Important String Methods — All with Examples

```java
String s = "  Hello, World!  ";
//          0123456789...
// [0]=' ' [1]=' ' [2]='H' [3]='e' [4]='l' [5]='l' [6]='o'
// [7]=',' [8]=' ' [9]='W' [10]='o' [11]='r' [12]='l' [13]='d'
// [14]='!' [15]=' ' [16]=' '

// ── Length and Characters ──────────────────────────────────────
s.length();              // 17  (not 18 — count the chars above)
s.charAt(7);            // ','  (not 'W' — index 7 is the comma)
s.indexOf('o');         // 6   (first 'o' is in "Hello" at index 6)
s.lastIndexOf('o');     // 10  (second 'o' is in "World" at index 10)
s.indexOf("World");     // 9   (correct — 'W' is at index 9)
s.indexOf('o', 5);      // 6   (first 'o' at or after index 5 is still index 6)

// ── Substring ────────────────────────────────────────────────
s.substring(7);         // ", World!  " (from index 7 to end — index 7 is ',')
s.substring(7, 12);     // ", Wor"      (indices 7 inclusive to 12 exclusive)

// ── Search ───────────────────────────────────────────────────
s.contains("World");    // true
s.startsWith("  He");   // true
s.endsWith("!  ");      // true
s.matches(".*World.*"); // true (regex)

// ── Case ─────────────────────────────────────────────────────
s.toUpperCase();        // "  HELLO, WORLD!  "
s.toLowerCase();        // "  hello, world!  "
s.toUpperCase(Locale.ENGLISH);   // locale-safe (use in production)

// ── Trim and Strip ───────────────────────────────────────────
s.trim();               // "Hello, World!" (removes ASCII whitespace ≤ \u0020)
s.strip();              // "Hello, World!" (Java 11 — Unicode-aware, removes all whitespace)
s.stripLeading();       // "Hello, World!  " (removes only leading)
s.stripTrailing();      // "  Hello, World!" (removes only trailing)

// ── Replace ──────────────────────────────────────────────────
s.replace('l', 'L');              // "  HeLLo, WorLd!  "
s.replace("World", "Java");       // "  Hello, Java!  "
s.replaceAll("[aeiou]", "*");     // replace all vowels (regex)
s.replaceFirst("[aeiou]", "*");   // replace first vowel only

// ── Split ─────────────────────────────────────────────────────
"a,b,c".split(",");              // ["a", "b", "c"]
"a,b,c".split(",", 2);          // ["a", "b,c"] (limit=2)
"a  b  c".split("\\s+");        // ["a", "b", "c"] (split on whitespace)
"a.b.c".split("\\.");           // ["a", "b", "c"] (escape dot in regex!)

// ── Comparison ────────────────────────────────────────────────
"hello".equals("hello");              // true
"hello".equalsIgnoreCase("HELLO");    // true
"hello".compareTo("world");           // negative (h < w)
"hello".compareToIgnoreCase("HELLO"); // 0 (equal ignoring case)
"abc".contentEquals(new StringBuilder("abc")); // true — works with CharSequence

// ── Conversion ────────────────────────────────────────────────
String.valueOf(42);          // "42"
String.valueOf(3.14);        // "3.14"
String.valueOf(true);        // "true"
String.valueOf('c');         // "c"
Integer.parseInt("42");      // 42 (String → int)
Double.parseDouble("3.14");  // 3.14
"hello".toCharArray();       // ['h','e','l','l','o']
"hello".getBytes();          // byte[] (default charset)
"hello".getBytes(StandardCharsets.UTF_8);  // byte[] UTF-8 (production use)
new String(byteArray, StandardCharsets.UTF_8);  // byte[] → String

// ── Join (Java 8+) ────────────────────────────────────────────
String.join(", ", "a", "b", "c");              // "a, b, c"
String.join("-", List.of("x", "y", "z"));      // "x-y-z"

// ── Format ────────────────────────────────────────────────────
String.format("Name: %s, Age: %d, Salary: %.2f", "Kriti", 30, 85000.5);
// "Name: Kriti, Age: 30, Salary: 85000.50"

// ── Intern ────────────────────────────────────────────────────
new String("hello").intern();   // returns pool reference

// ── isEmpty vs isBlank ────────────────────────────────────────
"".isEmpty();         // true  (length == 0)
"  ".isEmpty();       // false (has chars, just spaces)
"".isBlank();         // true  (Java 11: empty or only whitespace)
"  ".isBlank();       // true  (Java 11)
```

---

## 6. Java 8+ String with Streams

```java
// chars() returns IntStream of char values (ASCII/Unicode code points)
"hello".chars()
       .forEach(c -> System.out.print((char)c + " "));   // h e l l o

// Count vowels
long vowelCount = "Hello World".toLowerCase().chars()
    .filter(c -> "aeiou".indexOf(c) >= 0)
    .count();   // 3

// Frequency of each character
Map<Character, Long> freq = "abracadabra".chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
// {a=5, b=2, r=2, c=1, d=1}

// Find duplicate characters (preserve first-seen order)
Set<Character> seen = new LinkedHashSet<>();
Set<Character> duplicates = "abracadabra".chars()
    .mapToObj(c -> (char) c)
    .filter(c -> !seen.add(c))
    .collect(Collectors.toCollection(LinkedHashSet::new));
// {a, b, r}

// First non-repeating character
Optional<Character> firstUnique = "abracadabra".chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting()))
    .entrySet().stream()
    .filter(e -> e.getValue() == 1)
    .map(Map.Entry::getKey)
    .findFirst();
// Optional[c]

// Check if all chars are digits
boolean allDigits = "12345".chars().allMatch(Character::isDigit);   // true

// Reverse a string using streams
String reversed = "hello".chars()
    .mapToObj(c -> String.valueOf((char) c))
    .reduce("", (a, b) -> b + a);   // "olleh"
// Better: new StringBuilder("hello").reverse().toString()

// Collect to string from stream
String upper = "hello world".chars()
    .mapToObj(c -> String.valueOf((char) c))
    .map(String::toUpperCase)
    .collect(Collectors.joining());   // "HELLO WORLD"

// Stream.of lines — split and process
Arrays.stream("the quick brown fox".split(" "))
    .filter(w -> w.length() > 3)
    .map(String::toUpperCase)
    .sorted()
    .forEach(System.out::println);
// BROWN
// QUICK
```

---

## 7. Java 11+ New String Methods

```java
// isBlank() — true if empty or only whitespace (Unicode-aware)
"".isBlank();          // true
"  \t\n".isBlank();   // true
"  a  ".isBlank();    // false

// strip() / stripLeading() / stripTrailing() — Unicode whitespace aware
"\u2000hello\u2000".strip();         // "hello" (strips Unicode spaces too)
"\u2000hello\u2000".trim();          // "\u2000hello\u2000" (trim only handles ASCII ≤ \u0020)

// repeat(int count) — Java 11
"ab".repeat(3);       // "ababab"
"-".repeat(20);       // "--------------------"
"0".repeat(5);        // "00000"

// lines() — splits by line terminators, returns Stream<String>
String text = "line1\nline2\nline3";
text.lines()
    .map(String::trim)
    .filter(l -> !l.isBlank())
    .forEach(System.out::println);
// line1
// line2
// line3

// lines() vs split("\\n"):
// lines() handles \n, \r, \r\n correctly and returns a lazy Stream
// split("\\n") returns String[], misses \r\n combinations

// indent(int n) — Java 12 — adds/removes leading spaces
"hello\nworld".indent(4);
//     hello
//     world

// stripIndent() — Java 15 — removes common leading whitespace (for text blocks)
// translateEscapes() — Java 15 — processes escape sequences in strings

// Text Block (Java 15+)
String json = """
        {
            "name": "Kriti",
            "role": "Senior Developer"
        }
        """;
// Automatically removes leading whitespace (indent = 8 spaces stripped)
// Trailing newline included from the closing """
```

---

## 8. Tricky Interview Questions — Deep Explanations

---

### 🔥 TRICK Q1. What is the output?

```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");
String s4 = s3.intern();

System.out.println(s1 == s2);   // ?
System.out.println(s1 == s3);   // ?
System.out.println(s1 == s4);   // ?
System.out.println(s3 == s4);   // ?
```

**Answer:**
```
s1 == s2   →  true   (both are pool references — same object)
s1 == s3   →  false  (s3 is heap object, bypasses pool)
s1 == s4   →  true   (intern() returns pool reference — same as s1)
s3 == s4   →  false  (s3 is heap, s4 is pool — different objects)
```

**Memory picture:**
```
Pool (Heap):   "hello" ←── s1, s2, s4 (all point here)
Heap:          String@X1{"hello"} ←── s3 (separate object)
```

---

### 🔥 TRICK Q2. What is the output?

```java
String s = "hello";
s.toUpperCase();
System.out.println(s);
```

**Answer:** `hello`

`toUpperCase()` does NOT modify `s` — it returns a **new String**.
`s` still points to the original `"hello"`.

```java
// Correct usage:
s = s.toUpperCase();        // reassign reference
System.out.println(s);      // "HELLO"
```

> 💡 **All String methods return a new String. The original is NEVER modified.**

---

### 🔥 TRICK Q3. Concatenation with null

```java
String s = null;
String result = s + " world";
System.out.println(result);

String s2 = null;
System.out.println("Hello" + s2);
```

**Answer:**
```
null world
Hellonull
```

Java converts `null` to the literal String `"null"` during concatenation.
No `NullPointerException` — this is handled by `String.valueOf(null)` = `"null"`.

```java
// But this DOES throw NPE:
String s = null;
s.toUpperCase();        // NullPointerException!
s.length();             // NullPointerException!
s.equals("hello");      // NullPointerException! → use "hello".equals(s)
```

---

### 🔥 TRICK Q4. What is the output?

```java
String s1 = "Hello";
String s2 = "Hello";
String s3 = "Hel" + "lo";       // compile-time constant
String s4 = "Hel";
String s5 = s4 + "lo";          // runtime concatenation

System.out.println(s1 == s2);   // ?
System.out.println(s1 == s3);   // ?
System.out.println(s1 == s5);   // ?
```

**Answer:**
```
s1 == s2   →  true   (both are pool literals — same reference)
s1 == s3   →  true   (compiler folds "Hel"+"lo" to "Hello" at compile time → pool)
s1 == s5   →  false  (s4 + "lo" is runtime concat → new heap object)
```

**Key rule:**
- `"a" + "b"` where both are compile-time constants → compiler folds → pool reference
- `variable + "b"` where variable is non-final → runtime → new heap object

```java
final String s4 = "Hel";   // if s4 is final:
String s5 = s4 + "lo";     // s4 is compile-time constant → folded → pool
// Now s1 == s5 → true!
```

---

### 🔥 TRICK Q5. String comparison in switch (Java 7+)

```java
String day = "MONDAY";
switch (day) {
    case "MONDAY": System.out.println("Start of week"); break;
    case "FRIDAY": System.out.println("End of week"); break;
    default: System.out.println("Middle of week");
}
```

**Output:** `Start of week`

```java
// How switch on String works internally (Java 7+):
// 1. Calls day.hashCode() to find the bucket
// 2. Then calls day.equals(caseValue) to verify (handles collisions)
// Equivalent to:
int h = day.hashCode();
if (h == "MONDAY".hashCode() && day.equals("MONDAY")) { ... }
else if (h == "FRIDAY".hashCode() && day.equals("FRIDAY")) { ... }
```

> ⚠️ `switch` on String is null-unsafe — passing `null` throws `NullPointerException`!

---

### 🔥 TRICK Q6. String.format vs + vs StringBuilder — performance

```java
// Which is fastest for building "Name: Kriti, Age: 30"?

// Option 1: + concatenation
String s1 = "Name: " + name + ", Age: " + age;
// Compiler: new StringBuilder().append("Name: ").append(name).append(", Age: ").append(age).toString()
// ONE StringBuilder, reasonably fast

// Option 2: String.format
String s2 = String.format("Name: %s, Age: %d", name, age);
// Parses format string → uses Formatter internally → slowest of the three
// ~5x slower than + for simple cases

// Option 3: StringBuilder
String s3 = new StringBuilder()
    .append("Name: ").append(name)
    .append(", Age: ").append(age)
    .toString();
// Fastest and most explicit

// Rule for interviews:
// + in a single expression → fine (compiler optimizes)
// + in a loop → always use StringBuilder (compiler can't optimize loop append)
// String.format → readable but slowest, use for complex formatting or logging
```

---

### 🔥 TRICK Q7. What is the output?

```java
String s = "abcabc";
System.out.println(s.indexOf('c'));       // ?
System.out.println(s.indexOf('c', 3));   // ?
System.out.println(s.lastIndexOf('c'));   // ?
System.out.println(s.indexOf('z'));       // ?
```

**Answer:**
```
2   ← first 'c' is at index 2
5   ← next 'c' after index 3 is at index 5
5   ← last 'c' is at index 5
-1  ← 'z' not found → always -1 (not null, not exception)
```

> 💡 `indexOf()` returns `-1` when not found — NEVER throws exception, NEVER returns null.

---

### 🔥 TRICK Q8. split() edge cases

```java
System.out.println("a,b,c".split(",").length);      // ?
System.out.println(",a,b,c,".split(",").length);    // ?
System.out.println("a,,b".split(",").length);       // ?
System.out.println("a,b,c,".split(",").length);     // ?
System.out.println("a,b,c,".split(",", -1).length); // ?
```

**Answer:**
```
"a,b,c".split(",")       → ["a","b","c"]         length=3
",a,b,c,".split(",")     → ["","a","b","c"]       length=4  (leading empty kept)
"a,,b".split(",")        → ["a","","b"]           length=3  (middle empty kept)
"a,b,c,".split(",")      → ["a","b","c"]          length=3  (trailing empties REMOVED by default!)
"a,b,c,".split(",", -1)  → ["a","b","c",""]       length=4  (negative limit keeps trailing empties)
```

**Key rule:**
- `split(regex)` = `split(regex, 0)` → removes **trailing** empty strings
- `split(regex, -1)` → keeps ALL tokens including trailing empties
- `split(regex, n)` where n > 0 → at most n parts, keeps trailing empties

---

### 🔥 TRICK Q9. substring() memory in Java 6 vs Java 7

```java
String huge = loadVeryLargeString();    // 1MB string
String tiny = huge.substring(0, 5);    // 5 characters only
huge = null;
```

**Java 6 behavior (memory leak!):**
- `substring()` returned a new String that shared the SAME backing `char[]` as `huge`
- `tiny` held a reference to the 1MB array via `offset` and `count` fields
- Setting `huge = null` didn't free the 1MB — `tiny` still referenced it!
- Fix: `String tiny = new String(huge.substring(0, 5));` — forces new array

**Java 7+ behavior (fixed):**
- `substring()` creates a completely new `char[]` (or `byte[]` in Java 9+)
- `huge = null` allows the original array to be GC'd
- No more memory leak — `tiny` is truly independent

---

### 🔥 TRICK Q10. String comparison trap

```java
String s1 = new String("hello");
String s2 = new String("hello");

System.out.println(s1.equals(s2));    // ?
System.out.println(s1.hashCode() == s2.hashCode());  // ?
System.out.println(s1 == s2);         // ?

Set<String> set = new HashSet<>();
set.add(s1);
set.add(s2);
System.out.println(set.size());        // ?
```

**Answer:**
```
true    ← equals() compares content
true    ← same content → same hashCode (String overrides both correctly)
false   ← different heap objects
1       ← HashSet sees them as equal (same hashCode + equals) → one entry!
```

---

## 9. String Coding Problems — MNC Favourites

### Problem 1 — Reverse words in a sentence

```java
// "Hello World Java" → "Java World Hello"

// Approach 1: split + reverse
String reversed = Arrays.stream("Hello World Java".split(" "))
    .reduce("", (a, b) -> b + (a.isEmpty() ? "" : " ") + a);
// "Java World Hello"

// Approach 2: StringBuilder
String sentence = "Hello World Java";
String[] words = sentence.split(" ");
StringBuilder sb = new StringBuilder();
for (int i = words.length - 1; i >= 0; i--) {
    sb.append(words[i]);
    if (i > 0) sb.append(" ");
}
// "Java World Hello"

// Approach 3: Collections
List<String> wordList = Arrays.asList("Hello World Java".split(" "));
Collections.reverse(wordList);
String.join(" ", wordList);
// "Java World Hello"
```

### Problem 2 — Check Anagram

```java
boolean isAnagram(String s1, String s2) {
    if (s1.length() != s2.length()) return false;

    int[] freq = new int[26];
    for (char c : s1.toLowerCase().toCharArray()) freq[c - 'a']++;
    for (char c : s2.toLowerCase().toCharArray()) freq[c - 'a']--;
    for (int f : freq) if (f != 0) return false;
    return true;
    // Time: O(n), Space: O(1) — 26 is constant
}

// Stream approach:
boolean isAnagramStream(String s1, String s2) {
    return Arrays.equals(
        s1.toLowerCase().chars().sorted().toArray(),
        s2.toLowerCase().chars().sorted().toArray()
    );
    // Time: O(n log n), Space: O(n)
}
```

### Problem 3 — Count character frequency

```java
// "aabbccd" → {a=2, b=2, c=2, d=1}

Map<Character, Long> freq = "aabbccd".chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

// Sorted by frequency descending:
freq.entrySet().stream()
    .sorted(Map.Entry.<Character,Long>comparingByValue().reversed())
    .forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));
```

### Problem 4 — Longest substring without repeating characters

```java
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastIndex = new HashMap<>();
    int max = 0, left = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (lastIndex.containsKey(c) && lastIndex.get(c) >= left) {
            left = lastIndex.get(c) + 1;   // jump left past duplicate
        }
        lastIndex.put(c, right);
        max = Math.max(max, right - left + 1);
    }
    return max;
    // Time: O(n), Space: O(min(n, charset))
}
```

### Problem 5 — First non-repeating character

```java
Character firstNonRepeating(String s) {
    // LinkedHashMap preserves insertion order
    Map<Character, Long> freq = s.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(
            c -> c,
            LinkedHashMap::new,   // maintains insertion order
            Collectors.counting()
        ));

    return freq.entrySet().stream()
        .filter(e -> e.getValue() == 1)
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
}
// For "abracadabra" → 'c'
```

### Problem 6 — Check palindrome (ignoring non-alphanumeric)

```java
boolean isPalindrome(String s) {
    // Remove non-alphanumeric, lowercase
    s = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    int left = 0, right = s.length() - 1;
    while (left < right) {
        if (s.charAt(left++) != s.charAt(right--)) return false;
    }
    return true;
}
// isPalindrome("A man, a plan, a canal: Panama") → true

// Stream version:
boolean isPalindromeStream(String s) {
    String clean = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    return IntStream.range(0, clean.length() / 2)
        .allMatch(i -> clean.charAt(i) == clean.charAt(clean.length() - 1 - i));
}
```

### Problem 7 — Group anagrams

```java
Map<String, List<String>> groupAnagrams(List<String> words) {
    return words.stream()
        .collect(Collectors.groupingBy(w -> {
            char[] ch = w.toLowerCase().toCharArray();
            Arrays.sort(ch);
            return new String(ch);   // sorted chars as key
        }));
}
// groupAnagrams(["eat","tea","tan","ate","nat","bat"])
// → {"aet": ["eat","tea","ate"], "ant": ["tan","nat"], "abt": ["bat"]}
```

### Problem 8 — Roman to Integer

```java
int romanToInt(String s) {
    Map<Character, Integer> map = Map.of(
        'I',1, 'V',5, 'X',10, 'L',50,
        'C',100, 'D',500, 'M',1000
    );
    int result = 0;
    for (int i = 0; i < s.length(); i++) {
        int curr = map.get(s.charAt(i));
        int next = i + 1 < s.length() ? map.get(s.charAt(i + 1)) : 0;
        result += (curr < next) ? -curr : curr;
    }
    return result;
}
// romanToInt("XIV") → 14
// romanToInt("MCMXCIV") → 1994
```

---

## 10. Interview Q&A — All FAQs with Answers

### ❓ Q1. Why is String final (cannot be subclassed)?

> "String is `final` to protect immutability. If someone could subclass String and override methods, they could create a mutable 'String' that breaks the contract. For example, `hashCode()` could return different values each call — breaking HashMap. Also, security depends on String being trustworthy — class names, DB credentials are passed as String. A mutable subclass could allow the value to change after validation. Making String `final` ensures these guarantees hold forever."

---

### ❓ Q2. What is the difference between `equals()` and `==` for Strings?

```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");

s1 == s2      // true  (same pool object)
s1 == s3      // false (different heap objects)
s1.equals(s3) // true  (same content)
```

> "`==` compares **object references** (memory addresses). `equals()` for String compares **character content**. Always use `equals()` for String comparison in application code. Use `==` only when you explicitly want to check if two variables point to the exact same object, which is almost never needed for Strings."

---

### ❓ Q3. How does String.hashCode() work? Why is 31 used?

```java
// Formula:
// hash = s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]

// Java implementation:
public int hashCode() {
    int h = hash;
    if (h == 0 && !hashIsZero) {
        h = isLatin1()
            ? StringLatin1.hashCode(value)
            : StringUTF16.hashCode(value);
        if (h == 0) hashIsZero = true;
        else hash = h;   // cached!
    }
    return h;
}
```

> "31 is chosen because: (1) it's an **odd prime** — reduces hash collisions; (2) `31 * i == (i << 5) - i` — JVM can use bit-shift and subtract instead of multiplication (faster); (3) empirically gives excellent distribution for English text. The hashCode is **computed once and cached** in the `hash` field — safe because String is immutable."

---

### ❓ Q4. What is the difference between `trim()` and `strip()`?

```java
String s = "\u2000hello\u2000";   // Unicode space \u2000 (En Quad)

s.trim();          // "\u2000hello\u2000" — NOT trimmed! trim() only removes ≤ \u0020
s.strip();         // "hello" — strips Unicode whitespace (Java 11)
s.stripLeading();  // "hello\u2000" — only leading
s.stripTrailing(); // "\u2000hello" — only trailing
```

> "`trim()` removes characters where `char value ≤ '\u0020'` (ASCII space and control chars). `strip()` (Java 11) uses `Character.isWhitespace()` which handles all Unicode whitespace characters. In modern Java, always use `strip()` — it's Unicode-correct."

---

### ❓ Q5. When should you use `String.intern()`?

> "Use `intern()` when you have many duplicate strings created at runtime and want to save memory by sharing them from the pool. Example: parsing a CSV with millions of rows where column names repeat. Without intern: millions of separate String objects. With intern: one pooled String per unique value. Since Java 7, the pool is on the heap (GC-able), so intern() is safe. BUT in Java 9+ with compact strings, the memory savings are less dramatic. Always benchmark before using intern() as a 'optimization'."

---

### ❓ Q6. What is a String text block (Java 15+)?

```java
// Before (ugly escape sequences):
String json = "{\n" +
              "    \"name\": \"Kriti\",\n" +
              "    \"age\": 30\n" +
              "}";

// With text block (Java 15+):
String json = """
        {
            "name": "Kriti",
            "age": 30
        }
        """;
// No escape sequences needed!
// Leading whitespace removed based on closing """ position
// Trailing \n included
```

---

### ❓ Q7. How to split on special characters?

```java
// Special regex characters: . * + ? ^ $ { } [ ] | ( ) \
// Must be escaped with \\ in regex!

"a.b.c".split("\\.");          // ["a","b","c"]  ← escape dot
"a|b|c".split("\\|");          // ["a","b","c"]  ← escape pipe
"a+b+c".split("\\+");          // ["a","b","c"]  ← escape plus
"a$b$c".split("\\$");          // ["a","b","c"]  ← escape dollar

// Or use Pattern.quote() — escapes for you:
"a.b.c".split(Pattern.quote("."));   // ["a","b","c"]  ← safer
```

---

### ❓ Q8. What happens when you concatenate thousands of strings in a loop?

```java
// ❌ O(n²) time — creates n intermediate String objects
String result = "";
for (int i = 0; i < 10000; i++) {
    result += i;   // each += creates a new String!
}

// ✅ O(n) time — single StringBuilder, one final toString()
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);
}
String result = sb.toString();

// Why is += O(n²)?
// Iteration 1: copy 0 chars + append "0"      → "0"       (1 char)
// Iteration 2: copy 1 char  + append "1"      → "01"      (2 chars)
// Iteration 3: copy 2 chars + append "2"      → "012"     (3 chars)
// ...
// Total chars copied: 0+1+2+...+(n-1) = n*(n-1)/2 = O(n²)
```

---

## 11. Quick Reference Cheat Sheet

### String Immutability in One Picture

```
String s = "hello";    s ──────────────────► Pool: "hello"
s.toUpperCase();                              Pool: "HELLO" (new!)
                        s still points to ──► Pool: "hello"

s = s.toUpperCase();   s ──────────────────► Pool: "HELLO"
                                              Pool: "hello" (now unreferenced, GC eligible)
```

### == vs equals vs compareTo

| Method | What it compares | Returns | Use for |
|---|---|---|---|
| `==` | References (memory address) | boolean | Never for Strings in logic |
| `equals()` | Content (case-sensitive) | boolean | Equality check |
| `equalsIgnoreCase()` | Content (case-insensitive) | boolean | Case-insensitive equality |
| `compareTo()` | Lexicographic order | int (<0, 0, >0) | Sorting |
| `compareToIgnoreCase()` | Lexicographic, case-insensitive | int | Case-insensitive sorting |
| `contentEquals()` | Content vs any CharSequence | boolean | Compare with StringBuilder |

### String Methods — One-liner Summary

```
length()         → count of characters
charAt(i)        → char at index i
indexOf(c/s)     → first position (-1 if not found)
lastIndexOf(c/s) → last position (-1 if not found)
substring(s,e)   → new String from [s, e)
contains(cs)     → true if CharSequence found
startsWith(s)    → true if begins with s
endsWith(s)      → true if ends with s
replace(old,new) → new String with all replacements
replaceAll(re,r) → regex replacement
split(regex)     → String[] (trailing empties removed)
split(regex,-1)  → String[] (all tokens, trailing kept)
trim()           → remove ASCII whitespace both ends
strip()          → remove Unicode whitespace both ends (Java 11)
toUpperCase()    → new uppercase String
toLowerCase()    → new lowercase String
equals(o)        → content equality (case-sensitive)
equalsIgnoreCase → content equality (case-insensitive)
compareTo(s)     → lexicographic comparison
intern()         → pool reference
isEmpty()        → length == 0
isBlank()        → empty or only whitespace (Java 11)
repeat(n)        → repeat n times (Java 11)
lines()          → Stream<String> of lines (Java 11)
chars()          → IntStream of char values
toCharArray()    → char[]
getBytes()       → byte[] (use with Charset!)
String.valueOf() → convert any type to String
String.format()  → formatted String (uses printf syntax)
String.join()    → join with delimiter (Java 8)
```

### Common Gotchas Summary

```
❌ s.toUpperCase() without reassignment → s unchanged!
❌ s == "hello"                          → use s.equals("hello")
❌ null.equals("hello")                  → NullPointerException
✅ "hello".equals(s)                     → null-safe (returns false if s==null)
❌ + in loop                             → O(n²), use StringBuilder
❌ Arrays.asList().add()                 → UnsupportedOperationException
❌ "a.b".split(".")                      → [] (dot = any char in regex!)
✅ "a.b".split("\\.")                    → ["a", "b"]
❌ split(",") on "a,b,c,"               → trailing empties removed!
✅ split(",", -1) on "a,b,c,"           → ["a","b","c",""] kept
```

---

*Prepared from Claude AI session | Kriti Singh | 8.5 YOE Java Developer*
*Topics: String Immutability · Pool · StringBuilder · All Methods · Streams · Java 11 · Tricky Q&A · Coding Problems*
