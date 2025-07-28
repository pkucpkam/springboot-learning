package com.example.security_with_spring_security.repository;

import com.example.security_with_spring_security.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
