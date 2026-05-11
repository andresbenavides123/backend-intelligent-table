package com.edulive.domain.port.out;

import com.edulive.domain.model.Room;
import java.util.Optional;

public interface RoomRepositoryPort {
    Room save(Room room);
    Optional<Room> findByRoomId(String roomId);
}
