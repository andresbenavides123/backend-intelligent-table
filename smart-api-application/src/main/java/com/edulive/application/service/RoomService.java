package com.edulive.application.service;

import com.edulive.domain.model.Room;
import com.edulive.domain.port.out.RoomRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;

public class RoomService {

    private final RoomRepositoryPort roomRepositoryPort;

    public RoomService(RoomRepositoryPort roomRepositoryPort) {
        this.roomRepositoryPort = roomRepositoryPort;
    }

    public Room generateRoom() {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, LocalDateTime.now(), true);
        return roomRepositoryPort.save(room);
    }
}
// Optimización interna del servicio