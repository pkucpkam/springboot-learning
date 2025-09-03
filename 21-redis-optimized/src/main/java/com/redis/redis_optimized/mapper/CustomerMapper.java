package com.redis.redis_optimized.mapper;


import com.redis.redis_optimized.dto.CustomerDTO;
import com.redis.redis_optimized.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDto(Customer entity);
    Customer toEntity(CustomerDTO dto);
    List<CustomerDTO> toDtoList(List<Customer> customers);
    void updateEntity(CustomerDTO dto, @MappingTarget Customer entity);
}
