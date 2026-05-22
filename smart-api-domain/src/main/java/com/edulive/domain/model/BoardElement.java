package com.edulive.domain.model;

/**
 * Entidad de dominio que representa un elemento persistido en la pizarra de una sala.
 * Un elemento puede ser un trazo vectorial (path), un texto flotante (text) o
 * una imagen pegada en formato Base64 (image).
 */
public class BoardElement {

    private String id;
    /** "path" | "text" | "image" */
    private String type;
    private double x;
    private double y;
    /** JSON serializado del CanvasPath para trazos; texto plano; o Base64 para imágenes */
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
