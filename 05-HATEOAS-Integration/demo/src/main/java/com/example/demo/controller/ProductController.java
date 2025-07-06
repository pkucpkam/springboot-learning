package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductResource;
import com.example.demo.repository.ProductRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Lấy danh sách sản phẩm
    @GetMapping
    public ResponseEntity<CollectionModel<ProductResource>> getAllProducts() {
        List<ProductResource> productResources = productRepository.findAll().stream()
                .map(product -> {
                    ProductResource resource = new ProductResource(product);
                    // Thêm liên kết "self" cho từng sản phẩm
                    Link selfLink = WebMvcLinkBuilder
                            .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(product.getId()))
                            .withSelfRel();
                    resource.add(selfLink);
                    return resource;
                })
                .collect(Collectors.toList());

        // Thêm liên kết "self" cho danh sách sản phẩm
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllProducts())
                .withSelfRel();

        // Thêm liên kết "create" cho hành động tạo sản phẩm
        Link createLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).createProduct(null))
                .withRel("create");

        CollectionModel<ProductResource> collectionModel = CollectionModel.of(productResources, selfLink, createLink);
        return ResponseEntity.ok(collectionModel);
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResource> getProductById(@PathVariable int id) {
        return productRepository.findById(id)
                .map(product -> {
                    ProductResource resource = new ProductResource(product);

                    // Thêm liên kết "self"
                    Link selfLink = WebMvcLinkBuilder
                            .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(id))
                            .withSelfRel();

                    // Thêm liên kết tới danh sách sản phẩm
                    Link allProductsLink = WebMvcLinkBuilder
                            .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllProducts())
                            .withRel("all-products");

                    // Thêm liên kết cập nhật
                    Link updateLink = WebMvcLinkBuilder
                            .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).updateProduct(id, null))
                            .withRel("update");

                    // Thêm liên kết xóa
                    Link deleteLink = WebMvcLinkBuilder
                            .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).deleteProduct(id))
                            .withRel("delete");

                    resource.add(selfLink, allProductsLink, updateLink, deleteLink);
                    return ResponseEntity.ok(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo sản phẩm mới
    @PostMapping
    public ResponseEntity<ProductResource> createProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        ProductResource resource = new ProductResource(savedProduct);

        // Thêm liên kết "self"
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(savedProduct.getId()))
                .withSelfRel();

        // Thêm liên kết tới danh sách sản phẩm
        Link allProductsLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllProducts())
                .withRel("all-products");

        resource.add(selfLink, allProductsLink);
        return ResponseEntity.created(selfLink.toUri()).body(resource);
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<ProductResource> updateProduct(@PathVariable int id, @RequestBody Product updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    Product savedProduct = productRepository.save(product);

                    ProductResource resource = new ProductResource(savedProduct);

                    // Thêm liên kết "self"
                    Link selfLink = WebMvcLinkBuilder
                            .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(id))
                            .withSelfRel();

                    // Thêm liên kết tới danh sách sản phẩm
                    Link allProductsLink = WebMvcLinkBuilder
                            .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllProducts())
                            .withRel("all-products");

                    resource.add(selfLink, allProductsLink);
                    return ResponseEntity.ok(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}