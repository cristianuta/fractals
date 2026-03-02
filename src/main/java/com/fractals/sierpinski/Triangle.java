package com.fractals.sierpinski;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class Triangle {

    @FXML
    protected Polygon triangle;

    private static Triangle instance;

    private List<Shape> fullTriangle = new ArrayList<>();

    private Triangle() {
    }

    public static synchronized Triangle getInstance() {
        if (instance == null) {
            instance = new Triangle();
        }
        return instance;
    }

    public Polygon getTriangle() {
        return triangle;
    }

    public void setTriangle(Polygon triangle) {
        this.triangle = triangle;
    }

    public List<Shape> getFullTriangle() {
        return fullTriangle;
    }

    public void setFullTriangle(List<Shape> fullTriangle) {
        this.fullTriangle = fullTriangle;
    }

    public void drawTriangle(Polygon triangle) {
        int iterations = 5;
        drawTriangle(iterations, 250.0, 0, 0);
    }

    private void drawTriangle(int iteration, double length, double X, double Y) {
        if (iteration == 0) {
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(
                    X - length, Y + length,
                    X + length, Y + length,
                    X, Y - length
            );
            triangle.setTranslateX(X);
            triangle.setTranslateY(Y);
            triangle.setStroke(Color.BLACK);
            triangle.setFill(Color.ORANGE);
            triangle.setMouseTransparent(true); // Allow mouse events to pass through
            fullTriangle.add(triangle);
            return;
        }
        iteration--;

        drawTriangle(iteration, length / 2, X - length / 2, Y + length / 2);
        drawTriangle(iteration, length / 2, X + length / 2, Y + length / 2);
        drawTriangle(iteration, length / 2, X, Y - length / 2);
    }
}
