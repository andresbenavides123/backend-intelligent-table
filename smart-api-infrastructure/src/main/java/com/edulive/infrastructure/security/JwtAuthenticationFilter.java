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
 * Filtro JWT que se ejecuta UNA SOLA VEZ por request (OncePerRequestFilter).
 *
 * Flujo:
 *  1. Extrae el token del header "Authorization: Bearer <token>"
 *  2. Si no hay token → deja continuar (Spring Security decide con base en la config del endpoint)
 *  3. Si el token es válido → fija el Authentication en el SecurityContext
 *  4. Si el token es inválido → limpia el SecurityContext (Spring Security retornará 401)
 *
 * Rutas excluidas (shouldNotFilter): /api/v1/auth/**, /ws-board/**, /swagger-ui/**, /v3/api-docs/**
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

        // Sin header Authorization → continúa (Spring Security manejará el acceso según la config)
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            if (jwtService.isTokenValid(token)) {
                String userName = jwtService.extractUserName(token);
                String roomId   = jwtService.extractRoomId(token);

                // Solo establecer autenticación si aún no hay ninguna en el contexto
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

                // Pasar claims como atributos del request para los controladores
                request.setAttribute("userName", userName);
                request.setAttribute("roomId", roomId);

            } else {
                // Token presente pero inválido → limpiar contexto para garantizar 401
                SecurityContextHolder.clearContext();
                log.debug("Token JWT inválido rechazado para: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("Error procesando token JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Omite este filtro completamente para paths públicos.
     * Spring Security aún aplicará sus reglas de acceso, pero no es necesario validar JWT aquí.
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
