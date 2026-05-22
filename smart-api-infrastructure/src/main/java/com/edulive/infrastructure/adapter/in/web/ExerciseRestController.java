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
public class ExerciseRestController {

    private final ExerciseService exerciseService;

    public ExerciseRestController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ExerciseResponseDto> analyzeExercise(@Valid @RequestBody ExerciseRequestDto requestDto) {
        
        // Map DTO to domain model
        Exercise exercise = new Exercise(requestDto.getSubject(), requestDto.getBase64Image());

        Exercise result = exerciseService.processExercise(exercise);

        // Map domain to DTO
        ExerciseResponseDto responseDto = new ExerciseResponseDto(
                result.getId(),
                result.getSubject(),
                result.getAiFeedback()
        );

        return ResponseEntity.ok(responseDto);
    }
}
