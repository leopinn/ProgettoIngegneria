package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.CANZONE;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PaginaPaneCanzone {

    private static ObjSql objSql = ObjSql.oggettoSql();
    private Map<String, Object> rowCanzone;
    private List<Map<String, Object>> locListaCanzoni;
    @FXML private ImageView PaginaPaneCanzone_copertina;
    @FXML private Label PaginaPaneCanzone_titolo, PaginaPaneCanzone_altriDati, PaginaPaneCanzone_labelYoutube;
    @FXML private HBox PaginaPaneCanzone_hBoxUp;
    @FXML private TableView<CANZONE> PaginaPaneCanzone_tabelView;
    @FXML private VBox formModifica;
    @FXML private TextField fieldTitolo, fieldAutore, fieldGenere, fieldAnno, fieldYoutube;

    @FXML
    public void initialize() {
        formModifica.setVisible(false);
        formModifica.setManaged(false);
        popolaLista();
    }

    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    public void mostraSchermataCanzone(int parIdCanzone){
        // Non dovrebbe mai accedere
        if(parIdCanzone == 0) { return; }

        rowCanzone = objSql.leggi("SELECT * FROM CANZONE WHERE ID_CANZONE="+parIdCanzone);
        if(rowCanzone.isEmpty()) { return; }

        // Imposto la copertina, titolo e autore
        String locPath = "upload/copertine/" + parIdCanzone + ".jpg";
        if(!new File(locPath).exists())         // Controllo se eventualmente è un PNG
            locPath = "upload/copertine/" + parIdCanzone + ".png";

        Image immagine = new Image(new File(locPath).toURI().toString());
        PaginaPaneCanzone_copertina.setImage(immagine);
        PaginaPaneCanzone_copertina.setPreserveRatio(true);

        PaginaPaneCanzone_copertina.setOnMouseClicked(e -> apriImmagineIngrandita(immagine));

        // Arrotondo gli angoli della copertina
        Rectangle clip = new Rectangle(PaginaPaneCanzone_copertina.getFitWidth(), PaginaPaneCanzone_copertina.getFitHeight());
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        PaginaPaneCanzone_copertina.setClip(clip);

        PaginaPaneCanzone_titolo.setText(rowCanzone.get("TITOLO").toString());
        String locDatiAggiuntivi = rowCanzone.get("AUTORE").toString();
        locDatiAggiuntivi = locDatiAggiuntivi + " • " + rowCanzone.get("GENERE").toString();
        locDatiAggiuntivi = locDatiAggiuntivi + " • " + rowCanzone.get("ANNO_COMPOSIZIONE").toString();

        PaginaPaneCanzone_altriDati.setText(locDatiAggiuntivi);

        PaginaPaneCanzone_labelYoutube.setText(rowCanzone.get("LINK_YOUTUBE").toString());

        // Imposto il colore di sfondo basandomi sul colore predominante della copertina3
        Color coloreMedio = calcolaColoreMedio();
        applicaGradiente(PaginaPaneCanzone_hBoxUp, coloreMedio);
    }


    /* ---------- Inizio - gestione colori ----------*/

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

        // Calcola la radice quadrata della media dei quadrati (RMS)
        double avgRed = Math.sqrt(sumRedSq / count);
        double avgGreen = Math.sqrt(sumGreenSq / count);
        double avgBlue = Math.sqrt(sumBlueSq / count);

        return new Color(avgRed, avgGreen, avgBlue, 1.0);
    }



    public void applicaGradiente(HBox box, Color baseColor) {
        double radius = 10;

        LinearGradient verticalGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, baseColor.brighter()),
                new Stop(0.5, baseColor),
                new Stop(1, baseColor.darker().darker().darker().darker())
        );

        CornerRadii radii = new CornerRadii(10);

        box.setBackground(new Background(new BackgroundFill(verticalGradient, radii, Insets.EMPTY)));
    }



    /* ---------- Fine - gestione colori ----------*/


    private void apriImmagineIngrandita(Image image) {
        ImageView fullView = new ImageView(image);
        fullView.setPreserveRatio(true);
        fullView.setStyle("-fx-cursor: zoom-out;");

        // Ottieni dimensioni reali dell'immagine
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        // Optional: imposta limiti massimi (es. 90% dello schermo)
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double maxWidth = bounds.getWidth() * 0.9;
        double maxHeight = bounds.getHeight() * 0.9;

        // Scala solo se troppo grande per lo schermo
        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            fullView.setFitWidth(maxWidth);
            fullView.setFitHeight(maxHeight);
        }

        StackPane centerPane = new StackPane(fullView);
        centerPane.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(centerPane, fullView.getBoundsInParent().getWidth(), fullView.getBoundsInParent().getHeight());
        scene.setFill(Color.BLACK);

        // Qualasiasi tasto per chiudere la schermata
        scene.setOnKeyPressed(e -> {
            ((Stage) scene.getWindow()).close();
        });

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setScene(scene);

        // Usa le dimensioni reali (o scalate) dell'immagine
        double sceneWidth = fullView.getFitWidth() > 0 ? fullView.getFitWidth() : imageWidth;
        double sceneHeight = fullView.getFitHeight() > 0 ? fullView.getFitHeight() : imageHeight;
        popup.setWidth(sceneWidth);
        popup.setHeight(sceneHeight);

        popup.centerOnScreen();
        popup.show();
    }

    @FXML
    private void apriLinkYoutube(){
        String locUrl =rowCanzone.get("LINK_YOUTUBE").toString();

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(locUrl));
            } catch (IOException | URISyntaxException e) {
                ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube,"Errore nell'apertura del link!");
            }
        } else {
            ObjGenerici.mostraPopupErrore(PaginaPaneCanzone_labelYoutube,"Desktop non supportato, impossibile aprire il browser!");
        }
    }


    /* ---------- Inizio - gestione dati aggiunti dagli utenti ----------*/


    private void popolaLista() {
        // All'avvio carico tutte le musiche dell'utente (o tutte le musiche in generale se si tratta di un amministratore
        leggiCanzoni();
        if(locListaCanzoni.isEmpty()) return; // Se un utente non avesse canzoni dove lui è l'autore

        // Creo le colonne

        TableColumn<CANZONE, String> colTitolo = new TableColumn<>("TITOLO");
        colTitolo.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTITOLO()));

        TableColumn<CANZONE, String> colAutore = new TableColumn<>("AUTORE");
        colAutore.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAUTORE()));

        TableColumn<CANZONE, String> colGenere = new TableColumn<>("GENERE");
        colGenere.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getGENERE()));

        TableColumn<CANZONE, Integer> colAnno = new TableColumn<>("ANNO_COMPOSIZIONE");
        colAnno.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getANNO_COMPOSIZIONE()));

        TableColumn<CANZONE, String> colYoutube = new TableColumn<>("LINK_YOUTUBE");
        colYoutube.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLINK_YOUTUBE()));

        TableColumn<CANZONE, String> colMusica = new TableColumn<>("PERCORSO_MUSICA");
        colMusica.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPERCORSO_MUSICA()));

        TableColumn<CANZONE, String> colPDF = new TableColumn<>("PERCORSO_PDF");
        colPDF.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPERCORSO_PDF()));

        TableColumn<CANZONE, String> colCopertina = new TableColumn<>("PERCORSO_COPERTINA");
        colCopertina.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPERCORSO_COPERTINA()));

        TableColumn<CANZONE, String> colutenteIns = new TableColumn<>("UTENTE_INS");
        colutenteIns.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getUTENTE_INS()));


        PaginaPaneCanzone_tabelView.getColumns().addAll(colTitolo, colAutore, colGenere, colAnno, colYoutube, colMusica, colPDF, colCopertina, colutenteIns);

        // Dati da mostrare
        ObservableList<CANZONE> locListaCanzoniObservable = FXCollections.observableArrayList();
        for (Map<String, Object> riga : locListaCanzoni) {
            CANZONE canzone = new CANZONE(
                    Integer.parseInt(riga.get("ID_CANZONE").toString()),
                    String.valueOf(riga.get("TITOLO")),
                    String.valueOf(riga.get("AUTORE")),
                    String.valueOf(riga.get("GENERE")),
                    Integer.parseInt(riga.get("ANNO_COMPOSIZIONE").toString()),
                    String.valueOf(riga.get("LINK_YOUTUBE")),
                    String.valueOf(riga.get("PERCORSO_MUSICA")),
                    String.valueOf(riga.get("PERCORSO_PDF")),
                    String.valueOf(riga.get("PERCORSO_COPERTINA")),
                    String.valueOf(riga.get("UTENTE_INS")),
                    Integer.parseInt(riga.get("ID_UTENTE").toString())
            );
            locListaCanzoniObservable.add(canzone);
        }


        // Assegna i dati alla tabella
        PaginaPaneCanzone_tabelView.setItems(locListaCanzoniObservable);
        PaginaPaneCanzone_tabelView.setRowFactory(tv -> {
            TableRow<CANZONE> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            javafx.scene.control.MenuItem itemRimuoviCanzone = new MenuItem("✘ Rimuovi");
            itemRimuoviCanzone.setOnAction(e -> {
                CANZONE canzone = row.getItem();
                String locWhere = " ID_CANZONE="+canzone.getID_CANZONE();
                objSql.cancella("CANZONE", locWhere);
                popolaLista();
            });


            contextMenu.getItems().addAll(itemRimuoviCanzone);
            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                row.setContextMenu(isEmpty ? null : contextMenu);
            });

            return row;
        });
    }

    private void leggiCanzoni(){
        String locQuery="";
        locQuery = "SELECT * FROM CANZONE";
        locListaCanzoni= objSql.leggiLista(locQuery);
    }

    @FXML
    private void mostraFormModifica() {
        if (rowCanzone == null) return;

        // Precompilazione
        fieldTitolo.setText(rowCanzone.get("TITOLO").toString());
        fieldAutore.setText(rowCanzone.get("AUTORE").toString());
        fieldGenere.setText(rowCanzone.get("GENERE").toString());
        fieldAnno.setText(rowCanzone.get("ANNO_COMPOSIZIONE").toString());
        fieldYoutube.setText(rowCanzone.get("LINK_YOUTUBE").toString());

        // Mostra il form e permetti al layout di gestirlo
        formModifica.setVisible(true);
        formModifica.setManaged(true);
    }




    @FXML
    private void salvaModificheCanzone() {
        String sql = "UPDATE CANZONE SET " +
                "TITOLO = '" + fieldTitolo.getText().replace("'", "''") + "', " +
                "AUTORE = '" + fieldAutore.getText().replace("'", "''") + "', " +
                "GENERE = '" + fieldGenere.getText().replace("'", "''") + "', " +
                "ANNO_COMPOSIZIONE = " + Integer.parseInt(fieldAnno.getText()) + ", " +
                "LINK_YOUTUBE = '" + fieldYoutube.getText().replace("'", "''") + "' " +
                "WHERE ID_CANZONE = " + rowCanzone.get("ID_CANZONE");

        objSql.eseguiQuery(sql);

        formModifica.setVisible(false);
        formModifica.setManaged(false);
        mostraSchermataCanzone((int) rowCanzone.get("ID_CANZONE"));
        popolaLista(); // Ricarica la tabella
    }
}
