package com.fractals.backend.mandelbrot;

/**
 * Visible region of the complex plane: center and size ({@code span}).
 * {@code span} is the extent shown along the shorter side of the image.
 */
public record Viewport(double centerX, double centerY, double span) {

    /** Below this span double precision runs out and the image becomes blocky. */
    public static final double MIN_SPAN = 1e-13;
    public static final double MAX_SPAN = 10;

    /** Real coordinate of pixel column {@code px} in an image of size w × h. */
    public double toX(double px, int w, int h) {
        return centerX + (px - w / 2.0) * span / Math.min(w, h);
    }

    /** Imaginary coordinate of pixel row {@code py} (rows grow downwards). */
    public double toY(double py, int w, int h) {
        return centerY - (py - h / 2.0) * span / Math.min(w, h);
    }

    /** Pixel column of real coordinate {@code x}. */
    public double toPixelX(double x, int w, int h) {
        return w / 2.0 + (x - centerX) * Math.min(w, h) / span;
    }

    /** Pixel row of imaginary coordinate {@code y}. */
    public double toPixelY(double y, int w, int h) {
        return h / 2.0 - (y - centerY) * Math.min(w, h) / span;
    }

    /** Moves the view so the image content follows a mouse drag of (dx, dy) pixels. */
    public Viewport pan(double dxPx, double dyPx, int w, int h) {
        double s = span / Math.min(w, h);
        return new Viewport(centerX - dxPx * s, centerY + dyPx * s, span);
    }

    /** Zooms by {@code factor} (> 1 zooms in) keeping the point under pixel (px, py) fixed. */
    public Viewport zoom(double factor, double px, double py, int w, int h) {
        double ax = toX(px, w, h);
        double ay = toY(py, w, h);
        double newSpan = Math.max(MIN_SPAN, Math.min(MAX_SPAN, span / factor));
        double f = newSpan / span;
        return new Viewport(ax + (centerX - ax) * f, ay + (centerY - ay) * f, newSpan);
    }
}
