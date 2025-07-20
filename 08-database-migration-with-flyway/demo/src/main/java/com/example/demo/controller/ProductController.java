package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public List<Product> searchByName(@RequestParam String name) {
        return productService.findProductsByNameContaining(name);
    }

    @GetMapping("/price-range")
    public List<Product> searchByPriceRange(@RequestParam Double minPrice,
                                            @RequestParam Double maxPrice) {
        return productService.findProductsByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/exact-name")
    public List<Product> searchByExactName(@RequestParam String name) {
        return productService.findProductsByExactName(name);
    }

}
