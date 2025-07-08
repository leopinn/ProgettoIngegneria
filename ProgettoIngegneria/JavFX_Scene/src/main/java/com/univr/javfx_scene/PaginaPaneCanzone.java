package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.ModificaCanzone;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    @FXML private ImageView PaginaPaneCanzone_copertina;
    @FXML private Label PaginaPaneCanzone_titolo, PaginaPaneCanzone_altriDati, PaginaPaneCanzone_labelYoutube;
    @FXML private HBox PaginaPaneCanzone_hBoxUp;
    @FXML private VBox paginaContenuto; // Assicurati che esista nel tuo FXML

    private PaginaPrincipale mainController;

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    private String getSafe(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    public void mostraSchermataCanzone(int parIdCanzone) {
        if (parIdCanzone == 0) return;

        ID_CANZONE = parIdCanzone;
        rowCanzone = objSql.leggi("SELECT * FROM CANZONE WHERE ID_CANZONE=" + parIdCanzone);
        if (rowCanzone.isEmpty()) return;

        // Imposta immagine copertina

        // Imposto la copertina, titolo e autore
        String locPath =objGenerici.ritornaCopertina(parIdCanzone);

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
        PaginaPaneCanzone_labelYoutube.setText(getSafe(rowCanzone, "LINK_YOUTUBE"));

        // Imposto il colore di sfondo basandomi sul colore predominante della copertina3
        Color coloreMedio = calcolaColoreMedio();
        applicaGradiente(PaginaPaneCanzone_hBoxUp, coloreMedio);

        // ===================== NUOVE SEZIONI =========================

        VBox contenitoreExtra = new VBox(15);
        contenitoreExtra.setPadding(new Insets(20));

        // Documenti Allegati
        Label labelDocumenti = new Label("Documenti Allegati");
        Button btnCaricaDocumento = new Button("Carica Documento");
        btnCaricaDocumento.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleziona Documento");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Documenti", "*.pdf", "*.docx", "*.txt")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                System.out.println("Documento caricato: " + file.getName());
                // TODO: gestisci salvataggio o visualizzazione
            }
        });

        // File Multimediali
        Label labelMedia = new Label("File Multimediali");
        Button btnCaricaMedia = new Button("Carica MP3/MP4");
        btnCaricaMedia.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleziona File Multimediale");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Audio/Video", "*.mp3", "*.mp4")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                System.out.println("File multimediale caricato: " + file.getName());
                // TODO: gestisci salvataggio o visualizzazione
            }
        });

        Label labelYoutube = new Label("Link YouTube:");
        TextField campoYoutube = new TextField();
        campoYoutube.setPromptText("Inserisci link YouTube");

        // Note sui documenti
        Label labelNote = new Label("Note sui documenti");
        TextArea areaNote = new TextArea();
        areaNote.setPromptText("Scrivi qui i tuoi commenti...");
        areaNote.setWrapText(true);




        // Aggiunta al contenitore
        contenitoreExtra.getChildren().addAll(
                labelDocumenti, btnCaricaDocumento,
                labelMedia, btnCaricaMedia, labelYoutube, campoYoutube,
                labelNote, areaNote
        );

        // Aggiunta al contenitore principale della pagina
        paginaContenuto.getChildren().add(contenitoreExtra);
    }

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

    public void applicaGradiente(HBox box, Color baseColor) {
        LinearGradient verticalGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, baseColor.brighter()),
                new Stop(0.5, baseColor),
                new Stop(1, Color.rgb(31, 31, 31))
        );
        box.setBackground(new Background(new BackgroundFill(verticalGradient, new CornerRadii(10), Insets.EMPTY)));
    }

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

    @FXML
    private void apriLinkYoutube() {
        String locUrl = rowCanzone.get("LINK_YOUTUBE").toString();
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(locUrl));
            } catch (IOException | URISyntaxException e) {
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nell'apertura del link!");
            }
        } else {
            ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Desktop non supportato, impossibile aprire il browser!");
        }
    }
}
