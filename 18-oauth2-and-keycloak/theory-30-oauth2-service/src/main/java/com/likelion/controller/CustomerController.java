package com.likelion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.dto.CustomerDto;
import com.likelion.service.CustomerService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/search")
    public CustomerDto getCustomerByEmail(@RequestParam(required = false) String email) {
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/address")
    public List<CustomerDto> getCustomersByAdress(@RequestParam String address) {
        return customerService.getCustomersByAdress(address);
    } 

    @PostMapping
    public void createCustomer(@RequestBody CustomerDto customerDto) {
        customerService.createCustomer(customerDto);
    }

    @PutMapping("/{id}")
    public void updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        customerService.updateCustomer(id, customerDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
