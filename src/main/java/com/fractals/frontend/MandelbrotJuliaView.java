package com.fractals.frontend;

import com.fractals.backend.mandelbrot.EscapeTimeFractal;
import com.fractals.backend.mandelbrot.FractalRenderer;
import com.fractals.backend.mandelbrot.Mandelbrot;
import com.fractals.backend.mandelbrot.Palette;
import com.fractals.backend.mandelbrot.Viewport;
import com.fractals.backend.mandelbrot.julia.Julia;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Mandelbrot set (left) and the Julia set of the chosen point c (right).
 * Scroll = zoom, drag = move, click on the Mandelbrot set = choose c.
 */
final class MandelbrotJuliaView implements FractalView {

    /** Pixels rendered by the back-end for one of the two panes. */
    private record Frame(int[] pixels, int width, int height) {
    }

    private static final Mandelbrot MANDELBROT = new Mandelbrot();

    private final BorderPane root = new BorderPane();
    private final ImagePane mandelPane = new ImagePane();
    private final ImagePane juliaPane = new ImagePane();
    private final Circle marker = new Circle(5);
    private final Label mandelInfo = new Label();
    private final Label juliaInfo = new Label();
    private final TextField cRe = new TextField();
    private final TextField cIm = new TextField();
    private final ComboBox<Palette> palette = new ComboBox<>();
    private final ComboBox<Julia.Preset> juliaPresets = new ComboBox<>();
    private final CheckBox liveJulia = new CheckBox("Update Julia while hovering");
    private final CheckBox showJulia = new CheckBox("Show the Julia window");
    private final AsyncRender<Frame> mandelRenderer;
    private final AsyncRender<Frame> juliaRenderer;

    /** Fixed size of the Julia window, as fractions of the Mandelbrot area. */
    private static final double JULIA_WIDTH = 0.375;
    private static final double JULIA_HEIGHT = 0.5;

    private Viewport mandelView = Mandelbrot.DEFAULT_VIEW;
    private Viewport juliaView = Julia.DEFAULT_VIEW;
    private Julia julia = new Julia(Julia.PRESETS.getFirst().cr(), Julia.PRESETS.getFirst().ci());
    private int maxIterations = 300;
    private double density = 0.35;

    MandelbrotJuliaView() {
        mandelRenderer = new AsyncRender<>(f -> mandelPane.show(f.pixels, f.width, f.height), Throwable::printStackTrace);
        juliaRenderer = new AsyncRender<>(f -> juliaPane.show(f.pixels, f.width, f.height), Throwable::printStackTrace);

        marker.setFill(Color.TRANSPARENT);
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(2);
        marker.setManaged(false);
        marker.setMouseTransparent(true);
        mandelPane.getChildren().add(marker);

        installNavigation(mandelPane, () -> mandelView, v -> {
            mandelView = v;
            renderMandelbrot();
        });
        installNavigation(juliaPane, () -> juliaView, v -> {
            juliaView = v;
            renderJulia();
        });
        mandelPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
                pickC(e.getX(), e.getY());
            }
        });
        mandelPane.setOnMouseMoved(e -> {
            if (liveJulia.isSelected()) {
                pickC(e.getX(), e.getY());
            }
        });
        mandelPane.onResize(this::renderMandelbrot);
        juliaPane.onResize(this::renderJulia);

        // Mandelbrot fills the area; the Julia set floats over its top-right corner
        StackPane juliaWindow = titled(juliaPane, "Julia", juliaInfo);
        juliaWindow.getStyleClass().add("julia-window");
        StackPane.setAlignment(juliaWindow, Pos.TOP_RIGHT);
        StackPane.setMargin(juliaWindow, new Insets(12));
        StackPane images = new StackPane(titled(mandelPane, "Mandelbrot", mandelInfo), juliaWindow);
        images.setMinSize(0, 0);
        Runnable layoutJulia = () -> {
            // fixed rectangle in the top-right corner: 37.5 % of the width, half of the height
            double height = Math.max(120, images.getHeight() * JULIA_HEIGHT - 12);
            double width = Math.max(160, images.getWidth() * JULIA_WIDTH);
            juliaWindow.setPrefSize(width, height);
            juliaWindow.setMaxSize(width, height);
        };
        images.widthProperty().addListener((o, a, b) -> layoutJulia.run());
        images.heightProperty().addListener((o, a, b) -> layoutJulia.run());
        showJulia.setSelected(true);
        juliaWindow.visibleProperty().bind(showJulia.selectedProperty());
        showJulia.setOnAction(e -> {
            if (showJulia.isSelected()) {
                renderJulia();
            }
        });
        root.setCenter(images);

        palette.getItems().setAll(Palette.values());
        palette.setValue(Palette.CLASSIC);
        palette.setOnAction(e -> renderBoth());
        juliaPresets.getItems().setAll(Julia.PRESETS);
        juliaPresets.setPromptText("Famous values of c");
        juliaPresets.setOnAction(e -> {
            Julia.Preset p = juliaPresets.getValue();
            if (p != null) {
                setC(p.cr(), p.ci());
            }
        });
        cRe.setOnAction(e -> applyTypedC());
        cIm.setOnAction(e -> applyTypedC());
        Button applyC = new Button("Apply c");
        applyC.setOnAction(e -> applyTypedC());
        HBox cBox = new HBox(6, cRe, new Label("+"), cIm, new Label("i"));
        cBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cRe, Priority.ALWAYS);
        HBox.setHgrow(cIm, Priority.ALWAYS);

        Button reset = new Button("Reset views");
        reset.setMaxWidth(Double.MAX_VALUE);
        reset.setOnAction(e -> {
            mandelView = Mandelbrot.DEFAULT_VIEW;
            juliaView = Julia.DEFAULT_VIEW;
            renderBoth();
        });

        root.setRight(Ui.controlPanel(
                Ui.section("Mandelbrot & Julia"),
                Ui.slider("Max iterations", 50, 3000, maxIterations, v -> String.valueOf((int) v), v -> {
                    maxIterations = (int) v;
                    renderBoth();
                }),
                Ui.field("Palette", palette),
                Ui.slider("Colour density", 0.05, 1.5, density, v -> String.format(Locale.ROOT, "%.2f", v), v -> {
                    density = v;
                    renderBoth();
                }),
                Ui.section("Julia parameter c"),
                Ui.field("Preset", juliaPresets),
                Ui.field("c = re + im·i", cBox),
                applyC,
                liveJulia,
                showJulia,
                reset,
                Ui.hint("Scroll to zoom, drag to move – in both sets. Click a point of the Mandelbrot set to see "
                        + "its Julia set in the top-right window: points inside the black area give connected "
                        + "Julia sets, points outside give \"dust\".")));
        updateCFields();
    }

    private static StackPane titled(ImagePane pane, String title, Label info) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("overlay-title");
        info.getStyleClass().add("overlay-info");
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
        StackPane.setAlignment(info, Pos.BOTTOM_LEFT);
        titleLabel.setMouseTransparent(true);
        info.setMouseTransparent(true);
        StackPane stack = new StackPane(pane, titleLabel, info);
        stack.setMinSize(0, 0);
        return stack;
    }

    /** Scroll to zoom around the cursor, drag to pan. */
    private static void installNavigation(ImagePane pane, Supplier<Viewport> view, Consumer<Viewport> update) {
        double[] last = new double[2];
        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0) {
                return;
            }
            double factor = e.getDeltaY() > 0 ? 1.25 : 0.8;
            update.accept(view.get().zoom(factor, e.getX(), e.getY(), pane.pixelWidth(), pane.pixelHeight()));
        });
        pane.setOnMousePressed(e -> {
            last[0] = e.getX();
            last[1] = e.getY();
        });
        pane.setOnMouseDragged(e -> {
            double dx = e.getX() - last[0], dy = e.getY() - last[1];
            last[0] = e.getX();
            last[1] = e.getY();
            update.accept(view.get().pan(dx, dy, pane.pixelWidth(), pane.pixelHeight()));
        });
    }

    private void pickC(double px, double py) {
        int w = mandelPane.pixelWidth(), h = mandelPane.pixelHeight();
        setC(mandelView.toX(px, w, h), mandelView.toY(py, w, h));
    }

    private void applyTypedC() {
        try {
            setC(Double.parseDouble(cRe.getText().trim()), Double.parseDouble(cIm.getText().trim()));
        } catch (NumberFormatException ex) {
            updateCFields();
        }
    }

    private void setC(double re, double im) {
        julia = new Julia(re, im);
        updateCFields();
        updateMarker();
        renderJulia();
    }

    private void updateCFields() {
        cRe.setText(String.format(Locale.ROOT, "%.6f", julia.cr()));
        cIm.setText(String.format(Locale.ROOT, "%.6f", julia.ci()));
    }

    private void updateMarker() {
        int w = mandelPane.pixelWidth(), h = mandelPane.pixelHeight();
        double x = mandelView.toPixelX(julia.cr(), w, h);
        double y = mandelView.toPixelY(julia.ci(), w, h);
        marker.setCenterX(x);
        marker.setCenterY(y);
        marker.setVisible(x >= 0 && y >= 0 && x <= w && y <= h);
    }

    private void renderBoth() {
        renderMandelbrot();
        renderJulia();
    }

    private void renderMandelbrot() {
        updateMarker();
        mandelInfo.setText(describe(mandelView));
        render(mandelPane, mandelRenderer, MANDELBROT, mandelView);
    }

    private void renderJulia() {
        juliaInfo.setText(String.format(Locale.ROOT, "c = %.4f %+.4fi · %s", julia.cr(), julia.ci(), describe(juliaView)));
        if (showJulia.isSelected()) {
            render(juliaPane, juliaRenderer, julia, juliaView);
        }
    }

    private static String describe(Viewport v) {
        return String.format(Locale.ROOT, "zoom ×%.3g", 3.0 / v.span());
    }

    private void render(ImagePane pane, AsyncRender<Frame> renderer, EscapeTimeFractal fractal, Viewport view) {
        int w = pane.pixelWidth(), h = pane.pixelHeight();
        if (w < 2 || h < 2) {
            return;
        }
        int iterations = maxIterations;
        Palette p = palette.getValue();
        double d = density;
        renderer.request(cancelled -> {
            int[] pixels = FractalRenderer.render(fractal, view, w, h, iterations, p, d, cancelled);
            return pixels == null ? null : new Frame(pixels, w, h);
        });
    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    public void dispose() {
        mandelRenderer.dispose();
        juliaRenderer.dispose();
    }
}
