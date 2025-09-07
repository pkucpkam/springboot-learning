package com.likelion.dto.response;

import lombok.Data;

@Data
public class GoogleTokenResponse {
    private String access_token;
    private String id_token;
    private String refresh_token; // only have when access_type=offline & prompt=consent
    private String scope;
    private String token_type;
    private Long expires_in;
}
