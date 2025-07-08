package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.ModificaCanzone;
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

    @FXML
    private ImageView PaginaPaneCanzone_copertina;
    @FXML
    private Label PaginaPaneCanzone_titolo, PaginaPaneCanzone_altriDati, PaginaPaneCanzone_labelYoutube;
    @FXML
    private HBox PaginaPaneCanzone_hBoxUp;
    @FXML
    private VBox paginaContenuto; // Assicurati che esista nel tuo FXML
    //@FXML private Button btnDownloadSpartito;
    @FXML private VBox boxDocumenti;
    @FXML private VBox boxMedia;

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

        popolaDocumentiAllegati();
        popolaFileMultimediali();
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


    // Recupera Documenti Allegatii
    private void popolaDocumentiAllegati() {
        File dirDoc = new File(System.getProperty("user.dir") + "/upload/pdf/");
        System.out.println("Controllo documenti in: " + dirDoc.getAbsolutePath());

        if (dirDoc.exists() && dirDoc.isDirectory()) {
            File[] files = dirDoc.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".pdf") && name.startsWith(String.valueOf(ID_CANZONE))
            );

            if (files == null || files.length == 0) {
                System.out.println("⚠️ Nessun file PDF per canzone " + ID_CANZONE);
                return;
            }

            for (File file : files) {
                System.out.println("📄 File PDF: " + file.getName());

                HBox fileRow = new HBox(10);
                fileRow.setAlignment(Pos.CENTER_LEFT);

                Label nome = new Label(file.getName());
                nome.setStyle("-fx-text-fill: white;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button apri = new Button("📄");
                apri.setOnAction(e -> apriFile(file));

                Button scarica = new Button("⬇");
                scarica.setOnAction(e -> apriFile(file));

                fileRow.getChildren().addAll(nome, spacer, apri, scarica);
                boxDocumenti.getChildren().add(fileRow);
            }
        } else {
            System.out.println("❌ Cartella non trovata: " + dirDoc.getAbsolutePath());
        }
    }


    // Recupera File Multimediali
    private void popolaFileMultimediali() {
        File dirMedia = new File(System.getProperty("user.dir") + "/upload/musiche/");
        System.out.println("Controllo file multimediali in: " + dirMedia.getAbsolutePath());

        if (dirMedia.exists() && dirMedia.isDirectory()) {
            File[] files = dirMedia.listFiles((dir, name) ->
                    (name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".mp4")) &&
                            name.startsWith(String.valueOf(ID_CANZONE))
            );

            if (files == null || files.length == 0) {
                System.out.println("⚠️ Nessun file multimediale per canzone " + ID_CANZONE);
                return;
            }

            for (File file : files) {
                System.out.println("🎵 File media: " + file.getName());

                HBox mediaRow = new HBox(10);
                mediaRow.setAlignment(Pos.CENTER_LEFT);

                Label label = new Label(file.getName());
                label.setStyle("-fx-text-fill: white;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button play = new Button("▶");
                play.setOnAction(e -> apriFile(file));

                Button download = new Button("⬇");
                download.setOnAction(e -> apriFile(file));

                mediaRow.getChildren().addAll(label, spacer, play, download);
                boxMedia.getChildren().add(mediaRow);
            }
        } else {
            System.out.println("❌ Cartella non trovata: " + dirMedia.getAbsolutePath());
        }
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

    @FXML
    private void downloadSpartito() {
        try {
            // Percorso reale del file da scaricare/aprire
            File file = new File("canzoni/" + ID_CANZONE + "/spartito.pdf"); // <-- sostituisci con percorso corretto

            if (file.exists()) {
                Desktop.getDesktop().open(file); // apre il file con il programma predefinito
            } else {
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "File spartito non trovato:\n" + file.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
            ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nell'apertura del file spartito.");
        }
    }

    @FXML
    private void openSpartito() {
        // Esempio di apertura file PDF
        try {
            File file = new File("canzoni/" + ID_CANZONE + "/spartito.pdf");// <-- modifica percorso
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "File non trovato: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nell'apertura del file PDF");
        }
    }

    private void apriFile(File file) {
        try {
            if (file.exists()) Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
            ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube, "Errore nell'apertura:\n" + file.getAbsolutePath());
        }
    }


/*
    @FXML
    public void initialize() {
        btnDownloadSpartito.setOnAction(e -> downloadSpartito());
    } */
}