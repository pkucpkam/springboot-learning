package chatbot.example.service;
import chatbot.example.model.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {
    private final WebClient webClient;
    private final String model;
    private final ProductService productService;

    public ChatbotService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model}") String model,
            ProductService productService
    ) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = model;
        this.productService = productService;
    }

    public String chat(String userMessage) {
        List<Product> products = productService.searchProducts(userMessage);

        StringBuilder context = new StringBuilder("Danh sách sản phẩm liên quan:\n");
        for (Product p : products) {
            context.append("- ").append(p.getName())
                    .append(" (").append(p.getPrice()).append("$) : ")
                    .append(p.getDescription()).append("\n");
        }

        // Tạo body bằng Map
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", "Bạn là chatbot tư vấn sản phẩm thương mại điện tử."),
                        Map.of("role", "user", "content", userMessage + ". Dữ liệu sản phẩm: " + context)
                )
        );

        String rawResponse = webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawResponse);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Lỗi khi parse response: " + e.getMessage() + "\nRaw: " + rawResponse;
        }
    }


}


