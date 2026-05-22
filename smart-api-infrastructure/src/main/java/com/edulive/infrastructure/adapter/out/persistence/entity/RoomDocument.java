package com.edulive.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "rooms")
public class RoomDocument {

    @Id
    private String id;
    private String roomId;
    private LocalDateTime createdAt;
    private boolean active;

    /**
     * Historial embebido de elementos de la pizarra.
     * Cada elemento es un trazo vectorial, texto flotante o imagen Base64.
     * Se limpia cuando el usuario presiona "Limpiar" en la pizarra.
     */
    private List<BoardElementDocument> boardElements = new ArrayList<>();

    public RoomDocument() {}

    public RoomDocument(String id, String roomId, LocalDateTime createdAt, boolean active) {
        this.id        = id;
        this.roomId    = roomId;
        this.createdAt = createdAt;
        this.active    = active;
    }

    public String getId()                               { return id; }
    public void setId(String id)                        { this.id = id; }

    public String getRoomId()                           { return roomId; }
    public void setRoomId(String roomId)                { this.roomId = roomId; }

    public LocalDateTime getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }

    public boolean isActive()                           { return active; }
    public void setActive(boolean active)               { this.active = active; }

    public List<BoardElementDocument> getBoardElements()                              { return boardElements; }
    public void setBoardElements(List<BoardElementDocument> boardElements)            { this.boardElements = boardElements; }
}

