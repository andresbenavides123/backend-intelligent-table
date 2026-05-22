package com.edulive.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT filter that runs ONCE per request (OncePerRequestFilter).
 *
 * Flow:
 *  1. Extracts the token from the "Authorization: Bearer <token>" header.
 *  2. If no token is present → passes through (Spring Security decides access based on endpoint config).
 *  3. If the token is valid  → sets the Authentication object in the SecurityContext.
 *  4. If the token is invalid → clears the SecurityContext (Spring Security will return 401).
 *
 * Excluded paths (shouldNotFilter): /api/v1/auth/**, /ws-board/**, /swagger-ui/**, /v3/api-docs/**
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTH_HEADER);

        // No Authorization header → continue (Spring Security handles access according to config)
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            if (jwtService.isTokenValid(token)) {
                String userName = jwtService.extractUserName(token);
                String roomId   = jwtService.extractRoomId(token);

                // Only set authentication if none is already present in the context
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userName,
                                    null,
                                    Collections.emptyList() // Sin roles/authorities por ahora
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

                // Forward claims as request attributes for use in controllers
                request.setAttribute("username", userName);
                request.setAttribute("roomId", roomId);

            } else {
                // Token present but invalid → clear context to guarantee 401
                SecurityContextHolder.clearContext();
                log.debug("Invalid JWT token rejected for path: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Skips this filter entirely for public paths.
     * Spring Security still applies its access rules, but JWT validation is not needed here.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/")
                || path.startsWith("/ws-board")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}
