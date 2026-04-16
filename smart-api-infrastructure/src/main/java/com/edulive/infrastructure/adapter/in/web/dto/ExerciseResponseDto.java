package com.edulive.infrastructure.adapter.in.web.dto;

public class ExerciseResponseDto {
    private String id;
    private String subject;
    private String aiFeedback;

    public ExerciseResponseDto(String id, String subject, String aiFeedback) {
        this.id = id;
        this.subject = subject;
        this.aiFeedback = aiFeedback;
    }

    public String getId() { return id; }
    public String getSubject() { return subject; }
    public String getAiFeedback() { return aiFeedback; }
}
