package com.redis.redis_optimized.service.impl;

import com.redis.redis_optimized.contant.CacheConstant;
import com.redis.redis_optimized.dto.CustomerDTO;
import com.redis.redis_optimized.entity.Customer;
import com.redis.redis_optimized.mapper.CustomerMapper;
import com.redis.redis_optimized.repository.CustomerRepository;
import com.redis.redis_optimized.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(cacheNames = CacheConstant.CUSTOMER_BY_ID, key = "#result.id", unless = "#result == null"),
            @CachePut(cacheNames = CacheConstant.CUSTOMER_BY_EMAIL, key = "#result.email", unless = "#result == null")
    }, evict = {
            @CacheEvict(cacheNames = CacheConstant.ALL_CUSTOMERS, key = "'all'"),
            @CacheEvict(cacheNames = CacheConstant.CUSTOMERS_BY_ADDRESS, key = "#result.address", condition = "#result != null && #result.address != null")
    })
    public CustomerDTO createCustomer(CustomerDTO customerDto) {
        return Optional.ofNullable(customerRepository.findByEmail(customerDto.email()))
                .<CustomerDTO>map(exist -> {
                    throw new IllegalArgumentException(
                            "Customer already exists with email: " + customerDto.email());
                })
                .orElseGet(() -> customerMapper.toDto(
                        customerRepository.save(customerMapper.toEntity(customerDto))));
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.CUSTOMER_BY_ID, key = "#id", unless = "#result == null")
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDto)
                .orElse(null);
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.CUSTOMER_BY_EMAIL, key = "#email", unless = "#result == null")
    public CustomerDTO getCustomerByEmail(String email) {
        return Optional.ofNullable(customerRepository.findByEmail(email)) // Automation JPQL
                // return Optional.ofNullable(customerRepository.findByEmailWithJPQL(email)) //
                // Manual JPQL
                // return Optional.ofNullable(customerRepository.findByEmailWithSQL(email)) //
                // Manual SQL
                .map(customerMapper::toDto)
                .orElse(null);
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.CUSTOMERS_BY_ADDRESS, key = "#address", unless = "#result == null || #result.isEmpty()")
    public List<CustomerDTO> getCustomersByAdress(String address) {
        return customerRepository.findByAddress(address).stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.ALL_CUSTOMERS, key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(cacheNames = CacheConstant.CUSTOMER_BY_ID, key = "#id", unless = "#result == null"),
            @CachePut(cacheNames = CacheConstant.CUSTOMER_BY_EMAIL, key = "#result.email", unless = "#result == null")
    }, evict = {
            @CacheEvict(cacheNames = CacheConstant.ALL_CUSTOMERS, key = "'all'")
    })
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDto) {
        return customerRepository.findById(id)
                .map(existing -> {
                    customerMapper.updateEntity(customerDto, existing);
                    Customer saved = customerRepository.save(existing);
                    return customerMapper.toDto(saved);
                })
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConstant.CUSTOMER_BY_ID, key = "#id"),
            @CacheEvict(cacheNames = CacheConstant.ALL_CUSTOMERS, key = "'all'")
    })
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
