package one.jpro.hellojpro;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Hello JPro application.
 *
 * @author Florian Kirmaier
 */
public class TextTest extends Application {

    @Override
    public void start(Stage stage) {

        Text text = new Text("This is a JavaFX text.");

        Scene scene = new Scene(new VBox(text), 300, 250);
        stage.setScene(scene);
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
