package com.univr.javfx_scene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PaginaLogin implements Initializable {

    @FXML
    private BorderPane PaginaLogin_borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            impostaSchermata();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void impostaSchermata() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneLogin.fxml"));
        Parent registerPane = loader.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPaneLogin controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaLogin_borderPane.setCenter(registerPane);
    }

    public void paginaIscriviti() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneIscrizione.fxml"));
            Parent iscrizionePane = loader.load();

            PaginaPaneIscrizione controller = loader.getController();
            controller.setMainController(this);  // << chiave per comunicare

            PaginaLogin_borderPane.setCenter(iscrizionePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
