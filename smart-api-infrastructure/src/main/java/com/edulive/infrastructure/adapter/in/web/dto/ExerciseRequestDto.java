package com.edulive.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExerciseRequestDto {
    @NotBlank(message = "El asunto o materia (subject) no puede estar vacío")
    private String subject;

    @NotBlank(message = "Debe enviar una imagen en formato base64 (base64Image)")
    private String base64Image;
}
