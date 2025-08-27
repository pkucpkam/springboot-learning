package com.likelion.properties;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "web.cors")
@Getter @Setter
public class WebCorsProperties {
    private List<String> allowedOrigins = List.of();
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    private List<String> allowedHeaders = List.of("*");
    private List<String> exposedHeaders = List.of("Authorization");
    private boolean allowCredentials = true;
    private Duration maxAge = Duration.ofSeconds(3600);
}
