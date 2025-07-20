package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Truy vấn JPQL tìm sản phẩm theo tên chính xác
    @Query("SELECT p FROM Product p WHERE p.name = :name")
    List<Product> findByExactName(@Param("name") String name);
}
