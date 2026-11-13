package com.fractals.frontend;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.DoubleFunction;
import java.util.function.IntConsumer;

/**
 * Small factory methods for the control panels.
 */
final class Ui {

    private Ui() {
    }

    /** The scrollable panel with controls shown on the right of every fractal. */
    static ScrollPane controlPanel(Node... children) {
        VBox box = new VBox(12, children);
        box.getStyleClass().add("control-panel");
        box.setPadding(new Insets(16));
        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("control-scroll");
        scroll.setPrefWidth(300);
        scroll.setMinWidth(260);
        return scroll;
    }

    static Label section(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    static Label hint(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("hint");
        label.setWrapText(true);
        return label;
    }

    static VBox field(String name, Node control) {
        Label label = new Label(name);
        label.getStyleClass().add("field-label");
        if (control instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
        }
        return new VBox(4, label, control);
    }

    /** Slider with integer values; {@code onChange} runs only when the integer value changes. */
    static VBox intSlider(String name, int min, int max, int value, IntConsumer onChange, Slider[] out) {
        Slider slider = new Slider(min, max, value);
        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        Label valueLabel = new Label(String.valueOf(value));
        int[] last = {value};
        slider.valueProperty().addListener((o, a, b) -> {
            int v = (int) Math.round(b.doubleValue());
            if (v != last[0]) {
                last[0] = v;
                valueLabel.setText(String.valueOf(v));
                onChange.accept(v);
            }
        });
        if (out != null) {
            out[0] = slider;
        }
        return sliderBox(name, slider, valueLabel);
    }

    /** Slider with real values; the displayed text is produced by {@code format}. */
    static VBox slider(String name, double min, double max, double value, DoubleFunction<String> format,
                       java.util.function.DoubleConsumer onChange) {
        Slider slider = new Slider(min, max, value);
        Label valueLabel = new Label(format.apply(value));
        slider.valueProperty().addListener((o, a, b) -> {
            valueLabel.setText(format.apply(b.doubleValue()));
            onChange.accept(b.doubleValue());
        });
        return sliderBox(name, slider, valueLabel);
    }

    private static VBox sliderBox(String name, Slider slider, Label valueLabel) {
        Label label = new Label(name);
        label.getStyleClass().add("field-label");
        valueLabel.getStyleClass().add("value-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return new VBox(4, new HBox(label, spacer, valueLabel), slider);
    }
}
