package com.edulive.infrastructure.adapter.out.persistence;

import com.edulive.domain.model.Room;
import com.edulive.domain.port.out.RoomRepositoryPort;
import com.edulive.infrastructure.adapter.out.persistence.entity.RoomDocument;
import com.edulive.infrastructure.adapter.out.persistence.repository.MongoRoomRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoomRepositoryAdapter implements RoomRepositoryPort {

    private final MongoRoomRepository mongoRoomRepository;

    public RoomRepositoryAdapter(MongoRoomRepository mongoRoomRepository) {
        this.mongoRoomRepository = mongoRoomRepository;
    }

    @Override
    public Room save(Room room) {
        RoomDocument document = new RoomDocument(
                room.getId(),
                room.getRoomId(),
                room.getCreatedAt(),
                room.isActive()
        );
        RoomDocument savedDocument = mongoRoomRepository.save(document);
        room.setId(savedDocument.getId());
        return room;
    }

    @Override
    public Optional<Room> findByRoomId(String roomId) {
        return mongoRoomRepository.findByRoomId(roomId)
                .map(doc -> {
                    Room room = new Room(doc.getRoomId(), doc.getCreatedAt(), doc.isActive());
                    room.setId(doc.getId());
                    return room;
                });
    }
}
