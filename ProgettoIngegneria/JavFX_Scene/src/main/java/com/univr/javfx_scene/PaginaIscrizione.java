package com.univr.javfx_scene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.univr.javfx_scene.PaginaAvvio.stageLogin;

public class PaginaIscrizione {

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


    public void mostraPaginaLogin(MouseEvent par_event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaLogin.fxml"));
        root = loader.load();

        stage = (Stage) ((Node)par_event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();     // centro la scena rispetto lo schermo
        stage.show();
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

        // Recupero i dati inseriti
        nome = PaginaIscrizione_textNome.getText();
        cognome = PaginaIscrizione_textCognome.getText();
        email = PaginaIscrizione_textEmail.getText();
        ripetiEmail = PaginaIscrizione_textRipetiEmail.getText();
        password = PaginaIscrizione_textPassword.getText();
        ripetiPassword = PaginaIscrizione_textRipetiPassword.getText();
        dataNascita = PaginaIscrizione_dataNascita.getValue();

        if(nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || ripetiEmail.isEmpty() || password.isEmpty() || ripetiPassword.isEmpty() || dataNascita.lengthOfYear()==0)  return 1;

        if(!email.equals(ripetiEmail)) return 2;

        if(!password.equals(ripetiPassword))  return 3;

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

        if(risultato==1){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaLogin.fxml"));
                Parent root = loader.load();

                // Usa un nodo qualsiasi per ottenere lo stage
                Stage stage = (Stage) stageLogin.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
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
                txt = "Le email non corrispondono!";
                break;
            case 3:
                txt = "Le password non corrispondono!";
                break;
            default:
                txt = "Errore generico";
        }

        PaginaIscrizione_labelErrore.setText(txt);
        PaginaIscrizione_labelErrore.setVisible(true);
    }


}
