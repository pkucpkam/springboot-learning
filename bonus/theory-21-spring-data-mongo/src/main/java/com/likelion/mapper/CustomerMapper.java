package com.likelion.mapper;

import com.likelion.dto.CustomerDTO;
import com.likelion.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toCustomer(CustomerDTO customerDTO);
    CustomerDTO toCustomerDTO(Customer customer);
    List<CustomerDTO> toCustomerDTOs(List<Customer> customers);
    List<Customer> toCustomers(List<CustomerDTO> customerDTOs);
    void updateCustomer(CustomerDTO customerDTO, @MappingTarget Customer customer);
}
