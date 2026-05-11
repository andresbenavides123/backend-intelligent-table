package com.edulive.infrastructure.adapter.in.web.dto;

public class BoardSyncMessageDto {
    private String action; // "add", "update", "delete", "clear"
    private String roomId;
    private String senderId;
    private BoardElementDto element;
    private String payload;

    public BoardSyncMessageDto() {}

    public BoardSyncMessageDto(String action, String roomId, String senderId, BoardElementDto element, String payload) {
        this.action = action;
        this.roomId = roomId;
        this.senderId = senderId;
        this.element = element;
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public BoardElementDto getElement() {
        return element;
    }

    public void setElement(BoardElementDto element) {
        this.element = element;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
