package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

public class PaginaPrincipale implements Initializable {
    private  ObjSql objSql = ObjSql.oggettoSql();
    private  final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();

    private PaginaPanePrincipale controller;
    public MediaPlayer mediaPlayer;
    public int ID_CANZONE;  // ID della canzone attualmente in ascolto
    private int isCommentiNascosti=0;       //0=no, 1=si nascosti.

    private PaginaPaneCommenti commentiController;      // Per gestire i commenti in certi range
    private Parent PaginaPanePrincipaleParent;          // per impilarci sopra altre schermate

    @FXML private BorderPane PaginaPrincipale_borderPane;
    @FXML private ImageView PaginaPrincipale_imageCopertina;
    @FXML private Label PaginaPrincipale_labelTitoloCanzone;
    @FXML private Label PaginaPrincipale_labelAutoreCanzone,PaginaPrincipale_minutaggioIniziale, PaginaPrincipale_minutaggioFinale;
    @FXML private Slider PaginaPrincipale_sliderMusica, PaginaPrincipale_sliderVolume;
    @FXML private Button PaginaPrincipale_buttonPlay;
    @FXML private TextField cercaTextField;
    @FXML private HBox PaginaPrincipale_hBox;
    @FXML private Button PaginaPrincipale_bottoneHome;
    @FXML private StackPane PaginaPrincipale_stackPane;

    private Button pulsanteVideo;
    private List<Map<String, Object>> listaBrani, listaBraniMancanti;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            paginaPrincipale();

            // Ricerca canzone in tempo reale
            cercaTextField.textProperty().addListener((obs, oldValue, newValue) -> {
                try {
                    aggiornaMusiche(newValue);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            preparazioneSlider();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /* ---------- INIZIO - CAMBIO SCHERMATA ----------*/

    public void paginaPrincipale() throws IOException {
        PaginaPrincipale_stackPane.getChildren().clear();   // Ogni volta che torno in home pulisco lo stack pane

        FXMLLoader loaderPrincipale = new FXMLLoader(getClass().getResource("PaginaPanePrincipale.fxml"));
        PaginaPanePrincipaleParent = loaderPrincipale.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        controller = loaderPrincipale.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_stackPane.getChildren().add(PaginaPanePrincipaleParent);
        PaginaPrincipale_borderPane.setCenter(PaginaPrincipale_stackPane);
    }

    public void paginaUtente() throws IOException {
        FXMLLoader loaderPrincipale = new FXMLLoader(getClass().getResource("PaginaPaneUtente.fxml"));
        Parent registerPane = loaderPrincipale.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPaneUtente controller = loaderPrincipale.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
    }

    public void impostazioni() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneImpostazioni.fxml"));
        Parent impostazioniPane = loader.load();

        PaginaPaneImpostazioni controllerImpostazioni = loader.getController();
        controllerImpostazioni.setMainController(this, PaginaPrincipale_stackPane);  // Passaggio chiave

        // Applica sfocatura allo sfondo (pagina principale)
        Node background = PaginaPrincipale_stackPane.getChildren().get(0);
        background.setEffect(new GaussianBlur(10));
        background.setDisable(true);        // Disabilito la schermata di sfondo

        // Aggiunge impostazioniPane sopra
        PaginaPrincipale_stackPane.getChildren().add(impostazioniPane);
        PaginaPrincipale_borderPane.setCenter(PaginaPrincipale_stackPane);
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        // Imposto al centro dello stack pane paginaUtente, come sfondo
        PaginaPrincipale_stackPane.getChildren().clear();   // Ogni volta che chiamo upload, meglio pulire lo stack

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneUtente.fxml"));
        Parent utentePane = loader.load();

        PaginaPaneUtente controllerUtente = loader.getController();
        controllerUtente.setMainController(this);

        PaginaPrincipale_stackPane.getChildren().setAll(utentePane);

        // Apro successivamente paginaCanzone
        loader = new FXMLLoader(getClass().getResource("PaginaPaneUpload.fxml"));
        Parent canzonePane = loader.load();

        PaginaPaneUpload controllerUpload = loader.getController();
        controllerUpload.setMainController(PaginaPrincipale_stackPane, controllerUtente);

        nascondiCommenti();

        // Applica sfocatura allo sfondo (paginaUtente)
        Node background = PaginaPrincipale_stackPane.getChildren().get(0);
        background.setEffect(new GaussianBlur(10));
        background.setDisable(true);    // La pagina viene inizialmente utilizzata solo come sfondo, dunque la disabilito

        // Aggiunge canzonePane sopra
        PaginaPrincipale_stackPane.getChildren().add(canzonePane);
        PaginaPrincipale_borderPane.setCenter(PaginaPrincipale_stackPane);

    }

    public void PaginaPaneImpostazioniAmministratore() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneImpostazioniAmministratore.fxml"));
        Parent registerPane = loader.load();

        PaginaPaneImpostazioniAmministratore controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
    }

    public void paginaCanzone() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneCanzone.fxml"));
        Parent paneCanzone = loader.load();

        PaginaPaneCanzone controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave
        controller.mostraSchermataCanzone(ID_CANZONE);

        PaginaPrincipale_borderPane.setCenter(paneCanzone);
    }

    public void mostraCommenti(int id_canzone) throws IOException {
        FXMLLoader loaderCommenti = new FXMLLoader(getClass().getResource("PaginaPaneCommenti.fxml"));
        Parent registerPane = loaderCommenti.load();

        PaginaPaneCommenti controller = loaderCommenti.getController();

        commentiController = loaderCommenti.getController();
        controller.setMainController(this);  // <<< passaggio chiave
        controller.caricaCommenti(id_canzone);

        PaginaPrincipale_borderPane.setRight(registerPane);
        BorderPane.setMargin(registerPane, new Insets(0, 10, 0, 10)); // top, right, bottom, left
        isCommentiNascosti=0;
    }

    public void nascondiCommenti() {
        VBox locVBoxSpacer = new VBox();
        locVBoxSpacer.setPrefWidth(10);
        PaginaPrincipale_borderPane.setRight(locVBoxSpacer);
        isCommentiNascosti=1;
    }

    /* ---------- FINE - CAMBIO SCHERMATA ----------*/



    public void aggiornaMusiche(String val) throws IOException {
        List<Map<String, Object>> listaBrani = objSql.leggiLista("SELECT * FROM CANZONE WHERE (TITOLO LIKE '"
                + val.trim().replace("'", "''").toLowerCase()
                + "%' OR AUTORE LIKE '" + val.trim().replace("'", "''").toLowerCase() + "%')COLLATE NOCASE");
        controller.ricercaMusica(listaBrani, val);
    }

    public void logout(ActionEvent event) throws IOException {
        if(mediaPlayer!=null)   // Questo nel caso in cui non fosse stata ancora avviata una musica prima del logout
            mediaPlayer.stop();

        Parent root = FXMLLoader.load(getClass().getResource("PaginaLogin.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();  // Mi recupero lo stage corrente

        double locWidth = stage.getWidth();
        double locHeight = stage.getHeight();

        // Richiamo la scena con le dimensioni correnti
        Scene scene = new Scene(root, locWidth, locHeight);
        stage.setScene(scene);
        stage.show();
    }

    public void selezionaMusica(int parId, PaginaPanePrincipale controller) throws IOException {
        impostaDatiCanzone(parId);
        riproduciCanzone(parId, controller);
        PaginaPrincipale_sliderMusica.setDisable(false);    // Abilito lo slider solo dal momento in cui parte una canzone
    }

    private void impostaDatiCanzone(int parId) throws IOException {
        // Imposto la copertina
        String locPath = ObjGenerici.ritornaCopertina(parId);
        Image immagine = new Image(new File(locPath).toURI().toString());

        PaginaPrincipale_imageCopertina.setImage(immagine);

        // Imposto bordi tondeggianti
        Rectangle clip = new Rectangle(60, 60);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        PaginaPrincipale_imageCopertina.setClip(clip);

        // Imposto titolo e autore
        Map<String, Object> rowBrano = objSql.leggi(String.format("SELECT TITOLO, AUTORE, LINK_YOUTUBE FROM CANZONE WHERE ID_CANZONE=%s", parId));
        PaginaPrincipale_labelTitoloCanzone.setText(rowBrano.get("TITOLO").toString());
        PaginaPrincipale_labelTitoloCanzone.setVisible(true);

        PaginaPrincipale_labelAutoreCanzone.setText(rowBrano.get("AUTORE").toString());
        PaginaPrincipale_labelAutoreCanzone.setVisible(true);

        // Sezione commenti del brano. Se riproduzione casuale, non li mostor
        if(isCommentiNascosti==0)
            mostraCommenti(parId);

        // Aggiungi pulsante YT
        if (pulsanteVideo != null) {
            PaginaPrincipale_hBox.getChildren().remove(pulsanteVideo);
            pulsanteVideo = null;
        }
        if (pulsanteVideo == null) {
            pulsanteVideo = new Button("");
            pulsanteVideo.getStyleClass().add("PaginaPrincipale_bottoneYT");
            pulsanteVideo.setOnAction(e -> {
                String locUrl =rowBrano.get("LINK_YOUTUBE").toString();

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(locUrl));
                    } catch (IOException | URISyntaxException err) {
                        objGenerici.mostraPopupErrore(PaginaPrincipale_borderPane, "Attenzione!! Errore nella apertura del browser");
                    }
                } else {
                    objGenerici.mostraPopupErrore(PaginaPrincipale_borderPane, "Attenzione!! Errore nella apertura del browser");
                }
            });

            HBox.setMargin(pulsanteVideo, new Insets(0, 0, 0, 5));
            int indexHome = PaginaPrincipale_hBox.getChildren().indexOf(PaginaPrincipale_bottoneHome);
            PaginaPrincipale_hBox.getChildren().add(indexHome + 1, pulsanteVideo);
        }
    }

    public void playStop(){
        // Controlla se è in esecuzione
        if(mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            PaginaPrincipale_buttonPlay.getStyleClass().remove("PaginaPrincipale_buttonStop");
            PaginaPrincipale_buttonPlay.getStyleClass().remove("PaginaPrincipale_buttonPlay");

            PaginaPrincipale_buttonPlay.getStyleClass().add("PaginaPrincipale_buttonPlay");
            mediaPlayer.pause();
        } else {
            PaginaPrincipale_buttonPlay.getStyleClass().remove("PaginaPrincipale_buttonStop");
            PaginaPrincipale_buttonPlay.getStyleClass().remove("PaginaPrincipale_buttonPlay");

            PaginaPrincipale_buttonPlay.getStyleClass().add("PaginaPrincipale_buttonStop");
            mediaPlayer.play();
            mediaPlayer.setVolume(PaginaPrincipale_sliderVolume.getValue() / 100);
        }
    }

    public void riproduciCanzone(int parId, PaginaPanePrincipale controller){
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
        ID_CANZONE=parId;

        mediaPlayer.setOnReady(() -> {
            Duration durata = mediaPlayer.getMedia().getDuration();
            PaginaPrincipale_sliderMusica.setMax(durata.toMillis());
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            controller.riproduzioneCasuale();
        });

        // Metodo chiamato ogni secondo, in modo da aggioranre i vari valori continuamente
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!PaginaPrincipale_sliderMusica.isValueChanging()) { // evita salti mentre l’utente trascina
                PaginaPrincipale_sliderMusica.setValue(newTime.toMillis());
                int locMinutes = (int) newTime.toMinutes();
                int locSeconds = (int) (newTime.toSeconds() % 60);
                PaginaPrincipale_minutaggioFinale.setText(String.format("%02d:%02d", (int) mediaPlayer.getMedia().getDuration().toMinutes(), (int) (mediaPlayer.getMedia().getDuration().toSeconds() % 60)));
                PaginaPrincipale_minutaggioIniziale.setText(String.format("%02d:%02d", locMinutes, locSeconds));

                // Chiama il metodo per controllare se a questo istante parte un commento in minutaggio
                if (commentiController != null) {
                    commentiController.controllaRangeCommento((int) newTime.toSeconds());
                }
            }
        });

        mediaPlayer.play();
        mediaPlayer.setVolume(PaginaPrincipale_sliderVolume.getValue() / 100);

        playStop();
        System.out.println(PaginaPrincipale_buttonPlay);
    }

    public void avanti() throws IOException {
        controller.canzoneSucessiva();
    }

    public void indietro() throws IOException {
        controller.canzonePrecedente();
    }

    // Questo metodo inizializza lo slider per permettere il riempimento in avanzamento musica
    private void preparazioneSlider(){
        PaginaPrincipale_sliderMusica.valueProperty().addListener((obs, oldVal, newVal) -> {
            double locPercentuale = newVal.doubleValue() / PaginaPrincipale_sliderMusica.getMax();
            int locPercentualeInt = (int) (locPercentuale * 100);

            String style = String.format(
                    "-fx-background-color: linear-gradient(to right, #6d24e1 0%%, #6d24e1 %d%%, #444444 %d%%, #444444 100%%);"
                    , locPercentualeInt, locPercentualeInt);

            PaginaPrincipale_sliderMusica.lookup(".track").setStyle(style);
        });

        PaginaPrincipale_sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            double locPercentuale = newVal.doubleValue() / PaginaPrincipale_sliderVolume.getMax();
            int locPercentualeInt = (int) (locPercentuale * 100);

            String style = String.format(
                    "-fx-background-color: linear-gradient(to right, #6d24e1 0%%, #6d24e1 %d%%, #444444 %d%%, #444444 100%%);"
                    , locPercentualeInt, locPercentualeInt);

            PaginaPrincipale_sliderVolume.lookup(".track").setStyle(style);
        });

        // Modifica del volume in tempo reale
        PaginaPrincipale_sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
        });
    }

    public void sliderPressed(){
        mediaPlayer.pause();
    }

    public void sliderReleased(){
        mediaPlayer.seek(Duration.millis(PaginaPrincipale_sliderMusica.getValue()));
        mediaPlayer.play();
    }

    // Imposta il volume della musica
    public void sliderMusicaReleased(){
        mediaPlayer.setVolume(PaginaPrincipale_sliderVolume.getValue() / 100); // diviso 100 perchè il volume del player va da 0.0 a 1.0
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
