package com.likelion.service.impl;

import com.likelion.dto.CustomerDTO;
import com.likelion.exception.ResourceNotFoundException;
import com.likelion.mapper.CustomerMapper;
import com.likelion.repository.CustomerRepository;
import com.likelion.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        return customerMapper.toCustomerDTO(customerRepository.save(customerMapper.toCustomer(dto)));
    }

    @Override
    public CustomerDTO updateCustomer(String id, CustomerDTO dto) {
        return customerRepository.findById(id)
                .map(existing -> {
                    customerMapper.updateCustomer(dto, existing);
                    return customerMapper.toCustomerDTO(customerRepository.save(existing));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Not found: " + id));
    }

    @Override
    public CustomerDTO findCustomerById(String id) {
        return customerRepository.findById(id)
                .map(customerMapper::toCustomerDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Not found: " + id));
    }

    @Override
    public List<CustomerDTO> findAllCustomers() {
        return customerMapper.toCustomerDTOs(customerRepository.findAll());
    }

    @Override
    public void deleteCustomer(String id) {
        customerRepository.findById(id)
                .ifPresentOrElse(
                        customerRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Not found: " + id);
                        });
    }
}
