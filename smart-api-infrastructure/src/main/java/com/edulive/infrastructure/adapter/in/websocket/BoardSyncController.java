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
 * STOMP controller for real-time whiteboard synchronization.
 *
 * Persistence flow:
 *  - action "add"   → persists the element in MongoDB and broadcasts to the room.
 *  - action "clear" → clears the history in MongoDB and broadcasts to the room.
 *
 * Restore flow on reconnect:
 *  - On subscription to /topic/room/{roomId}/board the client sends a message to
 *    /app/room/{roomId}/board/init. The server reads MongoDB history and sends
 *    an "init" message ONLY to that client (via its sessionId).
 */
@Controller
public class BoardSyncController {

    private static final Logger logger = LoggerFactory.getLogger(BoardSyncController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomRepositoryPort roomRepositoryPort;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public BoardSyncController(SimpMessagingTemplate messagingTemplate,
                               RoomRepositoryPort roomRepositoryPort,
                               com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.messagingTemplate  = messagingTemplate;
        this.roomRepositoryPort = roomRepositoryPort;
        this.objectMapper       = objectMapper;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. INITIAL SUBSCRIPTION — sends board history only to the new client
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Triggered automatically when a client sends to /app/room/{roomId}/board/init.
     * Reads the persisted history from MongoDB and sends it exclusively to the
     * requesting client via their sessionId (routed to /user/{sessionId}/queue/board-init).
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

        // Build "init" message with all history elements as JSON payload
        BoardSyncMessageDto initMessage = new BoardSyncMessageDto();
        initMessage.setAction("init");
        initMessage.setRoomId(roomId);
        initMessage.setSenderId("server");
        initMessage.setPayload(serializeElements(elements));

        // Send ONLY to the client that requested history, identified by their sessionId
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            // Spring routes to /user/{sessionId}/queue/board-init
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/board-init", initMessage);
            logger.debug("Board history dispatched to session: {}", sessionId);
        }
    }


    // ─────────────────────────────────────────────────────────────────────────
    // 2. REAL-TIME SYNC — persists and broadcasts to the room
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Endpoint for synchronizing whiteboard events (text, images, drawings).
     * Client sends to /app/room/{roomId}/board.
     */
    @MessageMapping("/room/{roomId}/board")
    public void syncBoardEvent(@DestinationVariable String roomId,
                               @Payload BoardSyncMessageDto message) {

        logger.debug("Board sync event: action={} room={} sender={}",
                message.getAction(), roomId, message.getSenderId());

        message.setRoomId(roomId);

        // Persist according to the action received
        switch (message.getAction()) {
            case "add" -> persistAddElement(roomId, message.getElement());
            case "clear" -> {
                roomRepositoryPort.clearBoardElements(roomId);
                logger.debug("Board history cleared for room: {}", roomId);
            }
            default -> { /* feedback, update, delete — no history persistence needed */ }
        }

        // Broadcast to all clients in the room (including the sender)
        String destination = "/topic/room/" + roomId + "/board";
        messagingTemplate.convertAndSend(destination, message);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Private helper methods ──────────────────────────────────────────────────────

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
     * Serializes the list of board elements to a JSON string for the message payload.
     * Jackson's ObjectMapper is injected as a singleton to avoid the overhead of
     * creating a new instance on every call.
     */
    private String serializeElements(List<BoardElement> elements) {
        try {
            return objectMapper.writeValueAsString(elements);
        } catch (Exception e) {
            logger.error("Failed to serialize board elements: {}", e.getMessage());
            return "[]";
        }
    }
}
