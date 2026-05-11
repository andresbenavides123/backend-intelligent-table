package com.edulive.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "rooms")
public class RoomDocument {

    @Id
    private String id;
    private String roomId;
    private LocalDateTime createdAt;
    private boolean active;

    public RoomDocument() {}

    public RoomDocument(String id, String roomId, LocalDateTime createdAt, boolean active) {
        this.id = id;
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
