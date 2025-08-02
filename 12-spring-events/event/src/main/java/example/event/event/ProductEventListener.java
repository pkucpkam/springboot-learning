package example.event.event;

import example.event.entity.Product;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

    @EventListener
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        Product product = event.getProduct();
        System.out.println("Sản phẩm vừa được tạo: " + product);
    }
}