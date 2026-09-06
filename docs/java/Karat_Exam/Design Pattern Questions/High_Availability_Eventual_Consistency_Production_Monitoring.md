# High Availability, Eventual Consistency & Production Monitoring
## Level 5 Java Backend / Microservices Interview Guide

This guide covers three scenario-based interview questions with examples, diagrams, trade-offs, Java/Resilience4j examples, and interview-ready answers.

---

# 1. How do you design a system that remains available when a downstream service fails?

## Interview Question

> How do you design a system that remains available when a downstream service fails?

## Interview-Ready Answer

> I would design the service for graceful degradation. I would use a timeout so the caller never waits indefinitely, controlled retries with exponential backoff for transient failures, and a circuit breaker such as Resilience4j to stop repeatedly calling an unhealthy dependency. I would also provide a safe fallback such as cached/default data where appropriate. For resource isolation, I would use the bulkhead pattern so one dependency cannot consume all application resources and bring down unrelated functionality.

## Example — E-Commerce Product Page

```text
User
 |
 v
Product Service
 |
 +----> Product DB
 |
 +----> Recommendation Service
 |
 +----> Review Service
```

If Recommendation Service becomes slow:

```text
Many Users
    |
    v
Product Service
    |
    +----> Recommendation Service
    |          X
    |       Very slow
    |
    v
Threads waiting
    |
    v
Thread Pool exhausted
    |
    v
Product Service becomes unavailable
```

This is a **cascading failure**.

The goal is:

```text
Recommendation Service DOWN
          |
          v
Product Page still works
          |
          v
Show product + cached/default recommendations
```

---

# 1.1 Resilience Design

```text
                         Request
                            |
                            v
                     Product Service
                            |
                     +------+------+
                     |             |
                     v             v
              Resilience Layer   Product DB
                     |
      +--------------+---------------+
      |        |       |       |     |
      v        v       v       v     v
   Timeout   Retry  Circuit  Fallback Bulkhead
                     Breaker
                       |
                       v
              Recommendation Service
```

The five important mechanisms are:

1. Timeout
2. Retry with exponential backoff
3. Circuit breaker
4. Fallback / graceful degradation
5. Bulkhead

---

# 1.2 Timeout

Without a timeout:

```text
Product Service
      |
      | Request
      v
Recommendation Service
      |
      | waiting...
      | waiting...
      | waiting...
```

With a timeout:

```text
Product Service
      |
      | Request
      v
Recommendation Service
      |
      | No response
      X
   Timeout
      |
      v
Fallback
```

Example:

```text
Recommendation timeout = 500 ms
```

If the dependency does not respond within the configured limit, stop waiting and execute the fallback.

**Interview point:** A timeout prevents indefinite waiting and protects application resources.

---

# 1.3 Retry with Exponential Backoff

Retries can help with transient failures.

Bad approach:

```text
Failure
  |
  +--> Retry immediately
  +--> Retry immediately
  +--> Retry immediately
```

If the dependency is overloaded, this creates more traffic and can cause a **retry storm**.

Better:

```text
Attempt 1 → fail
      |
      | wait 100 ms
      v
Attempt 2 → fail
      |
      | wait 200 ms
      v
Attempt 3 → fail
      |
      | wait 400 ms
      v
Attempt 4
```

Conceptually:

```text
delay = baseDelay × 2^attempt
```

Use **jitter** so thousands of clients do not retry at exactly the same time.

Retry only failures that are likely to be transient and safe to retry.

```text
Temporary network failure → possibly retry
Temporary 503             → possibly retry
Invalid request            → don't retry
Authentication failure     → don't retry
Business validation error  → don't retry
```

---

# 1.4 Circuit Breaker

Retries alone are not enough when a service is persistently unhealthy.

Without circuit breaker:

```text
Request → Payment → timeout
Request → Payment → timeout
Request → Payment → timeout
Request → Payment → timeout
...
```

With circuit breaker:

```text
Product Service
      |
      v
Circuit Breaker
      |
      X
Recommendation Service DOWN
      |
      v
Fallback
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

The dependency is considered unhealthy. Calls fail fast instead of repeatedly hitting the dependency.

### HALF-OPEN

After a recovery period, a limited test request is allowed.

```text
HALF-OPEN
    |
    v
Test downstream
  /       \
Success   Failure
  |          |
  v          v
CLOSED      OPEN
```

## Java Example — Resilience4j

**Resilience4j** is a commonly used Java library for resilience patterns.

```java
@CircuitBreaker(
    name = "recommendationService",
    fallbackMethod = "recommendationFallback"
)
public RecommendationResponse getRecommendations(String userId) {

    return recommendationClient.getRecommendations(userId);
}

public RecommendationResponse recommendationFallback(
        String userId,
        Exception ex) {

    return RecommendationResponse.defaultRecommendations();
}
```

Example configuration concept:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      recommendationService:
        failureRateThreshold: 50
        slidingWindowSize: 10
        waitDurationInOpenState: 10s

  retry:
    instances:
      recommendationService:
        maxAttempts: 3
        waitDuration: 100ms
```

The actual values should be chosen based on the dependency and workload.

---

# 1.5 Fallback / Graceful Degradation

The principle is:

> If a non-critical dependency fails, return a useful response instead of failing the entire request.

Normal page:

```text
Product Page
 |
 +-- Product information
 +-- Price
 +-- Stock
 +-- Recommendations
 +-- Reviews
```

Recommendation service fails:

```text
Product Page
 |
 +-- Product information ✓
 +-- Price              ✓
 +-- Stock              ✓
 +-- Recommendations   X
 +-- Reviews            ✓
```

Possible fallbacks:

```text
Failure
  |
  +--> Cached response
  |
  +--> Default response
  |
  +--> Stale-but-acceptable data
  |
  +--> Simplified response
```

Example:

```json
{
  "product": "Laptop",
  "price": 75000,
  "stock": 12,
  "recommendations": []
}
```

This is graceful degradation.

**Important:** For critical operations such as payment, never return a false success simply because the downstream service failed. The fallback must preserve business correctness.

---

# 1.6 Bulkhead Pattern

The idea comes from ships: separate compartments prevent one flooded area from sinking the whole ship.

```text
+---------+---------+---------+
| Comp 1  | Comp 2  | Comp 3  |
+---------+---------+---------+
```

In software:

Without bulkhead:

```text
                Shared Thread Pool
              +--------------------+
Requests ---> | 100 threads        |
              +--------------------+
                    |
                    v
          Recommendation Service
                    X
                 VERY SLOW

All threads become occupied
          |
          v
Other APIs also fail
```

With bulkhead:

```text
                Product Service
                      |
          +-----------+-----------+
          |                       |
          v                       v
   Recommendation Pool       Payment Pool
      20 threads               30 threads
          |                       |
          v                       v
   Recommendation             Payment
      Service                   Service
```

If Recommendation Service becomes slow, its resource pool can become exhausted without necessarily exhausting resources reserved for Payment.

**Interview point:** Circuit breaker stops calls; bulkhead isolates resources.

---

# 1.7 Complete Failure-Handling Flow

```text
                         Request
                            |
                            v
                     Product Service
                            |
                            v
                     Circuit Breaker
                            |
                            v
                          Timeout
                            |
                            v
                    Recommendation
                       Service
                            |
                  +---------+---------+
                  |                   |
                Success             Failure
                  |                   |
                  v                   v
               Response           Retry?
                                      |
                              +-------+-------+
                              |               |
                           Transient       Persistent
                              |               |
                              v               v
                            Retry        Circuit OPEN
                                              |
                                              v
                                           Fallback
                                              |
                                              v
                                      Cached/default data
```

---

# 1.8 Resilience Pattern Summary

| Pattern | Main Purpose |
|---|---|
| Timeout | Prevent indefinite waiting |
| Retry | Handle transient failures |
| Exponential Backoff | Avoid hammering the dependency |
| Circuit Breaker | Stop calls to an unhealthy dependency |
| Fallback | Provide graceful degradation |
| Bulkhead | Isolate resource usage/failures |

Easy memory trick:

```text
Timeout
→ Don't wait forever

Retry
→ Try again carefully

Circuit Breaker
→ Stop calling unhealthy service

Fallback
→ Return something useful

Bulkhead
→ Don't let one failure consume everything
```

---

# 2. What is Eventual Consistency?

## Interview Question

> What is eventual consistency? Give a real-world example.

## Interview-Ready Answer

> Eventual consistency means that after a successful update, different replicas or services may temporarily have different views of the data, but if updates stop and the system continues operating, the replicas eventually converge to the same state. It is common in distributed systems where availability, scalability or latency is prioritized. For example, after a product purchase, inventory displayed in different regions may take some time to converge. It can be acceptable for feeds, search indexes or some catalogue views, but authoritative financial state generally requires stronger consistency guarantees.

## Example

Initial inventory:

```text
Stock = 10
```

Customer buys 2:

```text
Authoritative system → Stock = 8
```

Temporarily:

```text
Region A → 8
Region B → 10
Region C → 10
```

After replication:

```text
Region A → 8
Region B → 8
Region C → 8
```

The replicas eventually converge.

---

# 2.1 Eventual Consistency Diagram

```text
                 Write
                  |
                  v
            Primary System
                  |
               Stock = 8
                  |
          Replication/Event
          /       |       \
         v        v        v
      Region A Region B Region C
         8        10       10
                   |
             eventually
                   v
      Region A   Region B   Region C
          8          8          8
```

---

# 2.2 Real-World Example — Social Media

A user creates a post:

```text
"Hello World"
```

Immediately:

```text
India → visible
US → not yet visible
Europe → not yet visible
```

After asynchronous propagation:

```text
India → visible
US → visible
Europe → visible
```

For many social-feed scenarios, this delay is acceptable.

---

# 2.3 When Is Eventual Consistency Acceptable?

Examples:

- Social feeds
- Search indexes
- Recommendations
- Analytics
- Product catalogue views
- Some counters and aggregated views
- Some shopping-cart use cases

The exact choice depends on the business requirement.

---

# 2.4 When Is Stronger Consistency Required?

Examples:

- Bank balance
- Authoritative payment status
- Financial ledger
- Account debit/credit
- Critical inventory reservation

Example:

```text
Bank Balance = ₹50,000
```

We should not allow different authoritative transaction views to cause a customer to spend money based on stale state.

---

# 2.5 ACID

ACID describes transaction properties:

```text
A → Atomicity
C → Consistency
I → Isolation
D → Durability
```

### Atomicity

All operations happen or none happen.

```text
Debit ₹100
+
Credit ₹100
```

A failure should not leave an unintended partial transaction.

### Consistency

A transaction moves data between valid states according to defined constraints.

### Isolation

Concurrent transactions should not incorrectly interfere with each other.

### Durability

Committed data survives failures according to the system's durability guarantees.

---

# 2.6 BASE

BASE is commonly associated with highly available distributed systems:

```text
BA → Basically Available
S  → Soft State
E  → Eventually Consistent
```

### Basically Available

The system aims to remain available, potentially returning a degraded or temporarily stale result.

### Soft State

State can change over time because of asynchronous propagation.

### Eventually Consistent

Replicas converge after updates propagate.

---

# 2.7 ACID vs BASE

| | ACID | BASE |
|---|---|---|
| Main focus | Transaction correctness | Availability/scalability/flexible consistency |
| Consistency | Strong transactional guarantees | Eventual consistency may be used |
| Transactions | Strong transaction semantics | Often distributed differently |
| Typical use | Financial/transactional workloads | Large distributed/read-oriented workloads |
| Availability trade-off | May reject/fail when consistency cannot be maintained | Often favors availability |
| Convergence | Transactionally controlled | May converge asynchronously |

**Important:** Do not simply say "ACID = relational and BASE = NoSQL." These are different concepts. ACID/BASE describe system and data behavior; relational/NoSQL describe data models and database technologies.

---

# 3. How Would You Monitor a Backend Service in Production?

## Interview Question

> How would you monitor a backend service in production?

## Interview-Ready Answer

> I would monitor the service using metrics, structured logs, distributed tracing, alerting and health checks. For metrics, I would track traffic, error rate, latency such as p50, p95 and p99, and resource saturation. Prometheus and Grafana are common choices. For logs, I would use structured JSON logs with correlation IDs and centralize them using a stack such as ELK. For distributed requests, I would use OpenTelemetry with a tracing backend such as Jaeger or Zipkin. I would configure alerts around meaningful service objectives and expose liveness/readiness health checks for load balancers or orchestration platforms.

---

# 3.1 The Four Golden Signals

A very important interview term:

```text
1. Latency
2. Traffic
3. Errors
4. Saturation
```

---

# 3.2 Golden Signal — Latency

How long does a request take?

Example:

```text
p50 = 100 ms
p95 = 300 ms
p99 = 800 ms
```

Average latency can hide slow requests.

Example:

```text
99 requests → 100 ms
1 request  → 10 seconds
```

Percentiles show tail latency.

---

# 3.3 Golden Signal — Traffic

How much demand is the service receiving?

Examples:

```text
Requests/sec
Requests/minute
Transactions/sec
Messages/sec
```

Example:

```text
10 AM → 500 req/sec
11 AM → 2,000 req/sec
12 PM → 5,000 req/sec
```

A sudden increase may require scaling or investigation.

---

# 3.4 Golden Signal — Errors

Monitor:

```text
HTTP 5xx
HTTP 4xx
Timeouts
Failed database operations
Failed message processing
```

Example:

```text
Total requests = 100,000
Errors = 2,000

Error rate = 2%
```

A sudden increase should trigger investigation according to service objectives.

---

# 3.5 Golden Signal — Saturation

How close is the system to capacity?

Monitor:

```text
CPU
Memory
Thread pool
Connection pool
Disk
Queue depth
Kafka consumer lag
```

Example:

```text
CPU              → 90%
DB connections   → 95%
Thread pool      → 98%
```

The service may still respond but is approaching capacity.

---

# 3.6 Metrics — Prometheus + Grafana

Typical architecture:

```text
                    Application
                         |
                         | metrics
                         v
                    Prometheus
                         |
                         v
                      Grafana
                         |
                         v
                     Dashboard
```

Example dashboard:

```text
Request Rate       2,500 req/sec
Error Rate         0.8%
p95 Latency        250 ms
p99 Latency        700 ms
CPU                65%
Memory             70%
DB Pool            55%
```

---

# 3.7 Structured Logging

Bad:

```text
Payment failed
```

Better:

```json
{
  "timestamp": "2026-09-04T15:00:00Z",
  "level": "ERROR",
  "service": "payment-service",
  "correlationId": "abc-123",
  "orderId": "ORD-101",
  "event": "payment_failed",
  "errorCode": "TIMEOUT"
}
```

Structured logging makes filtering and aggregation easier.

---

# 3.8 Centralized Logging

```text
Order Service       ──┐
Payment Service      ──┤
Inventory Service    ──┼──> Centralized Logging
Notification Service ──┘
```

A commonly known stack is:

```text
ELK

E → Elasticsearch
L → Logstash
K → Kibana
```

Other centralized logging platforms are also possible.

---

# 3.9 Correlation ID

Request flow:

```text
Gateway
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

Assign:

```text
Correlation ID = abc-123
```

Every service logs the same ID.

Then engineers can search:

```text
correlationId = abc-123
```

and reconstruct the request path.

---

# 3.10 Distributed Tracing

Tracing shows where request time is spent.

```text
User Request
|
+-- API Gateway          20 ms
|
+-- Order Service        80 ms
|     |
|     +-- DB             30 ms
|
+-- Payment Service     500 ms
      |
      +-- External API  450 ms
```

Useful technologies include:

- OpenTelemetry
- Jaeger
- Zipkin

---

# 3.11 Alerting

Create alerts around meaningful symptoms.

Examples:

```text
Error rate > 5% for 5 minutes
```

```text
p95 latency > 500 ms for 10 minutes
```

```text
Database connection pool > 90%
```

```text
Kafka consumer lag continuously increasing
```

The exact thresholds should be based on workload and SLOs.

---

# 3.12 Health Checks

Use health endpoints, while distinguishing liveness and readiness.

### Liveness

> Is the application process alive?

```text
GET /health/live
```

### Readiness

> Is this instance ready to receive traffic?

```text
GET /health/ready
```

Example:

```text
Load Balancer
      |
      v
Readiness Check
      |
   +--+--+
   |     |
 READY  NOT READY
   |       |
   v       v
Traffic   Remove from traffic
```

An application can be alive but not ready, for example while initializing dependencies.

---

# 4. Complete Production Monitoring Architecture

```text
                         Users
                           |
                           v
                    Load Balancer
                           |
                           v
                    API Gateway
                           |
                           v
                    Backend Service
                    /      |       \
                   /       |        \
                  v        v         v
             Application  Logs     Metrics
                |           |         |
                v           v         v
             Traces     Log Store  Prometheus
                |           |         |
                v           v         v
          OpenTelemetry   Kibana    Grafana
                                      |
                                      v
                                   Alerts
                                      |
                                      v
                              On-call / Team
```

---

# 5. Putting All Three Concepts Together

A production-ready microservice combines resilience and observability.

```text
                         Client
                           |
                           v
                    API Gateway
                           |
                           v
                   Order Service
                           |
              +------------+------------+
              |                         |
              v                         v
        Payment Service          Inventory Service
              |                         |
        +-----+-----+             +-----+-----+
        |           |             |           |
     Timeout     Circuit       Timeout     Circuit
                 Breaker                   Breaker
        |           |             |           |
        +-----+-----+             +-----+-----+
              |
              v
          Fallback
              |
              v
       Cached / Safe Response


All services
     |
     +---- Metrics → Prometheus → Grafana
     |
     +---- Logs → Centralized Log Store
     |
     +---- Traces → OpenTelemetry/Jaeger
     |
     +---- Health → Load Balancer / Orchestrator
```

---

# 6. Scenario-Based Interview Example

## Scenario

> Your Order Service calls Payment Service. Payment Service becomes slow and starts returning 503 errors. Soon the Order Service also becomes unavailable. How would you fix the architecture?

## Strong Answer

I would use multiple layers:

### 1. Timeout

Bound how long Order Service waits for Payment.

### 2. Controlled Retry

Retry only transient failures, with exponential backoff and jitter.

### 3. Circuit Breaker

After enough failures:

```text
CLOSED → OPEN
```

Stop sending requests to Payment temporarily.

### 4. Fallback

For a non-critical dependency, return a safe degraded response.

For payment, do not report success when the actual payment state is unknown.

### 5. Bulkhead

Isolate payment resources so a slow Payment Service cannot consume all application threads/connections.

### 6. Monitoring

Track:

```text
Payment latency
Payment error rate
Order latency
Order error rate
Thread pool utilization
Connection pool utilization
Circuit breaker state
```

### 7. Distributed Tracing

Use correlation IDs and distributed tracing to identify where failures and latency originate.

---

# 7. Level 5 Follow-Up Questions

## Q1. Should we retry every failed request?

No.

Retry transient failures only when retrying is safe.

```text
Timeout / temporary 503 → possibly retry
Invalid input            → don't retry
Authentication failure   → don't retry
Business validation      → don't retry
```

For side-effecting operations, consider **idempotency** before retrying.

---

## Q2. Why can retries make an outage worse?

Because each failed request can generate more requests.

```text
1000 original requests
       |
       v
1000 failures
       |
       v
3000 retry attempts
       |
       v
More load
       |
       v
Service becomes even slower
```

This is a **retry storm**.

Use:

- Exponential backoff
- Jitter
- Retry limits
- Circuit breakers
- Timeouts

---

## Q3. Circuit breaker vs timeout?

```text
Timeout
→ How long can I wait for one call?

Circuit Breaker
→ Should I make calls to this dependency at all right now?
```

They solve different problems and are commonly used together.

---

## Q4. Circuit breaker vs bulkhead?

```text
Circuit Breaker
→ Stop calls to an unhealthy dependency.

Bulkhead
→ Isolate resources so one dependency cannot consume everything.
```

---

## Q5. Why is p99 latency useful?

Because average latency can hide tail latency.

```text
99 requests → 100 ms
1 request   → 10 seconds
```

p99 helps reveal the experience of approximately the slowest 1% of requests.

---

## Q6. What is the difference between liveness and readiness?

```text
Liveness
→ Is the application alive?

Readiness
→ Is the application ready to receive traffic?
```

An application can be alive but not ready.

---

# 8. Quick Revision Sheet

## Resilience

```text
Timeout
→ Don't wait forever

Retry + Exponential Backoff
→ Handle transient failures carefully

Circuit Breaker
→ Stop calling unhealthy dependency

Fallback
→ Graceful degradation

Bulkhead
→ Isolate resources
```

## Eventual Consistency

```text
Write
  |
  v
Replica A → Updated
Replica B → Old
Replica C → Old
  |
  v
Replication
  |
  v
All converge
```

## Monitoring

```text
Golden Signals
├── Latency
├── Traffic
├── Errors
└── Saturation
```

Also monitor:

```text
Logs
Traces
Alerts
Health Checks
```

---

# 9. One-Minute Level 5 Interview Answer

> For high availability, I assume downstream services can fail or become slow. I use bounded timeouts so requests don't wait indefinitely, controlled retries with exponential backoff and jitter for transient failures, and a Resilience4j circuit breaker to stop repeatedly calling an unhealthy dependency. I also use fallbacks for graceful degradation and bulkheads to isolate resources so one dependency doesn't exhaust the entire service's thread or connection pools.
>
> Eventual consistency means distributed replicas or services may temporarily have different values but eventually converge. It is suitable for things like feeds, search indexes and some catalogue views, but not for authoritative financial state where stronger consistency is required. I would distinguish ACID transaction guarantees from BASE-style availability/eventual-consistency approaches.
>
> For production monitoring, I track the four golden signals: latency, traffic, errors and saturation. I use Prometheus and Grafana for metrics, structured JSON logs with correlation IDs for centralized logging, and OpenTelemetry with Jaeger or Zipkin for distributed tracing. I also configure meaningful alerts and expose separate liveness and readiness health checks.

---

# 10. Key Terms to Memorize

```text
Resilience4j
Timeout
Retry
Exponential Backoff
Jitter
Retry Storm
Circuit Breaker
CLOSED / OPEN / HALF-OPEN
Fallback
Graceful Degradation
Bulkhead
Cascading Failure
Eventual Consistency
ACID
BASE
Correlation ID
Distributed Tracing
OpenTelemetry
Prometheus
Grafana
ELK
Golden Signals
Latency
Traffic
Errors
Saturation
Liveness
Readiness
SLO
Idempotency
```
