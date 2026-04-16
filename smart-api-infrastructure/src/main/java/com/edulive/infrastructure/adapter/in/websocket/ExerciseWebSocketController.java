package com.edulive.infrastructure.adapter.in.websocket;

import com.edulive.application.service.ExerciseService;
import com.edulive.domain.model.Exercise;
import com.edulive.infrastructure.adapter.in.web.dto.ExerciseRequestDto;
import com.edulive.infrastructure.adapter.in.web.dto.ExerciseResponseDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;

@Controller
public class ExerciseWebSocketController {

    private final ExerciseService exerciseService;

    public ExerciseWebSocketController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // Ruta de entrada de webSockets (prefijo /app está configurado por el MessageBroker) -> /app/analyze
    @MessageMapping("/analyze")
    @SendTo("/topic/feedback")
    public ExerciseResponseDto analyzeBoard(@Valid @Payload ExerciseRequestDto requestDto) {
        Exercise exercise = Exercise.builder()
                .subject(requestDto.getSubject())
                .base64Image(requestDto.getBase64Image())
                .build();

        Exercise result = exerciseService.processExercise(exercise);
        
        return ExerciseResponseDto.builder()
                .id(result.getId())
                .subject(result.getSubject())
                .aiFeedback(result.getAiFeedback())
                .build();
    }
}
