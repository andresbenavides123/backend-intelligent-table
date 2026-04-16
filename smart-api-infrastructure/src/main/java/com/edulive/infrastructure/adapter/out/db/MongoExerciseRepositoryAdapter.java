package com.edulive.infrastructure.adapter.out.db;

import com.edulive.domain.model.Exercise;
import com.edulive.domain.port.out.ExerciseRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class MongoExerciseRepositoryAdapter implements ExerciseRepositoryPort {

    private final SpringDataMongoExerciseRepository repository;

    public MongoExerciseRepositoryAdapter(SpringDataMongoExerciseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Exercise save(Exercise exercise) {
        if (exercise.getId() == null) {
            exercise.setId(UUID.randomUUID().toString());
        }
        
        ExerciseDocument doc = new ExerciseDocument();
        doc.setId(exercise.getId());
        doc.setSubject(exercise.getSubject());
        doc.setBase64Image(exercise.getBase64Image());
        doc.setAiFeedback(exercise.getAiFeedback());
        
        repository.save(doc);
        return exercise;
    }
}
