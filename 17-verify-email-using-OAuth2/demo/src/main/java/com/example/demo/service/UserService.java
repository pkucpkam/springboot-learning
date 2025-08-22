package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final EmailService emailService;

    public UserService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void registerUser(String email) throws Exception {
        // 1. Lưu user vào DB với trạng thái chưa xác thực
        String verificationCode = UUID.randomUUID().toString();
        // save email + verificationCode + status=false

        // 2. Gửi email xác thực
        emailService.sendVerificationEmail(email, verificationCode);
    }

    public boolean verifyUser(String code) {
        // 3. Khi user click link, check code -> cập nhật trạng thái verified=true
        return true; // ví dụ
    }
}

