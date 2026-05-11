package com.edulive.infrastructure.adapter.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Datos invalidos: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        log.error("Unhandled server exception: {}", e.getMessage(), e);

        // Detectar errores de rate limit de Groq (HTTP 429)
        if (isRateLimitError(e)) {
            log.warn("Groq API rate limit exceeded — returning 429 to client");
            Map<String, String> response = new HashMap<>();
            response.put("error", "Limite de solicitudes de IA alcanzado. Por favor espera un momento e intenta de nuevo.");
            response.put("code", "RATE_LIMIT_EXCEEDED");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno del servidor. Por favor, revisa los datos enviados.");
        response.put("details", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Inspects the full exception chain for Groq/OpenAI 429 rate-limit signals.
     * Groq returns HTTP 429 with a message containing "rate_limit_exceeded" or "429".
     */
    private boolean isRateLimitError(Throwable e) {
        Throwable current = e;
        while (current != null) {
            String msg = current.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase();
                if (lower.contains("429")
                        || lower.contains("rate_limit_exceeded")
                        || lower.contains("rate limit")
                        || lower.contains("quota exceeded")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }
}
