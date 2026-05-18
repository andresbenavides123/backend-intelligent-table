package com.edulive.infrastructure.adapter.in.web;

import com.edulive.infrastructure.adapter.in.web.dto.TokenRequestDto;
import com.edulive.infrastructure.adapter.in.web.dto.TokenResponseDto;
import com.edulive.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint público para obtener un token JWT de sesión.
 * Excluido de la autenticación JWT en SecurityConfig (PUBLIC_PATHS).
 *
 * Rate limiting aplicado por RateLimitFilter: máximo 20 req/min por IP.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Genera un token JWT de sesión para el usuario.
     *
     * @param request { roomId: "uuid-de-la-sala", name: "Nombre del usuario" }
     * @return        { token: "eyJhbGci..." }
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> generateToken(@Valid @RequestBody TokenRequestDto request) {
        String token = jwtService.generateToken(request.getRoomId(), request.getName());
        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
