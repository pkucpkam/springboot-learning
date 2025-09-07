package com.likelion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.entity.Customer;

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
