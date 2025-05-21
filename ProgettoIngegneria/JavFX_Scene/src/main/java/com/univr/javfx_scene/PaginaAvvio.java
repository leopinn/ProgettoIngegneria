package com.univr.javfx_scene;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;

public class PaginaAvvio extends Application {

    public static Stage stageLogin;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            stageLogin=stage;
            Parent root = FXMLLoader.load(getClass().getResource("PaginaLogin.fxml"));

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("Applicazione.css").toExternalForm());
            stage.setScene(scene);
            stage.setMinWidth(1280);
            stage.setMinHeight(720);
            stage.show();

            stage.setOnCloseRequest(event -> {
                event.consume();
                logout(stage);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        inizializzaVariabili();
        launch(args);
    }

    private static void inizializzaVariabili() {
        Font font = Font.loadFont(PaginaAvvio.class.getResourceAsStream("/font/GothamBoldItalic.ttf"), 12);
        System.out.println("Font caricato: " + font.getName());
        font = Font.loadFont(PaginaAvvio.class.getResourceAsStream("/font/GothamBold.ttf"), 12);
        System.out.println("Font caricato: " + font.getName());
        font = Font.loadFont(PaginaAvvio.class.getResourceAsStream("/font/GothamMedium.ttf"), 12);
        System.out.println("Font caricato: " + font.getName());
    }

    public void logout(Stage stage) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Stai per uscire dal programma!");
        alert.setContentText("Vuoi salvare i tuoi progressi prima di uscire?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Programma chiuso con successo");
            stage.close();
        }
    }
}