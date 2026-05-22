package com.edulive.infrastructure.adapter.in.web;

import com.edulive.infrastructure.adapter.in.web.dto.TokenRequestDto;
import com.edulive.infrastructure.adapter.in.web.dto.TokenResponseDto;
import com.edulive.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public endpoint for obtaining a JWT session token.
 * Excluded from JWT authentication in SecurityConfig (PUBLIC_PATHS).
 *
 * Rate limiting applied by RateLimitFilter: max 20 req/min per IP.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Generates a JWT session token for the user.
     *
     * @param request { roomId: "room-uuid", name: "User Name" }
     * @return        { token: "eyJhbGci..." }
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> generateToken(@Valid @RequestBody TokenRequestDto request) {
        String token = jwtService.generateToken(request.getRoomId(), request.getName());
        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
