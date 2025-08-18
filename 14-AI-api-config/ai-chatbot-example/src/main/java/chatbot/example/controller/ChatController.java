package chatbot.example.controller;

import chatbot.example.service.ChatbotService;
import chatbot.example.dto.ChatRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        return chatbotService.chat(request.getMessage());
    }
}
