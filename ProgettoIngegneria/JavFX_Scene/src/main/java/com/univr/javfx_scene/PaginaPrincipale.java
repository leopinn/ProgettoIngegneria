package com.univr.javfx_scene;

import com.univr.javfx_scene.classi.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PaginaPrincipale {

    @FXML
    Label nameLabel;

    @FXML
    private StackPane PaginaPrincipale_stackPane;


    private Stage stage;
    private Scene scene;
    private Parent root;

    public void impostazioni() {
        try {
            String fxml = "PaginaImpostazioni.fxml";
            Parent schermata = FXMLLoader.load(getClass().getResource(fxml));
            PaginaPrincipale_stackPane.getChildren().setAll(schermata);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("PaginaLogin.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Applicazione.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


}
