package com.example.asynchronous_processing.repository;

import com.example.asynchronous_processing.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
