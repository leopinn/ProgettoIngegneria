package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class PaginaPaneLogin {
    private final ObjSql objSql = ObjSql.oggettoSql();     // Inizializzazione dell'oggetto per la prima volta

    @FXML private Label PaginaLogin_labelErrore;
    @FXML private PaginaLogin mainController;
    @FXML private TextField PaginaLogin_nameTextField;
    @FXML private PasswordField PaginaLogin_passwordField;

    // Per salvarmi l'istanza di PaginaLogin
    public void setMainController(PaginaLogin controller) {
        this.mainController = controller;
    }


    /* ---------- Inizio - AUTENTICAZIONE SUL PROGRAMMA ----------*/

    public void invio(ActionEvent event) throws IOException, SQLException {
        // Salvo in una variabile locale username e password
        String locUsername = PaginaLogin_nameTextField.getText();
        String locPassword = PaginaLogin_passwordField.getText();

        // Controlli su username e password. Se è diverso da 0, c'è un errore
        int locErr=controlliAutenticazione(locUsername, locPassword);
        if (locErr>0){
            erroreLogin(locErr);
            return;     // Usciamo dalla fase di login
        }

        // Login avvenuto con successo
        login(event, locUsername, locPassword);
    }

    private int controlliAutenticazione(String parUsr, String parPsw) throws SQLException {
        // Controlli base
        if (parUsr.isEmpty())
            return 1;  //Errore manca utente

        if (parPsw.isEmpty())
            return 2;  //Errore manca password

        // Creo la query tramite text block per prendere l'utente
        String query= String.format("""
                SELECT *
                FROM UTENTI
                WHERE 1=1
                AND NOME="%s"
                AND PASSWORD='%s'
                """, parUsr, parPsw);

        // Leggo nel database se trovo l'utente
        Map<String, Object> locRowUtente = objSql.leggi(query);
        if (locRowUtente == null) return 3;   // Utente non trovato per i dati inseriti

        int locStato = (int) locRowUtente.get("STATO");     //0=DA AUTORIZZARE, 1=AUTORIZZATO, 2=SOSPESO
        if(locStato!=1) return 4;       // Utente non autorizzato all'accesso

        // Se è andato tutto a buon fine, istanzio anche l'objGenerici e salvo le variabili globali
        int locIdUtente;
        String locUsername, locEmail;

        locIdUtente = (int) locRowUtente.get("ID_UTENTE");
        locUsername = (String) locRowUtente.get("NOME");
        locEmail = (String) locRowUtente.get("EMAIL");

        // Inizializzo l'oggetto generico
        ObjGenerici locObjGenerici=new ObjGenerici(locIdUtente, locUsername, locEmail);

        return 0;
    }

    // Se viene chiamato questo metodo, significa che è andato tutto a buon fine e tutti i dati che mi servono sono stati letti e salvati
    private void login(ActionEvent par_event, String par_usr, String par_psw) throws IOException {
        mainController.paginaPrincipale();
    }

    /* ---------- Fine - AUTENTICAZIONE SUL PROGRAMMA ----------*/


    public void iscriviti(MouseEvent par_event) throws IOException {
        mainController.paginaIscriviti();
    }

    private void erroreLogin(int par_err) {
        PaginaLogin_nameTextField.setStyle("");
        PaginaLogin_passwordField.setStyle("");

        String loc_txt="";
        switch (par_err) {
            case 1:
                loc_txt = "Inserire l'username";
                PaginaLogin_nameTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 7;");
                break;
            case 2:
                loc_txt = "Inserire la password";
                PaginaLogin_passwordField.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 7;");
                break;
            case 3:
                loc_txt = "Utente e/o password errati";
                break;
            case 4:
                loc_txt = "Utente non autorizzato. Contattare un amministratore!";
                break;
            default:
                loc_txt = "Errore generico";
        }

        PaginaLogin_labelErrore.setText(loc_txt);
        PaginaLogin_labelErrore.setVisible(true);
    }
}
