package com.edulive.domain.model;

/**
 * Domain entity representing a persisted element on a room's whiteboard.
 * An element can be a vector stroke (path), a floating text label (text),
 * or a pasted image in Base64 format (image).
 */
public class BoardElement {

    private String id;
    /** "path" | "text" | "image" */
    private String type;
    private double x;
    private double y;
    /** Serialized JSON of the CanvasPath for strokes; plain text for text; or Base64 for images */
    private String content;
    private String color;
    private int size;

    public BoardElement() {}

    public BoardElement(String id, String type, double x, double y,
                        String content, String color, int size) {
        this.id      = id;
        this.type    = type;
        this.x       = x;
        this.y       = y;
        this.content = content;
        this.color   = color;
        this.size    = size;
    }

    public String getId()              { return id; }
    public void setId(String id)       { this.id = id; }

    public String getType()            { return type; }
    public void setType(String type)   { this.type = type; }

    public double getX()               { return x; }
    public void setX(double x)         { this.x = x; }

    public double getY()               { return y; }
    public void setY(double y)         { this.y = y; }

    public String getContent()         { return content; }
    public void setContent(String c)   { this.content = c; }

    public String getColor()           { return color; }
    public void setColor(String color) { this.color = color; }

    public int getSize()               { return size; }
    public void setSize(int size)      { this.size = size; }
}
