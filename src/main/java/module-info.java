/**
 * Module descriptor.
 *
 * @author Besmir Beqiri
 */
module com.fractals.fxml {
    requires javafx.controls;
    requires javafx.fxml;
    requires jpro.webapi;

    exports com.fractals.fxml;
    opens com.fractals.fxml to javafx.fxml;
    exports com.fractals.sierpinski;
    opens com.fractals.sierpinski to javafx.fxml;
}