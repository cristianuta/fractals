package com.fractals.backend.sierpinski;

import java.util.ArrayList;
import java.util.List;

/**
 * Sierpinski carpet in the unit square [0,1]×[0,1]: at every level each square is split
 * into 3×3 and the middle one is removed.
 */
public final class Carpet {

    public static final int MAX_DEPTH = 6;

    /** Axis aligned square; {@code level} is the iteration that produced it. */
    public record Square(double x, double y, double size, int level) {
    }

    private Carpet() {
    }

    /**
     * The removed middle squares ("holes") of all levels 1..depth.
     * Drawing the full square and then the holes is much cheaper than drawing every
     * remaining square: (8^n - 1) / 7 holes instead of 8^n squares.
     */
    public static List<Square> holes(int depth) {
        List<Square> out = new ArrayList<>();
        holes(0, 0, 1, 1, depth, out);
        return out;
    }

    private static void holes(double x, double y, double size, int level, int depth, List<Square> out) {
        if (level > depth) {
            return;
        }
        double third = size / 3;
        out.add(new Square(x + third, y + third, third, level));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != 1 || j != 1) {
                    holes(x + i * third, y + j * third, third, level + 1, depth, out);
                }
            }
        }
    }

    /** The 8^depth squares that remain after {@code depth} iterations. */
    public static List<Square> squares(int depth) {
        List<Square> out = new ArrayList<>();
        squares(0, 0, 1, depth, depth, out);
        return out;
    }

    private static void squares(double x, double y, double size, int remaining, int depth, List<Square> out) {
        if (remaining == 0) {
            out.add(new Square(x, y, size, depth));
            return;
        }
        double third = size / 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != 1 || j != 1) {
                    squares(x + i * third, y + j * third, third, remaining - 1, depth, out);
                }
            }
        }
    }
}
