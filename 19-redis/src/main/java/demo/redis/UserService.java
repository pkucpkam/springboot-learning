package demo.redis;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy user, nếu có trong cache thì dùng cache
    @Cacheable(value = CacheConstants.USER_CACHE, key = "#id")
    public Optional<User> getUser(Long id) {
        System.out.println("Fetching user from DB: " + id);
        return userRepository.findById(id);
    }

    // Thêm user và cache
    @CachePut(value = CacheConstants.USER_CACHE, key = "#result.id")
    public User addUser(User user) {
        User saved = userRepository.save(user);
        System.out.println("Added user to DB: " + saved);
        return saved;
    }

    // Cập nhật user và cache
    @CachePut(value = CacheConstants.USER_CACHE, key = "#user.id")
    public User updateUser(User user) {
        User updated = userRepository.save(user);
        System.out.println("Updated user in DB: " + updated);
        return updated;
    }

    // Xóa user và cache
    @CacheEvict(value = CacheConstants.USER_CACHE, key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        System.out.println("Deleted user from DB: " + id);
    }

    // Xóa toàn bộ cache
    @CacheEvict(value = CacheConstants.USER_CACHE, allEntries = true)
    public void clearCache() {
        System.out.println("Cleared all user cache");
    }
}

