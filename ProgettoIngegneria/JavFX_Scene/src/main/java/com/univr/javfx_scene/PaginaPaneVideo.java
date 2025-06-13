package com.univr.javfx_scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PaginaPaneVideo {
    private static ObjSql objSql = ObjSql.oggettoSql();
    private int ID_CANZONE;

    @FXML
    private VBox commentiContainer;
    @FXML
    private TextArea commentoTextArea;
    @FXML
    private VBox PaginaPaneVideo_vBox;

    // Carica i commenti presenti nel db per quella canzone
    public void caricaVideo(int id_canzone) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        Map<String, Object> url = objSql.leggi("SELECT LINK_YOUTUBE FROM CANZONE WHERE ID_CANZONE = " + id_canzone);

        String youtubeURL = "https://www.youtube.com/embed/" + estraiIdVideoYouTube(url.get("LINK_YOUTUBE").toString()) + "?autoplay=1&mute=1";
        System.out.println(youtubeURL);

        String html = "<iframe width=\"800\" height=\"450\" src=\"" + youtubeURL + "\" " +
                "frameborder=\"0\" allowfullscreen></iframe>";

        webEngine.loadContent(html, "text/html");

        webView.setPrefWidth(800);
        webView.setPrefHeight(450);
        PaginaPaneVideo_vBox.getChildren().add(webView);
    }

    public static String estraiIdVideoYouTube(String url) {
        String id = null;
        if (url == null) return null;

        int index = url.indexOf("v=");
        if (index != -1) {
            id = url.substring(index + 2);
            int ampIndex = id.indexOf('&');
            if (ampIndex != -1) {
                id = id.substring(0, ampIndex);
            }
        }

        return id;
    }

}