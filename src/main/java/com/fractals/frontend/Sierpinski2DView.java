package com.fractals.frontend;

import com.fractals.backend.sierpinski.Carpet;
import com.fractals.backend.sierpinski.Triangle;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Sierpinski carpet or triangle, drawn on a canvas from the geometry computed by the back-end.
 */
final class Sierpinski2DView implements FractalView {

    enum Kind {
        CARPET("Sierpinski carpet", Carpet.MAX_DEPTH, 4),
        TRIANGLE("Sierpinski triangle", Triangle.MAX_DEPTH, 6);

        final String title;
        final int maxDepth;
        final int defaultDepth;

        Kind(String title, int maxDepth, int defaultDepth) {
            this.title = title;
            this.maxDepth = maxDepth;
            this.defaultDepth = defaultDepth;
        }
    }

    private enum Scheme {
        CLASSIC("Classic orange"), LEVELS("Rainbow levels"), NIGHT("Night blue");

        private final String title;

        Scheme(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private static final Color BACKGROUND = Color.web("#11141c");

    private final Kind kind;
    private final BorderPane root = new BorderPane();
    private final CanvasPane canvasPane = new CanvasPane(this::redraw);
    private final ComboBox<Scheme> scheme = new ComboBox<>();
    private final Label info = Ui.hint("");
    private final AsyncRender<List<?>> renderer;
    private List<?> holes = List.of();
    private int depth;

    Sierpinski2DView(Kind kind) {
        this.kind = kind;
        this.depth = kind.defaultDepth;
        renderer = new AsyncRender<>(result -> {
            holes = result;
            redraw();
        }, Throwable::printStackTrace);

        scheme.getItems().setAll(Scheme.values());
        scheme.setValue(Scheme.CLASSIC);
        scheme.setOnAction(e -> redraw());

        String explanation = kind == Kind.CARPET
                ? "Every square is divided into 3×3 and the middle one is removed. After n steps 8ⁿ squares "
                + "remain and the area is (8/9)ⁿ – it tends to 0, while the dimension is log 8 / log 3 ≈ 1.893."
                : "Every triangle is divided into 4 by its edge midpoints and the middle one is removed. After n steps "
                + "3ⁿ triangles remain; the dimension is log 3 / log 2 ≈ 1.585.";
        root.setCenter(canvasPane);
        root.setRight(Ui.controlPanel(
                Ui.section(kind.title),
                Ui.intSlider("Iterations", 0, kind.maxDepth, depth, v -> {
                    depth = v;
                    compute();
                }, null),
                Ui.field("Colours", scheme),
                info,
                Ui.hint(explanation)));
        compute();
    }

    private void compute() {
        int n = depth;
        Kind k = kind;
        renderer.request(cancelled -> k == Kind.CARPET ? Carpet.holes(n) : Triangle.holes(n));
        long remaining = (long) Math.pow(kind == Kind.CARPET ? 8 : 3, n);
        info.setText(String.format("%,d %s remaining", remaining, kind == Kind.CARPET ? "squares" : "triangles"));
    }

    private Color fill(Scheme s) {
        return switch (s) {
            case CLASSIC -> Color.web("#ff9f1c");
            case LEVELS -> Color.web("#f4f1de");
            case NIGHT -> Color.web("#4cc9f0");
        };
    }

    private Color holeColor(Scheme s, int level) {
        return switch (s) {
            case CLASSIC -> Color.WHITE;
            case LEVELS -> Color.hsb((level - 1) * 360.0 / Math.max(1, kind.maxDepth), 0.65, 0.9);
            case NIGHT -> Color.web("#0b1d3a").interpolate(BACKGROUND, Math.min(1, level / 4.0));
        };
    }

    private void redraw() {
        GraphicsContext gc = canvasPane.canvas().getGraphicsContext2D();
        double w = canvasPane.getWidth();
        double h = canvasPane.getHeight();
        Scheme s = scheme.getValue();
        gc.setFill(s == Scheme.CLASSIC ? Color.WHITE : BACKGROUND);
        gc.fillRect(0, 0, w, h);
        double margin = 24;
        if (w < 2 * margin || h < 2 * margin) {
            return;
        }
        if (kind == Kind.CARPET) {
            double size = Math.min(w, h) - 2 * margin;
            double x0 = (w - size) / 2, y0 = (h - size) / 2;
            gc.setFill(fill(s));
            gc.fillRect(x0, y0, size, size);
            for (Object o : holes) {
                Carpet.Square q = (Carpet.Square) o;
                gc.setFill(holeColor(s, q.level()));
                gc.fillRect(x0 + q.x() * size, y0 + q.y() * size, q.size() * size, q.size() * size);
            }
            if (s == Scheme.CLASSIC) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(x0, y0, size, size);
            }
        } else {
            double size = Math.min(w - 2 * margin, (h - 2 * margin) / Triangle.HEIGHT);
            double x0 = (w - size) / 2, y0 = (h - size * Triangle.HEIGHT) / 2;
            gc.setFill(fill(s));
            fillTriangle(gc, Triangle.outline(), x0, y0, size);
            for (Object o : holes) {
                Triangle.Tri t = (Triangle.Tri) o;
                gc.setFill(holeColor(s, t.level()));
                fillTriangle(gc, t, x0, y0, size);
            }
        }
    }

    private static void fillTriangle(GraphicsContext gc, Triangle.Tri t, double x0, double y0, double size) {
        gc.fillPolygon(
                new double[]{x0 + t.x1() * size, x0 + t.x2() * size, x0 + t.x3() * size},
                new double[]{y0 + t.y1() * size, y0 + t.y2() * size, y0 + t.y3() * size}, 3);
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
