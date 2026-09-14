# System Design Interview Scenarios — Detailed Answers

## 1. Globalise a Social Media Application

### Scenario

A social media application currently operates only within one city. We need to expand it globally while maintaining low latency, high availability, scalability, and regulatory compliance.

### High-Level Architecture

```text
                         GLOBAL USERS
                              |
                     Global DNS / LB
                              |
          +-------------------+-------------------+
          |                   |                   |
       US Region          Europe Region       Asia Region
          |                   |                   |
       CDN/Edge            CDN/Edge            CDN/Edge
          |                   |                   |
      API Gateway         API Gateway         API Gateway
          |                   |                   |
      Services            Services             Services
          |                   |                   |
       Redis               Redis                Redis
          |                   |                   |
      US Database         EU Database         Asia Database
```

### 1. CDN

Use a CDN for static and media-heavy content:

- Images
- Videos
- JavaScript/CSS
- Profile pictures
- Frequently accessed public content

Instead of:

```text
User → Application Server → Storage
```

use:

```text
User → CDN Edge → Origin/Object Storage
```

The user receives content from a nearby edge location, reducing latency and origin traffic.

### 2. Regional Data Centres

Deploy application instances in multiple regions:

```text
US Region
Europe Region
Asia Region
```

Each region should contain:

- Load balancer
- API Gateway
- Stateless application services
- Regional cache
- Regional database/storage
- Monitoring and logging

A global DNS/load-balancing layer routes users to an appropriate region.

### 3. Geo-Sharding

A single global database can become a bottleneck.

Partition data geographically:

```text
User ID / Country
        ↓
 ┌──────┼────────┐
 US     EU       Asia
 DB     DB        DB
```

For example:

```text
user_id = 12345
country = IN
        ↓
Asia shard
```

Possible shard keys:

- User region
- User ID
- Country
- Tenant
- Combination of region + user ID

Avoid a shard key that creates hotspots.

### 4. Latency

Reduce latency using:

- CDN
- Regional application servers
- Regional caches
- Database replicas
- Connection pooling
- Compression
- Efficient APIs
- Async processing

For reads:

```text
User → nearest region → Redis → DB replica
```

For writes:

```text
User → home region → primary DB
```

### 5. Eventual Consistency

Not every operation needs immediate global consistency.

For example, when a user uploads a profile picture:

```text
User → Region A → DB
              ↓
          Event / Kafka
              ↓
      Other regional systems
```

Other regions may see the updated picture shortly afterward.

Strong consistency is more appropriate for:

- Payments
- Account ownership
- Security settings
- Critical authorization decisions

Eventual consistency is often acceptable for:

- Likes
- View counts
- Feeds
- Notifications
- Analytics

### 6. GDPR and Data Compliance

For European users, data may need to comply with GDPR requirements.

Design considerations:

- Data residency
- Consent management
- Right to access data
- Right to delete data
- Data minimisation
- Encryption at rest
- Encryption in transit
- Audit logging
- Retention policies

For example:

```text
EU User
  ↓
EU Region
  ↓
EU Data Store
```

Avoid unnecessarily copying regulated personal data into regions where it should not reside.

### Interview Summary

> I would use a global DNS/load-balancing layer, deploy stateless services across regional data centres, use a CDN for static/media content, geo-shard user data, use regional Redis caches and read replicas to reduce latency, and use asynchronous/event-driven replication where eventual consistency is acceptable. For regulated users, I would enforce data residency, encryption, retention, deletion, and GDPR controls.

---

# 2. Processing Large XML Files at Scale

## Scenario

We receive very large XML files that cannot safely be loaded entirely into memory.

### Architecture

```text
                 XML File
                    ↓
              Object Storage
                    ↓
             File Coordinator
                    ↓
             Chunk / Partition
              /      |       \
             ↓       ↓        ↓
          Worker   Worker   Worker
             \       |      /
              \      |     /
                Output Store
                    ↓
              Downstream System

Failures → Dead Letter Queue
```

### 1. Store the Files

Use object storage for large files:

- Google Cloud Storage
- Amazon S3
- Azure Blob Storage

Benefits:

- Durable
- Cheap
- Highly scalable
- Supports large files
- Separates storage from compute

Store metadata separately:

```text
file_id
file_name
size
status
created_at
checksum
processing_status
```

### 2. Why DOM Parsing Is a Problem

DOM-style parsing loads the entire XML structure into memory.

For a 10 GB XML file:

```text
10 GB XML
   ↓
DOM Parser
   ↓
Potentially huge memory requirement
```

This can cause:

- OutOfMemoryError
- Long GC pauses
- High memory usage
- Poor scalability

### 3. Use Streaming Parsing — StAX

StAX is a pull-based streaming XML parser.

Conceptually:

```text
XML
 ↓
Read event
 ↓
Process
 ↓
Discard
 ↓
Read next event
```

Memory remains approximately bounded by the current element/window rather than the complete XML document.

Example:

```java
XMLInputFactory factory = XMLInputFactory.newFactory();

try (InputStream input = Files.newInputStream(path)) {

    XMLStreamReader reader =
            factory.createXMLStreamReader(input);

    while (reader.hasNext()) {
        int event = reader.next();

        if (event == XMLStreamConstants.START_ELEMENT) {
            String name = reader.getLocalName();

            if ("customer".equals(name)) {
                // Read/process customer
            }
        }
    }
}
```

### 4. Chunking

A single huge XML file should ideally be divided into independently processable units.

For example:

```xml
<customers>
    <customer>...</customer>  <!-- Chunk 1 -->
    <customer>...</customer>  <!-- Chunk 2 -->
    <customer>...</customer>  <!-- Chunk 3 -->
</customers>
```

The challenge is that XML is hierarchical, so arbitrary byte-level splitting can produce invalid XML.

Therefore, chunk on logical boundaries such as:

- Customer
- Order
- Transaction
- Record group

### 5. Distributed Workers

Put chunks/messages into a queue:

```text
                Queue
             /    |    \
            ↓     ↓     ↓
        Worker Worker Worker
```

Benefits:

- Parallel processing
- Horizontal scaling
- Failure isolation
- Backpressure

If processing takes longer:

```text
10 workers → 50 workers → 100 workers
```

### 6. Dead-Letter Queue

If a message repeatedly fails:

```text
Queue
 ↓
Worker
 ↓
Failure
 ↓
Retry
 ↓
Retry
 ↓
DLQ
```

The DLQ lets us inspect problematic records without blocking the entire pipeline.

### Interview Summary

> I would store large XML files in object storage, use StAX instead of DOM to keep memory bounded, split the document at logical record boundaries, distribute chunks through a queue to horizontally scalable workers, make processing idempotent, and send repeatedly failing records to a dead-letter queue.

---

# 3. Design a Media Streaming Application

## Requirements

The system should:

- Stream video/audio
- Support millions of users
- Minimise buffering
- Adapt to network conditions
- Survive origin failures
- Scale during traffic spikes

### Architecture

```text
                    Users
                      ↓
                 Global DNS
                      ↓
                     CDN
                ↙     ↓     ↘
             Edge    Edge    Edge
                      ↓
                Origin Servers
                      ↓
             Object Storage
                      ↑
              Transcoding
                      ↑
              Uploaded Video
```

### 1. Video Processing Pipeline

```text
Upload
  ↓
Object Storage
  ↓
Transcoding
  ↓
Multiple Bitrates
  ↓
HLS / DASH Segments
  ↓
CDN
  ↓
Player
```

One video might be transcoded into:

```text
240p
480p
720p
1080p
4K
```

### 2. Segmentation

Do not send the entire video as one huge file.

Split it into small segments:

```text
Video
 ↓
Segment 1
Segment 2
Segment 3
Segment 4
...
```

Common protocols:

- HLS
- MPEG-DASH

The player downloads segments as needed.

### 3. Adaptive Bitrate Streaming

Suppose the network is fast:

```text
1080p → 5 Mbps
```

Network becomes slower:

```text
720p → 3 Mbps
```

Network becomes very slow:

```text
480p → 1.5 Mbps
```

The player dynamically chooses an appropriate bitrate.

This reduces buffering.

### 4. CDN

Video segments are highly cacheable.

```text
User
 ↓
Nearest CDN Edge
 ↓ cache hit
Video Segment
```

On a cache miss:

```text
CDN
 ↓
Origin
 ↓
Object Storage
```

This prevents millions of users from directly hitting the origin.

### 5. Fault Tolerance

Use:

- Multiple origins
- Replicated object storage
- Multiple CDN regions
- Health checks
- Automatic failover
- Retries
- Circuit breakers where appropriate

### Interview Summary

> I would store the original media in durable object storage, transcode it into multiple bitrates, segment it using HLS/DASH, distribute segments through a CDN, and let the player adapt bitrate based on bandwidth and buffer health. Multiple origins and replicated storage provide fault tolerance.

---

# 4. Slow Microservice but Not Down

## Scenario

Service A calls Service B.

Service B is slow:

```text
A → B
    ↓
  Slow
```

B is technically available, but it takes a long time to respond.

This can cause:

```text
B slow
 ↓
A threads/connections occupied
 ↓
A becomes slow
 ↓
Other services wait for A
 ↓
Cascading failure
```

This is called a **cascading failure**.

### 1. Timeout

Never wait indefinitely.

```text
A → B
    ↓
  timeout
```

Example:

```text
connect timeout = 500 ms
read timeout    = 2 sec
```

Values should be based on actual latency SLOs rather than arbitrary numbers.

### 2. Circuit Breaker

When B consistently fails or becomes unhealthy:

```text
Closed
  ↓ failures
Open
  ↓
Reject calls quickly
  ↓
Half-Open
  ↓
Test B
  ↓
Closed
```

This prevents unnecessary traffic to an unhealthy dependency.

### 3. Bulkhead Pattern

Separate resources for different dependencies.

```text
Service A

Pool 1 → Service B
Pool 2 → Service C
Pool 3 → Service D
```

If B becomes slow:

```text
Pool 1 exhausted
```

but C and D can continue operating.

How It Works
Naval Inspiration: The name comes from the watertight partitions (bulkheads) inside a ship's hull. If one compartment floods, the seals prevent water from spreading and sinking the vessel. 
Resource Partitioning: In software and System Design on GeeksforGeeks, it divides elements like thread pools, database connections, or CPU/memory allocations into isolated groups.
Failure Containment: If a downstream dependency or service slows down or crashes, it only exhausts its own assigned pool. Other parts of the application continue working normally.

### 4. Retry with Exponential Backoff

Bad:

```text
Request
 ↓
Retry
 ↓
Retry
 ↓
Retry
```

Better:

```text
Retry 1 → 100 ms
Retry 2 → 200 ms
Retry 3 → 400 ms
```

Add jitter to avoid synchronized retry storms.

Only retry operations that are safe/idempotent or explicitly designed for retry.

### 5. Async Messaging

If the operation does not need an immediate response:

```text
A → Queue → B
```

A does not need to wait for B.

Examples:

- Email
- Notifications
- Analytics
- Report generation

### Interview Summary

> A slow dependency can be more dangerous than a down dependency because it consumes threads, connections, and other resources while callers wait. I would protect the caller using timeouts, circuit breakers, bulkheads, bounded retries with exponential backoff and jitter, and asynchronous messaging for operations that don't require synchronous responses.

---

# 5. Time-Series Data

## Examples

Time-series data includes:

- CPU metrics
- Stock prices
- IoT measurements
- Application metrics
- Sensor readings

Typical structure:

```text
timestamp | metric | value | tags
```

Example:

```text
10:00:01 | CPU | 72%
10:00:02 | CPU | 75%
10:00:03 | CPU | 78%
```

### 1. InfluxDB

InfluxDB is designed specifically for time-series workloads.

Good for:

- Metrics
- Monitoring
- IoT
- High write rates
- Time-window queries

### 2. TimescaleDB

TimescaleDB extends PostgreSQL for time-series workloads.

Advantage:

```text
PostgreSQL
+
Time-series optimisation
```

Useful when the system needs relational SQL capabilities along with time-series optimisation.

### 3. Partition by Time

Instead of one enormous table:

```text
metrics
```

partition into:

```text
metrics_2026_01
metrics_2026_02
metrics_2026_03
```

Queries for a specific time range can scan only relevant partitions.

### 4. Downsampling

Raw data:

```text
1-second measurements
```

After some retention period:

```text
1-minute average
```

Later:

```text
1-hour average
```

This significantly reduces storage.

Example:

```text
Raw:
1,000,000 points

Downsampled:
60,000 points
```

### 5. Write-Heavy Optimisation

For high-volume writes:

- Batch writes
- Sequential/append-friendly storage
- Time-based partitioning
- Appropriate indexes
- Compression
- Avoid excessive secondary indexes

### Trade-offs

| Approach | Advantages | Trade-offs |
|---|---|---|
| InfluxDB | Purpose-built TSDB | Different ecosystem/query model |
| TimescaleDB | PostgreSQL compatibility | Can require careful tuning at huge scale |
| Raw data | Maximum detail | Expensive storage |
| Downsampling | Lower storage/query cost | Loses granular detail |
| More indexes | Faster selected reads | Slower writes/storage overhead |

### Interview Summary

> For time-series workloads, I would choose a specialised database such as InfluxDB or TimescaleDB depending on the query and ecosystem requirements. I would partition by time, batch writes, use appropriate indexes, compress older data, and downsample historical data to control storage and query cost.

---

# 6. At-Least-Once vs Exactly-Once Processing

## At-Least-Once

The message is guaranteed to be processed **one or more times**.

Possible sequence:

```text
Consumer receives message
 ↓
Processes message
 ↓
Consumer crashes before acknowledging
 ↓
Broker redelivers message
 ↓
Message processed again
```

Therefore:

```text
Message → Processed twice
```

Duplicates are possible.

### Kafka Offsets

Kafka tracks consumer offsets.

A consumer can:

```text
Read message
 ↓
Process
 ↓
Commit offset
```

If the consumer crashes before committing:

```text
Message may be consumed again
```

Therefore, Kafka offset management alone does not automatically make an external side effect exactly-once.

### Idempotent Consumer

Make repeated processing produce the same final result.

Example:

```text
Payment ID = P123
```

Before processing:

```text
if P123 already processed:
    ignore
else:
    process
    record P123
```

This is often the practical approach.

## Exactly-Once

The goal is that the effect of processing happens exactly once.

This is significantly harder in distributed systems because failures can happen between:

```text
DB commit
   ↓
Kafka acknowledgement
```

### Outbox Pattern

Suppose an application needs to update a DB and publish an event.

Problem:

```text
DB update succeeds
Kafka publish fails
```

With the Outbox pattern:

```text
Application
    ↓
DB Transaction
 ┌───────────────┐
 │ Business Data │
 │ Outbox Event  │
 └───────────────┘
    ↓
Outbox Publisher
    ↓
Kafka
```

The business update and outbox record are committed atomically in the same database transaction.
How It Works
The Problem: When an app updates a database and sends an event to a message broker (like Kafka or RabbitMQ) separately, one operation can fail after the other succeeds, leaving your system inconsistent. 
The Solution: Instead of publishing directly to the event bus, the service writes the event payload into an Outbox table inside its local database during the main business transaction. 
The Relay/Processor: A separate background worker or Change Data Capture (CDC) tool reads the outbox table asynchronously, publishes the message to the message broker, and marks it as processed.


### When to Choose Which?

**At-least-once + idempotency** is usually preferred because it is simpler and robust.

Use it for:

- Notifications
- Analytics
- Search indexing
- Background jobs

Stronger exactly-once semantics may be justified for critical workflows, but usually require careful transactional design and often mean **exactly-once effects**, not elimination of all duplicate delivery.

### Interview Summary

> At-least-once processing is easier to achieve and is usually combined with idempotent consumers. Exactly-once effects are harder because failures can occur between processing and acknowledgement. For database-plus-event consistency, I would use an Outbox pattern and idempotent consumers.

---

# 7. Safely Deploying Breaking API Changes

## Problem

Suppose:

```text
Old API:
GET /users/{id}

New API:
GET /customers/{id}
```

If we immediately remove the old API:

```text
Old consumers → FAILURE
```

### 1. API Versioning

Use:

```text
/api/v1/users
/api/v2/customers
```

Run both versions temporarily.

```text
Consumers
   ├── v1 → Old implementation
   └── v2 → New implementation
```

Gradually migrate consumers.

### 2. Backward Compatibility

Prefer additive changes.

Safe:

```json
{
  "name": "John",
  "age": 30,
  "email": "..."
}
```

Adding a new optional field usually does not break clients:

```json
{
  "name": "John",
  "age": 30,
  "email": "...",
  "phone": "..."
}
```

Risky changes:

- Renaming fields
- Removing fields
- Changing data types
- Changing semantics
- Making optional fields mandatory

### 3. Feature Flags

Deploy code without immediately enabling it:

```text
New Code
   ↓
Feature Flag
   ↓
10% users
   ↓
50%
   ↓
100%
```

If problems occur:

```text
Feature Flag OFF
```

### 4. Consumer-Driven Contract Testing

Consumers define expectations.

Example:

```text
Consumer expects:

GET /users/123

{
   "id": 123,
   "name": "John"
}
```

The provider runs contract tests before deployment.

This catches breaking changes early.

### 5. Safe Migration

A good sequence:

```text
1. Introduce new API
2. Keep old API
3. Deploy new consumer support
4. Migrate consumers gradually
5. Monitor usage
6. Deprecate old API
7. Remove old API after migration
```

### Interview Summary

> I would avoid breaking all consumers at once. I would introduce a versioned API, maintain backward compatibility, use feature flags for gradual rollout, use consumer-driven contract tests, monitor adoption, and remove the old version only after all consumers have migrated.

---

# 8. Scaling to Millions of Users and Handling Traffic Spikes

## Architecture

```text
                    Users
                      ↓
               Global Load Balancer
                      ↓
                  CDN / WAF
                      ↓
                 API Gateway
                      ↓
            ┌─────────┼─────────┐
            ↓         ↓         ↓
         App-1      App-2      App-N
            └─────────┼─────────┘
                      ↓
                   Redis
                      ↓
            ┌─────────┴─────────┐
            ↓                   ↓
       Read Replicas          Primary DB
                                  ↓
                              Write Path
```

For asynchronous workloads:

```text
Application
    ↓
Message Queue
    ↓
Worker 1
Worker 2
Worker 3
...
```

### 1. Horizontal Scaling

Instead of making one server extremely powerful:

```text
1 huge server
```

use:

```text
Server 1
Server 2
Server 3
...
Server N
```

behind a load balancer.

Benefits:

- Higher throughput
- Fault tolerance
- Easier scaling

### 2. Stateless Services

Application servers should ideally be stateless.

Bad:

```text
Session stored only on Server 1
```

If Server 1 fails, the user session is lost.

Better:

```text
App servers
    ↓
Shared Redis / database
```

Any application instance can handle the request.

### 3. Auto-Scaling

Monitor:

- CPU
- Memory
- Request rate
- Queue depth
- Latency

Example:

```text
Traffic ↑
   ↓
CPU / queue depth ↑
   ↓
Auto-scaling
   ↓
More instances
```

### 4. Queue-Based Load Levelling

Traffic spike:

```text
100,000 requests
      ↓
    Queue
      ↓
Workers process at controlled rate
```

The queue absorbs bursts.

Without a queue:

```text
100,000 requests
       ↓
Application
       ↓
Overload
```

### 5. Caching

Use Redis or another distributed cache for frequently accessed data.

```text
Request
  ↓
Redis
  ↓ cache hit
Response
```

For cache miss:

```text
Redis miss
   ↓
Database
   ↓
Redis
   ↓
Response
```

This is commonly called the **cache-aside pattern**.

### 6. Database Read Replicas

Separate read-heavy traffic:

```text
             Application
             /         \
           Reads       Writes
             ↓           ↓
       Read Replicas   Primary
```

This reduces pressure on the primary database.

### 7. CDN

Use CDN for:

- Images
- Videos
- CSS
- JavaScript
- Public/static content

This reduces application-server traffic.

### 8. Backpressure

If downstream systems cannot process requests fast enough, don't keep accepting unlimited work.

Use:

- Bounded queues
- Rate limiting
- Load shedding
- Concurrency limits
- Backpressure

### 9. Rate Limiting

Protect APIs from excessive traffic:

```text
Client
 ↓
API Gateway
 ↓
Rate Limiter
 ↓
Application
```

Example:

```text
100 requests/minute/user
```

This protects the platform from abusive clients and accidental traffic storms.

### Interview Summary

> For millions of users, I would horizontally scale stateless services behind load balancers, use CDN and caching to reduce origin traffic, use read replicas for read-heavy workloads, and use auto-scaling based on CPU, latency, request rate, or queue depth. For traffic spikes, queues provide load levelling, while rate limiting, bounded concurrency, backpressure, and load shedding protect downstream services.

---

# Quick Interview Cheat Sheet

| Topic | Key Points |
|---|---|
| Globalisation | CDN, regional DCs, geo-sharding, regional cache, GDPR, eventual consistency |
| Large XML | Object storage, StAX, logical chunking, workers, queue, DLQ |
| Media Streaming | HLS/DASH, segments, CDN, adaptive bitrate, multiple origins |
| Slow Service | Timeout, circuit breaker, bulkhead, async messaging, retry/backoff |
| Time Series | InfluxDB/TimescaleDB, time partitioning, downsampling, batching |
| At-Least-Once | Redelivery possible, idempotent consumers |
| Exactly-Once | Harder; transactional/idempotent design, Outbox |
| API Changes | Versioning, backward compatibility, feature flags, contract tests |
| Millions of Users | Horizontal scaling, auto-scaling, cache, CDN, read replicas, queues |

# How to Structure These Answers in an Interview

For almost every system-design question, use this sequence:

### 1. Clarify Requirements

Ask:

- How many users?
- Requests per second?
- Read/write ratio?
- Latency requirement?
- Availability requirement?
- Data retention?
- Consistency requirement?
- Compliance requirements?

### 2. Start With High-Level Architecture

```text
Client
  ↓
CDN / Load Balancer
  ↓
API Gateway
  ↓
Services
  ↓
Cache
  ↓
Database
```

Then add queues, workers, replicas, and regional infrastructure as required.

### 3. Identify Bottlenecks

Discuss:

- Database bottlenecks
- Network latency
- Hot partitions
- Cache misses
- Slow downstream services
- Queue backlog
- Storage growth

### 4. Explain Reliability

Mention:

- Timeouts
- Retries + exponential backoff + jitter
- Circuit breakers
- Bulkheads
- Health checks
- Replication
- Failover
- DLQs

### 5. Explain Scalability

Mention:

- Horizontal scaling
- Auto-scaling
- CDN
- Caching
- Read replicas
- Sharding
- Async processing
- Queue-based load levelling

### 6. Explain Trade-offs

A strong senior-level answer should not just say what technology to use. Explain **why** and what you give up.

For example:

> "I would use eventual consistency for feed counts because availability and latency are more important than immediately accurate counts. For payments, I would choose stronger consistency because correctness is more important."
