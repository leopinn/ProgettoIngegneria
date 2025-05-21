package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class PaginaPaneLogin {
    // Dichiarazione variabili pubbliche
    public static int UTENTE_ID;
    public static String UTENTE_NOME;

    @FXML
    TextField PaginaLogin_nameTextField;

    @FXML
    PasswordField PaginaLogin_passwordField;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private AnchorPane scenePane;
    @FXML
    private Button logoutButton;
    @FXML
    private Label PaginaLogin_labelErrore;
    @FXML
    private VBox PaginaPaneLogin_vBox;
    @FXML
    private StackPane PaginaLogin_stackPane;

    private PaginaLogin mainController;

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
        UTENTE_ID=loc_chiave;
        UTENTE_NOME=loc_username;

        return 0;
    }

    public void iscriviti(MouseEvent par_event) throws IOException {
        mainController.paginaIscriviti();
    }

    private void login(ActionEvent par_event, String par_usr, String par_psw) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPrincipale.fxml"));
        root = loader.load();

        PaginaPrincipale controller = loader.getController();

        stage = (Stage) ((Node)par_event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.show();
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

    public int getID(){
        return this.UTENTE_ID;
    }

    public void chiudiProgramma(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Stai per uscire dal programma!");
        alert.setContentText("Vuoi salvare i tuoi progressi prima di uscire?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) scenePane.getScene().getWindow();
            System.out.println("Programma chiuso con successo");
            stage.close();
        }
    }

    public void apriPaginaPrincipale(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("PaginaPrincipale.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();     // centro la scena rispetto lo schermo
        stage.show();
    }

}
