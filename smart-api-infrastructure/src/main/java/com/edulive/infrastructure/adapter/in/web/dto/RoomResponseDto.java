package com.edulive.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public class RoomResponseDto {
    private String roomId;
    private String joinUrl;
    private LocalDateTime createdAt;

    public RoomResponseDto() {}

    public RoomResponseDto(String roomId, String joinUrl, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.joinUrl = joinUrl;
        this.createdAt = createdAt;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getJoinUrl() { return joinUrl; }
    public void setJoinUrl(String joinUrl) { this.joinUrl = joinUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
