# Database Design & Scaling — Java Backend Interview Guide

## 1. When would you choose NoSQL over a relational database?

### Interview Question

> When would you choose a NoSQL database over a relational database?

### Strong Answer

> I would choose NoSQL when the application has unstructured or semi-structured data, needs flexible schema evolution, very high write throughput, or horizontal scaling across many nodes. The choice also depends on the access pattern: MongoDB is useful for document-oriented data, Cassandra for high-write distributed workloads, and Redis or DynamoDB for key-value access. For structured transactional data requiring complex joins and ACID transactions, I would prefer a relational database. In a real system, I would often use **polyglot persistence**, where relational and NoSQL databases are used together based on the requirements.

---

## 1.1 Relational Database

Examples:

- PostgreSQL
- MySQL
- Oracle
- SQL Server

```text
              Database
                 |
       +---------+---------+
       |                   |
    Customer              Order
       |                   |
       +------ Relation ---+
```

Typical tables:

```text
CUSTOMER
+----+----------+---------+
| ID | NAME     | EMAIL   |
+----+----------+---------+
| 1  | Kriti    | ...     |
| 2  | Rahul    | ...     |
+----+----------+---------+

ORDER
+----+-------------+-------+
| ID | CUSTOMER_ID | TOTAL |
+----+-------------+-------+
| 101| 1           | 500   |
| 102| 1           | 800   |
+----+-------------+-------+
```

Relational databases are strong when relationships, joins, structured data, ACID transactions and relational integrity are important.

---

## 1.2 NoSQL Database

Examples:

- MongoDB — document database
- Cassandra — wide-column database
- Redis — key-value/in-memory data store
- DynamoDB — key-value/document database

Example MongoDB document:

```json
{
  "customerId": 101,
  "name": "Kriti",
  "email": "kriti@example.com",
  "preferences": {
    "language": "English",
    "notifications": true
  },
  "addresses": [
    {
      "city": "Indore",
      "type": "home"
    }
  ]
}
```

NoSQL databases are useful when the data model and access pattern benefit from flexible documents, key-value access or distributed horizontal scaling.

---

## 1.3 When NoSQL is a Good Choice

### A. Unstructured / Semi-Structured Data

```text
User
 |
 +-- Name
 +-- Email
 +-- Preferences
 +-- Addresses
 +-- Devices
 +-- Custom fields
```

Different records can have different attributes. A document database can be a natural fit.

### B. Flexible Schema

Version 1:

```json
{
  "id": 1,
  "name": "Java"
}
```

Version 2:

```json
{
  "id": 1,
  "name": "Java",
  "duration": 60,
  "language": "English"
}
```

A document database can accommodate evolving document structures naturally.

**Important:** Flexible schema does not mean no schema. The application should still validate its expected data.

### C. Horizontal Scaling

Vertical scaling:

```text
       Bigger Server
      +-------------+
      |     DB      |
      | CPU ↑       |
      | RAM ↑       |
      +-------------+
```

Horizontal scaling:

```text
             Application
                  |
        +---------+---------+
        |         |         |
        v         v         v
      DB Node   DB Node   DB Node
```

Some NoSQL systems are designed for distributed horizontal scaling.

### D. Very High Write Throughput

```text
Millions of events
        |
        v
     Cassandra
     /   |   \
  Node  Node  Node
```

Cassandra can be considered for high-write, distributed workloads when its data model and consistency characteristics fit the application.

---

## 1.4 Choose NoSQL Based on Access Pattern

### MongoDB

```text
Application
    |
    v
Document
    |
    +-- Customer
    +-- Preferences
    +-- Address
    +-- Metadata
```

Good for document-oriented data.

### Cassandra

```text
High-volume writes
       |
       v
   Cassandra
   /   |   \
 Node Node Node
```

Good for distributed, high-write workloads with predictable query patterns.

### Redis

```text
Key                 Value

session:1001   →    User Session
product:100   →     Product
token:abc      →    Token data
```

Good for very fast key-based access, caching, sessions, counters and rate limiting.

### DynamoDB

```text
Key
 |
 +----> Value / Document
```

Useful for highly scalable managed key-value/document workloads.

---

## 1.5 When Relational is Better

Use a relational database when you need:

### Structured Data

```text
Customer
   |
   v
Order
   |
   v
Payment
```

### Complex Joins

```sql
SELECT c.name, o.total
FROM customer c
JOIN orders o
  ON c.id = o.customer_id;
```

### ACID Transactions

```text
Transfer ₹10,000

Account A
   - ₹10,000
      |
      | Same transaction
      v
Account B
   + ₹10,000
```

You do not want only one side of the operation to succeed.

---

## 1.6 Relational vs NoSQL

| Requirement | Relational | NoSQL |
|---|---|---|
| Structured data | Excellent | Depends on product |
| Flexible schema | Less flexible | Often strong |
| Complex joins | Excellent | Usually limited/different |
| ACID transactions | Strong | Varies by product |
| Horizontal scaling | Possible | Often a core strength |
| Very high distributed writes | Depends | Often strong |
| Document storage | Possible | MongoDB is specialized |
| Key-value access | Possible | Redis/DynamoDB are specialized |
| Relational integrity | Excellent | Usually application/database-specific |
| Best fit | Transactions/relationships | Scale/flexible access patterns |

---

# 2. Polyglot Persistence

## Important Interview Term

> **Polyglot persistence** means using different database technologies in the same application/system, choosing each according to the workload it handles best.

Example:

```text
                         Application
                              |
          +-------------------+-------------------+
          |                   |                   |
          v                   v                   v
     PostgreSQL            MongoDB             Redis
          |                   |                   |
          v                   v                   v
 Orders/Payments        Catalogue/Documents   Sessions/Cache
 Transactions           Flexible data         Fast key-value
```

Example:

```text
PostgreSQL
   |
   +-- Accounts
   +-- Transactions
   +-- Orders
   +-- Payments

MongoDB
   |
   +-- Flexible documents
   +-- Catalogue/configuration

Redis
   |
   +-- Sessions
   +-- Cache
   +-- Rate limiting
```

### Senior-Level Point

Do not choose a database simply because it is popular.

Consider:

```text
Data model
+
Access pattern
+
Consistency
+
Transaction requirements
+
Scale
+
Availability
+
Operational complexity
```

---

# 3. How do you handle database bottlenecks at scale?

## Interview Question

> How do you handle database bottlenecks at scale?

### Strong Interview Answer

> First I would identify the actual bottleneck using metrics, slow-query logs and database monitoring rather than immediately adding infrastructure. For read-heavy workloads, I can use read replicas and caching. For very large datasets, I can use partitioning or sharding. I would optimize queries and eliminate N+1 queries. On the application side, I would use connection pooling such as HikariCP. For systems with different read and write requirements, CQRS can separate read and write models.

---

## 3.1 First Identify the Bottleneck

```text
Application
    |
    v
Database
    |
    +-- CPU?
    +-- Memory?
    +-- Disk I/O?
    +-- Connections?
    +-- Slow queries?
    +-- Locks?
    +-- Network?
```

Measure:

- Query latency
- Queries per second
- CPU utilization
- Memory usage
- Disk I/O
- Connection pool utilization
- Lock waits
- Slow-query count
- Cache hit ratio

---

# 4. Read Replicas

For read-heavy workloads:

```text
                 Application
                      |
          +-----------+-----------+
          |                       |
       WRITE                     READ
          |                       |
          v                       v
    +-----------+         +---------------+
    | Primary DB| ----->  | Read Replica 1|
    +-----------+         +---------------+
          |
          +-------------> +---------------+
                           | Read Replica 2|
                           +---------------+
```

Writes go to the primary.

Reads can be distributed across replicas.

### Benefit

Before:

```text
Application
    |
    v
Primary DB
READ + WRITE
```

After:

```text
Primary   → Writes
Replicas  → Reads
```

### Important Interview Point

Read replicas can have **replication lag**.

Therefore, do not blindly route every read to a replica when read-after-write consistency is required.

---

# 5. Sharding

Sharding means distributing data across multiple database nodes.

Example by user ID:

```text
Users

User ID 1–1M
      |
      v
   Shard 1

User ID 1M–2M
      |
      v
   Shard 2

User ID 2M–3M
      |
      v
   Shard 3
```

Another strategy:

```text
India Users  → India Shard
US Users     → US Shard
Europe Users → Europe Shard
```

The **shard key** is critical.

A poor shard key can create a **hot shard**, where one node receives disproportionate traffic.

---

# 6. CQRS

CQRS = **Command Query Responsibility Segregation**

Separate write and read models.

```text
                 Application
                 /          \
                /            \
          COMMAND            QUERY
             |                  |
             v                  v
        Write Model         Read Model
             |                  |
             v                  v
       Write Database       Read Database
                                |
                                v
                              Cache
```

Why?

Read and write workloads can have different requirements.

```text
Writes → normalized transactional model

Reads → denormalized model optimized for queries
```

CQRS can allow independent scaling, but it adds complexity and may introduce eventual consistency.

---

# 7. Connection Pooling — HikariCP

Creating a database connection for every request is expensive.

Without pooling:

```text
Request
  |
  +-- Create connection
  +-- Query DB
  +-- Close connection
```

With HikariCP:

```text
             Java Application
                    |
                    v
              HikariCP Pool
             /   /   |   \
            v   v    v    v
          Conn Conn Conn Conn
            \   |    |   /
                 DB
```

Connections are reused.

Example configuration:

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

Do not blindly increase pool size. Too many connections can overload the database.

---

# 8. Query Optimization

Look for:

- Full table scans
- Missing indexes
- Unnecessary columns
- Expensive joins
- Poor query plans
- Repeated queries
- N+1 queries

Example:

```sql
SELECT *
FROM orders
WHERE customer_id = ?;
```

An appropriate index may help:

```sql
CREATE INDEX idx_orders_customer_id
ON orders(customer_id);
```

Always verify using the database's execution plan and real workload.

---

# 9. N+1 Query Problem

Suppose we fetch 100 customers:

```text
Query 1:
SELECT * FROM customer;
```

Then:

```text
Query 2:
SELECT * FROM orders WHERE customer_id = 1;

Query 3:
SELECT * FROM orders WHERE customer_id = 2;

...

Query 101:
SELECT * FROM orders WHERE customer_id = 100;
```

Total:

```text
1 + 100 = 101 queries
```

This is the **N+1 query problem**.

---

## N+1 Diagram

```text
Application
    |
    | Query customers
    v
Database
    |
    | 100 customers
    v
Application
    |
    +---- Orders for Customer 1 ----> DB
    +---- Orders for Customer 2 ----> DB
    +---- Orders for Customer 3 ----> DB
    +---- ...
    +---- Orders for Customer 100 -> DB
```

---

# 10. Fixing N+1

Possible approaches:

### Fetch Join

```jpql
SELECT c
FROM Customer c
JOIN FETCH c.orders
```

Conceptually:

```text
One optimized query
       |
       v
Customers + Orders
```

### Entity Graph

```java
@EntityGraph(attributePaths = "orders")
List<Customer> findAll();
```

### Batch Fetching

Instead of:

```text
1 query per customer
```

fetch related records in batches.

### DTO Projection

Fetch only the fields actually required.

### Important

Do not blindly use eager fetching everywhere. It can create huge joins and unnecessary data retrieval.

---

# 11. Database Caching with Redis

For repeated reads:

```text
Application
    |
    v
  Redis
  /   \
HIT   MISS
 |      |
 v      v
Return  DB
          |
          v
        Redis
```

Cache-aside:

```text
Cache HIT  → Return
Cache MISS → DB → Populate Cache → Return
```

Consider:

- TTL
- Cache invalidation
- Cache stampede
- Eviction
- Consistency

---

# 12. Complete Database Scaling Architecture

```text
                         Users
                           |
                           v
                    +-------------+
                    |Load Balancer|
                    +-------------+
                           |
                           v
                    Java Services
                           |
             +-------------+-------------+
             |                           |
             v                           v
          Redis                       Database
          Cache                          |
             |                  +---------+---------+
             |                  |                   |
             |                  v                   v
             |             Primary DB          Read Replica
             |             (Writes)             (Reads)
             |
             +---- Cache HIT
             |
             +---- Cache MISS → DB
```

For very large data:

```text
                   Database Layer
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
       Shard 1        Shard 2        Shard 3
```

---

# 13. What is Database Indexing?

## Interview Question

> What is database indexing? When does it help and when does it hurt?

### Strong Answer

> An index is a separate lookup structure that helps the database find rows without scanning the entire table. It is particularly useful for columns frequently used in WHERE, JOIN and ORDER BY operations. However, indexes consume storage and add write overhead because INSERT, UPDATE and DELETE operations may also need to maintain the indexes. Therefore, I would avoid over-indexing and choose indexes based on actual query patterns and execution plans.

---

# 14. Without an Index

Suppose:

```text
CUSTOMER

ID    NAME
1     A
2     B
3     C
...
1,000,000 rows
```

Query:

```sql
SELECT *
FROM customer
WHERE email = 'kriti@example.com';
```

Without a suitable index, the database may inspect many rows.

```text
Query
  |
  v
Database
  |
  +--> Row 1
  +--> Row 2
  +--> Row 3
  +--> ...
  +--> Row 1,000,000
```

This is often called a **full table scan**.

The exact execution depends on the database optimizer.

---

# 15. With an Index

Create:

```sql
CREATE INDEX idx_customer_email
ON customer(email);
```

Conceptually:

```text
Query
  |
  v
Index
  |
  v
Matching Row Location
  |
  v
Table Row
```

The database can use the index to find candidate rows efficiently.

---

# 16. How an Index Works

A common implementation is a **B-tree/B+tree-style structure**.

```text
                  [M]
                 /   \
               /       \
            [D]         [T]
           /  \         /  \
          A    H       P    Z
```

The database navigates the tree instead of scanning every row.

Exact index structures depend on the database and index type.

---

# 17. When Indexing Helps

## WHERE

```sql
SELECT *
FROM customer
WHERE email = ?;
```

An index on `email` may help.

## JOIN

```sql
SELECT *
FROM customer c
JOIN orders o
  ON c.id = o.customer_id;
```

Appropriate indexes on join columns can improve query performance.

## ORDER BY

```sql
SELECT *
FROM orders
ORDER BY created_at DESC;
```

An appropriate index may reduce sorting work depending on the query and database.

## Frequently Queried Columns

```sql
WHERE customer_id = ?
```

If this is a frequent selective query, an index may be valuable.

---

# 18. When Indexing Hurts

Indexes are not free.

## A. Extra Storage

```text
Table Data
   +
Index 1
   +
Index 2
   +
Index 3
```

## B. INSERT Cost

```text
INSERT
  |
  +---- Update table
  |
  +---- Update Index 1
  |
  +---- Update Index 2
  |
  +---- Update Index 3
```

## C. UPDATE Cost

If an indexed column changes:

```text
UPDATE email
      |
      +---- Update table
      |
      +---- Update email index
```

## D. DELETE Cost

Deleting a row may require index maintenance as well.

---

# 19. Composite Index

A composite index contains multiple columns.

```sql
CREATE INDEX idx_orders_customer_status
ON orders(customer_id, status);
```

Conceptually:

```text
(customer_id, status)
        |
        v
+----------------+
| 101, PAID      |
| 101, SHIPPED   |
| 102, PAID      |
| 103, CANCELLED |
+----------------+
```

---

# 20. Composite Index Order Matters

Suppose:

```text
INDEX(customer_id, status)
```

This is not equivalent to:

```text
INDEX(status, customer_id)
```

For B-tree-style composite indexes, the **leftmost-prefix principle** is important.

For:

```text
INDEX(customer_id, status)
```

the index can generally support:

```sql
WHERE customer_id = ?
```

and:

```sql
WHERE customer_id = ?
AND status = ?
```

But a query only on:

```sql
WHERE status = ?
```

may not use that composite index efficiently for the same purpose.

Always verify using the database execution plan.

---

# 21. "Most Selective Column First" — Important Nuance

You may hear:

> Always put the most selective column first.

This is an oversimplification.

The correct approach is:

> Design composite-index column order based on actual query patterns, predicates, sorting/grouping requirements and database optimizer behavior.

Example:

```sql
WHERE customer_id = ?
AND status = ?
ORDER BY created_at DESC
```

The optimal index may need to consider all three columns and their ordering.

Do not choose index order using selectivity alone.

---

# 22. Over-Indexing

Suppose a table has:

```text
10 columns
```

and you create:

```text
10 indexes
```

This may be unnecessary.

Every index can introduce:

- Storage cost
- Write overhead
- Maintenance cost
- Additional optimizer choices

Therefore:

> Avoid over-indexing.

Create indexes based on real query patterns and measured performance.

---

# 23. Indexing Interview Example

### Question

> A customer table has 10 million rows and this query is slow:

```sql
SELECT *
FROM customer
WHERE email = ?;
```

### Answer

> "First I would inspect the execution plan and confirm whether the query is doing a full table scan. If email is frequently queried and should be unique, I would consider a suitable index, potentially a unique index. Then I would measure the query performance again. I would also consider the write overhead and storage cost before adding indexes."

---

# 24. Senior-Level Database Scaling Framework

When asked:

> How would you scale the database?

Do not immediately answer:

> "Use sharding."

Use this thought process:

```text
1. Measure
     |
     v
2. Identify bottleneck
     |
     v
3. Optimize queries
     |
     v
4. Fix N+1
     |
     v
5. Review indexes
     |
     v
6. Add caching
     |
     v
7. Read replicas
     |
     v
8. Connection pooling
     |
     v
9. CQRS if justified
     |
     v
10. Sharding/partitioning when required
```

The exact order depends on the actual bottleneck.

---

# 25. Scenario-Based Interview Questions

## Scenario 1 — Read Heavy

> Your application has 10,000 reads/sec but only 100 writes/sec. The primary database is overloaded. What would you do?

### Answer

Consider:

```text
Redis Cache
+
Read Replicas
+
Query Optimization
+
Indexes
```

Architecture:

```text
Application
    |
    +---- Redis
    |
    +---- Read Replicas
             |
             v
          Primary
```

---

## Scenario 2 — High Write Volume

> Your system receives millions of writes per second and a single database cannot handle the workload. What would you investigate?

### Answer

Consider:

```text
Partitioning
Sharding
Write-optimized database
Batching
Asynchronous processing
Data model
```

A distributed NoSQL database such as Cassandra may be appropriate if its data model, consistency and access patterns fit the workload.

---

## Scenario 3 — Slow Query

> One query is taking 5 seconds. What would you do?

### Answer

First inspect:

```text
Slow Query
    |
    v
Execution Plan
    |
    +-- Full Scan?
    +-- Missing Index?
    +-- Bad Join?
    +-- Too Many Rows?
    +-- Sort?
    +-- Lock?
```

Then optimize based on evidence.

---

## Scenario 4 — 101 Queries

> Loading 100 customers causes 101 database queries. What is the problem?

### Answer

**N+1 query problem.**

Potential fixes:

```text
Fetch Join
Entity Graph
Batch Fetching
DTO Projection
```

---

## Scenario 5 — Orders and Sessions

> You are designing an e-commerce application. Where would you use relational and NoSQL databases?

### Answer

```text
PostgreSQL/MySQL
    |
    +-- Orders
    +-- Payments
    +-- Transactions

Redis
    |
    +-- Sessions
    +-- Cache
    +-- Rate limiting

MongoDB
    |
    +-- Flexible product/catalogue documents
```

This is:

> **Polyglot persistence**

---

# 26. Final Cheat Sheet

## NoSQL vs Relational

```text
RELATIONAL
→ Structured data
→ Complex joins
→ ACID transactions
→ Strong relational integrity

NOSQL
→ Flexible/semi-structured data
→ Horizontal scaling
→ High write throughput
→ Access-pattern-specific design
```

## Database Scaling

```text
Read-heavy
→ Cache
→ Read replicas

Large dataset
→ Partitioning / Sharding

Read/write separation
→ CQRS

Java DB connections
→ HikariCP

Slow queries
→ Query optimization
→ Indexes

Repeated DB queries
→ Redis

Too many ORM queries
→ Fix N+1
```

## Indexing

```text
INDEX
→ Faster reads
→ Extra storage
→ Extra write cost
```

Good candidates:

```text
WHERE
JOIN
ORDER BY
Frequently queried columns
```

Be careful with:

```text
Too many indexes
Low-selectivity indexes
Write-heavy tables
Poor composite-index ordering
```

---

# 27. One-Minute Interview Answer

> "For database selection, I look at the data model, access pattern, consistency and transaction requirements. Relational databases are usually a strong choice for structured transactional data, complex joins and ACID requirements, while NoSQL can be a better fit for flexible document data, very high distributed write workloads or specific key-value access patterns. In real systems I often use polyglot persistence—for example, a relational database for orders and transactions, MongoDB for flexible documents, and Redis for sessions and caching.
>
> For database bottlenecks, I first measure the actual bottleneck. Then I optimize queries, fix N+1 problems, review indexes, use connection pooling such as HikariCP, add Redis caching for repeated reads, and use read replicas for read-heavy workloads. For larger-scale requirements, I would consider CQRS, partitioning or sharding.
>
> Indexes improve lookup performance by providing an additional search structure, but they consume storage and increase INSERT, UPDATE and DELETE costs. Therefore I create indexes based on real query patterns and execution plans rather than indexing every column."

---

# 28. Key Terms to Remember

```text
NoSQL
Relational Database
Polyglot Persistence
Horizontal Scaling
Read Replica
Sharding
Partitioning
CQRS
HikariCP
N+1 Query Problem
Query Optimization
Database Index
Composite Index
Leftmost Prefix
ACID
Consistency
Replication Lag
Redis
```

## Most Important Interview Terms

> **Polyglot persistence** — use the right database for each workload.

> **N+1 query problem** — one query fetches parent records and then N additional queries fetch related records.

> **Read replica** — a replicated database node that can handle read workloads.

> **Sharding** — distributes data across multiple database nodes.

> **Index** — an additional lookup structure that can speed up reads but adds storage and write-maintenance cost.
