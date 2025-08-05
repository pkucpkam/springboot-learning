package com.example.demo.service;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.entity.Customer;

import java.util.List;

public interface CustomerService {
    void createCustomer(Customer customer);
    CustomerDTO getCustomer(Long id);
    CustomerDTO getCustomerByEmail(String email);
    List<CustomerDTO> getAllCustomers();
    void updateCustomer(Long id, CustomerDTO customer);
    void deleteCustomer(Long id);
}
