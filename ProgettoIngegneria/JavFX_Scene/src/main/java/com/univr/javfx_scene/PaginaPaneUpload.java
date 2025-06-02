package com.univr.javfx_scene;

import com.sun.jdi.connect.spi.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaginaPaneUpload {
    private  ObjSql objSql = ObjSql.oggettoSql();
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    private String autore, ripetiAutore, titolo, ripetiTitolo, link_youtube, anno_composizione;

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    // Metodo per tornare alla pagina principale
    public void annulla() throws IOException, CloneNotSupportedException {
        mainController.paginaPrincipale();
    }

    @FXML private TextField txtTitolo, txtRipetiTiolo;
    @FXML private TextField txtAutore, txtRipetiAutore;
    @FXML private TextField txtYouTube;
    @FXML private TextField txtAnno;
    @FXML private Label labelErrore;
    @FXML private ComboBox<String> comboGenere;

    @FXML private VBox paneAudio;
    @FXML private VBox paneTesto;
    @FXML private VBox paneCopertina;
/*
    private File selectedAudioFile;
    private File selectedTestoFile;
    private File selectedCopertinaFile;
*/

    // richiesta Upload Brano musicale
    public void richiediUploadBrano(){
        int errore = controllaDati();
        if(errore>0) {
            erroreUpload(errore);
            return;
        }

        uploadBrano();
    }

    //Controllo correttezza dei dati
    public int controllaDati(){
        int errore = 0;

        // Recupero i dati inseriti
        titolo = txtTitolo.getText();
        ripetiTitolo = txtRipetiTiolo.getText();
        autore = txtAutore.getText();
        ripetiAutore = txtRipetiAutore.getText();
        link_youtube = txtYouTube.getText();
        anno_composizione = txtAnno.getText();

        if(titolo.isEmpty() || autore.isEmpty())  return 1;

        if(!titolo.equals(ripetiTitolo)) return 2;

        if(!autore.equals(ripetiAutore))  return 3;

        return errore;
    }

    public void uploadBrano() {
        Map<String, Object> rowUtente = new LinkedHashMap<>();

        rowUtente.put("TITOLO", titolo);
        rowUtente.put("AUTORE", autore);
        rowUtente.put("LINK_YOUTUBE", link_youtube);
        rowUtente.put("ANNO", anno_composizione);

        ObjSql objSql = ObjSql.oggettoSql();
        int risultato=objSql.inserisci("UTENTI", rowUtente);

        // Vendor code che indica violazione di un vincolo -> titolo già presente nel database
        if(risultato==19){
            erroreUpload(risultato);
            return;
        }

        // Se ritorna 1 è andato tutto a buon fine
        /*
        if(risultato==1){
            try {
                mainController.mostraLabelIscrizione();
                mainController.impostaSchermata();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        */


    }

    private void erroreUpload(int errore) {
        labelErrore.setStyle("");
        labelErrore.setStyle("");

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

        labelErrore.setText(txt);
        labelErrore.setVisible(true);
    }
    
    /*
    public void inserisciBrano(String autore, int anno, String titolo, String link, String genere,
                               String audioPath, String testoPath, String copertinaPath) {
        String sql = "INSERT INTO brani(autore, anno, titolo, youtube_link, genere, audio_path, testo_path, copertina_path) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, autore);
            pstmt.setInt(2, anno);
            pstmt.setString(3, titolo);
            pstmt.setString(4, link);
            pstmt.setString(5, genere);
            pstmt.setString(6, audioPath);
            pstmt.setString(7, testoPath);
            pstmt.setString(8, copertinaPath);

            pstmt.executeUpdate();
            System.out.println("✅ Brano inserito correttamente!");

        } catch (SQLException e) {
            System.out.println("❌ Errore durante l'inserimento: " + e.getMessage());
        }
    }

    @FXML
    private void onUploadClick(ActionEvent event) {
        try {
            // 1. Raccogli dati dai campi
            String autore = txtAutore.getText().trim();
            int anno = Integer.parseInt(txtAnno.getText().trim());
            String titolo = txtTitolo.getText().trim();
            String link = txtYouTube.getText().trim();
            String genere = comboGenere.getValue();

            // 2. Percorsi file raccolti dal drag & drop
            String audioPath = paneAudio.getUserData() != null ? paneAudio.getUserData().toString() : "";
            String testoPath = paneTesto.getUserData() != null ? paneTesto.getUserData().toString() : "";
            String copertinaPath = paneCopertina.getUserData() != null ? paneCopertina.getUserData().toString() : "";

            // 3. Chiama la tua funzione
            inserisciBrano(autore, anno, titolo, link, genere, audioPath, testoPath, copertinaPath);

            // 4. Feedback
            System.out.println("Brano caricato con successo.");

        } catch (Exception e) {
            System.out.println("Errore durante l'inserimento: " + e.getMessage());
        }
    }
*/

}
