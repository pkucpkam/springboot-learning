package example.userdemo.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Kích hoạt simple message broker với prefix "/topic" (cho tin nhắn công khai) và "/queue" (cho tin nhắn cá nhân)
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix cho các tin nhắn từ client gửi đến server
        config.setApplicationDestinationPrefixes("/app");
        // Prefix cho các tin nhắn gửi đến người dùng cụ thể
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint WebSocket mà client sẽ kết nối
        registry.addEndpoint("/ws")
//                .setAllowedOrigins("http://127.0.0.1:5500")
                .setAllowedOriginPatterns("*")// Chỉ định origin cụ thể
                .withSockJS();
    }
}