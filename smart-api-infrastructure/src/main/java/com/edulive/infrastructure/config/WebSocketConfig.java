package com.edulive.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita el broker para que atienda a los clientes en estos prefijos
        // /topic para mensajes masivos (1 a N, ideal para enviar trazos a todos en una sala)
        config.enableSimpleBroker("/topic", "/queue");
        // Prefijo que deben usar los clientes al enviar algo al servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint principal al que el Frontend se conectará (ej. http://localhost:8080/ws-board)
        registry.addEndpoint("/ws-board")
                .setAllowedOriginPatterns("*") // Se usa AllowedOriginPatterns "*" en dev, en prod cambiar por el dominio real
                .withSockJS();
    }
}
