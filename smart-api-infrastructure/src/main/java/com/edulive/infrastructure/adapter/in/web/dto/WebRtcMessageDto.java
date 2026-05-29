package com.edulive.infrastructure.adapter.in.web.dto;

public class WebRtcMessageDto {
    private String type;
    private String roomId;
    private String senderId;
    private String targetId;
    private String payload;

    public WebRtcMessageDto() {
    }

    public WebRtcMessageDto(String type, String roomId, String senderId, String targetId, String payload) {
        this.type = type;
        this.roomId = roomId;
        this.senderId = senderId;
        this.targetId = targetId;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
