package com.edulive.infrastructure.adapter.in.websocket;

import com.edulive.domain.model.BoardElement;
import com.edulive.domain.port.out.RoomRepositoryPort;
import com.edulive.infrastructure.adapter.in.web.dto.BoardElementDto;
import com.edulive.infrastructure.adapter.in.web.dto.BoardSyncMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controlador STOMP para la sincronización de la pizarra.
 *
 * Flujo de persistencia:
 *  - action "add"   → persiste el elemento en MongoDB y retransmite a la sala.
 *  - action "clear" → limpia el historial en MongoDB y retransmite a la sala.
 *
 * Flujo de restauración al conectarse:
 *  - @SubscribeMapping → al suscribirse a /topic/room/{roomId}/board,
 *    lee el historial de MongoDB y envía un mensaje "init" SOLO al cliente
 *    que se acaba de conectar (usando headerAccessor para obtener su sessionId).
 */
@Controller
public class BoardSyncController {

    private static final Logger logger = LoggerFactory.getLogger(BoardSyncController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomRepositoryPort roomRepositoryPort;

    public BoardSyncController(SimpMessagingTemplate messagingTemplate,
                               RoomRepositoryPort roomRepositoryPort) {
        this.messagingTemplate  = messagingTemplate;
        this.roomRepositoryPort = roomRepositoryPort;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. SUSCRIPCIÓN INICIAL — envía historial solo al nuevo cliente
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Se dispara automáticamente cuando un cliente se suscribe a
     * /topic/room/{roomId}/board (prefijo /app en el cliente STOMP).
     *
     * Spring devuelve el valor retornado AL CLIENTE QUE SE SUSCRIBIÓ
     * (equivalente a SimpMessagingTemplate.convertAndSendToUser pero sin
     * necesitar autenticación: se usa el sessionId de la cabecera).
     *
     * El cliente envía la suscripción a: /app/room/{roomId}/board/init
     */
    @MessageMapping("/room/{roomId}/board/init")
    public void sendBoardHistory(
            @DestinationVariable String roomId,
            SimpMessageHeaderAccessor headerAccessor) {

        List<BoardElement> elements = roomRepositoryPort.getBoardElements(roomId);

        if (elements.isEmpty()) {
            logger.debug("No board history found for room: {}", roomId);
            return;
        }

        logger.debug("Sending board history ({} elements) to new client in room: {}",
                elements.size(), roomId);

        // Construir mensaje "init" con todos los elementos del historial como payload JSON
        BoardSyncMessageDto initMessage = new BoardSyncMessageDto();
        initMessage.setAction("init");
        initMessage.setRoomId(roomId);
        initMessage.setSenderId("server");
        initMessage.setPayload(serializeElements(elements));

        // Enviar SOLO al cliente que solicitó el historial, usando su sessionId como identificador
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            // Spring enruta a /user/{sessionId}/queue/board-init
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/board-init", initMessage);
            logger.debug("Board history dispatched to session: {}", sessionId);
        }
    }


    // ─────────────────────────────────────────────────────────────────────────
    // 2. SINCRONIZACIÓN EN TIEMPO REAL — persiste y retransmite a la sala
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Endpoint para sincronizar eventos de la pizarra (texto, imágenes, dibujos).
     * El cliente envía a /app/room/{roomId}/board
     */
    @MessageMapping("/room/{roomId}/board")
    public void syncBoardEvent(@DestinationVariable String roomId,
                               @Payload BoardSyncMessageDto message) {

        logger.debug("Board sync event: action={} room={} sender={}",
                message.getAction(), roomId, message.getSenderId());

        message.setRoomId(roomId);

        // Persistir según la acción recibida
        switch (message.getAction()) {
            case "add" -> persistAddElement(roomId, message.getElement());
            case "clear" -> {
                roomRepositoryPort.clearBoardElements(roomId);
                logger.debug("Board history cleared for room: {}", roomId);
            }
            default -> { /* feedback, update, delete — no requieren persistencia de historial */ }
        }

        // Retransmitir a todos los clientes de la sala (incluyendo el remitente)
        String destination = "/topic/room/" + roomId + "/board";
        messagingTemplate.convertAndSend(destination, message);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos privados de soporte
    // ─────────────────────────────────────────────────────────────────────────

    private void persistAddElement(String roomId, BoardElementDto dto) {
        if (dto == null) return;
        try {
            BoardElement element = new BoardElement(
                    dto.getId(),
                    dto.getType(),
                    dto.getX(),
                    dto.getY(),
                    dto.getContent(),
                    dto.getColor(),
                    dto.getSize()
            );
            roomRepositoryPort.addBoardElement(roomId, element);
        } catch (Exception e) {
            logger.error("Failed to persist board element for room {}: {}", roomId, e.getMessage());
        }
    }

    /**
     * Serializa la lista de elementos a JSON simple para incluirla en el payload del mensaje.
     * Se usa un formato de array JSON manual para evitar dependencias extra en este módulo.
     */
    private String serializeElements(List<BoardElement> elements) {
        // Usamos el ObjectMapper de Spring (Jackson) a través de la serialización estándar.
        // Al llegar al cliente, el payload del BoardSyncMessageDto será una cadena JSON
        // que el frontend parsea con JSON.parse().
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(elements);
        } catch (Exception e) {
            logger.error("Failed to serialize board elements: {}", e.getMessage());
            return "[]";
        }
    }
}
