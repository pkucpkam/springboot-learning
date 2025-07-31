package example.event.service;

import example.event.entity.Product;
import example.event.event.ProductCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Product createProduct(Long id, String name, double price) {
        Product product = new Product(id, name, price);

        eventPublisher.publishEvent(new ProductCreatedEvent(this, product));

        return product;
    }
}
