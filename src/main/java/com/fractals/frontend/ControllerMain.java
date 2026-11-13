package com.fractals.frontend;

import com.jpro.webapi.WebAPI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Controller of the main page: the menu opens one fractal screen at a time.
 * Every browser session gets its own controller and views (no shared UI state).
 */
public class ControllerMain {

    /** The screens reachable from the menu. */
    public enum Screen {
        HOME("Fractals", null),
        LINDENMAYER("Lindenmayer system", LindenmayerView::new),
        MANDELBROT("Mandelbrot & Julia", MandelbrotJuliaView::new),
        CUBE("Sierpinski cube", () -> new Solid3DView(Solid3DView.Kind.CUBE)),
        CARPET("Sierpinski carpet", () -> new Sierpinski2DView(Sierpinski2DView.Kind.CARPET)),
        PYRAMID("Sierpinski pyramid", () -> new Solid3DView(Solid3DView.Kind.PYRAMID)),
        TRIANGLE("Sierpinski triangle", () -> new Sierpinski2DView(Sierpinski2DView.Kind.TRIANGLE));

        final String title;
        final Supplier<FractalView> factory;

        Screen(String title, Supplier<FractalView> factory) {
            this.title = title;
            this.factory = factory;
        }

        /** Accepts names like "mandelbrot" (used for links such as index.html?fractal=mandelbrot). */
        public static Screen parse(String name) {
            if (name != null) {
                for (Screen s : values()) {
                    if (s.name().equalsIgnoreCase(name.trim())) {
                        return s;
                    }
                }
            }
            return HOME;
        }
    }

    @FXML
    private StackPane content;

    @FXML
    private Label title;

    private final Map<Screen, FractalView> views = new EnumMap<>(Screen.class);
    private FractalView current;
    private Stage stage;

    /** Called by the application after the FXML is loaded. */
    public void init(Stage stage, Screen initial) {
        this.stage = stage;
        show(initial);
    }

    public void show(Screen screen) {
        if (current != null) {
            current.onHide();
        }
        title.setText(screen.title);
        if (screen == Screen.HOME) {
            current = null;
            content.getChildren().setAll(homePage());
            return;
        }
        current = views.computeIfAbsent(screen, s -> s.factory.get());
        content.getChildren().setAll(current.node());
        current.onShow();
    }

    private Node homePage() {
        Label heading = new Label("Explore fractals in your browser");
        heading.getStyleClass().add("home-title");
        Label sub = new Label("JavaFX front-end streamed by JPro · computations done by the Java back-end. "
                + "Choose a fractal from the menu or below.");
        sub.getStyleClass().add("home-subtitle");
        sub.setWrapText(true);

        FlowPane cards = new FlowPane(16, 16);
        cards.setAlignment(Pos.CENTER);
        cards.setMaxWidth(860);
        cards.getChildren().addAll(
                card(Screen.LINDENMAYER, "Koch snowflake, dragon and Hilbert curves, plants – rewriting rules drawn by a turtle."),
                card(Screen.MANDELBROT, "Zoom into the Mandelbrot set and click any point to see its Julia set."),
                card(Screen.CUBE, "The Menger sponge in 3D: rotate it and change the number of iterations."),
                card(Screen.CARPET, "Squares divided into 3×3 with the middle one removed, again and again."),
                card(Screen.PYRAMID, "The Sierpinski tetrahedron in 3D, built from 4ⁿ small pyramids."),
                card(Screen.TRIANGLE, "The classic Sierpinski triangle – self-similar at every scale."));

        VBox box = new VBox(18, heading, sub, cards);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(900);
        box.getStyleClass().add("home");
        StackPane page = new StackPane(box);
        page.getStyleClass().add("home-page");
        return page;
    }

    private Button card(Screen screen, String description) {
        Label name = new Label(screen.title);
        name.getStyleClass().add("card-title");
        Label text = new Label(description);
        text.getStyleClass().add("card-text");
        text.setWrapText(true);
        VBox box = new VBox(8, name, text);
        Button card = new Button();
        card.setGraphic(box);
        card.getStyleClass().add("card");
        card.setPrefSize(270, 120);
        card.setOnAction(e -> show(screen));
        return card;
    }

    /** Releases background work when the browser tab is closed. */
    public void dispose() {
        views.values().forEach(FractalView::dispose);
        views.clear();
    }

    @FXML
    private void showHome(ActionEvent e) {
        show(Screen.HOME);
    }

    @FXML
    private void showLindenmayer(ActionEvent e) {
        show(Screen.LINDENMAYER);
    }

    @FXML
    private void showMandelbrot(ActionEvent e) {
        show(Screen.MANDELBROT);
    }

    @FXML
    private void showCube(ActionEvent e) {
        show(Screen.CUBE);
    }

    @FXML
    private void showCarpet(ActionEvent e) {
        show(Screen.CARPET);
    }

    @FXML
    private void showPyramid(ActionEvent e) {
        show(Screen.PYRAMID);
    }

    @FXML
    private void showTriangle(ActionEvent e) {
        show(Screen.TRIANGLE);
    }

    @FXML
    private void showAbout(ActionEvent e) {
        // no Dialog: dialogs block the FX thread, which JPro does not allow
        Label text = new Label("Fractals – a JPro application.\n\n"
                + "Back-end (com.fractals.backend): pure Java computations – L-systems, escape-time "
                + "rendering of the Mandelbrot and Julia sets, Sierpinski geometry and a small 3D renderer.\n\n"
                + "Front-end (com.fractals.frontend): JavaFX screens, shown in the browser by JPro.");
        text.setWrapText(true);
        text.getStyleClass().add("about-text");
        Button back = new Button("Back");
        back.getStyleClass().add("accent");
        back.setOnAction(ev -> show(Screen.HOME));
        VBox box = new VBox(16, text, back);
        box.setMaxWidth(560);
        box.getStyleClass().add("about");
        if (current != null) {
            current.onHide();
            current = null;
        }
        title.setText("About");
        content.getChildren().setAll(new StackPane(box));
    }

    @FXML
    private void exit(ActionEvent e) {
        if (WebAPI.isBrowser()) {
            WebAPI.getWebAPI(stage).closeInstance();
        } else {
            dispose();
            Platform.exit();
        }
    }
}
