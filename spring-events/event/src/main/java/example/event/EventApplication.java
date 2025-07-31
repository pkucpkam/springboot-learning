package example.event;

import example.event.service.ProductService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class EventApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EventApplication.class, args);

        ProductService productService = context.getBean(ProductService.class);

        productService.createProduct(1L, "Laptop", 999.99);
        productService.createProduct(2L, "Smartphone", 499.99);
    }

}
