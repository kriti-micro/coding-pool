# Microservices Resilience & Observability — Simple Explanation

## 1. Big Picture

Imagine a Java microservices application:

```text
                    User
                      |
                      v
                API Gateway
                      |
                      v
                Order Service
                  /       \
                 v         v
          Inventory      Payment
           Service       Service
```

In production, we want to know:

- How many requests are coming? → **Prometheus**
- How do I visualize metrics? → **Grafana**
- What happened during one request across services? → **OpenTelemetry + Jaeger/Zipkin**
- What did the application log? → **ELK**
- How should retries be spread out? → **Jitter**

These tools solve different problems.

---

## 2. Quick Relationship

| Technology / Term | Simple Meaning | Think of it as |
|---|---|---|
| Prometheus | Collects and stores metrics | Patient monitor |
| Grafana | Displays metrics in dashboards | Hospital dashboard |
| OpenTelemetry | Instruments applications and collects/exports telemetry | Data collector |
| Jaeger | Distributed tracing backend/UI | X-ray of request journey |
| Zipkin | Another distributed tracing system | Another X-ray tool |
| ELK | Centralized log collection/search/analysis | Hospital records system |
| Jitter | Adds randomness to retry timing | Prevents everyone retrying together |

---

# 3. OpenTelemetry

**OpenTelemetry (OTel)** is a vendor-neutral observability framework used to instrument applications and collect/export telemetry.

Telemetry commonly includes:

```text
Metrics
Logs
Traces
```

Example:

```text
Order Service
     |
     | trace/span information
     v
Payment Service
     |
     v
Bank API
```

OpenTelemetry can export trace data to a tracing backend:

```text
OpenTelemetry
      |
      +----> Jaeger
      |
      +----> Zipkin
      |
      +----> Other observability backends
```

### Important Interview Point

**OpenTelemetry and Jaeger are not the same thing.**

```text
OpenTelemetry
     |
     | Instrument / collect / export
     v
Jaeger
     |
     | Store / visualize
     v
Trace visualization
```

---

# 4. Jaeger

**Jaeger is a distributed tracing system.**

It helps answer:

> "Where did my request spend its time?"

Example:

```text
User
 |
 v
API Gateway       20 ms
 |
 v
Order Service     50 ms
 |
 v
Payment Service  500 ms
 |
 v
Bank API         450 ms
```

Jaeger can show:

```text
Request
|
+-- Gateway          20 ms
|
+-- Order Service    50 ms
|
+-- Payment Service 500 ms
     |
     +-- Bank API   450 ms
```

Now we know that the Bank API is contributing most of the latency.

---

# 5. Zipkin

**Zipkin is also a distributed tracing system.**

Its purpose is similar to Jaeger.

```text
             Distributed Tracing
                     |
             +-------+-------+
             |               |
           Jaeger          Zipkin
```

You would commonly choose one tracing backend rather than needing both for the same setup.

For example:

```text
OpenTelemetry → Jaeger
```

or:

```text
OpenTelemetry → Zipkin
```

### Interview Answer

> Jaeger and Zipkin are distributed tracing systems. They help visualize and troubleshoot requests across multiple microservices. OpenTelemetry can instrument the application and export trace data to a backend such as Jaeger or Zipkin.

---

# 6. Trace and Span

This is important for understanding Jaeger and Zipkin.

Suppose:

```text
User → Gateway → Order → Payment
```

The complete request journey is a:

```text
TRACE
```

Each individual operation is a:

```text
SPAN
```

Diagram:

```text
TRACE: abc-123
|
+-- Gateway Span
|
+-- Order Service Span
|
+-- Payment Service Span
     |
     +-- Database Span
```

Remember:

```text
Trace = Complete request journey

Span = One operation within that journey
```

---

# 7. Prometheus

**Prometheus is primarily a metrics monitoring and collection system.**

It answers:

- How many requests are coming?
- What is the error rate?
- What is the latency?
- What is CPU usage?
- What is memory usage?

Example:

```text
Request rate = 2,500 req/sec
Error rate   = 1.2%
CPU          = 72%
Memory       = 65%
p95 latency  = 300 ms
```

Architecture:

```text
Java Application
      |
      | metrics
      v
 Prometheus
```

---

# 8. Grafana

**Grafana is primarily a visualization and dashboarding tool.**

Prometheus has the metrics.

Grafana makes those metrics easy to understand visually.

```text
Java Application
       |
       v
   Prometheus
       |
       | query
       v
     Grafana
       |
       v
   Dashboard
```

Remember:

```text
Prometheus → Collect/store/query metrics

Grafana → Visualize metrics
```

---

# 9. Prometheus + Grafana

These two are commonly used together.

```text
             Java Application
                    |
                    | metrics
                    v
               Prometheus
                    |
                    | query
                    v
                 Grafana
                    |
                    v
                Dashboard
```

For example:

```text
http_requests_total
http_request_duration
jvm_memory_used
```

Prometheus collects them and Grafana displays them.

---

# 10. ELK

ELK is commonly used for **centralized logging**.

```text
E → Elasticsearch
L → Logstash
K → Kibana
```

Think:

```text
Prometheus → primarily metrics

ELK         → primarily logs
```

---

# 11. Why Centralized Logging?

Imagine five microservices:

```text
Order Service       ──┐
Payment Service      ──┤
Inventory Service    ──┼──> Centralized Logging
User Service         ──┤
Notification Service ──┘
```

With centralized logging, you can search:

```text
orderId = ORD-123
```

or:

```text
correlationId = abc-123
```

and find related logs across services.

---

# 12. Elasticsearch

**Elasticsearch** is commonly used to index, store and search data such as logs.

Example:

```text
Search:

service = payment-service
AND level = ERROR
AND orderId = ORD-123
```

---

# 13. Logstash

**Logstash** is commonly used to ingest, process and transform log data.

```text
Application Logs
      |
      v
   Logstash
      |
      | parse / transform
      v
Elasticsearch
```

---

# 14. Kibana

**Kibana** provides a UI for searching, exploring and visualizing Elasticsearch data.

```text
Applications
     |
     v
  Logstash
     |
     v
Elasticsearch
     |
     v
   Kibana
     |
     v
Search / Dashboard
```

---

# 15. OpenTelemetry vs ELK

This is a common source of confusion.

### ELK

Primarily associated with:

```text
LOGS
```

### OpenTelemetry

Provides instrumentation and telemetry for:

```text
TRACES
METRICS
LOGS
```

OpenTelemetry can export telemetry to different backends.

Therefore:

> Do not simply say "OpenTelemetry replaces ELK." They can coexist.

---

# 16. Jitter

**Jitter adds randomness to retry delays.**

It is a resilience concept, not a monitoring tool.

Suppose 1,000 requests fail at exactly the same time.

Without jitter:

```text
1000 requests fail
        |
        v
     Wait 1 sec
        |
        v
1000 requests retry together
```

This can create another traffic spike.

---

# 17. With Jitter

Instead of everyone retrying at exactly the same time:

```text
Request 1 → retry after 0.91 sec
Request 2 → retry after 1.07 sec
Request 3 → retry after 0.96 sec
Request 4 → retry after 1.12 sec
```

The requests are spread out.

### Simple Interview Definition

> Jitter adds randomness to retry delays so many clients don't retry at exactly the same time and overload an already struggling service.

---

# 18. Exponential Backoff + Jitter

### Without Backoff

```text
Failure
   ↓
Retry immediately
   ↓
Failure
   ↓
Retry immediately
```

### With Exponential Backoff

```text
Failure
   ↓
Wait 100 ms
   ↓
Failure
   ↓
Wait 200 ms
   ↓
Failure
   ↓
Wait 400 ms
```

Conceptually:

```text
delay = baseDelay × 2^attempt
```

Then add jitter:

```text
retry delay =
exponential backoff + random variation
```

So:

```text
Exponential Backoff
        +
      Jitter
        ↓
Controlled retries
```

---

# 19. How Everything Fits Together

```text
                         USER
                           |
                           v
                    +-------------+
                    | API Gateway |
                    +-------------+
                           |
                           v
                    +-------------+
                    | Order       |
                    | Service     |
                    +-------------+
                       /       \
                      /         \
                     v           v
              Inventory       Payment
               Service        Service
                   |              |
                   +--------------+
                          |
                       Database


========================================================
                    RESILIENCE
========================================================

Service-to-service calls

       |
       +-- Timeout
       |
       +-- Retry
       |      |
       |      +-- Exponential Backoff
       |      +-- Jitter
       |
       +-- Circuit Breaker
       |
       +-- Bulkhead
       |
       +-- Fallback


========================================================
                  OBSERVABILITY
========================================================

                 Microservices
                  /    |    \
                 /     |     \
                v      v      v
             Metrics  Logs  Traces
                |      |      |
                v      v      v
          Prometheus   ELK   OpenTelemetry
                |             |
                v             |
             Grafana           |
                              |
                         +----+----+
                         |         |
                         v         v
                      Jaeger    Zipkin
```

---

# 20. One Request Through the Complete System

Suppose:

```text
POST /orders
```

## Step 1 — Request

```text
User
 |
 v
API Gateway
```

## Step 2 — Order Service

```text
Gateway
   |
   v
Order Service
```

OpenTelemetry creates trace information:

```text
Trace ID = abc-123
```

## Step 3 — Payment Call

```text
Order Service
      |
      v
Circuit Breaker
      |
      v
Payment Service
```

Suppose Payment Service is temporarily unavailable.

Retry:

```text
Attempt 1 → fail
     |
   100ms + jitter
     |
Attempt 2 → fail
     |
   200ms + jitter
     |
Attempt 3 → fail
```

If failures cross the circuit-breaker threshold:

```text
Circuit
 CLOSED
   |
   v
 OPEN
```

A fallback may execute, depending on the business operation.

For critical operations such as payment, the fallback must not falsely report a successful payment when the actual payment status is unknown.

---

# 21. Meanwhile, Monitoring Is Happening

## Metrics

The application produces:

```text
Payment requests/sec
Payment error rate
Payment latency
CPU
Memory
```

Flow:

```text
Application
     |
     v
Prometheus
     |
     v
Grafana
```

## Logs

Example:

```json
{
  "level": "ERROR",
  "service": "payment-service",
  "traceId": "abc-123",
  "orderId": "ORD-101",
  "message": "Payment timeout"
}
```

These logs can be centralized and searched using a logging platform such as ELK.

## Traces

OpenTelemetry records:

```text
Trace abc-123

Gateway        20 ms
Order Service  80 ms
Payment       900 ms
```

The trace can be exported to:

```text
Jaeger
```

or:

```text
Zipkin
```

---

# 22. Three Main Categories

## A. Resilience — "How do I survive failures?"

```text
Timeout
Retry
Exponential Backoff
Jitter
Circuit Breaker
Fallback
Bulkhead
```

**Jitter belongs here.**

## B. Metrics — "How is my system performing?"

```text
Prometheus
    ↓
Grafana
```

Remember:

> Prometheus = metrics collection/storage/query

> Grafana = metrics visualization/dashboard

## C. Logs & Traces — "What exactly happened?"

### Logs

```text
ELK
```

> ELK = centralized log ingestion, search and analysis

### Traces

```text
OpenTelemetry
      |
      v
Jaeger / Zipkin
```

> OpenTelemetry = instrumentation and telemetry collection/export

> Jaeger/Zipkin = distributed tracing systems

---

# 23. Final Mental Model

```text
                 PRODUCTION MICROSERVICES
                          |
          +---------------+---------------+
          |                               |
          v                               v
      RESILIENCE                    OBSERVABILITY
          |                               |
    +-----+------+             +----------+----------+
    |     |      |             |          |          |
 Timeout Retry Circuit       Metrics     Logs      Traces
             |    Breaker       |          |          |
             |                  |          |          |
           Jitter               v          v          v
                            Prometheus    ELK     OpenTelemetry
                                |                    |
                                v                 +--+--+
                             Grafana              |     |
                                                  v     v
                                               Jaeger Zipkin
```

---

# 24. One-Sentence Definitions for Interviews

### Prometheus
> Prometheus is a monitoring system primarily used to collect, store and query metrics.

### Grafana
> Grafana is a visualization and dashboarding tool commonly used to display metrics from systems such as Prometheus.

### OpenTelemetry
> OpenTelemetry provides vendor-neutral instrumentation and telemetry collection/export for metrics, logs and traces.

### Jaeger
> Jaeger is a distributed tracing system used to visualize and troubleshoot requests across microservices.

### Zipkin
> Zipkin is another distributed tracing system used to trace requests across distributed services.

### ELK
> ELK stands for Elasticsearch, Logstash and Kibana and is commonly used for centralized log ingestion, storage/search and visualization.

### Jitter
> Jitter adds randomness to retry delays so many clients don't retry simultaneously and overload an already struggling service.

---

# 25. Interview Cheat Sheet

```text
RESILIENCE
---------------------------------
Timeout
→ Don't wait forever

Retry
→ Try again for transient failures

Exponential Backoff
→ Increase the wait between retries

Jitter
→ Add randomness to retry timing

Circuit Breaker
→ Stop calling an unhealthy service

Fallback
→ Return a safe/degraded response

Bulkhead
→ Isolate resources


METRICS
---------------------------------
Prometheus
→ Collect/query metrics

Grafana
→ Visualize metrics


LOGS
---------------------------------
ELK
→ Centralized logs

Elasticsearch
→ Index/search/store

Logstash
→ Ingest/process

Kibana
→ Search/visualize


TRACING
---------------------------------
OpenTelemetry
→ Instrument/collect/export telemetry

Jaeger
→ Distributed tracing backend/UI

Zipkin
→ Distributed tracing system
```

---

# 26. Final Memory Trick

> **Prometheus measures, Grafana shows, ELK logs, OpenTelemetry instruments, Jaeger/Zipkin trace, and Jitter spreads retries.**
