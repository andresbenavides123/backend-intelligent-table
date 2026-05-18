package com.edulive.infrastructure.config;

/**
 * DESACTIVADO — Reemplazado por SecurityConfig.java + JwtAuthenticationFilter.java
 *
 * El registro de interceptores de Spring MVC fue reemplazado por un
 * SecurityFilterChain de Spring Security con un OncePerRequestFilter (JwtAuthenticationFilter).
 * Este enfoque es más robusto y tiene acceso completo al SecurityContext de Spring.
 *
 * Este archivo se conserva como referencia histórica pero no tiene efecto
 * porque la anotación @Configuration fue removida intencionalmente.
 */
// @Configuration  ← REMOVIDO: reemplazado por SecurityConfig + JwtAuthenticationFilter
public class WebMvcConfig {
    // Vacío — ver SecurityConfig y JwtAuthenticationFilter
}
