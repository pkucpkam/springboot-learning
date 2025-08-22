package com.example.demo.controller;

import com.example.demo.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(@RequestParam String email) throws MessagingException {
        try {
            userService.registerUser(email);
            return "Vui lòng kiểm tra email để xác thực!";
        } catch (Exception e) {
            return "Gửi email thất bại: " + e.getMessage();
        }
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String code) {
        if (userService.verifyUser(code)) {
            return "Xác thực thành công!";
        } else {
            return "Xác thực thất bại!";
        }
    }
}
