package com.edulive.infrastructure.adapter.in.web;

import com.edulive.application.service.ExerciseService;
import com.edulive.domain.model.Exercise;
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
    public ResponseEntity<Exercise> analyzeExercise(@RequestBody Exercise exercise) {
        // Enviar el ejercicio con la imagen al servicio de IA y recibir correcciones
        Exercise result = exerciseService.processExercise(exercise);
        return ResponseEntity.ok(result);
    }
}
