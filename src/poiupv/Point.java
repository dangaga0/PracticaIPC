/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poiupv;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 *
 * @author damia
 */
public class Point extends Point2D{
    private double size;
    private Color color;

    public Point(double x, double y, Color c, double s) {
        super(x, y);
        color = c;
        size = s;
    }
    public Point(double x, double y) {
        super(x, y);
        color = Color.RED;
        size = 1;
    }
    
    public void setSize(double size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }
}
