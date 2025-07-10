package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class PaginaPaneLogin {
    // Dichiaro l'oggetto generico che poi inizializzo
    private ObjGenerici objGenerici;

    // Dichiarazione variabili pubbliche
    private static int ID_UTENTE;
    private static String UTENTE_PASSWORD;
    private static String UTENTE_NOME;
    private static String UTENTE_EMAIL;

    private Stage stage;

    @FXML private Label PaginaLogin_labelErrore;
    @FXML private PaginaLogin mainController;
    @FXML private TextField PaginaLogin_nameTextField;
    @FXML private PasswordField PaginaLogin_passwordField;

    public void setMainController(PaginaLogin controller) {
        this.mainController = controller;
    }

    public void invio(ActionEvent event) throws IOException, SQLException {
        // Salvo in una variabile username e password
        String loc_username = PaginaLogin_nameTextField.getText();
        String loc_password = PaginaLogin_passwordField.getText();

        // Controlli su username e password. Se è diverso da 0, c'è un errore
        int loc_err=controlli(loc_username, loc_password);
        if (loc_err!=0){
            erroreLogin(loc_err);
            return;
        }

        // Login avvenuto con successo
        login(event, loc_username, loc_password);
    }

    private int controlli(String par_usr, String par_psw) throws SQLException {
        // Controlli base
        if (par_usr.length()<=0)
            return 1;  //Errore manca utente

        if (par_psw.length()<=0)
            return 2;  //Errore manca password

        ObjSql objSql = ObjSql.oggettoSql();  // Creo l'oggetto connettendom al server

        // Creo la query tramite text block per prendere l'utente
        String query= String.format("""
                SELECT *
                FROM UTENTI
                WHERE 1=1
                AND NOME="%s"
                AND PASSWORD='%s'
                """, par_usr, par_psw);

        Map<String, Object> loc_row = objSql.leggi(query);  // Eseguo la query di lettura dell'utente
        if (loc_row == null) return 3;   // Errore utente o password errati

        int stato = (int) loc_row.get("STATO"); //0=DA AUTORIZZARE, 1=AUTORIZZATO, 2=SOSPESO
        if(stato!=1) return 4;       // Utente non autorizzato

        // Se è andato tutto a buon fine, salvo in variabili globali ID_UTENTE e NOME
        int loc_chiave = (int) loc_row.get("ID_UTENTE");
        String loc_username = (String) loc_row.get("NOME");
        int idUtente=loc_chiave;
        String utenteNome=loc_username;
        String utenteEmail = (String) loc_row.get("EMAIL");

        // Inizializzo l'oggetto generico
        objGenerici=new ObjGenerici(idUtente, utenteNome, utenteEmail);

        return 0;
    }

    public void iscriviti(MouseEvent par_event) throws IOException {
        mainController.paginaIscriviti();
    }

    private void login(ActionEvent par_event, String par_usr, String par_psw) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPrincipale.fxml"));
            Parent root = loader.load();

            // Ottengo lo stage corrente dalla scena del bottone
            stage = (Stage) ((Node)par_event.getSource()).getScene().getWindow();

            // Creo una nuova scena
            Scene nuovaScene = new Scene(root);
            stage.setScene(nuovaScene);

            // Questi passaggi servono per prendere le dimensioni dello schermo e creare uno "schermo intero appena più piccolo"
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            stage.show();

            // Una volta mostrata la scena, imposto la scena a schermo intero
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
