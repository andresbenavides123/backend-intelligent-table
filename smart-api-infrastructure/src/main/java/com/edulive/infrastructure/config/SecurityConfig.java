package com.edulive.infrastructure.config;

import com.edulive.infrastructure.security.JwtAuthenticationEntryPoint;
import com.edulive.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración principal de Spring Security.
 *
 * Estrategia de seguridad:
 *  - Stateless (sin sesiones HTTP ni cookies de seguridad)
 *  - CSRF desactivado (API REST stateless no lo necesita)
 *  - JWT validado en JwtAuthenticationFilter (OncePerRequestFilter)
 *  - Rate Limiting previo via RateLimitFilter (@Order(1))
 *  - Security Headers: X-Frame-Options, X-Content-Type-Options, Referrer-Policy
 *  - CORS configurado desde Spring Security (fuente única de verdad)
 *
 * Control de acceso por perfil (app.security.require-auth):
 *  - false (perfil dev): todos los endpoints son accesibles sin token
 *  - true  (perfil prod): todos los endpoints protegidos requieren JWT válido
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final boolean requireAuth;
    private final String frontendUrl;

    /** Paths completamente públicos — siempre sin autenticación */
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/**",
            "/ws-board/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            JwtAuthenticationEntryPoint entryPoint,
            @Value("${app.security.require-auth:true}") boolean requireAuth,
            @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.entryPoint    = entryPoint;
        this.requireAuth   = requireAuth;
        this.frontendUrl   = frontendUrl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── CORS ───────────────────────────────────────────────────────────────
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ── CSRF ───────────────────────────────────────────────────────────────
            // Desactivado: API REST stateless con JWT, sin cookies de sesión
            .csrf(AbstractHttpConfigurer::disable)

            // ── Sesión ─────────────────────────────────────────────────────────────
            // STATELESS: cada request se autentica de forma independiente via JWT
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ── Manejo de errores de auth ───────────────────────────────────────────
            .exceptionHandling(ex ->
                    ex.authenticationEntryPoint(entryPoint))

            // ── Security Headers ───────────────────────────────────────────────────
            .headers(headers -> headers
                    .frameOptions(frame -> frame.deny())             // Previene clickjacking
                    .contentTypeOptions(cto -> {})                   // X-Content-Type-Options: nosniff
                    .referrerPolicy(ref ->
                            ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            )

            // ── Control de Acceso ──────────────────────────────────────────────────
            .authorizeHttpRequests(auth -> {
                // Paths siempre públicos
                auth.requestMatchers(PUBLIC_PATHS).permitAll();
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll(); // Preflight CORS

                if (requireAuth) {
                    // PROD: cualquier otro endpoint requiere autenticación JWT válida
                    auth.anyRequest().authenticated();
                } else {
                    // DEV: sin restricciones (facilita el desarrollo local)
                    auth.anyRequest().permitAll();
                }
            })

            // ── Filtro JWT ─────────────────────────────────────────────────────────
            // Se ejecuta ANTES del filtro de usuario/contraseña de Spring Security
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration centralizada para toda la aplicación.
     * Reemplaza CorsConfig.java — Spring Security tiene prioridad sobre WebMvcConfigurer.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos mediante patrones (soporta subdominios dinámicos de Vercel)
        config.setAllowedOriginPatterns(List.of(
                frontendUrl,
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "https://*.vercel.app"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Preflight cacheado por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
