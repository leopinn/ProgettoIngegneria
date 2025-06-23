package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.UTENTI;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;

public class PaginaPaneImpostazioniAmministratore implements Initializable {
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }
    private  ObjSql objSql = ObjSql.oggettoSql();

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
        PaginaPaneImpostazioniAmministrazione_tabelView.setRowFactory(tv -> {
            TableRow<UTENTI> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem autorizzaItem = new MenuItem("✔ Autorizza");
            autorizzaItem.setOnAction(e -> {
                UTENTI utente = row.getItem();
                // Per evitare che l'amministratore possa auto autorizzarsi o sospendersi
                if (utente != null) {
                    if(Integer.parseInt(utente.getID_UTENTE())==PaginaPaneLogin.ID_UTENTE){
                        mostraMessaggio("Attenzione!!","Impossibile modificare se stessi");
                        return;
                    }

                    String locWhere = "WHERE ID_UTENTE="+utente.getID_UTENTE();

                    Map<String, Object> rowUtente=objSql.leggi(String.format("SELECT * FROM UTENTI WHERE ID_UTENTE=%s", utente.getID_UTENTE()));
                    if(rowUtente.isEmpty()) return;

                    rowUtente.put("STATO", 1);
                    objSql.aggiorna("UTENTI",locWhere, rowUtente);
                    mostraMessaggio("Attenzione!!",String.format("Utente %s autorizzato con successo",utente.getNOME()));
                    utente.setSTATO(rowUtente.get("STATO").toString());
                }
            });

            MenuItem eliminaItem = new MenuItem("✘ Sospendi");
            eliminaItem.setOnAction(e -> {
                UTENTI utente = row.getItem();
                if (utente != null) {
                    if(Integer.parseInt(utente.getID_UTENTE())==PaginaPaneLogin.ID_UTENTE){
                        mostraMessaggio("Attenzione!!","Impossibile modificare se stessi");
                        return;
                    }

                    String locWhere = "WHERE ID_UTENTE="+utente.getID_UTENTE();

                    Map<String, Object> rowUtente=objSql.leggi(String.format("SELECT * FROM UTENTI WHERE ID_UTENTE=%s", utente.getID_UTENTE()));
                    if(rowUtente.isEmpty()) return;

                    rowUtente.put("STATO", 2);
                    objSql.aggiorna("UTENTI",locWhere, rowUtente);
                    mostraMessaggio("Attenzione!!",String.format("Utente %s sospeso con successo",utente.getNOME()));
                    utente.setSTATO(rowUtente.get("STATO").toString());
                }
            });

            contextMenu.getItems().addAll(autorizzaItem, eliminaItem);

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                row.setContextMenu(isEmpty ? null : contextMenu);
            });

            return row;
        });
    }

    public void mostraMessaggio(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);

        alert.showAndWait();
    }
}
