package com.edulive.infrastructure.config;

import com.edulive.infrastructure.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * Configuración del broker STOMP sobre WebSocket.
 *
 * Seguridad del canal inbound (STOMP CONNECT):
 *   - Perfil dev  (ws-require-auth=false): token opcional, conexión siempre permitida.
 *   - Perfil prod (ws-require-auth=true):  token JWT obligatorio y válido para conectarse.
 *
 * Los límites de buffer/mensaje se fijan en 5MB para soportar la transmisión
 * de trazos complejos e imágenes Base64 pegadas en la pizarra.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    private final JwtService jwtService;
    private final boolean wsRequireAuth;
    private final String frontendUrl;

    public WebSocketConfig(
            JwtService jwtService,
            @Value("${app.security.ws-require-auth:true}") boolean wsRequireAuth,
            @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.jwtService     = jwtService;
        this.wsRequireAuth  = wsRequireAuth;
        this.frontendUrl    = frontendUrl;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        // Habilita enrutamiento de mensajes personales: /user/queue/...
        // Necesario para enviar el historial de pizarra únicamente al cliente que se conecta
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-board")
                // En prod, reemplazado por el dominio real via FRONTEND_URL env var
                .setAllowedOriginPatterns(frontendUrl, "http://localhost:5173", "http://127.0.0.1:5173")
                .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(
            @NonNull org.springframework.web.socket.config.annotation.WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(5 * 1024 * 1024); // 5 MB
        registration.setSendBufferSizeLimit(5 * 1024 * 1024);
        registration.setSendTimeLimit(20_000);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(5 * 1024 * 1024);
        container.setMaxBinaryMessageBufferSize(5 * 1024 * 1024);
        return container;
    }

    @Override
    public void configureClientInboundChannel(
            @NonNull org.springframework.messaging.simp.config.ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
                    return message;
                }

                String authHeader = accessor.getFirstNativeHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    // Token presente → validarlo siempre (dev y prod)
                    String token = authHeader.substring(7);
                    if (jwtService.isTokenValid(token)) {
                        String userName = jwtService.extractUserName(token);
                        String roomId   = jwtService.extractRoomId(token);
                        if (accessor.getSessionAttributes() != null) {
                            accessor.getSessionAttributes().put("userName", userName);
                            accessor.getSessionAttributes().put("roomId", roomId);
                        }
                        log.debug("WebSocket CONNECT autenticado: user={}, room={}", userName, roomId);
                    } else {
                        // Token inválido
                        if (wsRequireAuth) {
                            log.warn("WebSocket CONNECT rechazado — token inválido (prod mode)");
                            throw new IllegalArgumentException("Token JWT inválido o expirado.");
                        }
                        log.debug("WebSocket CONNECT con token inválido — permitido en modo dev");
                    }
                } else {
                    // Sin token
                    if (wsRequireAuth) {
                        log.warn("WebSocket CONNECT rechazado — sin token (prod mode)");
                        throw new IllegalArgumentException("Se requiere token JWT para conectarse al WebSocket.");
                    }
                    log.debug("WebSocket CONNECT sin token — permitido en modo dev");
                }

                return message;
            }
        });
    }
}
