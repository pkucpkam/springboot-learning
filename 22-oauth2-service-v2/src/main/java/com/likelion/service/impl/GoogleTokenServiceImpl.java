package com.likelion.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.likelion.dto.GoogleUser;
import com.likelion.dto.response.GoogleTokenResponse;
import com.likelion.service.GoogleTokenService;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleTokenServiceImpl implements GoogleTokenService {
    private final WebClient.Builder webClientBuilder;

    // Bean này phải trùng tên @Bean trong GoogleJwtConfiguration:
    // googleJwtProcessor
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Override
    public GoogleTokenResponse exchangeCode(String code) {
        WebClient webClient = webClientBuilder.build();
        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("code", code)
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();
    }

    public GoogleUser parseAndVerify(String idToken) {
        try {
            JWTClaimsSet claims = jwtProcessor.process(idToken, null);

            if (!claims.getAudience().contains(clientId)) {
                throw new IllegalArgumentException("Invalid audience");
            }
            if (new Date().after(claims.getExpirationTime())) {
                throw new IllegalArgumentException("Token expired");
            }

            return new GoogleUser(
                    claims.getSubject(),
                    (String) claims.getClaim("email"),
                    (String) claims.getClaim("name"),
                    (String) claims.getClaim("picture"));
        } catch (Exception e) {
            throw new RuntimeException("Invalid ID token", e);
        }
    }

}
