package com.edulive.infrastructure.config;

/**
 * DESACTIVADO — Reemplazado por SecurityConfig.java
 *
 * La configuración CORS ahora es manejada directamente por Spring Security
 * en SecurityConfig#corsConfigurationSource(), que tiene prioridad sobre
 * WebMvcConfigurer y garantiza que CORS funcione correctamente con JWT stateless.
 *
 * Este archivo se conserva como referencia histórica pero no tiene efecto
 * porque la anotación @Configuration fue removida intencionalmente.
 */
// @Configuration  ← REMOVIDO: reemplazado por SecurityConfig
public class CorsConfig {
    // Vacío — ver SecurityConfig.corsConfigurationSource()
}
