package com.fractals.frontend;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Resizable pane that shows an ARGB pixel buffer computed by the back-end.
 * JPro sends the image to the browser whenever its pixels change.
 */
class ImagePane extends Pane {

    private final ImageView view = new ImageView();
    private WritableImage image;

    ImagePane() {
        getStyleClass().add("image-pane");
        setMinSize(0, 0);
        view.setManaged(false);
        getChildren().add(view);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
    }

    int pixelWidth() {
        return Math.max(0, (int) Math.floor(getWidth()));
    }

    int pixelHeight() {
        return Math.max(0, (int) Math.floor(getHeight()));
    }

    void show(int[] argb, int width, int height) {
        if (image == null || (int) image.getWidth() != width || (int) image.getHeight() != height) {
            image = new WritableImage(width, height);
            view.setImage(image);
        }
        image.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
    }

    /** Runs {@code action} whenever the size changes. */
    void onResize(Runnable action) {
        widthProperty().addListener((o, a, b) -> action.run());
        heightProperty().addListener((o, a, b) -> action.run());
    }
}
