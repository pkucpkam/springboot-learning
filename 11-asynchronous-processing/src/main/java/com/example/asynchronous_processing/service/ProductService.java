package com.example.asynchronous_processing.service;

import com.example.asynchronous_processing.entity.Product;
import com.example.asynchronous_processing.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);

        emailService.sendEmail(product.getName());

        return savedProduct;
    }
}