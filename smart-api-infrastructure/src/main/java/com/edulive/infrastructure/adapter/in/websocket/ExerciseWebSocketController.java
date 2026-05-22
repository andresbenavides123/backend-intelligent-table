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

    // WebSocket entry path (MessageBroker configures the /app prefix) -> /app/analyze
    @MessageMapping("/analyze")
    @SendTo("/topic/feedback")
    public ExerciseResponseDto analyzeBoard(@Valid @Payload ExerciseRequestDto requestDto) {
        Exercise exercise = new Exercise(requestDto.getSubject(), requestDto.getBase64Image());

        Exercise result = exerciseService.processExercise(exercise);
        
        return new ExerciseResponseDto(
                result.getId(),
                result.getSubject(),
                result.getAiFeedback()
        );
    }
}
