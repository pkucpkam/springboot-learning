package com.likelion.service;

import com.likelion.dto.CustomerDTO;
import com.likelion.entity.Customer;

import java.util.List;

public interface CustomerService {

    CustomerDTO findCustomerById(String id);
    List<CustomerDTO> findAllCustomers();
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(String id ,CustomerDTO customerDTO);
    void deleteCustomer(String id);
}
