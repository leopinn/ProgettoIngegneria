package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.univr.javfx_scene.PaginaPaneLogin.UTENTE_NOME;

public class PaginaPaneUpload {
    private PaginaPrincipale mainController;
    private String autore, titolo, link_youtube, anno_composizione, genere, ruolo, strumenti;
    private int ID_CANZONE;

    // Variabili temporanee per musica, copertina e pdf
    private File fileMusica, fileCopertina, filePdf;


    @FXML private TextField PaginaPaneUpload_textTitolo, PaginaPaneUpload_textAutore, PaginaPaneUpload_textLink, PaginaPaneUpload_textAnno;
    @FXML private ComboBox<String> PaginaPaneUpload_comboGenere;
    @FXML private Label PaginaPaneUplaod_labelMusica, PaginaPaneUplaod_labelCopertina, PaginaPaneUplaod_labelPdf;
    @FXML private CheckBox checkboxUsaNomeUtente;
    @FXML private ComboBox<String> comboRuolo;
    @FXML private TextField textStrumenti;


    private String nomeUtente;

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
        this.nomeUtente = UTENTE_NOME; // recupera il nome utente
    }


    // Metodo per tornare alla pagina principale
    public void annulla() throws IOException, CloneNotSupportedException {
        mainController.paginaPrincipale();
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
        strumenti = textStrumenti.getText();

        if (checkboxUsaNomeUtente.isSelected()) {
            autore = nomeUtente;
        } else {
            autore = PaginaPaneUpload_textAutore.getText();
        }

        link_youtube = PaginaPaneUpload_textLink.getText();
        anno_composizione = PaginaPaneUpload_textAnno.getText();
        genere = PaginaPaneUpload_comboGenere.getValue();

        if (titolo.isEmpty() || autore.isEmpty() || genere == null || genere.isEmpty() || ruolo == null)
            return 1;

        if ("Interprete".equals(ruolo) && (strumenti == null || strumenti.isBlank()))
            return 4;

        if (anno_composizione != null && anno_composizione.matches("\\d+")) {
            if (Integer.parseInt(anno_composizione) < 1900 || Integer.parseInt(anno_composizione) > Year.now().getValue())
                return 2;
        } else {
            anno_composizione = "";
        }

        if (fileMusica == null || filePdf == null || fileCopertina == null)
            return 3;

        return errore;
    }


    // Gestione attivazione box varie
    @FXML
    public void initialize() {
        // Inizialmente disabilita il campo strumenti
        textStrumenti.setDisable(true);

        // Disattiva il campo autore se la checkbox è attiva
        checkboxUsaNomeUtente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            PaginaPaneUpload_textAutore.setDisable(newVal);
            if (newVal) {
                PaginaPaneUpload_textAutore.setText(nomeUtente);
            } else {
                PaginaPaneUpload_textAutore.clear();
            }
        });

        // Abilita il campo strumenti se si seleziona "Interprete"
        comboRuolo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isInterprete = "Interprete".equals(newVal);
            textStrumenti.setDisable(!isInterprete);
            if (!isInterprete) {
                textStrumenti.clear();
            }
        });
    }


    public void aggiungiCanzone() {
        ruolo = comboRuolo.getValue();
        strumenti = textStrumenti.getText();

        Map<String, Object> rowCanzone = new LinkedHashMap<>();

        rowCanzone.put("TITOLO", titolo);
        rowCanzone.put("AUTORE", autore);
        rowCanzone.put("GENERE", genere);
        rowCanzone.put("ANNO_COMPOSIZIONE", anno_composizione);
        rowCanzone.put("LINK_YOUTUBE", link_youtube);
        rowCanzone.put("RUOLO", ruolo);
        rowCanzone.put("STRUMENTI", strumenti);
        rowCanzone.put("UTENTE_INS", PaginaPaneLogin.UTENTE_NOME);
        rowCanzone.put("ID_UTENTE", PaginaPaneLogin.ID_UTENTE);

        ObjSql objSql = ObjSql.oggettoSql();
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
            richiediInserimentoCanzone(fileMusica, 0);
            richiediInserimentoCanzone(filePdf, 1);
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
            case 3 -> txt = "Devi trascinare tutti i file richiesti (musica, pdf, copertina)";
            case 4 -> txt = "Inserire almeno uno strumento se il ruolo è 'Interprete'";
            case 19 -> txt = "Il seguente Titolo risulta già registrato!";
            default -> txt = "Errore generico durante l'inserimento";
        }
        mostraPopupErrore(txt);
    }



    public void dragFile(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }

    public void dropCanzone(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);

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
            File file = db.getFiles().get(0);                                // (opzionale) puoi mostrare anteprima con un label
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
            File file = db.getFiles().get(0);

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
            mainController.paginaPrincipale(); // Torna al pane principale
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        FadeTransition fade = new FadeTransition(Duration.seconds(3), contenuto);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2));

        fade.play();
    }

    private void mostraPopupErrore(String messaggio) {
        Label contenuto = new Label(messaggio);
        contenuto.setStyle("""
        -fx-background-color: #dc3545;
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

        popup.setX(finestra.getX() + finestra.getWidth() / 2 - 100);
        popup.setY(finestra.getY() + finestra.getHeight() - 100);

        FadeTransition fade = new FadeTransition(Duration.seconds(3), contenuto);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2));
        fade.play();
    }

}