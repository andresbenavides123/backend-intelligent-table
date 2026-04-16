package com.edulive.domain.port.out;

import com.edulive.domain.model.Exercise;

public interface ExerciseRepositoryPort {
    Exercise save(Exercise exercise);
}
