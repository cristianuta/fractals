package com.fractals.sierpinski;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class Carpet {

    private static Carpet instance;
    @FXML
    protected Rectangle carpet;

    ;
    private List<Shape> fullCarpet = new ArrayList<>();

    private Carpet() {
    }

    public static synchronized Carpet getInstance() {
        if (instance == null) {
            instance = new Carpet();
        }
        return instance;
    }

    public Rectangle getCarpet() {
        return carpet;
    }

    public void setCarpet(Rectangle carpet) {
        this.carpet = carpet;
    }

    public List<Shape> getFullCarpet() {
        return fullCarpet;
    }

    public void setFullCarpet(List<Shape> fullCarpet) {
        this.fullCarpet = fullCarpet;
    }

    public void drawCarpet(Rectangle carpet) {
        int iterations = 4;
        drawSquare(iterations, 200, 0, 0);
    }

    private void drawSquare(int iteration, double length, double X, double Y) {
        if (iteration == 0) {
            Rectangle carpet = new Rectangle();
            carpet.setTranslateX(X);
            carpet.setTranslateY(Y);
            carpet.setWidth(length * 3);
            carpet.setHeight(length * 3);
            carpet.setStroke(Color.BLACK);
            carpet.setFill(Color.ORANGE);
            carpet.setMouseTransparent(true); // Allow mouse events to pass through
            fullCarpet.add(carpet);
            return;
        }
        iteration--;

        drawSquare(iteration, length / 3, X - length, Y - length);
        drawSquare(iteration, length / 3, X - length, Y);
        drawSquare(iteration, length / 3, X - length, Y + length);

        drawSquare(iteration, length / 3, X, Y - length);
        drawSquare(iteration, length / 3, X, Y + length);

        drawSquare(iteration, length / 3, X + length, Y - length);
        drawSquare(iteration, length / 3, X + length, Y);
        drawSquare(iteration, length / 3, X + length, Y + length);
    }
}
