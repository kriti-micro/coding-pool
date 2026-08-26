# Java Exception Handling — Interview Prep (8.5 Years Backend Experience)

> Target level: Senior Java Backend Engineer / Lead. Expect deep-dives on **checked vs unchecked design philosophy**, **try-with-resources internals**, **exception chaining**, **performance cost of exceptions**, and **production-grade error-handling architecture** (not just `try/catch` syntax).

---

## 1. `Throwable` Hierarchy — Full Class/Interface Tree

```
java.lang.Object
      |
java.lang.Throwable  (implements Serializable)
      |
      |----------------------------------------------------
      |                                                     |
   java.lang.Exception                              java.lang.Error
      |                                                     |
      |------------------------------------          -------------------------------
      |                                    |          |          |          |       |
  Checked Exceptions               RuntimeException  VirtualMachineError  AssertionError  LinkageError  ...
  (direct/indirect subclasses            |            |                                       |
   of Exception, NOT of                  |     ------------------------              --------------------
   RuntimeException)                     |     |                        |             |                  |
                                          |  OutOfMemoryError   StackOverflowError  NoClassDefFoundError  ExceptionInInitializerError
                                          |
    ---------------------------------------------------------------------------------
    |                |               |                  |                |          |
NullPointerException ArithmeticException  ArrayIndexOutOfBoundsException  ClassCastException  IllegalArgumentException  IllegalStateException
                                              (extends IndexOutOfBoundsException)                     |                       |
                                                                                              NumberFormatException   ConcurrentModificationException
    |
UnsupportedOperationException   NoSuchElementException   NegativeArraySizeException
```

**Checked Exception subtree (extends `Exception` directly, does NOT extend `RuntimeException`):**

```
Exception
   |-- IOException
   |       |-- FileNotFoundException
   |       |-- EOFException
   |       |-- SocketException
   |       |-- UnknownHostException
   |-- SQLException
   |-- ClassNotFoundException
   |-- CloneNotSupportedException
   |-- InterruptedException
   |-- ParseException
   |-- TimeoutException (java.util.concurrent)
   |-- ReflectiveOperationException
           |-- NoSuchMethodException
           |-- NoSuchFieldException
           |-- InstantiationException
           |-- InvocationTargetException
```

**`RuntimeException` subtree (Unchecked):**

```
RuntimeException
   |-- NullPointerException
   |-- ArithmeticException                    (e.g., divide by zero on int)
   |-- ArrayStoreException
   |-- ClassCastException
   |-- IllegalArgumentException
   |       |-- NumberFormatException
   |-- IllegalStateException
   |       |-- ConcurrentModificationException
   |-- IndexOutOfBoundsException
   |       |-- ArrayIndexOutOfBoundsException
   |       |-- StringIndexOutOfBoundsException
   |-- UnsupportedOperationException
   |-- NoSuchElementException (extends RuntimeException, in java.util package, NOT IndexOutOfBounds)
   |-- NegativeArraySizeException
   |-- EmptyStackException
```

**`Error` subtree (never meant to be caught/handled by application code):**

```
Error
   |-- VirtualMachineError
   |       |-- OutOfMemoryError
   |       |-- StackOverflowError
   |       |-- InternalError
   |-- LinkageError
   |       |-- NoClassDefFoundError
   |       |-- ExceptionInInitializerError
   |       |-- UnsatisfiedLinkError
   |-- AssertionError
   |-- ThreadDeath (deprecated)
```

**Key structural facts to state confidently:**
- `Throwable` implements `Serializable` — because exceptions frequently cross JVM boundaries (RMI, distributed systems, serialized logs).
- `Exception` and `Error` are siblings, both direct children of `Throwable` — a very common confusion point ("is `Error` a type of `Exception`?" → **No.**).
- `RuntimeException` extends `Exception` (not a sibling of it) — meaning **all unchecked exceptions are technically still `Exception`s**, just exempted from the compiler's checked-exception enforcement.

---

## 2. Checked vs Unchecked — Design Philosophy (the #1 conceptual question)

### Q1. What's the actual difference between checked and unchecked exceptions — and why does this distinction exist?
- **Checked exceptions** (`Exception` and subclasses, excluding `RuntimeException` and its subclasses): the **compiler forces** you to either `catch` them or declare them via `throws` in the method signature. Represent conditions a **well-written application should anticipate and recover from** (e.g., `IOException` — a file might legitimately not exist; `SQLException` — the DB connection might legitimately drop).
- **Unchecked exceptions** (`RuntimeException` and subclasses, plus all `Error`s): **no compiler enforcement**. Represent **programming bugs** (`NullPointerException`, `ArrayIndexOutOfBoundsException`, `ClassCastException`) or **unrecoverable conditions** the caller typically can't meaningfully react to at that call site.
- **Design rationale (per Joshua Bloch, *Effective Java*):** Use checked exceptions for **recoverable conditions the caller can reasonably act on**; use unchecked exceptions for **programming errors**. Overusing checked exceptions leads to boilerplate `try/catch` pyramids and "swallowed exception" anti-patterns.

### Q2. This is a hotly debated Java design choice — what are the criticisms of checked exceptions, and how has the ecosystem responded?
- Checked exceptions **don't compose well with functional interfaces / lambdas / Streams** — `Function<T,R>.apply()` cannot declare `throws IOException`, forcing awkward wrapping (see Q20).
- They tend to **leak implementation details** into API contracts (e.g., a method's `throws SQLException` ties callers to the fact that a DB is involved underneath, hurting abstraction/layering).
- They **propagate poorly** across layered architectures — every intermediate layer must either catch, wrap, or re-declare `throws`, leading to "throws-clause pollution" up the call stack.
- **Industry response:** Most modern frameworks favor **unchecked exceptions almost universally** — Spring wraps virtually all checked exceptions (JDBC's `SQLException`, etc.) into its own unchecked `DataAccessException` hierarchy; Hibernate does similarly. Kotlin **removed checked exceptions entirely** (no compiler distinction) as a direct reaction to Java's design. This is an excellent discussion point to raise proactively — shows architectural maturity.

### Q3. Is `Error` checked or unchecked? Should you ever catch `Error`?
`Error` is unchecked (no compiler enforcement), but it's a **sibling** of `Exception`, not a subtype. You **generally should not catch `Error`** — it represents conditions like `OutOfMemoryError` or `StackOverflowError` that indicate the JVM itself is in a compromised state; attempting to "recover" and continue is usually unsafe (e.g., after an `OutOfMemoryError`, the heap may be in an inconsistent state for other threads too). Narrow exceptions exist (e.g., some frameworks catch `StackOverflowError` specifically to abort a single recursive request without crashing the whole server) but this is an advanced, deliberate exception to the rule — never blanket-catch `Throwable` or `Error` in normal business logic.

---

## 3. Core Syntax & Semantics — Tricky Behavior Questions

### Q4. `throw` vs `throws` — commonly confused, know both cold.
- `throw` — a **statement**, used inside a method body to actually **raise** an exception instance: `throw new IllegalArgumentException("bad input");`
- `throws` — a **declaration** in the method signature, informing callers this method **might** propagate a checked exception: `void readFile() throws IOException { ... }`

### Q5. Classic trap: what does this print, and why?
```java
public static int test() {
    try {
        return 1;
    } finally {
        return 2;
    }
}
// test() returns 2
```
**The `finally` block's `return` silently swallows/overrides the `try` block's `return`.** This is a well-known and genuinely dangerous anti-pattern — a `return` (or `throw`, or `break`/`continue`) inside `finally` **discards** any exception or return value pending from the `try`/`catch` block. This can silently swallow real exceptions:
```java
public static int risky() {
    try {
        throw new RuntimeException("real error");
    } finally {
        return 99;  // The RuntimeException is COMPLETELY SUPPRESSED — never propagates!
    }
}
```
**Rule to state explicitly:** Never put `return`, `break`, `continue`, or `throw` inside a `finally` block — it's flagged by static analysis tools (SonarQube, SpotBugs) as a serious code smell precisely because of this silent-swallowing behavior.

### Q6. Does `finally` **always** execute? What are the exceptions to this rule?
`finally` runs even if the `try`/`catch` returns, breaks, continues, or throws — **except**:
1. `System.exit()` is called inside `try`/`catch` → JVM halts immediately, `finally` is **skipped**.
2. The JVM crashes / is forcibly killed (power loss, `kill -9`, `Runtime.halt()`).
3. An infinite loop or a thread `Error`/deadlock inside `try` that never returns control.
4. The thread executing it is forcibly terminated by another thread (`Thread.stop()` — deprecated/dangerous, rarely relevant now).

### Q7. What is a "suppressed exception," and how does try-with-resources relate to it?
```java
try (AutoCloseable r = () -> { throw new RuntimeException("close failed"); }) {
    throw new RuntimeException("try-block failed");
}
```
- The exception from the **`try` block body** ("try-block failed") is the **primary** exception that propagates out.
- The exception thrown while **closing** the resource ("close failed") is **not lost** — it's attached to the primary exception as a **suppressed exception**, retrievable via `primaryException.getSuppressed()` (returns `Throwable[]`).
- **Contrast with the old JDK 6-style manual `try/finally` close pattern**, where closing in `finally` would **completely overwrite/discard** the original try-block exception (a real, historically common bug class) — try-with-resources (Java 7+) fixes this by preserving both, with the close-exception demoted to "suppressed" rather than replacing the original.

### Q8. How does try-with-resources actually work under the hood? What interface must a resource implement?
The resource must implement `AutoCloseable` (or its stricter checked-exception-free sibling, `Closeable`, from `java.io`, used by IO classes). The compiler desugars:
```java
try (Resource r = new Resource()) {
    use(r);
}
```
into (conceptually) the equivalent of:
```java
Resource r = new Resource();
Throwable primaryExc = null;
try {
    use(r);
} catch (Throwable t) {
    primaryExc = t;
    throw t;
} finally {
    if (r != null) {
        if (primaryExc != null) {
            try { r.close(); }
            catch (Throwable closeExc) { primaryExc.addSuppressed(closeExc); }
        } else {
            r.close();
        }
    }
}
```
**Key detail:** resources are closed in the **reverse order** of declaration (last declared, first closed) — like a stack, mirroring RAII-style resource management conventions from other languages.

### Q9. Java 9 enhancement to try-with-resources — what changed?
Pre-Java-9, the resource **had to be declared inside** the `try(...)` parentheses. Java 9 allows using an **effectively final variable declared outside** the try block directly:
```java
BufferedReader br = new BufferedReader(new FileReader("file.txt"));
try (br) {   // Java 9+ — no need to redeclare, just reference an effectively-final variable
    return br.readLine();
}
```

### Q10. Multi-catch (`catch (IOException | SQLException e)`) — what's the restriction, and why?
You can catch multiple **unrelated** exception types in one block (Java 7+) to avoid duplicated catch-body code. **Restriction:** the caught variable `e` is implicitly treated as the **most specific common supertype**, and is effectively `final` — you **cannot reassign** it inside the catch block. Also, you **cannot combine two exception types where one is a subclass of the other** (e.g., `catch (IOException | FileNotFoundException e)` is a **compile error**, since it's redundant — just catch the supertype `IOException` alone).

---

## 4. Exception Chaining & Custom Exceptions

### Q11. What is exception chaining/wrapping, and why is it important?
```java
try {
    dbCall();
} catch (SQLException e) {
    throw new ServiceException("Failed to process order", e);  // 'e' passed as the CAUSE
}
```
Preserves the **original root-cause exception** (via the `cause` field, set through the `Throwable(String, Throwable)` constructor or `initCause()`) while allowing you to translate a low-level, layer-specific exception (e.g., `SQLException`) into a higher-level, more meaningful one for the calling layer (e.g., a custom `ServiceException`). **Without chaining, you lose the original stack trace entirely** — a critical production-debuggability requirement. `getCause()` retrieves the chain; stack traces printed via `printStackTrace()` automatically show `"Caused by:"` sections recursively.

### Q12. Best practices for designing custom exceptions (a system-design-flavored question at this level).
1. Extend `RuntimeException` in most modern service codebases (unless you have a genuinely recoverable, caller-actionable condition — see Q2's design debate).
2. Always provide constructors that accept a **message** and a **cause** (`Throwable`) — never discard the original exception.
3. Keep custom exceptions **immutable** and avoid embedding mutable state or heavy objects (exceptions can be serialized/logged; avoid leaking sensitive data like passwords into exception messages).
4. Design a **small, meaningful hierarchy** matching your domain (e.g., `OrderNotFoundException extends DomainException extends RuntimeException`) rather than one flat generic `MyAppException` for everything — enables callers to catch at the right granularity.
5. Avoid exceptions for **expected/frequent control flow** (see Q17 — performance cost) — e.g., don't use a `NotFoundException` for a routine "check if exists" lookup in a hot loop; prefer an `Optional<T>` or a boolean-returning method instead.

### Q13. What happens if you override `fillInStackTrace()` or construct exceptions without stack traces — and why would you deliberately do this?
`fillInStackTrace()` is called by `Throwable`'s constructor and is actually the **most expensive part of exception creation** (it walks the entire call stack). For extremely hot-path exceptions used purely as **control-flow signals** (a legitimate but advanced pattern — e.g., some high-performance libraries throw exceptions to break out of deep recursion/loops), you can override the constructor to skip stack trace capture:
```java
class FastException extends RuntimeException {
    public FastException(String msg) {
        super(msg, null, false, false); // writableStackTrace = false
    }
}
```
The 4-arg `Throwable` constructor (Java 7+) with `enableSuppression` and `writableStackTrace` flags exists **specifically for this performance optimization** — a great "do you know the JDK internals beyond basics" question.

---

## 5. Exceptions & Modern Java (Streams, Lambdas, Concurrency)

### Q14. Why can't you throw a checked exception directly inside a `Stream`/lambda, and what are the common workarounds?
Functional interfaces like `Function<T,R>`, `Consumer<T>`, `Predicate<T>` declare `apply()`/`accept()`/`test()` with **no `throws` clause** — Java lambdas implementing these interfaces therefore cannot throw checked exceptions without a compile error, because a lambda must conform exactly to its functional interface's method signature (including its exception specification).
**Common workarounds:**
1. Wrap the checked exception in an unchecked one inline: `.map(x -> { try { return risky(x); } catch (IOException e) { throw new UncheckedIOException(e); } })`.
2. Use JDK-provided unchecked wrapper types where available: `UncheckedIOException` (wraps `IOException` specifically).
3. Define your own custom functional interface that **does** declare `throws Exception`, then adapt/wrap it at the call site.
4. Use a small utility "sneaky-throw" trick (type-erasure hack to throw a checked exception without declaring it) — controversial, generally discouraged in production code, but worth knowing exists (Lombok's `@SneakyThrows` implements exactly this).

### Q15. How does exception handling differ in `CompletableFuture`/async code vs synchronous code?
Exceptions thrown inside a `CompletableFuture`'s task are **not thrown directly to the calling thread** — they're captured and stored, surfacing only when you call `.get()` (wrapped in `ExecutionException`), `.join()` (wrapped in unchecked `CompletionException`), or are explicitly handled via `.exceptionally()`, `.handle()`, or `.whenComplete()`. **Common trap:** forgetting to call `.join()`/`.get()` or attach an exception handler means a failed async task can **fail silently** — no stack trace ever printed, no error ever surfaced, unless you explicitly inspect it. This is a very real production bug pattern worth mentioning proactively.

### Q16. What happens to an uncaught exception thrown inside a `Thread`'s `run()` method (not the main thread)?
It does **not** crash the JVM (unlike an uncaught exception on `main`) and does **not** propagate to any other thread automatically. It's handled by the thread's **`UncaughtExceptionHandler`** (default implementation just prints the stack trace to `System.err` and the thread simply dies). In production systems, you should set a **custom `Thread.setDefaultUncaughtExceptionHandler()`** (or configure it on your `ExecutorService`'s thread factory) to properly log/alert on these — otherwise, background worker thread failures can go completely unnoticed.

---

## 6. Performance & Best Practices

### Q17. "Exceptions are expensive" — is this actually true, and specifically why?
The expense is **not in the `throw`/`catch` control-flow transfer itself** (modern JVMs handle this reasonably efficiently) — it's overwhelmingly in **`fillInStackTrace()`** (Q13), which walks and records the entire call stack at the point of exception **construction**. This cost scales with call-stack depth and happens **even if the exception is immediately caught and ignored**. Practical implication: **never use exceptions for routine/expected control flow in hot paths** (e.g., don't throw-and-catch inside a tight loop to signal "not found" — return `null`/`Optional`/a sentinel value instead). This is a classic senior-level "do you actually understand JVM cost model" question.

### Q18. Why is catching `Exception` (or worse, `Throwable`) broadly considered a bad practice, and when (rarely) is it acceptable?
Broad catches (`catch (Exception e) {}`, especially empty ones — the infamous "exception swallowing" anti-pattern) **hide real bugs**, mask `NullPointerException`s and other programming errors that should surface and be fixed, and make debugging production incidents extremely painful (no visibility into what actually failed). **Acceptable narrow use cases:** a top-level request handler / servlet filter / message-consumer loop that must **never crash the whole process** due to one bad request — but even then, it must **log the full exception with stack trace** (never silently swallow) and typically should still avoid catching `Error` (see Q3).

### Q19. What's the difference between `printStackTrace()` and proper logging (e.g., via SLF4J), and why does it matter in production?
`printStackTrace()` writes directly to `System.err`, which in most production deployments is **not captured by structured log aggregation** (ELK, Splunk, CloudWatch, etc.) the way `logger.error("msg", exception)` output is — leading to lost diagnostic information in real incidents. Also, `printStackTrace()` offers no log-level control, no structured/JSON output, and no centralized correlation (trace IDs, MDC context). **Always use a logging framework** with the exception object passed as the last argument (`log.error("Failed to process order {}", orderId, e)`) so the full stack trace is captured in the structured log output.

---

## 7. Tricky / Rapid-Fire Questions

**Q20. Can a `catch` block catch `Error`s if you write `catch (Exception e)`?**
No — `Error` is a sibling of `Exception`, not a subtype, so `catch (Exception e)` will **not** catch an `OutOfMemoryError` or `StackOverflowError`. Only `catch (Throwable t)` catches everything (and is almost always inadvisable — see Q3/Q18).

**Q21. Is it legal to have a `try` block with no `catch` and no `finally`? What about `try` with only `finally`?**
`try` **must** have at least one `catch` **or** a `finally` (or be a try-with-resources, which implicitly supplies close-handling). `try { ... } finally { ... }` (no catch at all) is perfectly legal and common — used purely for guaranteed cleanup, letting exceptions propagate untouched.

**Q22. Can you catch a `checked` exception type that a `try` block's code cannot actually throw? What does the compiler say?**
No — the compiler performs **reachability analysis on checked exceptions specifically**: if the `try` block's contained code (including all called methods' `throws` declarations) cannot possibly throw a given checked exception type, `catch`ing that specific checked type is a **compile error** ("exception X is never thrown in the corresponding try block"). **This restriction does NOT apply to unchecked exceptions or `Exception`/`Throwable`** — you can always write `catch (RuntimeException e)` or `catch (Exception e)` even if nothing in the try block obviously throws one, since the compiler can't prove the negative for unchecked types (any line could theoretically throw an unchecked exception like `NullPointerException`).

**Q23. What's the difference between `NoSuchElementException` and `NoSuchMethodException`/`NoSuchFieldException`? Trick: are they related in the hierarchy?**
No relation despite similar naming! `NoSuchElementException` (`java.util` package) extends `RuntimeException` directly — used by `Iterator.next()`, `Optional.get()` when empty, etc. `NoSuchMethodException`/`NoSuchFieldException` (`java.lang` package) extend the **checked** `ReflectiveOperationException` — used by reflection APIs. A great "gotcha" testing whether candidates assume naming similarity implies hierarchy relationship.

**Q24. Does overriding a method let you widen or narrow the checked exceptions it declares?**
You can only **narrow or eliminate** checked exceptions in an override — **never widen or add new checked exception types** not declared (or not a subtype of ones declared) in the parent/interface method signature. This is enforced by the compiler to preserve the **Liskov Substitution Principle** — callers relying on the parent type's `throws` contract must not be surprised by new checked exceptions from a subtype. (Unchecked exceptions are exempt from this restriction entirely — any override can throw any `RuntimeException` freely.)

**Q25. What's the actual object returned by `e.getMessage()` vs `e.toString()` vs `e.getLocalizedMessage()`?**
- `getMessage()` → the raw message string passed to the constructor (or `null` if none given).
- `toString()` → `ClassName: message` (e.g., `"java.lang.NullPointerException: Cannot invoke..."`), the format used by default uncaught-exception printing.
- `getLocalizedMessage()` → by default, identical to `getMessage()`, but designed to be **overridden by subclasses** to provide locale-specific messages (rarely actually overridden in practice, but a legitimate hook for i18n-aware exception messages).

**Q26. Helpful modern JDK feature: "Helpful NullPointerExceptions" (JEP 358, Java 14+) — what changed?**
Pre-Java-14, an NPE on a chained call like `a.getB().getC().getD()` gave you almost no information about **which** part of the chain was null. Java 14+ (enabled by default since Java 15) generates a **precise, human-readable message** identifying exactly which variable/method-call-result was null (e.g., `"Cannot invoke \"C.getD()\" because the return value of \"B.getC()\" is null"`) by analyzing bytecode-level debug info — a huge real-world debugging quality-of-life improvement worth mentioning as a modern-Java-awareness signal.

**Q27. What happens if an exception is thrown from a `static` initializer block?**
It gets wrapped in an `ExceptionInInitializerError` (a subtype of `LinkageError`, itself an `Error`) — and critically, the class is marked as **erroneous and will fail to initialize on every subsequent attempt to use it**, throwing `NoClassDefFoundError` on later references, even if the underlying cause was a transient/fixable condition. This is a genuinely nasty, hard-to-diagnose production issue class — static initializer exceptions should be avoided or very carefully guarded.

**Q28. Can a `finally` block's exception "hide" an exception thrown in `catch`, similar to Q5's try/finally issue?**
Yes — the exact same override behavior applies to **any** abrupt completion in `finally`, regardless of whether it's overriding a `try` block's exception, a `catch` block's exception, or a normal return value. The rule is simply: **whatever `finally` does abruptly (return/throw/break/continue) always wins**, discarding whatever was pending before it, unless you're using try-with-resources' suppressed-exception mechanism (Q7), which is specifically designed to preserve both rather than discard.

**Q29. Difference between `Exception(String message)` and `Exception(String message, Throwable cause)` — and what does `Exception(Throwable cause)` (single-arg) do to the message?**
The single-`Throwable`-arg constructor sets the message to `cause == null ? null : cause.toString()` — i.e., it auto-derives a message from the cause's own `toString()` (class name + its message) rather than leaving it null. Useful shorthand when you're purely wrapping without adding new context, though providing an explicit descriptive message is almost always better practice for production debuggability.

**Q30. Is `assert` related to exception handling? What does `AssertionError` signify, and why is `assert` rarely used in production code?**
`assert condition : "message";` throws `AssertionError` (unchecked, extends `Error` — meaning it's **not meant to be caught/recovered from**) if the condition is false — but **assertions are disabled by default at runtime** unless the JVM is started with `-ea` (enable assertions). Because production deployments almost never enable `-ea` (assertion checks have a runtime cost and are considered a debug/test-time tool, not a production validation mechanism), **never use `assert` for business-logic validation or input validation** — use explicit `if`-checks throwing proper unchecked exceptions (`IllegalArgumentException`, custom validation exceptions) instead, since those always execute regardless of JVM flags.

---

## 8. Complexity/Behavior Cheat-Sheet

| Scenario | Behavior |
|---|---|
| Uncaught exception on `main` thread | Prints stack trace to `System.err`, JVM exits with non-zero status |
| Uncaught exception on a non-main `Thread` | Handled by `UncaughtExceptionHandler`; does NOT crash JVM or propagate to other threads |
| `finally` after `System.exit()` in try | **Skipped entirely** |
| `return` inside `finally` | **Overrides/discards** any pending return value or exception from `try`/`catch` |
| try-with-resources close-exception | Attached as **suppressed**, not discarded (Java 7+) |
| Checked exception not declared/caught | **Compile error** |
| Unchecked exception not declared/caught | Compiles fine; propagates at runtime up the call stack |
| Exception in `static` initializer | Wrapped in `ExceptionInInitializerError`; class permanently fails to load thereafter |
| `catch` block ordering (subclass before superclass) | **Compile error** if superclass catch precedes subclass catch (unreachable code) |

---

## 9. Scenario / System-Design-Flavored Questions

**Q31.** *"Design a global exception-handling strategy for a Spring Boot REST API serving external clients. What layers/mechanisms would you use?"*
> Expected: `@ControllerAdvice` + `@ExceptionHandler` for centralized translation of exceptions → proper HTTP status codes + structured error response bodies (avoid leaking internal stack traces/class names to external clients — a security concern). Discuss a clean exception hierarchy (e.g., `ResourceNotFoundException` → 404, `ValidationException` → 400, generic fallback → 500 with a **generic** message but **full internal logging** of the real cause). Mention correlation/trace IDs in error responses for support debugging without exposing internals.

**Q32.** *"You inherited a legacy codebase full of empty `catch (Exception e) {}` blocks causing silent failures in production. How do you approach fixing this safely without introducing regressions?"*
> Discuss: don't blanket-remove all catches at once (some may be intentionally suppressing truly benign/expected conditions); triage by adding logging first (non-breaking change) to observe actual frequency/nature of swallowed exceptions in production before deciding whether to fix root cause, rethrow, or genuinely leave a documented no-op with a clear comment explaining why it's safe to ignore. Good opportunity to discuss incremental, observability-first refactoring discipline.

---

### How to use this file
- Memorize the **hierarchy tree in Section 1** cold — being able to place any exception (`NumberFormatException`, `ConcurrentModificationException`, etc.) correctly in the tree on a whiteboard is a very common ask.
- Section 3 (Q5, Q6, Q7) — the `finally`/try-with-resources tricky behavior — is the highest-yield live-coding trap area; review immediately before the interview.
- Be ready to discuss **Q2's checked-vs-unchecked design debate** as an open-ended architecture conversation — senior-level interviewers often use it to gauge whether you can reason about language design trade-offs, not just recite facts.
