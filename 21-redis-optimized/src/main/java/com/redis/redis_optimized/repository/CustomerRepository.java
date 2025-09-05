package com.redis.redis_optimized.repository;

import com.redis.redis_optimized.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.address = %:address%") // JPQL
    List<Customer> findByAddress(@Param("address") String address);

    Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.email = :email") // JPQL
    Customer findByEmailWithJPQL(@Param("email") String email);

    @Query(nativeQuery = true,
            value = "SELECT * FROM customers c WHERE c.email = :email") // SQL
    Customer findByEmailWithSQL(@Param("email") String email);
}
