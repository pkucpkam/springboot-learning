package com.likelion.service;

import java.util.List;

import com.likelion.dto.CustomerDto;

public interface CustomerService {
    void createCustomer(CustomerDto customerDto);
    CustomerDto getCustomerById(Long id);
    CustomerDto getCustomerByEmail(String email);
    List<CustomerDto> getAllCustomers();
    List<CustomerDto> getCustomersByAdress(String address);
    void updateCustomer(Long id, CustomerDto customerDto);
    void deleteCustomer(Long id);
}
