package com.edulive.domain.model;

public class Exercise {
    private String id;
    private String subject;
    private String base64Image;
    private String aiFeedback;

    public Exercise() {}

    public Exercise(String subject, String base64Image) {
        this.subject = subject;
        this.base64Image = base64Image;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }

    public String getAiFeedback() { return aiFeedback; }
    public void setAiFeedback(String aiFeedback) { this.aiFeedback = aiFeedback; }
}