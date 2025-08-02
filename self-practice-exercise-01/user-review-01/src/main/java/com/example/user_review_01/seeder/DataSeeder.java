package com.example.user_review_01.seeder;

import com.example.user_review_01.entity.UserEntity;
import com.example.user_review_01.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {

            // Admin
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("Administrator");
            admin.setEmail("admin@example.com");
            admin.setPhone("0123456789");
            admin.setAddress("123 Admin St, Admin City");
            admin.setNote("System administrator account.");
            admin.setStatus("ACTIVE");

            // Regular User
            UserEntity user = new UserEntity();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setName("Regular User");
            user.setEmail("user@example.com");
            user.setPhone("0987654321");
            user.setAddress("456 User Ave, User Town");
            user.setNote("Regular user account.");
            user.setStatus("ACTIVE");

            // Save to DB
            userRepository.save(admin);
            userRepository.save(user);
        }
    }
}
