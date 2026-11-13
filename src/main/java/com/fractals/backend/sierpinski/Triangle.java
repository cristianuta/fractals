package com.fractals.backend.sierpinski;

import java.util.ArrayList;
import java.util.List;

/**
 * Sierpinski triangle: an equilateral triangle with base 1, apex at (0.5, 0) and base on
 * y = √3/2 (screen orientation, y grows downwards). At every level the triangle joining the
 * edge midpoints is removed.
 */
public final class Triangle {

    public static final int MAX_DEPTH = 9;
    public static final double HEIGHT = Math.sqrt(3) / 2;

    /** Triangle given by its three corners; {@code level} is the iteration that produced it. */
    public record Tri(double x1, double y1, double x2, double y2, double x3, double y3, int level) {
    }

    private Triangle() {
    }

    /** The outer triangle. */
    public static Tri outline() {
        return new Tri(0.5, 0, 1, HEIGHT, 0, HEIGHT, 0);
    }

    /** The removed (upside-down) middle triangles of levels 1..depth: (3^n - 1) / 2 of them. */
    public static List<Tri> holes(int depth) {
        List<Tri> out = new ArrayList<>();
        Tri t = outline();
        holes(t.x1, t.y1, t.x2, t.y2, t.x3, t.y3, 1, depth, out);
        return out;
    }

    private static void holes(double ax, double ay, double bx, double by, double cx, double cy,
                              int level, int depth, List<Tri> out) {
        if (level > depth) {
            return;
        }
        double abx = (ax + bx) / 2, aby = (ay + by) / 2;
        double bcx = (bx + cx) / 2, bcy = (by + cy) / 2;
        double cax = (cx + ax) / 2, cay = (cy + ay) / 2;
        out.add(new Tri(abx, aby, bcx, bcy, cax, cay, level));
        holes(ax, ay, abx, aby, cax, cay, level + 1, depth, out);
        holes(abx, aby, bx, by, bcx, bcy, level + 1, depth, out);
        holes(cax, cay, bcx, bcy, cx, cy, level + 1, depth, out);
    }

    /** The 3^depth triangles that remain after {@code depth} iterations. */
    public static List<Tri> triangles(int depth) {
        List<Tri> out = new ArrayList<>();
        Tri t = outline();
        triangles(t.x1, t.y1, t.x2, t.y2, t.x3, t.y3, depth, depth, out);
        return out;
    }

    private static void triangles(double ax, double ay, double bx, double by, double cx, double cy,
                                  int remaining, int depth, List<Tri> out) {
        if (remaining == 0) {
            out.add(new Tri(ax, ay, bx, by, cx, cy, depth));
            return;
        }
        double abx = (ax + bx) / 2, aby = (ay + by) / 2;
        double bcx = (bx + cx) / 2, bcy = (by + cy) / 2;
        double cax = (cx + ax) / 2, cay = (cy + ay) / 2;
        triangles(ax, ay, abx, aby, cax, cay, remaining - 1, depth, out);
        triangles(abx, aby, bx, by, bcx, bcy, remaining - 1, depth, out);
        triangles(cax, cay, bcx, bcy, cx, cy, remaining - 1, depth, out);
    }
}
