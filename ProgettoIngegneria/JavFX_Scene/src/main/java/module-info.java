module com.univr.javfx_scene {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.univr.javfx_scene to javafx.fxml;
    exports com.univr.javfx_scene;
}