package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PaginaLogin implements Initializable {

    @FXML private BorderPane PaginaLogin_borderPane;
    @FXML private Label PaginaLogin_labelRichiestaAccount;
    @FXML private StackPane PaginaLogin_stackPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            impostaSchermata();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Una volta effettuato l'accesso alla piattaforma
    public void paginaPrincipale() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPrincipale.fxml"));
        Parent registerPane = loader.load();
        PaginaPrincipale controller = loader.getController();

        // Questo passaggio server per sostituire completamente la scena, ossia sto completamente trascrivendo il broderpane
        Scene scena = PaginaLogin_borderPane.getScene();
        scena.setRoot(registerPane);
    }

    public void impostaSchermata() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneLogin.fxml"));
        Parent registerPane = loader.load();
        PaginaPaneLogin controller = loader.getController();
        controller.setMainController(this);

        PaginaLogin_borderPane.setCenter(registerPane);
    }

    public void paginaIscriviti() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneIscrizione.fxml"));
        Parent iscrizionePane = loader.load();
        PaginaPaneIscrizione controller = loader.getController();
        controller.setMainController(this);

        PaginaLogin_borderPane.setCenter(iscrizionePane);
    }

    public void mostraLabelIscrizione() {
        // FADE IN
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), PaginaLogin_labelRichiestaAccount);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Dopo il fade-in, parte il fade-out
        fadeIn.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), PaginaLogin_labelRichiestaAccount);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.seconds(2.5));

            // Quando il fade-out è completato, nasconde e rimuove dal layout
            fadeOut.setOnFinished(e -> {
                PaginaLogin_stackPane.setVisible(false);
                PaginaLogin_stackPane.setManaged(false);
            });

            fadeOut.play();
        });

        // Assicura che il nodo sia visibile e gestito prima del fade-in
        PaginaLogin_stackPane.setVisible(true);
        PaginaLogin_stackPane.setManaged(true);

        fadeIn.play();
    }

}
