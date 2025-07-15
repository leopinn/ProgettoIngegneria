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
import java.util.ResourceBundle;

public class PaginaPrincipale implements Initializable {
    // Dichiaro gli oggetti nel metodo
    private  final ObjSql objSql = ObjSql.oggettoSql();
    private  final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();

    private PaginaPanePrincipale controller;
    private PaginaPaneCommenti commentiController;      // Per gestire i commenti in certi range
    private Parent PaginaPanePrincipaleParent;          // Mi salvo solo la pagina principale per avere continuità
    private Button pulsanteVideo;
    private List<Map<String, Object>> listaBrani, listaBraniMancanti;

    public MediaPlayer mediaPlayer;
    public int ID_CANZONE;  // ID della canzone attualmente in ascolto
    private int isCommentiNascosti=0;       //0=no, 1=si nascosti.

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
        PaginaPrincipale_stackPane.getChildren().clear();

        // Controllo se la pagina principale è già stata caricata almeno una volta.
        if(PaginaPanePrincipaleParent==null) {
            FXMLLoader loaderPrincipale = new FXMLLoader(getClass().getResource("PaginaPanePrincipale.fxml"));
            PaginaPanePrincipaleParent = loaderPrincipale.load();
            controller = loaderPrincipale.getController();
            controller.setMainController(this);
        }
            PaginaPrincipale_stackPane.getChildren().add(PaginaPanePrincipaleParent);
    }

    public void paginaUtente() throws IOException {
        // Lascio come base la pagina principale caricata
        PaginaPrincipale_stackPane.getChildren().clear();

        FXMLLoader loaderPrincipale = new FXMLLoader(getClass().getResource("PaginaPaneUtente.fxml"));
        Parent registerPane = loaderPrincipale.load();
        PaginaPaneUtente controller = loaderPrincipale.getController();
        controller.setMainController(this);

        PaginaPrincipale_stackPane.getChildren().add(registerPane);
    }

    public void impostazioni() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneImpostazioni.fxml"));
        Parent impostazioniPane = loader.load();

        PaginaPaneImpostazioni controllerImpostazioni = loader.getController();
        controllerImpostazioni.setMainController(this, PaginaPrincipale_stackPane);     // Passo anche lo stack per poterlo gestire direttamente dalle impostazioni

        // Applica sfocatura allo sfondo (pagina principale)
        Node background = PaginaPrincipale_stackPane.getChildren().getFirst();
        background.setEffect(new GaussianBlur(10));
        background.setDisable(true);        // Disabilito la schermata di sfondo

        // Aggiungo allo stack pane le impostazioni
        PaginaPrincipale_stackPane.getChildren().add(impostazioniPane);
        PaginaPrincipale_borderPane.setCenter(PaginaPrincipale_stackPane);
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        PaginaPrincipale_stackPane.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneUtente.fxml"));
        Parent utentePane = loader.load();
        PaginaPaneUtente controllerUtente = loader.getController();
        controllerUtente.setMainController(this);

        PaginaPrincipale_stackPane.getChildren().add(utentePane);

        // Apro successivamente paginaCanzone
        loader = new FXMLLoader(getClass().getResource("PaginaPaneUpload.fxml"));
        Parent canzonePane = loader.load();
        PaginaPaneUpload controllerUpload = loader.getController();
        controllerUpload.setMainController(PaginaPrincipale_stackPane, controllerUtente);

        nascondiCommenti(0);    // 0 indica la chiusura forzata da parte del programma

        // Applica sfocatura allo sfondo (paginaUtente)
        Node background = PaginaPrincipale_stackPane.getChildren().getFirst();
        background.setEffect(new GaussianBlur(10));
        background.setDisable(true);    // La pagina viene inizialmente utilizzata solo come sfondo, dunque la disabilito

        // Aggiunge canzonePane sopra
        PaginaPrincipale_stackPane.getChildren().add(canzonePane);
    }

    public void paginaCanzone() throws IOException {
        PaginaPrincipale_stackPane.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneCanzone.fxml"));
        Parent paneCanzone = loader.load();
        PaginaPaneCanzone controller = loader.getController();
        controller.setMainController(this);
        controller.mostraSchermataCanzone(ID_CANZONE);

        PaginaPrincipale_stackPane.getChildren().add(paneCanzone);
    }

    public void mostraCommenti(int id_canzone) throws IOException {
        FXMLLoader loaderCommenti = new FXMLLoader(getClass().getResource("PaginaPaneCommenti.fxml"));
        Parent registerPane = loaderCommenti.load();

        PaginaPaneCommenti controller = loaderCommenti.getController();
        commentiController = loaderCommenti.getController();
        controller.setMainController(this);
        controller.caricaCommenti(id_canzone);

        PaginaPrincipale_borderPane.setRight(registerPane);
        BorderPane.setMargin(registerPane, new Insets(0, 10, 0, 10)); // top, right, bottom, left
        isCommentiNascosti=0;
    }

    public void logout(ActionEvent event) throws IOException {
        if(mediaPlayer!=null)   // Questo nel caso in cui non fosse stata ancora avviata una musica prima del logout
            mediaPlayer.stop();

        objGenerici.logout();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaLogin.fxml"));
        Parent registerPane = loader.load();
        PaginaLogin controllerLogin = loader.getController();

        // Questo passaggio server per sostituire completamente la scena, ossia sto completamente trascrivendo il broderpane
        Scene scena = PaginaPrincipale_borderPane.getScene();
        scena.setRoot(registerPane);
    }

    /* ---------- FINE - CAMBIO SCHERMATA ----------*/


    /* Il parametro serve a capire se i commenti sono nascosti dall'utente o forzati dall'apertura di una finestra
       0=no, 1=si*/
    public void nascondiCommenti(int parIsNascostiUtente) {
        VBox locVBoxSpacer = new VBox();
        locVBoxSpacer.setPrefWidth(10);
        PaginaPrincipale_borderPane.setRight(locVBoxSpacer);

        isCommentiNascosti=parIsNascostiUtente;
    }


    public void aggiornaMusiche(String val) throws IOException {
        List<Map<String, Object>> locListaBrani = objSql.leggiLista("SELECT * FROM CANZONE WHERE (TITOLO LIKE '"
                + val.trim().replace("'", "''").toLowerCase()
                + "%' OR AUTORE LIKE '" + val.trim().replace("'", "''").toLowerCase() + "%')COLLATE NOCASE");
        controller.ricercaMusica(locListaBrani, val);
    }

    // IsCasuale=0 -> no, IsCasuale=1 -> è casuale
    public void selezionaMusica(int parId, PaginaPanePrincipale parController) throws IOException {
        impostaDatiCanzone(parId);
        riproduciCanzone(parId, parController);
        PaginaPrincipale_sliderMusica.setDisable(false);    // Abilito lo slider solo dal momento in cui parte una canzone
    }

    private void impostaDatiCanzone(int parId) throws IOException {
        // Imposto la copertina
        String locPath = ObjGenerici.ritornaCopertina(parId);
        Image locImmagine = new Image(new File(locPath).toURI().toString());

        PaginaPrincipale_imageCopertina.setImage(locImmagine);

        // Imposto bordi tondeggianti
        Rectangle locCornice = new Rectangle(60, 60);
        locCornice.setArcWidth(10);
        locCornice.setArcHeight(10);
        PaginaPrincipale_imageCopertina.setClip(locCornice);

        // Imposto titolo e autore
        Map<String, Object> locRowBrano = objSql.leggi(String.format("SELECT TITOLO, AUTORE, LINK_YOUTUBE FROM CANZONE WHERE ID_CANZONE=%s", parId));
        PaginaPrincipale_labelTitoloCanzone.setText(locRowBrano.get("TITOLO").toString());
        PaginaPrincipale_labelTitoloCanzone.setVisible(true);

        PaginaPrincipale_labelAutoreCanzone.setText(locRowBrano.get("AUTORE").toString());
        PaginaPrincipale_labelAutoreCanzone.setVisible(true);

        // Sezione commenti del brano. Se riproduzione casuale, non li mostor
        if(isCommentiNascosti==0)
            mostraCommenti(parId);

        // Aggiungi pulsante YT
        if (pulsanteVideo != null) {
            PaginaPrincipale_hBox.getChildren().remove(pulsanteVideo);
            pulsanteVideo = null;
        } else {
            pulsanteVideo = new Button("");
            pulsanteVideo.getStyleClass().add("PaginaPrincipale_bottoneYT");
            pulsanteVideo.setOnAction(e -> {
                String locUrl =locRowBrano.get("LINK_YOUTUBE").toString();

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


    /* ---------- INIZIO - RIPRODUZIONE BRANO ----------*/

    public void playStop(){
        // Controlla se è in esecuzione il player
        if(mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            // Sono costretto a rimuoverli entrambi per evitare sorprese
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

    public void riproduciCanzone(int parId, PaginaPanePrincipale parController){
        File locFile = new File("upload/musiche/"+parId+".mp3");
        if (!locFile.exists()) {
            System.out.println("File audio non trovato!");
            return;
        }
        String locPath = locFile.toURI().toString();
        Media locMedia = new Media(locPath);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();  // Libera risorse native
        }

        mediaPlayer = new MediaPlayer(locMedia);
        ID_CANZONE=parId;       // Imposto l'id della canzone come variabile d'istanza

        mediaPlayer.setOnReady(() -> {
            Duration durata = mediaPlayer.getMedia().getDuration();
            PaginaPrincipale_sliderMusica.setMax(durata.toMillis());
        });

        mediaPlayer.setOnEndOfMedia(() -> parController.riproduzioneCasuale());

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
    }

    public void avanti() throws IOException {
        controller.canzoneSucessiva();
    }

    public void indietro() throws IOException {
        controller.canzonePrecedente();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /* ---------- FINE - RIPRODUZIONE BRANO ----------*/



    /* ---------- INIZIO - GESTIONE SLIDER ----------*/

    // Questo metodo inizializza lo slider per permettere il riempimento in avanzamento musica
    private void preparazioneSlider(){
        // Preparo lo slider della musica
        PaginaPrincipale_sliderMusica.valueProperty().addListener((obs, oldVal, newVal) -> {
            double locPercentuale = newVal.doubleValue() / PaginaPrincipale_sliderMusica.getMax();
            int locPercentualeInt = (int) (locPercentuale * 100);

            String locStile = String.format(
                    "-fx-background-color: linear-gradient(to right, #6d24e1 0%%, #6d24e1 %d%%, #444444 %d%%, #444444 100%%);"
                    , locPercentualeInt, locPercentualeInt);

            PaginaPrincipale_sliderMusica.lookup(".track").setStyle(locStile);
        });

        // Preparo lo slider del volume
        PaginaPrincipale_sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            double locPercentuale = newVal.doubleValue() / PaginaPrincipale_sliderVolume.getMax();
            int locPercentualeInt = (int) (locPercentuale * 100);

            String locStile = String.format(
                    "-fx-background-color: linear-gradient(to right, #6d24e1 0%%, #6d24e1 %d%%, #444444 %d%%, #444444 100%%);"
                    , locPercentualeInt, locPercentualeInt);

            PaginaPrincipale_sliderVolume.lookup(".track").setStyle(locStile);
        });

        // Modifica del volume in tempo reale
        PaginaPrincipale_sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> mediaPlayer.setVolume(newVal.doubleValue() / 100.0));
    }

    public void sliderPressed(){
        mediaPlayer.pause();
    }

    public void sliderReleased(){
        mediaPlayer.seek(Duration.millis(PaginaPrincipale_sliderMusica.getValue()));
        mediaPlayer.play();
    }

    // Imposto il volume della musica
    public void sliderMusicaReleased(){
        mediaPlayer.setVolume(PaginaPrincipale_sliderVolume.getValue() / 100); // diviso 100 perchè il volume del player va da 0.0 a 1.0
    }

    /* ---------- FINE - GESTIONE SLIDER ----------*/
}
