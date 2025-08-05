package com.example.demo.service.impl;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.entity.Customer;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public void createCustomer(Customer customer) {
        Optional<Customer> existingCustomer = Optional.ofNullable(customerRepository.selectByEmail(customer.getEmail()));
        if (existingCustomer.isPresent()) {
            throw new IllegalArgumentException("Customer with email " + customer.getEmail() + " already exists");
        }
        customerRepository.insert(customer);
    }


    @Override
    public CustomerDTO getCustomer(Long id) {
        Optional<Customer> customer = Optional.ofNullable(customerRepository.selectById(id));
        return customer.map(customerMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));
    }

    @Override
    public CustomerDTO getCustomerByEmail(String email) {
        Optional<Customer> customer = Optional.ofNullable(customerRepository.selectByEmail(email));
        return customer.map(customerMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Customer with email " + email + " not found"));
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.selectAll()
                .stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCustomer(Long id, CustomerDTO customerDTO) {
        Optional.ofNullable(customerRepository.selectById(id))
                .ifPresentOrElse(
                        existing -> {
                            customerMapper.updateEntity(customerDTO, existing);
                            customerRepository.update(existing);
                        },
                        () -> {
                            throw new IllegalArgumentException("Customer with ID " + id + " not found");
                        }
                );
    }

    public void deleteCustomer(Long id) {
        Optional.ofNullable(id).ifPresentOrElse(
                validId -> Optional.ofNullable(customerRepository.selectById(validId)).ifPresentOrElse(
                        customer -> {
                            customerRepository.delete(customer);
                        },
                        () -> {
                            throw new IllegalArgumentException("Customer with ID " + id + " not found");
                        }
                ),
                () -> {
                    throw new IllegalArgumentException("Customer with ID " + id + " not found");
                }
        );
    }
}
