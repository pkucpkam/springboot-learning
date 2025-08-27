package com.likelion.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.likelion.dto.response.MeResponseDto;
import com.likelion.utility.AuthUtility;

import lombok.RequiredArgsConstructor;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  // Optional: client-id để build logout URL
  @Value("${keycloak.client-id:}")
  private String clientId;

  /** Return user information from JWT (Keycloak) */
  @GetMapping("/me")
  public ResponseEntity<MeResponseDto> me(@AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    String username = AuthUtility.optionalStr(jwt.getClaimAsString("preferred_username"), sub);
    String email = jwt.getClaimAsString("email");
    List<String> roles = AuthUtility.extractAllRoles(jwt);

    return ResponseEntity.ok(new MeResponseDto(sub, username, email, roles));
  }

}
