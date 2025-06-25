package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class ObjGenerici {

    public static void mostraPopupSuccesso(Node parNodo, String parMsg) {
        Label contenuto = new Label(parMsg);
        contenuto.setStyle("""
        -fx-background-color: #28a745;
        -fx-text-fill: white;
        -fx-padding: 12px 24px;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);
    """);

        generaPopUp(contenuto, parNodo);
    }

    public static void mostraPopupErrore(Node parNodo, String parMsg) {
        Label contenuto = new Label(parMsg);
        contenuto.setStyle("""
        -fx-background-color: #dc3545;
        -fx-text-fill: white;
        -fx-padding: 12px 24px;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);
    """);

        generaPopUp(contenuto, parNodo);
    }


    private static void generaPopUp(Label contenuto, Node parNodo){
        Popup popup = new Popup();
        popup.getContent().add(contenuto);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Window finestra = parNodo.getScene().getWindow();
        popup.show(finestra);

        // Posiziona in basso al centro
        double popupWidth = popup.getWidth();

        popup.setX(finestra.getX() + (finestra.getWidth() - popupWidth) / 2);
        popup.setY(finestra.getY() + finestra.getHeight() - 160);       // Immediatamente sopra al player

        FadeTransition fade = new FadeTransition(Duration.seconds(3), contenuto);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2));

        fade.play();
    }
}
