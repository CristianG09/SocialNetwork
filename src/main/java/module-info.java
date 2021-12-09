module com.example.socialtpygui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.socialtpygui to javafx.fxml;
    exports com.example.socialtpygui;

    exports com.example.socialtpygui.controller;
    opens com.example.socialtpygui.controller to javafx.fxml;
}