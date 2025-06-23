package com.univr.javfx_scene;

import com.univr.javfx_scene.Classi.CANZONE;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PaginaPanePrincipale implements Initializable {

    private  ObjSql objSql = ObjSql.oggettoSql();
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    @FXML
    private TilePane PaginaPanePrincipale_grigliaMusiche;
    @FXML private ScrollPane PaginaPrincipale_scrollPane;

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PaginaPanePrincipale_grigliaMusiche.prefWidthProperty().bind(PaginaPrincipale_scrollPane.widthProperty());

        List<Map<String, Object>> listaBrani = objSql.leggiLista("SELECT * FROM CANZONE");
        for (Map<String, Object> brano : listaBrani) {
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER);
            card.setStyle("""
    -fx-background-color: #1e1e1e;
    -fx-background-radius: 15;
    -fx-padding: 10;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);
    """);
            card.setPrefWidth(150);
            card.setPrefHeight(150);
            card.setId(Integer.toString((Integer) brano.get("ID_CANZONE")));   // In modo che ogni VBOX sia direttamente associata alla Canzone, mi servirà dopo per scegliere la musica e riprodurla
            card.setOnMouseClicked(event -> {
                try {
                    selezionaMusica(card.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            String locPath="upload/copertine/"+brano.get("ID_CANZONE")+".jpg";
            Image immagine = new Image(new File(locPath).toURI().toString());
            ImageView copertina = new ImageView(immagine);
            copertina.setFitWidth(120);
            copertina.setFitHeight(120);
            copertina.setPreserveRatio(true); // se vuoi una forma esatta 120x120

            Label titolo = new Label((String) brano.get("TITOLO"));
            titolo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            titolo.setWrapText(true);
            titolo.setAlignment(Pos.CENTER);

            Label autore = new Label((String) brano.get("AUTORE"));
            autore.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
            autore.setWrapText(true);
            autore.setAlignment(Pos.CENTER);

            card.getChildren().addAll(copertina, titolo, autore);
            PaginaPanePrincipale_grigliaMusiche.getChildren().add(card);
        }
    }

    private void selezionaMusica(String parId) throws IOException {
        mainController.selezionaMusica(Integer.parseInt(parId));
    }

}
