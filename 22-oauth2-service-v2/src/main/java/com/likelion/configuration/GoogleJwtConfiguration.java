package com.likelion.configuration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class GoogleJwtConfiguration {
    private static final String GOOGLE_ISSUER = "https://accounts.google.com";
    private static final String GOOGLE_JWKS = "https://www.googleapis.com/oauth2/v3/certs";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Bean
    public ConfigurableJWTProcessor<SecurityContext> googleJwtProcessor() throws Exception {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new IllegalStateException(
                    "Missing 'spring.security.oauth2.client.registration.google.client-id' in application.yml");
        }
        
        var resourceRetriever = new DefaultResourceRetriever(
                (int) Duration.ofSeconds(3).toMillis(), // connection timeout
                (int) Duration.ofSeconds(3).toMillis(), // read timeout
                1024 * 1024 // maximum size limit
        );

        // JWKSourceBuilder is the modern replacement for RemoteJWKSet, comes with cache
        // and rate-limiting support
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(
                URI.create(GOOGLE_JWKS).toURL(), // avoid deprecated URL(String) constructor
                resourceRetriever // optional: can be omitted if using defaults
        )
                // .cache(ttlMillis, refreshAheadMillis) // optional: configure caching
                // .retrying(true) // optional: enable retries on network errors
                // .outageTolerant(ttlMillis) // optional: tolerate outages by using cached keys
                .build();

        var jwtProcessor = new DefaultJWTProcessor<SecurityContext>();

        // Google uses RS256 for ID Token signatures
        var keySelector = new JWSVerificationKeySelector<SecurityContext>(JWSAlgorithm.RS256, jwkSource);
        jwtProcessor.setJWSKeySelector(keySelector);

        // Enforce issuer & audience (client_id) + required claims
        JWTClaimsSet expectedClaims = new JWTClaimsSet.Builder()
                .issuer(GOOGLE_ISSUER)
                .audience(googleClientId)
                .build();

        Set<String> requiredClaims = new HashSet<>();
        requiredClaims.add("sub");
        requiredClaims.add("exp");
        requiredClaims.add("iat");
        // You may also require: "email", "email_verified", ... if needed

        var claimsVerifier = new DefaultJWTClaimsVerifier<SecurityContext>(expectedClaims, requiredClaims);
        jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);

        return jwtProcessor;
    }

}
