package com.univr.javfx_scene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PaginaPaneImpostazioni implements Initializable {
    private final ObjSql objSql = ObjSql.oggettoSql();
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();

    @FXML private Label PaginaImpostazioni_labelNomeUtente, PaginaPaneImpostazioni_labelAmministratore;
    @FXML private TextField PaginaImpostazioni_textEmail;
    @FXML private PasswordField PaginaImpostazioni_textPassword;
    @FXML private HBox PaginaPaneImpostazioni_hboxImmagine;
    @FXML private ImageView PaginaPaneImpostazioni_fotoProfiloUtente;

    private StackPane stackPaneMainController;
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    public void setMainController(PaginaPrincipale controller, StackPane stackPaneMainController) {
        this.mainController = controller;
        this.stackPaneMainController = stackPaneMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PaginaImpostazioni_labelNomeUtente.setText(objGenerici.getUTENTE_NOME());
        PaginaImpostazioni_textEmail.setText(objGenerici.getUTENTE_EMAIL());

        // Inizio - gestione foto profilo

        caricaImmagineUtente();

        MenuItem caricaFoto = new MenuItem("Carica foto");
        caricaFoto.setOnAction(e -> {
            impostaImmagineProfilo();
        });

        ContextMenu contextMenu = new ContextMenu(caricaFoto);
        PaginaPaneImpostazioni_fotoProfiloUtente.setOnContextMenuRequested(event ->{
            contextMenu.show(PaginaPaneImpostazioni_fotoProfiloUtente, event.getScreenX(), event.getScreenY());
        });

        // Fine - gestione foto profilo

        // Controllo se l'utente non è amministratore, nascondo le impostazioni
        Map<String, Object> rowUtente=objSql.leggi(String.format("SELECT * FROM UTENTI WHERE ID_UTENTE=%s", objGenerici.getID_UTENTE()));
        if(Integer.parseInt(rowUtente.get("RUOLO").toString())!=1)
            PaginaPaneImpostazioni_labelAmministratore.setVisible(false);
    }

    private void caricaImmagineUtente(){
        String locPath= ObjGenerici.ritornaFotoProfilo(String.valueOf(objGenerici.getID_UTENTE()));

        Image immagine = new Image(new File(locPath).toURI().toString());
        PaginaPaneImpostazioni_fotoProfiloUtente.setImage(immagine);
        PaginaPaneImpostazioni_fotoProfiloUtente.setFitWidth(250);
        PaginaPaneImpostazioni_fotoProfiloUtente.setFitHeight(250);
        PaginaPaneImpostazioni_fotoProfiloUtente.setPreserveRatio(false);

        // Imposto un contenitore con i bordi smussati
        Rectangle contenitore = new Rectangle(250, 250);
        contenitore.setArcWidth(10);
        contenitore.setArcHeight(10);

        // Metto la foto nel contenitore
        PaginaPaneImpostazioni_fotoProfiloUtente.setClip(contenitore);
    }

    private void impostaImmagineProfilo(){
        // Richiedo il file da inserire
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli una foto profilo");

        // Filtro immagini
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Immagini (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        // Seleziona il file sorgente
        File sorgente = fileChooser.showOpenDialog(null);
        if (sorgente != null) {
            try {
                // Cartella di destinazione
                String nomeFile = objGenerici.getID_UTENTE() + ".png";
                File destinazione = new File("impostazioni/fotoProfilo/" + nomeFile);

                // Crea la cartella se non esiste
                destinazione.getParentFile().mkdirs();

                // Copia il file nella destinazione
                java.nio.file.Files.copy(
                        sorgente.toPath(),
                        destinazione.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                // Ricarica immagine profilo
                caricaImmagineUtente();
            } catch (IOException e) {
                e.printStackTrace();
                ObjGenerici.mostraPopupErrore(PaginaPaneImpostazioni_hboxImmagine,
                        "Errore durante il salvataggio della foto:\n" + e.getMessage());
            }
        }
    }

    public void salvaProfilo(){
        String email, password;
        Map<String, Object> rowUtente = new LinkedHashMap<>();

        email=PaginaImpostazioni_textEmail.getText();
        if(!email.equals(objGenerici.getUTENTE_EMAIL())) {
            rowUtente.put("EMAIL", email);
        }

        String query= String.format("SELECT PASSWORD FROM UTENTI WHERE ID_UTENTE=%s", objGenerici.getID_UTENTE());

        Map<String, Object> loc_row = objSql.leggi(query);
        password=PaginaImpostazioni_textPassword.getText();
        if(!password.isEmpty()){
            if(!password.equals(loc_row.get("PASSWORD"))) {
                rowUtente.put("PASSWORD", password);
            }
        }

        if(!rowUtente.isEmpty()){
            String where = "WHERE ID_UTENTE="+objGenerici.getID_UTENTE();
            objSql.aggiorna("UTENTI", where, rowUtente);
            objGenerici.putUTENTE_EMAIL(email); // Aggiorno la variabile globale
            ObjGenerici.mostraPopupSuccesso(PaginaPaneImpostazioni_hboxImmagine, "Modifiche salvate con successo!");
            PaginaImpostazioni_textPassword.setText("");
        }
    }

    public void annulla() throws IOException, CloneNotSupportedException {
        if (stackPaneMainController.getChildren().size() > 1) {
            stackPaneMainController.getChildren().removeLast();   // Rimuove l'ultimo pannello (paginaImpostazioni)

            // Ripristina lo sfondo
            Node sfondo = stackPaneMainController.getChildren().getFirst();
            sfondo.setEffect(null);
            sfondo.setDisable(false);
        }
    }

    public void mostraImpostazioniAmministratore() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneImpostazioniAmministratore.fxml"));
        Parent registerPane = loader.load();

        PaginaPaneImpostazioniAmministratore controller = loader.getController();
        controller.setMainController(this, stackPaneMainController);

        // Aggiunge impostazioniPane sopra
        stackPaneMainController.getChildren().add(registerPane);
    }
}
