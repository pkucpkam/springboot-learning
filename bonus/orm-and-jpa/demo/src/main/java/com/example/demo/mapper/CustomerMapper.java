package com.example.demo.mapper;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.entity.Customer;
import org.springframework.stereotype.Component;

public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO customerDTO);
    void updateEntity(CustomerDTO customerDTO, Customer customer);
}
