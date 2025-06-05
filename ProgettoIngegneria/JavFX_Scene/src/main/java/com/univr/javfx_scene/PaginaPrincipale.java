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
import javafx.scene.layout.StackPane;

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
    @FXML
    private Parent commentiPane;
    @FXML
    private StackPane PaginaPrincipale_rightPane;
    @FXML
    private StackPane placeholderPane;

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

        if (commentiPane != null) {
            nascondiCommenti();
        }
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

        if (commentiPane != null) {
            nascondiCommenti();
        }

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

    public void selezionaMusica(int parId) throws IOException {
        impostaDatiCanzone(parId);
    }

    private void impostaDatiCanzone(int parId) throws IOException {
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

        // Sezione commenti del brano
        mostraCommenti(1, parId);
    }

    public void mostraCommenti(int parId, int id_canzone) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneCommenti.fxml"));
        commentiPane = loader.load();

        PaginaPaneCommenti controller = loader.getController();
        controller.caricaCommenti(id_canzone);

        PaginaPrincipale_rightPane.getChildren().setAll(commentiPane);
        PaginaPrincipale_rightPane.setPrefWidth(250);
        PaginaPrincipale_rightPane.setVisible(true);
        PaginaPrincipale_rightPane.setManaged(true);
    }

    public void nascondiCommenti() {
        PaginaPrincipale_rightPane.getChildren().setAll(placeholderPane);
        PaginaPrincipale_rightPane.setPrefWidth(100);
        PaginaPrincipale_rightPane.setVisible(true);
        PaginaPrincipale_rightPane.setManaged(true);
    }
}
