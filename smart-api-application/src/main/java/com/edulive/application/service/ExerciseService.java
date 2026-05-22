package com.edulive.application.service;

import com.edulive.domain.model.Exercise;
import com.edulive.domain.port.out.AIAnalyzerPort;
import com.edulive.domain.port.out.ExerciseRepositoryPort;

// Pure Java dependency injection — no Spring annotations by design (explicit config in AppConfig).
public class ExerciseService {

    private final AIAnalyzerPort aiAnalyzerPort;
    private final ExerciseRepositoryPort exerciseRepositoryPort;

    public ExerciseService(AIAnalyzerPort aiAnalyzerPort, ExerciseRepositoryPort exerciseRepositoryPort) {
        this.aiAnalyzerPort = aiAnalyzerPort;
        this.exerciseRepositoryPort = exerciseRepositoryPort;
    }

    public Exercise processExercise(Exercise exercise) {
        String feedback = aiAnalyzerPort.analyze(exercise);
        exercise.setAiFeedback(feedback);
        return exerciseRepositoryPort.save(exercise);
    }
}