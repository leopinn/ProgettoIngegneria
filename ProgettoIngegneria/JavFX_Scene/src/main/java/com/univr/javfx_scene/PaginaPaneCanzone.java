package com.univr.javfx_scene;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class PaginaPaneCanzone {
    private static ObjSql objSql = ObjSql.oggettoSql();
    private ObjGenerici objGenerici;
    private int ID_CANZONE;
    private Map<String, Object> rowCanzone;
    private PaginaPrincipale mainController;

    @FXML private ImageView PaginaPaneCanzone_copertina;
    @FXML private Label PaginaPaneCanzone_titolo, PaginaPaneCanzone_altriDati, PaginaPaneCanzone_labelYoutube;
    @FXML private HBox PaginaPaneCanzone_hBoxUp;
    @FXML private VBox boxDocumenti;
    @FXML private VBox boxMedia;
    @FXML private VBox paginaContenuto;

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    // Getter
    private String getSafe(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    //Inizializza il Pane Canzone
    public void mostraSchermataCanzone(int parIdCanzone) {
        if (parIdCanzone == 0) return;

        ID_CANZONE = parIdCanzone;
        rowCanzone = objSql.leggi("SELECT * FROM CANZONE WHERE ID_CANZONE=" + parIdCanzone);
        if (rowCanzone.isEmpty()) return;

        // Imposta immagine copertina

        // Imposto la copertina, titolo e autore
        String locPath = objGenerici.ritornaCopertina(parIdCanzone);

        Image immagine = new Image(new File(locPath).toURI().toString());
        PaginaPaneCanzone_copertina.setImage(immagine);
        PaginaPaneCanzone_copertina.setPreserveRatio(true);

        PaginaPaneCanzone_copertina.setOnMouseClicked(e -> apriImmagineIngrandita(immagine));

        // Arrotondo gli angoli della copertina
        Rectangle clip = new Rectangle(PaginaPaneCanzone_copertina.getFitWidth(), PaginaPaneCanzone_copertina.getFitHeight());
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        PaginaPaneCanzone_copertina.setClip(clip);

        PaginaPaneCanzone_titolo.setText(getSafe(rowCanzone, "TITOLO"));

        String locDatiAggiuntivi = getSafe(rowCanzone, "AUTORE") + " • " +
                getSafe(rowCanzone, "GENERE") + " • " +
                getSafe(rowCanzone, "ANNO_COMPOSIZIONE");

        PaginaPaneCanzone_altriDati.setText(locDatiAggiuntivi);

        // Imposto il colore di sfondo basandomi sul colore predominante della copertina3
        Color coloreMedio = calcolaColoreMedio();
        applicaGradiente(PaginaPaneCanzone_hBoxUp, coloreMedio);

        // Imposta larghezza massima in modo dinamico per le box documenti e file allegati (metà larghezza contenuto)
        paginaContenuto.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double halfWidth = newWidth.doubleValue() / 2;
            boxDocumenti.setMaxWidth(halfWidth);
            boxMedia.setMaxWidth(halfWidth);
        });

        // Imposta larghezza iniziale subito
        double initialWidth = paginaContenuto.getWidth();
        boxDocumenti.setMaxWidth(initialWidth / 2);
        boxMedia.setMaxWidth(initialWidth / 2);

        popolaDocumentiAllegati();
        popolaFileMultimediali();
    }

    // Utilizzato per calcolare il colore del Background
    public Color calcolaColoreMedio() {
        Image immagine = PaginaPaneCanzone_copertina.getImage();
        if (immagine == null) return Color.BLACK;

        PixelReader pixelReader = immagine.getPixelReader();
        if (pixelReader == null) return Color.BLACK;

        int width = (int) immagine.getWidth();
        int height = (int) immagine.getHeight();

        double sumRedSq = 0, sumGreenSq = 0, sumBlueSq = 0;
        int count = 0;
        final int step = 10;

        for (int y = 0; y < height; y += step) {
            for (int x = 0; x < width; x += step) {
                Color color = pixelReader.getColor(x, y);
                sumRedSq += color.getRed() * color.getRed();
                sumGreenSq += color.getGreen() * color.getGreen();
                sumBlueSq += color.getBlue() * color.getBlue();
                count++;
            }
        }

        if (count == 0) return Color.BLACK;

        double avgRed = Math.sqrt(sumRedSq / count);
        double avgGreen = Math.sqrt(sumGreenSq / count);
        double avgBlue = Math.sqrt(sumBlueSq / count);

        return new Color(avgRed, avgGreen, avgBlue, 1.0);
    }

    //Colore Background
    public void applicaGradiente(HBox box, Color baseColor) {
        LinearGradient verticalGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, baseColor.brighter()),
                new Stop(0.5, baseColor),
                new Stop(1, Color.rgb(31, 31, 31))
        );
        box.setBackground(new Background(new BackgroundFill(verticalGradient, new CornerRadii(10), Insets.EMPTY)));
    }

    // Carica l'immagine copertina del Brano
    private void apriImmagineIngrandita(Image image) {
        ImageView fullView = new ImageView(image);
        fullView.setPreserveRatio(true);
        fullView.setStyle("-fx-cursor: zoom-out;");

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double maxWidth = bounds.getWidth() * 0.9;
        double maxHeight = bounds.getHeight() * 0.9;

        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            fullView.setFitWidth(maxWidth);
            fullView.setFitHeight(maxHeight);
        }

        StackPane centerPane = new StackPane(fullView);
        centerPane.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(centerPane);
        scene.setFill(Color.BLACK);

        scene.setOnKeyPressed(e -> ((Stage) scene.getWindow()).close());

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setScene(scene);
        popup.setWidth(fullView.getFitWidth() > 0 ? fullView.getFitWidth() : imageWidth);
        popup.setHeight(fullView.getFitHeight() > 0 ? fullView.getFitHeight() : imageHeight);
        popup.centerOnScreen();
        popup.show();
    }


    // Recupera Documenti Allegatii e popola la medesima sezione
    private void popolaDocumentiAllegati() {
        File dirDoc = new File(System.getProperty("user.dir") + "/upload/pdf/");
        System.out.println("Controllo documenti in: " + dirDoc.getAbsolutePath());

        // Aggiunge file PDF, se disponibili
        if (dirDoc.exists() && dirDoc.isDirectory()) {
            File[] files = dirDoc.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".pdf") && name.startsWith(String.valueOf(ID_CANZONE))
            );

            if (files == null || files.length == 0) {
                System.out.println("Nessun file PDF per canzone " + ID_CANZONE);
                return;
            }

            for (File file : files) {
                System.out.println("File PDF: " + file.getName());

                HBox fileRow = new HBox(10);
                fileRow.setAlignment(Pos.CENTER_LEFT);

                Label nome = new Label(file.getName());
                nome.getStyleClass().add("labelBiancaMedia");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button apri = new Button();
                apri.getStyleClass().add("PaginaPaneCanzone_bottoneDocumento");
                apri.setOnAction(e -> apriFile(file));

                Button scarica = new Button();
                scarica.getStyleClass().add("PaginaPaneCanzone_bottoneDownload");
                scarica.setOnAction(e -> salvaFile(file));

                fileRow.getChildren().addAll(nome, spacer, apri, scarica);
                boxDocumenti.getChildren().add(fileRow);
            }
        } else {
            System.out.println("Cartella non trovata: " + dirDoc.getAbsolutePath());
        }
    }


    // Recupera File Multimediali e popola la medesima sezione
    private void popolaFileMultimediali() {
        File dirMedia = new File(System.getProperty("user.dir") + "/upload/musiche/");
        System.out.println("Controllo file multimediali in: " + dirMedia.getAbsolutePath());

        boolean contenutoAggiunto = false;

        // Aggiunge file mp3 e mp4, se disponibili
        if (dirMedia.exists() && dirMedia.isDirectory()) {
            File[] files = dirMedia.listFiles((dir, name) ->
                    (name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".mp4")) &&
                            name.startsWith(String.valueOf(ID_CANZONE))
            );

            if (files != null && files.length > 0) {
                for (File file : files) {
                    System.out.println("File media: " + file.getName());

                    HBox mediaRow = new HBox(10);
                    mediaRow.setAlignment(Pos.CENTER_LEFT);

                    Label label = new Label(file.getName());
                    label.getStyleClass().add("labelBiancaMedia");
                    Region spacer = new Region();;
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button play = new Button();
                    play.getStyleClass().add("PaginaPaneCanzone_bottonePlay");
                    play.setOnAction(e -> apriFile(file));

                    Button download = new Button();
                    download.getStyleClass().add("PaginaPaneCanzone_bottoneDownload");
                    download.setOnAction(e -> salvaFile(file));

                    mediaRow.getChildren().addAll(label, spacer, play, download);
                    boxMedia.getChildren().add(mediaRow);
                    contenutoAggiunto = true;
                }
            }
        }

        // Aggiunge link YouTube, se disponibile
        String linkYoutube = getSafe(rowCanzone, "LINK_YOUTUBE");
        if (!linkYoutube.isEmpty()) {
            HBox youtubeRow = new HBox(10);
            youtubeRow.setAlignment(Pos.CENTER_LEFT);

            Label label = new Label("YouTube");
            label.getStyleClass().add("labelBiancaMedia");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button apriYoutube = new Button();
            apriYoutube.getStyleClass().add("PaginaPaneCanzone_bottoneYoutube");
            apriYoutube.setOnAction(e -> apriLink(linkYoutube));

            youtubeRow.getChildren().addAll(label, spacer, apriYoutube);
            boxMedia.getChildren().add(youtubeRow);
            contenutoAggiunto = true;
        }

        if (!contenutoAggiunto) {
            System.out.println("Nessun file multimediale o link YouTube per canzone " + ID_CANZONE);
        }
    }


    //Apre i link
    @FXML
    private void apriLink(String url) {
        if (url == null || url.trim().isEmpty()) return;

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nell'apertura del link!");
            }
        } else {
            ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Desktop non supportato, impossibile aprire il browser!");
        }
    }

    //Apre i file
    private void apriFile(File file) {
        try {
            if (file.exists()) Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
            ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nell'apertura:\n" + file.getAbsolutePath());
        }
    }


    //Salva il file dove vuole l'utente
    private void salvaFile(File sorgente) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva file come...");
        fileChooser.setInitialFileName(sorgente.getName());

        // Estensione del file sorgente
        String estensione = getEstensione(sorgente.getName());

        if (!estensione.isEmpty()) {
            // Aggiungi filtro specifico (es. PDF, MP3, MP4)
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(estensione.toUpperCase() + " files", "*." + estensione)
            );
        }

        // Filtro generico per tutti i file
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );

        // Mostra la finestra di salvataggio
        File destinazione = fileChooser.showSaveDialog(null);

        if (destinazione != null) {
            try {
                java.nio.file.Files.copy(
                        sorgente.toPath(),
                        destinazione.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException ex) {
                ex.printStackTrace();
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube,
                        "Errore durante il salvataggio del file:\n" + ex.getMessage());
            }
        }
    }


    //Ottenere l’estensione file
    private String getEstensione(String nomeFile) {
        int punto = nomeFile.lastIndexOf('.');
        if (punto > 0 && punto < nomeFile.length() - 1) {
            return nomeFile.substring(punto + 1).toLowerCase();
        }
        return "";
    }

    // Gestione del caricamento dei Documenti inseriti dall'utente
    @FXML
    private void caricaDocumento() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona documento PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File PDF", "*.pdf", "*.txt", "*.doc"));

        File fileScelto = fileChooser.showOpenDialog(null);
        if (fileScelto != null) {
            File destinazione = new File(System.getProperty("user.dir") + "/upload/pdf/" + ID_CANZONE + "_" + fileScelto.getName());
            try {
                java.nio.file.Files.copy(fileScelto.toPath(), destinazione.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                popolaDocumentiAllegati();
            } catch (IOException ex) {
                ex.printStackTrace();
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nel caricamento del documento:\n" + ex.getMessage());
            }
        }
    }


    // Gestione del caricamento del File inserito dall'utente
    @FXML
    private void caricaMediaFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file multimediale");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio e Video", "*.mp3", "*.mp4", "*.midi")
        );

        File fileScelto = fileChooser.showOpenDialog(null);
        if (fileScelto != null) {
            File destinazione = new File(System.getProperty("user.dir") + "/upload/musiche/" + ID_CANZONE + "_" + fileScelto.getName());
            try {
                java.nio.file.Files.copy(fileScelto.toPath(), destinazione.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                popolaFileMultimediali();
            } catch (IOException ex) {
                ex.printStackTrace();
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nel caricamento del file multimediale:\n" + ex.getMessage());
            }
        }
    }
}