package com.fractals.backend.mandelbrot.julia;

import com.fractals.backend.mandelbrot.EscapeTimeFractal;
import com.fractals.backend.mandelbrot.Viewport;

import java.util.List;

/**
 * The Julia set of z → z² + c for a fixed parameter c.
 * Every point of the Mandelbrot set gives a connected Julia set.
 */
public record Julia(double cr, double ci) implements EscapeTimeFractal {

    public static final Viewport DEFAULT_VIEW = new Viewport(0, 0, 3.0);

    /** A famous value of c with a name. */
    public record Preset(String name, double cr, double ci) {
        @Override
        public String toString() {
            return name;
        }
    }

    public static final List<Preset> PRESETS = List.of(
            new Preset("Dragon (-0.8 + 0.156i)", -0.8, 0.156),
            new Preset("Douady rabbit", -0.123, 0.745),
            new Preset("San Marco", -0.75, 0.0),
            new Preset("Siegel disk", -0.391, -0.587),
            new Preset("Dendrite (i)", 0.0, 1.0),
            new Preset("Spiral (-0.7269 + 0.1889i)", -0.7269, 0.1889),
            new Preset("Galaxy (0.285 + 0.01i)", 0.285, 0.01),
            new Preset("Snowflakes (-0.4 + 0.6i)", -0.4, 0.6));

    @Override
    public double iterate(double x, double y, int maxIterations) {
        return EscapeTimeFractal.orbit(x, y, cr, ci, maxIterations);
    }
}
