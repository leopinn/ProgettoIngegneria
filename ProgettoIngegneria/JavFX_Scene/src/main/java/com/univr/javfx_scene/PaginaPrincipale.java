package com.univr.javfx_scene;

import com.univr.javfx_scene.classi.*;
import javafx.animation.FadeTransition;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PaginaPrincipale implements Initializable {

    @FXML
    Label nameLabel;

    @FXML
    private BorderPane PaginaPrincipale_borderPane;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            paginaPrincipale();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void paginaPrincipale() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPanePrincipale.fxml"));
        Parent registerPane = loader.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPanePrincipale controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
    }

    public void impostazioni() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaPaneImpostazioni.fxml"));
        Parent registerPane = loader.load();

        // Ottieni il controller della registrazione e passa il riferimento a questo controller principale
        PaginaPaneImpostazioni controller = loader.getController();
        controller.setMainController(this);  // <<< passaggio chiave

        PaginaPrincipale_borderPane.setCenter(registerPane);
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
