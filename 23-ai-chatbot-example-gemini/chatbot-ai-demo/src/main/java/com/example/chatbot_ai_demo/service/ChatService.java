package com.example.chatbot_ai_demo.service;

import com.example.chatbot_ai_demo.dto.ChatRequest;
import com.example.chatbot_ai_demo.dto.ChatResponse;
import com.example.chatbot_ai_demo.dto.ProductRequest;
import com.example.chatbot_ai_demo.entity.Product;
import com.example.chatbot_ai_demo.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ProductRepository productRepository;

    public ChatService(ChatClient.Builder builder, ProductRepository productRepository) {
        this.chatClient = builder.build();
        this.productRepository = productRepository;
    }

    public ChatResponse sendMessage(ChatRequest request) {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return new ChatResponse("Hiện tại chưa có sản phẩm nào trong hệ thống.");
        }

        // Ví dụ: xử lý nhanh 1 số keyword trước khi gọi AI
        String msg = request.message().toLowerCase();
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

        // Ghép data sản phẩm thành context cho AI
        String productInfo = products.stream()
                .map(p -> String.format("%s (%s): %s - giá %s",
                        p.getName(), p.getCategory(), p.getDescription(), p.getPrice()))
                .collect(Collectors.joining("\n"));

        String prompt = """
                Bạn là chatbot tư vấn sản phẩm.
                Dữ liệu sản phẩm hiện có:
                %s

                Người dùng hỏi: %s
                Trả lời gọn gàng, dễ hiểu, ưu tiên chọn sản phẩm trong danh sách.
                """.formatted(productInfo, request.message());

        String reply = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return new ChatResponse(reply);
    }

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
