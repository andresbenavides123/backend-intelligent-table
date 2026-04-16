package com.edulive.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseResponseDto {
    private String id;
    private String subject;
    private String aiFeedback;
    // Omitimos enviar el base64 de vuelta porque es inmenso y el Frontend ya lo tiene
}
