package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaginaPaneIscrizione {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String nome, cognome, email, ripetiEmail, password, ripetiPassword;
    private LocalDate dataNascita;

    @FXML
    TextField PaginaIscrizione_textNome, PaginaIscrizione_textCognome, PaginaIscrizione_textEmail, PaginaIscrizione_textRipetiEmail;
    @FXML
    PasswordField PaginaIscrizione_textPassword, PaginaIscrizione_textRipetiPassword;
    @FXML
    DatePicker PaginaIscrizione_dataNascita;
    @FXML
    Label PaginaIscrizione_labelErrore;
    @FXML
    VBox paginaIscrizione_vBox;

    private PaginaLogin mainController;

    public void setMainController(PaginaLogin controller) {
        this.mainController = controller;
    }

    public void mostraPaginaLogin(MouseEvent par_event) throws IOException {
        mainController.impostaSchermata();
    }

    public void richiediIscrizione(){
        int errore = controllaDati();
        if(errore>0) {
            erroreIscrizione(errore);
            return;
        }

        iscriviUtente();
    }

    public int controllaDati(){
        int errore = 0;
        LocalDate dataMinima = LocalDate.of((LocalDate.now().getYear()-14), 1, 1); // anno, mese, giorno

        // Recupero i dati inseriti
        nome = PaginaIscrizione_textNome.getText();
        cognome = PaginaIscrizione_textCognome.getText();
        email = PaginaIscrizione_textEmail.getText();
        ripetiEmail = PaginaIscrizione_textRipetiEmail.getText();
        password = PaginaIscrizione_textPassword.getText();
        ripetiPassword = PaginaIscrizione_textRipetiPassword.getText();
        dataNascita = (LocalDate) PaginaIscrizione_dataNascita.getValue();

        if(nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || ripetiEmail.isEmpty() || password.isEmpty() || ripetiPassword.isEmpty() || dataNascita.lengthOfYear()==0)  return 1;

        if(!email.equals(ripetiEmail)) return 2;

        if(!password.equals(ripetiPassword))  return 3;

        if(dataNascita.isAfter(dataMinima))  return 4;

        if(password.length()<4) return 5;

        if(!email.contains("@")) return 6;

        return errore;
    }

    public void iscriviUtente() {
        Map<String, Object> rowUtente = new LinkedHashMap<>();

        rowUtente.put("NOME", nome);
        rowUtente.put("COGNOME", cognome);
        rowUtente.put("EMAIL", email);
        rowUtente.put("DATA_NASCITA", dataNascita);
        rowUtente.put("PASSWORD", password);
        rowUtente.put("RUOLO", 0);  //0=UTENTE NORMALE, 1=ADMIN
        rowUtente.put("STATO", 0);  //0=DA AUTORIZZARE, 1=AUTORIZZATO, 2=SOSPESO

        ObjSql objSql = ObjSql.oggettoSql();
        int risultato=objSql.inserisci("UTENTI", rowUtente);

        // Vendor code che indica violazione di un vincolo -> email già presente nel database
        if(risultato==19){
            erroreIscrizione(risultato);
            return;
        }

        // Se ritorna 1 è andato tutto a buon fine
        if(risultato==1){
            try {
                mainController.mostraLabelIscrizione();
                mainController.impostaSchermata();
            }catch (IOException e){
                e.printStackTrace();
            }
        }


    }

    private void erroreIscrizione(int errore) {
        PaginaIscrizione_labelErrore.setStyle("");
        PaginaIscrizione_labelErrore.setStyle("");

        String txt="";
        switch (errore) {
            case 1:
                txt = "Inserire tutti i campi obbligatori!";
                break;
            case 2:
                txt = "Le e-mail non corrispondono!";
                break;
            case 3:
                txt = "Le password non corrispondono!";
                break;
            case 4:
                txt = "Inserire una data valida!";
                break;
            case 5:
                txt = "La password deve contenere minimo 4 caratteri!";
                break;
            case 6:
                txt = "Inserire una e-mail valida!";
                break;
            case 19:
                txt = "La seguente e-mail risulta già registrata!";
                break;
            default:
                txt = "Errore generico";
        }

        PaginaIscrizione_labelErrore.setText(txt);
        PaginaIscrizione_labelErrore.setVisible(true);
    }
}
