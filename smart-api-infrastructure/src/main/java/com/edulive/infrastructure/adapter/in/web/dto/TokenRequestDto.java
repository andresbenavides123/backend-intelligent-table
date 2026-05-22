package com.edulive.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TokenRequestDto {

    @NotBlank(message = "Room ID cannot be blank")
    @Size(max = 100, message = "Room ID cannot exceed 100 characters")
    private String roomId;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 60, message = "Name must be between 2 and 60 characters")
    private String name;

    public TokenRequestDto() {}

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
