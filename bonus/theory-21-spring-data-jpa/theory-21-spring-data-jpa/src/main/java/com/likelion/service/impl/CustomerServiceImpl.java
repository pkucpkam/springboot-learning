package com.likelion.service.impl;

import com.likelion.dto.CustomerDTO;
import com.likelion.entity.Customer;
import com.likelion.mapper.CustomerMapper;
import com.likelion.repository.CustomerRepository;
import com.likelion.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    private CustomerMapper customerMapper;

    @Override
    public void createCustomer(CustomerDTO customerDto) {
        Optional.ofNullable(customerRepository.findByEmail(customerDto.getEmail()))
                .ifPresentOrElse(
                        existing -> {
                            throw new IllegalArgumentException(
                                    "Customer already exists with email: " + customerDto.getEmail());
                        },
                        () -> {
                            Customer newCustomer = customerMapper.toCustomer(customerDto);
                            customerRepository.save(newCustomer);
                        });
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toCustomerDTO)
                .orElse(null);
    }

    @Override
    public CustomerDTO getCustomerByEmail(String email) {
        return Optional.ofNullable(customerRepository.findByEmail(email))
                .map(customerMapper::toCustomerDTO)
                .orElse(null);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCustomer(Long id, CustomerDTO customerDto) {
        customerRepository.findById(id)
                .ifPresentOrElse(
                        existing -> {
                            customerMapper.updateCustomer(customerDto, existing);
                            customerRepository.save(existing);
                        },
                        () -> {
                            throw new NoSuchElementException("Customer not found with id: " + id);
                        });
    }

    @Override
    public void deleteCustomer(Long id) {
        Optional.ofNullable(customerRepository.findById(id))
                .ifPresentOrElse(
                        existing -> {
                            customerRepository.deleteById(id);
                        },
                        () -> {
                            throw new NoSuchElementException("Customer not found with id: " + id);
                        });
    }

}
