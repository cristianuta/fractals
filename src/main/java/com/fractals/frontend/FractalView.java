package com.fractals.frontend;

import javafx.scene.Node;

/**
 * A screen of the application, opened from the menu.
 */
interface FractalView {

    Node node();

    /** Called when the view becomes visible. */
    default void onShow() {
    }

    /** Called when another view is selected (stop animations etc.). */
    default void onHide() {
    }

    /** Called when the browser session ends. */
    default void dispose() {
    }
}
