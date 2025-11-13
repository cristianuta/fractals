package com.fractals.sierpinski.controller;

import com.jpro.webapi.JProApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Cristian Uță
 */
public class SierpinskiController implements Initializable {

    private static final boolean CARPET_ON = true;
    public static final String SHOW_CARPET = "Show Carpet";
    public static final String SHOW_TRIANGLE = "Show Triangle";

    @FXML
    public Button button;

    @FXML
    protected StackPane root;

    @FXML
    protected Rectangle carpet;

    @FXML
    protected Polygon triangle;

    protected JProApplication app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init(JProApplication app) {
        this.app = app;
        this.button.setText(CARPET_ON ? SHOW_TRIANGLE : SHOW_CARPET);
        this.button.setOnAction(e -> {
            if (CARPET_ON) {
                button.setText(SHOW_TRIANGLE);
                triangle.setVisible(false);
                drawCarpet(this.carpet);
            } else {
                button.setText(SHOW_CARPET);
                carpet.setVisible(false);
                drawTriangle(this.triangle);
            }
        });
        if (CARPET_ON) {
            drawCarpet(this.carpet);
        } else {
            drawTriangle(this.triangle);
        }
        this.button.setVisible(true);
    }

    public void drawCarpet(Rectangle carpet) {
        int iterations = 3;
        drawSquare(iterations, 200, root.getWidth() / 2, root.getHeight() / 2);
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
            root.getChildren().add(carpet);
            carpet.setVisible(true);
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

    public void drawTriangle(Polygon triangle) {
        int iterations = 4;
        drawTriangle(iterations, Double.valueOf(250.0), Double.valueOf(root.getWidth() / 2), Double.valueOf(root.getHeight() / 2));
    }

    private void drawTriangle(int iteration, Double length, Double X, Double Y) {
        if (iteration == 0) {
            Polygon triangle = new Polygon();
            Double[] points = {(Double) (X - length), (Double) (Y + length), (Double) (X + length), (Double) (Y + length), X, (Double) (Y - length)};
            triangle.getPoints().addAll(points);
            triangle.setTranslateX(X);
            triangle.setTranslateY(Y);
            triangle.setStroke(Color.BLACK);
            triangle.setFill(Color.ORANGE);
            root.getChildren().add(triangle);
            triangle.setVisible(true);
            return;
        }
        iteration--;

        drawTriangle(iteration, Double.valueOf(length / 2), Double.valueOf(X - length / 2), Double.valueOf(Y + length / 2));
        drawTriangle(iteration, Double.valueOf(length / 2), Double.valueOf(X + length / 2), Double.valueOf(Y + length / 2));
        drawTriangle(iteration, Double.valueOf(length / 2), Double.valueOf(X), Double.valueOf(Y - length / 2));
    }
}

