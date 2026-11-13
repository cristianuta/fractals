package com.fractals.backend.lindenmayer;

/**
 * Well-known L-systems.
 */
public enum LSystemPreset {

    KOCH_SNOWFLAKE("Koch snowflake", "F--F--F", "F=F+F--F+F", 60, 0, 4),
    KOCH_CURVE("Quadratic Koch curve", "F", "F=F+F-F-F+F", 90, 0, 4),
    DRAGON("Dragon curve", "F", "F=F+G\nG=F-G", 90, 0, 12),
    LEVY("Lévy C curve", "F", "F=+F--F+", 45, 0, 12),
    SIERPINSKI_TRIANGLE("Sierpinski triangle", "F-G-G", "F=F-G+F+G-F\nG=GG", 120, 0, 6),
    SIERPINSKI_ARROWHEAD("Sierpinski arrowhead", "F", "F=G-F-G\nG=F+G+F", 60, 0, 7),
    HILBERT("Hilbert curve", "X", "X=+YF-XFX-FY+\nY=-XF+YFY-XF+", 90, 0, 6),
    GOSPER("Gosper curve", "F", "F=F-G--G+F++FF+G-\nG=+F-GG--G-F++F+G", 60, 0, 4),
    PLANT("Fractal plant", "X", "X=F+[[X]-X]-F[-FX]+X\nF=FF", 25, 70, 6),
    BUSH("Bush", "F", "F=FF+[+F-F-F]-[-F+F+F]", 22.5, 90, 4);

    private final String title;
    private final String axiom;
    private final String rules;
    private final double angle;
    private final double heading;
    private final int iterations;

    LSystemPreset(String title, String axiom, String rules, double angle, double heading, int iterations) {
        this.title = title;
        this.axiom = axiom;
        this.rules = rules;
        this.angle = angle;
        this.heading = heading;
        this.iterations = iterations;
    }

    public String title() {
        return title;
    }

    public String axiom() {
        return axiom;
    }

    /** Rules, one per line, in the {@code X=...} format. */
    public String rules() {
        return rules;
    }

    public double angle() {
        return angle;
    }

    public double heading() {
        return heading;
    }

    /** Recommended number of iterations. */
    public int iterations() {
        return iterations;
    }

    @Override
    public String toString() {
        return title;
    }
}
