package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaginaPaneCommenti {
    private final ObjSql objSql = ObjSql.oggettoSql();
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();

    private int ID_CANZONE;
    private List<Map<String, Object>> listaCommenti, listaCommentiRange;    // Messa pubblica per averla disponibile in tutta la gestione commenti
    private Map<String, Object> rowCommento, rowCanzone;            // Il commento attualmente selezionato e la canzone attuale in riproduzione

    public int tipoCommento=0;      // 0=commento a se stante, 1=risposta ad un commento esistente

    @FXML private VBox commentiContainer;
    @FXML private VBox PaginaPaneCommenti_vBoxMinutaggio;
    @FXML private TextArea commentoTextArea;
    @FXML private TextField PaginaPaneCommenti_testoInizio, PaginaPaneCommenti_testoFine;

    private VBox commentoMostrato = null;
    private int rangeInizioCorrente = -1;
    private int rangeFineCorrente = -1;
    private PaginaPrincipale mainController;
    public void setMainController(PaginaPrincipale mainController) {this.mainController=mainController;}

    // Carica i commenti presenti nel db per quella canzone
    public void caricaCommenti(int id_canzone) {
        impostaTextField();
        ID_CANZONE = id_canzone;

        rowCanzone = objSql.leggi("SELECT * FROM CANZONE WHERE ID_CANZONE = " + ID_CANZONE);

        commentiContainer.getChildren().clear();

        // Carica tutti i commenti della canzone
        listaCommenti = objSql.leggiLista("SELECT ID_COMMENTO,ID_CANZONE, UTENTI.ID_UTENTE, ID_PADRE, TESTO, RANGE_INIZIO, RANGE_FINE, UTENTI.NOME, UTENTI.COGNOME "+
                "FROM COMMENTI "+
                "INNER JOIN UTENTI ON UTENTI.ID_UTENTE=COMMENTI.ID_UTENTE "+
                "WHERE ID_CANZONE = " + id_canzone + " AND (RANGE_INIZIO IS NULL OR RANGE_INIZIO='') AND (RANGE_FINE IS NULL OR RANGE_FINE='') ORDER BY ID_COMMENTO ASC");

        // Leggo la lista dei commenti a minutaggio
        listaCommentiRange = objSql.leggiLista("SELECT ID_COMMENTO,ID_CANZONE, UTENTI.ID_UTENTE, ID_PADRE, TESTO, RANGE_INIZIO, RANGE_FINE, UTENTI.NOME, UTENTI.COGNOME "+
                "FROM COMMENTI "+
                "INNER JOIN UTENTI ON UTENTI.ID_UTENTE=COMMENTI.ID_UTENTE "+
                "WHERE ID_CANZONE = " + id_canzone + " AND (RANGE_INIZIO IS NOT NULL OR RANGE_INIZIO!='') AND (RANGE_FINE IS NOT NULL OR RANGE_FINE!='') ORDER BY ID_COMMENTO ASC");

        if (listaCommenti == null) return;

        // Filtra i commenti principali (padri) con ID_PADRE = null
        for (Map<String, Object> commento : listaCommenti) {
            if (commento.get("ID_PADRE") != null) continue; // solo padri

            VBox commentoBox = creaVBoxCommento(commento, false);
            commentiContainer.getChildren().add(commentoBox);

            // Carica le risposte (figli)
            for (Map<String, Object> risposta : listaCommenti) {
                Object idPadre = risposta.get("ID_PADRE");
                if (idPadre != null && idPadre.equals(commento.get("ID_COMMENTO"))) {
                    VBox rispostaBox = creaVBoxCommento(risposta, true);
                    commentiContainer.getChildren().add(rispostaBox);
                }
            }
        }
    }

    private void impostaTextField(){
        PaginaPaneCommenti_testoInizio.setTextFormatter(new TextFormatter<>(change -> {
            String nuovoTesto = change.getControlNewText();
            return nuovoTesto.matches("\\d*") ? change : null;
        }));
        PaginaPaneCommenti_testoFine.setTextFormatter(new TextFormatter<>(change -> {
            String nuovoTesto = change.getControlNewText();
            return nuovoTesto.matches("\\d*") ? change : null;
        }));
    }

    @FXML
    public void inviaCommento(ActionEvent event) {
        // codice da eseguire quando si clicca "Invia"
        String testo = commentoTextArea.getText();

        if (testo.trim().isEmpty()) {return;}

        int utente = objGenerici.getID_UTENTE();


        Map<String, Object> locRowCommento = new LinkedHashMap<>();
        locRowCommento.put("ID_CANZONE", ID_CANZONE);
        locRowCommento.put("ID_UTENTE", utente);
        locRowCommento.put("TESTO", testo);

        // Controllo se si tratta di un commento per un determinato minutaggio della canzone
        String locInizio = PaginaPaneCommenti_testoInizio.getText();
        String locFine = PaginaPaneCommenti_testoFine.getText();
        if(!locInizio.isEmpty() && (!locFine.isEmpty())){
            // Se è un commento che dura più di 10 secondi
            int inizio = Integer.parseInt(locInizio);
            int fine = Integer.parseInt(locFine);
            if(fine-inizio>10) {
                erroreCommento(1);
                pulisciCampi();
                return;
            }

            // Se il commento viene scritto dentro al range della musica
            MediaPlayer locMediaPlayer = mainController.getMediaPlayer();
            Duration locDurata = locMediaPlayer.getMedia().getDuration();
            if(inizio>(int) locDurata.toSeconds() || fine>(int) locDurata.toSeconds()) {
                erroreCommento(2);
                pulisciCampi();
                return;
            }

            // Se esiste già un commento dentro quel range
            for (Map<String, Object> rowCommento : listaCommentiRange) {
                String locInizioCommento = rowCommento.get("RANGE_INIZIO").toString();
                String locFineCommento = rowCommento.get("RANGE_FINE").toString();

                if (!locInizioCommento.isEmpty() && !locFineCommento.isEmpty()) {
                    int inizioCommento = Integer.parseInt(locInizioCommento);
                    int fineCommento = Integer.parseInt(locFineCommento);


                    // Controllo se i due intervalli si sovrappongono
                    boolean siSovrappone = (inizio >= inizioCommento && inizio <= fineCommento) ||
                            (fine >= inizioCommento && fine <= fineCommento) ||
                            (inizio <= inizioCommento && fine >= fineCommento);

                    if (siSovrappone) {
                        erroreCommento(3); // Sovrapposizione di range
                        pulisciCampi();
                        return;
                    }
                }
            }

            ObjGenerici.mostraPopupSuccesso(commentoTextArea,"Commento aggiunto con successo!");
            locRowCommento.put("RANGE_INIZIO", PaginaPaneCommenti_testoInizio.getText());
            locRowCommento.put("RANGE_FINE", PaginaPaneCommenti_testoFine.getText());
        }

        if(tipoCommento==1){
            locRowCommento.put("ID_PADRE", rowCommento.get("ID_PADRE"));
            tipoCommento=0;     // Lo ripristino immediatamente per non avere sorprese
        }

        objSql.inserisci("COMMENTI", locRowCommento);
        caricaCommenti(ID_CANZONE);
        impostaTextArea(tipoCommento);

        pulisciCampi();
    }

    private void pulisciCampi(){
        PaginaPaneCommenti_testoInizio.setText("");
        PaginaPaneCommenti_testoFine.setText("");
        commentoTextArea.setText("");
    }

    public void annullaCommento(){
        tipoCommento=0;
        // Cambio il bordo per far capire che si riferisce ad una risposta di un commento
        impostaTextArea(tipoCommento);
        commentoTextArea.setText("");
    }

    private VBox creaVBoxCommento(Map<String, Object> commento, boolean isFiglio) {
            String autore;
            Label autoreLabel;

            VBox contenutoBox = new VBox();
            contenutoBox.setSpacing(2);
            contenutoBox.getStyleClass().add("commento");

            // Controllo autore
            if (rowCanzone.get("AUTORE").toString().equals(commento.get("NOME").toString())) {
                autore = commento.get("NOME").toString() + " " + commento.get("COGNOME").toString() + " (autore)";
                autoreLabel = new Label(autore);
                autoreLabel.setStyle("-fx-text-fill: #E5484D; -fx-font-weight: bold; -fx-font-size: 16; -fx-padding: 0 0 4 0;");
            } else {
                autore = commento.get("NOME").toString() + " " + commento.get("COGNOME").toString();
                autoreLabel = new Label(autore);
                autoreLabel.setStyle("-fx-text-fill: #6d24e1; -fx-font-weight: bold; -fx-font-size: 16; -fx-padding: 0 0 4 0;");
            }

            Label testoLabel = new Label(commento.get("TESTO").toString());
            testoLabel.setWrapText(true);
            testoLabel.setMaxWidth(400);
            testoLabel.setStyle("-fx-font-size: 14; -fx-line-spacing: 5; -fx-text-fill: #ffffff");

            Label testoRispondi = new Label("Rispondi");
            testoRispondi.setStyle("-fx-text-fill: #b1b1b1 ; -fx-underline: false; -fx-font-size: 12; -fx-padding: 4 0 4 0;");
            testoRispondi.setOnMouseClicked(event -> {
                rispondiCommento((Integer) commento.get("ID_COMMENTO"));
            });
            testoRispondi.setOnMouseEntered(event -> {
                testoRispondi.setStyle("-fx-text-fill: #b1b1b1 ; -fx-underline: true; -fx-font-size: 12; -fx-padding: 4 0 4 0; -fx-cursor: hand;");
            });
            testoRispondi.setOnMouseExited(event -> {
                testoRispondi.setStyle("-fx-text-fill: #b1b1b1 ; -fx-underline: false; -fx-font-size: 12; -fx-padding: 4 0 4 0; -fx-cursor: default;");
            });

            HBox rispondiBox = new HBox(testoRispondi);
            rispondiBox.setAlignment(Pos.CENTER_RIGHT);

            contenutoBox.getChildren().addAll(autoreLabel, testoLabel, rispondiBox);

            // Foto profilo tonda
            ImageView fotoProfilo = new ImageView();
            String pathFoto = ObjGenerici.ritornaFotoProfilo(String.valueOf(commento.get("ID_UTENTE")));
            if (pathFoto.isEmpty()) pathFoto = ObjGenerici.ritornaFotoProfilo("fotoProfiloBase");

            Image image = new Image(new File(pathFoto).toURI().toString());
            fotoProfilo.setImage(image);
            fotoProfilo.setFitWidth(40); // usa 10 se sei sicuro, ma è molto piccolo
            fotoProfilo.setFitHeight(40);
            fotoProfilo.setPreserveRatio(false);

            Circle clip = new Circle(20, 20, 20); // raggio = metà larghezza/altezza
            fotoProfilo.setClip(clip);

            // HBox principale che contiene immagine e contenuto
            HBox box = new HBox(10); // Spaziatura tra immagine e contenuto
            box.getChildren().addAll(fotoProfilo, contenutoBox);
            box.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(contenutoBox, Priority.ALWAYS);
            box.setMaxWidth(Double.MAX_VALUE);

            // Stili
            if (isFiglio) {
                contenutoBox.setMaxWidth(400);
                contenutoBox.setStyle("-fx-background-color: #292929; -fx-padding: 6 12 6 12; -fx-background-radius: 8;");
                VBox.setMargin(box, new Insets(0, 0, 3, 60));
            } else {
                contenutoBox.setMaxWidth(Double.MAX_VALUE);
                VBox.setMargin(box, new Insets(0, 0, 3, 0));
            }

            // Context menu se è l'autore del commento o admin
            if (commento.get("ID_UTENTE").equals(objGenerici.getID_UTENTE()) || objGenerici.getUTENTE_NOME().equals("adm")) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem eliminaCommento = new MenuItem("✘ Elimina commento");
                eliminaCommento.setId(commento.get("ID_COMMENTO").toString());
                eliminaCommento.setOnAction(event -> {
                    cancellaCommentoSelezionato(eliminaCommento.getId());
                });
                contextMenu.getItems().add(eliminaCommento);
                box.setOnContextMenuRequested(event -> {
                    contextMenu.show(box, event.getScreenX(), event.getScreenY());
                });
            }
            VBox boxFinale = new VBox(10);
            boxFinale.getChildren().addAll(box);

            return boxFinale;
        }

    private VBox creaVBoxCommentoMinutaggio(Map<String, Object> commento) {
        String autore;
        Label autoreLabel;

        VBox box = new VBox();
        box.setSpacing(2);
        box.getStyleClass().add("commento");

        // Se l'utente che commenta è l'autore della canzone
        if(rowCanzone.get("AUTORE").toString().equals(commento.get("NOME").toString())) {
            autore = commento.get("NOME").toString() + " " + commento.get("COGNOME").toString()+ " (autore)";
            autoreLabel = new Label(autore);
            autoreLabel.setStyle("-fx-text-fill: #E5484D; -fx-font-weight: bold; -fx-font-size: 16; -fx-padding: 0 0 4 0;");
        } else {
            autore = commento.get("NOME").toString() + " " + commento.get("COGNOME").toString();
            autoreLabel = new Label(autore);
            autoreLabel.setStyle("-fx-text-fill: #6d24e1; -fx-font-weight: bold; -fx-font-size: 16; -fx-padding: 0 0 4 0;");
        }

        Label testoLabel = new Label(commento.get("TESTO").toString());
        testoLabel.setWrapText(true);
        testoLabel.setMaxWidth(400);
        testoLabel.setStyle("-fx-font-size: 14; -fx-line-spacing: 5; -fx-text-fill: #ffffff");

        box.getChildren().addAll(autoreLabel, testoLabel);
        box.setMaxWidth(Double.MAX_VALUE); // padre prende tutta la larghezza disponibile
        VBox.setMargin(box, new Insets(0, 0, 3, 0)); // spaziatura tra i commenti

        if (commento.get("ID_UTENTE").equals(objGenerici.getID_UTENTE()) || objGenerici.getUTENTE_NOME().equals("adm")) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem eliminaCommento = new MenuItem("✘ Elimina commento");
            eliminaCommento.setId(commento.get("ID_COMMENTO").toString());
            eliminaCommento.setOnAction(event -> {
                cancellaCommentoSelezionato(eliminaCommento.getId());
            });
            contextMenu.getItems().add(eliminaCommento);

            box.setOnContextMenuRequested(event -> {
                contextMenu.show(box, event.getScreenX(), event.getScreenY());
            });
        }

        return box;
    }

    private void cancellaCommentoSelezionato(String idCommento) {
        objSql.cancella("COMMENTI","ID_COMMENTO="+idCommento);
        caricaCommenti(ID_CANZONE);
    }


    public void rispondiCommento(int idCommento){
        // Controlliamo se il commento figlio è già collegato ad un figlio padre
        int idCommentoPadre;
        for(Map<String, Object> rowC : listaCommenti){
            if(rowC.get("ID_COMMENTO").equals(idCommento)) {
                rowCommento = rowC;
                break;
            }
        }

        // Controllo se qualcosa andasse storto
        if(rowCommento == null) return;


        // Mi ricavo l'id papà
        try{
            idCommentoPadre = Integer.parseInt(rowCommento.get("ID_PADRE").toString());
        }
        catch(NullPointerException e){
            idCommentoPadre = Integer.parseInt(rowCommento.get("ID_COMMENTO").toString());
        }

        rowCommento.put("ID_PADRE", idCommentoPadre);

        // preparo il tipo di commento = 1
        tipoCommento=1;

        // Cambio il bordo per far capire che si riferisce ad una risposta di un commento
        impostaTextArea(tipoCommento);

        // Sposto il mouse nella text area
        commentoTextArea.requestFocus();
        commentoTextArea.positionCaret(commentoTextArea.getText().length());
        }

        private void impostaTextArea(int parValore){
        if(parValore == 0) {
            commentoTextArea.setPromptText("Scrivi un commento...");
        }else{  // Se è una risposta
            commentoTextArea.setPromptText("Rispondi al commento...");
        }
        commentoTextArea.setStyle(commentoTextArea.getStyle() + "; -fx-border-width: 2.001;");  // Mi tocca usare questo trucchetto per forzare un repaint, se no rimanevano parti di testo nello sfondo trasparente...
    }

    //Banner per controllo degli errori
    private void erroreCommento(int errore) {
        String txt;
        switch (errore) {
            case 1 -> txt = "Impossibile inserire un range maggiore di 10 secondi";
            case 2 -> txt = "Attenzione!! Impostare un range interno alla musica";
            case 3 -> txt = "Attenzione!! Commento in sovrapposizione";
            default -> txt = "Errore generico durante l'inserimento";
        }
        ObjGenerici.mostraPopupErrore(commentoTextArea,txt);
    }

    public void controllaRangeCommento(int parMinutaggioCanzone) {
        // Controllo se il minutaggio attuale è ancora nel range corrente
         if (rangeInizioCorrente != -1 && rangeFineCorrente != -1) {
            if (parMinutaggioCanzone >= rangeInizioCorrente && parMinutaggioCanzone <= rangeFineCorrente) {
                 // Siamo ancora nel range attuale, niente da fare
                return;
             }
        }

         // Minutaggio non più nel range corrente, resetto tutto
        PaginaPaneCommenti_vBoxMinutaggio.getChildren().clear();
        commentoMostrato = null;
        rangeInizioCorrente = -1;
        rangeFineCorrente = -1;

        // Cerco il nuovo commento da mostrare
        for (Map<String, Object> commento : listaCommentiRange) {
             Object inizioObj = commento.get("RANGE_INIZIO");
              Object fineObj = commento.get("RANGE_FINE");

               if (inizioObj != null && fineObj != null) {
                  try {
                     int inizio = Integer.parseInt(inizioObj.toString());
                     int fine = Integer.parseInt(fineObj.toString());

                    if (parMinutaggioCanzone >= inizio && parMinutaggioCanzone <= fine) {
                       VBox box = creaVBoxCommentoMinutaggio(commento);
                       PaginaPaneCommenti_vBoxMinutaggio.getChildren().add(box);
                       PaginaPaneCommenti_vBoxMinutaggio.setPrefHeight(box.getPrefHeight());
                        PaginaPaneCommenti_vBoxMinutaggio.setMinHeight(box.getPrefHeight());

                        // Memorizzo lo stato corrente
                        commentoMostrato = box;
                        rangeInizioCorrente = inizio;
                        rangeFineCorrente = fine;

                         break;  // Esco appena trovato il commento valido
                    }
                  } catch (NumberFormatException e) {
                    System.err.println("Errore nel parsing del range per il commento ID: " + commento.get("ID_COMMENTO"));
                  }
               }
        }
    }

    @FXML
    private void chiudiCommenti(){
        mainController.nascondiCommenti(1);     // 1 indica la chiusura voluta dall'utente
    }
}