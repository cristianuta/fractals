package com.fractals.backend.render3d;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * A small z-buffer rasterizer: perspective projection, back-face culling, flat Lambert shading
 * and optional supersampling (anti-aliasing). Produces an ARGB image, so the 3D fractals work in
 * any browser through JPro without relying on JavaFX 3D.
 */
public final class SoftwareRenderer {

    private static final double FOV_DEGREES = 40;
    private static final double AMBIENT = 0.28;
    // direction towards the light, in camera space (up-left, in front of the object)
    private static final double LX, LY, LZ;

    static {
        double x = -0.45, y = 0.65, z = -0.62;
        double len = Math.sqrt(x * x + y * y + z * z);
        LX = x / len;
        LY = y / len;
        LZ = z / len;
    }

    /** Camera orbiting the origin. */
    public record Camera(double yawDegrees, double pitchDegrees, double distance) {
    }

    private SoftwareRenderer() {
    }

    /**
     * @param supersample 1 = no anti-aliasing, 2 = 2×2 samples per pixel, …
     * @return ARGB pixels, or {@code null} if cancelled
     */
    public static int[] render(Mesh mesh, Camera camera, int width, int height, int supersample,
                               BooleanSupplier cancelled) {
        int ss = Math.max(1, supersample);
        int w = width * ss;
        int h = height * ss;

        double yaw = Math.toRadians(camera.yawDegrees());
        double pitch = Math.toRadians(camera.pitchDegrees());
        double cy = Math.cos(yaw), sy = Math.sin(yaw), cp = Math.cos(pitch), sp = Math.sin(pitch);
        double focal = Math.min(w, h) * 0.5 / Math.tan(Math.toRadians(FOV_DEGREES / 2));

        // 1. transform, cull and shade every triangle
        int n = mesh.triangleCount();
        float[] sx = new float[n * 3];
        float[] syy = new float[n * 3];
        float[] iz = new float[n * 3];
        int[] shade = new int[n];
        int visible = 0;
        double[] px = new double[3], py = new double[3], pz = new double[3];
        for (int t = 0; t < n; t++) {
            for (int k = 0; k < 3; k++) {
                double x = mesh.coord(t, k, 0), y = mesh.coord(t, k, 1), z = mesh.coord(t, k, 2);
                double x1 = cy * x + sy * z;
                double z1 = -sy * x + cy * z;
                double y2 = cp * y + sp * z1; // positive pitch looks from above
                double z2 = -sp * y + cp * z1;
                px[k] = x1;
                py[k] = y2;
                pz[k] = z2 + camera.distance();
            }
            if (pz[0] < 0.05 || pz[1] < 0.05 || pz[2] < 0.05) {
                continue; // behind or too close to the camera
            }
            double ux = px[1] - px[0], uy = py[1] - py[0], uz = pz[1] - pz[0];
            double vx = px[2] - px[0], vy = py[2] - py[0], vz = pz[2] - pz[0];
            double nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx;
            if (nx * px[0] + ny * py[0] + nz * pz[0] >= 0) {
                continue; // back face
            }
            double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
            double lambert = Math.max(0, (nx * LX + ny * LY + nz * LZ) / len);
            shade[visible] = shadeColor(mesh.color(t), AMBIENT + (1 - AMBIENT) * lambert);
            for (int k = 0; k < 3; k++) {
                int i = visible * 3 + k;
                sx[i] = (float) (w / 2.0 + focal * px[k] / pz[k]);
                syy[i] = (float) (h / 2.0 - focal * py[k] / pz[k]);
                iz[i] = (float) (1.0 / pz[k]);
            }
            visible++;
        }
        if (cancelled.getAsBoolean()) {
            return null;
        }

        // 2. rasterize in horizontal bands, in parallel (each band owns its rows)
        int[] color = new int[w * h];
        float[] depth = new float[w * h];
        for (int y = 0; y < h; y++) {
            Arrays.fill(color, y * w, (y + 1) * w, background(y, h));
        }
        int triangles = visible;
        int bands = Math.max(1, Math.min(h, Runtime.getRuntime().availableProcessors() * 4));
        int bandHeight = (h + bands - 1) / bands;
        boolean completed = IntStream.range(0, bands).parallel().allMatch(band -> {
            if (cancelled.getAsBoolean()) {
                return false;
            }
            int rowStart = band * bandHeight;
            int rowEnd = Math.min(h, rowStart + bandHeight);
            for (int t = 0; t < triangles; t++) {
                rasterize(t, sx, syy, iz, shade[t], w, rowStart, rowEnd, color, depth);
            }
            return true;
        });
        if (!completed) {
            return null;
        }
        return ss == 1 ? color : downsample(color, width, height, ss);
    }

    private static void rasterize(int t, float[] sx, float[] sy, float[] iz, int rgb, int w,
                                  int rowStart, int rowEnd, int[] color, float[] depth) {
        int i = t * 3;
        float x0 = sx[i], y0 = sy[i], x1 = sx[i + 1], y1 = sy[i + 1], x2 = sx[i + 2], y2 = sy[i + 2];
        float minY = Math.min(y0, Math.min(y1, y2));
        float maxY = Math.max(y0, Math.max(y1, y2));
        int yStart = Math.max(rowStart, (int) Math.ceil(minY - 0.5f));
        int yEnd = Math.min(rowEnd - 1, (int) Math.floor(maxY - 0.5f));
        if (yStart > yEnd) {
            return;
        }
        int xStart = Math.max(0, (int) Math.ceil(Math.min(x0, Math.min(x1, x2)) - 0.5f));
        int xEnd = Math.min(w - 1, (int) Math.floor(Math.max(x0, Math.max(x1, x2)) - 0.5f));
        if (xStart > xEnd) {
            return;
        }
        float area = (x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0);
        if (area == 0) {
            return;
        }
        float inv = 1f / area;
        float z0 = iz[i], z1 = iz[i + 1], z2 = iz[i + 2];
        for (int y = yStart; y <= yEnd; y++) {
            float py = y + 0.5f;
            int row = y * w;
            for (int x = xStart; x <= xEnd; x++) {
                float px = x + 0.5f;
                // barycentric weights; all have the sign of the area when the pixel is inside
                float b0 = ((x1 - px) * (y2 - py) - (x2 - px) * (y1 - py)) * inv;
                float b1 = ((x2 - px) * (y0 - py) - (x0 - px) * (y2 - py)) * inv;
                float b2 = 1 - b0 - b1;
                if (b0 < 0 || b1 < 0 || b2 < 0) {
                    continue;
                }
                float z = b0 * z0 + b1 * z1 + b2 * z2; // 1/z: larger is closer
                int p = row + x;
                if (z > depth[p]) {
                    depth[p] = z;
                    color[p] = rgb;
                }
            }
        }
    }

    private static int shadeColor(int rgb, double intensity) {
        int r = (int) Math.min(255, ((rgb >> 16) & 0xFF) * intensity);
        int g = (int) Math.min(255, ((rgb >> 8) & 0xFF) * intensity);
        int b = (int) Math.min(255, (rgb & 0xFF) * intensity);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /** Vertical gradient from dark blue-grey to almost black. */
    private static int background(int y, int h) {
        double t = (double) y / Math.max(1, h - 1);
        int r = (int) (0x26 * (1 - t) + 0x0B * t);
        int g = (int) (0x2C * (1 - t) + 0x0D * t);
        int b = (int) (0x3E * (1 - t) + 0x14 * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int[] downsample(int[] src, int width, int height, int ss) {
        int[] out = new int[width * height];
        int srcWidth = width * ss;
        int samples = ss * ss;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = 0, g = 0, b = 0;
                for (int dy = 0; dy < ss; dy++) {
                    int row = (y * ss + dy) * srcWidth + x * ss;
                    for (int dx = 0; dx < ss; dx++) {
                        int c = src[row + dx];
                        r += (c >> 16) & 0xFF;
                        g += (c >> 8) & 0xFF;
                        b += c & 0xFF;
                    }
                }
                out[y * width + x] = 0xFF000000 | ((r / samples) << 16) | ((g / samples) << 8) | (b / samples);
            }
        }
        return out;
    }
}
