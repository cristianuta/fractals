package com.fractals;

import com.jpro.webapi.JProApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Author: Cristian Uță
 */
public class FractalsMain extends JProApplication {

    @Override
    public void start(Stage stage) {
        // load user interface as FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fractals/fxml/FractalsPage.fxml"));
        Scene scene = null;
        try {
            Parent root = loader.load();
            ControllerMain controller = loader.getController();

            // Create scene with explicit dimensions
            scene = new Scene(root, 800, 600);

            // Initialize controller after scene is created
            controller.init(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setTitle("Fractals");
        stage.setScene(scene);

        // open JavaFX window
        stage.show();
    }
}

