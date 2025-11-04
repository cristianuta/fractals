package com.fractals.sierpinski.fxml.controller;

import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Cristian Uță
 */
public class SierpinskiCarpetFXMLController implements Initializable {

    @FXML
    public Label platform;

    @FXML
    protected StackPane root;

    @FXML
    protected Rectangle carpet;

    protected JProApplication app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        platform.setText(String.format("Platform: %s", WebAPI.isBrowser() ? "Browser" : "Desktop"));
    }

    public void init(JProApplication app) {
        this.app = app;
        drawCarpet(this.carpet);
    }

    public void drawCarpet(Rectangle carpet) {
        int iterations = 3;
        drawSquare(iterations, 200, root.getWidth()/2, root.getHeight()/2);
    }

    private void drawSquare(int iteration, int length, double X, double Y) {
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

        drawSquare(iteration, length/3, X- (double) length, Y- (double) length);
        drawSquare(iteration, length/3, X- (double) length, Y);
        drawSquare(iteration, length/3, X- (double) length, Y+ (double) length);

        drawSquare(iteration, length/3, X, Y- (double) length);
        drawSquare(iteration, length/3, X, Y+ (double) length);

        drawSquare(iteration, length/3, X+ (double) length, Y- (double) length);
        drawSquare(iteration, length/3,X+ (double) length, Y);
        drawSquare(iteration, length/3, X+ (double) length, Y+ (double) length);
    }
}
/*

drawSquare(iteration, length/3, X- (double) length /3, Y- (double) length /3);
drawSquare(iteration, length/3, X- (double) length /3, Y);
drawSquare(iteration, length/3, X- (double) length /3, Y+ (double) length /3);

drawSquare(iteration, length/3, X- (double) length /3, Y);
drawSquare(iteration, length/3, X+ (double) length /3, Y);

drawSquare(iteration, length/3, X+ (double) length /3, Y- (double) length /3);
drawSquare(iteration, length/3,X+ (double) length /3, Y);
drawSquare(iteration, length/3, X+ (double) length /3, Y+ (double) length /3);*/
