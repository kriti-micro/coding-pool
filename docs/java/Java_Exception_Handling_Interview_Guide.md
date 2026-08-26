# ⚠️ Java Exception Handling — Complete Interview Guide
> Level: 8.5 Years Experience | MNC / Product Companies
> Topics: Hierarchy · Checked/Unchecked · try-catch-finally · Custom Exceptions · Tricky Q&A · FAQ

---

## 📌 Table of Contents

1. [Exception Hierarchy — Full Tree](#1-exception-hierarchy--full-tree)
2. [Checked vs Unchecked Exceptions](#2-checked-vs-unchecked-exceptions)
3. [try-catch-finally — Rules & Behaviour](#3-try-catch-finally--rules--behaviour)
4. [try-with-resources — AutoCloseable](#4-try-with-resources--autocloseable)
5. [throws vs throw](#5-throws-vs-throw)
6. [Custom Exceptions — Best Practices](#6-custom-exceptions--best-practices)
7. [Exception Chaining & Suppressed Exceptions](#7-exception-chaining--suppressed-exceptions)
8. [Multi-catch & Re-throwing](#8-multi-catch--re-throwing)
9. [Tricky Interview Questions — Deep Explanations](#9-tricky-interview-questions--deep-explanations)
10. [Exception Handling in Java 8+ (Streams & Lambdas)](#10-exception-handling-in-java-8-streams--lambdas)
11. [Production Best Practices](#11-production-best-practices)
12. [Interview Q&A — All FAQs with Answers](#12-interview-qa--all-faqs-with-answers)
13. [Quick Reference Cheat Sheet](#13-quick-reference-cheat-sheet)

---

## 1. Exception Hierarchy — Full Tree

```
java.lang.Object
    └── java.lang.Throwable                        ← root of ALL errors and exceptions
          │   getMessage(), getCause(), printStackTrace(),
          │   getStackTrace(), initCause(), getSuppressed()
          │
          ├── java.lang.Error                       ← serious JVM problems — DON'T catch!
          │     ├── OutOfMemoryError                ← heap full
          │     ├── StackOverflowError              ← deep/infinite recursion
          │     ├── VirtualMachineError             ← JVM internal error
          │     ├── AssertionError                  ← assert statement failed
          │     ├── ThreadDeath                     ← thread.stop() called (deprecated)
          │     ├── LinkageError
          │     │     ├── NoClassDefFoundError      ← class found at compile, missing at runtime
          │     │     ├── ClassFormatError
          │     │     └── UnsatisfiedLinkError      ← native library not found
          │     └── ExceptionInInitializerError     ← static initializer threw exception
          │
          └── java.lang.Exception                   ← recoverable conditions
                │
                ├── java.lang.RuntimeException      ← UNCHECKED (compiler doesn't enforce)
                │     ├── NullPointerException      ← method called on null reference
                │     ├── ArrayIndexOutOfBoundsException
                │     ├── StringIndexOutOfBoundsException
                │     ├── IndexOutOfBoundsException
                │     │     └── ArrayIndexOutOfBoundsException
                │     ├── ClassCastException        ← invalid type cast
                │     ├── IllegalArgumentException
                │     │     ├── NumberFormatException  ← parseInt("abc")
                │     │     └── IllegalThreadStateException
                │     ├── IllegalStateException     ← method called at wrong time
                │     ├── UnsupportedOperationException ← operation not supported
                │     ├── ArithmeticException       ← divide by zero
                │     ├── ConcurrentModificationException
                │     ├── NegativeArraySizeException
                │     ├── StackOverflowError        ← (also under Error)
                │     └── NoSuchElementException    ← Iterator.next() on empty
                │
                └── (All other Exception subclasses) ← CHECKED (compiler enforces)
                      ├── IOException
                      │     ├── FileNotFoundException
                      │     ├── EOFException
                      │     ├── SocketException
                      │     └── MalformedURLException
                      ├── SQLException
                      ├── ClassNotFoundException    ← Class.forName() fails
                      ├── CloneNotSupportedException
                      ├── InterruptedException      ← thread interrupted during wait/sleep
                      ├── ParseException
                      ├── TimeoutException
                      └── ReflectiveOperationException
                            ├── NoSuchMethodException
                            ├── NoSuchFieldException
                            └── IllegalAccessException


─── KEY RULE ─────────────────────────────────────────────────────────────────
  Throwable
    ├── Error          → UNCHECKED — JVM issues, don't catch
    ├── RuntimeException → UNCHECKED — programming bugs, don't force declare
    └── Exception (others) → CHECKED — must declare (throws) or catch
```

---

## 2. Checked vs Unchecked Exceptions

### Definition

```
CHECKED exceptions:
  ✓ Subclass of Exception BUT NOT RuntimeException
  ✓ Compiler FORCES you to handle (catch or declare with throws)
  ✓ Represent conditions outside programmer's control (network, file, DB)
  ✓ Examples: IOException, SQLException, ClassNotFoundException

UNCHECKED exceptions:
  ✓ Subclass of RuntimeException OR Error
  ✓ Compiler does NOT force handling
  ✓ Represent programming bugs (null check, array bounds, cast)
  ✓ Examples: NullPointerException, ClassCastException, StackOverflowError
```

### Why the Distinction Exists

```java
// CHECKED — you CAN'T know if file exists without trying
// Compiler forces you to handle it — you must deal with this possibility
public void readFile(String path) throws IOException {
    FileReader fr = new FileReader(path);   // compiler: "handle or declare!"
}

// UNCHECKED — this is a PROGRAMMER BUG
// You SHOULD have checked for null — no point forcing every caller to declare
String s = null;
s.length();   // NullPointerException — your bug, fix the code
// If NPE were checked: every method would need throws NullPointerException!
```

### Comparison Table

| Feature | Checked | Unchecked |
|---|---|---|
| Superclass | `Exception` (not Runtime) | `RuntimeException` or `Error` |
| Compiler check | ✅ Must catch or declare | ❌ Optional |
| Represents | External conditions | Programming bugs |
| Common examples | `IOException`, `SQLException` | `NPE`, `ClassCastException` |
| Best practice | Catch and recover meaningfully | Fix the code, don't catch |
| Spring / JPA | Wraps checked → `DataAccessException` (unchecked) | — |

---

## 3. try-catch-finally — Rules & Behaviour

### Basic Syntax

```java
try {
    // code that might throw
    int result = 10 / 0;
} catch (ArithmeticException e) {
    // handle specific exception — most specific FIRST
    System.out.println("Division error: " + e.getMessage());
} catch (RuntimeException e) {
    // handle broader exception — more general AFTER specific
    System.out.println("Runtime error");
} catch (Exception e) {
    // broadest catch — last resort
    System.out.println("General error");
} finally {
    // ALWAYS executes — cleanup code goes here
    // Runs even if: no exception, exception caught, exception uncaught
    System.out.println("Finally block");
}
```

### finally Rules — When Does It NOT Run?

```java
// finally ALWAYS runs EXCEPT:

// Exception 1: System.exit()
try {
    System.exit(0);   // JVM terminates — finally NEVER runs
} finally {
    System.out.println("Never printed");
}

// Exception 2: JVM crash (OutOfMemoryError, hardware failure)
// Exception 3: Thread killed externally (Thread.stop() — deprecated)

// Exception 4: Infinite loop or deadlock in try block
try {
    while(true) { }   // finally never reached
} finally { }
```

### Catch Order — Most Specific First

```java
// ❌ WRONG — compile error: "Exception has already been caught"
try { ... }
catch (Exception e) { }       // broader caught first
catch (IOException e) { }     // unreachable — compile ERROR

// ✅ CORRECT — specific first
try { ... }
catch (FileNotFoundException e) { }   // most specific
catch (IOException e) { }             // less specific
catch (Exception e) { }              // most general last
```

### Return in try-catch-finally

```java
// What does this return?
public int test() {
    try {
        return 1;
    } finally {
        return 2;   // finally return OVERRIDES try return!
    }
}
// Returns: 2
// Reason: finally ALWAYS executes, including after try's return statement
// The try's return value (1) is DISCARDED

// Another tricky case:
public int test2() {
    try {
        return 1;
    } finally {
        // no return here
        System.out.println("finally");
    }
}
// Returns: 1 (finally runs "println" but try's return value preserved)
// Output: "finally" printed, then 1 returned
```

---

## 4. try-with-resources — AutoCloseable

### The Problem (Before Java 7)

```java
// ❌ Old way — verbose, error-prone
Connection conn = null;
PreparedStatement ps = null;
try {
    conn = dataSource.getConnection();
    ps = conn.prepareStatement(sql);
    // use ps
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // Must close in reverse order, each in its own try!
    if (ps != null) {
        try { ps.close(); } catch (SQLException e) { /* ignored */ }
    }
    if (conn != null) {
        try { conn.close(); } catch (SQLException e) { /* ignored */ }
    }
}
```

### Java 7 — try-with-resources

```java
// ✅ New way — auto-close, exception-safe
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {

    ResultSet rs = ps.executeQuery();
    // use rs, ps, conn

} catch (SQLException e) {
    log.error("DB error", e);
}
// Resources auto-closed in REVERSE order: ps.close(), then conn.close()
// Even if exception occurs mid-way!
```

### AutoCloseable vs Closeable

```java
// Closeable (java.io) — extends AutoCloseable
// close() throws IOException

// AutoCloseable (java.lang) — since Java 7
// close() throws Exception (broader)
// Any class implementing AutoCloseable can be used in try-with-resources

// Custom resource:
class DatabaseTransaction implements AutoCloseable {
    private final Connection conn;

    DatabaseTransaction(DataSource ds) throws SQLException {
        this.conn = ds.getConnection();
        this.conn.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        conn.commit();
    }

    @Override
    public void close() throws SQLException {
        if (!conn.isClosed()) {
            conn.rollback();   // rollback if not committed
            conn.close();
        }
    }
}

// Usage:
try (DatabaseTransaction tx = new DatabaseTransaction(ds)) {
    repo.save(entity);
    tx.commit();   // explicit commit
}
// If commit() throws → close() runs → rollback() called automatically
```

### Suppressed Exceptions

```java
// If BOTH try body AND close() throw exceptions:
// → try's exception is PRIMARY
// → close()'s exception is SUPPRESSED (attached to primary)

try (MyResource r = new MyResource()) {
    throw new RuntimeException("try exception");
    // r.close() also throws: "close exception"
} catch (RuntimeException e) {
    System.out.println(e.getMessage());              // "try exception"
    System.out.println(e.getSuppressed()[0].getMessage()); // "close exception"
}
// getSuppressed() returns array of suppressed exceptions
```

---

## 5. throws vs throw

```java
// throw — used INSIDE method to throw an exception INSTANCE
public void validate(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative: " + age);
        //    ↑ throws the object right here
    }
}

// throws — used in METHOD SIGNATURE to DECLARE which exceptions may propagate
public void readFile(String path) throws IOException, FileNotFoundException {
    //                               ↑ declares — doesn't throw, just warns callers
    new FileReader(path);
}

// Can throw without throws if unchecked:
public void parse(String s) {
    Integer.parseInt(s);   // throws NumberFormatException (unchecked)
    // No "throws" needed in signature
}

// throws is part of METHOD CONTRACT — callers know what to expect
// Multiple exceptions separated by comma:
public void process() throws IOException, SQLException, InterruptedException { }
```

### Override Rules with throws

```java
class Parent {
    public void read() throws IOException { }
}

class Child extends Parent {
    // ✅ Can narrow (throw less):
    @Override
    public void read() throws FileNotFoundException { }   // subtype of IOException

    // ✅ Can remove throws entirely:
    @Override
    public void read() { }   // no exception thrown

    // ❌ CANNOT widen (throw more):
    @Override
    public void read() throws Exception { }   // compile error! Exception > IOException

    // ❌ CANNOT add new checked exceptions:
    @Override
    public void read() throws SQLException { }   // compile error! Not in parent
}
```

---

## 6. Custom Exceptions — Best Practices

### When to Create Custom Exceptions

```
Create custom exception when:
  ✓ Existing exceptions don't convey the right domain meaning
  ✓ You want to carry extra domain-specific data (orderId, errorCode)
  ✓ Callers need to distinguish your exception from general ones
  ✓ You want to centralise error messages for a domain

Don't create custom exception when:
  ✗ IllegalArgumentException, IllegalStateException etc. already fit
  ✗ You'd just wrap with no added value
```

### Well-Designed Custom Exception

```java
// ✅ Production-grade custom exception
public class OrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;   // for Serializable

    private final String orderId;
    private final String errorCode;

    // Constructor 1: message only
    public OrderNotFoundException(String orderId) {
        super("Order not found: " + orderId);
        this.orderId = orderId;
        this.errorCode = "ORDER_NOT_FOUND";
    }

    // Constructor 2: with cause (exception chaining — VERY important)
    public OrderNotFoundException(String orderId, Throwable cause) {
        super("Order not found: " + orderId, cause);
        this.orderId = orderId;
        this.errorCode = "ORDER_NOT_FOUND";
    }

    // Constructor 3: with custom error code
    public OrderNotFoundException(String orderId, String errorCode) {
        super(String.format("[%s] Order not found: %s", errorCode, orderId));
        this.orderId = orderId;
        this.errorCode = errorCode;
    }

    public String getOrderId() { return orderId; }
    public String getErrorCode() { return errorCode; }
}

// Usage:
try {
    Order order = orderRepo.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));
} catch (OrderNotFoundException e) {
    log.error("Order lookup failed [{}]: {}", e.getOrderId(), e.getMessage());
    return ResponseEntity.notFound().build();
}
```

### Exception Hierarchy for a Domain

```java
// Base exception for entire module/domain
public class PaymentException extends RuntimeException {
    private final String errorCode;

    public PaymentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    public PaymentException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public String getErrorCode() { return errorCode; }
}

// Specific subclasses
public class InsufficientFundsException extends PaymentException {
    private final BigDecimal available;
    private final BigDecimal required;

    public InsufficientFundsException(BigDecimal available, BigDecimal required) {
        super(String.format("Insufficient funds: required %.2f, available %.2f",
              required, available), "INSUFFICIENT_FUNDS");
        this.available = available;
        this.required = required;
    }
    // getters...
}

public class PaymentGatewayException extends PaymentException {
    private final int gatewayStatusCode;

    public PaymentGatewayException(String message, int statusCode, Throwable cause) {
        super(message, "GATEWAY_ERROR", cause);
        this.gatewayStatusCode = statusCode;
    }
}

// Catch hierarchy:
try {
    paymentService.process(request);
} catch (InsufficientFundsException e) {
    // handle specific case
    return "Insufficient funds: " + e.getAvailable();
} catch (PaymentGatewayException e) {
    // handle gateway issue
    log.error("Gateway [{}]: {}", e.getGatewayStatusCode(), e.getMessage());
    return "Payment gateway error";
} catch (PaymentException e) {
    // catch all payment exceptions
    return "Payment failed: " + e.getErrorCode();
}
```

---

## 7. Exception Chaining & Suppressed Exceptions

### Exception Chaining — Preserve Root Cause

```java
// ❌ BAD — swallowing the root cause
public User loadUser(int id) throws ServiceException {
    try {
        return userRepo.findById(id);
    } catch (SQLException e) {
        throw new ServiceException("Failed to load user");
        // ↑ SQL cause LOST — you can't diagnose the real problem!
    }
}

// ✅ GOOD — chain exceptions to preserve root cause
public User loadUser(int id) throws ServiceException {
    try {
        return userRepo.findById(id);
    } catch (SQLException e) {
        throw new ServiceException("Failed to load user: " + id, e);
        //                                                         ↑ cause preserved!
    }
}

// Access the chain:
try {
    service.loadUser(42);
} catch (ServiceException e) {
    System.out.println(e.getMessage());             // "Failed to load user: 42"
    System.out.println(e.getCause().getMessage());  // original SQLException message
    e.printStackTrace();   // prints full chain: ServiceException caused by SQLException
}
```

### Suppressed Exceptions (Java 7+)

```java
// Suppressed exceptions: secondary exceptions that occurred during cleanup
// try-with-resources handles this automatically

// Manual suppressed exception (rare, but know the API):
Exception primary = new RuntimeException("primary");
Exception suppressed = new IOException("during cleanup");
primary.addSuppressed(suppressed);
throw primary;

// Reading suppressed:
try { ... }
catch (Exception e) {
    for (Throwable t : e.getSuppressed()) {
        log.warn("Suppressed: {}", t.getMessage());
    }
}
```

---

## 8. Multi-catch & Re-throwing

### Multi-catch (Java 7+)

```java
// Before Java 7 — duplicated handling
try {
    riskyOperation();
} catch (IOException e) {
    log.error("Error", e);
    throw new ServiceException("Failed", e);
} catch (SQLException e) {
    log.error("Error", e);            // same code duplicated
    throw new ServiceException("Failed", e);
}

// Java 7+ — multi-catch
try {
    riskyOperation();
} catch (IOException | SQLException e) {
    // e is effectively final — cannot be reassigned
    log.error("Error", e);
    throw new ServiceException("Failed", e);
}

// ❌ CANNOT use related types in multi-catch:
catch (Exception | IOException e) { }  // compile error: IOException is subtype of Exception
```

### Re-throwing

```java
// Re-throw same exception (preserves original stack trace):
try {
    risky();
} catch (IOException e) {
    log.warn("Attempt failed, retrying...");
    throw e;   // re-throw original
}

// Re-throw as different type (wrap):
try {
    risky();
} catch (IOException e) {
    throw new ServiceException("Wrapped", e);   // chain the cause
}

// Re-throw after Java 7 type inference improvement:
// Compiler knows exact type from try block, not just the catch type
public void readAndWrite() throws IOException, SQLException {
    try {
        readFile();    // throws IOException
        writeDb();     // throws SQLException
    } catch (Exception e) {
        log.error("Failed", e);
        throw e;   // Java 7+: compiler infers actual type (IOException or SQLException)
        //           NOT Exception — so no widening of throws clause needed!
    }
}
```

---

## 9. Tricky Interview Questions — Deep Explanations

---

### 🔥 TRICK Q1. What is the output?

```java
public class Test {
    public static void main(String[] args) {
        try {
            System.out.println("try");
            return;
        } finally {
            System.out.println("finally");
        }
    }
}
```

**Answer:**
```
try
finally
```

**Why?** `finally` ALWAYS executes — even when `return` is in the `try` block.
The execution order:
1. `"try"` printed
2. `return` statement encountered — JVM saves the return value
3. `finally` block executes — `"finally"` printed
4. Actual return happens

---

### 🔥 TRICK Q2. What does this return?

```java
public static int getValue() {
    try {
        return 10;
    } finally {
        return 20;   // return in finally
    }
}
System.out.println(getValue());
```

**Answer:** `20`

`finally` block's `return` **overrides** `try` block's `return`.
The value `10` is computed and staged, but `finally`'s `return 20` replaces it.

> ⚠️ **Production rule:** NEVER put `return`, `break`, or `continue` inside `finally`. It masks exceptions and produces confusing behaviour. Most static analysis tools flag this.

---

### 🔥 TRICK Q3. What is the output?

```java
public static int divide() {
    int x = 0;
    try {
        x = 5;
        int result = 10 / 0;   // throws ArithmeticException
        x = 20;
    } catch (ArithmeticException e) {
        x = 30;
        return x;              // return 30 from catch
    } finally {
        x = 40;                // x modified in finally
        // no return here
    }
    return x;
}
System.out.println(divide());
```

**Answer:** `30`

```
Step 1: x = 5
Step 2: 10/0 → ArithmeticException
Step 3: catch: x = 30, return x (30 STAGED for return)
Step 4: finally: x = 40  ← x changes to 40, but return value ALREADY STAGED as 30
Step 5: 30 returned  ← staged value returned, not current x
```

> 💡 `finally` modifying a local variable does NOT affect the already-staged return value.
> BUT if the return type is an **object reference**, modifying the object's **state** WILL be visible.

```java
// Contrast with object reference:
public static List<Integer> getList() {
    List<Integer> list = new ArrayList<>();
    try {
        list.add(1);
        return list;   // reference to list staged for return
    } finally {
        list.add(2);   // modifies the OBJECT — this DOES affect return!
    }
}
// Returns: [1, 2]  ← finally modified the list object!
```

---

### 🔥 TRICK Q4. Can we catch Error?

```java
try {
    recurse();   // causes StackOverflowError
} catch (StackOverflowError e) {
    System.out.println("Caught: " + e);
}
```

**Answer:** Yes, syntactically you CAN catch `Error`. But **should you?**

```
Generally: NO — don't catch Error
Reason: Errors indicate the JVM is in an unrecoverable state
  - StackOverflowError: call stack corrupted
  - OutOfMemoryError:   heap exhausted, even logging may fail
  - Catching gives false sense of recovery

Exceptions (rare legitimate cases):
  ✓ Catch OutOfMemoryError in top-level handler to log before JVM dies
  ✓ Catch StackOverflowError in recursive evaluators to return error result
  ✓ Test frameworks catch AssertionError to report failures
  ✓ Never re-throw as RuntimeException — lose the signal

Rule: Catch Error only at the very top level, log, and die gracefully.
```

---

### 🔥 TRICK Q5. What is the output?

```java
try {
    System.out.println("A");
    throw new RuntimeException("1");
} catch (RuntimeException e) {
    System.out.println("B");
    throw new RuntimeException("2");
} finally {
    System.out.println("C");
}
System.out.println("D");
```

**Answer:**
```
A
B
C
Exception in thread "main" java.lang.RuntimeException: 2
```

- `"A"` → try executes
- `RuntimeException("1")` thrown → catch block runs
- `"B"` → catch executes
- `RuntimeException("2")` thrown from catch → but first...
- `"C"` → finally ALWAYS runs, even when catch throws
- `RuntimeException("2")` propagates upward (exception "1" is lost!)
- `"D"` is NEVER reached

> 💡 `RuntimeException("1")` is completely lost — replaced by `RuntimeException("2")`. This is why you should chain exceptions: `throw new RuntimeException("2", e)`.

---

### 🔥 TRICK Q6. NoClassDefFoundError vs ClassNotFoundException

```java
// ClassNotFoundException — CHECKED exception
// Thrown by: Class.forName(), ClassLoader.loadClass()
// Cause: class name doesn't exist at runtime
try {
    Class<?> clazz = Class.forName("com.example.NonExistentClass");
} catch (ClassNotFoundException e) {
    System.out.println("Class not found: " + e.getMessage());
}

// NoClassDefFoundError — ERROR (unchecked)
// Cause: class WAS available at compile time but MISSING at runtime
// (JAR removed from classpath between compile and run)
// You CAN'T catch this in normal code — it's an Error
```

| | `ClassNotFoundException` | `NoClassDefFoundError` |
|---|---|---|
| Type | Checked Exception | Error (unchecked) |
| When | Class name doesn't exist | Class existed at compile, missing at run |
| Thrown by | `Class.forName()`, ClassLoader | JVM classloader during resolution |
| Catch? | ✅ Yes, expected | ❌ Avoid — JVM issue |
| Common cause | Wrong class name string | Missing JAR in classpath |

---

### 🔥 TRICK Q7. What is the output? (finally with exception)

```java
public static void main(String[] args) {
    try {
        System.out.println(test());
    } catch (Exception e) {
        System.out.println("Caught: " + e.getMessage());
    }
}

public static String test() {
    try {
        throw new RuntimeException("from try");
    } catch (RuntimeException e) {
        throw new RuntimeException("from catch");
    } finally {
        return "from finally";   // return in finally swallows exception!
    }
}
```

**Answer:** `from finally`

> **Shocking but true:** `return` in `finally` **silently swallows** the `RuntimeException("from catch")`!
> The exception is completely lost — no stack trace, no log, nothing.
> This is why `return` in `finally` is one of Java's worst practices.

---

### 🔥 TRICK Q8. NullPointerException improvement in Java 14

```java
// Before Java 14 — cryptic NPE message
User user = null;
String city = user.getAddress().getCity();
// NullPointerException (no info about which call was null!)

// Java 14+ Helpful NullPointerExceptions (JEP 358):
// Cannot invoke "User.getAddress()" because "user" is null
// Much more precise — tells you EXACTLY which variable was null!

// Enable in Java 14 (preview):
// java -XX:+ShowCodeDetailsInExceptionMessages MyApp

// Default since Java 15 — no flag needed
```

---

### 🔥 TRICK Q9. Exception in static initializer

```java
class Config {
    static int value;
    static {
        value = Integer.parseInt("not-a-number");   // throws NumberFormatException
    }
}

// What happens when you use Config anywhere?
Config c = new Config();
```

**Answer:** `ExceptionInInitializerError` on first use, then `NoClassDefFoundError` on every subsequent use.

```
First access:
  → Static initializer runs
  → NumberFormatException thrown
  → JVM wraps it: throw new ExceptionInInitializerError(cause)
  → Class marked as "failed to initialize"

Every subsequent access:
  → NoClassDefFoundError: Could not initialize class Config
  → Even though the exception cause is different!
  → The class is permanently "broken" for this JVM run

// Catch:
try {
    Class.forName("Config");
} catch (ExceptionInInitializerError e) {
    System.out.println("Init failed: " + e.getCause().getMessage());
}
```

---

### 🔥 TRICK Q10. Multi-catch with effectively final

```java
// e is effectively final in multi-catch — cannot be reassigned
try {
    riskyOp();
} catch (IOException | SQLException e) {
    e = new IOException("reassigned");   // ❌ COMPILE ERROR
    // "The parameter e of a multi-catch block cannot be assigned"
    throw e;
}

// Contrast: single catch — CAN reassign (unusual but allowed)
try {
    riskyOp();
} catch (IOException e) {
    e = new IOException("reassigned");   // ✅ compiles (but bad practice)
    throw e;
}
```

---

### 🔥 TRICK Q11. StackOverflowError vs OutOfMemoryError

```java
// StackOverflowError — call stack too deep
void recurse() {
    recurse();   // each call adds a frame to the stack
}
// Stack frames are in Stack memory (not heap)
// Default stack size: ~512KB to 1MB per thread
// Fix: add base case, or increase: java -Xss4m

// OutOfMemoryError — heap exhausted (most common)
List<byte[]> leak = new ArrayList<>();
while (true) {
    leak.add(new byte[1024 * 1024]);   // keep adding 1MB chunks
}
// java.lang.OutOfMemoryError: Java heap space

// OutOfMemoryError variants:
// "Java heap space"          — heap full (most common)
// "GC overhead limit exceeded" — GC spending >98% time, <2% freed
// "Metaspace"                — class metadata space full (too many classes)
// "Unable to create native thread" — too many threads
// "Direct buffer memory"     — NIO direct byte buffers exhausted
```

---

### 🔥 TRICK Q12. What happens when catch and finally both throw?

```java
public static void test() throws Exception {
    try {
        throw new Exception("from try");
    } catch (Exception e) {
        throw new Exception("from catch");
    } finally {
        throw new Exception("from finally");   // THIS wins!
    }
}
```

**Answer:** `Exception: from finally` is thrown.

```
Execution:
  1. try throws "from try"
  2. catch catches it, throws "from catch"
  3. finally ALWAYS runs → throws "from finally"
  4. "from finally" REPLACES "from catch" — which REPLACES "from try"
  5. ALL previous exceptions are silently LOST!

Order of exception priority:
  finally exception > catch exception > try exception

This is why: NEVER throw from finally block!
```

---

## 10. Exception Handling in Java 8+ (Streams & Lambdas)

### The Problem — Checked Exceptions in Lambdas

```java
// Functional interfaces don't declare checked exceptions
// This doesn't compile:
List<String> paths = Arrays.asList("file1.txt", "file2.txt");
paths.stream()
     .map(path -> new FileReader(path))  // ❌ IOException not handled!
     .collect(Collectors.toList());
```

### Solution 1 — Wrap in try-catch inside lambda

```java
paths.stream()
     .map(path -> {
         try {
             return new FileReader(path);
         } catch (IOException e) {
             throw new UncheckedIOException(e);   // wrap as unchecked
         }
     })
     .collect(Collectors.toList());
```

### Solution 2 — Utility wrapper method

```java
// Create a helper that wraps checked exceptions
@FunctionalInterface
interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}

static <T, R> Function<T, R> wrap(ThrowingFunction<T, R> fn) {
    return t -> {
        try {
            return fn.apply(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}

// Usage — clean lambda with checked exception:
paths.stream()
     .map(wrap(path -> new FileReader(path)))   // ✅ clean!
     .collect(Collectors.toList());
```

### Solution 3 — Using UncheckedIOException (Java 8 built-in)

```java
// java.io.UncheckedIOException wraps IOException as unchecked
paths.stream()
     .map(path -> {
         try { return Files.readString(Path.of(path)); }
         catch (IOException e) { throw new UncheckedIOException(e); }
     })
     .collect(Collectors.toList());
```

### Exception Handling with CompletableFuture

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> callExternalApi())
    .thenApply(result -> processResult(result))
    .exceptionally(ex -> {
        log.error("API call failed", ex);
        return "default-value";   // fallback
    });

// Handle and transform:
CompletableFuture<String> future2 = CompletableFuture
    .supplyAsync(() -> riskyOp())
    .handle((result, ex) -> {
        if (ex != null) {
            log.error("Failed", ex);
            return "fallback";
        }
        return result;
    });
```

---

## 11. Production Best Practices

### ✅ DO's

```java
// 1. Be specific — catch what you can handle
try {
    order = orderService.findById(id);
} catch (OrderNotFoundException e) {
    return ResponseEntity.notFound().build();
} catch (DatabaseException e) {
    return ResponseEntity.internalServerError().build();
}

// 2. Always chain exceptions — preserve root cause
catch (SQLException e) {
    throw new DataAccessException("Failed to save order", e);  // ← chain!
}

// 3. Log with cause
catch (Exception e) {
    log.error("Failed processing order {}", orderId, e);  // ← 3rd arg logs stack trace
    // NOT: log.error("Failed: " + e.getMessage());  ← loses stack trace
}

// 4. Close resources with try-with-resources
try (InputStream is = new FileInputStream(file)) {
    // use is
}

// 5. Custom exceptions with domain data
throw new PaymentFailedException(orderId, amount, gateway, cause);

// 6. Use Optional to avoid NPE (return empty, not null)
return Optional.ofNullable(userRepo.findById(id));

// 7. Validate early — throw at source, not deep inside
public void setAge(int age) {
    if (age < 0 || age > 150)
        throw new IllegalArgumentException("Invalid age: " + age);
    this.age = age;
}
```

### ❌ DON'Ts

```java
// 1. NEVER swallow exceptions silently
catch (Exception e) { }              // ❌ completely hides problem

// 2. NEVER catch Throwable or Exception broadly without re-throwing
catch (Exception e) {
    log.error("error");              // ❌ no context, no re-throw
}

// 3. NEVER use exceptions for flow control
// Exception creation is expensive (captures stack trace)
try {
    int val = Integer.parseInt(input);
} catch (NumberFormatException e) {
    val = 0;   // ❌ use input.matches("\\d+") to check first!
}

// 4. NEVER return null for "not found" — throw or use Optional
public User findUser(int id) {
    return null;   // ❌ caller must check for null (often forgets → NPE)
}
public Optional<User> findUser(int id) {
    return Optional.ofNullable(repo.findById(id));  // ✅

// 5. NEVER throw from finally
finally {
    throw new RuntimeException("from finally");  // ❌ masks original exception!
}

// 6. NEVER catch and re-throw without chaining
catch (IOException e) {
    throw new ServiceException("Failed");  // ❌ e lost!
    throw new ServiceException("Failed", e);  // ✅ e preserved
}

// 7. NEVER log AND throw (double logging)
catch (Exception e) {
    log.error("Failed", e);                // logged here
    throw new ServiceException("Failed", e); // and again at caller → duplicate logs!
}
// Rule: either log OR throw, not both
```

### Spring Boot Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle domain exceptions
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrderNotFound(OrderNotFoundException ex,
                                             HttpServletRequest request) {
        log.warn("Order not found: {}", ex.getOrderId());
        return ErrorResponse.builder()
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .timestamp(Instant.now())
            .build();
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid"
            ));
        return ErrorResponse.builder()
            .errorCode("VALIDATION_ERROR")
            .message("Validation failed")
            .fieldErrors(errors)
            .build();
    }

    // Catch-all
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return ErrorResponse.builder()
            .errorCode("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .build();
    }
}
```

---

## 12. Interview Q&A — All FAQs with Answers

### ❓ Q1. What is the difference between throw and throws?

> "`throw` is a **statement** used inside a method body to explicitly throw an exception instance. `throws` is a **keyword** in the method signature that declares which checked exceptions the method may propagate. `throw` actually throws; `throws` just warns callers."

---

### ❓ Q2. Can finally block be skipped?

> "Yes — in four cases: (1) `System.exit()` is called — JVM terminates immediately; (2) JVM crash (OOM, hardware failure); (3) the thread is forcibly killed; (4) there's an infinite loop or deadlock in the try block. In all normal cases — even with an uncaught exception or a `return` in try — `finally` always executes."

---

### ❓ Q3. What is exception chaining? Why is it important?

> "Exception chaining (wrapping) means including the original exception as the `cause` of a new one: `throw new ServiceException("message", originalException)`. It's important because it preserves the entire call chain from the root cause to the surface error. Without it, you lose diagnostic information — you'd see 'Service failed' but not the underlying `SQLException` that caused it. The `getCause()` method traverses the chain, and `printStackTrace()` shows the full chain."

---

### ❓ Q4. What is the difference between `e.getMessage()` and `e.toString()` and `e.printStackTrace()`?

```java
try {
    throw new RuntimeException("Something went wrong");
} catch (RuntimeException e) {
    e.getMessage();     // "Something went wrong"
    e.toString();       // "java.lang.RuntimeException: Something went wrong"
    e.getClass().getName(); // "java.lang.RuntimeException"
    e.printStackTrace();    // full stack trace to stderr
    // For logging — use logger, not printStackTrace:
    log.error("Failed", e);   // logs message + full stack trace
}
```

---

### ❓ Q5. Can we have try without catch?

> "Yes — two valid forms: (1) `try-finally` without catch: the code in try runs, finally always cleans up, and any exception propagates naturally. (2) `try-with-resources` without explicit catch: resources auto-close, exceptions propagate. You cannot have try alone without either catch or finally."

```java
// Valid: try-finally without catch
try {
    riskyOperation();
} finally {
    cleanup();   // runs always, exception still propagates
}
```

---

### ❓ Q6. What is the difference between `Checked` and `Unchecked` exceptions from a design perspective?

> "Checked exceptions are part of the **method contract** — they signal expected failure modes that callers should handle (file not found, network timeout). Unchecked exceptions signal **programming errors** that should be fixed in code (null check, bounds check). The debate in modern Java: many frameworks (Spring, Hibernate) convert checked exceptions to unchecked because forcing callers to catch `SQLException` everywhere is noise. The principle: use checked when the caller can meaningfully recover; use unchecked when it's a bug or an unrecoverable state."

---

### ❓ Q7. Why does Spring's @Transactional only roll back on RuntimeException by default?

```java
@Transactional   // rolls back on RuntimeException and Error only!
public void saveOrder(Order order) throws IOException {
    orderRepo.save(order);
    writeAuditLog(order);   // throws IOException (CHECKED)
    // IOException will NOT trigger rollback by default!
}

// Fix:
@Transactional(rollbackFor = Exception.class)   // roll back on all exceptions
public void saveOrder(Order order) throws IOException { ... }

// Or:
@Transactional(rollbackFor = {IOException.class, CustomException.class})
```

> "Spring chose `RuntimeException` as the default rollback trigger because EJB used the same convention, and because checked exceptions in service methods were considered 'expected' recoverable conditions. It's a common production bug — always specify `rollbackFor = Exception.class` when your transactional method throws checked exceptions."

---

### ❓ Q8. What are suppressed exceptions?

> "Suppressed exceptions occur in `try-with-resources` when both the try body and the resource's `close()` method throw exceptions. Java attaches the `close()` exception as a suppressed exception on the primary exception. Access via `e.getSuppressed()`. This preserves both exceptions rather than losing the close() exception (which was the old behavior before Java 7)."

---

## 13. Quick Reference Cheat Sheet

### Exception Hierarchy Summary

```
Throwable
  ├── Error (unchecked, don't catch)
  │     StackOverflowError, OutOfMemoryError, AssertionError
  └── Exception
        ├── RuntimeException (unchecked, fix the code)
        │     NPE, ClassCastException, IllegalArgumentException,
        │     IllegalStateException, UnsupportedOperationException,
        │     ConcurrentModificationException, NumberFormatException
        └── Checked (must handle or declare)
              IOException, SQLException, ClassNotFoundException,
              InterruptedException, ParseException
```

### try-catch-finally Rules

```
finally ALWAYS runs (except System.exit / JVM crash)
finally return OVERRIDES try/catch return (avoid!)
finally exception SWALLOWS try/catch exception (avoid throw in finally!)
Catch order: most specific FIRST, most general LAST
Multi-catch (|): e is effectively final, types must not be related
Override: child can narrow/remove throws, cannot widen
```

### Custom Exception Checklist

```
☐ Extends RuntimeException (unchecked) or Exception (checked)
☐ serialVersionUID defined (Serializable compliance)
☐ Constructor with (String message)
☐ Constructor with (String message, Throwable cause)   ← most important
☐ Domain fields (orderId, errorCode etc.)
☐ Getters for domain fields
☐ Meaningful message including context data
☐ Never swallow the cause (always pass to super)
```

### When to Use Which

```
IllegalArgumentException    → parameter validation (age < 0, null input)
IllegalStateException       → method called at wrong time (already closed)
NullPointerException        → don't throw manually, fix null check
UnsupportedOperationException → feature not implemented
NoSuchElementException      → empty iterator, missing element
IndexOutOfBoundsException   → invalid index (don't throw manually)
NumberFormatException       → bad numeric string (from parseInt)
Custom Exception            → domain-specific failures with extra data
```

### Logging Best Practices

```java
// Full exception with stack trace (use exception as last arg):
log.error("Failed to process order {}", orderId, exception);

// Message only (no stack trace):
log.error("Failed: {}", exception.getMessage());

// NEVER concatenate in log:
log.error("Failed: " + exception);   // ❌ always evaluated even if log level off

// Log OR throw, NOT both:
// ❌ Don't: log.error(...); throw new ... ;  → duplicate in logs
// ✅ Do:    throw new ServiceException(msg, cause);  // let top-level log
```

---

*Prepared from Claude AI session | Kriti Singh | 8.5 YOE Java Developer*
*Topics: Exception Hierarchy · Checked/Unchecked · try-catch-finally · try-with-resources · Custom Exceptions · Tricky Q&A*
