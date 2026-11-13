package com.fractals.backend.sierpinski;

import com.fractals.backend.render3d.Mesh;

/**
 * Sierpinski pyramid (tetrahedron, "tetrix") – the 3D analogue of the Sierpinski triangle.
 * A regular tetrahedron is replaced by the 4 half-size tetrahedra at its corners, recursively.
 */
public final class MengerTriangle {

    public static final int MAX_LEVEL = 7;

    private MengerTriangle() {
    }

    /** Number of small tetrahedra: 4^level. */
    public static long tetrahedronCount(int level) {
        return 1L << (2 * level);
    }

    /** Builds the pyramid inscribed in the unit sphere, apex up; 4·4^level triangles. */
    public static Mesh mesh(int level, Mesh.Colorizer colorizer) {
        double r = Math.sqrt(8) / 3;
        double[] apex = {0, 1, 0};
        double[] b1 = {r, -1.0 / 3, 0};
        double[] b2 = {-r / 2, -1.0 / 3, r * Math.sqrt(3) / 2};
        double[] b3 = {-r / 2, -1.0 / 3, -r * Math.sqrt(3) / 2};
        Mesh mesh = new Mesh();
        subdivide(mesh, apex, b1, b2, b3, level, colorizer);
        return mesh;
    }

    private static void subdivide(Mesh mesh, double[] a, double[] b, double[] c, double[] d,
                                  int level, Mesh.Colorizer colorizer) {
        if (level == 0) {
            addTetrahedron(mesh, a, b, c, d, colorizer);
            return;
        }
        double[] ab = mid(a, b), ac = mid(a, c), ad = mid(a, d);
        double[] bc = mid(b, c), bd = mid(b, d), cd = mid(c, d);
        subdivide(mesh, a, ab, ac, ad, level - 1, colorizer);
        subdivide(mesh, ab, b, bc, bd, level - 1, colorizer);
        subdivide(mesh, ac, bc, c, cd, level - 1, colorizer);
        subdivide(mesh, ad, bd, cd, d, level - 1, colorizer);
    }

    private static void addTetrahedron(Mesh mesh, double[] a, double[] b, double[] c, double[] d,
                                       Mesh.Colorizer colorizer) {
        double[] center = {(a[0] + b[0] + c[0] + d[0]) / 4, (a[1] + b[1] + c[1] + d[1]) / 4,
                (a[2] + b[2] + c[2] + d[2]) / 4};
        int rgb = colorizer.rgb(center[0], center[1], center[2]);
        double[][][] faces = {{b, c, d}, {a, c, d}, {a, b, d}, {a, b, c}};
        for (double[][] f : faces) {
            // outward normal = from the tetrahedron centre towards the face centre
            double nx = (f[0][0] + f[1][0] + f[2][0]) / 3 - center[0];
            double ny = (f[0][1] + f[1][1] + f[2][1]) / 3 - center[1];
            double nz = (f[0][2] + f[1][2] + f[2][2]) / 3 - center[2];
            mesh.addTriangle(f[0], f[1], f[2], nx, ny, nz, rgb);
        }
    }

    private static double[] mid(double[] p, double[] q) {
        return new double[]{(p[0] + q[0]) / 2, (p[1] + q[1]) / 2, (p[2] + q[2]) / 2};
    }
}
