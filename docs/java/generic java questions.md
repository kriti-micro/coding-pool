# Section 1 : Abstract Class vs Interface in Java

## Why Does an Abstract Class Have a Constructor?

An abstract class cannot be instantiated directly.

```java
abstract class Animal {
    Animal() {
        System.out.println("Animal Constructor");
    }
}

Animal a = new Animal(); // Compilation Error
```

However, when a subclass object is created, the abstract class constructor is executed first.

```java
abstract class Animal {
    Animal() {
        System.out.println("Animal Constructor");
    }
}

class Dog extends Animal {
    Dog() {
        System.out.println("Dog Constructor");
    }
}

public class Main {
    public static void main(String[] args) {
        Dog d = new Dog();
    }
}
```

Output:

```text
Animal Constructor
Dog Constructor
```

### Purpose of Abstract Class Constructors

The constructor is used to initialize common data required by all child classes.

```java
abstract class Employee {
    protected String companyName;

    Employee() {
        companyName = "ABC Bank";
    }
}
```

Every employee automatically gets the company name initialized.

---

# Purpose of Abstract Classes in Real Projects

Abstract classes are used when multiple classes share:

* Common state (instance variables)
* Common behavior (implemented methods)
* A common workflow that should not be duplicated

Example: Payment Processing System

```java
abstract class Payment {

    public void initiatePayment() {
        validate();
        processPayment();
        generateReceipt();
    }

    protected abstract void processPayment();

    private void validate() {
        System.out.println("Validation");
    }

    private void generateReceipt() {
        System.out.println("Receipt Generated");
    }
}
```

Child classes:

```java
class UpiPayment extends Payment {
    @Override
    protected void processPayment() {
        System.out.println("UPI Payment Processing");
    }
}

class CardPayment extends Payment {
    @Override
    protected void processPayment() {
        System.out.println("Card Payment Processing");
    }
}
```

Benefits:

* Code reuse
* Reduced duplication
* Standard workflow
* Forces child classes to implement required methods

This is a classic example of the Template Method Design Pattern.

---

# What Is an Interface?

An interface defines a contract that implementing classes must follow.

```java
interface Notifier {
    void sendNotification();
}
```

Implementations:

```java
class EmailNotifier implements Notifier {
    public void sendNotification() {
        System.out.println("Email Sent");
    }
}

class SmsNotifier implements Notifier {
    public void sendNotification() {
        System.out.println("SMS Sent");
    }
}
```

Usage:

```java
Notifier notifier = new EmailNotifier();
notifier.sendNotification();
```

Later:

```java
Notifier notifier = new SmsNotifier();
```

No changes are required in the calling code.

Benefits:

* Loose coupling
* Dependency Injection
* Easy testing and mocking
* Multiple implementations

---

# Why Were Default Methods Introduced in Java 8?

Before Java 8, interfaces could contain only abstract methods.

Suppose an interface is used by hundreds of classes.

```java
interface Vehicle {
    void start();
}
```

Later, a new method is required:

```java
interface Vehicle {
    void start();
    void stop();
}
```

Problem:

All existing implementations break and must implement the new method.

To solve this, Java 8 introduced Default Methods.

---

# Default Methods in Interface

Default methods provide a method implementation inside an interface.

```java
interface Vehicle {

    void start();

    default void stop() {
        System.out.println("Vehicle Stopped");
    }
}
```

Implementation:

```java
class Car implements Vehicle {

    @Override
    public void start() {
        System.out.println("Car Started");
    }
}
```

Usage:

```java
Car car = new Car();

car.start();
car.stop();
```

Output:

```text
Car Started
Vehicle Stopped
```

### Why Use Default Methods?

* Add new functionality without breaking existing code.
* Backward compatibility.
* Share common behavior among implementations.

### Real Example

Java Collection Framework:

```java
List<String> list = new ArrayList<>();

list.forEach(System.out::println);
```

The `forEach()` method was added as a default method in the Collection interface.

---

# Static Methods in Interface (Java 8)

Java 8 also introduced static methods in interfaces.

```java
interface MathUtil {

    static int add(int a, int b) {
        return a + b;
    }
}
```

Usage:

```java
int result = MathUtil.add(10, 20);
System.out.println(result);
```

Output:

```text
30
```

### Why Use Static Methods?

* Utility/helper methods related to the interface.
* No need to create separate utility classes.
* Keeps related functionality together.

Example:

```java
interface ValidationUtil {

    static boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
```

Usage:

```java
boolean valid = ValidationUtil.isValidEmail("abc@gmail.com");
```

---

# Difference Between Default and Static Methods

| Feature                         | Default Method  | Static Method        |
| ------------------------------- | --------------- | -------------------- |
| Inherited by implementing class | Yes             | No                   |
| Can be overridden               | Yes             | No                   |
| Called using object             | Yes             | No                   |
| Called using interface name     | No              | Yes                  |
| Purpose                         | Shared behavior | Utility/helper logic |

Example:

```java
interface Test {

    default void display() {
        System.out.println("Default Method");
    }

    static void print() {
        System.out.println("Static Method");
    }
}
```

Usage:

```java
class Demo implements Test {
}

public class Main {

    public static void main(String[] args) {

        Demo d = new Demo();

        d.display();       // Valid

        Test.print();      // Valid
    }
}
```

---

# Abstract Class vs Interface

| Feature               | Abstract Class        | Interface                     |
| --------------------- | --------------------- | ----------------------------- |
| Constructor           | Yes                   | No                            |
| Instance Variables    | Yes                   | Constants only                |
| State/Data            | Can hold state        | Generally no state            |
| Method Implementation | Abstract + Concrete   | Abstract + Default + Static   |
| Multiple Inheritance  | No                    | Yes                           |
| Access Modifiers      | Any                   | Methods are public by default |
| Use Case              | Shared code and state | Contract and loose coupling   |

---

# Spring Boot Real-World Usage

### Interface

```java
public interface PaymentService {
    void pay(double amount);
}
```

Implementation:

```java
@Service
public class UpiPaymentService implements PaymentService {

    @Override
    public void pay(double amount) {
        System.out.println("UPI Payment");
    }
}
```

Controller:

```java
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Spring injects the implementation automatically.

This enables:

* Dependency Injection
* Easy Unit Testing
* Multiple Implementations
* Loose Coupling

---

# Interview Answer (Short Version)

### When to Use Abstract Class?

Use an abstract class when:

* Classes share common state and behavior.
* Code reuse is required.
* You need constructors.
* You want a common workflow.

Example:

```text
Employee
 ├── Developer
 ├── Tester
 └── Manager
```

### When to Use Interface?

Use an interface when:

* You need a contract.
* Multiple implementations are possible.
* Loose coupling is required.
* Dependency Injection is used.

Example:

```text
PaymentService
 ├── UpiPaymentService
 ├── CardPaymentService
 └── WalletPaymentService
```

### Rule of Thumb

* Abstract Class = Common State + Common Behavior
* Interface = Contract + Flexibility + Dependency Injection

In modern Spring Boot applications, interfaces are used far more frequently, while abstract classes are used when substantial shared logic or state must be reused.

# Section 2 : Method Hiding in Java

Method Hiding occurs when a child class declares a **static method** with the same signature as a static method in the parent class.

Unlike method overriding, static methods belong to the class, not to objects.

Therefore, the method that gets called is determined by the **reference type**, not the actual object type.

---

## Example of Method Hiding

```java
class Parent {

    static void display() {
        System.out.println("Parent Static Method");
    }
}

class Child extends Parent {

    static void display() {
        System.out.println("Child Static Method");
    }
}

public class Main {

    public static void main(String[] args) {

        Parent p = new Child();

        p.display();
    }
}
```

Output:

```text
Parent Static Method
```

Many developers expect:

```text
Child Static Method
```

But that is incorrect.

Since `display()` is static, Java resolves it using the reference type (`Parent`) during compile time.

Equivalent to:

```java
Parent.display();
```

---

# Method Hiding vs Method Overriding

## Overriding (Runtime Polymorphism)

```java
class Parent {

    void display() {
        System.out.println("Parent");
    }
}

class Child extends Parent {

    @Override
    void display() {
        System.out.println("Child");
    }
}

public class Main {

    public static void main(String[] args) {

        Parent p = new Child();

        p.display();
    }
}
```

Output:

```text
Child
```

Because instance methods participate in runtime polymorphism.

---

## Hiding (Compile-Time Binding)

```java
class Parent {

    static void display() {
        System.out.println("Parent");
    }
}

class Child extends Parent {

    static void display() {
        System.out.println("Child");
    }
}

public class Main {

    public static void main(String[] args) {

        Parent p = new Child();

        p.display();
    }
}
```

Output:

```text
Parent
```

Because static methods are resolved at compile time.

---

# Why Can't Static Methods Be Overridden?

Static methods belong to the class itself.

```java
Parent.display();
Child.display();
```

No object is required.

Since overriding depends on the actual object created at runtime, static methods cannot participate in runtime polymorphism.

Therefore:

```text
Instance Methods  -> Overriding
Static Methods    -> Hiding
```

---

# Real Project Example

Consider utility classes:

```java
class BaseLogger {

    static void log() {
        System.out.println("Base Logging");
    }
}

class ApplicationLogger extends BaseLogger {

    static void log() {
        System.out.println("Application Logging");
    }
}
```

Usage:

```java
BaseLogger logger = new ApplicationLogger();

logger.log();
```

Output:

```text
Base Logging
```

The reference type determines which static method is called.

---

# Interview Question

### What is Method Hiding?

Method hiding occurs when a subclass defines a static method with the same signature as a static method in the parent class.

Unlike overriding:

* Static methods are resolved at compile time.
* Resolution is based on the reference type.
* Runtime polymorphism does not apply.

### Key Difference

| Feature       | Method Overriding | Method Hiding         |
| ------------- | ----------------- | --------------------- |
| Method Type   | Instance Method   | Static Method         |
| Binding       | Runtime           | Compile Time          |
| Polymorphism  | Yes               | No                    |
| Determined By | Actual Object     | Reference Type        |
| Annotation    | @Override Allowed | @Override Not Allowed |

### One-Line Interview Answer

**Static methods are hidden, not overridden, because they belong to the class rather than the object. The method invoked depends on the reference type, not the runtime object type.**

### Note

* Overloading  -> Same method name, different parameters.
* Overriding   -> Runtime polymorphism (instance methods).
* Method Hiding -> Compile-time polymorphism (static methods).

# Section 3 : Java `static` Keyword: Explanation and Detailed Guide

## Part 1: Original Explanation & Example

The **`static` keyword** in Java is a non-access modifier used mainly for **memory management**. It indicates that a particular member (variable, method, block, or nested class) **belongs to the class itself** rather than to individual instances (objects) of that class.

When you declare something as static, Java allocates memory for it **only once** when the class loads, and all objects of that class share this single copy.

### Core Uses of the `static` Keyword

You can apply the `static` keyword to four components of a Java program:

#### 1. Static Variables (Class Variables)
* A variable declared with `static` is shared among all objects of that class.
* Modifying its value in one instance changes it across all instances.
* **Common Use:** Storing data that should be common to all objects, such as a company name for employees or a counter to track instances.

#### 2. Static Methods (Class Methods)
* A method that belongs to the class and can be invoked directly using the class name (`ClassName.methodName()`) without instantiating an object first.
* **Restrictions:** They can **only** access other static variables or methods directly. They cannot use the `this` or `super` keywords.
* **Common Use:** Creating utility or helper functions (like Java's built-in `Math` class methods).

#### 3. Static Blocks
* A block of code that executes exactly **once** when the class is first loaded into memory, even before the `main` method runs.
* **Common Use:** Initializing static variables or performing pre-runtime configurations (like establishing a database connection).

#### 4. Static Nested Classes
* In Java, you cannot make an outer class static, but you can declare a nested (inner) class as static.
* A static nested class does not need a reference to the outer class object to be instantiated.

### Complete Code Example

The following program demonstrates how static variables, methods, blocks, and regular instances interact.

```java
class Student {
    // Instance variables (unique to each object)
    int rollNo;
    String name;

    // 1. Static Variable (shared across all student objects)
    static String collegeName;
    static int studentCount = 0;

    // 3. Static Block (runs once when class is loaded)
    static {
        collegeName = "Global Tech University";
        System.out.println("Static block executed: College name initialized to " + collegeName);
    }

    // Constructor
    public Student(int rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
        studentCount++; // Tracks total students created
    }

    // 2. Static Method (belongs to the class)
    public static void displayTotalStudents() {
        // Can only access static fields directly
        System.out.println("Total Students Enrolled: " + studentCount);
        
        // System.out.println(this.name); // ERROR: Cannot use 'this' or instance fields here
    }

    // Regular Instance Method
    public void displayDetails() {
        // Can access both instance and static variables
        System.out.println("Roll No: " + rollNo + " | Name: " + name + " | College: " + collegeName);
    }
}

public class Main {
    // The main method is static so the JVM can call it without instantiating Main
    public static void main(String[] args) {
        System.out.println("Main method started.");

        // Accessing a static method directly via Class Name (no object created yet)
        Student.displayTotalStudents(); 

        // Creating student objects
        Student s1 = new Student(101, "Alice");
        Student s2 = new Student(102, "Bob");

        // Display individual object details
        s1.displayDetails();
        s2.displayDetails();

        // Updating the shared static variable via class name
        Student.collegeName = "MIT Tech";
        System.out.println("\n--- After Changing College Name ---");
        
        // The change reflects across all existing and future objects
        s1.displayDetails();
        s2.displayDetails();

        // Check the total student count again
        Student.displayTotalStudents();
    }
}
```

#### Output:
```text
Static block executed: College name initialized to Global Tech University
Main method started.
Total Students Enrolled: 0
Roll No: 101 | Name: Alice | College: Global Tech University
Roll No: 102 | Name: Bob | College: Global Tech University

--- After Changing College Name ---
Roll No: 101 | Name: Alice | College: MIT Tech
Roll No: 102 | Name: Bob | College: MIT Tech
Total Students Enrolled: 2
```

### Quick Summary: Static vs. Non-Static

| Feature | Static Members | Non-Static (Instance) Members |
| :--- | :--- | :--- |
| **Ownership** | Belongs to the **Class**. | Belongs to the **Object Instance**. |
| **Memory Allocation** | Created **once** when class loads. | Created **every time** an object is made. |
| **How to Access** | Via class name (`Class.member`). | Via object reference (`object.member`). |
| **Direct Access Rules**| Can only access other static members. | Can access both static and non-static members. |

---

## Part 2: Static Nested Classes & Method Polymorphism Rules

### Static Nested Classes vs. Regular Inner Classes

The fundamental difference lies in **enclosing instance dependency**. A regular inner class is bound to a specific instance of the outer class, whereas a static nested class is independent and behaves like a top-level class packaged inside another for convenience.

| Feature | Static Nested Class | Regular Inner Class (Non-Static) |
| :--- | :--- | :--- |
| **Outer Instance Required?** | **No**. Can exist without an outer object. | **Yes**. Must be linked to an outer object. |
| **Outer Member Access** | Only **static** outer members. | Both **static and instance** outer members. |
| **Declaration of Static Fields**| **Allowed**. Can have its own static variables/methods. | **Forbidden** (prior to Java 16). |
| **Instantiation Syntax** | `Outer.Nested n = new Outer.Nested();` | `Outer.Inner i = new Outer().new Inner();` |

#### Code Example:
```java
class Outer {
    static int staticField = 10;
    int instanceField = 20;

    // 1. Static Nested Class
    static class StaticNested {
        void display() {
            System.out.println(staticField); // Allowed
            // System.out.println(instanceField); // ERROR: Cannot access non-static
        }
    }

    // 2. Regular Inner Class
    class RegularInner {
        void display() {
            System.out.println(staticField);   // Allowed
            System.out.println(instanceField); // Allowed
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Instantiate Static Nested Class (No Outer object needed)
        Outer.StaticNested nested = new Outer.StaticNested();

        // Instantiate Regular Inner Class (Requires an Outer object)
        Outer outerObj = new Outer();
        Outer.RegularInner inner = outerObj.new RegularInner();
    }
}
```

---

### Static Method Overloading and Overriding Rules

The rules governing polymorphism differ significantly when the `static` keyword is applied to methods.

#### 1. Overloading Static Methods (Allowed ✅)
You can overload static methods exactly like regular instance methods. As long as the **method signature differs** (different parameter types, number, or order), the class can house multiple static methods of the same name.

```java
class Calculator {
    public static int add(int a, int b) { return a + b; }
    public static double add(double a, double b) { return a + b; } // Overloaded
}
```

#### 2. Overriding Static Methods (Not Allowed ❌ — Method Hiding)
You **cannot** override a static method. If a subclass defines a static method with the exact same signature as a static method in the superclass, it is called **Method Hiding**, not overriding.

* **Compile-time binding**: Static methods are resolved at compile time based on the **reference type**, not runtime polymorphism based on the actual **object type**.

#### Code Example:
```java
class Parent {
    public static void display() {
        System.out.println("Static method in Parent");
    }
}

class Child extends Parent {
    // This HIDES the Parent method; it does NOT override it.
    // Adding @Override here will trigger a compilation error.
    public static void display() {
        System.out.println("Static method in Child");
    }
}

public class Test {
    public static void main(String[] args) {
        Parent p1 = new Parent();
        Parent p2 = new Child(); // Reference type Parent, Object type Child

        p1.display(); // Outputs: "Static method in Parent"
        p2.display(); // Outputs: "Static method in Parent" (Compile-time binding)
    }
}
```

