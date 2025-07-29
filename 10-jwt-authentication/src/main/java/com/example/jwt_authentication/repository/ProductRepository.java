package com.example.jwt_authentication.repository;

import com.example.jwt_authentication.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
