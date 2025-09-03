package com.redis.redis_optimized.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void set(String key, Object value, long ttl, TimeUnit unit);
    void set(String key, Object value);

    Object get(String key);
}
