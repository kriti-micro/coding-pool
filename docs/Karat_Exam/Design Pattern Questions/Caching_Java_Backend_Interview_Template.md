# Caching — Java Backend Interview Template

## 1. Problem: Repeated Database Queries

### Interview Question
> A service is slow because it repeatedly queries the same database records. How do you fix it?

### Problem
Without caching:

```text
Users
  |
  v
Java/Spring Service
  |
  +---- SQL ----> Database
  +---- SQL ----> Database
  +---- SQL ----> Database
```

Repeated reads increase database load, latency, connections, and can make the database the bottleneck.

---

## 2. Solution: In-Memory Cache

Use Redis or Memcached in front of the database.

```text
Users
  |
  v
Java Service
  |
  v
+-----------+
|   Redis   |
|   Cache   |
+-----------+
  |       |
 HIT     MISS
  |       |
  |       v
  |   +---------+
  |   | Database|
  |   +---------+
  |       |
  +-------+
```

**Core idea:** check the cache first; query the database only on a cache miss.

---

## 3. Cache Hit

A cache hit means the requested data exists in Redis.

```text
GET /podcasts/100
        |
        v
Java Service
        |
        v
Redis
        |
      FOUND
        |
        v
Return data
```

Example:

```text
Key: podcast:100
Value:
{
  "id": 100,
  "title": "Java Architecture"
}
```

The database is not queried.

---

## 4. Cache Miss

A cache miss means the requested data is not in Redis.

```text
Request
  |
  v
Service
  |
  v
Redis
  |
 MISS
  |
  v
Database
  |
  v
Service
  |
  v
Redis (populate)
  |
  v
User
```

---

## 5. Cache-Aside Pattern

The application manages the cache.

```text
Request
   |
   v
Check Cache
  /      \
HIT      MISS
 |         |
 v         v
Return   Query DB
            |
            v
       Store in Cache
            |
            v
         Return
```

### Pseudocode

```java
public Podcast getPodcast(Long id) {
    String key = "podcast:" + id;

    Podcast cached = redis.get(key);

    if (cached != null) {
        return cached;
    }

    Podcast podcast = repository.findById(id)
            .orElseThrow();

    redis.set(key, podcast, Duration.ofMinutes(10));

    return podcast;
}
```

---

## 6. Why Caching Improves Performance

Without cache:

```text
1000 Requests
      |
      v
1000 DB Queries
```

With cache:

```text
1000 Requests
      |
      v
    Redis
      |
      +---- Many cache hits
      |
      +---- Some cache misses
                    |
                    v
                 Database
```

The benefit depends on the cache hit ratio and workload.

---

## 7. TTL — Time To Live

Cached data can become stale.

```text
Database → "Advanced Java"
Redis    → "Java Basics"
```

Set a TTL:

```text
podcast:100
TTL = 10 minutes
```

After expiry:

```text
Cache Entry
    |
    v
Expired
    |
    v
Cache MISS
    |
    v
Database
    |
    v
Latest Data
    |
    v
Redis
```

TTL should be based on how frequently data changes and how much staleness the business can tolerate.

---

## 8. Cache Eviction

Cache memory is limited.

```text
Cache capacity = 10 GB
Application wants = 50 GB
```

The cache must remove entries.

A common policy is **LRU — Least Recently Used**.

```text
A → used recently
B → used recently
C → not used for 10 min
D → not used for 1 hour
```

With an LRU-style policy, less recently used entries become candidates for eviction.

---

## 9. Cache Stampede / Thundering Herd

A popular key expires:

```text
podcast:100 → EXPIRED
```

Then thousands of users request it simultaneously.

```text
10,000 Requests
       |
       v
     Redis
       |
      MISS
       |
       +----------------------+
       |       |       |      |
       v       v       v      v
      DB      DB      DB     DB
```

All requests hit the database at once.

This is the **cache stampede** or **thundering herd** problem.

---

## 10. Solution: Distributed Lock / Mutex

Allow one request to rebuild the cache.

```text
Requests
   |
   v
Redis → MISS
   |
   v
Acquire Lock
  /        \
YES         NO
 |           |
 v           v
Query DB    Wait
 |
 v
Update Redis
 |
 v
Other requests read Redis
```

Conceptually:

```text
Request 1 → MISS → Lock → DB → Redis
Request 2 → MISS → Wait
Request 3 → MISS → Wait
Request 4 → MISS → Wait

After cache population:

Request 2 → Redis HIT
Request 3 → Redis HIT
Request 4 → Redis HIT
```

For multiple application instances, a local Java `synchronized` block is not sufficient for a distributed lock.

---

## 11. Probabilistic Early Expiry

Instead of letting a popular key expire for everyone at exactly the same moment, refresh it slightly before expiry.

```text
Normal expiry: 10:00

Possible early refresh:
09:58
09:59
```

This spreads refresh work and reduces sudden database spikes.

---

## 12. Stale Data

Example:

```text
Database:
Podcast = "Advanced Java"

Redis:
Podcast = "Java Basics"
```

The application may return old information.

This is **stale data**.

Solutions include:

- Appropriate TTL
- Explicit invalidation
- Event-driven invalidation
- Versioned keys

---

## 13. Event-Driven Cache Invalidation

When data changes:

```text
Update
  |
  v
Database
  |
  v
Publish Event
  |
  v
Cache Invalidation
  |
  v
Redis
  |
DELETE podcast:100
```

Next read:

```text
GET podcast:100
       |
       v
Redis MISS
       |
       v
Database
       |
       v
Latest data
       |
       v
Redis
```

Possible technologies include Pub/Sub, Kafka, queues, or application events.

---

## 14. Versioned Cache Keys

Instead of:

```text
podcast:100
```

use:

```text
podcast:100:v1
```

After an update:

```text
podcast:100:v2
```

Example:

```text
v1 → Java Basics
v2 → Advanced Java
```

Versioning helps switch consumers to a new representation, but old keys still need TTL or cleanup.

---

## 15. Cache Invalidation

The core problem is:

```text
Database changes
      |
      v
How do we make cache reflect the change?
```

Common strategies:

### TTL

```text
Wait for expiry
```

### Explicit invalidation

```text
DB Update
   |
   v
DELETE Redis key
```

### Event-driven

```text
DB Update
   |
   v
Event
   |
   v
Invalidate Redis
```

### Versioning

```text
v1 → old
v2 → new
```

Choose based on consistency requirements.

---

## 16. Cache-Aside Write Flow

A common approach:

```text
Update Request
      |
      v
Application
      |
      v
Database
      |
      v
Invalidate Cache
      |
      v
Redis
```

Example:

```text
PUT /podcasts/100

1. Update DB
2. Delete/invalidate podcast:100
```

Then:

```text
GET /podcasts/100
       |
       v
Redis MISS
       |
       v
Database
       |
       v
Latest data
       |
       v
Redis
```

Exact ordering and failure handling should match the required consistency guarantees.

---

## 17. When NOT to Use Caching

### A. Frequently changing critical data

If stale data causes business problems, ordinary caching may be inappropriate.

```text
100 → 101 → 102 → 103 → 104
```

A cache may return an older value.

### B. Financial / transactional authoritative state

Examples:

- Account balance
- Available funds
- Transaction status

The database/transaction system should remain the authoritative source of truth.

### C. Very low-traffic endpoints

If an endpoint receives only a few requests per day, Redis may add more complexity than value.

### D. Write-heavy workloads

Caching primarily helps repeated reads.

```text
1,000,000 writes/sec
10 reads/sec
```

A read cache does not directly solve the write bottleneck.

---

# 18. Complete Caching Architecture

```text
                         Users
                           |
                           v
                    +-------------+
                    |Load Balancer|
                    +-------------+
                           |
                           v
                    Java Service
                           |
                           v
                    +-------------+
                    |    Redis    |
                    |    Cache    |
                    +-------------+
                      |         |
                   HIT|         |MISS
                      |         |
                      |         v
                      |    +-----------+
                      |    | Database  |
                      |    +-----------+
                      |         |
                      |         v
                      |   Populate Cache
                      |         |
                      +---------+
                           |
                           v
                          User
```

---

# 19. Cache Stampede Protection

```text
                    Requests
                       |
                       v
                 Java Service
                       |
                       v
                    Redis
                   /     \
                HIT       MISS
                |           |
                v           v
             Return     Acquire Lock
                            |
                   +--------+--------+
                   |                 |
                 Got Lock         Lock Exists
                   |                 |
                   v                 v
                Query DB            Wait
                   |                 |
                   v                 |
              Update Redis <---------+
                   |
                   v
                 Return
```

---

# 20. Java/Spring Pseudocode

```java
public Podcast getPodcast(Long id) {

    String key = "podcast:" + id;

    // Check cache
    Podcast cached = redis.get(key);

    if (cached != null) {
        return cached;
    }

    // Cache miss
    Podcast podcast = repository.findById(id)
            .orElseThrow();

    // Populate cache
    redis.set(
        key,
        podcast,
        Duration.ofMinutes(10)
    );

    return podcast;
}
```

---

# 21. Interview Answer — Repeated DB Queries

### Question

> A service is slow because it repeatedly queries the same database records. How do you fix it?

### Strong Answer

> "I would consider adding an in-memory cache such as Redis in front of the database. I would use the cache-aside pattern: the application first checks Redis; on a cache hit it returns the cached value, and on a miss it queries the database and populates Redis. I would choose TTL based on the acceptable freshness of the data and configure an appropriate eviction policy such as LRU where applicable. I would also monitor cache hit ratio, latency and memory usage."

---

# 22. Interview Answer — Caching Problems

### Question

> What problems can arise with caching?

### Strong Answer

> "The main issues are cache stampede, stale data and cache invalidation. A cache stampede can happen when many requests see the same expired key and simultaneously hit the database. I can mitigate it using a distributed lock or early/probabilistic refresh. For stale data, I can use an appropriate TTL or event-driven invalidation. For invalidation, I can explicitly delete or update keys after writes, use events/pub-sub, or use versioned keys depending on the consistency requirements."

---

# 23. Interview Answer — When Not to Cache

### Question

> When should you not use caching?

### Strong Answer

> "I wouldn't use normal caching when data changes very frequently and stale values can cause business problems, particularly for authoritative financial or transactional state. I would also avoid adding a cache to very low-traffic endpoints where the operational overhead isn't justified. Caching primarily addresses repeated reads, so it doesn't by itself solve a write-heavy database bottleneck."

---

# 24. Cache Interview Cheat Sheet

| Concept | Meaning | Typical Solution |
|---|---|---|
| Cache Hit | Data found in cache | Return cached data |
| Cache Miss | Data not found | Query DB and populate |
| Cache-Aside | Application manages cache | Check → DB on miss → Cache |
| TTL | Expiration time | Set based on freshness |
| LRU | Least Recently Used | Evict less recently used entries |
| Cache Stampede | Many misses hit DB together | Distributed lock / early refresh |
| Stale Data | Cache contains old data | TTL / invalidation |
| Cache Invalidation | Remove/update old cache | Delete / events / versioning |
| Redis | In-memory shared store | Cache/session/rate limiting |
| Read-heavy workload | Many repeated reads | Good cache candidate |
| Write-heavy workload | Many writes | Cache does not directly solve writes |

---

# 25. Three Things to Remember

## How to make repeated DB reads faster?

```text
Application
     |
     v
   Redis
     |
   MISS
     |
     v
 Database
```

**Cache hit:** Return cache.

**Cache miss:** DB → Cache → Return.

---

## What can go wrong?

Remember:

```text
STAMPede
STALE DATA
INVALIDATION
```

Solutions:

```text
Stampede → Distributed Lock / Early Refresh
Stale    → TTL / Events
Invalid  → Delete / PubSub / Versioning
```

---

## When NOT to cache?

Remember:

```text
Frequently changing critical data
Financial/transactional authoritative state
Very low traffic
Write-heavy bottleneck
```

---

# 26. Senior-Level Mental Model

Do not simply say:

> "Use Redis."

Say:

> "I would use Redis if repeated reads justify the additional complexity. I would use the cache-aside pattern, choose TTL based on freshness requirements, select an appropriate eviction policy, monitor cache hit ratio and latency, protect against cache stampedes, define an explicit invalidation strategy, and verify that the application's consistency requirements allow caching."

---

# 27. Final Summary

```text
Cache-aside
    |
    +-- HIT  → Return cache
    |
    +-- MISS → Database
                  |
                  v
               Cache
                  |
                  v
                Return
```

Remember:

**Cache-aside** → application checks cache first.

**TTL** → controls how long cached data lives.

**Eviction** → controls what happens when cache memory is limited.

**Cache stampede** → many requests miss simultaneously and overload DB.

**Distributed lock / early refresh** → reduces stampede.

**Stale data** → cache is older than source of truth.

**Invalidation** → removes or updates stale entries.

**Do not cache blindly** → consistency, traffic pattern, and business requirements matter.

## One-line interview summary

> **Use caching to reduce repeated database reads, but design TTL, eviction, stampede protection, invalidation, and consistency deliberately rather than simply adding Redis.**
