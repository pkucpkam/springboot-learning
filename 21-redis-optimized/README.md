

# Redis Caching in Spring Boot

## 1️⃣ Redis Overview
Redis (Remote Dictionary Server) is an **in-memory key-value database**, very fast, often used for:
- Caching temporary data (reduce DB load)
- Session store
- Message broker (Pub/Sub)
- Leaderboard / Rate limiting
- Storing data with various structures: String, Hash, List, Set, Sorted Set, Bitmap, HyperLogLog, Stream

### Key Features
- Stores data in RAM → extremely fast
- Supports TTL (Time to live) for keys
- Supports Spring Boot annotations: `@Cacheable`, `@CachePut`, `@CacheEvict`
- Supports transactions, Lua scripting, Pub/Sub

---

## 2️⃣ Spring Boot Redis Caching

Spring Boot provides **Spring Cache Abstraction**, making Redis integration easy:

| Annotation      | Function |
|-----------------|-----------|
| @Cacheable      | Stores the result of a method in the cache. If the key exists, fetches from cache. |
| @CachePut       | Saves or updates the cache while still executing the method. |
| @CacheEvict     | Removes cache by key or clears all entries. |

**CacheConstants.java**
- Centralized cache/key management
- Helps avoid typos and improves maintainability

**RedisConfig.java**
- Configures CacheManager
- Sets TTL, disables caching null values

---

## 3️⃣ CRUD + Redis Flow

**Execution flow when using Redis caching:**

### 3.1 GET (Read)
1. Controller calls `userService.getUser(id)`
2. `@Cacheable` checks Redis cache:
    - **Hit** → returns data from Redis, DB not called
    - **Miss** → calls DB → stores result in Redis
3. Returns data to Controller → Client

### 3.2 POST / PUT (Create / Update)
1. Controller calls `userService.addUser(user)` or `updateUser(user)`
2. `@CachePut`:
    - Writes to DB
    - Updates the cache with the corresponding key
3. Returns data to Controller → Client

### 3.3 DELETE
1. Controller calls `userService.deleteUser(id)`
2. `@CacheEvict`:
    - Removes data from DB
    - Removes corresponding cache key
3. Returns deletion confirmation

### 3.4 Clear All Cache
- Controller calls `userService.clearCache()`
- `@CacheEvict(allEntries = true)` clears the entire cache
- Useful when data changes frequently to avoid stale cache

---

## 4️⃣ TTL and Null Handling
- TTL: Cache automatically expires after a defined duration (`entryTtl(Duration.ofMinutes(10))`)
- Null values: Nulls are not stored in cache to avoid storing invalid data

---

## 5️⃣ Flow Diagram (Overview)

```
      Client
        |
     Controller
        |
    Service Layer
   /           \


@Cacheable / @CachePut / @CacheEvict
Redis Cache    DB
↑             ↑
└-------------┘

- `@Cacheable` → checks cache before DB
- `@CachePut` → updates cache after DB
- `@CacheEvict` → removes cache on delete/update
- DB is called only on cache miss or update/delete

```
---

## 6️⃣ Notes / Best Practices
- Use CacheConstants to avoid hardcoding keys
- Avoid caching very large data (Redis stores in RAM)
- Set TTL properly to avoid stale data
- Debug cache hits/misses with logs
- Use Redis for **read-heavy workloads** to reduce DB load

---

## 7️⃣ Example annotations in UserService

```java
@Cacheable(value = "userCache", key = "#id")  // GET
@CachePut(value = "userCache", key = "#user.id") // POST/PUT
@CacheEvict(value = "userCache", key = "#id")   // DELETE
@CacheEvict(value = "userCache", allEntries = true) // Clear all cache
```

---
## 8️⃣ Serializable Requirement in Redis

### Why Serializable?

* Spring Boot’s default **JdkSerializationRedisSerializer** serializes Java objects to store them in Redis.
* **Redis stores bytes**, so Java objects must implement `java.io.Serializable`.
* If object is **not Serializable**, you get an exception like:

```
org.springframework.data.redis.serializer.SerializationException: Cannot serialize object of type [demo.redis.User]
```

* This happens because JDK serialization cannot convert the object to bytes.

---

### How to Fix

1. Make the entity implement `Serializable`:

```java
@Entity
@Table(name = "app_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;

    // constructors, getters, setters
}
```

2. **Alternative:** Use JSON serializer for Redis instead of JDK serializer:

```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Jackson JSON serializer
    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
    template.setDefaultSerializer(serializer);

    return template;
}
```

* JSON serializer doesn’t require `Serializable`.
* Makes Redis data **human-readable** and easier to debug.
* Useful when working with complex object graphs or microservices.

---


```
# Summary:
- Redis is an in-memory key-value DB with extremely fast access
- Spring Boot abstraction allows easy caching integration
- @Cacheable, @CachePut, @CacheEvict manage caching
- CRUD + Redis cache flow: cache hit → use cache, cache miss → fetch from DB + save to cache
- TTL + Null handling is important to avoid stale data
- Default behavior: JDK serializer → must implement Serializable.
- Alternative: JSON serializer → no Serializable needed, easier debugging. 
- Key point: Always ensure your objects are serializable before caching in Redis, otherwise cache will fail at runtime.
```