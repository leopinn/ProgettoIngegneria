package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PaginaPrincipale implements Initializable {

    private  ObjSql objSql = ObjSql.oggettoSql();

    @FXML
    private BorderPane PaginaPrincipale_borderPane;
    @FXML
    private ImageView PaginaPrincipale_imageCopertina;
    @FXML
    private Label PaginaPrincipale_labelTitoloCanzone;
    @FXML
    private Label PaginaPrincipale_labelAutoreCanzone;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            paginaPrincipale();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void paginaPrincipale() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPanePrincipale.fxml"));
        Parent registerPane = loader.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPanePrincipale controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
    }

    public void impostazioni() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneImpostazioni.fxml"));
        Parent registerPane = loader.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPaneImpostazioni controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
    }

    public void logout(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("PaginaLogin.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Applicazione.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneUpload.fxml"));
        Parent registerPane = loader.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPaneUpload controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
    }

    public void PaginaPaneImpostazioniAmministratore() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneImpostazioniAmministratore.fxml"));
        Parent registerPane = loader.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPaneImpostazioniAmministratore controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
    }

    public void selezionaMusica(int parId){
        impostaDatiCanzone(parId);
    }

    private void impostaDatiCanzone(int parId){
        // Imposto la copertina
        String locPath="upload/copertine/"+parId+".jpg";
        Image immagine = new Image(new File(locPath).toURI().toString());

        PaginaPrincipale_imageCopertina.setImage(immagine);

        // Imposto titolo e autore
        Map<String, Object> rowBrano = objSql.leggi(String.format("SELECT TITOLO, AUTORE FROM CANZONE WHERE ID_CANZONE=%s", parId));
        PaginaPrincipale_labelTitoloCanzone.setText(rowBrano.get("TITOLO").toString());
        PaginaPrincipale_labelTitoloCanzone.setVisible(true);

        PaginaPrincipale_labelAutoreCanzone.setText(rowBrano.get("AUTORE").toString());
        PaginaPrincipale_labelAutoreCanzone.setVisible(true);
    }
}
