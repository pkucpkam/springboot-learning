package com.likelion.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.likelion.security.ForbiddenEntryPoint;
import com.likelion.security.UnauthorizedEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final ForbiddenEntryPoint forbiddenEntryPoint;
    private final WebCsrfConfiguration webCsrfConfiguration;

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> authenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("realm_access.roles"); // get roles from claim
        roles.setAuthorityPrefix("ROLE_");

        return jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(roles.convert(jwt));
            // (optional) map additional authorities from resource_access if needed
            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
            throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(webCsrfConfiguration.csrfTokenRepository())
                        .ignoringRequestMatchers(webCsrfConfiguration.csrfIgnoringRequestMatcher()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedEntryPoint) // 401
                        .accessDeniedHandler(forbiddenEntryPoint) // 403
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                                authenticationConverter())))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**", "/public/**").permitAll()
                        .requestMatchers("/api/auth/me", "/api/customers/**")
                        .authenticated())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
