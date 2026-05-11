package com.edulive.infrastructure.adapter.in.web.dto;

public class BoardElementDto {
    private String id;
    private String type; // "text", "image", "path"
    private double x;
    private double y;
    private String content; // text content or base64 image data
    private String color;
    private int size;

    public BoardElementDto() {}

    public BoardElementDto(String id, String type, double x, double y, String content, String color, int size) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.content = content;
        this.color = color;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
