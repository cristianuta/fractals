/**
 * Fractals: Java back-end (computations, no JavaFX) and JavaFX front-end shown in the browser by JPro.
 */
module com.fractals.fxml {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jpro.webapi;

    exports com.fractals;
    exports com.fractals.backend.lindenmayer;
    exports com.fractals.backend.mandelbrot;
    exports com.fractals.backend.mandelbrot.julia;
    exports com.fractals.backend.render3d;
    exports com.fractals.backend.sierpinski;
    exports com.fractals.frontend;
    opens com.fractals.frontend to javafx.fxml;
}
