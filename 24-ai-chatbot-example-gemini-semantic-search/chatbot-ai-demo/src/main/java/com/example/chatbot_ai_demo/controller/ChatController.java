package com.example.chatbot_ai_demo.controller;

import com.example.chatbot_ai_demo.dto.ChatRequest;
import com.example.chatbot_ai_demo.dto.ChatResponse;
import com.example.chatbot_ai_demo.dto.ProductRequest;
import com.example.chatbot_ai_demo.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.sendMessage(request);
    }

    @PostMapping("/check")
    public boolean isProductAllowed(@RequestBody ProductRequest request) {
        return chatService.isProductAllowed(request);
    }
}
