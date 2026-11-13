package com.fractals.frontend;

import com.fractals.backend.render3d.Mesh;
import com.fractals.backend.render3d.SoftwareRenderer;
import com.fractals.backend.sierpinski.MengerSponge;
import com.fractals.backend.sierpinski.MengerTriangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * 3D Sierpinski fractals (cube = Menger sponge, pyramid = Sierpinski tetrahedron), rendered to an
 * image by the back-end software renderer. Drag to rotate, scroll to zoom.
 */
final class Solid3DView implements FractalView {

    enum Kind {
        CUBE("Sierpinski cube (Menger sponge)", MengerSponge.MAX_LEVEL, 2, 5.2),
        PYRAMID("Sierpinski pyramid", MengerTriangle.MAX_LEVEL, 4, 3.4);

        final String title;
        final int maxLevel;
        final int defaultLevel;
        final double distance;

        Kind(String title, int maxLevel, int defaultLevel, double distance) {
            this.title = title;
            this.maxLevel = maxLevel;
            this.defaultLevel = defaultLevel;
            this.distance = distance;
        }
    }

    private enum Scheme {
        GOLD("Gold"), ICE("Ice"), RAINBOW("Rainbow");

        private final String title;

        Scheme(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

        Mesh.Colorizer colorizer() {
            return switch (this) {
                case GOLD -> (x, y, z) -> blend(0xC9822B, 0xFFD27A, (y + 1) / 2);
                case ICE -> (x, y, z) -> blend(0x3A6EA5, 0xBDEBFF, (y + 1) / 2);
                case RAINBOW -> (x, y, z) -> {
                    javafx.scene.paint.Color c = javafx.scene.paint.Color.hsb((x + z + 2) / 4 * 300, 0.6, 1);
                    return ((int) (c.getRed() * 255) << 16) | ((int) (c.getGreen() * 255) << 8) | (int) (c.getBlue() * 255);
                };
            };
        }

        private static int blend(int a, int b, double t) {
            t = Math.max(0, Math.min(1, t));
            int r = (int) (((a >> 16) & 0xFF) * (1 - t) + ((b >> 16) & 0xFF) * t);
            int g = (int) (((a >> 8) & 0xFF) * (1 - t) + ((b >> 8) & 0xFF) * t);
            int bl = (int) ((a & 0xFF) * (1 - t) + (b & 0xFF) * t);
            return (r << 16) | (g << 8) | bl;
        }
    }

    private record Frame(int[] pixels, int width, int height) {
    }

    private final Kind kind;
    private final BorderPane root = new BorderPane();
    private final ImagePane imagePane = new ImagePane();
    private final ComboBox<Scheme> scheme = new ComboBox<>();
    private final CheckBox autoRotate = new CheckBox("Rotate automatically");
    private final Label info = Ui.hint("");
    private final AsyncRender<Mesh> meshBuilder;
    private final AsyncRender<Frame> renderer;
    private final Timeline rotation;
    private Mesh mesh;
    private int level;
    private double yaw = 35;
    private double pitch = 25;
    private double distance;
    private boolean interacting;

    Solid3DView(Kind kind) {
        this.kind = kind;
        this.level = kind.defaultLevel;
        this.distance = kind.distance;
        meshBuilder = new AsyncRender<>(m -> {
            mesh = m;
            info.setText(String.format("%,d %s · %,d triangles", count(), kind == Kind.CUBE ? "cubes" : "tetrahedra",
                    m.triangleCount()));
            render();
        }, Throwable::printStackTrace);
        renderer = new AsyncRender<>(f -> imagePane.show(f.pixels, f.width, f.height), Throwable::printStackTrace);

        rotation = new Timeline(new KeyFrame(Duration.millis(66), e -> {
            yaw += 2;
            render();
        }));
        rotation.setCycleCount(Timeline.INDEFINITE);
        autoRotate.setOnAction(e -> {
            if (autoRotate.isSelected()) {
                rotation.play();
            } else {
                rotation.stop();
                render();
            }
        });

        double[] last = new double[2];
        imagePane.setOnMousePressed(e -> {
            last[0] = e.getX();
            last[1] = e.getY();
            interacting = true;
        });
        imagePane.setOnMouseDragged(e -> {
            yaw -= (e.getX() - last[0]) * 0.5;
            pitch = Math.max(-89, Math.min(89, pitch - (e.getY() - last[1]) * 0.5));
            last[0] = e.getX();
            last[1] = e.getY();
            render();
        });
        imagePane.setOnMouseReleased(e -> {
            interacting = false;
            render();
        });
        imagePane.setOnScroll(e -> {
            if (e.getDeltaY() != 0) {
                distance = Math.max(kind.distance * 0.45, Math.min(kind.distance * 3, distance * (e.getDeltaY() > 0 ? 0.9 : 1.1)));
                render();
            }
        });
        imagePane.onResize(this::render);

        scheme.getItems().setAll(Scheme.values());
        scheme.setValue(kind == Kind.CUBE ? Scheme.GOLD : Scheme.ICE);
        scheme.setOnAction(e -> buildMesh());

        Button reset = new Button("Reset view");
        reset.setMaxWidth(Double.MAX_VALUE);
        reset.setOnAction(e -> {
            yaw = 35;
            pitch = 25;
            distance = kind.distance;
            render();
        });

        String explanation = kind == Kind.CUBE
                ? "The cube is divided into 27 smaller cubes and the 7 in the middle (centre and face centres) are "
                + "removed. After n steps 20ⁿ cubes remain; the dimension is log 20 / log 3 ≈ 2.727."
                : "The tetrahedron is replaced by the 4 half-size tetrahedra at its corners. After n steps 4ⁿ "
                + "tetrahedra remain; the surface area stays constant while the volume tends to 0 (dimension 2).";
        root.setCenter(imagePane);
        root.setRight(Ui.controlPanel(
                Ui.section(kind.title),
                Ui.intSlider("Iterations", 0, kind.maxLevel, level, v -> {
                    level = v;
                    buildMesh();
                }, null),
                Ui.field("Colours", scheme),
                autoRotate,
                reset,
                info,
                Ui.hint("Drag to rotate, scroll to zoom. The image is rendered on the server by the back-end "
                        + "(z-buffer with perspective and lighting) and streamed to the browser by JPro."),
                Ui.hint(explanation)));
        buildMesh();
    }

    private long count() {
        return kind == Kind.CUBE ? MengerSponge.cubeCount(level) : MengerTriangle.tetrahedronCount(level);
    }

    private void buildMesh() {
        int n = level;
        Mesh.Colorizer colorizer = scheme.getValue().colorizer();
        Kind k = kind;
        info.setText("Building…");
        meshBuilder.request(cancelled -> k == Kind.CUBE ? MengerSponge.mesh(n, colorizer) : MengerTriangle.mesh(n, colorizer));
    }

    private void render() {
        int w = imagePane.pixelWidth(), h = imagePane.pixelHeight();
        if (mesh == null || w < 2 || h < 2) {
            return;
        }
        Mesh m = mesh;
        SoftwareRenderer.Camera camera = new SoftwareRenderer.Camera(yaw, pitch, distance);
        // full anti-aliasing for still images, faster single sampling while moving
        int supersample = interacting || rotation.getStatus() == Timeline.Status.RUNNING ? 1 : 2;
        renderer.request(cancelled -> {
            int[] pixels = SoftwareRenderer.render(m, camera, w, h, supersample, cancelled);
            return pixels == null ? null : new Frame(pixels, w, h);
        });
    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    public void onShow() {
        if (autoRotate.isSelected()) {
            rotation.play();
        }
    }

    @Override
    public void onHide() {
        rotation.stop();
    }

    @Override
    public void dispose() {
        rotation.stop();
        meshBuilder.dispose();
        renderer.dispose();
    }
}
