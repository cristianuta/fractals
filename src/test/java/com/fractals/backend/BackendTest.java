package com.fractals.backend;

import com.fractals.backend.lindenmayer.LSystemPreset;
import com.fractals.backend.lindenmayer.Lindenmayer;
import com.fractals.backend.lindenmayer.TurtlePath;
import com.fractals.backend.mandelbrot.EscapeTimeFractal;
import com.fractals.backend.mandelbrot.FractalRenderer;
import com.fractals.backend.mandelbrot.Mandelbrot;
import com.fractals.backend.mandelbrot.Palette;
import com.fractals.backend.mandelbrot.Viewport;
import com.fractals.backend.mandelbrot.julia.Julia;
import com.fractals.backend.render3d.Mesh;
import com.fractals.backend.render3d.SoftwareRenderer;
import com.fractals.backend.sierpinski.Carpet;
import com.fractals.backend.sierpinski.MengerSponge;
import com.fractals.backend.sierpinski.MengerTriangle;
import com.fractals.backend.sierpinski.Triangle;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackendTest {

    // ---------- Lindenmayer ----------

    @Test
    void algaeGrowsLikeFibonacci() {
        Lindenmayer algae = new Lindenmayer("A", Map.of('A', "AB", 'B', "A"), 0, 0);
        int[] expected = {1, 2, 3, 5, 8, 13, 21};
        for (int n = 0; n < expected.length; n++) {
            assertEquals(expected[n], algae.expand(n, 1000).length());
        }
        assertEquals("ABAAB", algae.expand(3, 1000));
    }

    @Test
    void kochCurveSegmentCounts() {
        Lindenmayer koch = Lindenmayer.of(LSystemPreset.KOCH_CURVE);
        for (int n = 0; n <= 4; n++) {
            assertEquals((int) Math.pow(5, n), koch.draw(n).size());
        }
        Lindenmayer snowflake = Lindenmayer.of(LSystemPreset.KOCH_SNOWFLAKE);
        assertEquals(3 * 256, snowflake.draw(4).size());
    }

    @Test
    void closedCurvesReturnToTheStart() {
        TurtlePath square = new Lindenmayer("F+F+F+F", Map.of(), 90, 0).draw(0);
        assertEquals(4, square.size());
        assertEquals(0, square.x2(3), 1e-9);
        assertEquals(0, square.y2(3), 1e-9);
    }

    @Test
    void branchesRestoreTheTurtle() {
        TurtlePath path = new Lindenmayer("F[+F]F", Map.of(), 90, 0).draw(0);
        assertEquals(3, path.size());
        assertEquals(1, path.depth(1));
        assertEquals(1, path.x1(2), 1e-9); // continues from the end of the first segment
        assertEquals(0, path.y1(2), 1e-9);
        assertEquals(2, path.x2(2), 1e-9);
    }

    @Test
    void parsesRuleFormats() {
        Map<Character, String> rules = Lindenmayer.parseRules("F = F+F\nX -> F−X\n\nY→Y");
        assertEquals("F+F", rules.get('F'));
        assertEquals("F-X", rules.get('X'));
        assertEquals("Y", rules.get('Y'));
        assertThrows(IllegalArgumentException.class, () -> Lindenmayer.parseRules("FF=F"));
    }

    @Test
    void refusesHugeExpansions() {
        Lindenmayer koch = Lindenmayer.of(LSystemPreset.KOCH_CURVE);
        assertThrows(IllegalStateException.class, () -> koch.expand(12, 10_000));
    }

    @Test
    void allPresetsDrawSomething() {
        for (LSystemPreset preset : LSystemPreset.values()) {
            assertTrue(Lindenmayer.of(preset).draw(preset.iterations()).size() > 10, preset.title());
        }
    }

    // ---------- Mandelbrot & Julia ----------

    @Test
    void mandelbrotInsideAndOutside() {
        Mandelbrot m = new Mandelbrot();
        assertEquals(EscapeTimeFractal.INSIDE, m.iterate(0, 0, 500));
        assertEquals(EscapeTimeFractal.INSIDE, m.iterate(-1, 0, 500));
        assertEquals(EscapeTimeFractal.INSIDE, m.iterate(-0.1225, 0.7449, 2000)); // period-3 bulb, not covered by the quick tests
        assertTrue(m.iterate(2, 2, 500) >= 0);
        assertTrue(m.iterate(0.3, 0.6, 500) > m.iterate(1, 1, 500));
    }

    @Test
    void juliaWithZeroIsTheUnitDisk() {
        Julia j = new Julia(0, 0);
        assertEquals(EscapeTimeFractal.INSIDE, j.iterate(0.5, 0.5, 500));
        assertTrue(j.iterate(1.1, 0, 500) >= 0);
    }

    @Test
    void zoomKeepsTheAnchorFixed() {
        Viewport v = Mandelbrot.DEFAULT_VIEW;
        Viewport z = v.zoom(2, 100, 50, 400, 300);
        assertEquals(v.toX(100, 400, 300), z.toX(100, 400, 300), 1e-12);
        assertEquals(v.toY(50, 400, 300), z.toY(50, 400, 300), 1e-12);
        assertEquals(v.span() / 2, z.span(), 1e-12);
        assertEquals(123, v.toPixelX(v.toX(123, 400, 300), 400, 300), 1e-9);
    }

    @Test
    void rendersAndCancels() {
        int[] pixels = FractalRenderer.render(new Mandelbrot(), Mandelbrot.DEFAULT_VIEW, 60, 40, 200,
                Palette.CLASSIC, 0.35, () -> false);
        assertEquals(60 * 40, pixels.length);
        assertEquals(0xFF000000, pixels[20 * 60 + 33]); // centre column ≈ -0.6: inside the set
        assertNotEquals(0xFF000000, pixels[0]);
        assertNull(FractalRenderer.render(new Mandelbrot(), Mandelbrot.DEFAULT_VIEW, 60, 40, 200,
                Palette.CLASSIC, 0.35, () -> true));
    }

    // ---------- Sierpinski 2D ----------

    @Test
    void carpetCounts() {
        for (int n = 0; n <= 4; n++) {
            assertEquals((long) Math.pow(8, n), Carpet.squares(n).size());
            assertEquals(((long) Math.pow(8, n) - 1) / 7, Carpet.holes(n).size());
            double area = Carpet.squares(n).stream().mapToDouble(s -> s.size() * s.size()).sum();
            assertEquals(Math.pow(8.0 / 9, n), area, 1e-9);
        }
    }

    @Test
    void triangleCounts() {
        for (int n = 0; n <= 6; n++) {
            assertEquals((long) Math.pow(3, n), Triangle.triangles(n).size());
            assertEquals(((long) Math.pow(3, n) - 1) / 2, Triangle.holes(n).size());
        }
    }

    // ---------- Sierpinski 3D ----------

    @Test
    void mengerSpongeCubesAndVisibleFaces() {
        for (int level = 0; level <= 3; level++) {
            int n = (int) Math.pow(3, level);
            int solid = 0;
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    for (int z = 0; z < n; z++) {
                        if (MengerSponge.isSolid(x, y, z)) {
                            solid++;
                        }
                    }
                }
            }
            assertEquals(MengerSponge.cubeCount(level), solid);
            long faces = 2 * (long) Math.pow(20, level) + 4 * (long) Math.pow(8, level);
            assertEquals(2 * faces, MengerSponge.mesh(level, (x, y, z) -> 0xFFFFFF).triangleCount());
        }
    }

    @Test
    void pyramidHasFourTrianglesPerTetrahedron() {
        for (int level = 0; level <= 4; level++) {
            assertEquals(4 * MengerTriangle.tetrahedronCount(level),
                    MengerTriangle.mesh(level, (x, y, z) -> 0xFFFFFF).triangleCount());
        }
    }

    @Test
    void softwareRendererDrawsTheObjectInTheCentre() {
        Mesh cube = MengerSponge.mesh(0, (x, y, z) -> 0xFF0000);
        SoftwareRenderer.Camera camera = new SoftwareRenderer.Camera(30, 20, 5);
        int[] image = SoftwareRenderer.render(cube, camera, 80, 60, 1, () -> false);
        int center = image[30 * 80 + 40];
        assertTrue(((center >> 16) & 0xFF) > 60 && (center & 0xFF) < 20, "centre should be red");
        int corner = image[0];
        assertTrue(((corner >> 16) & 0xFF) < 60, "corner should be background");

        // supersampled image has the requested size, and the pyramid renders too
        int[] aa = SoftwareRenderer.render(MengerTriangle.mesh(2, (x, y, z) -> 0x00FF00), camera, 40, 30, 2, () -> false);
        assertEquals(40 * 30, aa.length);
    }
}
