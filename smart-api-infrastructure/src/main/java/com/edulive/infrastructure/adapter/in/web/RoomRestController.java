package com.edulive.infrastructure.adapter.in.web;

import com.edulive.application.service.RoomService;
import com.edulive.domain.model.Room;
import com.edulive.infrastructure.adapter.in.web.dto.RoomResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomRestController {

    private final RoomService roomService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public RoomRestController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/generate")
    public ResponseEntity<RoomResponseDto> generateRoom() {
        Room room = roomService.generateRoom();
        
        String joinUrl = frontendUrl + "/?room=" + room.getRoomId();
        
        RoomResponseDto responseDto = new RoomResponseDto(
                room.getRoomId(),
                joinUrl,
                room.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
