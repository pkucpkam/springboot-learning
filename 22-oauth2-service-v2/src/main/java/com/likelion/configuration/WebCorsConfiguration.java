package com.likelion.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.likelion.properties.WebCorsProperties;

@Configuration
@EnableConfigurationProperties(WebCorsProperties.class)
public class WebCorsConfiguration {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource(WebCorsProperties corsProps) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(corsProps.getAllowedOrigins());
        corsConfig.setAllowedHeaders(corsProps.getAllowedHeaders());
        corsConfig.setAllowedMethods(corsProps.getAllowedMethods());
        corsConfig.setAllowCredentials(corsProps.isAllowCredentials());
        corsConfig.setExposedHeaders(corsProps.getExposedHeaders());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
