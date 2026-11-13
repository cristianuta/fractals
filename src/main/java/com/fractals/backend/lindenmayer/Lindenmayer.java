package com.fractals.backend.lindenmayer;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lindenmayer system (L-system): string rewriting followed by turtle interpretation.
 * <p>
 * Turtle alphabet:
 * <ul>
 *     <li>{@code F}, {@code G} – move forward one step and draw a line</li>
 *     <li>{@code f}, {@code g} – move forward one step without drawing</li>
 *     <li>{@code +} / {@code -} – turn left / right by the angle</li>
 *     <li>{@code |} – turn around (180°)</li>
 *     <li>{@code [} / {@code ]} – push / pop the turtle state (branching)</li>
 *     <li>any other symbol (e.g. {@code X}, {@code Y}) only takes part in rewriting</li>
 * </ul>
 * Pure Java (no JavaFX) – this is part of the back-end.
 */
public final class Lindenmayer {

    /** Upper bound for the expanded string, protects the server against huge requests. */
    public static final int DEFAULT_MAX_SYMBOLS = 4_000_000;

    private final String axiom;
    private final Map<Character, String> rules;
    private final double angle;
    private final double heading;

    /**
     * @param axiom   start string
     * @param rules   production rules (symbol → replacement)
     * @param angle   turning angle in degrees
     * @param heading initial direction of the turtle in degrees (0 = east, 90 = north)
     */
    public Lindenmayer(String axiom, Map<Character, String> rules, double angle, double heading) {
        if (axiom == null || axiom.isBlank()) {
            throw new IllegalArgumentException("The axiom must not be empty");
        }
        this.axiom = normalize(axiom).replaceAll("\\s+", "");
        this.rules = Collections.unmodifiableMap(new LinkedHashMap<>(rules));
        this.angle = angle;
        this.heading = heading;
    }

    public static Lindenmayer of(LSystemPreset preset) {
        return new Lindenmayer(preset.axiom(), parseRules(preset.rules()), preset.angle(), preset.heading());
    }

    /**
     * Parses rules written one per line: {@code F=F+F-F}, {@code F -> F+F} or {@code F → F+F}.
     */
    public static Map<Character, String> parseRules(String text) {
        Map<Character, String> rules = new LinkedHashMap<>();
        if (text == null) {
            return rules;
        }
        for (String raw : text.split("\\R")) {
            String line = normalize(raw).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("->|→|=", 2);
            if (parts.length != 2 || parts[0].trim().length() != 1) {
                throw new IllegalArgumentException("Invalid rule \"" + raw.trim() + "\" (expected e.g. F=F+F-F)");
            }
            rules.put(parts[0].trim().charAt(0), parts[1].replaceAll("\\s+", ""));
        }
        return rules;
    }

    /** Accepts the Unicode minus sign often found in copied rules. */
    private static String normalize(String s) {
        return s.replace('−', '-');
    }

    /**
     * Applies the rules {@code iterations} times to the axiom.
     *
     * @throws IllegalStateException if the result would be longer than {@code maxSymbols}
     */
    public String expand(int iterations, int maxSymbols) {
        String current = axiom;
        for (int i = 0; i < iterations; i++) {
            StringBuilder next = new StringBuilder(Math.min(maxSymbols, current.length() * 4));
            for (int k = 0; k < current.length(); k++) {
                char c = current.charAt(k);
                String replacement = rules.get(c);
                if (replacement == null) {
                    next.append(c);
                } else {
                    next.append(replacement);
                }
                if (next.length() > maxSymbols) {
                    throw new IllegalStateException("Too many symbols at iteration " + (i + 1)
                            + " (limit " + maxSymbols + "). Reduce the number of iterations.");
                }
            }
            current = next.toString();
        }
        return current;
    }

    /** Runs the turtle over the given command string and returns the drawn segments. */
    public TurtlePath interpret(String commands) {
        TurtlePath path = new TurtlePath();
        Deque<double[]> stack = new ArrayDeque<>();
        double x = 0;
        double y = 0;
        double dir = Math.toRadians(heading);
        double turn = Math.toRadians(angle);
        int depth = 0;
        for (int i = 0; i < commands.length(); i++) {
            char c = commands.charAt(i);
            switch (c) {
                case 'F', 'G' -> {
                    double nx = x + Math.cos(dir);
                    double ny = y + Math.sin(dir);
                    path.add(x, y, nx, ny, depth);
                    x = nx;
                    y = ny;
                }
                case 'f', 'g' -> {
                    x += Math.cos(dir);
                    y += Math.sin(dir);
                }
                case '+' -> dir += turn;
                case '-' -> dir -= turn;
                case '|' -> dir += Math.PI;
                case '[' -> {
                    stack.push(new double[]{x, y, dir});
                    depth++;
                }
                case ']' -> {
                    if (!stack.isEmpty()) {
                        double[] state = stack.pop();
                        x = state[0];
                        y = state[1];
                        dir = state[2];
                        depth--;
                    }
                }
                default -> {
                    // symbol used only for rewriting
                }
            }
        }
        return path;
    }

    /** Expands and interprets in one step. */
    public TurtlePath draw(int iterations) {
        return interpret(expand(iterations, DEFAULT_MAX_SYMBOLS));
    }

    public String axiom() {
        return axiom;
    }

    public Map<Character, String> rules() {
        return rules;
    }

    public double angle() {
        return angle;
    }

    public double heading() {
        return heading;
    }
}
