package com.redis.redis_optimized.service;

import com.redis.redis_optimized.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDto);
    CustomerDTO getCustomerById(Long id);
    CustomerDTO getCustomerByEmail(String email);
    List<CustomerDTO> getAllCustomers();
    List<CustomerDTO> getCustomersByAdress(String address);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDto);
    void deleteCustomer(Long id);
}
