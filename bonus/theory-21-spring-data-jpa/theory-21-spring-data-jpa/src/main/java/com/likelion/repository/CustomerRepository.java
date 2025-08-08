package com.likelion.repository;

import com.likelion.dto.CustomerDTO;
import com.likelion.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByEmail(String email);

}
