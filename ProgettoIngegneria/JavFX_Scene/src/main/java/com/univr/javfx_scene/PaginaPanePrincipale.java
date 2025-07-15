package com.univr.javfx_scene;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PaginaPanePrincipale implements Initializable {
    private final ObjSql objSql = ObjSql.oggettoSql();
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();;

    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    private List<Map<String, Object>> listaBrani, listaBraniTutto, listaBraniGeneri, listaBraniArtisti, listaBraniMancanti, listaTipi, listaBraniConcerti;
    private int canzoneCorrente, tabCorrente;   // tabCorrente-> 0=tutto, 1=generi, 2=artisti, 3=concerti
    public ArrayList<Integer> codaBrani = new ArrayList<Integer>();

    @FXML private ScrollPane PaginaPrincipale_scrollPane;
    @FXML private Label PaginaPanePrincipale_labelMusiche;
    @FXML private VBox PaginaPanePrincipale_vBoxGrigliaMusiche;
    @FXML private HBox PaginaPanePrincipale_parteSuperiore;

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


    /* ---------- INIZIO - GESTIONE BRANI ----------*/

    // Metodo per trovare il brano a partire dall'ID_CANZONE mandato
    private Map<String, Object> trovaBrano(String parId, List<Map<String, Object>> parListaBrani){
        for (Map<String, Object> locRowBrano:parListaBrani){
            if(locRowBrano.get("ID_CANZONE").toString().equals(parId)){
                return locRowBrano;
            }
        }
        return null;
    }

    // Metodo per eliminare un brano dalla lista di quelli mancanti alla riproduzione
    private void rimuoviBrano(String parId){
        listaBraniMancanti.remove(trovaBrano(parId, listaBraniMancanti));
    }

    private void aggiungiBrano(String parId){
        listaBraniMancanti.add(trovaBrano(parId, listaBrani));
    }

    /* ---------- FINE - GESTIONE BRANI ----------*/



    /* ---------- INIZIO - GESTIONE MUSICA AUTOMATICA ----------*/

    // isCasuale=0 -> no. isCasuale=1 -> è casuale
    private void selezionaMusica(String parId, int isCasuale) throws IOException {
        // Se listaBrani è ancora da inizializzare, ossia se siamo appena passati da un tipo ad un altro (Tutto, Generi, Artisti)
        if(listaBrani==null){
            switch (tabCorrente) {
                case 0:
                    listaBrani=listaBraniTutto;
                    break;
                case 1:
                    listaBrani = listaBraniGeneri;      // Se abbiamo appena premuto una card dentro Generi per la prima volta
                    break;
                case 2:
                    listaBrani = listaBraniArtisti;     // Se abbiamo appena premuto una card dentro Artisti per la prima volta
                    break;
                case 3:
                    listaBrani = listaBraniConcerti;    // Se abbiamo appena premuto una card dentro Concerti per la prima volta
                    break;
                default:
                    listaBrani=listaBraniTutto;
                    break;
            }
            listaBraniMancanti=listaBrani;              // Inizializzo anche la lista dei brani mancanti all'ascolto
        }

        Map<String, Object> locRowBrano = trovaBrano(parId, listaBrani);
        if(locRowBrano == null){
            objGenerici.mostraPopupErrore(PaginaPrincipale_scrollPane, "Attenzione!! Brano non trovato");
            return;
        }

        canzoneCorrente=Integer.parseInt(parId);
        if(isCasuale==0) {
            // Ogni volta che si seleziona una musica manualmente, si resettano i brani mancanti
            switch (tabCorrente) {
                case 0:
                    listaBraniMancanti = objSql.leggiLista("SELECT * FROM CANZONE");
                    break;
                case 1:
                    listaBraniMancanti = objSql.leggiLista("SELECT * FROM CANZONE WHERE GENERE='"+locRowBrano.get("GENERE")+"'");
                    break;
                case 2:
                    listaBraniMancanti = objSql.leggiLista("SELECT * FROM CANZONE WHERE AUTORE='"+locRowBrano.get("AUTORE")+"'");
                    break;
                case 3:
                    listaBraniMancanti = objSql.leggiLista("SELECT * FROM CANZONE WHERE IS_CONCERTO='TRUE'");
                    break;
                default:
                    break;
            }
            codaBrani.clear();
        }
        rimuoviBrano(parId);

        if(!codaBrani.contains(Integer.parseInt(parId))) {
            codaBrani.add(Integer.parseInt(parId));
        }

        mainController.selezionaMusica(Integer.parseInt(parId), this);
    }

    // Riproduzione casuale delle musiche
    public void riproduzioneCasuale(){
        if(listaBraniMancanti.isEmpty()){ return; }       // Se dovesse scorrere tutte le canzoni nella coda, significa che non ci sono altri brani che non ha ascoltato almeno una volta, dunque non ha brani successivi

        Random rand = new Random();
        int canzomeRandom = rand.nextInt(listaBraniMancanti.size());

        Map<String, Object> rowBrano = listaBraniMancanti.get(canzomeRandom);
        try {
            selezionaMusica(rowBrano.get("ID_CANZONE").toString(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void canzonePrecedente() throws IOException {
        if(codaBrani.isEmpty()){ return; }

        // Se non ci sono altre canzoni precedenti, continuo a ripetere la stessa
        if(codaBrani.indexOf(canzoneCorrente) == 0) {
            selezionaMusica(String.valueOf(canzoneCorrente), 1);
        } else {
            aggiungiBrano(String.valueOf(canzoneCorrente));  // Prima di riprodurre la musica, ripristino la successiva
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






    /* ---------- Inizio - GESTIONE GRIGLIA TUTTO ----------*/

    @FXML private void paginaTutto() throws IOException {
        impostaTab(0);
        inizializzaListaBrani();
    }

    private void inizializzaListaBrani() {
        listaBraniTutto = objSql.leggiLista("SELECT * FROM CANZONE");
        PaginaPanePrincipale_labelMusiche.setText("Esplora");
        setGrigliaMusicaTutto();
    }

    public void setGrigliaMusicaTutto () {
        impostaTab(0);

        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().clear();

        // Creo un TilePane per avere una gestione automatica delle card
        TilePane tilePane = new TilePane();
        tilePane.setHgap(15);
        tilePane.setVgap(15);
        tilePane.setPrefColumns(4);  // Numero colonne preferite (opzionale)
        tilePane.setTileAlignment(Pos.CENTER);


        for (Map<String, Object> rowBrano : listaBraniTutto) {
            VBox card = creaCard(rowBrano);
            tilePane.getChildren().add(card);
        }
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().add(tilePane);

        refreshSchermata();
    }

    /* ---------- Fine - GESTIONE GRIGLIA TUTTO ----------*/


    /* ---------- Inizio - GESTIONE GRIGLIA GENERI ----------*/

    @FXML
    private void paginaGeneri (){
        impostaTab(1);
        // Popolo la lista dei brani e quella dei generi
        listaBraniGeneri = objSql.leggiLista("SELECT * FROM CANZONE");
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
            for (Map<String, Object> rowBrano : listaBraniGeneri) {
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




    /* ---------- Inizio - GESTIONE GRIGLIA ARTISTI ----------*/

    @FXML
    private void paginaArtisti (){
        impostaTab(2);
        // Popolo la lista dei brani e quella dei generi
        listaBraniArtisti = objSql.leggiLista("SELECT * FROM CANZONE");
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
            for (Map<String, Object> rowBrano : listaBraniArtisti) {
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

    /* ---------- Fine - GESTIONE GRIGLIA ARTISTI ----------*/


    /* ---------- Inizio - GESTIONE GRIGLIA CONCERTI ----------*/

    @FXML private void paginaConcerti() throws IOException {
        impostaTab(3);
        listaBraniConcerti = objSql.leggiLista("SELECT * FROM CANZONE WHERE IS_CONCERTO = true");
        PaginaPanePrincipale_labelMusiche.setText("Concerti");
        setGrigliaMusicaConcerti();
    }

    public void setGrigliaMusicaConcerti () {
        impostaTab(0);

        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().clear();

        // Creo un TilePane per avere una gestione automatica delle card
        TilePane tilePane = new TilePane();
        tilePane.setHgap(15);
        tilePane.setVgap(15);
        tilePane.setPrefColumns(4);  // Numero colonne preferite (opzionale)
        tilePane.setTileAlignment(Pos.CENTER);


        for (Map<String, Object> rowBrano : listaBraniConcerti) {
            VBox card = creaCard(rowBrano);
            tilePane.getChildren().add(card);
        }
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().add(tilePane);

        refreshSchermata();
    }


    /* ---------- Fine - GESTIONE GRIGLIA CONCERTI ----------*/


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
            if(event.getButton()== MouseButton.PRIMARY){
                 try {
                     /* Ogni volta che cambio una musica, devo sapere dove mi trovo, dunque la ricalcolo.
                     Questo perchè potrei stare selezionando una musica da "Artisti" mentre in riproduzione ho una musica da "Generi" e questo fa confusione con le musica prox da riprodurre  */
                     listaBrani=null;
                     selezionaMusica(card.getId(),0);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
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
        MenuItem downloadItem = new MenuItem("Download");
        MenuItem apriCommenti = new MenuItem("Commenti");

        apriCommenti.setOnAction(e -> {
            int locIdCanzone = Integer.parseInt(card.getId());
            try {
                mainController.mostraCommenti(locIdCanzone);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        downloadItem.setOnAction(e -> {
            int locIdCanzone = Integer.parseInt(card.getId());
            try {
                scaricaFileCanzone(locIdCanzone);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        ContextMenu contextMenu = new ContextMenu(downloadItem, apriCommenti);

        card.setOnContextMenuRequested(e -> {
            contextMenu.show(card, e.getScreenX(), e.getScreenY());
        });


        // Controllo se eventualmente è un PNG o JPEG
        String locPath = objGenerici.ritornaCopertina(Integer.parseInt(rowBrano.get("ID_CANZONE").toString()));

        Image immagine = new Image(new File(locPath).toURI().toString());
        ImageView copertina = new ImageView(immagine);
        copertina.setFitWidth(150);
        copertina.setFitHeight(150);
        copertina.setPreserveRatio(false);

        Rectangle contenitore = new Rectangle(150, 150);
        contenitore.setArcWidth(10);
        contenitore.setArcHeight(10);

        copertina.setClip(contenitore); // In modo che l'immagine abbia i borsi smussati


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

    private void impostaTab(int parTabCorrente){
        tabCorrente=parTabCorrente;
    }

    private void refreshSchermata(){
        PaginaPanePrincipale_vBoxGrigliaMusiche.requestLayout();
        PaginaPanePrincipale_vBoxGrigliaMusiche.layout();

        PaginaPanePrincipale_parteSuperiore.requestLayout();
        PaginaPanePrincipale_parteSuperiore.layout();
    }


    /* ---------- Inizio - GESTIONE RICERCA CANZONE ----------*/

    public void ricercaMusica(List<Map<String, Object>> parListaBrani, String parChiaveRicerca) throws IOException {
        listaBrani=parListaBrani;
        PaginaPanePrincipale_labelMusiche.setText("");


        if(listaBrani.isEmpty()) {  // Se non trova nessun risultato
            PaginaPanePrincipale_labelMusiche.setText("Nessun brano trovato");
        } else {
            if (parChiaveRicerca.isEmpty())  // Se ha terminato la sua ricerca
                PaginaPanePrincipale_labelMusiche.setText("Esplora");
            else    // Se sta cercando
                PaginaPanePrincipale_labelMusiche.setText("Ricerca ...");
        }

        setGrigliaMusicaRicerca(parListaBrani);
    }

    public void setGrigliaMusicaRicerca (List<Map<String, Object>> listaCanzone) {
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().clear();

        // Creo un TilePane per avere una gestione automatica delle card
        TilePane tilePane = new TilePane();
        tilePane.setHgap(15);
        tilePane.setVgap(15);
        tilePane.setPrefColumns(4);
        tilePane.setTileAlignment(Pos.CENTER);

        for (Map<String, Object> rowBrano : listaCanzone) {
            VBox card = creaCard(rowBrano);
            tilePane.getChildren().add(card);
        }
        PaginaPanePrincipale_vBoxGrigliaMusiche.getChildren().add(tilePane);
    }

    /* ---------- Fine - GESTIONE RICERCA CANZONE ----------*/
}


