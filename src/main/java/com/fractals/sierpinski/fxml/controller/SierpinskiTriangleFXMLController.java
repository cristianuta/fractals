package com.fractals.sierpinski.fxml.controller;

import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Cristian Uță
 */
public class SierpinskiTriangleFXMLController implements Initializable {

    @FXML
    public Label platform;

    @FXML
    protected StackPane root;

    @FXML
    protected Polygon triangle;

    protected JProApplication app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        platform.setText(String.format("Platform: %s", WebAPI.isBrowser() ? "Browser" : "Desktop"));
    }

    public void init(JProApplication app) {
        this.app = app;
        drawTriangle(this.triangle);
    }

    public void drawTriangle(Polygon triangle) {
        int iterations = 4;
        drawTriangle(iterations, Double.valueOf(250.0), Double.valueOf(root.getWidth()/2), Double.valueOf(root.getHeight() / 2));
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

        drawTriangle(iteration, Double.valueOf(length/2), Double.valueOf(X- length/2), Double.valueOf(Y+ length/2));
        drawTriangle(iteration, Double.valueOf(length/2), Double.valueOf(X+ length/2), Double.valueOf(Y+ length/2));
        drawTriangle(iteration, Double.valueOf(length/2), Double.valueOf(X), Double.valueOf(Y- length/2));
    }
}

