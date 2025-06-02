package com.univr.javfx_scene;

import java.io.IOException;

public class PaginaPaneUpload {
    private PaginaPrincipale mainController; // Importante per permettere il cambio dei vari pane nella pagina principale

    public void setMainController(PaginaPrincipale controller) {
        this.mainController = controller;
    }

    // Metodo per tornare alla pagina principale
    public void annulla() throws IOException, CloneNotSupportedException {
        mainController.paginaPrincipale();
    }

}
