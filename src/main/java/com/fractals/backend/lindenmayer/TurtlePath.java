package com.fractals.backend.lindenmayer;

import java.util.Arrays;

/**
 * Line segments produced by the turtle, stored compactly in primitive arrays.
 * Coordinates use a mathematical orientation (y grows upwards), one unit = one step.
 */
public final class TurtlePath {

    private double[] coords = new double[1024];
    private int[] depths = new int[256];
    private int size;
    private int maxDepth;
    private double minX = Double.POSITIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    void add(double x1, double y1, double x2, double y2, int depth) {
        if (size == depths.length) {
            depths = Arrays.copyOf(depths, size * 2);
            coords = Arrays.copyOf(coords, size * 8);
        }
        int i = size * 4;
        coords[i] = x1;
        coords[i + 1] = y1;
        coords[i + 2] = x2;
        coords[i + 3] = y2;
        depths[size] = depth;
        size++;
        maxDepth = Math.max(maxDepth, depth);
        minX = Math.min(minX, Math.min(x1, x2));
        maxX = Math.max(maxX, Math.max(x1, x2));
        minY = Math.min(minY, Math.min(y1, y2));
        maxY = Math.max(maxY, Math.max(y1, y2));
    }

    /** Number of segments. */
    public int size() {
        return size;
    }

    public double x1(int i) {
        return coords[i * 4];
    }

    public double y1(int i) {
        return coords[i * 4 + 1];
    }

    public double x2(int i) {
        return coords[i * 4 + 2];
    }

    public double y2(int i) {
        return coords[i * 4 + 3];
    }

    /** Branch depth ({@code [} nesting level) of the segment. */
    public int depth(int i) {
        return depths[i];
    }

    public int maxDepth() {
        return maxDepth;
    }

    public double minX() {
        return size == 0 ? 0 : minX;
    }

    public double minY() {
        return size == 0 ? 0 : minY;
    }

    public double maxX() {
        return size == 0 ? 0 : maxX;
    }

    public double maxY() {
        return size == 0 ? 0 : maxY;
    }
}
