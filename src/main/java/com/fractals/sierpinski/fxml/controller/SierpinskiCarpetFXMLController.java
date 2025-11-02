package com.fractals.sierpinski.fxml.controller;

import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Cristian Uță
 */
public class SierpinskiCarpetFXMLController implements Initializable {

    @FXML
    public Label platformLabel;

    @FXML
    public Box box;

    @FXML
    public Node boxGroup;

    @FXML
    protected StackPane root;

    @FXML
    protected Rectangle carpet;

    protected JProApplication jproApplication;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        platformLabel.setText(String.format("Platform: %s", WebAPI.isBrowser() ? "Browser" : "Desktop"));
    }

    public void init(JProApplication jproApplication) {
        this.jproApplication = jproApplication;
        initCarpet(this.carpet);
    }

    public void initCarpet(Rectangle carpet) {
        carpet.setX(this.root.getLayoutX()/2);
        carpet.setY(this.root.getLayoutY()/2);
        carpet.setWidth(500);
        carpet.setHeight(500);
        carpet.setStroke(Color.BLACK);
        carpet.setFill(Color.ORANGE);
        carpet.setVisible(true);
    }
}
