package com.edulive.domain.model;

import java.time.LocalDateTime;

public class Room {
    private String id;
    private String roomId;
    private LocalDateTime createdAt;
    private boolean active;

    public Room() {}

    public Room(String roomId, LocalDateTime createdAt, boolean active) {
        this.roomId = roomId;
        this.createdAt = createdAt;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
