package com.edulive.infrastructure.adapter.in.websocket;

import com.edulive.infrastructure.adapter.in.web.dto.WebRtcMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebRtcSignalingController {

    private static final Logger logger = LoggerFactory.getLogger(WebRtcSignalingController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebRtcSignalingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Endpoint para procesar mensajes de señalización WebRTC (Offer, Answer, ICE Candidates, Join, Leave).
     * El cliente debe enviar el mensaje a /app/room/{roomId}/signaling
     */
    @MessageMapping("/room/{roomId}/signaling")
    public void processSignalingMessage(@DestinationVariable String roomId, @Payload WebRtcMessageDto message) {
        logger.info("Received signaling message of type: {} in room: {} from sender: {} targeting: {}", 
                message.getType(), roomId, message.getSenderId(), message.getTargetId());
        
        // Asegurar que el mensaje tenga el roomId correcto por si acaso no lo incluyeron en el payload
        message.setRoomId(roomId);
        
        // Re-enviar (broadcast) el mensaje a todos los clientes suscritos al topic de la sala
        // El frontend se encarga de filtrar por targetId si es un mensaje directo (como offer/answer)
        String destination = "/topic/room/" + roomId;
        messagingTemplate.convertAndSend(destination, message);
    }
}
