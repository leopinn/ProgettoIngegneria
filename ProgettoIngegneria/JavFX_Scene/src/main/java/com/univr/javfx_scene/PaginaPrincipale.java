package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

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
    @FXML private Label PaginaPrincipale_labelAutoreCanzone,PaginaPrincipale_minutaggioIniziale, PaginaPrincipale_minutaggioFinale;
    @FXML
    private Parent commentiPane;
    @FXML private StackPane PaginaPrincipale_rightPane, placeholderPane;
    @FXML private Slider PaginaPrincipale_sliderMusica;
    @FXML private Button PaginaPrincipale_buttonPlay;

    public MediaPlayer mediaPlayer;

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
        FXMLLoader loaderPrincipale = new FXMLLoader(getClass().getResource("PaginaPanePrincipale.fxml"));
        Parent registerPane = loaderPrincipale.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPanePrincipale controller = loaderPrincipale.getController();
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
        riproduciCanzone(parId);
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
        FXMLLoader loaderCommenti = new FXMLLoader(getClass().getResource("PaginaPaneCommenti.fxml"));
        Parent registerPane = loaderCommenti.load();

        PaginaPaneCommenti controller = loaderCommenti.getController();
        controller.caricaCommenti(id_canzone);

        PaginaPrincipale_borderPane.setRight(registerPane);
    }

    public void nascondiCommenti() {
        PaginaPrincipale_borderPane.setRight(null);
    }

    public void playStop(){
        // Controlla se è in esecuzione
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            PaginaPrincipale_buttonPlay.getStyleClass().remove("PaginaPrincipale_buttonStop");
            PaginaPrincipale_buttonPlay.getStyleClass().add("PaginaPrincipale_buttonPlay");
            mediaPlayer.pause();
        } else {
            PaginaPrincipale_buttonPlay.getStyleClass().remove("PaginaPrincipale_buttonPlay");
            PaginaPrincipale_buttonPlay.getStyleClass().add("PaginaPrincipale_buttonStop");
            mediaPlayer.play();
        }
    }

    public void riproduciCanzone(int parId){
        File file = new File("upload/musiche/"+parId+".mp3");
        if (!file.exists()) {
            System.out.println("File audio non trovato!");
            return;
        }
        String locPath = file.toURI().toString();
        Media media = new Media(locPath);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Libera risorse native
        }

        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {
            Duration durata = mediaPlayer.getMedia().getDuration();
            PaginaPrincipale_sliderMusica.setMax(durata.toMillis());
        });


        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!PaginaPrincipale_sliderMusica.isValueChanging()) { // evita salti mentre l’utente trascina
                PaginaPrincipale_sliderMusica.setValue(newTime.toMillis());
                int locMinutes = (int) newTime.toMinutes();
                int locSeconds = (int) (newTime.toSeconds() % 60);
                PaginaPrincipale_minutaggioFinale.setText(String.format("%02d:%02d", (int) mediaPlayer.getMedia().getDuration().toMinutes(), (int) (mediaPlayer.getMedia().getDuration().toSeconds() % 60)));
                PaginaPrincipale_minutaggioIniziale.setText(String.format("%02d:%02d", locMinutes, locSeconds));
            }
        });

        mediaPlayer.play();

        PaginaPrincipale_buttonPlay.getStyleClass().remove("PaginaPrincipale_buttonPlay");
        PaginaPrincipale_buttonPlay.getStyleClass().add("PaginaPrincipale_buttonStop");
    }
}
