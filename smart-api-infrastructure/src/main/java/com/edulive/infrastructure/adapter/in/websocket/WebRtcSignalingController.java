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
     * Endpoint for processing WebRTC signaling messages (Offer, Answer, ICE Candidates, Join, Leave).
     * Client must send the message to /app/room/{roomId}/signaling.
     */
    @MessageMapping("/room/{roomId}/signaling")
    public void processSignalingMessage(@DestinationVariable String roomId, @Payload WebRtcMessageDto message) {
        logger.info("Received signaling message of type: {} in room: {} from sender: {} targeting: {}",
                message.getType(), roomId, message.getSenderId(), message.getTargetId());

        // Ensure the message has the correct roomId in case the client omitted it from the payload
        message.setRoomId(roomId);

        // Broadcast the message to all clients subscribed to the room topic.
        // The frontend filters by targetId for direct messages (e.g. offer/answer).
        String destination = "/topic/room/" + roomId + "/signaling";
        messagingTemplate.convertAndSend(destination, message);
    }
}
