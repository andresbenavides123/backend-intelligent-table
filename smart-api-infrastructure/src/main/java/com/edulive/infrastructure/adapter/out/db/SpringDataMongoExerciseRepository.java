package com.edulive.infrastructure.adapter.out.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataMongoExerciseRepository extends MongoRepository<ExerciseDocument, String> {
}
