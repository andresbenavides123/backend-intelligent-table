package com.edulive.infrastructure.adapter.out.persistence.repository;

import com.edulive.infrastructure.adapter.out.persistence.entity.RoomDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoRoomRepository extends MongoRepository<RoomDocument, String> {
    Optional<RoomDocument> findByRoomId(String roomId);
}
