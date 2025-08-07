package com.example.user_review_01.mapper;

import com.example.user_review_01.dto.request.UserRegisterRequest;
import com.example.user_review_01.dto.response.UserInformationResponse;
import com.example.user_review_01.dto.response.UserLoginResponse;
import com.example.user_review_01.dto.response.UserResponse;
import com.example.user_review_01.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "name", source = "name")
    User toEntity(UserRegisterRequest request);

    UserResponse toResponse(User user);

    UserInformationResponse toInfoResponse(User response);

    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(com.example.user_review_01.entity.Role::getRoleName).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "jwt", ignore = true)
    UserLoginResponse toLoginResponse(User user);
}