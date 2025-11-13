package com.fractals.sierpinski.controller;

import com.jpro.webapi.JProApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: Cristian Uță
 */
public class SierpinskiFXMLController implements Initializable {

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
    private List<Shape> fullCarpet = new ArrayList<>();
    private List<Shape> fullTriangle = new ArrayList<>();
    private boolean carpetVisible = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init(JProApplication app) {
        this.app = app;

        // Remove the FXML-defined shapes since we're creating new ones dynamically
        if (carpet != null) root.getChildren().remove(carpet);
        if (triangle != null) root.getChildren().remove(triangle);

        drawCarpet(this.carpet);
        drawTriangle(this.triangle);

        if (carpetVisible) {
            root.getChildren().addAll(fullCarpet);
        } else {
            root.getChildren().addAll(fullTriangle);
        }
        for (Shape shape : fullCarpet) {
            shape.setMouseTransparent(true); // Allow mouse events to pass through
        }

        // Make triangle shapes mouse transparent as well
        for (Shape shape : fullTriangle) {
            shape.setMouseTransparent(true);
        }

        // Ensure button is on top and has proper styling
        button.toFront();
        button.setPickOnBounds(true); // Ensure button responds to clicks
        button.setMouseTransparent(false); // Button MUST receive mouse events
        button.setText(carpetVisible ? SHOW_TRIANGLE : SHOW_CARPET);
        button.setVisible(true);
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

    @FXML
    public void buttonClicked(ActionEvent actionEvent) {
        if (carpetVisible) {
            button.setText(SHOW_CARPET);
            root.getChildren().removeAll(fullCarpet);
            root.getChildren().addAll(fullTriangle);
            for (Shape shape : fullTriangle) {
                shape.setVisible(true);
            }
            root.getChildren().remove(button);
            root.getChildren().add(button); // Re-add button to keep it on top
            carpetVisible = false;
        } else {
            button.setText(SHOW_TRIANGLE);
            root.getChildren().removeAll(fullTriangle);
            root.getChildren().addAll(fullCarpet);
            for (Shape shape : fullCarpet) {
                shape.setVisible(true);
            }
            root.getChildren().remove(button);
            root.getChildren().add(button); // Re-add button to keep it on top
            carpetVisible = true;
        }
    }
}
