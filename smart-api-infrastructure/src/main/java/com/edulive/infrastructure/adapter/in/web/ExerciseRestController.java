package com.edulive.infrastructure.adapter.in.web;

import com.edulive.application.service.ExerciseService;
import com.edulive.domain.model.Exercise;
import com.edulive.infrastructure.adapter.in.web.dto.ExerciseRequestDto;
import com.edulive.infrastructure.adapter.in.web.dto.ExerciseResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exercises")
@CrossOrigin(origins = "*") // Habilita CORS para pruebas fáciles en desarrollo
public class ExerciseRestController {

    private final ExerciseService exerciseService;

    public ExerciseRestController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ExerciseResponseDto> analyzeExercise(@Valid @RequestBody ExerciseRequestDto requestDto) {
        
        // Mappeo DTO a Domain
        Exercise exercise = Exercise.builder()
                .subject(requestDto.getSubject())
                .base64Image(requestDto.getBase64Image())
                .build();

        Exercise result = exerciseService.processExercise(exercise);
        
        // Mappeo Domain a DTO
        ExerciseResponseDto responseDto = ExerciseResponseDto.builder()
                .id(result.getId())
                .subject(result.getSubject())
                .aiFeedback(result.getAiFeedback())
                .build();

        return ResponseEntity.ok(responseDto);
    }
}
