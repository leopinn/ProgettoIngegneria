package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.UTENTI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.net.URL;
import java.util.*;

public class PaginaPaneImpostazioniAmministratore implements Initializable {
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();
    private final ObjSql objSql = ObjSql.oggettoSql();

    private PaginaPaneImpostazioni mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    private StackPane stackPaneMainController;
    private List<Map<String, Object>> listaUtenti;

    @FXML private TableView<UTENTI> PaginaPaneImpostazioniAmministrazione_tabelView;
    @FXML private VBox PaginaPaneImpostazioniAmministrazione_vBox;

    public void setMainController(PaginaPaneImpostazioni controller, StackPane stackPaneMainController) {
        this.mainController = controller;
        this.stackPaneMainController = stackPaneMainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        impostaLista();
    }

    private void impostaLista(){
        leggiUtenti();
        popolaLista();
    }

    private void leggiUtenti() {
        String locQuery = "SELECT * FROM UTENTI   WHERE NOME!='adm' ORDER BY NOME ASC";
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


            Label nomeBrano = new Label(objGenerici.getSafe(rowUtente, "NOME") + " • " +
                    objGenerici.getSafe(rowUtente, "COGNOME") + " • " +
                    objGenerici.getSafe(rowUtente, "EMAIL"));

            nomeBrano.setStyle(
                    """
                    -fx-text-fill: #cccccc;
                    -fx-font-size: 16;
                    """
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Imposto in primis le icone
            Image imageSospendi = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaSospendi.png")).toExternalForm());
            Image imageSospendiHover = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaSospendiHover.png")).toExternalForm());
            Image imageAutorizza = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaAutorizza.png")).toExternalForm());
            Image imageAutorizzaHover = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaAutorizzaHover.png")).toExternalForm());


            ImageView icona, iconaHover;

            // In base allo stato dell'utente, capisco quale icona prendere
            if(Integer.parseInt(rowUtente.get("STATO").toString())!=1){
                icona = new ImageView(imageAutorizza);
                iconaHover = new ImageView(imageAutorizzaHover);
            } else {
                icona = new ImageView(imageSospendi);
                iconaHover = new ImageView(imageSospendiHover);
            }

            icona.setFitWidth(25);
            icona.setFitHeight(25);
            icona.setPreserveRatio(true);
            icona.setSmooth(true);

            iconaHover.setFitWidth(25);
            iconaHover.setFitHeight(25);
            iconaHover.setPreserveRatio(true);
            iconaHover.setSmooth(true);


            Label label = new Label();

            label.setGraphic(icona);
            label.setStyle("-fx-cursor: hand;");
            label.setOnMouseEntered(event -> label.setGraphic(iconaHover));
            label.setOnMouseExited(event -> label.setGraphic(icona));
            if(Integer.parseInt(rowUtente.get("STATO").toString())!=1){
                label.setOnMouseClicked(event -> {
                    autorizzautente(rowUtente, riga);
                    impostaLista();
                });
            } else {
                label.setOnMouseClicked(event -> {
                    sospendiUtente(rowUtente, riga);
                    impostaLista();
                });
            }

            riga.getChildren().addAll(copertina, nomeBrano, spacer, label);
            PaginaPaneImpostazioniAmministrazione_vBox.getChildren().add(riga);
        }
    }

    private void sospendiUtente(Map<String, Object> rowUtente, HBox riga) {
        if (rowUtente != null) {
            String locWhere = "WHERE ID_UTENTE=" + rowUtente.get("ID_UTENTE").toString();

            // Imposto lo stato in SOSPESO
            rowUtente.put("STATO", 2);
            objSql.aggiorna("UTENTI", locWhere, rowUtente);
            ObjGenerici.mostraPopupErrore(PaginaPaneImpostazioniAmministrazione_vBox, "Attenzione!! Utente "+rowUtente.get("NOME").toString()+" sospeso con successo");
        }
    }

    private void autorizzautente(Map<String, Object> rowUtente, HBox riga) {
        if (rowUtente != null) {
            String locWhere = "WHERE ID_UTENTE=" + rowUtente.get("ID_UTENTE").toString();

            // Imposto lo stato in AUTORIZZATO
            rowUtente.put("STATO", 1);
            objSql.aggiorna("UTENTI", locWhere, rowUtente);
            ObjGenerici.mostraPopupSuccesso(PaginaPaneImpostazioniAmministrazione_vBox, "Attenzione!! Utente "+rowUtente.get("NOME").toString()+" autorizzato con successo");
        }
    }

    @FXML
    private void indietro() {
        if (stackPaneMainController.getChildren().size() > 1) {
            stackPaneMainController.getChildren().removeLast();
        }
    }
}
