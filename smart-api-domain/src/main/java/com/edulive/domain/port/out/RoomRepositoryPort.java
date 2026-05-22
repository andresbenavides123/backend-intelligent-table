package com.edulive.domain.port.out;

import com.edulive.domain.model.BoardElement;
import com.edulive.domain.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomRepositoryPort {
    Room save(Room room);
    Optional<Room> findByRoomId(String roomId);

    /**
     * Añade un elemento al historial de la pizarra de la sala de forma atómica ($push).
     * Si la sala no existe, la operación no tiene efecto.
     */
    void addBoardElement(String roomId, BoardElement element);

    /**
     * Elimina todos los elementos del historial de la pizarra de la sala ($set vacío).
     */
    void clearBoardElements(String roomId);

    /**
     * Obtiene todos los elementos actuales del historial de la pizarra de una sala.
     * @return lista vacía si la sala no existe o no tiene historial.
     */
    List<BoardElement> getBoardElements(String roomId);
}
