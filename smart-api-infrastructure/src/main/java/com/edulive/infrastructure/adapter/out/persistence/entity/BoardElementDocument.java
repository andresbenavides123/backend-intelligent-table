package com.edulive.infrastructure.adapter.out.persistence.entity;

/**
 * Embedded POJO (no @Document of its own) representing a whiteboard element
 * stored inside the room document in the "rooms" collection.
 *
 * The {@code content} field stores:
 *   - Type "path":  Serialized JSON of the CanvasPath (vector strokes from react-sketch-canvas)
 *   - Type "text":  Plain text content of the floating element
 *   - Type "image": Base64 image data (data-URL included)
 */
public class BoardElementDocument {

    private String id;
    /** "path" | "text" | "image" */
    private String type;
    private double x;
    private double y;
    private String content;
    private String color;
    private int size;

    public BoardElementDocument() {}

    public BoardElementDocument(String id, String type, double x, double y,
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
