package com.edulive.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TokenRequestDto {

    @NotBlank(message = "El roomId no puede estar vacío")
    @Size(max = 100, message = "El roomId no puede superar 100 caracteres")
    private String roomId;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
    private String name;

    public TokenRequestDto() {}

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
