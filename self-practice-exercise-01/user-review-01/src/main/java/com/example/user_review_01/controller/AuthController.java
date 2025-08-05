package com.example.user_review_01.controller;

import com.example.user_review_01.dto.request.UserLoginRequest;
import com.example.user_review_01.dto.request.UserRegisterRequest;
import com.example.user_review_01.dto.response.UserLoginResponse;
import com.example.user_review_01.dto.response.UserResponse;
import com.example.user_review_01.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

}
