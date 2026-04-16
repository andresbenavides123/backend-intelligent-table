package com.edulive.infrastructure.adapter.in.websocket;

import com.edulive.application.service.ExerciseService;
import com.edulive.domain.model.Exercise;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ExerciseWebSocketController {

    private final ExerciseService exerciseService;

    public ExerciseWebSocketController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // Ruta de entrada de webSockets (prefijo /app está configurado por el MessageBroker) -> /app/analyze
    @MessageMapping("/analyze")
    // Ruta de salida donde los clientes se suscriben -> /topic/feedback
    @SendTo("/topic/feedback")
    public Exercise analyzeBoard(Exercise exercise) {
        // Analiza el evento en tiempo real y retransmite la respuesta con feedback
        return exerciseService.processExercise(exercise);
    }
}
