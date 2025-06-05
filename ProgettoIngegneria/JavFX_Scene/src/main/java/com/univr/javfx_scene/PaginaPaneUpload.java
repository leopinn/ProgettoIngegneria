package com.univr.javfx_scene;

import com.sun.jdi.connect.spi.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
/** import java.lang.classfile.instruction.SwitchCase; **/
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaginaPaneUpload {
    private  ObjSql objSql = ObjSql.oggettoSql();
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    private String destFile="upload/musiche";
    private String autore, ripetiAutore, titolo, ripetiTitolo, link_youtube, anno_composizione, genere;
    private int ID_CANZONE;

    // Variabili temporanee per musica, copertina e pdf
    private File fileMusica, fileCopertina, filePdf;


    @FXML private TextField PaginaPaneUpload_textTitolo, PaginaPaneUpload_textAutore, PaginaPaneUpload_textLink, PaginaPaneUpload_textAnno;
    @FXML private ComboBox<String> PaginaPaneUpload_comboGenere;
    @FXML private Label PaginaPaneUplaod_labelMusica, PaginaPaneUplaod_labelCopertina, PaginaPaneUplaod_labelPdf, PaginaIscrizione_labelErrore;

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    // Metodo per tornare alla pagina principale
    public void annulla() throws IOException, CloneNotSupportedException {
        mainController.paginaPrincipale();
    }

    // richiesta Upload Brano musicale
    public void richiediInserimentoCanzone(){
        int errore = controllaDati();
        if(errore>0) {
            erroreUpload(errore);
            return;
        }

        aggiungiCanzone();
    }

    //Controllo correttezza dei dati
    public int controllaDati(){
        int errore = 0;

        // Recupero i dati inseriti
        titolo = PaginaPaneUpload_textTitolo.getText();
        autore = PaginaPaneUpload_textAutore.getText();
        link_youtube = PaginaPaneUpload_textLink.getText();
        anno_composizione = PaginaPaneUpload_textAnno.getText();
        genere = PaginaPaneUpload_comboGenere.getValue();

        if(titolo.isEmpty() || autore.isEmpty() || genere.isEmpty())  return 1;

        if(anno_composizione != null && anno_composizione.matches("\\d+")) {
            if (Integer.parseInt(anno_composizione) < 1900 || Integer.parseInt(anno_composizione) > Year.now().getValue())
                return 2;
        }else{
            anno_composizione="";
        }

        if(!fileMusica.exists() || !fileCopertina.exists()) return 3;

        return errore;
    }

    public void aggiungiCanzone() {
        Map<String, Object> rowCanzone = new LinkedHashMap<>();

        rowCanzone.put("TITOLO", titolo);
        rowCanzone.put("AUTORE", autore);
        rowCanzone.put("GENERE", genere);
        rowCanzone.put("ANNO_COMPOSIZIONE", anno_composizione);
        rowCanzone.put("LINK_YOUTUBE", link_youtube);

        ObjSql objSql = ObjSql.oggettoSql();
        int risultato=objSql.inserisci("CANZONE", rowCanzone);

        // Vendor code che indica violazione di un vincolo -> titolo già presente nel database
        if(risultato==19){
            erroreUpload(risultato);
            return;
        }

        // Se ritorna 1 è andato tutto a buon fine
        if(risultato==1){
           /* try {
                mainController.mostraLabelIscrizione();
               mainController.impostaSchermata();
            }catch (IOException e){
                e.printStackTrace();
            }*/
            // Adesso leggo la chiave della canzone e sposto le canzoni
            rowCanzone = objSql.leggi("SELECT ID_CANZONE FROM CANZONE ORDER BY ID_CANZONE DESC LIMIT 1");
            String locChiave= rowCanzone.get("ID_CANZONE").toString();
            ID_CANZONE = Integer.parseInt(locChiave);

            // Adesso prendo i file, li rinomino e li sposto
            richiediInserimentoCanzone(fileMusica, 0);
            richiediInserimentoCanzone(filePdf, 1);
            richiediInserimentoCanzone(fileCopertina, 2);

        }
    }

    private void erroreUpload(int errore) {
        String txt="";
        switch (errore) {
            case 1:
                txt = "Inserire tutti i campi obbligatori!";
                break;
            case 19:
                txt = "Il seguente Titolo risulta già registrato!";
                break;
            default:
                txt = "Errore generico";
        }
    }

    public void dragFile(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    public void dropCanzone(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            if (file.getName().toLowerCase().endsWith(".mp3")) {
                fileMusica = file; // memorizziamo il file
                System.out.println("File caricato: " + file.getName());

                // (opzionale) puoi mostrare anteprima con un label
                PaginaPaneUplaod_labelMusica.setText("File selezionato: " + file.getName());
            }
        }
        event.setDropCompleted(true);
        event.consume();
    }

    public void dropCopertina(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            if (file.getName().toLowerCase().endsWith(".jpg")) {
                fileCopertina = file; // memorizziamo il file
                System.out.println("File caricato: " + file.getName());

                // (opzionale) puoi mostrare anteprima con un label
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
            if (file.getName().toLowerCase().endsWith(".pdf")) {
                filePdf= file; // memorizziamo il file
                System.out.println("File caricato: " + file.getName());

                // (opzionale) puoi mostrare anteprima con un label
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
        String locCartella="";

        if(parTipo==0)  locCartella="musiche";
        else if(parTipo==1) locCartella="pdf";
        else locCartella="copertine";

        File cartellaMusiche = new File("upload/"+locCartella);
        if (!cartellaMusiche.exists()) {
            cartellaMusiche.mkdirs(); // crea se mancante
        }

       File dest = new File("upload/" + locCartella+"/" + ID_CANZONE + extension);
        try {
            Files.copy(parFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File salvato: " + dest.getAbsolutePath());
            // procedi con il resto dell’inserimento (nome, cognome, ecc.)

            PaginaPaneUpload_textTitolo.clear();
            PaginaPaneUpload_textAutore.clear();
            PaginaPaneUpload_textAnno.clear();
            PaginaPaneUpload_textLink.clear();
            PaginaPaneUpload_comboGenere.getSelectionModel().clearSelection();

            PaginaPaneUplaod_labelMusica.setText("Trascina qui il tuo file audio");
            PaginaPaneUplaod_labelPdf.setText("Trascina qui il tuo file pdf");
            PaginaPaneUplaod_labelCopertina.setText("Trascina qui il tuo file jpg");

            PaginaIscrizione_labelErrore.setText("Musica inserita con successo!");
            PaginaIscrizione_labelErrore.setStyle("-fx-text-fill: green;");
            PaginaIscrizione_labelErrore.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
            // mostra errore all’utente
        }
    }
}


