package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.UTENTI;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PaginaPaneImpostazioniAmministratore implements Initializable {
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();
    private  ObjSql objSql = ObjSql.oggettoSql();

    private PaginaPaneImpostazioni mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    private StackPane stackPaneMainController;
    public void setMainController(PaginaPaneImpostazioni controller, StackPane stackPaneMainController) {
        this.mainController = controller;
        this.stackPaneMainController = stackPaneMainController;
    }

    @FXML private TableView<UTENTI> PaginaPaneImpostazioniAmministrazione_tabelView;
    @FXML private VBox PaginaPaneImpostazioniAmministrazione_vBox;
    private List<Map<String, Object>> listaUtenti;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*// All'avvio carico gli utenti registrati al programma, quelli che devono essere accettati ecc.
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
                    if(Integer.parseInt(utente.getID_UTENTE())==objGenerici.getID_UTENTE()){
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
                    if(Integer.parseInt(utente.getID_UTENTE())==objGenerici.getID_UTENTE()){
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
        });*/
        leggiUtenti();
        popolaLista();
    }

    private void leggiUtenti() {
        String locQuery="";

        locQuery = "SELECT * FROM UTENTI   WHERE NOME!='adm' ORDER BY NOME ASC";

        listaUtenti= objSql.leggiLista(locQuery);
    }

    public void popolaLista() {
        PaginaPaneImpostazioniAmministrazione_vBox.getChildren().clear();

        for (Map<String, Object> rowUtente : listaUtenti) {
            HBox riga = new HBox(10);
            riga.setStyle("""
                 -fx-background-color: #292929;
                 -fx-background-radius: 12;
                 -fx-border-radius: 12;
                 -fx-padding: 10;
                -fx-spacing: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0.2, 0, 2);
                
                -fx-min-height: 80;
                -fx-pref-height: 80;
                -fx-alignment: center;
                """);

            // Ricavo la copertina della canzone
            String locPath = ObjGenerici.ritornaFotoProfilo((rowUtente.get("ID_UTENTE").toString()));
            Image immagine = new Image(new File(locPath).toURI().toString());
            ImageView copertina = new ImageView(immagine);
            copertina.setFitWidth(60);
            copertina.setFitHeight(60);
            copertina.setPreserveRatio(false);

            Rectangle contenitore = new Rectangle(60, 60);
            contenitore.setArcWidth(10);
            contenitore.setArcHeight(10);

            copertina.setClip(contenitore); // In modo che l'immagine abbia i borsi smussati


            Label nomeBrano = new Label(getSafe(rowUtente, "NOME") + " • " +
                    getSafe(rowUtente, "COGNOME") + " • " +
                    getSafe(rowUtente, "EMAIL"));

            nomeBrano.setStyle(
                    """
                    -fx-text-fill: #cccccc;
                    -fx-font-size: 16;
                            """
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Imposto in primis le icone
            Image imageCestino = new Image(getClass().getResource("/immagini/iconaCestino.png").toExternalForm());
            Image imageCestinoHover = new Image(getClass().getResource("/immagini/iconaCestinoHover.png").toExternalForm());

            // Creo l'icona normale
            ImageView iconaCestino = new ImageView(imageCestino);
            iconaCestino.setFitWidth(25);
            iconaCestino.setFitHeight(25);
            iconaCestino.setPreserveRatio(true);
            iconaCestino.setSmooth(true);

            // Creo l'icona hover
            ImageView iconaCestinoHover = new ImageView(imageCestinoHover);
            iconaCestinoHover.setFitWidth(25);
            iconaCestinoHover.setFitHeight(25);
            iconaCestinoHover.setPreserveRatio(true);
            iconaCestinoHover.setSmooth(true);

            Label labelElimina = new Label();
            labelElimina.setGraphic(iconaCestino);
            labelElimina.setStyle("-fx-cursor: hand;");
            labelElimina.setOnMouseClicked(event -> sospendiUtente(rowUtente, riga));
            labelElimina.setOnMouseEntered(event -> labelElimina.setGraphic(iconaCestinoHover));
            labelElimina.setOnMouseExited(event -> labelElimina.setGraphic(iconaCestino));

            riga.getChildren().addAll(copertina, nomeBrano, spacer, labelElimina);
            PaginaPaneImpostazioniAmministrazione_vBox.getChildren().add(riga);
        }
    }

    private void sospendiUtente(Map<String, Object> rowUtente, HBox riga) {
        if (rowUtente != null) {
            String locWhere = "WHERE ID_UTENTE=" + rowUtente.get("ID_UTENTE").toString();

            // Imposto lo stato in SOSPESO
            rowUtente.put("STATO", 2);
            objSql.aggiorna("UTENTI", locWhere, rowUtente);
            objGenerici.mostraPopupSuccesso(PaginaPaneImpostazioniAmministrazione_vBox, "Attenzione!!\nUtente "+rowUtente.get("NOME").toString()+" sospeso con successo");
            //rowUtente.setSTATO(rowUtente.get("STATO").toString());
        }
    }

    @FXML
    private void indietro() {
        if (stackPaneMainController.getChildren().size() > 1) {
            stackPaneMainController.getChildren().remove(stackPaneMainController.getChildren().size() - 1);
        }
    }


    private String getSafe(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }
}
