package com.likelion.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class WebCsrfConfiguration {
    
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        // Send token to FE via cookie XSRF-TOKEN, FE send back via header X-XSRF-TOKEN
        var repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setCookieName("XSRF-TOKEN");
        repo.setHeaderName("X-XSRF-TOKEN");
        repo.setCookiePath("/");
        return repo;
    }

    @Bean
    public RequestMatcher csrfIgnoringRequestMatcher() {
        // Ignore CSRF for endpoints auth/health if needed
        return new OrRequestMatcher(
            new RegexRequestMatcher("^/auth(/.*)?$", null),
            new RegexRequestMatcher("^/api/auth(/.*)?$", null),
            new RegexRequestMatcher("^/actuator(/.*)?$", null)
        );
    }
}
