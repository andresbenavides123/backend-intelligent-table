package com.edulive.infrastructure.adapter.in.websocket;

import com.edulive.infrastructure.adapter.in.web.dto.BoardSyncMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class BoardSyncController {

    private static final Logger logger = LoggerFactory.getLogger(BoardSyncController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public BoardSyncController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Endpoint para sincronizar eventos de la pizarra (texto, imágenes, dibujos).
     * El cliente envía a /app/room/{roomId}/board
     */
    @MessageMapping("/room/{roomId}/board")
    public void syncBoardEvent(@DestinationVariable String roomId, @Payload BoardSyncMessageDto message) {
        logger.debug("Received board sync event: {} in room: {} from sender: {}", 
                message.getAction(), roomId, message.getSenderId());
        
        message.setRoomId(roomId);
        
        // Retransmitir a todos los clientes en la sala
        String destination = "/topic/room/" + roomId + "/board";
        messagingTemplate.convertAndSend(destination, message);
    }
}
