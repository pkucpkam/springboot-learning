package chatbot.example.service;
import chatbot.example.model.Product;
import chatbot.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> searchProducts(String keyword) {
        return repository.findByNameContainingIgnoreCase(keyword);
    }
}
