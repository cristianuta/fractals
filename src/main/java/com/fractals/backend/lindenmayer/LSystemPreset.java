package com.fractals.backend.lindenmayer;

/**
 * Well-known L-systems, grouped by category.
 * Sources: Prusinkiewicz &amp; Lindenmayer, "The Algorithmic Beauty of Plants", and Paul Bourke's L-system pages.
 */
public enum LSystemPreset {

    // ---------- curves ----------
    KOCH_SNOWFLAKE(Category.CURVE, "Koch snowflake", "F--F--F", "F=F+F--F+F", 60, 0, 4),
    KOCH_ANTI_SNOWFLAKE(Category.CURVE, "Koch anti-snowflake", "F++F++F", "F=F+F--F+F", 60, 0, 4),
    KOCH_CURVE(Category.CURVE, "Quadratic Koch curve", "F", "F=F+F-F-F+F", 90, 0, 4),
    CESARO(Category.CURVE, "Cesàro curve", "F", "F=F+F--F+F", 85, 0, 5),
    DRAGON(Category.CURVE, "Dragon curve", "F", "F=F+G\nG=F-G", 90, 0, 12),
    TWIN_DRAGON(Category.CURVE, "Twin dragon", "FX+FX+", "X=X+YF\nY=FX-Y", 90, 0, 10),
    TERDRAGON(Category.CURVE, "Terdragon", "F", "F=F+F-F", 120, 90, 7),
    LEVY(Category.CURVE, "Lévy C curve", "F", "F=+F--F+", 45, 0, 12),
    SIERPINSKI_TRIANGLE(Category.CURVE, "Sierpinski triangle", "F-G-G", "F=F-G+F+G-F\nG=GG", 120, 0, 6),
    SIERPINSKI_ARROWHEAD(Category.CURVE, "Sierpinski arrowhead", "F", "F=G-F-G\nG=F+G+F", 60, 0, 7),
    SIERPINSKI_CURVE(Category.CURVE, "Sierpinski curve", "F--XF--F--XF", "X=XF+G+XF--F--XF+G+X", 45, 0, 4),
    HILBERT(Category.CURVE, "Hilbert curve", "X", "X=+YF-XFX-FY+\nY=-XF+YFY+FX-", 90, 0, 6),
    MOORE(Category.CURVE, "Moore curve", "LFL+F+LFL", "L=-RF+LFL+FR-\nR=+LF-RFR-FL+", 90, 0, 5),
    PEANO(Category.CURVE, "Peano curve", "X", "X=XFYFX+F+YFXFY-F-XFYFX\nY=YFXFY-F-XFYFX+F+YFXFY", 90, 0, 3),
    GOSPER(Category.CURVE, "Gosper curve", "F", "F=F-G--G+F++FF+G-\nG=+F-GG--G-F++F+G", 60, 0, 4),

    // ---------- islands and tilings ----------
    KOCH_ISLAND(Category.TILING, "Quadratic Koch island", "F-F-F-F", "F=F-F+F+FF-F-F+F", 90, 0, 3),
    CRYSTAL(Category.TILING, "Crystal", "F+F+F+F", "F=FF+F++F+F", 90, 0, 4),
    BOARD(Category.TILING, "Board", "F+F+F+F", "F=FF+F+F+F+FF", 90, 0, 4),
    RINGS(Category.TILING, "Rings", "F+F+F+F", "F=FF+F+F+F+F+F-F", 90, 0, 3),
    PENTAPLEXITY(Category.TILING, "Pentaplexity", "F++F++F++F++F", "F=F++F++F|F-F++F", 36, 0, 4),
    PENROSE(Category.TILING, "Penrose tiling (P3)", "[N]++[N]++[N]++[N]++[N]",
            "M=OF++PF----NF[-OF----MF]++\nN=+OF--PF[---MF--NF]+\nO=-MF++NF[+++OF++PF]-\n"
                    + "P=--OF++++MF[+PF++++NF]--NF\nF=", 36, 0, 4),

    // ---------- plants ----------
    PLANT(Category.PLANT, "Fractal plant", "X", "X=F+[[X]-X]-F[-FX]+X\nF=FF", 25, 70, 6),
    BUSH(Category.PLANT, "Bush", "F", "F=FF+[+F-F-F]-[-F+F+F]", 22.5, 90, 4),
    TREE(Category.PLANT, "Fractal tree", "F", "F=F[+F]F[-F]F", 25.7, 90, 5),
    WEED(Category.PLANT, "Weed", "F", "F=F[+F]F[-F][F]", 20, 90, 5),
    TWIG(Category.PLANT, "Twig", "X", "X=F[+X]F[-X]+X\nF=FF", 20, 90, 7),
    FERN(Category.PLANT, "Fern", "X", "X=F[+X][-X]FX\nF=FF", 25.7, 90, 7),
    WILLOW(Category.PLANT, "Willow", "X", "X=F-[[X]+X]+F[+FX]-X\nF=FF", 22.5, 90, 5),
    BINARY_TREE(Category.PLANT, "Binary tree", "X", "X=F[+X][-X]\nF=FF", 35, 90, 8);

    /** Kind of figure; plants look best coloured by branch depth. */
    public enum Category {
        CURVE("Curve"), TILING("Island / tiling"), PLANT("Plant");

        private final String title;

        Category(String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }
    }

    private final Category category;
    private final String title;
    private final String axiom;
    private final String rules;
    private final double angle;
    private final double heading;
    private final int iterations;

    LSystemPreset(Category category, String title, String axiom, String rules, double angle, double heading,
                  int iterations) {
        this.category = category;
        this.title = title;
        this.axiom = axiom;
        this.rules = rules;
        this.angle = angle;
        this.heading = heading;
        this.iterations = iterations;
    }

    public Category category() {
        return category;
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
        return category.title() + " · " + title;
    }
}
