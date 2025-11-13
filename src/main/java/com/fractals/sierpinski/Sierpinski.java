package com.fractals.sierpinski;

import com.fractals.sierpinski.controller.SierpinskiController;
import com.jpro.webapi.JProApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Author: Cristian Uță
 */
public class Sierpinski extends JProApplication {

    @Override
    public void start(Stage stage) {
        // load user interface as FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fractals/fxml/Sierpinski.fxml"));
        Scene scene = null;
        try {
            Parent root = loader.load();
            SierpinskiController controller = loader.getController();
            controller.init(this);

            // set camera
            boolean fixedEyeAtCameraZero = false;
            PerspectiveCamera camera = new PerspectiveCamera(fixedEyeAtCameraZero);
            camera.setTranslateX(150);
            camera.setTranslateY(-100);
            camera.setTranslateZ(250);

            root.setRotationAxis(Rotate.X_AXIS);
            root.setRotate(30);

            // create JavaFX scene
            scene = new Scene(root);
            scene.setCamera(camera);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setTitle("Fractals");
        stage.setScene(scene);

        // open JavaFX window
        stage.show();
    }

    /**
     * Application entry point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
