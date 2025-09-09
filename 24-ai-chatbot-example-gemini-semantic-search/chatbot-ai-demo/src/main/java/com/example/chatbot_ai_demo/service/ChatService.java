package com.example.chatbot_ai_demo.service;

import com.example.chatbot_ai_demo.dto.ChatRequest;
import com.example.chatbot_ai_demo.dto.ChatResponse;
import com.example.chatbot_ai_demo.dto.ProductRequest;
import com.example.chatbot_ai_demo.entity.Product;
import com.example.chatbot_ai_demo.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ProductRepository productRepository;

    public ChatService(ChatClient.Builder builder, ProductRepository productRepository) {
        this.chatClient = builder.build();
        this.productRepository = productRepository;
    }

    // Hàm cosine similarity cho List<Double>
    private double cosineSimilarity(List<Double> a, List<Double> b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Demo generate embedding (thay bằng Gemini / HF thật)
    private List<Double> generateEmbedding(String text) {
        Random rand = new Random();
        List<Double> embedding = new ArrayList<>();
        for (int i = 0; i < 128; i++) embedding.add(rand.nextDouble());
        return embedding;
    }

    public ChatResponse sendMessage(ChatRequest request) {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return new ChatResponse("Hiện tại chưa có sản phẩm nào trong hệ thống.");
        }

        String msg = request.message().toLowerCase();

        // Xử lý nhanh rẻ nhất / đắt nhất
        if (msg.contains("rẻ nhất")) {
            Product cheapest = products.stream()
                    .min(Comparator.comparing(Product::getPrice))
                    .orElseThrow();
            return new ChatResponse("Sản phẩm rẻ nhất là: " + cheapest.getName()
                    + " - giá " + cheapest.getPrice());
        }

        if (msg.contains("đắt nhất")) {
            Product expensive = products.stream()
                    .max(Comparator.comparing(Product::getPrice))
                    .orElseThrow();
            return new ChatResponse("Sản phẩm đắt nhất là: " + expensive.getName()
                    + " - giá " + expensive.getPrice());
        }

        // 1️⃣ Generate embedding cho query
        List<Double> queryVec = generateEmbedding(request.message());

        // 2️⃣ Tính cosine similarity và lấy top-5 sản phẩm
        List<Product> topK = products.stream()
                .sorted((p1, p2) -> {
                    double sim1 = cosineSimilarity(queryVec, p1.getEmbedding());
                    double sim2 = cosineSimilarity(queryVec, p2.getEmbedding());
                    return Double.compare(sim2, sim1); // giảm dần
                })
                .limit(5)
                .collect(Collectors.toList());

        // 3️⃣ Chuyển subset topK thành JSON
        String productsJson = topK.stream()
                .map(p -> String.format("{\"name\":\"%s\",\"category\":\"%s\",\"description\":\"%s\",\"price\":%s}",
                        p.getName(), p.getCategory(), p.getDescription(), p.getPrice()))
                .collect(Collectors.joining(",", "[", "]"));

        // 4️⃣ Tạo prompt dynamic
        String prompt = """
                Bạn là chatbot tư vấn sản phẩm.
                Dữ liệu sản phẩm liên quan (JSON):
                %s

                Người dùng hỏi: %s
                Hãy trả lời gọn gàng, chỉ chọn sản phẩm trong dữ liệu trên.
                """.formatted(productsJson, request.message());

        // 5️⃣ Gọi ChatClient (Gemini / Coze)
        String reply = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return new ChatResponse(reply);
    }

    // Kiểm duyệt sản phẩm (giữ nguyên)
    public boolean isProductAllowed(ProductRequest request) {
        String prompt = """
                Bạn là một trợ lý kiểm duyệt sản phẩm. Hãy kiểm tra sản phẩm sau đây có vi phạm pháp luật hoặc thuộc danh mục hàng cấm tại Việt Nam hay không.
                Tên sản phẩm: %s
                Mô tả sản phẩm: %s
                Chỉ trả về "true" nếu sản phẩm được phép, và "false" nếu sản phẩm vi phạm hoặc bị cấm.
                """.formatted(request.name(), request.description());

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content()
                .trim();

        return Boolean.parseBoolean(response);
    }
}
