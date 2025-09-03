package com.redis.redis_optimized;


import com.redis.redis_optimized.service.RedisService;
import com.redis.redis_optimized.service.impl.RedisServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisConnectionTest implements CommandLineRunner {
    private final RedisService redisService;


    @Override
    public void run(String... args) throws Exception {
        try {
            redisService.set("testKey", "Hello Redis");
            String value = redisService.get("testKey").toString();
            System.out.println("✅ Redis connection OK - Value: " + value);
        } catch (Exception e) {
            System.err.println("❌ Redis connection failed: " + e.getMessage());
        }
    }
}
