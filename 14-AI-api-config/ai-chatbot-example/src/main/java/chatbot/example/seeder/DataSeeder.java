package chatbot.example.seeder;
import chatbot.example.model.Product;
import chatbot.example.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Product(null, "iPhone 15", "Smartphone", 1200.0, "Điện thoại Apple iPhone 15 mới nhất"));
                repository.save(new Product(null, "Samsung Galaxy S23", "Smartphone", 999.0, "Điện thoại Samsung cao cấp"));
                repository.save(new Product(null, "MacBook Pro M2", "Laptop", 2200.0, "Laptop hiệu năng cao cho dân lập trình"));
                repository.save(new Product(null, "Sony WH-1000XM5", "Headphone", 400.0, "Tai nghe chống ồn tốt nhất"));
                repository.save(new Product(null, "Logitech MX Master 3S", "Mouse", 120.0, "Chuột không dây cao cấp"));
                repository.save(new Product(null, "Asus ROG Strix G16", "Laptop", 1800.0, "Laptop gaming mạnh mẽ cho game thủ"));
                repository.save(new Product(null, "Dell XPS 13", "Laptop", 1500.0, "Laptop mỏng nhẹ cho dân văn phòng"));
                repository.save(new Product(null, "AirPods Pro 2", "Headphone", 250.0, "Tai nghe không dây chống ồn của Apple"));
                repository.save(new Product(null, "Samsung Galaxy Buds 2 Pro", "Headphone", 200.0, "Tai nghe không dây cao cấp của Samsung"));
                repository.save(new Product(null, "Razer DeathAdder V3 Pro", "Mouse", 150.0, "Chuột gaming siêu nhẹ và nhạy"));
            }
        };
    }
}
