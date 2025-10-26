package com.fractals.sierpinski;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Hello JPro application.
 *
 * @author Florian Kirmaier
 */
public class SierpinskiCarpet extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello JPro!");
        label.setFont(new Font(50));
        label.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(label));
        stage.show();
    }

    /*@Override
    public void start(Stage primaryStage) {

        Circle circle = new Circle();
        circle.setCenterX(100);
        circle.setCenterY(100);
        circle.setRadius(125);
        circle.setStroke(Color.valueOf("#ff00ff"));
        circle.setStrokeWidth(5);
        circle.setFill(Color.TRANSPARENT);

        Rectangle rectangle = new Rectangle();
        rectangle.setX(200);
        rectangle.setY(200);
        rectangle.setWidth(300);
        rectangle.setHeight(400);
        rectangle.setStroke(Color.TRANSPARENT);
        rectangle.setFill(Color.valueOf("#00ffff"));

        Pane pane = new Pane();
        pane.getChildren().add(circle);
        pane.getChildren().add(rectangle);

        Scene scene = new Scene(pane, 1024, 800, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("2D Example");

        primaryStage.show();
    }*/

    /**
     * Application entry point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
