// package com.likelion.security;

// import java.io.IOException;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.web.servlet.error.ErrorAttributes;
// import org.springframework.lang.NonNull;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;
// import org.springframework.web.filter.OncePerRequestFilter;
// import org.springframework.web.servlet.HandlerExceptionResolver;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class AuthenticationFilter extends OncePerRequestFilter {
//     private final TokenProvider tokenProvider;
//     private final UserPrincipal userPrincipal;
//     private final HandlerExceptionResolver resolver;

//     @Override
//     protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                     @NonNull HttpServletResponse response,
//                                     @NonNull FilterChain filterChain)
//             throws IOException, ServletException {
//         try {
//             Optional.ofNullable(tokenProvider.getToken(request))
//                 .filter(StringUtils::hasText)
//                 .filter(tokenProvider::validateToken)
//                 .map(tokenProvider::getSubject)
//                 .map(userPrincipal::loadUserByUsername)
//                 .map(userDetails -> {
//                     UsernamePasswordAuthenticationToken auth =
//                             new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                     auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                     return auth;
//                 })
//                 .ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
//         } catch (AuthenticationException ex) { // handle 401
//             SecurityContextHolder.clearContext();
//             log.error("Unauthorized error", ex);
//         } catch (AccessDeniedException ex) { // handle 403
//             SecurityContextHolder.clearContext();
//             log.error("Access denied error", ex);
//         } catch (Exception ex) { // handle another exceptions
//             SecurityContextHolder.clearContext();
//             resolver.resolveException(request, response, null, ex);
//         }

//         filterChain.doFilter(request, response);
//     }
// }
