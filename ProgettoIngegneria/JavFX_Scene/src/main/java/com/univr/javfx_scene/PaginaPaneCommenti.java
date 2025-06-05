package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaginaPaneCommenti {
    private static ObjSql objSql = ObjSql.oggettoSql();
    private int ID_CANZONE;

    @FXML
    private VBox commentiContainer;
    @FXML
    private TextArea commentoTextArea;

    @FXML
    public void inviaCommento(ActionEvent event) {
        // codice da eseguire quando si clicca "Invia"
        String testo = commentoTextArea.getText();
        int utente = PaginaPaneLogin.UTENTE_ID;

        Map<String, Object> rowCommento = new LinkedHashMap<>();
        rowCommento.put("ID_CANZONE", ID_CANZONE);
        rowCommento.put("ID_UTENTE", utente);
        rowCommento.put("TESTO", testo);

        objSql.inserisci("COMMENTI", rowCommento);
        caricaCommenti(ID_CANZONE);
    }

    // Carica i commenti presenti nel db per quella canzone
    public void caricaCommenti(int id_canzone) {
        ID_CANZONE = id_canzone;

        commentiContainer.getChildren().clear();

        // Select commenti
        List<Map<String, Object>> commenti=  objSql.leggiLista("SELECT NOME, COGNOME, TESTO FROM COMMENTI INNER JOIN UTENTI ON COMMENTI.ID_UTENTE = UTENTI.ID_UTENTE WHERE ID_CANZONE = " + id_canzone);

        if(commenti == null) {
            return;
        }

        for (Map<String, Object> commento : commenti) {
            VBox commentoBox = new VBox();
            commentoBox.setSpacing(2);
            commentoBox.getStyleClass().add("commento");

            String autore = commento.get("NOME").toString() + " " + commento.get("COGNOME").toString();
            Label autoreLabel = new Label(autore);
            autoreLabel.setStyle("-fx-text-fill: #6d24e1; -fx-font-weight: bold; -fx-padding: 0 0 4 0;");

            Label testoLabel = new Label(commento.get("TESTO").toString());
            testoLabel.setWrapText(true);

            commentoBox.getChildren().addAll(autoreLabel, testoLabel);

            commentiContainer.getChildren().add(commentoBox);
        }
    }
}