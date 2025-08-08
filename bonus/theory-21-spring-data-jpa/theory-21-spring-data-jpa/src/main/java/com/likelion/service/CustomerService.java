package com.likelion.service;

import com.likelion.dto.CustomerDTO;
import com.likelion.entity.Customer;

import java.util.List;

public interface CustomerService {
    void createCustomer(CustomerDTO customerDTO);
    CustomerDTO getCustomerById(Long id);
    CustomerDTO getCustomerByEmail(String email);
    List<CustomerDTO> getAllCustomers();
    void updateCustomer(Long Id, CustomerDTO customerDTO);
    void deleteCustomer(Long id);
}
