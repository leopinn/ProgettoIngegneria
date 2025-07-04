package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.CANZONE;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PaginaPaneUtente implements Initializable {
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    private  ObjSql objSql = ObjSql.oggettoSql();
    private List<Map<String, Object>> locListaCanzoni;

    @FXML private TableView<CANZONE> PaginaPaneUtente_tabelView;
    @FXML private Label PaginaPaneUtente_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        popolaLista();
    }

    public void popolaLista() {
        // All'avvio carico tutte le musiche dell'utente (o tutte le musiche in generale se si tratta di un amministratore
        leggiCanzoni();
        if(locListaCanzoni.isEmpty()) return; // Se un utente non avesse canzoni dove lui è l'autore

        PaginaPaneUtente_tabelView.getColumns().clear();

        // Creo le colonne
        TableColumn<CANZONE, Integer> colId = new TableColumn<>("ID_CANZONE");
        colId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getID_CANZONE()));

        TableColumn<CANZONE, String> colTitolo = new TableColumn<>("TITOLO");
        colTitolo.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTITOLO()));

        TableColumn<CANZONE, String> colAutore = new TableColumn<>("AUTORE");
        colAutore.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAUTORE()));

        TableColumn<CANZONE, String> colGenere = new TableColumn<>("GENERE");
        colGenere.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getGENERE()));

        TableColumn<CANZONE, Integer> colAnno = new TableColumn<>("ANNO_COMPOSIZIONE");
        colAnno.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getANNO_COMPOSIZIONE()));

        TableColumn<CANZONE, String> colYoutube = new TableColumn<>("LINK_YOUTUBE");
        colYoutube.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLINK_YOUTUBE()));

        TableColumn<CANZONE, String> colMusica = new TableColumn<>("PERCORSO_MUSICA");
        colMusica.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPERCORSO_MUSICA()));

        TableColumn<CANZONE, String> colPDF = new TableColumn<>("PERCORSO_PDF");
        colPDF.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPERCORSO_PDF()));

        TableColumn<CANZONE, String> colCopertina = new TableColumn<>("PERCORSO_COPERTINA");
        colCopertina.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPERCORSO_COPERTINA()));

        TableColumn<CANZONE, String> colutenteIns = new TableColumn<>("UTENTE_INS");
        colutenteIns.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getUTENTE_INS()));

        TableColumn<CANZONE, Integer> colIdUtente = new TableColumn<>("ID_UTENTE");
        colIdUtente.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getID_UTENTE()));

        PaginaPaneUtente_tabelView.getColumns().addAll(colId, colTitolo, colAutore, colGenere, colAnno, colYoutube, colMusica, colPDF, colCopertina, colutenteIns, colIdUtente);

        // Dati da mostrare
        ObservableList<CANZONE> locListaCanzoniObservable = FXCollections.observableArrayList();
        for (Map<String, Object> riga : locListaCanzoni) {
            CANZONE canzone = new CANZONE(
                    Integer.parseInt(riga.get("ID_CANZONE").toString()),
                    String.valueOf(riga.get("TITOLO")),
                    String.valueOf(riga.get("AUTORE")),
                    String.valueOf(riga.get("GENERE")),
                    Integer.parseInt(riga.get("ANNO_COMPOSIZIONE").toString()),
                    String.valueOf(riga.get("LINK_YOUTUBE")),
                    String.valueOf(riga.get("PERCORSO_MUSICA")),
                    String.valueOf(riga.get("PERCORSO_PDF")),
                    String.valueOf(riga.get("PERCORSO_COPERTINA")),
                    String.valueOf(riga.get("UTENTE_INS")),
                    Integer.parseInt(riga.get("ID_UTENTE").toString())
            );
            locListaCanzoniObservable.add(canzone);
        }


        // Assegna i dati alla tabella
        PaginaPaneUtente_tabelView.setItems(locListaCanzoniObservable);
        PaginaPaneUtente_tabelView.setRowFactory(tv -> {
            TableRow<CANZONE> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem itemRimuoviCanzone = new MenuItem("✘ Rimuovi");
            itemRimuoviCanzone.setOnAction(e -> {
                CANZONE canzone = row.getItem();
                String locWhere = " ID_CANZONE="+canzone.getID_CANZONE();
                objSql.cancella("CANZONE", locWhere);
                popolaLista();
            });


            contextMenu.getItems().addAll(itemRimuoviCanzone);
            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                row.setContextMenu(isEmpty ? null : contextMenu);
            });

            return row;
        });
    }

    private void leggiCanzoni(){
        String locQuery="";

        if(PaginaPaneLogin.UTENTE_NOME.equals("adm")) {
            locQuery = "SELECT * FROM CANZONE";
            PaginaPaneUtente_label.setText("Brani caricati sulla piattaforma");

        } else {    // Le canzoni caricate dall'utente corrente
            locQuery = "SELECT * FROM CANZONE WHERE ID_UTENTE = " + PaginaPaneLogin.ID_UTENTE;
        }

        locListaCanzoni= objSql.leggiLista(locQuery);
    }
}
