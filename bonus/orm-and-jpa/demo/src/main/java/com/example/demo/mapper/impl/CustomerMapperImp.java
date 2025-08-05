package com.example.demo.mapper.impl;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.entity.Customer;
import com.example.demo.mapper.CustomerMapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapperImp implements CustomerMapper {

    @Override
    public CustomerDTO toDTO(Customer entity) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public Customer toEntity(CustomerDTO dto) {
        Customer entity = new Customer();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(CustomerDTO dto , Customer entity) {

    }
}
