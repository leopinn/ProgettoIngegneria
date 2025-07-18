package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.*;

public class PaginaPaneUpload {
    private final ObjSql objSql = ObjSql.oggettoSql();
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();

    private StackPane mainController;
    private PaginaPaneUtente paneUtenteControllore;
    private String nomeUtente, autore, titolo, link_youtube, anno_composizione, genere, ruolo, strumenti;
    private int ID_CANZONE;
    private File fileMusica, fileCopertina, filePdf;        // Variabili temporanee per musica, copertina e pdf

    @FXML private TextField PaginaPaneUpload_textTitolo, PaginaPaneUpload_textLink, PaginaPaneUpload_textAnno, PaginaPaneUpload_textNuovoAutore,textNuovoStrumento, textNuovoGenere;
    @FXML private ComboBox<String> PaginaPaneUpload_comboGenere, PaginaPaneUpload_comboAutore, comboRuolo;
    @FXML private Label PaginaPaneUplaod_labelMusica, PaginaPaneUplaod_labelCopertina, PaginaPaneUplaod_labelPdf;
    @FXML private CheckBox checkboxUsaNomeUtente;
    @FXML private ListView<String> listStrumenti;
    @FXML private CheckBox is_concerto;

    public void setMainController(StackPane controller, PaginaPaneUtente paneUtenteControllore) {
        this.mainController = controller;
        this.paneUtenteControllore=paneUtenteControllore;
        this.nomeUtente = objGenerici.getUTENTE_NOME(); // recupera il nome utente
    }

    // Metodo per tornare alla pagina principale
    public void chiudiPaneUpload() throws IOException, CloneNotSupportedException {
        if (mainController.getChildren().size() > 1) {
            mainController.getChildren().removeLast();   // Rimuove l'ultimo pannello (paginaCanzone)

            // Ripristina lo sfondo
            Node sfondo = mainController.getChildren().getFirst();
            sfondo.setEffect(null);
            sfondo.setDisable(false);

            paneUtenteControllore.popolaLista();
        }
    }

    // richiesta Upload Brano musicale
    public void richiediInserimentoCanzone(){
        int errore = controllaDati();
        if (errore > 0) {
            erroreUpload(errore);
            return;
        }
        aggiungiCanzone();
    }

    //Controllo correttezza dei dati
    public int controllaDati() {
        int errore = 0;

        titolo = PaginaPaneUpload_textTitolo.getText();
        ruolo = comboRuolo.getValue();
        strumenti = String.join(",", listStrumenti.getSelectionModel().getSelectedItems());

        if (checkboxUsaNomeUtente.isSelected()) {
            autore = nomeUtente;
        } else {
            autore = PaginaPaneUpload_comboAutore.getValue();
        }

        link_youtube = PaginaPaneUpload_textLink.getText();
        anno_composizione = PaginaPaneUpload_textAnno.getText();
        genere = PaginaPaneUpload_comboGenere.getValue();

        if (titolo.isEmpty() || autore.isEmpty() || genere == null || genere.isEmpty())
            return 1;

        if ("Interprete".equals(ruolo) && listStrumenti.getSelectionModel().getSelectedItems().isEmpty())
            return 4;

        if (anno_composizione != null && anno_composizione.matches("\\d+")) {
            if (Integer.parseInt(anno_composizione) < 1900 || Integer.parseInt(anno_composizione) > Year.now().getValue())
                return 2;
        } else {
            anno_composizione = "";
        }

        if (fileMusica == null || fileCopertina == null)
            return 3;

        return errore;
    }

    // Gestione attivazione box varie
    @FXML
    public void initialize() {
        // Inizializza la combo dei generi con una lista predefinita (se non già fatto altrove)
        PaginaPaneUpload_comboGenere.setItems(FXCollections.observableArrayList(
                "Pop", "Rock", "Trap", "Jazz", "Hip Hop", "Classica", "Reggaetton", "Blues", "Elettronica", "Metal", "Country"
        ));

        List<Map<String, Object>> listaArtisti = objSql.leggiLista("SELECT * FROM CANZONE GROUP BY AUTORE");
        // Estraggo solo i campi degli autori
        List<String> listaNomiAutori = new ArrayList<String>();
        for(Map<String, Object> rowArtisti : listaArtisti) {
            listaNomiAutori.add(rowArtisti.get("AUTORE").toString());
        }

        PaginaPaneUpload_comboAutore.setItems(FXCollections.observableArrayList(listaNomiAutori));

        // Inizializza la ListView degli strumenti con strumenti predefiniti
        ObservableList<String> strumentiBase = FXCollections.observableArrayList(
                "Chitarra", "Pianoforte", "Batteria", "Violino", "Sax", "Voce", "Basso", "Synth"
        );
        listStrumenti.setItems(strumentiBase);
        listStrumenti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listStrumenti.setDisable(true); // disabilitata di default

        // Disattiva il campo autore se la checkbox è attiva
        checkboxUsaNomeUtente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            PaginaPaneUpload_comboAutore.setDisable(newVal);
            if (newVal) {
                PaginaPaneUpload_comboAutore.setValue(nomeUtente);
            } else {
                PaginaPaneUpload_comboAutore.setValue("");
            }
        });

        // Mostra la lista strumenti solo se si è "Interprete"
        comboRuolo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isInterprete = "Interprete".equals(newVal);
            listStrumenti.setDisable(!isInterprete);
            if (!isInterprete) {
                listStrumenti.getSelectionModel().clearSelection();
            }
        });
    }

    //Permette all'utente di aggiungere generi musicali
    @FXML
    private void aggiungiGenere() {
        String nuovoGenere = textNuovoGenere.getText().trim();

        if (nuovoGenere.isEmpty()) {
            ObjGenerici.mostraPopupErrore(PaginaPaneUpload_textTitolo,"Inserisci un nome per il nuovo genere");
            return;
        }

        // Controllo case-insensitive per evitare duplicati
        boolean locGiaPresente = PaginaPaneUpload_comboGenere.getItems().stream()
                .anyMatch(g -> g.equalsIgnoreCase(nuovoGenere));

        if (locGiaPresente) {
            ObjGenerici.mostraPopupErrore(PaginaPaneUpload_textTitolo,"Questo genere esiste già");
            return;
        } else{
            ObjGenerici.mostraPopupSuccesso(PaginaPaneUpload_textTitolo,"Elemento aggiunto");
            PaginaPaneUpload_comboGenere.getItems().add(nuovoGenere);
            PaginaPaneUpload_comboGenere.setValue(nuovoGenere); // Seleziona il nuovo
            textNuovoGenere.clear();
        }
    }

    @FXML
    private void aggiungiAutore() {
        String locNuovoAutore = PaginaPaneUpload_textNuovoAutore.getText().trim();

        if (locNuovoAutore.isEmpty()) {
            ObjGenerici.mostraPopupErrore(PaginaPaneUpload_textTitolo,"Inserisci un nome per il nuovo autore");
            return;
        }

        // Controllo case-insensitive per evitare duplicati
        boolean locGiaPresente = PaginaPaneUpload_comboGenere.getItems().stream()
                .anyMatch(g -> g.equalsIgnoreCase(locNuovoAutore));

        if (locGiaPresente) {
            ObjGenerici.mostraPopupErrore(PaginaPaneUpload_textTitolo,"Questo autore esiste già");
            return;
        } else{
            ObjGenerici.mostraPopupSuccesso(PaginaPaneUpload_textTitolo,"Elemento aggiunto");
            PaginaPaneUpload_comboGenere.getItems().add(locNuovoAutore);
            PaginaPaneUpload_comboGenere.setValue(locNuovoAutore);
            textNuovoGenere.clear();
        }
    }

    @FXML
    private void aggiungiStrumento() {
        String nuovoStrumento = textNuovoStrumento.getText().trim();

        if (nuovoStrumento.isEmpty()) {
            ObjGenerici.mostraPopupErrore(PaginaPaneUpload_textTitolo,"Inserisci un nome per lo strumento.");
            return;
        }

        boolean locGiaPresente = listStrumenti.getItems().stream()
                .anyMatch(s -> s.equalsIgnoreCase(nuovoStrumento));

        if (locGiaPresente) {
            ObjGenerici.mostraPopupErrore(PaginaPaneUpload_textTitolo, "Questo strumento è già presente nella lista.");
            return;
        }

        listStrumenti.getItems().add(nuovoStrumento);
        listStrumenti.getSelectionModel().select(nuovoStrumento); // seleziona subito il nuovo
        textNuovoStrumento.clear();
        ObjGenerici.mostraPopupSuccesso(PaginaPaneUpload_textTitolo,"Strumento aggiunto.");
    }

    public void aggiungiCanzone() {
        ruolo = comboRuolo.getValue();
        strumenti = String.join(",", listStrumenti.getSelectionModel().getSelectedItems());

        Map<String, Object> rowCanzone = new LinkedHashMap<>();

        rowCanzone.put("TITOLO", titolo);
        rowCanzone.put("AUTORE", autore);
        rowCanzone.put("GENERE", genere);
        rowCanzone.put("ANNO_COMPOSIZIONE", anno_composizione);
        rowCanzone.put("LINK_YOUTUBE", link_youtube);
        rowCanzone.put("RUOLO", ruolo);
        rowCanzone.put("STRUMENTI", strumenti);
        rowCanzone.put("UTENTE_INS", objGenerici.getUTENTE_NOME());
        rowCanzone.put("ID_UTENTE", objGenerici.getID_UTENTE());

        if(is_concerto.isSelected()) {
            rowCanzone.put("IS_CONCERTO", true);
        } else {
            rowCanzone.put("IS_CONCERTO", false);
        }

        int risultato = objSql.inserisci("CANZONE", rowCanzone);

        // Vendor code che indica violazione di un vincolo -> titolo già presente nel database
        if (risultato == 19) {
            erroreUpload(risultato);
            return;
        }

        // Adesso leggo la chiave della canzone e sposto le canzoni
        if (risultato == 1) {
            rowCanzone = objSql.leggi("SELECT ID_CANZONE FROM CANZONE ORDER BY ID_CANZONE DESC LIMIT 1");
            String locChiave = rowCanzone.get("ID_CANZONE").toString();
            ID_CANZONE = Integer.parseInt(locChiave);

            // Adesso prendo i file, li rinomino e li sposto
            if(fileMusica != null ) // Eseguo nuovamente i controlli per evitare sorprese
                richiediInserimentoCanzone(fileMusica, 0);
            if(filePdf != null )
                richiediInserimentoCanzone(filePdf, 1);
            if(fileCopertina != null )
                richiediInserimentoCanzone(fileCopertina, 2);

            mostraPopupSuccessoEtorna("Brano caricato con successo!");
        }
    }

    //Banner per controllo degli errori
    private void erroreUpload(int errore) {
        String txt;
        switch (errore) {
            case 1 -> txt = "Inserire tutti i campi obbligatori!";
            case 2 -> txt = "Anno non valido. Deve essere tra 1900 e l'anno corrente";
            case 3 -> txt = "Devi trascinare tutti i file richiesti (musica o copertina)";
            case 4 -> txt = "Inserire almeno uno strumento se il ruolo è 'Interprete'";
            case 19 -> txt = "Il seguente Titolo risulta già registrato!";
            default -> txt = "Errore generico durante l'inserimento";
        }
        ObjGenerici.mostraPopupErrore(PaginaPaneUpload_textTitolo, txt);
    }



    public void dragFile(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }

    public void dropCanzone(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().getFirst();

            // (opzionale) puoi mostrare anteprima con un label
            if (file.getName().toLowerCase().matches(".*\\.(mp3|mp4|midi)$")) {
                fileMusica = file;
                PaginaPaneUplaod_labelMusica.setText("File selezionato: " + file.getName());
             }
        }
        event.setDropCompleted(true);
        event.consume();
    }

    public void dropCopertina(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().getFirst();
        if (file.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
            fileCopertina = file;
            PaginaPaneUplaod_labelCopertina.setText("File selezionato: " + file.getName());
        }
        }
        event.setDropCompleted(true);
        event.consume();
    }

    public void dropPdf(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().getFirst();

            // (opzionale) puoi mostrare anteprima con un label
            if (file.getName().toLowerCase().matches(".*\\.(pdf|txt|doc|docx)$")) {
                filePdf = file;
                PaginaPaneUplaod_labelPdf.setText("File selezionato: " + file.getName());
            }
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void richiediInserimentoCanzone(File parFile, int parTipo) {
        if (parFile == null) {
            // mostra errore all’utente
            System.out.println("Nessun file trascinato");
            return;
        }

        String titolo = PaginaPaneUpload_textTitolo.getText().trim().replaceAll("[^a-zA-Z0-9]", "_");
        if (titolo.isEmpty()) titolo = "musica_" + System.currentTimeMillis();

        // Estensione del file originale (es. .mp3)
        String fileName = parFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String locCartella = switch (parTipo) {
            case 0 -> "musiche";
            case 1 -> "pdf";
            default -> "copertine";
        };

        File cartella = new File("upload/" + locCartella);
        if (!cartella.exists()) cartella.mkdirs();

        File dest = new File(cartella, ID_CANZONE + extension);
        try {
            Files.copy(parFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File salvato: " + dest.getAbsolutePath());
            // procedi con il resto dell’inserimento (nome, cognome, ecc.)
        } catch (IOException e) {
            e.printStackTrace();
            // mostra errore all’utente
        }
    }

    private void mostraPopupSuccessoEtorna(String messaggio) {
        Label contenuto = new Label(messaggio);
        contenuto.setStyle("""
        -fx-background-color: #28a745;
        -fx-text-fill: white;
        -fx-padding: 12px 24px;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);
    """);

        Popup popup = new Popup();
        popup.getContent().add(contenuto);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Window finestra = PaginaPaneUpload_textTitolo.getScene().getWindow();
        popup.show(finestra);

        // Posiziona in basso al centro
        popup.setX(finestra.getX() + finestra.getWidth() / 2 - 100);
        popup.setY(finestra.getY() + finestra.getHeight() - 100);

        try {
            chiudiPaneUpload();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        FadeTransition fade = new FadeTransition(Duration.seconds(3), contenuto);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2));

        fade.play();
    }

    @FXML
    private void abilitaDatiAggiuntivi(){
        comboRuolo.setDisable(!checkboxUsaNomeUtente.isSelected());

        // Se viene deselezionato, sbianco
        if(!checkboxUsaNomeUtente.isSelected()){
            comboRuolo.getSelectionModel().clearSelection();
            listStrumenti.getSelectionModel().clearSelection();
        }
    }
}