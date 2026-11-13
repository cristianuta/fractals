package com.fractals;

import com.fractals.frontend.ControllerMain;
import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Entry point. With JPro every browser tab calls {@link #start(Stage)} on its own instance;
 * it also runs as a normal desktop JavaFX application.
 * <p>
 * Author: Cristian Uță
 */
public class FractalsMain extends JProApplication {

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fractals/fxml/FractalsPage.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load the main page", e);
        }
        ControllerMain controller = loader.getController();

        stage.setTitle("Fractals");
        stage.setScene(new Scene(root, 1280, 800));

        // Deep links: index.html?fractal=mandelbrot opens a screen directly
        String requested = null;
        if (WebAPI.isBrowser()) {
            WebAPI webAPI = getWebAPI();
            requested = webAPI.getURLQueryParams().get("fractal");
            webAPI.addInstanceCloseListener(controller::dispose);
        } else {
            requested = getParameters().getNamed().get("fractal");
            stage.setOnHidden(e -> controller.dispose());
        }
        controller.init(stage, ControllerMain.Screen.parse(requested));
        stage.show();
    }
}
