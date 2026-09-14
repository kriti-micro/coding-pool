# Microservices Architecture — Level 5 Java Developer Interview Guide

## 1. What are the main challenges of a microservices architecture?

### Interview Question

> What are the main challenges of a microservices architecture?

### Interview-Ready Answer

> The main challenges are service discovery, distributed logging and tracing, network latency and partial failures, data consistency across services, and increased operational complexity. For service discovery we can use Eureka or Consul. For distributed tracing and logging we use correlation IDs and tracing systems. For network failures we use timeouts, controlled retries and circuit breakers such as Resilience4j. Since each service may have its own database, maintaining consistency may require eventual consistency and patterns such as SAGA.

---

## 1.1 Service Discovery

Services need to find each other dynamically.

```text
Order Service
     |
     | Where is Payment Service?
     v
+------------------+
| Service Registry |
| Eureka / Consul  |
+------------------+
     |
     | Payment Service
     | 10.0.0.15:8082
     v
Payment Service
```

Without discovery:

```text
Order Service → http://10.0.0.15:8082
```

This is fragile if the Payment Service moves or scales.

With discovery:

```text
Payment Service
      |
      | Register
      v
Eureka / Consul
      ^
      |
      | Discover
      |
Order Service
```

**Key term:** Service Discovery.

---

## 1.2 Distributed Logging and Tracing

A single request can cross many services:

```text
User
 |
 v
API Gateway
 |
 v
Order Service
 |
 v
Payment Service
 |
 v
Notification Service
```

If Payment fails, we need to know which request and where it failed.

### Correlation ID

Generate an ID such as:

```text
Correlation ID = abc-123
```

Pass it through every service:

```text
Client
  |
  | abc-123
  v
Gateway
  |
  | abc-123
  v
Order Service
  |
  | abc-123
  v
Payment Service
```

Logs can then be searched using `abc-123`.

### Distributed tracing

```text
Request abc-123
|
+-- Gateway       20 ms
|
+-- Order         80 ms
|    |
|    +-- DB       30 ms
|
+-- Payment      500 ms
     |
     +-- External API 450 ms
```

This helps identify latency bottlenecks.

---

# 2. Network Latency and Partial Failures

In a monolith:

```text
Method A → Method B
```

In microservices:

```text
Service A
   |
   | Network call
   v
Service B
```

The network can fail.

Possible problems:

- Timeout
- Connection failure
- DNS failure
- Service unavailable
- Network congestion
- Slow downstream service

---

# 3. Circuit Breaker Pattern

**Circuit Breaker is a high-value Level 5 interview term.**

Suppose:

```text
Order Service
      |
      v
Payment Service
```

Payment is down.

Without a circuit breaker:

```text
Request 1 → Payment → timeout
Request 2 → Payment → timeout
Request 3 → Payment → timeout
Request 4 → Payment → timeout
...
```

This can exhaust threads/connections and cause cascading failures.

With a circuit breaker:

```text
Order Service
      |
      v
Circuit Breaker
      |
      X
Payment Service DOWN

      |
      v
Fail Fast / Fallback
```

## Circuit Breaker States

```text
          failures
CLOSED ----------------> OPEN
  ^                       |
  |                       | wait
  |                       v
  +---------------- HALF-OPEN
          success / failure
```

### CLOSED

Normal calls flow through.

### OPEN

Too many failures occurred. Calls are rejected quickly.

### HALF-OPEN

After a recovery period, a limited test request is allowed.

```text
HALF-OPEN
    |
    v
Test downstream
  /       \
OK       FAIL
 |          |
 v          v
CLOSED     OPEN
```

### Java Example — Resilience4j

```java
@CircuitBreaker(
    name = "paymentService",
    fallbackMethod = "paymentFallback"
)
public PaymentResponse makePayment(PaymentRequest request) {
    return paymentClient.pay(request);
}

public PaymentResponse paymentFallback(
        PaymentRequest request,
        Exception ex) {

    return new PaymentResponse(
        "PAYMENT_SERVICE_UNAVAILABLE"
    );
}
```

### Important

Circuit breaker is not a replacement for:

- Timeouts
- Controlled retries
- Monitoring
- Proper error handling

Aggressive retries can create a **retry storm** and make an unhealthy service even worse.

---

# 4. Data Consistency Across Services

In a monolith:

```text
Order
Payment
Inventory
     |
     v
One database transaction
```

In microservices:

```text
Order Service       → Order DB

Payment Service     → Payment DB

Inventory Service   → Inventory DB
```

A single simple ACID transaction may not span all these independent databases.

---

# 5. Eventual Consistency

One approach is event-driven communication:

```text
Order Service
     |
     | OrderCreated
     v
Message Broker
     |
     +------------+
     |            |
     v            v
Payment       Inventory
Service       Service
```

The services may temporarily have different states, but after events are processed they converge.

This is:

> **Eventual consistency**

---

# 6. SAGA Pattern

**SAGA is another high-value Level 5 term.**

Suppose the workflow is:

```text
Create Order
     |
     v
Reserve Inventory
     |
     v
Process Payment
     |
     v
Confirm Order
```

What if payment fails?

With separate databases, we cannot simply perform one global `ROLLBACK`.

Instead, use a compensating action:

```text
Create Order
     |
     v
Reserve Inventory
     |
     v
Payment FAILED
     |
     v
Release Inventory
     |
     v
Cancel Order
```

Conceptually:

```text
Local Transaction
       +
Compensating Transaction
```

## SAGA: Choreography

Services react to events.

```text
Order Service
     |
 OrderCreated
     v
Payment Service
     |
PaymentFailed
     v
Inventory Service
```

There is no central coordinator.

## SAGA: Orchestration

A central orchestrator controls the workflow.

```text
             SAGA Orchestrator
              /      |       \
             v       v        v
          Order   Inventory  Payment
```

### Choreography vs Orchestration

| | Choreography | Orchestration |
|---|---|---|
| Controller | No central controller | Central orchestrator |
| Communication | Events | Commands/calls |
| Simple workflow | Good | Good |
| Complex workflow | Can become difficult to understand | Easier to visualize |
| Debugging | Can be harder | Central workflow visibility |

---

# 7. Increased Operational Complexity

Monolith:

```text
Application
    |
    v
Database
```

Microservices:

```text
              Gateway
                 |
       +---------+---------+
       |         |         |
       v         v         v
    Order     Payment   Inventory
       |         |         |
       v         v         v
      DB        DB        DB
```

Now we need to manage:

- Deployment
- Service discovery
- Configuration
- Secrets
- Monitoring
- Logging
- Distributed tracing
- Scaling
- Health checks
- CI/CD
- Network security
- Failure handling

This is a major trade-off of microservices.

---

# 8. How do Two Microservices Communicate?

## Interview Question

> How do two microservices communicate? What are the trade-offs?

### Interview-Ready Answer

> Microservices can communicate synchronously or asynchronously. Synchronous communication commonly uses REST/HTTP or gRPC, where the caller waits for a response. REST is simple and widely supported, while gRPC provides strong contracts and efficient binary communication. Asynchronous communication can use Kafka or RabbitMQ, where services communicate through messages or events. Async communication provides stronger decoupling and resilience but makes tracing, ordering, retries and debugging more complex. I choose based on whether an immediate response is required, throughput, coupling and failure-handling requirements.

---

# 9. Synchronous Communication

The caller waits.

```text
Order Service
     |
     | HTTP request
     v
Payment Service
     |
     | Response
     v
Order Service
```

---

# 10. REST / HTTP

Example:

```http
POST /payments
```

Advantages:

- Simple
- Widely supported
- Easy to debug
- Natural for user-facing APIs

Disadvantages:

- Caller depends on downstream availability
- Network latency
- Runtime coupling
- Requires timeout/retry/circuit-breaker handling

---

# 11. gRPC

```text
Order Service
     |
     | gRPC
     v
Payment Service
```

Example contract:

```protobuf
service PaymentService {
  rpc ProcessPayment(PaymentRequest)
      returns (PaymentResponse);
}
```

Advantages:

- Strongly typed contract
- Efficient binary serialization
- Good performance
- Good for internal service communication

Disadvantages:

- More complex than basic REST
- Requires compatible tooling
- Browser/client integration may require additional considerations

---

# 12. Asynchronous Communication

The caller does not have to wait for processing to finish.

```text
Order Service
     |
     | OrderCreated
     v
+------------------+
| Message Broker   |
| Kafka/RabbitMQ   |
+------------------+
      |         |
      v         v
 Payment    Notification
 Service      Service
```

This is useful for event-driven workflows and fire-and-forget operations.

---

# 13. REST vs gRPC vs Kafka/RabbitMQ

| Factor | REST/HTTP | gRPC | Kafka/RabbitMQ |
|---|---|---|---|
| Communication | Sync | Sync | Async |
| Caller waits | Yes | Yes | Usually no |
| Simplicity | High | Medium | Lower |
| Strong typing | API/schema dependent | Strong | Schema-dependent |
| Decoupling | Lower | Lower | Higher |
| Immediate response | Excellent | Excellent | Usually no |
| Event-driven workflow | Not ideal | Not ideal | Excellent |
| Debugging | Easier | Moderate | More complex |
| Failure isolation | Lower | Lower | Higher |

---

# 14. When to Choose Synchronous Communication

Use synchronous communication when an immediate response is required.

Example:

```text
User
 |
 v
Order API
 |
 v
Get Order Details
 |
 v
Immediate Response
```

REST or gRPC can be appropriate.

---

# 15. When to Choose Asynchronous Communication

Use async when:

- Fire-and-forget is acceptable
- High throughput is required
- Processing can happen later
- Event-driven architecture is appropriate
- Services should be more loosely coupled

Example:

```text
Order Created
      |
      v
Publish Event
      |
      +---- Email
      +---- Analytics
      +---- Notification
      +---- Recommendation
```

---

# 16. What is an API Gateway?

## Interview Question

> What is an API Gateway and why would you use it?

### Interview-Ready Answer

> An API Gateway is a single entry point between clients and internal microservices. It can handle request routing, authentication and authorization, rate limiting, TLS termination, logging and other cross-cutting concerns. It also hides the internal service topology from clients. However, the gateway can become a bottleneck or single point of failure if it is not deployed with high availability, so it should normally be horizontally scaled and deployed redundantly.

---

# 17. Without API Gateway

```text
                 Client
            /       |       \
           v        v        v
      Order API  Payment API  User API
```

The client knows the internal services.

This can lead to:

- More endpoints for clients
- Duplicated cross-cutting logic
- Internal topology exposure
- Harder service evolution

---

# 18. With API Gateway

```text
                  Client
                    |
                    v
             +-------------+
             | API Gateway |
             +-------------+
              /     |      \
             v      v       v
          Order   Payment   User
         Service  Service  Service
```

The client only needs the gateway endpoint.

The gateway routes requests internally.

---

# 19. API Gateway Responsibilities

```text
                    API Gateway
                         |
       +-----------------+-----------------+
       |        |        |        |        |
       v        v        v        v        v
   Routing    Auth    Rate     TLS      Logging
                     Limit
```

Common responsibilities:

- Request routing
- Authentication
- Authorization
- Rate limiting
- TLS/SSL termination
- Logging
- Request/response transformation
- API version routing
- CORS
- Observability hooks

Avoid putting core business logic into the gateway.

---

# 20. Request Routing Example

```text
GET /api/orders/101
          |
          v
      API Gateway
          |
          | route
          v
    Order Service
```

Another request:

```text
POST /api/payments
          |
          v
      API Gateway
          |
          | route
          v
   Payment Service
```

---

# 21. Authentication and Authorization

```text
Client
   |
   v
Gateway
   |
   +-- Validate JWT
   +-- Authentication
   +-- Authorization
   |
   v
Internal Service
```

Example:

```http
Authorization: Bearer <JWT>
```

The gateway can validate the token before routing.

**Important:** The gateway should not necessarily be the only security layer. Internal services should also have appropriate service-to-service security and authorization.

---

# 22. Rate Limiting

Suppose:

```text
Client
  |
  | 10,000 requests/sec
  v
Gateway
```

The gateway can enforce:

```text
Client
   |
   v
Rate Limiter
   |
   +-- Allowed → Service
   |
   +-- Exceeded → 429 Too Many Requests
```

This protects downstream services.

---

# 23. TLS Termination

```text
Client
  |
  | HTTPS
  v
API Gateway
  |
  | Internal communication
  v
Microservice
```

The gateway can terminate TLS and centralize certificate management, depending on the architecture.

---

# 24. API Gateway High Availability

Bad:

```text
Client
   |
   v
Gateway
   X
Single Point of Failure
```

Better:

```text
                 Load Balancer
                       |
             +---------+---------+
             |                   |
             v                   v
        Gateway-1           Gateway-2
             |                   |
             +---------+---------+
                       |
                Microservices
```

Deploy multiple gateway instances with health checks and load balancing.

---

# 25. API Gateway Trade-Offs

## Advantages

```text
+------------------------------+
| API Gateway                  |
+------------------------------+
| Single client entry point    |
| Centralized cross-cutting    |
| Security                     |
| Rate limiting                |
| Routing                      |
| Observability                |
| Hides internal topology      |
+------------------------------+
```

## Disadvantages

### 1. Extra network hop

```text
Client → Gateway → Service
```

### 2. Possible bottleneck

```text
Many Clients
     |
     v
  Gateway
     |
     v
Services
```

### 3. Single point of failure

If only one gateway instance exists.

### 4. Configuration complexity

Many routes, policies and versions must be managed.

### 5. Business-logic risk

Putting too much business logic into the gateway makes it a new monolith.

### Solution

```text
             Load Balancer
                    |
            +-------+-------+
            |               |
        Gateway-1       Gateway-2
```

Use HA and keep gateway responsibilities focused on cross-cutting concerns.

---

# 26. Complete Microservices Architecture

```text
                         Clients
                            |
                            v
                    +---------------+
                    | Load Balancer |
                    +---------------+
                            |
                            v
                    +---------------+
                    | API Gateway   |
                    +---------------+
                      |     |      |
                      v     v      v
                   Order  Payment  User
                  Service Service Service
                     |       |       |
                     v       v       v
                    DB      DB      DB

                 +----------------------+
                 | Service Discovery    |
                 | Eureka / Consul      |
                 +----------------------+

                 +----------------------+
                 | Message Broker       |
                 | Kafka / RabbitMQ     |
                 +----------------------+

                 +----------------------+
                 | Observability        |
                 | Logs + Traces +      |
                 | Metrics              |
                 +----------------------+
```

---

# 27. Complete Order Example

Suppose a user places an order.

### Step 1 — Client

```text
POST /orders
```

### Step 2 — API Gateway

```text
Authenticate
Rate Limit
Route
Propagate Correlation ID
```

### Step 3 — Order Service

```text
Create Order
```

### Step 4 — Payment

Synchronous:

```text
Order Service
      |
      | REST/gRPC
      v
Payment Service
```

Or asynchronous:

```text
Order Service
      |
      | OrderCreated
      v
Kafka
      |
      v
Payment Service
```

### Step 5 — Failure Handling

```text
Order
  |
  v
Payment
  X
  |
  v
Circuit Breaker
  |
  v
Fallback / Retry / Recovery
```

### Step 6 — Distributed Consistency

If inventory was reserved but payment failed:

```text
Reserve Inventory
       |
Payment Failed
       |
       v
Compensating Action
       |
       v
Release Inventory
```

This is where SAGA can be useful.

---

# 28. High-Value Follow-Up Questions

## Q1. Why is microservices harder than a monolith?

Because a local method call becomes a distributed network call.

```text
Network
+
Service Discovery
+
Timeouts
+
Retries
+
Circuit Breakers
+
Distributed Transactions
+
Distributed Logging
+
Deployment Complexity
```

---

## Q2. Why is a circuit breaker needed if we already have retries?

They solve different problems.

```text
Retry
→ Try again for a transient failure.

Circuit Breaker
→ Stop repeatedly calling an unhealthy dependency.
```

Bad retry strategy:

```text
1000 requests
   |
   v
Payment DOWN
   |
   +-- retry
   +-- retry
   +-- retry
   ...
```

This can create a retry storm.

---

## Q3. Why can't we use one normal DB transaction across microservices?

Because services may own independent databases:

```text
Order DB
Payment DB
Inventory DB
```

A normal local transaction does not automatically make all three one atomic transaction.

SAGA can coordinate local transactions using compensating actions.

---

## Q4. What happens if a message is processed twice?

Design consumers to be **idempotent** where possible.

```text
Event ID = 12345
       |
       v
Consumer
       |
       v
Already processed?
   /          \
 Yes          No
  |            |
Ignore       Process
```

This prevents duplicate effects such as charging a customer twice.

---

## Q5. What if Kafka is temporarily unavailable?

Possible approaches:

```text
Producer
   |
   X Kafka unavailable
   |
   +-- Controlled retry
   +-- Persistent outbox
   +-- Alerting
```

### Transactional Outbox

```text
Order Service
     |
     +---- Order DB
     |       |
     |       +-- orders
     |       +-- outbox_events
     |
     v
Outbox Publisher
     |
     v
Kafka
```

This helps solve:

```text
DB transaction succeeds
        +
Kafka publish fails
        =
Database and event become inconsistent
```

---

# 29. REST vs gRPC vs Kafka — Decision Guide

```text
Need immediate response?
        |
       YES
        |
   +----+----+
   |         |
 REST       gRPC
   |
Simple/public API

Need decoupling/event-driven processing?
        |
       YES
        |
      Kafka/
     RabbitMQ
```

The choice depends on business requirements, latency, throughput, coupling, reliability and operational complexity.

---

# 30. One-Minute Level 5 Interview Answer

> "The main challenges of microservices are distributed communication, service discovery, observability, partial failures, data consistency and operational complexity. For service discovery I can use Eureka or Consul. For observability I use correlation IDs, centralized logging and distributed tracing. For network failures I use timeouts, controlled retries and a circuit breaker such as Resilience4j. Since services may have separate databases, I use eventual consistency and patterns such as SAGA when a business transaction spans multiple services.
>
> For communication, I choose synchronous REST or gRPC when the caller needs an immediate response, and asynchronous messaging through Kafka or RabbitMQ when I need decoupling, high throughput or event-driven processing. The trade-off is that asynchronous communication makes tracing, ordering, retries and debugging more complex.
>
> An API Gateway provides a single entry point for clients and can centralize routing, authentication, authorization, rate limiting, TLS termination and observability. I would deploy multiple gateway instances behind a load balancer so the gateway is not a single point of failure. I would also keep business logic out of the gateway."

---

# 31. Key Terms to Memorize

```text
Service Discovery
Eureka
Consul
Correlation ID
Distributed Tracing
Circuit Breaker
Resilience4j
Timeout
Retry
Retry Storm
Eventual Consistency
SAGA
Choreography
Orchestration
REST
gRPC
Kafka
RabbitMQ
API Gateway
Rate Limiting
TLS Termination
High Availability
Idempotency
Transactional Outbox
```

## Most Important Level 5 Terms

> **Circuit Breaker** — stops repeated calls to an unhealthy downstream service and helps prevent cascading failures.

> **SAGA** — manages a distributed business transaction using local transactions and compensating actions.

> **Correlation ID** — identifier propagated across services so one request can be traced through distributed logs.

> **API Gateway** — single client-facing entry point that handles routing and cross-cutting concerns.

> **Polyglot communication** — choose REST, gRPC or asynchronous messaging based on the communication requirement rather than forcing one technology everywhere.
