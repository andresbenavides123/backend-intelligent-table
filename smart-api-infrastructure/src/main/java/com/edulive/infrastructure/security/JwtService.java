package com.edulive.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio de generación y validación de tokens JWT.
 *
 * Claims estándar utilizados:
 *   - sub  → nombre del usuario (estándar RFC 7519)
 *   - iat  → timestamp de emisión
 *   - exp  → timestamp de expiración
 *
 * Claims custom:
 *   - room → ID de la sala de colaboración
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-hours:24}") long expirationHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationHours * 60 * 60 * 1000L;
    }

    /**
     * Genera un token JWT de sesión firmado con HMAC-SHA256.
     * Válido por {@code app.jwt.expiration-hours} horas (default 24h).
     */
    public String generateToken(String roomId, String userName) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(userName)           // Claim estándar "sub"
                .claim("room", roomId)          // Claim custom
                .setIssuedAt(now)               // "iat"
                .setExpiration(expiry)          // "exp"
                .signWith(key)
                .compact();
    }

    /**
     * Valida la firma y la expiración del token.
     * @return true si el token es válido y no ha expirado.
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expirado: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Firma JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT malformado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims vacío: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrae el nombre del usuario desde el claim estándar "sub".
     */
    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae el ID de sala desde el claim custom "room".
     */
    public String extractRoomId(String token) {
        return extractAllClaims(token).get("room", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
