package com.fractals.backend.mandelbrot;

/**
 * The Mandelbrot set: all c for which the orbit of 0 under z → z² + c stays bounded.
 */
public final class Mandelbrot implements EscapeTimeFractal {

    public static final Viewport DEFAULT_VIEW = new Viewport(-0.6, 0, 2.6);

    @Override
    public double iterate(double cr, double ci, int maxIterations) {
        // Quick tests for the main cardioid and the period-2 bulb (large black areas).
        double xq = cr - 0.25;
        double q = xq * xq + ci * ci;
        if (q * (q + xq) <= 0.25 * ci * ci) {
            return INSIDE;
        }
        if ((cr + 1) * (cr + 1) + ci * ci <= 0.0625) {
            return INSIDE;
        }
        return EscapeTimeFractal.orbit(0, 0, cr, ci, maxIterations);
    }
}
