package com.fractals.frontend;

import com.fractals.backend.lindenmayer.LSystemPreset;
import com.fractals.backend.lindenmayer.Lindenmayer;
import com.fractals.backend.lindenmayer.TurtlePath;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * L-system editor: presets, editable axiom / rules / angle, drawn by the back-end turtle.
 */
final class LindenmayerView implements FractalView {

    private enum Coloring {
        GRADIENT("Gradient along the path"), DEPTH("By branch depth"), SINGLE("Single colour");

        private final String title;

        Coloring(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private static final Color BACKGROUND = Color.web("#11141c");
    private static final int COLOR_BUCKETS = 96;

    private final BorderPane root = new BorderPane();
    private final CanvasPane canvasPane = new CanvasPane(this::redraw);
    private final ComboBox<LSystemPreset> presets = new ComboBox<>();
    private final TextField axiom = new TextField();
    private final TextArea rules = new TextArea();
    private final TextField angle = new TextField();
    private final TextField heading = new TextField();
    private final ComboBox<Coloring> coloring = new ComboBox<>();
    private final Slider[] iterationsSlider = new Slider[1];
    private final Label status = Ui.hint("");
    private final AsyncRender<TurtlePath> renderer;
    private TurtlePath path;
    private int iterations;

    LindenmayerView() {
        renderer = new AsyncRender<>(result -> {
            path = result;
            status.setText(String.format("%,d segments", result.size()));
            redraw();
        }, error -> status.setText("⚠ " + error.getMessage()));

        presets.getItems().setAll(LSystemPreset.values());
        presets.setOnAction(e -> loadPreset(presets.getValue()));
        rules.setPrefRowCount(4);
        rules.setWrapText(false);
        coloring.getItems().setAll(Coloring.values());
        coloring.setValue(Coloring.GRADIENT);
        coloring.setOnAction(e -> redraw());

        Button draw = new Button("Draw");
        draw.getStyleClass().add("accent");
        draw.setMaxWidth(Double.MAX_VALUE);
        draw.setDefaultButton(true);
        draw.setOnAction(e -> compute());
        angle.setOnAction(e -> compute());
        heading.setOnAction(e -> compute());
        axiom.setOnAction(e -> compute());

        root.setCenter(canvasPane);
        root.setRight(Ui.controlPanel(
                Ui.section("Lindenmayer system"),
                Ui.field("Preset", presets),
                Ui.field("Axiom", axiom),
                Ui.field("Rules (one per line)", rules),
                Ui.field("Angle (degrees)", angle),
                Ui.field("Start direction (degrees)", heading),
                Ui.intSlider("Iterations", 0, 14, 4, v -> {
                    iterations = v;
                    compute();
                }, iterationsSlider),
                draw,
                Ui.field("Colouring", coloring),
                status,
                Ui.hint("F, G: draw a line · f: move · + / -: turn · |: turn around · [ ]: branch. "
                        + "Other letters (X, Y…) are only rewritten.")));

        presets.setValue(LSystemPreset.KOCH_SNOWFLAKE);
        loadPreset(LSystemPreset.KOCH_SNOWFLAKE);
    }

    private void loadPreset(LSystemPreset preset) {
        if (preset == null) {
            return;
        }
        axiom.setText(preset.axiom());
        rules.setText(preset.rules());
        angle.setText(format(preset.angle()));
        heading.setText(format(preset.heading()));
        iterations = preset.iterations();
        iterationsSlider[0].setValue(iterations);
        compute();
    }

    private static String format(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    /** Sends the current definition to the back-end. */
    private void compute() {
        Lindenmayer system;
        try {
            Map<Character, String> parsed = Lindenmayer.parseRules(rules.getText());
            system = new Lindenmayer(axiom.getText(), parsed,
                    Double.parseDouble(angle.getText().trim().replace(',', '.')),
                    Double.parseDouble(heading.getText().trim().replace(',', '.')));
        } catch (NumberFormatException e) {
            status.setText("⚠ The angle must be a number");
            return;
        } catch (IllegalArgumentException e) {
            status.setText("⚠ " + e.getMessage());
            return;
        }
        int n = iterations;
        status.setText("Computing…");
        renderer.request(cancelled -> system.draw(n));
    }

    private void redraw() {
        GraphicsContext gc = canvasPane.canvas().getGraphicsContext2D();
        double w = canvasPane.getWidth();
        double h = canvasPane.getHeight();
        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, w, h);
        if (path == null || path.size() == 0 || w < 10 || h < 10) {
            return;
        }
        double margin = 24;
        double pathW = Math.max(1e-9, path.maxX() - path.minX());
        double pathH = Math.max(1e-9, path.maxY() - path.minY());
        double scale = Math.min((w - 2 * margin) / pathW, (h - 2 * margin) / pathH);
        double offX = (w - pathW * scale) / 2 - path.minX() * scale;
        double offY = (h + pathH * scale) / 2 + path.minY() * scale; // y axis flipped

        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        double baseWidth = Math.max(0.6, Math.min(2.5, scale * 0.25));

        int n = path.size();
        switch (coloring.getValue()) {
            case SINGLE -> {
                gc.setLineWidth(baseWidth);
                strokeRange(gc, 0, n, Color.web("#7fdbff"), scale, offX, offY);
            }
            case GRADIENT -> {
                gc.setLineWidth(baseWidth);
                int buckets = Math.min(COLOR_BUCKETS, n);
                for (int b = 0; b < buckets; b++) {
                    int from = (int) ((long) n * b / buckets);
                    int to = (int) ((long) n * (b + 1) / buckets);
                    Color c = Color.hsb(200 + 160.0 * b / buckets, 0.75, 1.0);
                    strokeRange(gc, from, to, c, scale, offX, offY);
                }
            }
            case DEPTH -> {
                int maxDepth = path.maxDepth();
                List<List<Integer>> byDepth = new ArrayList<>();
                for (int d = 0; d <= maxDepth; d++) {
                    byDepth.add(new ArrayList<>());
                }
                for (int i = 0; i < n; i++) {
                    byDepth.get(path.depth(i)).add(i);
                }
                for (int d = 0; d <= maxDepth; d++) {
                    double t = maxDepth == 0 ? 1 : (double) d / maxDepth;
                    // brown trunk → green leaves
                    gc.setStroke(Color.color(0.45 * (1 - t) + 0.35 * t, 0.30 * (1 - t) + 0.85 * t, 0.15 * (1 - t) + 0.30 * t));
                    gc.setLineWidth(Math.max(0.6, baseWidth * 2.2 * Math.pow(0.72, d)));
                    gc.beginPath();
                    for (int i : byDepth.get(d)) {
                        gc.moveTo(offX + path.x1(i) * scale, offY - path.y1(i) * scale);
                        gc.lineTo(offX + path.x2(i) * scale, offY - path.y2(i) * scale);
                    }
                    gc.stroke();
                }
            }
        }
    }

    /** Strokes segments [from, to) as one path; consecutive connected segments share a lineTo. */
    private void strokeRange(GraphicsContext gc, int from, int to, Color color, double scale, double offX, double offY) {
        gc.setStroke(color);
        gc.beginPath();
        double lastX = Double.NaN, lastY = Double.NaN;
        for (int i = from; i < to; i++) {
            double x1 = offX + path.x1(i) * scale, y1 = offY - path.y1(i) * scale;
            double x2 = offX + path.x2(i) * scale, y2 = offY - path.y2(i) * scale;
            if (Math.abs(x1 - lastX) > 1e-6 || Math.abs(y1 - lastY) > 1e-6) {
                gc.moveTo(x1, y1);
            }
            gc.lineTo(x2, y2);
            lastX = x2;
            lastY = y2;
        }
        gc.stroke();
    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
