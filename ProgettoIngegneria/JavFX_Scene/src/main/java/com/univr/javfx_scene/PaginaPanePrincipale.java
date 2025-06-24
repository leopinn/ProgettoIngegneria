package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.stage.Popup;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.stage.DirectoryChooser;


public class PaginaPanePrincipale implements Initializable {

    private  ObjSql objSql = ObjSql.oggettoSql();
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    @FXML
    private TilePane PaginaPanePrincipale_grigliaMusiche;
    @FXML private ScrollPane PaginaPrincipale_scrollPane;


    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PaginaPanePrincipale_grigliaMusiche.prefWidthProperty().bind(PaginaPrincipale_scrollPane.widthProperty());

        List<Map<String, Object>> listaBrani = objSql.leggiLista("SELECT * FROM CANZONE");
        for (Map<String, Object> brano : listaBrani) {
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER);
            card.setStyle("""
        -fx-background-color: #1e1e1e;
        -fx-background-radius: 15;
        -fx-padding: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);
    """);
            card.setPrefWidth(150);
            card.setPrefHeight(150);
            card.setId(Integer.toString((Integer) brano.get("ID_CANZONE")));

            card.setOnMouseClicked(event -> {
                try {
                    selezionaMusica(card.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Crea menu contestuale (tasto destro)
            ContextMenu contextMenu = new ContextMenu();
            MenuItem downloadItem = new MenuItem("Download");

            downloadItem.setOnAction(e -> {
                int idCanzone = Integer.parseInt(card.getId());
                scaricaFileCanzone(idCanzone);
            });

            contextMenu.getItems().add(downloadItem);

            card.setOnContextMenuRequested(e -> {
                contextMenu.show(card, e.getScreenX(), e.getScreenY());
            });

            String locPath = "upload/copertine/" + brano.get("ID_CANZONE") + ".jpg";
            if(!new File(locPath).exists())         // Controllo se eventualmente è un PNG
                locPath = "upload/copertine/" + brano.get("ID_CANZONE") + ".png";

            Image immagine = new Image(new File(locPath).toURI().toString());
            ImageView copertina = new ImageView(immagine);
            copertina.setFitWidth(120);
            copertina.setFitHeight(120);
            copertina.setPreserveRatio(true);

            Label titolo = new Label((String) brano.get("TITOLO"));
            titolo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            titolo.setWrapText(true);
            titolo.setAlignment(Pos.CENTER);

            Label autore = new Label((String) brano.get("AUTORE"));
            autore.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
            autore.setWrapText(true);
            autore.setAlignment(Pos.CENTER);

            card.getChildren().addAll(copertina, titolo, autore);
            PaginaPanePrincipale_grigliaMusiche.getChildren().add(card);
        }
    }

    private void selezionaMusica(String parId) throws IOException {
        mainController.selezionaMusica(Integer.parseInt(parId));
    }

    private void mostraPopup(String messaggio) {
        Label contenuto = new Label(messaggio);
        contenuto.setStyle("""
        -fx-background-color: #28a745;
        -fx-text-fill: white;
        -fx-padding: 12px 24px;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);
    """);

        StackPane container = new StackPane(contenuto);
        container.setStyle("-fx-padding: 10;");
        container.setOpacity(0);

        Popup popup = new Popup();
        popup.getContent().add(container);
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        // Ottieni la finestra corrente
        Window finestra = PaginaPrincipale_scrollPane.getScene().getWindow();

        // Mostra il popup
        popup.show(finestra);

        // Posizionalo in basso al centro della finestra
        popup.setX(finestra.getX() + finestra.getWidth() / 2 - 100); // centrato orizzontalmente
        popup.setY(finestra.getY() + finestra.getHeight() - 100);    // 100px dal fondo

        // Animazione fade-in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animazione fade-out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), container);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2.5));
        fadeOut.setOnFinished(e -> popup.hide());

        // Avvia animazioni
        fadeIn.play();
        fadeOut.play();
    }

    // in questa funzione viene gestito il download da parte dell'utente dei file che compngono il brano
    /**
     * Scarica i file associati a una canzone (audio, testo, video) in una cartella scelta dall'utente.
     * I file vengono rinominati con il titolo del brano e salvati in una sottocartella con lo stesso nome.
     *
     * @param idCanzone ID della canzone nel database
     */
    private void scaricaFileCanzone(int idCanzone) {
        String basePath = "upload/"; // Cartella locale dei file sorgenti

        // Percorsi dei file sorgente (basati sull'ID della canzone)
        File audioFile = new File(basePath + "musiche/" + idCanzone + ".mp3");
        File testoFile = new File(basePath + "pdf/" + idCanzone + ".txt");
        File videoFile = new File(basePath + "video/" + idCanzone + ".mp4");

        // Recupera il titolo del brano dal database
        String titoloBrano = "Brano";
        List<Map<String, Object>> risultato = objSql.leggiLista(
                "SELECT TITOLO FROM CANZONE WHERE ID_CANZONE = " + idCanzone
        );
        if (!risultato.isEmpty()) {
            titoloBrano = (String) risultato.get(0).get("TITOLO");
        }

        // Finestra di selezione cartella per far scegliere all'utente dove salvare
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Scegli cartella per salvare: " + titoloBrano);
        File targetFolder = chooser.showDialog(PaginaPrincipale_scrollPane.getScene().getWindow());

        if (targetFolder == null) {
            return; // Utente ha annullato la selezione
        }

        // Crea una sottocartella con il nome del brano (sanificando caratteri illegali)
        String safeName = titoloBrano.replaceAll("[\\\\/:*?\"<>|]", "_");
        File destinazioneBrano = new File(targetFolder, safeName);
        destinazioneBrano.mkdirs(); // Crea la cartella se non esiste

        try {
            // Copia e rinomina i file solo se esistono
            if (audioFile.exists()) {
                copiaFile(audioFile, new File(destinazioneBrano, safeName + ".mp3"));
            }

            if (testoFile.exists()) {
                copiaFile(testoFile, new File(destinazioneBrano, safeName + ".txt"));
            }

            if (videoFile.exists()) {
                copiaFile(videoFile, new File(destinazioneBrano, safeName + ".mp4"));
            }

            // Mostra notifica popup di conferma
            mostraPopup("Download completato: " + titoloBrano);

        } catch (IOException e) {
            // Errore durante la copia dei file
            e.printStackTrace();
            mostraPopup("Errore durante il download di: " + titoloBrano);
        }
    }

    /**
     * Copia un file dalla posizione sorgente alla destinazione scelta dall'utente.
     * Se il file sorgente non esiste, stampa un messaggio di errore.
     *
     * @param sorgente      Il file da copiare (es. .mp3, .txt, .mp4, ecc.)
     * @param destinazione  Il percorso dove il file verrà copiato
     */
    private void copiaFile(File sorgente, File destinazione) throws IOException {
        if (sorgente.exists()) {
            java.nio.file.Files.copy(
                    sorgente.toPath(),
                    destinazione.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
        } else {
            // Se il file sorgente non esiste, stampa un messaggio in console
            System.out.println("File non trovato: " + sorgente.getAbsolutePath());
        }
    }
}
