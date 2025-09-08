package com.example.chatbot_ai_demo;

import com.example.chatbot_ai_demo.entity.Product;
import com.example.chatbot_ai_demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class Seeder {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                List<Product> products = List.of(
                        Product.builder()
                                .name("Laptop ASUS ROG Strix")
                                .category("Laptop")
                                .description("Laptop gaming RTX 4060, RAM 16GB, SSD 1TB")
                                .price(BigDecimal.valueOf(25000000))
                                .build(),
                        Product.builder()
                                .name("Laptop MSI GF63 Thin")
                                .category("Laptop")
                                .description("Laptop gaming RTX 4050, RAM 16GB, SSD 512GB")
                                .price(BigDecimal.valueOf(22000000))
                                .build(),
                        Product.builder()
                                .name("iPhone 13")
                                .category("Điện thoại")
                                .description("iPhone 13, chip A15 Bionic, camera kép 12MP")
                                .price(BigDecimal.valueOf(15000000))
                                .build(),
                        Product.builder()
                                .name("Samsung Galaxy S23 FE")
                                .category("Điện thoại")
                                .description("Điện thoại Samsung S23 FE màn 6.4 inch, Snapdragon 8 Gen 1")
                                .price(BigDecimal.valueOf(12000000))
                                .build(),
                        Product.builder()
                                .name("Tai nghe Sony WH-1000XM5")
                                .category("Phụ kiện")
                                .description("Tai nghe chống ồn chủ động, pin 30h, Bluetooth 5.2")
                                .price(BigDecimal.valueOf(8000000))
                                .build()
                );

                productRepository.saveAll(products);
                System.out.println("✅ Seeded sample products into database!");
            }
        };
    }
}

