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

    @FXML
    private Label PaginaImpostazioni_labelNomeUtente;
    @FXML
    private TextField PaginaImpostazioni_textEmail;
    @FXML
    private PasswordField PaginaImpostazioni_textPassword;
    @FXML
    private Label PaginaPaneImpostazioni_labelRichiestaAccount;
    @FXML
    private StackPane PaginaPaneImpostazioni_stackPane;
    @FXML
    private Label PaginaPaneImpostazioni_labelAmministratore;

    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PaginaImpostazioni_labelNomeUtente.setText(PaginaPaneLogin.UTENTE_NOME);
        PaginaImpostazioni_textEmail.setText(PaginaPaneLogin.UTENTE_EMAIL);

        // Controllo se l'utente non è amministratore, nascondo le impostazioni
        Map<String, Object> rowUtente=objSql.leggi(String.format("SELECT * FROM UTENTI WHERE ID_UTENTE=%s", PaginaPaneLogin.ID_UTENTE));
        if(Integer.parseInt(rowUtente.get("RUOLO").toString())!=1)
            PaginaPaneImpostazioni_labelAmministratore.setVisible(false);
    }

    public void salvaProfilo(){
        String email, password;
        Map<String, Object> rowUtente = new LinkedHashMap<>();

        email=PaginaImpostazioni_textEmail.getText();
        if(!email.equals(PaginaPaneLogin.UTENTE_EMAIL)) {
            rowUtente.put("EMAIL", email);
        }

        String query= String.format("SELECT PASSWORD FROM UTENTI WHERE ID_UTENTE=%s", PaginaPaneLogin.ID_UTENTE);

        Map<String, Object> loc_row = objSql.leggi(query);
        password=PaginaImpostazioni_textPassword.getText();
        if(password.length()>0){
            if(!password.equals(loc_row.get("PASSWORD"))) {
                rowUtente.put("PASSWORD", password);
            }
        }

        if(!rowUtente.isEmpty()){
            String where = "WHERE ID_UTENTE="+PaginaPaneLogin.ID_UTENTE;
            objSql.aggiorna("UTENTI", where, rowUtente);
            PaginaPaneLogin.UTENTE_EMAIL=email; // Aggiorno la variabile globale
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
