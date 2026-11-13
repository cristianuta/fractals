package com.fractals.frontend;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * Pane with a canvas that always fills it; {@code redraw} is called after every resize.
 */
class CanvasPane extends Pane {

    private final Canvas canvas = new Canvas();

    CanvasPane(Runnable redraw) {
        getStyleClass().add("canvas-pane");
        setMinSize(0, 0);
        getChildren().add(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        canvas.widthProperty().addListener((o, a, b) -> redraw.run());
        canvas.heightProperty().addListener((o, a, b) -> redraw.run());
    }

    Canvas canvas() {
        return canvas;
    }
}
