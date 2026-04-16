package com.edulive.application.service;

import com.edulive.domain.model.Exercise;
import com.edulive.domain.port.out.AIAnalyzerPort;
import com.edulive.domain.port.out.ExerciseRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExerciseServiceTest {

    private AIAnalyzerPort aiAnalyzerPort;
    private ExerciseRepositoryPort exerciseRepositoryPort;
    private ExerciseService exerciseService;

    @BeforeEach
    void setUp() {
        aiAnalyzerPort = mock(AIAnalyzerPort.class);
        exerciseRepositoryPort = mock(ExerciseRepositoryPort.class);
        exerciseService = new ExerciseService(aiAnalyzerPort, exerciseRepositoryPort);
    }

    @Test
    void processExercise_shouldAnalyzeAndSave() {
        // Arrange
        Exercise inputExercise = new Exercise();
        inputExercise.setSubject("Math");
        inputExercise.setBase64Image("base64");

        when(aiAnalyzerPort.analyze(any(Exercise.class))).thenReturn("Buen trabajo, 2+2=4");
        when(exerciseRepositoryPort.save(any(Exercise.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Exercise result = exerciseService.processExercise(inputExercise);

        // Assert
        assertEquals("Buen trabajo, 2+2=4", result.getAiFeedback());
        verify(exerciseRepositoryPort, times(1)).save(inputExercise);
    }
}
