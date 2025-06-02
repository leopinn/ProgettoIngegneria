package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.UTENTI;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.*;

public class PaginaPaneImpostazioniAmministratore implements Initializable {
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }
    private  ObjSql objSql = ObjSql.oggettoSql();
    private  TableView<UTENTI> tabellautenti = new TableView<>();

    @FXML
    private TableView<UTENTI> PaginaPaneImpostazioniAmministrazione_tabelView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // All'avvio carico gli utenti registrati al programma, quelli che devono essere accettati ecc.
        List<Map<String, Object>> locListaUtente;
        String locQuery = "SELECT * FROM UTENTI";

        locListaUtente= objSql.leggiLista(locQuery);
        if(locListaUtente.isEmpty()) return; // Non dovrebbe succedere, ma metto un controllo

        // Creo le colonne
        TableColumn<UTENTI, String> locColID = new TableColumn<>("ID_UTENTE");
        locColID.setCellValueFactory(cellData -> cellData.getValue().ID_UTENTEProperty());

        TableColumn<UTENTI, String> locColNome = new TableColumn<>("NOME");
        locColNome.setCellValueFactory(cellData -> cellData.getValue().NOMEProperty());

        TableColumn<UTENTI, String> locColCognome = new TableColumn<>("COGNOME");
        locColCognome.setCellValueFactory(cellData -> cellData.getValue().COGNOMEProperty());

        TableColumn<UTENTI, String> locColEmail = new TableColumn<>("EMAIL");
        locColEmail.setCellValueFactory(cellData -> cellData.getValue().EMAILProperty());

        TableColumn<UTENTI, String> locColDataNascita = new TableColumn<>("DATA_NASCITA");
        locColDataNascita.setCellValueFactory(cellData -> cellData.getValue().DATA_NASCITAProperty());

        TableColumn<UTENTI, String> locColPassword = new TableColumn<>("PASSWORD");
        locColPassword.setCellValueFactory(cellData -> cellData.getValue().PASSWORDProperty());

        TableColumn<UTENTI, String> locColRuolo = new TableColumn<>("RUOLO");
        locColRuolo.setCellValueFactory(cellData -> cellData.getValue().RUOLOProperty());

        TableColumn<UTENTI, String> locColStato = new TableColumn<>("STATO");
        locColStato.setCellValueFactory(cellData -> cellData.getValue().STATOProperty());

        PaginaPaneImpostazioniAmministrazione_tabelView.getColumns().addAll(
                locColID, locColNome, locColCognome, locColEmail, locColDataNascita, locColPassword, locColRuolo, locColStato
        );

        // Dati da mostrare
        ObservableList<UTENTI> locListaUtenti = FXCollections.observableArrayList();
        for (Map<String, Object> riga : locListaUtente) {
            UTENTI ut = new UTENTI(
                    new SimpleStringProperty(String.valueOf(riga.get("ID_UTENTE"))),
                    new SimpleStringProperty(String.valueOf(riga.get("NOME"))),
                    new SimpleStringProperty(String.valueOf(riga.get("COGNOME"))),
                    new SimpleStringProperty(String.valueOf(riga.get("EMAIL"))),
                    new SimpleStringProperty(String.valueOf(riga.get("DATA_NASCITA"))),
                    new SimpleStringProperty(String.valueOf(riga.get("PASSWORD"))),
                    new SimpleStringProperty(String.valueOf(riga.get("RUOLO"))),
                    new SimpleStringProperty(String.valueOf(riga.get("STATO")))
            );
            locListaUtenti.add(ut);
        }

        // Assegna i dati alla tabella
        PaginaPaneImpostazioniAmministrazione_tabelView.setItems(locListaUtenti);
    }
}
