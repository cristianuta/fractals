package com.fractals.backend.mandelbrot;

/**
 * A fractal computed with the escape-time algorithm over the complex plane.
 */
public interface EscapeTimeFractal {

    /** Squared bailout radius; a large radius gives smoother colour bands. */
    double BAILOUT = 256.0 * 256.0;

    /** Returned by {@link #iterate} for points that never escape (inside the set). */
    double INSIDE = -1;

    /**
     * @return the smooth (fractional) iteration count at which the orbit of point (x, y) escaped,
     * or {@link #INSIDE} if it did not escape within {@code maxIterations}
     */
    double iterate(double x, double y, int maxIterations);

    /** Normalized iteration count: removes the colour bands of the plain integer count. */
    static double smooth(int n, double modulusSquared) {
        double logModulus = 0.5 * Math.log(modulusSquared);
        return n + 1 - Math.log(logModulus / Math.log(2)) / Math.log(2);
    }

    /** Iterates z → z² + c starting from z0. */
    static double orbit(double zr, double zi, double cr, double ci, int maxIterations) {
        for (int n = 0; n < maxIterations; n++) {
            double zr2 = zr * zr;
            double zi2 = zi * zi;
            if (zr2 + zi2 > BAILOUT) {
                return smooth(n, zr2 + zi2);
            }
            zi = 2 * zr * zi + ci;
            zr = zr2 - zi2 + cr;
        }
        return INSIDE;
    }
}
