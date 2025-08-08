package com.likelion.controller;

import com.likelion.dto.CustomerDTO;
import com.likelion.entity.Customer;
import com.likelion.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerDTO> getAll() {
        return customerService.findAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getById(@PathVariable String id) {
        return customerService.findCustomerById(id);
    }

    @PostMapping
    public CustomerDTO create(@RequestBody CustomerDTO dto) {
        return customerService.createCustomer(dto);
    }

    @PutMapping("/{id}")
    public CustomerDTO update(@PathVariable String id, @RequestBody CustomerDTO dto) {
        return customerService.updateCustomer(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        customerService.deleteCustomer(id);
    }
}