package com.likelion.dto.response;

import java.util.List;

import lombok.Value;

@Value
public class MeResponseDto {
    String sub;
    String username;
    String email;
    List<String> roles;
}
