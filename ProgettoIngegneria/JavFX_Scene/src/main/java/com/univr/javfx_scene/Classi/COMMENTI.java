package com.univr.javfx_scene.Classi;

import javafx.beans.property.SimpleStringProperty;

public class COMMENTI {

    private final int ID_COMMENTO;
    private final int ID_CANZONE;
    private final int ID_UTENTE;
    private final int ID_PADRE;
    private final String TESTO;
    private final int RANGE_INIZIO;
    private final int RANGE_FINE;

    public COMMENTI(int idCommento, int idCanzone, int idUtente, int idPadre, String testo, int rangeInizio, int rangeFine) {
        this.ID_COMMENTO = idCommento;
        this.ID_CANZONE = idCanzone;
        this.ID_UTENTE = idUtente;
        this.ID_PADRE = idPadre;
        this.TESTO = testo;
        this.RANGE_INIZIO = rangeInizio;
        this.RANGE_FINE = rangeFine;
    }

    public int getID_COMMENTO() {
        return ID_COMMENTO;
    }

    public int getID_CANZONE() {
        return ID_CANZONE;
    }

    public int getID_UTENTE() {
        return ID_UTENTE;
    }

    public int getID_PADRE() {
        return ID_PADRE;
    }

    public String getTESTO() {
        return TESTO;
    }

    public int getRANGE_INIZIO() {
        return RANGE_INIZIO;
    }

    public int getRANGE_FINE() {
        return RANGE_FINE;
    }
}
