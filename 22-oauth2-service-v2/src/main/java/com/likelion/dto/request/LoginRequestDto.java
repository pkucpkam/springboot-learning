package com.likelion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
