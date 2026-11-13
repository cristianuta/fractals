package com.fractals.backend.mandelbrot;

import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * Renders an escape-time fractal into an ARGB pixel buffer, using all CPU cores.
 */
public final class FractalRenderer {

    private FractalRenderer() {
    }

    /**
     * @param cancelled polled once per row; when it returns true the render stops
     * @return pixels in row-major order (ARGB), or {@code null} if cancelled
     */
    public static int[] render(EscapeTimeFractal fractal, Viewport view, int width, int height,
                               int maxIterations, Palette palette, double density, BooleanSupplier cancelled) {
        int[] pixels = new int[width * height];
        double step = view.span() / Math.min(width, height);
        double left = view.toX(0, width, height);
        double top = view.toY(0, width, height);
        boolean completed = IntStream.range(0, height).parallel().allMatch(row -> {
            if (cancelled.getAsBoolean()) {
                return false;
            }
            double y = top - row * step;
            int offset = row * width;
            for (int col = 0; col < width; col++) {
                double n = fractal.iterate(left + col * step, y, maxIterations);
                pixels[offset + col] = palette.color(n, density);
            }
            return true;
        });
        return completed ? pixels : null;
    }
}
