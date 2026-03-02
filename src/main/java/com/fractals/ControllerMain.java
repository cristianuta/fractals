package com.fractals;

import com.fractals.sierpinski.Carpet;
import com.fractals.sierpinski.Triangle;
import com.jpro.webapi.JProApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMain implements Initializable {

    public static final String SHOW_CARPET = "Show Carpet";
    public static final String SHOW_TRIANGLE = "Show Triangle";

    @FXML
    public Button button;

    @FXML
    protected StackPane root;

    protected JProApplication app;
    private boolean carpetVisible = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init(JProApplication app) {
        this.app = app;

        // Remove the FXML-defined shapes since we're creating new ones dynamically
        if (Carpet.getInstance().getCarpet() != null) root.getChildren().remove(Carpet.getInstance().getCarpet());
        if (Triangle.getInstance().getTriangle() != null)
            root.getChildren().remove(Triangle.getInstance().getTriangle());

        Carpet.getInstance().drawCarpet(Carpet.getInstance().getCarpet());
        Triangle.getInstance().drawTriangle(Triangle.getInstance().getTriangle());

        if (carpetVisible) {
            root.getChildren().addAll(Carpet.getInstance().getFullCarpet());
        } else {
            root.getChildren().addAll(Triangle.getInstance().getFullTriangle());
        }
        for (Shape shape : Carpet.getInstance().getFullCarpet()) {
            shape.setMouseTransparent(true); // Allow mouse events to pass through
        }

        // Make triangle shapes mouse transparent as well
        for (Shape shape : Triangle.getInstance().getFullTriangle()) {
            shape.setMouseTransparent(true);
        }

        // Ensure button is on top and has proper styling
        button.toFront();
        button.setPickOnBounds(true); // Ensure button responds to clicks
        button.setMouseTransparent(false); // Button MUST receive mouse events
        button.setText(carpetVisible ? SHOW_TRIANGLE : SHOW_CARPET);
        button.setVisible(true);
    }

    @FXML
    public void buttonClicked(ActionEvent actionEvent) {
        if (carpetVisible) {
            button.setText(SHOW_CARPET);
            root.getChildren().removeAll(Carpet.getInstance().getFullCarpet());
            root.getChildren().addAll(Triangle.getInstance().getFullTriangle());
            for (Shape shape : Triangle.getInstance().getFullTriangle()) {
                shape.setVisible(true);
            }
            root.getChildren().remove(button);
            root.getChildren().add(button); // Re-add button to keep it on top
            carpetVisible = false;
        } else {
            button.setText(SHOW_TRIANGLE);
            root.getChildren().removeAll(Triangle.getInstance().getFullTriangle());
            root.getChildren().addAll(Carpet.getInstance().getFullCarpet());
            for (Shape shape : Carpet.getInstance().getFullCarpet()) {
                shape.setVisible(true);
            }
            root.getChildren().remove(button);
            root.getChildren().add(button); // Re-add button to keep it on top
            carpetVisible = true;
        }
    }
}
