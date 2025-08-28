package demo.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserSeeder(UserRepository userRepository,
                      RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed database
        List<User> users = List.of(
                new User(null, "Alice", 25),
                new User(null, "Bob", 30),
                new User(null, "Charlie", 22)
        );

        users.forEach(user -> {
            User saved = userRepository.save(user);

            // Seed Redis cache
            String key = CacheConstants.USER_CACHE + "::" + saved.getId();
            redisTemplate.opsForValue().set(key, saved);
            System.out.println("Seeded user to DB and Redis: " + saved);
        });
    }
}

