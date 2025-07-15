package com.univr.javfx_scene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class PaginaPaneUtente implements Initializable {
    private final ObjSql objSql = ObjSql.oggettoSql();
    private final ObjGenerici objGenerici=ObjGenerici.oggettoGenerico();

    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale
    private List<Map<String, Object>> listaBrani;

    @FXML private AnchorPane PaginaPaneUtente_anteprimaCanzone;
    @FXML private VBox PaginaPaneUtente_vboxCanzoni;
    @FXML private Label PaginaPaneUtente_label;

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        leggiBrani();
        popolaLista();
    }

    private void leggiBrani(){
        String locQuery="";

        if(objGenerici.getUTENTE_NOME().equals("adm")) {
            locQuery = "SELECT * FROM CANZONE ORDER BY AUTORE ASC";
            PaginaPaneUtente_label.setText("Canzoni caricate sulla piattaforma");

        } else {    // Le canzoni caricate dall'utente corrente
            locQuery = "SELECT * FROM CANZONE WHERE ID_UTENTE = " + objGenerici.getID_UTENTE() + " ORDER BY AUTORE ASC";
            PaginaPaneUtente_label.setText("Le canzoni che hai caricato");
        }

        listaBrani= objSql.leggiLista(locQuery);
    }

    public void popolaLista() {
        PaginaPaneUtente_vboxCanzoni.getChildren().clear();

        for (Map<String, Object> rowBrano : listaBrani) {
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
            String locPath = ObjGenerici.ritornaCopertina(Integer.parseInt(rowBrano.get("ID_CANZONE").toString()));
            Image immagine = new Image(new File(locPath).toURI().toString());
            ImageView copertina = new ImageView(immagine);
            copertina.setFitWidth(60);
            copertina.setFitHeight(60);
            copertina.setPreserveRatio(false);

            Rectangle contenitore = new Rectangle(60, 60);
            contenitore.setArcWidth(10);
            contenitore.setArcHeight(10);

            copertina.setClip(contenitore); // In modo che l'immagine abbia i borsi smussati


            Label nomeBrano = new Label(getSafe(rowBrano, "TITOLO") + " • " +
                    getSafe(rowBrano, "AUTORE") + " • " +
                    getSafe(rowBrano, "GENERE") + " • " +
                    getSafe(rowBrano, "ANNO_COMPOSIZIONE"));

            nomeBrano.setStyle(
                    """
                    -fx-text-fill: #cccccc;
                    -fx-font-size: 16;
                    """
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Imposto in primis le icone
            Image imageCestino = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaCestino.png")).toExternalForm());
            Image imageCestinoHover = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaCestinoHover.png")).toExternalForm());
            Image imageAnteprima = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaAnteprima.png")).toExternalForm());
            Image imageAnteprimaHover = new Image(Objects.requireNonNull(getClass().getResource("/immagini/iconaAnteprimaHover.png")).toExternalForm());

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
            labelElimina.setOnMouseClicked(event -> eliminaBrano(rowBrano, riga));
            labelElimina.setOnMouseEntered(event -> labelElimina.setGraphic(iconaCestinoHover));
            labelElimina.setOnMouseExited(event -> labelElimina.setGraphic(iconaCestino));


            // Creo l'icona normale
            ImageView iconaAnteprima = new ImageView(imageAnteprima);
            iconaAnteprima.setFitWidth(25);
            iconaAnteprima.setFitHeight(25);
            iconaAnteprima.setPreserveRatio(true);
            iconaAnteprima.setSmooth(true);

            // Creo l'icona hover
            ImageView iconaAnteprimaHover = new ImageView(imageAnteprimaHover);
            iconaAnteprimaHover.setFitWidth(25);
            iconaAnteprimaHover.setFitHeight(25);
            iconaAnteprimaHover.setPreserveRatio(true);
            iconaAnteprimaHover.setSmooth(true);

            Label labelAnteprima = new Label();
            labelAnteprima.setGraphic(iconaAnteprima);
            labelAnteprima.setStyle("-fx-cursor: hand;");
            labelAnteprima.setOnMouseClicked(event -> {
                try {
                    mostraAnteprimaBrano(Integer.parseInt(rowBrano.get("ID_CANZONE").toString()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            labelAnteprima.setOnMouseEntered(event -> labelAnteprima.setGraphic(iconaAnteprimaHover));
            labelAnteprima.setOnMouseExited(event -> labelAnteprima.setGraphic(iconaAnteprima));

            riga.getChildren().addAll(copertina, nomeBrano, spacer, labelAnteprima, labelElimina);
            PaginaPaneUtente_vboxCanzoni.getChildren().add(riga);
        }
    }

    private void mostraAnteprimaBrano(int padIdCanzone) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneCanzone.fxml"));
        Parent paneCanzone = loader.load();

        PaginaPaneCanzone controller = loader.getController();
        controller.mostraSchermataCanzone(padIdCanzone);

        AnchorPane.setTopAnchor(paneCanzone, 0.0);
        AnchorPane.setBottomAnchor(paneCanzone, 0.0);
        AnchorPane.setLeftAnchor(paneCanzone, 0.0);
        AnchorPane.setRightAnchor(paneCanzone, 0.0);

        PaginaPaneUtente_anteprimaCanzone.getChildren().add(paneCanzone);
    }

    private String getSafe(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    private void eliminaBrano(Map<String, Object> rowBrano, HBox parRigaCorrente) {
        boolean locRisposta = ObjGenerici.yesNoMessage("Attenzione!!", "Cancellare il brano "+'"'+rowBrano.get("TITOLO").toString()+'"'+" e tutti i suoi dati?");
        if(!locRisposta) {
            return;
        }

        PaginaPaneUtente_vboxCanzoni.getChildren().remove(parRigaCorrente);
        objSql.cancella("CANZONE", rowBrano.get("ID_CANZONE").toString());

        /*
        ##############################################################
                GESTIRE CANCELLAZIONE DATI AGGIUNTIVI
        ##############################################################
         */

    }
}
