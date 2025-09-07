package com.likelion.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.likelion.dto.CustomerDto;
import com.likelion.entity.Customer;

@Mapper(componentModel = "spring") // allows Spring to auto-inject the mapper
public interface CustomerMapper {
    CustomerDto toDto(Customer entity);
    Customer toEntity(CustomerDto dto);
    List<CustomerDto> toDtoList(List<Customer> customers);
    void updateEntity(CustomerDto dto, @MappingTarget Customer entity);
}
