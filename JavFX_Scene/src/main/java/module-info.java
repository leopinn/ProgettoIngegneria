module com.univr.javfx_scene {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires jdk.jdi;
    requires java.desktop;
    requires javafx.web;
    requires javafx.graphics;


    opens com.univr.javfx_scene to javafx.fxml;
    exports com.univr.javfx_scene;
}