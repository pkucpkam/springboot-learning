package com.example.user_review_01.service;

import com.example.user_review_01.dto.request.UserLoginRequest;
import com.example.user_review_01.dto.request.UserRegisterRequest;
import com.example.user_review_01.dto.response.UserInformationResponse;
import com.example.user_review_01.dto.response.UserLoginResponse;
import com.example.user_review_01.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
    UserLoginResponse login(UserLoginRequest request);
    UserInformationResponse getCurrentUserInfo();
    UserResponse getUserById(UUID id);
    List<UserInformationResponse> getAllUsers();
    UserResponse updateUser(UUID id, UserRegisterRequest request);
    void deleteUser(UUID id);
}
