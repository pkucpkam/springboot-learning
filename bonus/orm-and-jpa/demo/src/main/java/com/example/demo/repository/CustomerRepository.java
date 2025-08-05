package com.example.demo.repository;

import com.example.demo.entity.Customer;

import java.util.List;

public interface CustomerRepository {
    void insert(Customer customer);
    Customer selectById(Long id);
    Customer selectByEmail(String email);
    List<Customer> selectAll();
    void update(Customer customer);
    void delete(Customer customer);
}
