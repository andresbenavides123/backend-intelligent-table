package com.edulive.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.context.annotation.Bean;
import com.edulive.infrastructure.security.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    @Autowired
    public WebSocketConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Habilita el broker para que atienda a los clientes en estos prefijos
        // /topic para mensajes masivos (1 a N, ideal para enviar trazos a todos en una sala)
        config.enableSimpleBroker("/topic", "/queue");
        // Prefijo que deben usar los clientes al enviar algo al servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Endpoint principal al que el Frontend se conectará (ej. http://localhost:8080/ws-board)
        registry.addEndpoint("/ws-board")
                .setAllowedOriginPatterns("*") // Se usa AllowedOriginPatterns "*" en dev, en prod cambiar por el dominio real
                .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(@NonNull org.springframework.web.socket.config.annotation.WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(5 * 1024 * 1024); // 5MB
        registration.setSendBufferSizeLimit(5 * 1024 * 1024); // 5MB
        registration.setSendTimeLimit(20000);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(5 * 1024 * 1024);
        container.setMaxBinaryMessageBufferSize(5 * 1024 * 1024);
        return container;
    }

    @Override
    public void configureClientInboundChannel(@NonNull org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("Missing or invalid Authorization header");
                    }
                    
                    String token = authHeader.substring(7);
                    if (!jwtService.isTokenValid(token)) {
                        throw new IllegalArgumentException("Invalid JWT token");
                    }
                    
                    // We can also extract the username and attach it to the session if needed
                    String userName = jwtService.extractUserName(token);
                    accessor.getSessionAttributes().put("userName", userName);
                }
                return message;
            }
        });
    }
}
