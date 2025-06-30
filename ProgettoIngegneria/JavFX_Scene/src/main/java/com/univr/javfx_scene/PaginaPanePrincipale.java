package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.stage.Popup;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.stage.DirectoryChooser;


public class PaginaPanePrincipale implements Initializable {

    private  ObjSql objSql = ObjSql.oggettoSql();
    private  ObjGenerici objGenerici;

    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    private List<Map<String, Object>> listaBrani, listaBraniMancanti, listaBraniCustom, listaTipi;

    @FXML private ScrollPane PaginaPrincipale_scrollPane;
    @FXML private Label PaginaPanePrincipale_labelMusiche;
    @FXML private VBox PaginaPanePrincipale_vBoxGrigliaMusiche;

    private int canzoneCorrente;

    public ArrayList<Integer> codaBrani = new ArrayList<Integer>();


    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    public PaginaPrincipale getController(){
        return mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PaginaPanePrincipale_vBoxGrigliaMusiche.prefWidthProperty().bind(PaginaPrincipale_scrollPane.widthProperty());
        inizializzaListaBrani();
    }

    private void inizializzaListaBrani() {
        listaBrani = objSql.leggiLista("SELECT * FROM CANZONE");
        PaginaPanePrincipale_labelMusiche.setText("Esplora");
        setGrigliaMusica(listaBrani);
    }

    public void setGrigliaMusica (List<Map<String, Object>> rowCanzone) {
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().clear();

        // Creo un TilePane per avere una gestione automatica delle card
        TilePane tilePane = new TilePane();
        tilePane.setHgap(15);
        tilePane.setVgap(15);
        tilePane.setPrefColumns(4);  // Numero colonne preferite (opzionale)
        tilePane.setTileAlignment(Pos.CENTER);


        for (Map<String, Object> rowBrano : rowCanzone) {
            VBox card = creaCard(rowBrano);
            tilePane.getChildren().add(card);
        }
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().add(tilePane);
    }

    /* ---------- Inizio - GESTIONE MUSICA AUTOMATICA ----------*/

    private void selezionaMusica(String parId, int isCasuale) throws IOException {
        if(isCasuale==0) {
            // Ogni volta che si seleziona una musica manualmente, si resettano i brani mancanti
            listaBraniMancanti = objSql.leggiLista("SELECT * FROM CANZONE");
            listaBraniMancanti = objSql.leggiLista("SELECT * FROM CANZONE");
            codaBrani.clear();
        }
        rimuoviBrano(parId);

        if(!codaBrani.contains(Integer.parseInt(parId))) {
            codaBrani.add(Integer.parseInt(parId));
        }
        System.out.println(codaBrani);

        canzoneCorrente = Integer.parseInt(parId);
        mainController.selezionaMusica(Integer.parseInt(parId), this);
    }

    private void rimuoviBrano(String parId){
        for (Map<String, Object> rowBrano:listaBraniMancanti){
            if(rowBrano.get("ID_CANZONE").toString().equals(parId)){
                listaBraniMancanti.remove(rowBrano);
                break;
            }
        }
    }

    // Riproduzione casuale delle musiche
    public void riproduzioneCasuale(){
        Random rand = new Random();
        int canzomeRandom = rand.nextInt(listaBraniMancanti.size());

        Map<String, Object> rowCanzone = listaBraniMancanti.get(canzomeRandom);
        try {
            selezionaMusica(rowCanzone.get("ID_CANZONE").toString(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void canzonePrecedente() throws IOException {
        if(codaBrani.indexOf(canzoneCorrente) == 0) {
            selezionaMusica(String.valueOf(canzoneCorrente), 1);
        } else {
            selezionaMusica(codaBrani.get(codaBrani.indexOf(canzoneCorrente) - 1).toString(), 1);
        }
    }

    public void canzoneSucessiva() throws IOException {
        if(codaBrani.contains(canzoneCorrente)) {
            if(codaBrani.indexOf(canzoneCorrente) == codaBrani.size() - 1) {
                riproduzioneCasuale();
            } else {
                selezionaMusica(codaBrani.get(codaBrani.indexOf(canzoneCorrente) + 1).toString(), 1);
            }
        }
    }

    /* ---------- Fine - GESTIONE MUSICA AUTOMATICA ----------*/






    @FXML private void paginaConcerti() throws IOException { }


    /* ---------- Inizio - GESTIONE GRIGLIA TUTO ----------*/
    @FXML private void paginaTutto() throws IOException {
        inizializzaListaBrani();
    }
    /* ---------- Fine - GESTIONE GRIGLIA TUTO ----------*/


    /* ---------- Inizio - GESTIONE GRIGLIA GENERI ----------*/

    @FXML
    private void paginaGeneri (){
        // Popolo la lista dei brani e quella dei generi
        listaBraniCustom = objSql.leggiLista("SELECT * FROM CANZONE");
        listaTipi = objSql.leggiLista("SELECT GENERE FROM CANZONE GROUP BY GENERE");
        PaginaPanePrincipale_labelMusiche.setText("Generi");

        setGrigliaMusicaGeneri();
    }

    private void setGrigliaMusicaGeneri(){
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().clear();
        PaginaPanePrincipale_vBoxGrigliaMusiche.setPadding(new Insets(10));

        // Ciclo i generi che contengono almeno una canzone
        for (Map<String, Object> genereMap : listaTipi) {
            String nomeGenere = (String) genereMap.get("GENERE");

            // HBox principale per le card delle canzoni
            HBox canzoniHBox = new HBox(15);
            canzoniHBox.setPadding(new Insets(10, 0, 10, 0));

            // Ciclo sui brani cercando quelli del genere corrente
            for (Map<String, Object> rowBrano : listaBraniCustom) {
                if (rowBrano.get("GENERE") != null && rowBrano.get("GENERE").equals(nomeGenere)) {
                    VBox card = creaCard(rowBrano);
                    canzoniHBox.getChildren().add(card);
                }
            }

            // Se trovo una canzone, inizio a comporne la riga
            if (!canzoniHBox.getChildren().isEmpty()) {
                // Imposto l'etichetta del genere
                Label genereLabel = new Label(nomeGenere);
                genereLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 22px; ");

                // Imposto lo scroll pane solo orizzontale, con hbox per le musiche
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(canzoniHBox);
                scrollPane.setFitToWidth(false);
                scrollPane.setFitToHeight(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


                scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

                // Aggiungo il tutto al Vbox principale
                PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().addAll(genereLabel, scrollPane);
            }
        }
    }

    /* ---------- Fine - GESTIONE GRIGLIA GENERI ----------*/




    /* ---------- Inizio - GESTIONE GRIGLIA GENERI ----------*/

    @FXML
    private void paginaArtisti (){
        // Popolo la lista dei brani e quella dei generi
        listaBraniCustom = objSql.leggiLista("SELECT * FROM CANZONE");
        listaTipi = objSql.leggiLista("SELECT AUTORE FROM CANZONE GROUP BY AUTORE");
        PaginaPanePrincipale_labelMusiche.setText("Artisti");

        setGrigliaMusicaArtisti();
    }

    private void setGrigliaMusicaArtisti(){
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().clear();
        PaginaPanePrincipale_vBoxGrigliaMusiche.setPadding(new Insets(10));

        // Ciclo gli artisti che contengono almeno una canzone
        for (Map<String, Object> genereMap : listaTipi) {
            String nomeAutore = (String) genereMap.get("AUTORE");

            // HBox principale per le card delle canzoni
            HBox canzoniHBox = new HBox(15);
            canzoniHBox.setPadding(new Insets(10, 0, 10, 0));

            // Ciclo sui brani cercando quelli del genere corrente
            for (Map<String, Object> rowBrano : listaBraniCustom) {
                if (rowBrano.get("AUTORE") != null && rowBrano.get("AUTORE").equals(nomeAutore)) {
                    VBox card = creaCard(rowBrano);
                    canzoniHBox.getChildren().add(card);
                }
            }

            // Se trovo una canzone, inizio a comporne la riga
            if (!canzoniHBox.getChildren().isEmpty()) {
                // Imposto l'etichetta del genere
                Label genereLabel = new Label(nomeAutore);
                genereLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 22px; ");

                // Imposto lo scroll pane solo orizzontale, con hbox per le musiche
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(canzoniHBox);
                scrollPane.setFitToWidth(false);
                scrollPane.setFitToHeight(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


                scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

                // Aggiungo il tutto al Vbox principale
                PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().addAll(genereLabel, scrollPane);
            }
        }
    }

    /* ---------- Fine - GESTIONE GRIGLIA GENERI ----------*/

    private VBox creaCard(Map<String, Object> rowBrano) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);

        card.setStyle("""
        -fx-background-color: transparent;
        -fx-background-radius: 15;
        -fx-padding: 10;
    """);


        card.setPrefWidth(180);
        card.setPrefHeight(180);
        card.setId(Integer.toString((Integer) rowBrano.get("ID_CANZONE")));

        card.setOnMouseClicked(event -> {
            try {
                selezionaMusica(card.getId(), 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Imposto lo stile al passaggio del mouse
        card.setOnMouseEntered(event -> {
            card.setCursor(Cursor.HAND);
            card.setStyle("""
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 5;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);
                """);
        });

        card.setOnMouseExited(event -> {
            card.setCursor(Cursor.DEFAULT);
            card.setStyle("""
                 -fx-background-color: transparent;
                  -fx-background-radius: 5;
                  -fx-padding: 10;
                  """);
        });

        // Crea menu contestuale (tasto destro)
        ContextMenu contextMenu = new ContextMenu();
        MenuItem downloadItem = new MenuItem("Download");

        downloadItem.setOnAction(e -> {
            int idCanzone = Integer.parseInt(card.getId());
            try {
                scaricaFileCanzone(idCanzone);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        contextMenu.getItems().add(downloadItem);

        card.setOnContextMenuRequested(e -> {
            contextMenu.show(card, e.getScreenX(), e.getScreenY());
        });


        // Controllo se eventualmente è un PNG o JPEG
        String locPath = objGenerici.ritornaCopertina(Integer.parseInt(rowBrano.get("ID_CANZONE").toString()));

        Image immagine = new Image(new File(locPath).toURI().toString());
        ImageView copertina = new ImageView(immagine);
        copertina.setFitWidth(150);
        copertina.setFitHeight(150);
        copertina.setPreserveRatio(true);

        Label titolo = new Label((String) rowBrano.get("TITOLO"));
        titolo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        titolo.setWrapText(true);
        titolo.setAlignment(Pos.CENTER);

        Label autore = new Label((String) rowBrano.get("AUTORE"));
        autore.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
        autore.setWrapText(true);
        autore.setAlignment(Pos.CENTER);

        card.getChildren().addAll(copertina, titolo, autore);
        return card;
    }

    /* ---------- Fine - GESTIONE CAMBIO PAGINA MUSICA - TUTTO - GENERI - AUTORI ----------*/

    ReadOnlyDoubleProperty getScrollPaneWidth(){
        return PaginaPrincipale_scrollPane.widthProperty();
    }

    private void scaricaFileCanzone(int parIdCanzone) throws IOException {
        objGenerici.scaricaFileCanzone(PaginaPanePrincipale_vBoxGrigliaMusiche, objSql, parIdCanzone);
    }
}


