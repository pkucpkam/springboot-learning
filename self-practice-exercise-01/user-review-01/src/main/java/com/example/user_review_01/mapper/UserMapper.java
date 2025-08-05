package com.example.user_review_01.mapper;

import com.example.user_review_01.dto.request.UserRegisterRequest;
import com.example.user_review_01.dto.response.UserInformationResponse;
import com.example.user_review_01.dto.response.UserLoginResponse;
import com.example.user_review_01.dto.response.UserResponse;
import com.example.user_review_01.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "name", source = "name")
    UserEntity toEntity(UserRegisterRequest request);

    UserResponse toResponse(UserEntity user);

    UserInformationResponse toInfoResponse(UserEntity response);

    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(com.example.user_review_01.entity.RoleEntity::getRoleName).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "jwt", ignore = true)
    UserLoginResponse toLoginResponse(UserEntity user);
}