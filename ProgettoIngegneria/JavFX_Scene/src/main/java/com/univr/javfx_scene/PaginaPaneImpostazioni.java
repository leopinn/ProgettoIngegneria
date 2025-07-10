package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PaginaPaneImpostazioni implements Initializable {
    private  ObjSql objSql = ObjSql.oggettoSql();
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();

    @FXML private Label PaginaImpostazioni_labelNomeUtente, PaginaPaneImpostazioni_labelRichiestaAccount,PaginaPaneImpostazioni_labelAmministratore;
    @FXML private TextField PaginaImpostazioni_textEmail;
    @FXML private PasswordField PaginaImpostazioni_textPassword;
    @FXML private StackPane PaginaPaneImpostazioni_stackPane;

    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PaginaImpostazioni_labelNomeUtente.setText(objGenerici.getUTENTE_NOME());
        PaginaImpostazioni_textEmail.setText(objGenerici.getUTENTE_EMAIL());

        // Controllo se l'utente non è amministratore, nascondo le impostazioni
        Map<String, Object> rowUtente=objSql.leggi(String.format("SELECT * FROM UTENTI WHERE ID_UTENTE=%s", objGenerici.getID_UTENTE()));
        if(Integer.parseInt(rowUtente.get("RUOLO").toString())!=1)
            PaginaPaneImpostazioni_labelAmministratore.setVisible(false);
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
        if(password.length()>0){
            if(!password.equals(loc_row.get("PASSWORD"))) {
                rowUtente.put("PASSWORD", password);
            }
        }

        if(!rowUtente.isEmpty()){
            String where = "WHERE ID_UTENTE="+objGenerici.getID_UTENTE();
            objSql.aggiorna("UTENTI", where, rowUtente);
            objGenerici.putUTENTE_EMAIL(email); // Aggiorno la variabile globale
            mostraLabelIscrizione();
        }
    }

    public void annulla() throws IOException, CloneNotSupportedException {
        mainController.paginaPrincipale();
    }

    public void mostraLabelIscrizione() {
        // FADE IN
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), PaginaPaneImpostazioni_labelRichiestaAccount);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Assicura che il nodo sia visibile e gestito prima del fade-in
        PaginaPaneImpostazioni_stackPane.setVisible(true);
        PaginaPaneImpostazioni_stackPane.setManaged(true);

        fadeIn.play();
    }

    public void mostraImpostazioniAmministratore() throws IOException {
        mainController.PaginaPaneImpostazioniAmministratore();
    }
}
