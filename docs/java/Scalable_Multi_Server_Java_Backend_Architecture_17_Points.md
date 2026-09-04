# Scalable Multi-Server Java Backend Architecture — 17 Points

## 1. Overall Multi-Server Architecture

A single-server application is:

```text
Users → One Application Server → Database
```

A scalable architecture can be:

```text
                         Users
                           |
                    +-------------+
                    |Load Balancer|
                    +-------------+
                      /    |    \
                     v     v     v
                    S1     S2     S3
                     \     |     /
                      +----+----+
                           |
                     API Gateway
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
      User Service    Podcast Service   Auth Service
          |                |                |
          +----------------+----------------+
                           |
                    +-------------+
                    |    Redis    |
                    +-------------+
                           |
                     Primary DB
                       /     \
                      v       v
                 Read Replica  Read Replica
```

Goals: scalability, availability, fault tolerance, traffic distribution, independent scaling, and database offloading.

---

## 2. Horizontal Scaling

Horizontal scaling means **adding more servers/instances** instead of making one server increasingly powerful.

### Vertical scaling

```text
4 CPU / 16 GB RAM
        ↓
32 CPU / 128 GB RAM
```

### Horizontal scaling

```text
Server 1
Server 2
Server 3
```

If one server handles 10,000 requests/second and traffic grows to 30,000 requests/second, three instances can provide additional capacity.

Benefits:
- Easy capacity expansion
- Redundancy
- Cloud-friendly
- Better fault tolerance

---

## 3. Load Balancer

A load balancer receives requests and distributes them among healthy application instances.

```text
Users
  |
Load Balancer
 /    |    \
S1    S2    S3
```

Benefits:
- Traffic distribution
- Health checks
- Failover
- Hides individual server addresses from clients

---

## 4. Round-Robin

Requests are distributed sequentially:

```text
R1 → S1
R2 → S2
R3 → S3
R4 → S1
R5 → S2
R6 → S3
```

### Advantage
Simple and predictable.

### Disadvantage
It does not necessarily account for current server workload.

If S1 is processing an expensive 5-minute request while S2 is idle, round-robin may still send the next request to S1.

---

## 5. Least Connections

The load balancer chooses the instance with fewer active connections.

Example:

```text
S1 → 100 connections
S2 → 20 connections
S3 → 50 connections

New request → S2
```

This can work better when requests have different processing times.

Example:

```text
GET /podcasts → 20 ms
GET /large-file → 5 seconds
```

The strategy is more workload-aware than simple round-robin.

---

## 6. Health Checks

The load balancer should avoid unhealthy servers.

For example:

```text
GET /health
```

If:

```text
S1 → Healthy
S2 → DOWN
S3 → Healthy
```

traffic is sent only to S1 and S3.

```text
Before: LB → S1, S2, S3
After:  LB → S1, S3
```

This improves availability.

---

## 7. Stateless Services

A stateless service does not depend on state stored in one particular application instance's memory.

Bad for scaling:

```text
Request 1 → S1
             |
             +-- session stored in S1 RAM

Request 2 → S2
             |
             +-- S2 doesn't know S1's session
```

Stateless approach:

```text
Request 1 → S1
Request 2 → S3
Request 3 → S2
```

Any instance can handle any request.

This is important because a load balancer can send consecutive requests to different instances.

---

## 8. JWT and Stateless Authentication

For REST APIs, authentication information can be carried in a token:

```text
Authorization: Bearer <JWT>
```

The server validates the JWT instead of depending on authentication state stored in its local memory.

```text
Request
   |
Server 2
   |
Validate JWT
   |
Process request
```

JWT can make authentication stateless, but other application state may still require external storage.

---

## 9. Session Data in Redis

Some applications still require server-side state:

- Sessions
- Shopping carts
- OTP state
- Temporary workflow state
- Distributed counters

Instead of storing it in local RAM:

```text
S1 RAM → Session
```

store it centrally:

```text
              Redis
             /  |  \
            S1  S2  S3
```

Example:

```text
session:user123
{
    "userId": "123",
    "role": "USER"
}
```

S1 can write it and S3 can later read it.

Therefore the session is not tied to one instance.

---

## 10. Why Redis?

Redis is an in-memory key-value store commonly used for:

- Sessions
- Caching
- Counters
- Rate limiting
- Temporary state

### Sticky sessions vs Redis

Sticky sessions:

```text
User A → Always S1
User B → Always S2
```

Redis approach:

```text
User A → S1 → Redis
User A → S3 → Redis
```

Redis allows requests to move between instances more freely.

### Trade-off

Redis becomes an important infrastructure dependency and must itself be designed for appropriate availability and capacity.

---

## 11. API Gateway

An API Gateway provides a centralized API entry point.

Instead of:

```text
Client → User Service
Client → Podcast Service
Client → Payment Service
```

use:

```text
Client
  |
API Gateway
 /   |   \
User Podcast Payment
```

Typical responsibilities:
- API routing
- Rate limiting
- Authentication integration
- Request transformation
- API policies
- API-level logging

---

## 12. API Gateway Routing

Example routes:

```text
/api/users/**     → User Service
/api/podcasts/**  → Podcast Service
/api/payments/**  → Payment Service
```

Example:

```text
GET /api/podcasts/123
          |
          v
     API Gateway
          |
          v
    Podcast Service
```

The client does not need to know internal service addresses.

---

## 13. API Gateway Rate Limiting

Suppose one client sends 10,000 requests/second.

The gateway can enforce:

```text
100 requests/minute/user
```

Conceptually:

```text
Client
  |
API Gateway
  |
Rate-limit check
  |
  +-- Allowed → Backend
  |
  +-- Exceeded → HTTP 429
```

Redis can maintain distributed counters when multiple gateway instances exist.

---

## 14. Cloud Auto-Scaling

Traffic changes over time.

```text
Normal:
Load Balancer
    |
   S1 S2
```

During a traffic spike:

```text
Load Balancer
    |
S1 S2 S3 S4 S5
```

When traffic decreases:

```text
Load Balancer
    |
   S1 S2
```

Auto-scaling can use:
- CPU
- Memory
- Request count
- Requests per instance
- Queue length
- Custom metrics

Example:

```text
CPU > 70% → Add instances
CPU < 30% → Remove instances
```

Request rate or queue depth can sometimes be a better signal than CPU alone.

---

## 15. Database Read Replicas

For read-heavy applications, the primary database can become a bottleneck.

Instead of:

```text
100,000 reads/sec
       |
   Primary DB
```

use:

```text
             Primary DB
             /        \
            v          v
       Replica 1    Replica 2
```

Typically:
- Writes → Primary
- Reads → Replicas

Example:

```text
POST /podcast → Primary DB
GET /podcast/123 → Read Replica
```

This is called **read offloading**.

---

## 16. Replication Lag and Consistency

Read replicas are not necessarily updated instantaneously.

Example:

```text
Time 1:
Write → Primary

Time 2:
Replica has not received update

Time 3:
Replica receives update
```

This delay is **replication lag**.

Therefore:

```text
Write → Primary
Immediately read → Replica
```

may temporarily return older data.

For operations requiring immediate read-after-write consistency, read from the primary or use a strategy that provides the required consistency guarantee.

---

## 17. Complete Architecture + Interview Answer

```text
                              USERS
                                |
                                v
                       +----------------+
                       | Load Balancer  |
                       +----------------+
                         /      |      \
                        v       v       v
                       GW1     GW2     GW3
                         \      |      /
                          +-----+-----+
                                |
                           API Gateway
                                |
             +------------------+------------------+
             |                  |                  |
             v                  v                  v
        User Service      Podcast Service     Payment Service
          S1 S2               S1 S2              S1 S2
             |                  |                  |
             +------------------+------------------+
                                |
                     +----------+----------+
                     |                     |
                     v                     v
                   Redis              Primary DB
                                         |
                                  +------+------+
                                  |             |
                                  v             v
                            Read Replica 1  Read Replica 2
```

Cloud auto-scaling dynamically changes the number of service instances.

### Strong senior Java interview answer

> "I would use horizontal scaling with multiple application instances behind a load balancer. The services should ideally be stateless so any instance can process any request. If server-side session state is required, I would store it in a shared store such as Redis instead of local memory. I would use an API Gateway for API routing and cross-cutting concerns such as rate limiting and authentication integration. In the cloud, I would use auto-scaling to dynamically add or remove instances based on workload. For a read-heavy database workload, I would use read replicas to offload queries from the primary database, while considering replication lag and consistency requirements."

---

# Senior-Level Follow-Up Questions

### Why horizontal scaling?
To add capacity by adding instances and improve redundancy.

### Why stateless services?
Because requests can be routed to any healthy instance without depending on local state.

### Why Redis?
For shared, fast-access state such as sessions, caching and distributed counters.

### Why not local session memory?
A subsequent request may reach another server that does not have the session.

### Why not always use sticky sessions?
They tie users to particular instances and can reduce flexibility and create uneven load.

### Why load balancer?
To distribute traffic and route around unhealthy instances.

### Load balancer vs API Gateway?
A load balancer primarily distributes traffic among instances. An API Gateway is API-aware and handles routing and cross-cutting API concerns such as rate limiting and authentication integration.

### Why read replicas?
To offload read-heavy workloads from the primary database.

### Main read-replica disadvantage?
Replication lag and the resulting consistency considerations.

### What if Redis goes down?
If it is only a cache, the application may fall back to the database. If it stores mandatory session state, user sessions may be affected. Critical Redis deployments therefore need appropriate high-availability design.

---

# Component Responsibility Cheat Sheet

| Component | Main responsibility |
|---|---|
| Load Balancer | Distribute traffic |
| Round-Robin | Sequential distribution |
| Least Connections | Prefer fewer active connections |
| Health Check | Detect unhealthy instances |
| Stateless Service | Any instance can serve requests |
| JWT | Carry/verify authentication information |
| Redis | Shared fast-access state/cache |
| API Gateway | API routing and cross-cutting API concerns |
| Rate Limiting | Protect services from excessive traffic |
| Auto Scaling | Add/remove instances |
| Primary DB | Authoritative data and typically writes |
| Read Replica | Offload reads |
| Replication | Copy data to replicas |
| Replication Lag | Delay before replica reflects a write |
| Horizontal Scaling | Add instances |
| Fault Tolerance | Continue despite component failures |
| High Availability | Minimize service interruption |

---

# Final Mental Model

```text
Load Balancer
      ↓
Multiple Instances
      ↓
Stateless APIs
      ↓
Redis for shared state
      ↓
Database
      ↓
Read replicas for read offloading

API Gateway
  ├── Routing
  ├── Rate limiting
  └── API policies

Cloud Auto Scaling
  └── Add/remove instances based on demand
```

**One-line summary:**

> Load balancer distributes traffic → stateless services allow any instance to serve requests → Redis provides shared state → API Gateway controls API traffic → auto-scaling adjusts capacity → read replicas offload database reads.

The senior-level point is that each component solves a different problem and introduces its own trade-offs. Always explain both **why you need it** and **what new problem it introduces**.
