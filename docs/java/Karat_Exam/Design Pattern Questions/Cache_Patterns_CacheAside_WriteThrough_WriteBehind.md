# Caching Patterns — Cache-Aside vs Write-Through vs Write-Behind

## 1. What are these patterns?

These are different strategies for deciding **when data is written to the cache and database**.

The three important patterns are:

1. **Cache-Aside (Lazy Loading)**
2. **Write-Through**
3. **Write-Behind (Write-Back)**

The biggest difference is **where and when the application writes data**.

---

# 2. Quick Comparison

| Pattern | Read | Write | Main Benefit | Main Risk |
|---|---|---|---|---|
| Cache-Aside | App checks cache, then DB on miss | App updates DB and usually invalidates cache | Simple and flexible | Cache miss / stale data |
| Write-Through | Read from cache after population | Cache → DB synchronously | Cache and DB stay closely synchronized | Higher write latency |
| Write-Behind | Read from cache | Cache immediately, DB asynchronously | Very fast writes | Data loss / consistency risk |

### Easy way to remember

```text
Cache-Aside   → Application manages cache
Write-Through → Write Cache + DB before success
Write-Behind  → Write Cache first, DB later
```

---

# 3. Pattern 1 — Cache-Aside / Lazy Loading

## Definition

In **Cache-Aside**, the application is responsible for checking and populating the cache.

The cache does not automatically load data from the database.

---

## Architecture

```text
                         READ
                          |
                          v
                   +--------------+
                   | Application  |
                   +--------------+
                          |
                          v
                     +--------+
                     | Redis  |
                     +--------+
                       /    \
                    HIT      MISS
                     |         |
                     |         v
                     |      +------+
                     |      |  DB  |
                     |      +------+
                     |         |
                     |         v
                     |    Populate Cache
                     |         |
                     +---------+
                          |
                          v
                       Response
```

---

# 4. Cache-Aside Read Flow

Suppose the user requests:

```text
GET /podcasts/100
```

## Step 1 — Application checks Redis

```text
Application
     |
     | GET podcast:100
     v
   Redis
```

### If found

```text
Redis
  |
  | HIT
  v
Podcast data
  |
  v
Application
  |
  v
User
```

This is a **cache hit**.

---

## Step 2 — Cache Miss

If Redis doesn't contain the record:

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

Database returns the record.

Then the application puts it into Redis:

```text
Database
    |
    v
Application
    |
    +---- SET podcast:100 ----> Redis
    |
    v
  User
```

---

# 5. Cache-Aside Write Flow

A common write strategy is:

```text
                WRITE
                  |
                  v
            Application
               /    \
              /      \
             v        v
        Database    Redis
                     |
                  Invalidate
```

More commonly:

```text
Application
     |
     v
 Database
     |
     | Update successful
     v
 Invalidate Redis
     |
     v
 Redis DELETE podcast:100
```

Then the next read gets fresh data:

```text
GET podcast:100
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
Latest data
       |
       v
    Redis
```

---

# 6. Cache-Aside Example

```java
public Podcast getPodcast(Long id) {

    String key = "podcast:" + id;

    // 1. Check cache
    Podcast podcast = redis.get(key);

    if (podcast != null) {
        return podcast;       // Cache HIT
    }

    // 2. Cache MISS
    podcast = repository.findById(id)
            .orElseThrow();

    // 3. Populate cache
    redis.set(key, podcast, Duration.ofMinutes(10));

    return podcast;
}
```

Write:

```java
public void updatePodcast(Podcast podcast) {

    // Update source of truth
    repository.save(podcast);

    // Invalidate cache
    redis.delete("podcast:" + podcast.getId());
}
```

---

# 7. Advantages of Cache-Aside

- Simple to understand
- Application has full control
- Good for read-heavy workloads
- Only frequently accessed data needs to be cached
- Database remains the source of truth
- Works well with Redis

---

# 8. Disadvantages of Cache-Aside

- Cache miss causes database access
- Application must manage cache logic
- Cache invalidation is difficult
- Possible stale data
- Cache stampede can happen
- First request after expiry can be slower

---

# 9. Pattern 2 — Write-Through Cache

## Definition

In **Write-Through**, the application writes to the cache, and the cache synchronously writes the data to the database.

The write is considered complete only after the database update succeeds.

---

## Architecture

```text
                 WRITE
                   |
                   v
             Application
                   |
                   v
              +--------+
              | Redis  |
              +--------+
                   |
                   | Synchronous Write
                   v
              +---------+
              |Database |
              +---------+
                   |
                   v
              Write Success
                   |
                   v
               Response
```

### Key idea

```text
Application
     |
     v
   Cache
     |
     v
 Database
```

The cache is part of the write path.

---

# 10. Write-Through Write Flow

Suppose:

```text
PUT /podcasts/100
```

Flow:

```text
User
 |
 | PUT
 v
Application
 |
 v
Redis
 |
 | Write-through
 v
Database
 |
 | Success
 v
Redis
 |
 v
Application
 |
 v
User
```

The application doesn't simply write to Redis and immediately return.

It waits for the database operation to complete.

---

# 11. Why Use Write-Through?

Suppose the application updates:

```text
Podcast 100
title = "Advanced Java"
```

Write-through ensures:

```text
Redis:
Advanced Java

Database:
Advanced Java
```

After a successful write.

The cache is therefore kept populated with the latest value.

---

# 12. Write-Through Read Flow

After the data has been populated:

```text
User
 |
 v
Application
 |
 v
Redis
 |
 | HIT
 v
Data
 |
 v
User
```

The application can get frequently accessed data directly from cache.

---

# 13. Write-Through Example

Conceptually:

```java
public void updatePodcast(Podcast podcast) {

    // Write to cache
    redis.set(
        "podcast:" + podcast.getId(),
        podcast
    );

    // Cache layer synchronously updates DB
    repository.save(podcast);
}
```

In a real architecture, write-through is often implemented by a cache/data-access layer rather than manually doing these operations in every service method.

---

# 14. Advantages of Write-Through

- Cache contains newly written data
- Good cache consistency relative to the completed write
- Subsequent reads are less likely to miss
- Useful when recently written data is likely to be read immediately
- Application does not need to separately invalidate the updated cache entry in the common successful-write path

---

# 15. Disadvantages of Write-Through

- Higher write latency because DB write is synchronous
- Every write may update cache even if the data is rarely read
- More infrastructure complexity
- Cache and database failure handling becomes important
- Does not eliminate all consistency problems or distributed-system failure cases

---

# 16. Pattern 3 — Write-Behind / Write-Back

## Definition

In **Write-Behind**, the application writes to the cache first.

The cache acknowledges the write immediately, and the database is updated **asynchronously later**.

---

## Architecture

```text
                 WRITE
                   |
                   v
             Application
                   |
                   v
              +--------+
              | Redis  |
              +--------+
                   |
             Immediate ACK
                   |
                   v
                User


Later / Asynchronously:

              Redis
                |
                v
          Queue / Worker
                |
                v
            Database
```

### Key idea

```text
Write Cache NOW
Write Database LATER
```

---

# 17. Write-Behind Detailed Flow

Suppose:

```text
PUT /podcasts/100
```

Flow:

```text
User
 |
 v
Application
 |
 v
Redis
 |
 | Write
 v
ACK immediately
 |
 v
User


Background process
       |
       v
Read pending update
       |
       v
Database
```

The user doesn't wait for the database update.

---

# 18. Why Write-Behind Is Fast

Compare:

### Write-Through

```text
User
 |
 v
Application
 |
 v
Cache
 |
 v
Database
 |
 | Wait
 v
Response
```

### Write-Behind

```text
User
 |
 v
Application
 |
 v
Cache
 |
 v
Response

Database update happens later
```

Therefore write-behind can provide very low write latency.

---

# 19. Write-Behind Example

Suppose a podcast receives thousands of likes.

Instead of doing:

```text
Like
 |
 v
Database
 |
 v
Like
 |
 v
Database
 |
 v
Like
 |
 v
Database
```

you could temporarily accumulate updates:

```text
Users
  |
  v
Redis
  |
  | 10,000 likes
  v
Queue / Batch Worker
  |
  v
Database
```

The worker can batch updates:

```text
10,000 individual updates

        ↓

1 or several batched DB operations
```

This can significantly reduce database write pressure.

---

# 20. Advantages of Write-Behind

- Very fast writes
- Reduces immediate database load
- Can batch multiple writes
- Useful for high-volume write workloads
- Database writes can be optimized asynchronously

---

# 21. Disadvantages of Write-Behind

This pattern has the biggest consistency risk.

### Problem 1 — Data Loss

Suppose:

```text
User
 |
 v
Redis
 |
 | Write successful
 v
Response sent
```

But before the database is updated:

```text
Redis crashes
```

The database may never receive the update.

---

### Problem 2 — Temporary Inconsistency

For some period:

```text
Redis:
Likes = 10,000

Database:
Likes = 9,500
```

The cache is ahead of the database.

---

### Problem 3 — Ordering

Suppose:

```text
Update A → value = 100
Update B → value = 200
```

If asynchronous processing happens out of order:

```text
B processed first
A processed second
```

the database could incorrectly end up with:

```text
100
```

instead of:

```text
200
```

The implementation therefore needs appropriate ordering/idempotency guarantees.

---

# 22. Cache-Aside vs Write-Through vs Write-Behind

## Diagram Comparison

### Cache-Aside

```text
READ:

App → Cache
       |
      MISS
       v
      DB
       |
       v
     Cache


WRITE:

App → DB
      |
      v
Invalidate Cache
```

---

### Write-Through

```text
READ:

App → Cache


WRITE:

App → Cache → DB
            |
         Synchronous
```

---

### Write-Behind

```text
READ:

App → Cache


WRITE:

App → Cache
       |
   Immediate ACK
       |
       v
Async Worker
       |
       v
      DB
```

---

# 23. Main Difference — Who Controls the Database Write?

```text
CACHE-ASIDE

Application
    |
    +----> Database
    |
    +----> Cache


WRITE-THROUGH

Application
    |
    v
  Cache
    |
    v
 Database


WRITE-BEHIND

Application
    |
    v
  Cache
    |
    v
 Immediate Response

      ↓ later

 Background Worker
      |
      v
   Database
```

---

# 24. Detailed Comparison Table

| Feature | Cache-Aside | Write-Through | Write-Behind |
|---|---|---|---|
| Read strategy | Cache first, DB on miss | Usually cache first | Usually cache first |
| Cache populated on read miss | Yes | Typically yes | Typically yes |
| Write goes to cache | Not necessarily | Yes | Yes |
| Write goes to DB | Application writes DB | Synchronously through cache | Asynchronously later |
| Write latency | Normal DB latency | Higher | Very low |
| DB always immediately updated? | Yes, after successful write path | Yes, synchronously | No |
| Stale cache risk | Yes | Lower after successful writes | Different consistency model; cache may be ahead |
| Data loss risk | Lower from cache alone | Lower, depending on implementation | Higher if pending writes aren't durably handled |
| Complexity | Low | Medium | High |
| Best for | Read-heavy systems | Read + write systems needing populated cache | High-volume write workloads |
| Main problem | Invalidation | Write latency | Consistency/data loss |

---

# 25. Example: Podcast Website

Imagine:

```text
Podcast Service
```

Data:

```text
Podcast ID = 100
Title = "Java Architecture"
Views = 1,000,000
```

---

## Cache-Aside

### Read

```text
User
 |
 v
Podcast Service
 |
 v
Redis
 |
 +-- HIT → Return
 |
 +-- MISS → DB → Redis → Return
```

### Best when

```text
Many reads
Few writes
```

Example:

```text
100,000 podcast views
100 metadata updates
```

---

## Write-Through

```text
User updates podcast
       |
       v
Application
       |
       v
Redis
       |
       v
Database
```

After success:

```text
Redis = latest
Database = latest
```

Good when recently written data is likely to be read immediately.

---

## Write-Behind

Suppose podcast view count receives massive traffic:

```text
1,000,000 views
```

Instead of synchronously writing every view:

```text
1,000,000 DB writes
```

use:

```text
Users
  |
  v
Redis
  |
  v
Queue / Worker
  |
  v
Batch DB update
```

This can reduce database write pressure.

But you must accept and properly handle asynchronous consistency.

---

# 26. Interview Scenario Questions

## Scenario 1

> A product catalog is read millions of times but changes only occasionally. Which pattern?

### Answer

**Cache-Aside** is a strong default.

```text
Read → Cache
       |
      MISS
       v
      DB
       |
       v
     Cache
```

Reason:

- Read-heavy
- Data changes relatively infrequently
- Simple
- Database remains source of truth

---

## Scenario 2

> Users frequently read data immediately after updating it. Which pattern can be useful?

### Answer

**Write-Through**

```text
Update
  |
  v
Cache
  |
  v
DB
```

The updated value is placed in cache as part of the successful write path, so the subsequent read can hit the cache.

---

## Scenario 3

> An application receives millions of writes and database writes are the bottleneck. Which pattern might help?

### Answer

**Write-Behind**

```text
Millions of writes
       |
       v
     Cache
       |
       v
Async Queue/Worker
       |
       v
     Database
```

The system can batch or asynchronously process updates.

But mention:

> "I would use write-behind only if the business can tolerate asynchronous persistence and I can make pending writes durable and recoverable."

---

# 27. Important Interview Trap

### Interviewer:

> Is Write-Behind always better because it's faster?

### Correct answer:

**No.**

Write-behind trades consistency and persistence guarantees for write performance.

```text
Faster writes
     ↕
More asynchronous complexity
     ↕
Higher risk if pending updates are lost
```

You need to evaluate:

- Data durability
- Ordering
- Retry
- Idempotency
- Failure recovery
- Eventual consistency
- Business tolerance for delayed persistence

---

# 28. Easy Memory Trick

Remember the direction.

### Cache-Aside

```text
Application
 ↙       ↘
Cache    DB
```

Application manages both.

### Write-Through

```text
Application
     |
   Cache
     |
     v
    DB
```

Cache writes through to DB.

### Write-Behind

```text
Application
     |
   Cache
     |
     ↓ later
    DB
```

Cache writes behind the application response.

---

# 29. Senior-Level Interview Answer

> "For a read-heavy system, I would normally start with cache-aside because it is simple and gives the application control over what gets cached. On a cache miss, the application reads from the database and populates the cache. For write-through, writes go through the cache and synchronously reach the database, which keeps the cache populated with the latest successfully persisted value but adds write latency. Write-behind acknowledges the write after updating the cache and persists it to the database asynchronously, which can greatly improve write throughput and allow batching, but introduces eventual consistency, ordering, durability, and failure-recovery concerns. So I would choose based on the read/write pattern and, most importantly, the required consistency and durability guarantees."

---

# 30. Final Cheat Sheet

```text
                CACHING PATTERNS
                       |
        +--------------+--------------+
        |              |              |
        v              v              v
  CACHE-ASIDE    WRITE-THROUGH   WRITE-BEHIND
        |              |              |
        v              v              v
   App manages     Cache → DB      Cache → DB
   Cache + DB      synchronously   asynchronously
        |              |              |
        v              v              v
   Simple/Flexible  Consistency     Fast writes
        |              |              |
        v              v              v
   Invalidation     Write latency   Data-loss risk
   / stampede       / complexity    / eventual consistency
```

## One-line definitions

**Cache-Aside:**
> Application checks cache first; on miss it reads DB and populates cache.

**Write-Through:**
> Application writes through the cache, and the cache synchronously persists to DB before the write is considered complete.

**Write-Behind:**
> Application writes to cache and gets a fast response; the cache/database layer persists to DB asynchronously later.

## Best interview keywords

```text
Cache-Aside
→ Lazy Loading
→ Cache Hit / Miss
→ TTL
→ Invalidation
→ Cache Stampede

Write-Through
→ Synchronous
→ Cache + DB
→ Lower stale-read risk after successful write
→ Higher write latency

Write-Behind
→ Asynchronous
→ Eventual consistency
→ Batching
→ High write throughput
→ Durability
→ Retry
→ Ordering
→ Idempotency
→ Failure recovery
```
