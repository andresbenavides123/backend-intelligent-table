package com.edulive.infrastructure.adapter.out.persistence.entity;

/**
 * POJO embebido (sin @Document propio) que representa un elemento de la pizarra
 * almacenado dentro del documento de sala en la colección "rooms".
 *
 * El campo {@code content} almacena:
 *   - Tipo "path":  JSON serializado del CanvasPath (trazos vectoriales de react-sketch-canvas)
 *   - Tipo "text":  Texto plano del elemento flotante
 *   - Tipo "image": Datos de imagen en formato Base64 (data-URL incluido)
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
