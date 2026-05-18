package com.edulive.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro de Rate Limiting usando un Token Bucket simple con ventana deslizante.
 * Se ejecuta ANTES del filtro JWT (@Order(1)).
 *
 * Límites configurados en application.yml:
 *   - POST /api/v1/auth/token        → app.security.rate-limit.auth-rpm    (default 20 req/min)
 *   - POST /api/v1/exercises/analyze → app.security.rate-limit.analyze-rpm (default 5 req/min)
 *
 * El límite se aplica POR IP. Soporta X-Forwarded-For para Railway/Nginx.
 */
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final int authRpm;
    private final int analyzeRpm;

    // Implementación Token Bucket en memoria sin dependencias externas
    private final ConcurrentHashMap<String, TokenBucket> authBuckets    = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TokenBucket> analyzeBuckets = new ConcurrentHashMap<>();

    public RateLimitFilter(
            @Value("${app.security.rate-limit.auth-rpm:20}") int authRpm,
            @Value("${app.security.rate-limit.analyze-rpm:5}") int analyzeRpm) {
        this.authRpm    = authRpm;
        this.analyzeRpm = analyzeRpm;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();
        TokenBucket bucket = null;
        String endpoint = null;

        if ("/api/v1/auth/token".equals(path)) {
            String ip = resolveClientIp(request);
            bucket   = authBuckets.computeIfAbsent(ip, k -> new TokenBucket(authRpm));
            endpoint = "auth/token";
        } else if ("/api/v1/exercises/analyze".equals(path)) {
            String ip = resolveClientIp(request);
            bucket   = analyzeBuckets.computeIfAbsent(ip, k -> new TokenBucket(analyzeRpm));
            endpoint = "exercises/analyze";
        }

        if (bucket != null && !bucket.tryConsume()) {
            log.warn("Rate limit excedido desde {} en /{}", resolveClientIp(request), endpoint);
            sendRateLimitResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Resuelve la IP real del cliente considerando proxies inversos (Railway, Nginx, Cloudflare).
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"error":"Demasiadas solicitudes. Por favor espera un momento e intenta de nuevo.","code":"RATE_LIMIT_EXCEEDED"}
                """);
    }

    /**
     * Token Bucket simple basado en ventana de 1 minuto.
     * Thread-safe gracias a AtomicInteger y sincronización en refill.
     */
    private static class TokenBucket {
        private final int capacity;
        private final AtomicInteger tokens;
        private volatile Instant windowStart;

        TokenBucket(int capacity) {
            this.capacity    = capacity;
            this.tokens      = new AtomicInteger(capacity);
            this.windowStart = Instant.now();
        }

        synchronized boolean tryConsume() {
            Instant now = Instant.now();
            // Refill cada 60 segundos
            if (now.minusSeconds(60).isAfter(windowStart)) {
                tokens.set(capacity);
                windowStart = now;
            }
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        }
    }
}
