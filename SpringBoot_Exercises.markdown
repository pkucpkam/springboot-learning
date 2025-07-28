# Danh sách bài tập thực hành Spring Boot: Từ Beginner đến Master

## Giới thiệu
Danh sách này bao gồm các bài tập thực hành được thiết kế để giúp bạn nắm vững Spring Boot từ cơ bản đến nâng cao. Mỗi bài tập tập trung vào một khía cạnh cụ thể của framework, với hướng dẫn chi tiết, mã nguồn mẫu, và các bước thực hiện. Bạn sẽ cần:
- **Công cụ**: JDK 17 hoặc 21, Maven, IntelliJ IDEA (hoặc Eclipse), Postman, H2/MySQL/PostgreSQL, Docker (tuỳ chọn).
- **Kiến thức cơ bản**: Java, OOP, REST API, và SQL cơ bản.
- **Cách thực hiện**: Mỗi bài tập nên được thực hiện trong một module riêng trong dự án Maven/Gradle hoặc một dự án độc lập.

---

## 1. Kiến thức cơ bản về Spring Boot

### Bài tập 1: Tạo ứng dụng Spring Boot đầu tiên
- **Mục tiêu**: Làm quen với Spring Boot và tạo một endpoint cơ bản.
- **Hướng dẫn**:
  1. Truy cập [Spring Initializr](https://start.spring.io), chọn:
     - Java 17, Maven, Spring Boot 3.x, dependency `spring-boot-starter-web`.
     - Tạo và tải dự án về.
  2. Mở dự án trong IDE, tạo một controller với endpoint `/hello`.
  3. Chạy ứng dụng và kiểm tra endpoint bằng Postman hoặc trình duyệt.
- **Mã nguồn mẫu**:
  ```java
  package com.example.demo;

  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RestController;

  @RestController
  public class HelloController {
      @GetMapping("/hello")
      public String sayHello() {
          return "Hello, Spring Boot!";
      }
  }
  ```
- **Kết quả mong đợi**: Gọi `GET http://localhost:8080/hello` trả về "Hello, Spring Boot!".
- **Thời gian ước tính**: 1 giờ.

### Bài tập 2: Quản lý cấu hình với profiles
- **Mục tiêu**: Hiểu cách sử dụng `application.yml` và profiles.
- **Hướng dẫn**:
  1. Tạo file `application.yml` với hai profile: `dev` và `prod`.
  2. Thêm thuộc tính tùy chỉnh `app.name`.
  3. Truy cập thuộc tính bằng `@Value` trong một controller.
  4. Chạy ứng dụng với profile `dev` (`--spring.profiles.active=dev`) và `prod`.
- **Mã nguồn mẫu**:
  ```yaml
  # application.yml
  app:
    name: MyApp
  ---
  spring:
    profiles: dev
  app:
    name: MyApp-Dev
  ---
  spring:
    profiles: prod
  app:
    name: MyApp-Prod
  ```
  ```java
  @RestController
  public class ConfigController {
      @Value("${app.name}")
      private String appName;

      @GetMapping("/config")
      public String getConfig() {
          return "App Name: " + appName;
      }
  }
  ```
- **Kết quả mong đợi**: Gọi `GET http://localhost:8080/config` trả về tên ứng dụng tương ứng với profile.
- **Thời gian ước tính**: 1.5 giờ.

---

## 2. Xây dựng RESTful API

### Bài tập 3: Xây dựng API quản lý sản phẩm
- **Mục tiêu**: Tạo REST API CRUD cho đối tượng Product.
- **Hướng dẫn**:
  1. Tạo một class `Product` với các thuộc tính: `id`, `name`, `price`.
  2. Tạo một service lưu trữ danh sách sản phẩm trong `List<Product>` (chưa dùng database).
  3. Tạo controller với các endpoint:
     - `GET /products`: Lấy tất cả sản phẩm.
     - `GET /products/{id}`: Lấy sản phẩm theo ID.
     - `POST /products`: Thêm sản phẩm mới.
     - `PUT /products/{id}`: Cập nhật sản phẩm.
     - `DELETE /products/{id}`: Xóa sản phẩm.
  4. Test API bằng Postman.
- **Mã nguồn mẫu**:
  ```java
  // Product.java
  public class Product {
      private Long id;
      private String name;
      private double price;
      // Getters and setters
  }

  // ProductService.java
  @Service
  public class ProductService {
      private List<Product> products = new ArrayList<>();
      private Long nextId = 1L;

      public List<Product> findAll() { return products; }
      public Product findById(Long id) { return products.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null); }
      public Product create(Product product) { product.setId(nextId++); products.add(product); return product; }
      public Product update(Long id, Product product) { /* Implement update logic */ }
      public void delete(Long id) { products.removeIf(p -> p.getId().equals(id)); }
  }

  // ProductController.java
  @RestController
  @RequestMapping("/products")
  public class ProductController {
      @Autowired
      private ProductService productService;

      @GetMapping
      public List<Product> getAllProducts() { return productService.findAll(); }
      @GetMapping("/{id}")
      public Product getProductById(@PathVariable Long id) { return productService.findById(id); }
      @PostMapping
      public Product createProduct(@RequestBody Product product) { return productService.create(product); }
      @PutMapping("/{id}")
      public Product updateProduct(@PathVariable Long id, @RequestBody Product product) { return productService.update(id, product); }
      @DeleteMapping("/{id}")
      public void deleteProduct(@PathVariable Long id) { productService.delete(id); }
  }
  ```
- **Kết quả mong đợi**: API hoạt động với các thao tác CRUD, test được bằng Postman.
- **Thời gian ước tính**: 3 giờ.

### Bài tập 4: Xử lý lỗi trong API
- **Mục tiêu**: Thêm xử lý lỗi cho API sản phẩm.
- **Hướng dẫn**:
  1. Tạo một ngoại lệ tùy chỉnh `ProductNotFoundException`.
  2. Sử dụng `@ControllerAdvice` để xử lý ngoại lệ và trả về mã trạng thái HTTP phù hợp.
  3. Kiểm tra lỗi khi sản phẩm không tồn tại (`GET /products/{id}`).
- **Mã nguồn mẫu**:
  ```java
  // ProductNotFoundException.java
  public class ProductNotFoundException extends RuntimeException {
      public ProductNotFoundException(String message) { super(message); }
  }

  // GlobalExceptionHandler.java
  @ControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(ProductNotFoundException.class)
      public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
          return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
      }
  }

  // ProductService.java (update findById)
  public Product findById(Long id) {
      return products.stream()
          .filter(p -> p.getId().equals(id))
          .findFirst()
          .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
  }
  ```
- **Kết quả mong đợi**: Gọi `GET /products/999` trả về HTTP 404 với thông báo "Product not found with id: 999".
- **Thời gian ước tính**: 2 giờ.

### Bài tập 5: Tích hợp HATEOAS
- **Mục tiêu**: Thêm liên kết HATEOAS vào API sản phẩm.
- **Hướng dẫn**:
  1. Thêm dependency `spring-boot-starter-hateoas`.
  2. Sửa endpoint `GET /products/{id}` để trả về response với các liên kết (self, all products).
  3. Test API bằng Postman.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-hateoas</artifactId>
  </dependency>
  ```
  ```java
  // ProductController.java
  @GetMapping("/{id}")
  public EntityModel<Product> getProductById(@PathVariable Long id) {
      Product product = productService.findById(id);
      EntityModel<Product> resource = EntityModel.of(product);
      resource.add(linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel());
      resource.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("all-products"));
      return resource;
  }
  ```
- **Kết quả mong đợi**: Response của `GET /products/1` chứa các liên kết như `"self": "/products/1"`, `"all-products": "/products"`.
- **Thời gian ước tính**: 2 giờ.

---

## 3. Quản lý dữ liệu với Spring Data

### Bài tập 6: Kết nối cơ sở dữ liệu với Spring Data JPA
- **Mục tiêu**: Lưu trữ sản phẩm vào cơ sở dữ liệu H2.
- **Hướng dẫn**:
  1. Thêm dependency `spring-boot-starter-data-jpa` và `h2`.
  2. Tạo entity `Product` với các trường `id`, `name`, `price`.
  3. Tạo repository `ProductRepository` với các phương thức CRUD.
  4. Sửa `ProductService` để sử dụng repository thay vì `List`.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
  </dependency>
  ```
  ```java
  // Product.java
  @Entity
  public class Product {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String name;
      private double price;
      // Getters and setters
  }

  // ProductRepository.java
  public interface ProductRepository extends JpaRepository<Product, Long> {
  }

  // ProductService.java
  @Service
  public class ProductService {
      @Autowired
      private ProductRepository productRepository;

      public List<Product> findAll() { return productRepository.findAll(); }
      public Product findById(Long id) { return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found")); }
      public Product create(Product product) { return productRepository.save(product); }
      public Product update(Long id, Product product) { product.setId(id); return productRepository.save(product); }
      public void delete(Long id) { productRepository.deleteById(id); }
  }
  ```
- **Kết quả mong đợi**: API hoạt động tương tự Bài tập 3, nhưng dữ liệu được lưu trong H2.
- **Thời gian ước tính**: 3 giờ.

### Bài tập 7: Truy vấn tùy chỉnh
- **Mục tiêu**: Viết các truy vấn tùy chỉnh cho Product.
- **Hướng dẫn**:
  1. Thêm các phương thức vào `ProductRepository`:
     - Tìm sản phẩm theo tên (chứa chuỗi, không phân biệt hoa thường).
     - Tìm sản phẩm có giá trong khoảng min-max.
  2. Viết một truy vấn JPQL để tìm sản phẩm theo tên chính xác.
  3. Test các truy vấn bằng Postman.
- **Mã nguồn mẫu**:
  ```java
  // ProductRepository.java
  public interface ProductRepository extends JpaRepository<Product, Long> {
      List<Product> findByNameContainingIgnoreCase(String name);
      List<Product> findByPriceBetween(double min, double max);
      @Query("SELECT p FROM Product p WHERE p.name = :name")
      List<Product> findByExactName(@Param("name") String name);
  }

  // ProductController.java
  @GetMapping("/search")
  public List<Product> searchProducts(@RequestParam String name) {
      return productRepository.findByNameContainingIgnoreCase(name);
  }
  ```
- **Kết quả mong đợi**: Gọi `GET /products/search?name=phone` trả về danh sách sản phẩm chứa "phone" trong tên.
- **Thời gian ước tính**: 2 giờ.

### Bài tập 8: Migration cơ sở dữ liệu với Flyway
- **Mục tiêu**: Sử dụng Flyway để quản lý schema và dữ liệu ban đầu.
- **Hướng dẫn**:
  1. Thêm dependency `flyway-core`.
  2. Tạo file `db/migration/V1__create_product_table.sql` để tạo bảng `products`.
  3. Tạo file `db/migration/V2__add_initial_data.sql` để thêm dữ liệu mẫu.
  4. Chạy ứng dụng và kiểm tra dữ liệu trong H2 Console (`http://localhost:8080/h2-console`).
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-core</artifactId>
  </dependency>
  ```
  ```sql
  -- db/migration/V1__create_product_table.sql
  CREATE TABLE product (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      price DOUBLE NOT NULL
  );

  -- db/migration/V2__add_initial_data.sql
  INSERT INTO product (name, price) VALUES ('Laptop', 999.99);
  INSERT INTO product (name, price) VALUES ('Phone', 499.99);
  ```
- **Kết quả mong đợi**: Bảng `product` được tạo và chứa dữ liệu mẫu khi ứng dụng khởi động.
- **Thời gian ước tính**: 2 giờ.

---

## 4. Bảo mật với Spring Security

### Bài tập 9: Bảo mật cơ bản
- **Mục tiêu**: Thêm xác thực và phân quyền cơ bản.
- **Hướng dẫn**:
  1. Thêm dependency `spring-boot-starter-security`.
  2. Cấu hình in-memory authentication với 2 user: `user` (vai trò USER) và `admin` (vai trò ADMIN).
  3. Bảo vệ endpoint `/products`:
     - `GET` cho phép cả USER và ADMIN.
     - `POST`, `PUT`, `DELETE` chỉ cho ADMIN.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  ```
  ```java
  // SecurityConfig.java
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig {
      @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http
              .authorizeHttpRequests(auth -> auth
                  .requestMatchers(HttpMethod.GET, "/products/**").hasAnyRole("USER", "ADMIN")
                  .requestMatchers("/products/**").hasRole("ADMIN")
                  .anyRequest().authenticated()
              )
              .httpBasic();
          return http.build();
      }

      @Bean
      public UserDetailsService userDetailsService() {
          UserDetails user = User.withUsername("user").password("{noop}password").roles("USER").build();
          UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
          return new InMemoryUserDetailsManager(user, admin);
      }
  }
  ```
- **Kết quả mong đợi**: Gọi `POST /products` với user `user` trả về HTTP 403; với `admin` thành công.
- **Thời gian ước tính**: 3 giờ.

### Bài tập 10: JWT Authentication
- **Mục tiêu**: Bảo vệ API với JWT.
- **Hướng dẫn**:
  1. Thêm dependency `jjwt` (hoặc dùng thư viện tương tự).
  2. Tạo endpoint `/login` để sinh token JWT.
  3. Tạo filter để kiểm tra token trong header `Authorization`.
  4. Bảo vệ các endpoint `/products` bằng JWT.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt</artifactId>
      <version>0.9.1</version>
  </dependency>
  ```
  ```java
  // JwtUtil.java
  public class JwtUtil {
      private String secret = "your-secret-key";
      public String generateToken(String username) {
          return Jwts.builder()
              .setSubject(username)
              .setIssuedAt(new Date())
              .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
              .signWith(SignatureAlgorithm.HS256, secret)
              .compact();
      }
      public String extractUsername(String token) {
          return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
      }
  }

  // AuthController.java
  @RestController
  public class AuthController {
      @PostMapping("/login")
      public String login(@RequestBody Map<String, String> credentials) {
          String username = credentials.get("username");
          String password = credentials.get("password");
          if ("admin".equals(username) && "admin".equals(password)) {
              return jwtUtil.generateToken(username);
          }
          throw new RuntimeException("Invalid credentials");
      }
  }
  ```
- **Kết quả mong đợi**: Gọi `/login` để nhận token, sau đó dùng token trong header `Authorization: Bearer <token>` để truy cập `/products`.
- **Thời gian ước tính**: 4 giờ.

---

## 5. Xử lý bất đồng bộ và sự kiện

### Bài tập 11: Xử lý bất đồng bộ
- **Mục tiêu**: Gửi email giả lập bất đồng bộ khi tạo sản phẩm.
- **Hướng dẫn**:
  1. Thêm annotation `@EnableAsync` vào class chính.
  2. Tạo service gửi email giả lập (in ra console).
  3. Gọi service này bất đồng bộ khi tạo sản phẩm.
- **Mã nguồn mẫu**:
  ```java
  // Application.java
  @SpringBootApplication
  @EnableAsync
  public class Application {
      public static void main(String[] args) {
          SpringApplication.run(Application.class, args);
      }
  }

  // EmailService.java
  @Service
  public class EmailService {
      @Async
      public void sendEmail(String to, String message) {
          System.out.println("Sending email to " + to + ": " + message);
          // Simulate delay
          try { Thread.sleep(2000); } catch (Exception e) {}
      }
  }

  // ProductService.java
  public Product create(Product product) {
      Product savedProduct = productRepository.save(product);
      emailService.sendEmail("user@example.com", "New product created: " + product.getName());
      return savedProduct;
  }
  ```
- **Kết quả mong đợi**: Khi tạo sản phẩm, email được gửi bất đồng bộ mà không làm chậm request.
- **Thời gian ước tính**: 2 giờ.

### Bài tập 12: Spring Events
- **Mục tiêu**: Xử lý sự kiện khi sản phẩm được tạo.
- **Hướng dẫn**:
  1. Tạo class `ProductCreatedEvent`.
  2. Publish event khi sản phẩm được tạo.
  3. Tạo listener để xử lý event (in ra console).
- **Mã nguồn mẫu**:
  ```java
  // ProductCreatedEvent.java
  public class ProductCreatedEvent extends ApplicationEvent {
      private final Product product;
      public ProductCreatedEvent(Object source, Product product) {
          super(source);
          this.product = product;
      }
      public Product getProduct() { return product; }
  }

  // ProductService.java
  @Autowired
  private ApplicationEventPublisher eventPublisher;

  public Product create(Product product) {
      Product savedProduct = productRepository.save(product);
      eventPublisher.publishEvent(new ProductCreatedEvent(this, savedProduct));
      return savedProduct;
  }

  // ProductEventListener.java
  @Component
  public class ProductEventListener {
      @EventListener
      public void handleProductCreated(ProductCreatedEvent event) {
          System.out.println("Product created: " + event.getProduct().getName());
      }
  }
  ```
- **Kết quả mong đợi**: Khi tạo sản phẩm, listener in ra thông tin sản phẩm.
- **Thời gian ước tính**: 2 giờ.

---

## 6. Microservices với Spring Boot

### Bài tập 13: Tạo microservices cơ bản
- **Mục tiêu**: Xây dựng hai dịch vụ Product và Order.
- **Hướng dẫn**:
  1. Tạo hai dự án Spring Boot riêng:
     - `product-service`: Tương tự Bài tập 6.
     - `order-service`: Quản lý đơn hàng, gọi API của `product-service` để lấy thông tin sản phẩm.
  2. Sử dụng `RestTemplate` để gọi API giữa các dịch vụ.
- **Mã nguồn mẫu** (Order Service):
  ```java
  // Order.java
  public class Order {
      private Long id;
      private Long productId;
      private int quantity;
      // Getters and setters
  }

  // OrderService.java
  @Service
  public class OrderService {
      @Autowired
      private RestTemplate restTemplate;

      public Order createOrder(Order order) {
          Product product = restTemplate.getForObject("http://localhost:8081/products/" + order.getProductId(), Product.class);
          // Save order logic
          return order;
      }
  }
  ```
- **Kết quả mong đợi**: Order Service gọi được API của Product Service để lấy thông tin sản phẩm.
- **Thời gian ước tính**: 4 giờ.

### Bài tập 14: Service Discovery với Eureka
- **Mục tiêu**: Tích hợp Eureka để quản lý các dịch vụ.
- **Hướng dẫn**:
  1. Tạo dự án `eureka-server` với dependency `spring-cloud-starter-netflix-eureka-server`.
  2. Đăng ký `product-service` và `order-service` với Eureka.
  3. Sử dụng `@LoadBalanced` RestTemplate để gọi API.
- **Mã nguồn mẫu**:
  ```xml
  <!-- eureka-server pom.xml -->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
  </dependency>
  ```
  ```java
  // EurekaServerApplication.java
  @SpringBootApplication
  @EnableEurekaServer
  public class EurekaServerApplication {
      public static void main(String[] args) {
          SpringApplication.run(EurekaServerApplication.class, args);
      }
  }
  ```
  ```yaml
  # product-service application.yml
  eureka:
    client:
      serviceUrl:
        defaultZone: http://localhost:8761/eureka/
  ```
- **Kết quả mong đợi**: Các dịch vụ đăng ký với Eureka và giao tiếp qua tên dịch vụ.
- **Thời gian ước tính**: 4 giờ.

---

## 7. Testing trong Spring Boot

### Bài tập 15: Unit Testing
- **Mục tiêu**: Viết unit test cho `ProductService`.
- **Hướng dẫn**:
  1. Thêm dependency `spring-boot-starter-test`.
  2. Viết test cho phương thức `findById` với Mockito.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
  </dependency>
  ```
  ```java
  // ProductServiceTest.java
  @ExtendWith(MockitoExtension.class)
  public class ProductServiceTest {
      @Mock
      private ProductRepository productRepository;
      @InjectMocks
      private ProductService productService;

      @Test
      public void testFindById() {
          Product product = new Product(1L, "Laptop", 999.99);
          when(productRepository.findById(1L)).thenReturn(Optional.of(product));
          Product result = productService.findById(1L);
          assertEquals("Laptop", result.getName());
      }
  }
  ```
- **Kết quả mong đợi**: Test pass với trường hợp tìm sản phẩm thành công.
- **Thời gian ước tính**: 2 giờ.

### Bài tập 16: Integration Testing
- **Mục tiêu**: Kiểm tra endpoint `/products` với `@SpringBootTest`.
- **Hướng dẫn**:
  1. Viết integration test cho endpoint `GET /products`.
  2. Sử dụng `TestRestTemplate` để gọi API.
- **Mã nguồn mẫu**:
  ```java
  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
  public class ProductControllerIntegrationTest {
      @Autowired
      private TestRestTemplate restTemplate;

      @Test
      public void testGetAllProducts() {
          ResponseEntity<List> response = restTemplate.getForEntity("/products", List.class);
          assertEquals(HttpStatus.OK, response.getStatusCode());
      }
  }
  ```
- **Kết quả mong đợi**: Test pass, endpoint trả về HTTP 200.
- **Thời gian ước tính**: 2 giờ.

---

## 8. Triển khai và DevOps

### Bài tập 17: Đóng gói và triển khai với Docker
- **Mục tiêu**: Đóng gói ứng dụng thành JAR và chạy trong Docker.
- **Hướng dẫn**:
  1. Tạo file `Dockerfile` cho `product-service`.
  2. Build và chạy container.
- **Mã nguồn mẫu**:
  ```dockerfile
  # Dockerfile
  FROM openjdk:17-jdk-slim
  COPY target/product-service-0.0.1-SNAPSHOT.jar app.jar
  ENTRYPOINT ["java", "-jar", "/app.jar"]
  ```
  ```bash
  mvn clean package
  docker build -t product-service .
  docker run -p 8080:8080 product-service
  ```
- **Kết quả mong đợi**: Ứng dụng chạy trong container và API hoạt động bình thường.
- **Thời gian ước tính**: 3 giờ.

### Bài tập 18: Monitoring với Actuator
- **Mục tiêu**: Thêm monitoring với Spring Boot Actuator.
- **Hướng dẫn**:
  1. Thêm dependency `spring-boot-starter-actuator`.
  2. Kích hoạt các endpoint `/actuator/health`, `/actuator/info`.
  3. Test bằng Postman.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
  ```
  ```yaml
  # application.yml
  management:
    endpoints:
      web:
        exposure:
          include: health,info
  ```
- **Kết quả mong đợi**: Gọi `GET http://localhost:8080/actuator/health` trả về `{"status":"UP"}`.
- **Thời gian ước tính**: 1.5 giờ.

---

## 9. Hiệu năng và tối ưu hóa

### Bài tập 19: Caching với Spring Cache
- **Mục tiêu**: Tích hợp caching để cải thiện hiệu năng.
- **Hướng dẫn**:
  1. Thêm dependency `spring-boot-starter-cache`.
  2. Sử dụng `@Cacheable` cho phương thức `findById`.
  3. Test hiệu năng bằng cách gọi API nhiều lần.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-cache</artifactId>
  </dependency>
  ```
  ```java
  // ProductService.java
  @Cacheable("products")
  public Product findById(Long id) {
      return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
  }
  ```
- **Kết quả mong đợi**: Lần gọi thứ hai của `GET /products/1` nhanh hơn do dữ liệu được lấy từ cache.
- **Thời gian ước tính**: 2 giờ.

### Bài tập 20: Tối ưu truy vấn
- **Mục tiêu**: Tối ưu truy vấn với index và fetch.
- **Hướng dẫn**:
  1. Thêm index cho cột `name` trong bảng `product` (sử dụng Flyway).
  2. Sửa truy vấn `findByNameContaining` để sử dụng `@Query` với `JOIN FETCH` nếu có liên kết.
- **Mã nguồn mẫu**:
  ```sql
  -- db/migration/V3__add_index.sql
  CREATE INDEX idx_product_name ON product(name);
  ```
- **Kết quả mong đợi**: Truy vấn tìm kiếm nhanh hơn nhờ index.
- **Thời gian ước tính**: 2 giờ.

---

## 10. Các công nghệ bổ trợ

### Bài tập 21: Spring WebFlux
- **Mục tiêu**: Xây dựng API reactive cho Product.
- **Hướng dẫn**:
  1. Tạo dự án mới với `spring-boot-starter-webflux`.
  2. Sử dụng MongoDB và `ReactiveMongoRepository`.
  3. Chuyển endpoint `GET /products` sang reactive.
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webflux</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
  </dependency>
  ```
  ```java
  // ProductRepository.java
  public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
  }

  // ProductController.java
  @RestController
  @RequestMapping("/products")
  public class ProductController {
      @Autowired
      private ProductRepository productRepository;

      @GetMapping
      public Flux<Product> getAllProducts() {
          return productRepository.findAll();
      }
  }
  ```
- **Kết quả mong đợi**: API trả về danh sách sản phẩm theo kiểu reactive.
- **Thời gian ước tính**: 4 giờ.

### Bài tập 22: GraphQL
- **Mục tiêu**: Tích hợp GraphQL để truy vấn sản phẩm.
- **Hướng dẫn**:
  1. Thêm dependency `spring-boot-starter-graphql`.
  2. Tạo schema GraphQL để truy vấn và thêm sản phẩm.
  3. Test bằng GraphiQL (`http://localhost:8080/graphiql`).
- **Mã nguồn mẫu**:
  ```xml
  <!-- pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-graphql</artifactId>
  </dependency>
  ```
  ```graphql
  # src/main/resources/graphql/schema.graphqls
  type Query {
      products: [Product]
      product(id: ID!): Product
  }
  type Mutation {
      createProduct(name: String!, price: Float!): Product
  }
  type Product {
      id: ID!
      name: String!
      price: Float!
  }
  ```
  ```java
  // ProductResolver.java
  @Controller
  public class ProductResolver {
      @Autowired
      private ProductService productService;

      @QueryMapping
      public List<Product> products() {
          return productService.findAll();
      }

      @MutationMapping
      public Product createProduct(@Argument String name, @Argument double price) {
          Product product = new Product();
          product.setName(name);
          product.setPrice(price);
          return productService.create(product);
      }
  }
  ```
- **Kết quả mong đợi**: Gọi query `{ products { id name price } }` trả về danh sách sản phẩm.
- **Thời gian ước tính**: 3 giờ.

---

## Lộ trình thực hành
- **Tháng 1-3 (Beginner)**: Bài tập 1-6 (cơ bản, REST API, Spring Data JPA).
- **Tháng 4-6 (Intermediate)**: Bài tập 7-12 (truy vấn, security, asynchronous).
- **Tháng 7-12 (Advanced)**: Bài tập 13-18 (microservices, testing, deployment).
- **Tháng 12+ (Master)**: Bài tập 19-22 (performance, WebFlux, GraphQL).

## Lời khuyên
- **Thực hành đều đặn**: Dành 2-4 giờ mỗi bài tập, làm từ dễ đến khó.
- **Debug và log**: Sử dụng logging (SLF4J) để theo dõi lỗi.
- **Tài liệu tham khảo**:
  - [Spring Boot Documentation](https://spring.io/projects/spring-boot)
  - [Baeldung](https://www.baeldung.com/spring-boot)
- **Repo GitHub**: Tạo một repository để lưu trữ các bài tập, thêm README cho mỗi module.

Nếu bạn cần code mẫu chi tiết hơn hoặc hướng dẫn cụ thể cho một bài tập, hãy cho mình biết nhé!