package com.fractals.backend.sierpinski;

import com.fractals.backend.render3d.Mesh;

/**
 * Menger sponge – the Sierpinski cube, 3D analogue of the carpet. The cube [-1,1]³ is split
 * into 27 sub-cubes and the 7 middle ones (centre and face centres) are removed, recursively.
 */
public final class MengerSponge {

    public static final int MAX_LEVEL = 4;

    private static final int[][] DIRECTIONS = {
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

    private MengerSponge() {
    }

    /**
     * Whether the cell (x, y, z) of the 3^level grid is solid: a cell is removed when, at some
     * scale, at least two of its base-3 digits are 1.
     */
    public static boolean isSolid(int x, int y, int z) {
        while (x > 0 || y > 0 || z > 0) {
            int ones = (x % 3 == 1 ? 1 : 0) + (y % 3 == 1 ? 1 : 0) + (z % 3 == 1 ? 1 : 0);
            if (ones >= 2) {
                return false;
            }
            x /= 3;
            y /= 3;
            z /= 3;
        }
        return true;
    }

    /** Number of small cubes: 20^level. */
    public static long cubeCount(int level) {
        return (long) Math.pow(20, level);
    }

    /**
     * Builds a mesh with only the visible faces (faces between two solid cubes are skipped):
     * 2·20^n + 4·8^n squares.
     */
    public static Mesh mesh(int level, Mesh.Colorizer colorizer) {
        int n = (int) Math.pow(3, level);
        double cell = 2.0 / n;
        Mesh mesh = new Mesh();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                for (int z = 0; z < n; z++) {
                    if (!isSolid(x, y, z)) {
                        continue;
                    }
                    for (int[] d : DIRECTIONS) {
                        int nx = x + d[0], ny = y + d[1], nz = z + d[2];
                        boolean inside = nx >= 0 && ny >= 0 && nz >= 0 && nx < n && ny < n && nz < n;
                        if (inside && isSolid(nx, ny, nz)) {
                            continue;
                        }
                        addFace(mesh, -1 + x * cell, -1 + y * cell, -1 + z * cell, cell, d, colorizer);
                    }
                }
            }
        }
        return mesh;
    }

    private static void addFace(Mesh mesh, double x0, double y0, double z0, double s, int[] d,
                                Mesh.Colorizer colorizer) {
        // axis = the one the normal points along; u, v = the two others
        int axis = d[0] != 0 ? 0 : d[1] != 0 ? 1 : 2;
        int u = (axis + 1) % 3, v = (axis + 2) % 3;
        double[] origin = {x0, y0, z0};
        if (d[axis] > 0) {
            origin[axis] += s;
        }
        double[] a = origin.clone();
        double[] b = origin.clone();
        b[u] += s;
        double[] c = b.clone();
        c[v] += s;
        double[] e = origin.clone();
        e[v] += s;
        double cx = (a[0] + c[0]) / 2, cy = (a[1] + c[1]) / 2, cz = (a[2] + c[2]) / 2;
        mesh.addQuad(a, b, c, e, d[0], d[1], d[2], colorizer.rgb(cx, cy, cz));
    }
}
