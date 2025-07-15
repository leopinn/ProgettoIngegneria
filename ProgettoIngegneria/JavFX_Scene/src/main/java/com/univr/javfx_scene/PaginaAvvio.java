package com.univr.javfx_scene;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

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
            stage.setMinWidth(1600);
            stage.setMinHeight(900);

            Image iconaApplicazione = new Image(getClass().getResourceAsStream("/immagini/iconaApplicazione.png"));
            stage.getIcons().add(iconaApplicazione);
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
        alert.setHeaderText(null);
        alert.setContentText("Vuoi veramente uscire da UniSound?");

        // Creo i pulsanti per scegliere si o no
        ButtonType buttonYes = new ButtonType("✔ Sì");
        ButtonType buttonNo = new ButtonType("✖ No");
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        // Applico lo stile
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(PaginaAvvio.class.getResource("Applicazione.css").toExternalForm());
        dialogPane.getStyleClass().add("yesNoMessage");

        // Prendo il risultato e lo restituisco
        Optional<ButtonType> risultato = alert.showAndWait();

        if(risultato.get() == buttonYes)
            stage.close();
        }
}