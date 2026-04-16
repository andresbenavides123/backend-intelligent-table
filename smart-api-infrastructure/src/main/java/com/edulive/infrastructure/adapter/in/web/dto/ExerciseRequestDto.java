package com.edulive.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public class ExerciseRequestDto {

    @NotBlank(message = "El asunto o materia (subject) no puede estar vacío")
    private String subject;

    @NotBlank(message = "Debe enviar una imagen en formato base64 (base64Image)")
    private String base64Image;

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }
}
