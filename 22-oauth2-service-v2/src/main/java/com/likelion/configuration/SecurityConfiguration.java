package com.likelion.configuration;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.likelion.security.ForbiddenEntryPoint;
import com.likelion.security.UnauthorizedEntryPoint;
import com.likelion.utility.KeyLoader;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
        private final UnauthorizedEntryPoint unauthorizedEntryPoint;
        private final ForbiddenEntryPoint forbiddenEntryPoint;
        private final WebCsrfConfiguration webCsrfConfiguration;
        private final KeyLoader keyLoader;

        @Bean
        Converter<Jwt, ? extends AbstractAuthenticationToken> authenticationConverter() {
                var roles = new JwtGrantedAuthoritiesConverter();
                roles.setAuthoritiesClaimName("roles"); // token nội bộ của bạn chứa "roles"
                roles.setAuthorityPrefix(""); // đã có tiền tố ROLE_ trong claim

                return jwt -> {
                        var authorities = new ArrayList<GrantedAuthority>(roles.convert(jwt));
                        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
                };
        }

        @Bean
        HandlerMappingIntrospector mvcIntrospector() {
                return new HandlerMappingIntrospector();
        }

        @Bean
        BearerTokenResolver bearerTokenResolver(HandlerMappingIntrospector introspector) {
                DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
                delegate.setAllowFormEncodedBodyParameter(false);
                delegate.setAllowUriQueryParameter(false);

                return request -> {
                        String path = request.getRequestURI();
                        // Ignore access token for endpoint refresh
                        if (path.startsWith("/api/auth/refresh")) {
                                return null;
                        }
                        return delegate.resolve(request);
                };
        }

        private static RequestMatcher authApiMatcher() {
                return request -> {
                        String base = request.getContextPath(); // thường là ""
                        String uri = request.getRequestURI();
                        return uri.startsWith(base + "/api/auth/");
                };
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
                        throws Exception {
                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(webCsrfConfiguration.csrfTokenRepository())
                                                .ignoringRequestMatchers(
                                                                webCsrfConfiguration.csrfIgnoringRequestMatcher(),
                                                                authApiMatcher() // ignore CSRF for /api/auth/**
                                                ))
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(unauthorizedEntryPoint) // 401
                                                .accessDeniedHandler(forbiddenEntryPoint) // 403
                                )
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .bearerTokenResolver(bearerTokenResolver(mvcIntrospector()))
                                                .jwt(jwt -> jwt.decoder(NimbusJwtDecoder
                                                                .withPublicKey(keyLoader.loadPublicKey()).build())))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/me").authenticated()
                                                .requestMatchers("/api/customers/**")
                                                .authenticated())
                                .build();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(12);
        }

}
