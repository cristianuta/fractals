package com.fractals.backend.mandelbrot;

/**
 * Cyclic colour gradients for escape-time images, stored as lookup tables of ARGB values.
 */
public enum Palette {

    CLASSIC("Classic", new double[]{0, 0.16, 0.42, 0.6425, 0.8575},
            new int[]{0x000764, 0x206BCB, 0xEDFFFF, 0xFFAA00, 0x000200}),
    FIRE("Fire", new double[]{0, 0.25, 0.5, 0.75},
            new int[]{0x100000, 0xB01000, 0xFFA000, 0xFFF4C0}),
    OCEAN("Ocean", new double[]{0, 0.3, 0.6, 0.8},
            new int[]{0x001020, 0x00608F, 0x40E0D0, 0xF0FFFF}),
    NEON("Neon", new double[]{0, 0.33, 0.66},
            new int[]{0x2B0A5E, 0xFF2D95, 0x2DE2FF}),
    GRAYSCALE("Grayscale", new double[]{0, 0.5},
            new int[]{0x101010, 0xF0F0F0});

    private static final int SIZE = 1024;
    private static final int INSIDE_COLOR = 0xFF000000;

    private final String title;
    private final int[] lut = new int[SIZE];

    Palette(String title, double[] stops, int[] colors) {
        this.title = title;
        for (int i = 0; i < SIZE; i++) {
            double t = (double) i / SIZE;
            int k = stops.length - 1;
            while (k > 0 && stops[k] > t) {
                k--;
            }
            int next = (k + 1) % stops.length;
            double end = next == 0 ? 1.0 : stops[next];
            double f = (t - stops[k]) / (end - stops[k]);
            lut[i] = 0xFF000000 | mix(colors[k], colors[next], f);
        }
    }

    private static int mix(int a, int b, double f) {
        int r = (int) Math.round(((a >> 16) & 0xFF) * (1 - f) + ((b >> 16) & 0xFF) * f);
        int g = (int) Math.round(((a >> 8) & 0xFF) * (1 - f) + ((b >> 8) & 0xFF) * f);
        int bl = (int) Math.round((a & 0xFF) * (1 - f) + (b & 0xFF) * f);
        return (r << 16) | (g << 8) | bl;
    }

    /**
     * @param smoothIteration result of {@link EscapeTimeFractal#iterate}
     * @param density         how fast the gradient repeats (higher = more colour cycles)
     */
    public int color(double smoothIteration, double density) {
        if (smoothIteration < 0) {
            return INSIDE_COLOR;
        }
        double t = Math.max(0, Math.sqrt(smoothIteration) - 1) * density;
        int index = (int) ((t - Math.floor(t)) * SIZE);
        return lut[Math.min(SIZE - 1, index)];
    }

    @Override
    public String toString() {
        return title;
    }
}
