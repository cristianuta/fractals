/**
 * Module descriptor.
 *
 * @author Besmir Beqiri
 */
module com.fractals.fxml {
    requires javafx.controls;
    requires javafx.fxml;
    requires jpro.webapi;
    requires javafx.graphics;

    exports com.fractals.sierpinski;
    opens com.fractals.sierpinski to javafx.fxml;
    exports com.fractals.sierpinski.fxml;
    opens com.fractals.sierpinski.fxml to javafx.fxml;
    exports com.fractals.sierpinski.fxml.controller;
    opens com.fractals.sierpinski.fxml.controller to javafx.fxml;
}