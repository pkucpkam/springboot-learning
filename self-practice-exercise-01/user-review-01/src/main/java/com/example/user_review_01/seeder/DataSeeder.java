package com.example.user_review_01.seeder;

import com.example.user_review_01.entity.RoleEntity;
import com.example.user_review_01.entity.UserEntity;
import com.example.user_review_01.repository.RoleRepository;
import com.example.user_review_01.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Chỉ seed nếu database trống
        if (userRepository.count() == 0 && roleRepository.count() == 0) {
            // Khởi tạo các vai trò
            RoleEntity userRole = new RoleEntity("user");
            RoleEntity sellerRole = new RoleEntity("seller");
            RoleEntity adminRole = new RoleEntity("admin");

            // Lưu vai trò vào database
            roleRepository.save(userRole);
            roleRepository.save(sellerRole);
            roleRepository.save(adminRole);

            // Tạo tài khoản Admin
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("Administrator");
            admin.setEmail("admin@example.com");
            admin.setPhone("0123456789");
            admin.setAddress("123 Admin St, Admin City");
            admin.setNote("System administrator account.");
            admin.setStatus("ACTIVE");
            admin.addRole(adminRole);
            admin.addRole(userRole); // Admin cũng có vai trò user

            // Tạo tài khoản Regular User
            UserEntity user = new UserEntity();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setName("Regular User");
            user.setEmail("user@example.com");
            user.setPhone("0987654321");
            user.setAddress("456 User Ave, User Town");
            user.setNote("Regular user account.");
            user.setStatus("ACTIVE");
            user.addRole(userRole);

            // Tạo tài khoản Seller
            UserEntity seller = new UserEntity();
            seller.setUsername("seller");
            seller.setPassword(passwordEncoder.encode("seller123"));
            seller.setName("Seller User");
            seller.setEmail("seller@example.com");
            seller.setPhone("0912345678");
            seller.setAddress("789 Seller Rd, Seller City");
            seller.setNote("Seller account for e-commerce platform.");
            seller.setStatus("ACTIVE");
            seller.addRole(sellerRole);
            seller.addRole(userRole); // Seller cũng có vai trò user

            // Lưu tất cả vào database
            userRepository.save(admin);
            userRepository.save(user);
            userRepository.save(seller);
        }
    }
}