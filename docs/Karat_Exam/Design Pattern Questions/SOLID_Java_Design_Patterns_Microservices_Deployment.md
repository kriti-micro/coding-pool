# SOLID, Java Design Patterns & Microservices Deployment Patterns

## 1. SOLID Principles

SOLID helps make Java code maintainable, testable, loosely coupled and easy to extend.

### S — Single Responsibility Principle (SRP)

**Definition:** A class should have one responsibility and one reason to change.

Bad:
```java
class Employee {
    void calculateSalary() {}
    void saveToDatabase() {}
    void sendEmail() {}
}
```

Better:
```java
class SalaryCalculator {
    double calculateSalary(Employee employee) {
        return employee.getBasicSalary() * 1.2;
    }
}

class EmployeeRepository {
    void save(Employee employee) {}
}

class EmailService {
    void sendEmail(Employee employee) {}
}
```

**Interview:** Separate business logic, persistence and notification so a change in one area does not affect unrelated functionality.

---

### O — Open/Closed Principle (OCP)

**Definition:** Classes should be open for extension but closed for modification.

Bad:
```java
class PaymentService {
    void pay(String type) {
        if (type.equals("CARD")) {
            // card
        } else if (type.equals("UPI")) {
            // UPI
        }
    }
}
```

Better:
```java
interface PaymentProcessor {
    void pay(double amount);
}

class CardPayment implements PaymentProcessor {
    public void pay(double amount) {
        System.out.println("Card");
    }
}

class UpiPayment implements PaymentProcessor {
    public void pay(double amount) {
        System.out.println("UPI");
    }
}

class PaymentService {
    private final PaymentProcessor processor;

    PaymentService(PaymentProcessor processor) {
        this.processor = processor;
    }

    void pay(double amount) {
        processor.pay(amount);
    }
}
```

Adding PayPal means adding another implementation instead of modifying `PaymentService`.

---

### L — Liskov Substitution Principle (LSP)

**Definition:** A child class should be usable wherever its parent is expected without breaking expected behavior.

Bad:
```java
class Bird {
    void fly() {}
}

class Penguin extends Bird {
    @Override
    void fly() {
        throw new UnsupportedOperationException();
    }
}
```

Better:
```java
interface Bird {}

interface FlyingBird extends Bird {
    void fly();
}

class Sparrow implements FlyingBird {
    public void fly() {
        System.out.println("Flying");
    }
}

class Penguin implements Bird {}
```

**Interview:** If a subclass must violate assumptions of the parent, the abstraction is probably wrong.

---

### I — Interface Segregation Principle (ISP)

**Definition:** Clients should not be forced to depend on methods they do not need.

Bad:
```java
interface Worker {
    void work();
    void eat();
    void sleep();
}
```

Better:
```java
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Robot implements Workable {
    public void work() {}
}

class Human implements Workable, Eatable, Sleepable {
    public void work() {}
    public void eat() {}
    public void sleep() {}
}
```

**Interview:** Prefer small, focused interfaces over large interfaces containing unrelated methods.

---

### D — Dependency Inversion Principle (DIP)

**Definition:** High-level modules should not depend directly on low-level implementations. Both should depend on abstractions.

Bad:
```java
class OrderService {
    private MySQLOrderRepository repository =
        new MySQLOrderRepository();
}
```

Better:
```java
interface OrderRepository {
    void save(Order order);
}

class MySQLOrderRepository implements OrderRepository {
    public void save(Order order) {
        // MySQL
    }
}

class OrderService {
    private final OrderRepository repository;

    OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    void createOrder(Order order) {
        repository.save(order);
    }
}
```

Spring commonly applies this through dependency injection:

```java
@Service
class OrderService {
    private final OrderRepository repository;

    @Autowired
    OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

**Important:** Dependency Inversion is a principle; Dependency Injection is a technique used to implement it.

---

## SOLID Memory Trick

```text
S → Single Responsibility → One reason to change
O → Open/Closed → Extend without modifying stable code
L → Liskov Substitution → Child honors parent contract
I → Interface Segregation → Small focused interfaces
D → Dependency Inversion → Depend on abstractions
```

---

# 2. Java Design Patterns

A design pattern is a reusable solution to a common software-design problem.

```text
Design Patterns
├── Creational
├── Structural
└── Behavioral
```

## Creational Patterns

### Singleton

Ensures only one instance exists.

```java
enum DatabaseConnection {
    INSTANCE;

    public void connect() {
        System.out.println("Connected");
    }
}
```

Use carefully because global state can make testing harder. Spring beans are singleton-scoped by default within an application context.

---

### Factory

Centralizes object creation.

```java
interface Vehicle {
    void drive();
}

class Car implements Vehicle {
    public void drive() {
        System.out.println("Car");
    }
}

class Bike implements Vehicle {
    public void drive() {
        System.out.println("Bike");
    }
}

class VehicleFactory {
    static Vehicle create(String type) {
        if ("CAR".equals(type)) return new Car();
        if ("BIKE".equals(type)) return new Bike();
        throw new IllegalArgumentException("Unknown type");
    }
}
```

Usage:
```java
Vehicle vehicle = VehicleFactory.create("CAR");
vehicle.drive();
```

---

### Abstract Factory

Creates families of related objects.

```text
Windows UI Factory
 ├── Windows Button
 └── Windows Checkbox

Mac UI Factory
 ├── Mac Button
 └── Mac Checkbox
```

Useful when related objects need to be compatible.

---

### Builder

Useful for objects with many optional parameters.
```java
class User {

    private String name;
    private int age;
    private String email;

    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.email = builder.email;
    }

    static class Builder {

        private String name;
        private int age;
        private String email;

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder age(int age) {
            this.age = age;
            return this;
        }

        Builder email(String email) {
            this.email = email;
            return this;
        }

        User build() {
            return new User(this);
        }
    }
}
```
```java
User user = User.builder()
        .name("John")
        .age(30)
        .email("john@gmail.com")
        .country("India")
        .build();
```

Useful because it improves readability and avoids large constructors.

---

### Prototype

Creates a new object by copying an existing object.

```java
class Document implements Cloneable {
    String content;

    @Override
    public Document clone() {
        try {
            return (Document) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

Remember shallow vs deep copy.

---

## Structural Patterns

### Adapter

Makes incompatible interfaces work together.

```text
Application
    ↓
Adapter
    ↓
Legacy API
```

```java
interface PaymentProcessor {
    void pay(double amount);
}

class LegacyPayment {
    void makePayment(double amount) {
        System.out.println("Legacy payment");
    }
}

class PaymentAdapter implements PaymentProcessor {
    private final LegacyPayment legacyPayment;

    PaymentAdapter(LegacyPayment legacyPayment) {
        this.legacyPayment = legacyPayment;
    }

    public void pay(double amount) {
        legacyPayment.makePayment(amount);
    }
}
```

Useful for legacy systems and third-party APIs.

---

### Decorator

Adds behavior without modifying the original class.

```text
Coffee
  ↓
Milk Decorator
  ↓
Sugar Decorator
```

Java I/O uses this style:

```java
InputStream input =
    new BufferedInputStream(
        new FileInputStream("file.txt"));
```

---

### Facade

Provides a simple interface over a complex subsystem.

```text
OrderFacade
 ├── Inventory
 ├── Payment
 ├── Shipping
 └── Notification
```

Client simply calls:

```java
orderFacade.placeOrder(order);
```

---

### Proxy

Controls access to another object.

```text
Client
  ↓
Proxy
  ↓
Real Object
```

Can provide security, logging, caching or lazy loading.

Spring AOP commonly uses proxies.

Example:

```java
@Transactional
public void createOrder() {
    // business logic
}
```

---

## Behavioral Patterns

### Strategy ⭐

Allows behavior/algorithm to be changed dynamically.

```text
PaymentService
      ↓
PaymentStrategy
   /    |    Card   UPI  PayPal
```

```java
interface PaymentStrategy {
    void pay(double amount);
}

class CardPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Card");
    }
}

class UpiPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("UPI");
    }
}

class PaymentService {
    private final PaymentStrategy strategy;

    PaymentService(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    void pay(double amount) {
        strategy.pay(amount);
    }
}
```

This is a very important pattern for backend interviews because it commonly helps implement OCP.

---

### Observer

One object changes and multiple interested objects are notified.

```text
Order Created
   ├── Email
   ├── SMS
   └── Analytics
```

In distributed systems, Kafka/Pub/Sub/message brokers often provide a similar event-notification model.

---

### Template Method

Defines the skeleton of an algorithm while subclasses customize steps.

```java
abstract class DataProcessor {

    final void process() {
        read();
        transform();
        save();
    }

    abstract void read();
    abstract void transform();

    void save() {
        System.out.println("Saving");
    }
}
```

---

### Command

Encapsulates a request as an object.

```text
Button
  ↓
Command
  ↓
Receiver
```

Useful for undo/redo, queues and scheduled operations.

---

### Chain of Responsibility

Passes a request through multiple handlers.

```text
Request
   ↓
Authentication
   ↓
Validation
   ↓
Logging
   ↓
Controller
```

Servlet filters/interceptors are good Java/Spring examples.

---

## Design Pattern Quick Reference

| Pattern | Remember It As | Example |
|---|---|---|
| Singleton | One instance | Configuration |
| Factory | Object creation | Vehicle/Payment |
| Abstract Factory | Family of objects | UI components |
| Builder | Complex object creation | User/DTO |
| Prototype | Clone object | Expensive objects |
| Adapter | Incompatible interfaces | Legacy API |
| Decorator | Add behavior | Java I/O |
| Facade | Simplify subsystem | Order processing |
| Proxy | Control access | Spring AOP |
| Strategy | Swap behavior | Payment methods |
| Observer | Notify subscribers | Events |
| Template Method | Fixed algorithm skeleton | Data processing |
| Command | Request as object | Undo/queue |
| Chain of Responsibility | Pass through handlers | Filters |

---

# 3. Microservices Deployment Patterns

Deployment patterns describe how a new microservice version is released safely.

```text
Current → v1
New     → v2
```

The main question is:

> How do we move users from v1 to v2 safely?

---

## Rolling Deployment

Instances are gradually replaced.

```text
Before:
LB → v1 v1 v1 v1

During:
LB → v2 v1 v1 v1
LB → v2 v2 v1 v1
LB → v2 v2 v2 v1

After:
LB → v2 v2 v2 v2
```

**Advantages**
- No complete downtime
- Simple
- Common with Kubernetes
- No duplicate full environment

**Trade-off**
- v1 and v2 coexist
- APIs/database changes should be backward compatible

**Interview:** Use rolling deployment for normal releases when gradual replacement is sufficient.

---

## Blue-Green Deployment

Maintain two complete environments.

```text
             Load Balancer
                   │
             ┌─────┴─────┐
             │           │
           Blue        Green
            V1           V2
```

Initially:

```text
Users → BLUE v1
```

Deploy/test GREEN, then switch:

```text
Users → GREEN v2
```

If v2 fails:

```text
Users → BLUE v1
```

**Advantages**
- Very fast rollback
- Easy environment validation

**Trade-off**
- Approximately 2× infrastructure during deployment

---

## Canary Deployment

Send a small percentage of traffic to the new version.

```text
Load Balancer
    /        \
   95%       5%
   |         |
   v1       v2 
  Canary
```

Gradually:

```text
95/5
 ↓
90/10
 ↓
75/25
 ↓
50/50
 ↓
0/100
```

Monitor:
- Error rate
- Latency
- CPU
- HTTP 5xx
- Business metrics

If v2 fails:

```text
v2 → 0%
```

**Main advantage:** very small blast radius.

---

## A/B Testing

Different user groups receive different versions/features.

```text
Users
 ├── Group A → v1
 └── Group B → v2
```

Used mainly for business experimentation.

Compare:
- Conversion
- Click-through rate
- Revenue
- Engagement
- Latency

**Difference:** A/B is mainly business experimentation; Canary is mainly safe production rollout.

---

## Shadow Deployment

Production traffic is copied to the new version, but the new version's response is not returned to the user.

```text
Request
  ├── v1 → Real response → User
  └── v2 → Shadow response → Metrics
```

Useful for:
- Major rewrites
- Framework migrations
- Performance testing
- New algorithms

---

## Deployment Comparison

| Pattern | Main Purpose | Benefit | Trade-off |
|---|---|---|---|
| Rolling | Normal release | Simple, no full downtime | v1/v2 coexist |
| Blue-Green | Safe switch | Very fast rollback | Duplicate environment |
| Canary | Risk reduction | Small blast radius | Monitoring required |
| A/B | Experimentation | Compare business outcomes | Analysis complexity |
| Shadow | Production validation | Real traffic without user impact | Extra compute |

---

# 4. SOLID + Design Patterns Together

Patterns often help implement SOLID principles.

## Payment Example

Requirement:

```text
Card
UPI
PayPal
```

Poor design:

```java
if (type.equals("CARD")) {
    // ...
} else if (type.equals("UPI")) {
    // ...
} else if (type.equals("PAYPAL")) {
    // ...
}
```

Better:

```text
              PaymentService
                    |
             PaymentStrategy
             /      |      \           
             Card      UPI     PayPal
```

Use **Strategy Pattern**.

It supports:

```text
OCP → Add new payment methods without modifying service

DIP → PaymentService depends on interface

SRP → Each payment implementation handles one payment method
```

A Factory can select the strategy dynamically:

```text
Request
  ↓
PaymentFactory
  ├── CardPayment
  ├── UpiPayment
  └── PaypalPayment
```

---

# 5. SOLID vs Design Pattern

## SOLID

SOLID provides principles/guidelines.

It answers:

> How should I structure my classes?

Examples:

```text
SRP → One responsibility
DIP → Depend on abstractions
```

## Design Pattern

A design pattern provides a reusable solution to a common design problem.

Example:

```text
Problem:
Need different payment algorithms

Solution:
Strategy Pattern
```

Therefore:

```text
SOLID
  ↓
Principles / Guidelines
  ↓
Good OO design

Design Patterns
  ↓
Reusable solutions
  ↓
Implement good designs
```

---

# 6. Senior Interview Answer Framework

When asked about a principle or pattern, use:

```text
1. What problem does it solve?
        ↓
2. Which principle/pattern would I use?
        ↓
3. How does it work?
        ↓
4. What happens during failure?
        ↓
5. Benefits?
        ↓
6. Trade-offs?
        ↓
7. Real-world example?
```

Example:

> If Payment Service becomes slow or starts failing, continuously calling it can exhaust threads and connections in Order Service and cause cascading failure. I would use a circuit breaker with failure thresholds and time windows. Once the failure rate crosses the threshold, the circuit opens and calls fail fast. After a recovery period, it enters half-open and allows a few test requests. If those succeed, it closes again. I would combine this with timeouts, bounded retries and bulkheads.

---

# 7. Priority for an 8-Year Java/Spring Interview

## SOLID

Know all five well:

1. SRP
2. OCP
3. LSP
4. ISP
5. DIP

## Java Design Patterns

Highest priority:

1. Strategy
2. Factory
3. Builder
4. Singleton
5. Adapter
6. Decorator
7. Facade
8. Proxy
9. Observer
10. Template Method
11. Chain of Responsibility
12. Command

## Microservices Deployment

Know:

1. Rolling
2. Blue-Green
3. Canary
4. Shadow
5. A/B

---

# Final Mental Model

## SOLID

```text
S → One responsibility
O → Extend without modifying stable code
L → Child honors parent contract
I → Small focused interfaces
D → Depend on abstractions
```

## Design Patterns

```text
Object creation?
    → Factory / Builder / Singleton

Need to change behavior?
    → Strategy

Incompatible systems?
    → Adapter

Add behavior?
    → Decorator

Simplify complex subsystem?
    → Facade

Control access?
    → Proxy

Notify many consumers?
    → Observer

Fixed workflow?
    → Template Method

Pass request through handlers?
    → Chain of Responsibility
```

## Deployment

```text
Normal safe release?
    → Rolling

Fast rollback?
    → Blue-Green

Small production exposure?
    → Canary

Real traffic without user impact?
    → Shadow

Business experiment?
    → A/B
```

## One-Line Interview Memory

```text
SOLID = principles for maintainable OO code

Design Patterns = reusable solutions to common design problems

Deployment Patterns = safe ways to release new application versions
```
