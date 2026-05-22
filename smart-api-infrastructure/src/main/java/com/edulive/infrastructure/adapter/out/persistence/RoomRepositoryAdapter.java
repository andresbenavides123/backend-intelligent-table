package com.edulive.infrastructure.adapter.out.persistence;

import com.edulive.domain.model.BoardElement;
import com.edulive.domain.model.Room;
import com.edulive.domain.port.out.RoomRepositoryPort;
import com.edulive.infrastructure.adapter.out.persistence.entity.BoardElementDocument;
import com.edulive.infrastructure.adapter.out.persistence.entity.RoomDocument;
import com.edulive.infrastructure.adapter.out.persistence.repository.MongoRoomRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RoomRepositoryAdapter implements RoomRepositoryPort {

    private final MongoRoomRepository mongoRoomRepository;
    private final MongoTemplate mongoTemplate;

    public RoomRepositoryAdapter(MongoRoomRepository mongoRoomRepository,
                                 MongoTemplate mongoTemplate) {
        this.mongoRoomRepository = mongoRoomRepository;
        this.mongoTemplate       = mongoTemplate;
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

    /**
     * Añade un elemento al historial de la pizarra de forma atómica usando $push.
     * Se evita cargar el documento completo en memoria para mejor rendimiento.
     */
    @Override
    public void addBoardElement(String roomId, BoardElement element) {
        BoardElementDocument doc = toDocument(element);

        Query query = Query.query(Criteria.where("roomId").is(roomId));
        Update update = new Update().push("boardElements", doc);
        mongoTemplate.updateFirst(query, update, RoomDocument.class);
    }

    /**
     * Limpia el historial de la pizarra usando $set con lista vacía (operación atómica).
     */
    @Override
    public void clearBoardElements(String roomId) {
        Query query = Query.query(Criteria.where("roomId").is(roomId));
        Update update = new Update().set("boardElements", new ArrayList<>());
        mongoTemplate.updateFirst(query, update, RoomDocument.class);
    }

    /**
     * Obtiene todos los elementos del historial de la pizarra de una sala.
     * Retorna lista vacía si la sala no existe o no tiene historial.
     */
    @Override
    public List<BoardElement> getBoardElements(String roomId) {
        return mongoRoomRepository.findByRoomId(roomId)
                .map(doc -> {
                    List<BoardElementDocument> elements = doc.getBoardElements();
                    if (elements == null) return new ArrayList<BoardElement>();
                    return elements.stream()
                            .map(this::toDomain)
                            .toList();
                })
                .orElse(new ArrayList<>());
    }

    // ── Mapeo entre dominio e infraestructura ─────────────────────────────────

    private BoardElementDocument toDocument(BoardElement e) {
        return new BoardElementDocument(
                e.getId(), e.getType(), e.getX(), e.getY(),
                e.getContent(), e.getColor(), e.getSize()
        );
    }

    private BoardElement toDomain(BoardElementDocument d) {
        return new BoardElement(
                d.getId(), d.getType(), d.getX(), d.getY(),
                d.getContent(), d.getColor(), d.getSize()
        );
    }
}
