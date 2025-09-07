package com.likelion.controller;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.likelion.dto.GoogleUser;
import com.likelion.dto.response.GoogleTokenResponse;
import com.likelion.entity.RefreshToken;
import com.likelion.entity.User;
import com.likelion.service.GoogleTokenService;
import com.likelion.service.JwtService;
import com.likelion.service.RefreshTokenService;
import com.likelion.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final GoogleTokenService googleToken;
  private final UserService userService; // find-or-create user
  private final JwtService jwtService;
  private final RefreshTokenService refreshService;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;
  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String clientSecret;
  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String redirectUri;

  // Dành cho web app: bạn có thể redirect trực tiếp
  @GetMapping("/login/google")
  public void redirectToGoogle(HttpServletResponse response) throws IOException {
    String url = UriComponentsBuilder
        .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
        .queryParam("client_id", clientId)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("response_type", "code")
        .queryParam("scope", "openid email profile")
        .queryParam("access_type", "offline") // để Google cấp refresh_token (lần đầu)
        .queryParam("prompt", "consent") // buộc consent để nhận refresh_token
        .build().toUriString();
    response.sendRedirect(url);
  }

  // Callback nhận code từ Google
  @GetMapping("/login/google/callback")
  public ResponseEntity<?> googleCallback(@RequestParam String code) {
    GoogleTokenResponse gtr = googleToken.exchangeCode(code);
    GoogleUser guser = googleToken.parseAndVerify(gtr.getId_token());

    User user = userService.upsertGoogleUser(guser);

    // 1) Issue session (server-side refresh)
    var issue = refreshService.issue(user, null); // sid + rawRefresh (server only)

    // 2) Create short-lived Access Token
    String access = jwtService.createAccessToken(user.getId(), user.getEmail(), user.getRoleCodes());

    // 3) Set cookies
    ResponseCookie at = ResponseCookie.from("AT", access)
        .httpOnly(true).secure(true).sameSite("Strict")
        .path("/").maxAge(Duration.ofMinutes(15)).build();

    ResponseCookie sid = ResponseCookie.from("SID", issue.sessionId())
        .httpOnly(true).secure(true).sameSite("Strict")
        .path("/").maxAge(Duration.ofDays(14)).build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, at.toString())
        .header(HttpHeaders.SET_COOKIE, sid.toString())
        .body(Map.of(
            "access_token", access,
            "session_id", issue.sessionId(),
            "token_type", "Bearer",
            "expires_in", 900));
  }

  @PostMapping("/refresh-login")
  public ResponseEntity<?> refresh(@CookieValue(name = "SID", required = false) String sid) {
    if (sid == null || sid.isBlank()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No session"));
    }
    var session = refreshService.validateBySession(sid); // kiểm tra còn hạn/chưa revoke
    if (session == null) {
        // clear cookies
        ResponseCookie at0  = ResponseCookie.from("AT","").maxAge(0).path("/").httpOnly(true).build();
        ResponseCookie sid0 = ResponseCookie.from("SID","").maxAge(0).path("/").httpOnly(true).build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, at0.toString(), sid0.toString())
                .body(Map.of("error","Invalid session"));
    }

    var user = session.getUser();
    var issued = refreshService.issue(user, sid); // <<--- PASS SID HIỆN TẠI để UPDATE

    String newAT = jwtService.createAccessToken(user.getId(), user.getEmail(), user.getRoleCodes());

    ResponseCookie at = ResponseCookie.from("AT", newAT)
            .httpOnly(true).secure(false) // dev
            .sameSite("Lax").path("/")
            .maxAge(Duration.ofMinutes(15)).build();

    // giữ nguyên SID (không rotate) ở mô hình 1-record-per-session
    ResponseCookie sidCookie = ResponseCookie.from("SID", sid)
            .httpOnly(true).secure(false) // dev
            .sameSite("Lax").path("/")
            .maxAge(Duration.ofDays(14)).build();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, at.toString(), sidCookie.toString())
            .body(Map.of("refreshed", true));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(@CookieValue(value = "SID", required = false) String sid) {
    if (sid != null) {
      try {
        RefreshToken rt = refreshService.validateBySession(sid);
        rt.setRevoked(true);
      } catch (IllegalArgumentException ignore) {
      }
    }
    ResponseCookie clearAt = ResponseCookie.from("AT", "").httpOnly(true).secure(true)
        .sameSite("Strict").path("/").maxAge(0).build();
    ResponseCookie clearSid = ResponseCookie.from("SID", "").httpOnly(true).secure(true)
        .sameSite("Strict").path("/").maxAge(0).build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, clearAt.toString())
        .header(HttpHeaders.SET_COOKIE, clearSid.toString())
        .build();
  }

}
