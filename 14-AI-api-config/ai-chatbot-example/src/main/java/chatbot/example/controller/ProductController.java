package chatbot.example.controller;

import chatbot.example.model.Product;
import chatbot.example.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Lấy tất cả sản phẩm
    @GetMapping
    public List<Product> list() {
        return productRepository.findAll();
    }

    // Thêm sản phẩm mới
    @PostMapping
    public Product save(@RequestBody Product product) {
        return productRepository.save(product);
    }
}

