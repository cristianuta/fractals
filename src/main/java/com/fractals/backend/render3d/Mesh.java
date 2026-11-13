package com.fractals.backend.render3d;

import java.util.Arrays;

/**
 * Triangle soup with one RGB colour per triangle. Triangles are wound counter-clockwise
 * when seen from outside, so the renderer can skip faces turned away from the camera.
 */
public final class Mesh {

    /** Colour of a face, chosen from the position of its centre. */
    @FunctionalInterface
    public interface Colorizer {
        int rgb(double x, double y, double z);
    }

    private float[] vertices = new float[9 * 256];
    private int[] colors = new int[256];
    private int count;

    public int triangleCount() {
        return count;
    }

    /** Coordinate {@code axis} (0 = x, 1 = y, 2 = z) of corner {@code corner} of triangle {@code t}. */
    public float coord(int t, int corner, int axis) {
        return vertices[t * 9 + corner * 3 + axis];
    }

    public int color(int t) {
        return colors[t];
    }

    /** Adds a triangle, flipping it if needed so its normal points along (nx, ny, nz). */
    public void addTriangle(double[] a, double[] b, double[] c, double nx, double ny, double nz, int rgb) {
        double ux = b[0] - a[0], uy = b[1] - a[1], uz = b[2] - a[2];
        double vx = c[0] - a[0], vy = c[1] - a[1], vz = c[2] - a[2];
        double cx = uy * vz - uz * vy, cy = uz * vx - ux * vz, cz = ux * vy - uy * vx;
        if (cx * nx + cy * ny + cz * nz < 0) {
            double[] tmp = b;
            b = c;
            c = tmp;
        }
        if (count == colors.length) {
            colors = Arrays.copyOf(colors, count * 2);
            vertices = Arrays.copyOf(vertices, count * 18);
        }
        int i = count * 9;
        for (double[] p : new double[][]{a, b, c}) {
            vertices[i++] = (float) p[0];
            vertices[i++] = (float) p[1];
            vertices[i++] = (float) p[2];
        }
        colors[count++] = rgb;
    }

    /** Adds the quad a-b-c-d (corners in order around the edge) as two triangles. */
    public void addQuad(double[] a, double[] b, double[] c, double[] d, double nx, double ny, double nz, int rgb) {
        addTriangle(a, b, c, nx, ny, nz, rgb);
        addTriangle(a, c, d, nx, ny, nz, rgb);
    }
}
