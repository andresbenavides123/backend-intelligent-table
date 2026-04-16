package com.edulive.infrastructure.adapter.out.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "exercises")
public class ExerciseDocument {
    @Id
    private String id;
    private String subject;
    private String base64Image;
    private String aiFeedback;
}
