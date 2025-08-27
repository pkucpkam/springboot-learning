// package com.likelion.security;

// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
// import java.util.Date;
// import java.util.UUID;

// import javax.crypto.SecretKey;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.Authentication;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.JwtException;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import jakarta.annotation.PostConstruct;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class TokenProvider {
//     @Value("${app.jwt.secret}")
//     private String secret;

//     @Value("${app.jwt.access-expires-in-mili-seconds:600000}")
//     private long accessExpMs;

//     @Value("${app.jwt.refresh-expires-in-days:7}")
//     private long refreshExpDays;

//     private SecretKey key;

//     @PostConstruct
//     void init() {
//         this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
//     }

//     public String generateAccessToken(Authentication authentication) {
//         Date now = new Date();
//         Date exp = new Date(now.getTime() + accessExpMs);
//         return Jwts.builder()
//                 .subject(authentication.getName())
//                 .issuedAt(now)
//                 .expiration(exp)
//                 .signWith(key) // 0.12.x self-deduced algorithm from Key
//                 .compact();
//     }

//     public String generateRefreshToken(String username) {
//         Instant now = Instant.now();
//         return Jwts.builder()
//                 .subject(username)
//                 .id(UUID.randomUUID().toString()) // jti
//                 .issuedAt(Date.from(now))
//                 .expiration(Date.from(now.plus(refreshExpDays, ChronoUnit.DAYS)))
//                 .signWith(key)
//                 .compact();
//     }

//     public boolean validateToken(String token) {
//         try {
//             Jwts.parser()           // 0.12.x
//                 .verifyWith(key)    // replace setSigningKey(...)
//                 .build()
//                 .parseSignedClaims(token); // throw JwtException if token was error/expired
//             return true;
//         } catch (JwtException | IllegalArgumentException e) {
//             log.warn("Invalid JWT: {}", e.getMessage());
//             return false;
//         }
//     }

//     public String getSubject(String token) {
//         Claims claims = Jwts.parser()
//                 .verifyWith(key)
//                 .build()
//                 .parseSignedClaims(token)
//                 .getPayload();       // 0.12.x use getPayload() instead of getBody()
//         return claims.getSubject();
//     }

//     public Instant getExpiration(String token) {
//         Claims claims = Jwts.parser()
//                 .verifyWith(key)
//                 .build()
//                 .parseSignedClaims(token)
//                 .getPayload();
//         return claims.getExpiration().toInstant();
//     }

//     public String getToken(HttpServletRequest request) {
//         String bearer = request.getHeader("Authorization");
//         return (StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))
//                 ? bearer.substring(7) : null;
//     }
// }
