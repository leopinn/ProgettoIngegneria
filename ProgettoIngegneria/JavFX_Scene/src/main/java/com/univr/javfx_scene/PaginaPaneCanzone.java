package com.univr.javfx_scene;
import com.univr.javfx_scene.Classi.ModificaCanzone;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
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
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class PaginaPaneCanzone {
    private static ObjSql objSql = ObjSql.oggettoSql();
    private Map<String, Object> rowCanzone;
    private List<Map<String, Object>> locListaModifiche;
    @FXML private VBox formModifica;
    @FXML private TextField fieldTitolo, fieldAutore, fieldGenere, fieldAnno, fieldYoutube;
    @FXML private ImageView PaginaPaneCanzone_copertina;
    @FXML private Label PaginaPaneCanzone_titolo, PaginaPaneCanzone_altriDati, PaginaPaneCanzone_labelYoutube;
    @FXML private HBox PaginaPaneCanzone_hBoxUp;
    @FXML private TableView<ModificaCanzone> PaginaPaneCanzone_tabelView;


    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    private String getSafe(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    public void mostraSchermataCanzone(int parIdCanzone){
        // Non dovrebbe mai accedere
        if (parIdCanzone == 0) return;

        rowCanzone = objSql.leggi("SELECT * FROM CANZONE WHERE ID_CANZONE=" + parIdCanzone);
        if (rowCanzone.isEmpty()) return;

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

        PaginaPaneCanzone_titolo.setText(getSafe(rowCanzone, "TITOLO"));

        String locDatiAggiuntivi = getSafe(rowCanzone, "AUTORE") + " • " +
                getSafe(rowCanzone, "GENERE") + " • " +
                getSafe(rowCanzone, "ANNO_COMPOSIZIONE");

        PaginaPaneCanzone_altriDati.setText(locDatiAggiuntivi);
        PaginaPaneCanzone_labelYoutube.setText(getSafe(rowCanzone, "LINK_YOUTUBE"));

        // Imposto il colore di sfondo basandomi sul colore predominante della copertina3
        Color coloreMedio = calcolaColoreMedio();
        applicaGradiente(PaginaPaneCanzone_hBoxUp, coloreMedio);

        // Nascondi il form all'apertura
        formModifica.setVisible(false);
        formModifica.setManaged(false);

        // Inizializza le colonne solo se vuote (la prima volta)
        popolaLista();
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

        // Imposto dei limiti massimi
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

        // Qualasiasi tasto della tastiera per chiudere la schermata
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


    /* ---------- Inizio - gestione Table View ----------*/


    // Popola la Table View
    private void popolaLista() {
        leggiModifiche();

        if(locListaModifiche.isEmpty()) return; // Se un utente non avesse canzoni dove lui è l'autore

        TableColumn<ModificaCanzone, Integer> colId = new TableColumn<>("ID_DATI");
        colId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getID_DATI()));

        //TableColumn<ModificaCanzone, Integer> colIdCanzone = new TableColumn<>("ID_CANZONE");
        //colIdCanzone.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getID_CANZONE()));

        TableColumn<ModificaCanzone, String> colTitolo = new TableColumn<>("TITOLO");
        colTitolo.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTITOLO()));

        TableColumn<ModificaCanzone, String> colAutore = new TableColumn<>("AUTORE");
        colAutore.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAUTORE()));

        TableColumn<ModificaCanzone, String> colGenere = new TableColumn<>("GENERE");
        colGenere.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getGENERE()));

        TableColumn<ModificaCanzone, Integer> colAnno = new TableColumn<>("ANNO_COMPOSIZIONE");
        colAnno.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getANNO()));

        TableColumn<ModificaCanzone, String> colYoutube = new TableColumn<>("LINK_YOUTUBE");
        colYoutube.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getYOUTUBE()));

        //TableColumn<ModificaCanzone, Integer> colIdUtente = new TableColumn<>("ID_UTENTE");
        //colIdUtente.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getID_UTENTE()));

        TableColumn<ModificaCanzone, String> colutenteIns = new TableColumn<>("UTENTE_INS");
        colutenteIns.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getUTENTE_INS()));

        TableColumn<ModificaCanzone, String> colData = new TableColumn<>("DATA_INSERIMENTO");
        colData.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDATA()));

        if (PaginaPaneCanzone_tabelView.getColumns().isEmpty()) {
            //PaginaPaneCanzone_tabelView.getColumns().addAll(colId, colIdCanzone, colTitolo, colAutore, colGenere, colAnno, colYoutube, colIdUtente, colutenteIns, colData);
            PaginaPaneCanzone_tabelView.getColumns().addAll(colId, colTitolo, colAutore, colGenere, colAnno, colYoutube, colutenteIns, colData);
        }


        // Carica modifiche utente dalla tabella DATI_AGGIUNTIVI_CANZONE
        ObservableList<ModificaCanzone> listaModifiche = FXCollections.observableArrayList();
        //var modifiche = objSql.leggiTutti("SELECT * FROM DATI_AGGIUNTIVI_CANZONE WHERE ID_CANZONE = " + parIdCanzone + " ORDER BY DATA_INSERIMENTO DESC");
        for (Map<String, Object> riga : locListaModifiche) {
            //
            Integer annoComposizione = null;
            Object valAnno = riga.get("ANNO_COMPOSIZIONE");
            if (valAnno != null) {
                try {
                    annoComposizione = Integer.parseInt(valAnno.toString());
                } catch (NumberFormatException e) {
                    annoComposizione = 0; // oppure puoi anche ignorare
                }
            }

            ModificaCanzone modCanzone = new ModificaCanzone(
                    Integer.parseInt(riga.get("ID_DATI").toString()),
                    //Integer.parseInt(riga.get("ID_CANZONE").toString()),
                    String.valueOf(riga.get("TITOLO")),
                    String.valueOf(riga.get("AUTORE")),
                    String.valueOf(riga.get("GENERE")),
                    annoComposizione != null ? annoComposizione : 0, // oppure lascia null se accetta
                    String.valueOf(riga.get("LINK_YOUTUBE")),
                    //Integer.parseInt(riga.get("ID_UTENTE").toString()),
                    String.valueOf(riga.get("UTENTE_INS")),
                    String.valueOf(riga.get("DATA_INSERIMENTO"))
            );
            listaModifiche.add(modCanzone);
        }

        // Assegna i dati alla tabella
        PaginaPaneCanzone_tabelView.setItems(listaModifiche);
    }


    /* ---------- Fine - gestione Table View ----------*/


    private void leggiModifiche(){
        String locQuery="";

        if(PaginaPaneLogin.UTENTE_NOME.equals("adm")) {
            locQuery = "SELECT * FROM DATI_AGGIUNTIVI_CANZONE";

        } else {    // Le canzoni caricate dall'utente corrente
            locQuery = "SELECT * FROM DATI_AGGIUNTIVI_CANZONE WHERE ID_UTENTE = " + PaginaPaneLogin.ID_UTENTE;
        }

        locListaModifiche= objSql.leggiLista(locQuery);
    }


    // Gestione della visbilità del form Modifica dati
    @FXML
    private void mostraFormModifica() {
        if (rowCanzone == null) return;

        // Toggle visibilità
        boolean attualeVisibilita = formModifica.isVisible();
        formModifica.setVisible(!attualeVisibilita);
        formModifica.setManaged(!attualeVisibilita);

        // Solo se si sta mostrando, precompila i campi
        if (!attualeVisibilita) {
            fieldTitolo.setPromptText(rowCanzone.get("TITOLO").toString());
            fieldAutore.setPromptText(rowCanzone.get("AUTORE").toString());
            fieldGenere.setPromptText(rowCanzone.get("GENERE").toString());
            fieldAnno.setPromptText(rowCanzone.get("ANNO_COMPOSIZIONE").toString());
            fieldYoutube.setPromptText(rowCanzone.get("LINK_YOUTUBE").toString());
        }
    }

    //Salvataggio modifica dati
    @FXML
    private void salvaModificheCanzone() {
        String nuovoTitolo = fieldTitolo.getText().trim();
        String nuovoAutore = fieldAutore.getText().trim();
        String nuovoGenere = fieldGenere.getText().trim();
        String nuovoAnnoStr = fieldAnno.getText().trim();
        String nuovoYoutube = fieldYoutube.getText().trim();

        // Validazione base
        if (nuovoTitolo.isEmpty() || nuovoAutore.isEmpty() || nuovoGenere.isEmpty()) {
            ObjGenerici.mostraPopupErrore(fieldTitolo, "Compila tutti i campi obbligatori.");
            return;
        }

        // Convalida ANNO_COMPOSIZIONE
        Integer nuovoAnno = null;
        if (!nuovoAnnoStr.isEmpty()) {
            try {
                nuovoAnno = Integer.parseInt(nuovoAnnoStr);
            } catch (NumberFormatException e) {
                ObjGenerici.mostraPopupErrore(fieldAnno, "Anno non valido. Inserisci un numero.");
                return;
            }
        }

        int idCanzone = Integer.parseInt(rowCanzone.get("ID_CANZONE").toString());

        Map<String, Object> nuoviDati = Map.of(
                "ID_CANZONE", idCanzone,
                "TITOLO", nuovoTitolo,
                "AUTORE", nuovoAutore,
                "GENERE", nuovoGenere,
                "ANNO_COMPOSIZIONE", nuovoAnno,
                "LINK_YOUTUBE", nuovoYoutube,
                "ID_UTENTE", PaginaPaneLogin.ID_UTENTE,
                "UTENTE_INS", PaginaPaneLogin.UTENTE_NOME
                // DATA_INSERIMENTO viene gestito automaticamente dal DB
        );

        boolean successo = objSql.inserisci("DATI_AGGIUNTIVI_CANZONE", nuoviDati) == 1;

        if (successo) {
            ObjGenerici.mostraPopupSuccesso(fieldTitolo, "Modifiche salvate con successo!");
            mostraSchermataCanzone(idCanzone); // aggiorna vista
        } else {
            ObjGenerici.mostraPopupErrore(fieldTitolo, "Errore durante il salvataggio.");
        }
    }
}