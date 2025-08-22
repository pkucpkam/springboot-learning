package com.example.demo.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        String email = System.getenv("EMAIL");
        String clientId = System.getenv("CLIENT_ID");
        String clientSecret = System.getenv("CLIENT_SECRET");
        String refreshToken = System.getenv("REFRESH_TOKEN");
        // Tạo Google OAuth2 credentials
        UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();

        String accessToken;
        try {
            accessToken = userCredentials.refreshAccessToken().getTokenValue();
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy access token từ refresh token", e);
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(email);
        mailSender.setPassword(accessToken); // Dùng access token
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2"); // quan trọng
        props.put("mail.debug", "true");
        return mailSender;
    }
}

