package com.univr.javfx_scene.Classi;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class UTENTI {
    private final SimpleStringProperty ID_UTENTE;
    private final SimpleStringProperty NOME;

    private final SimpleStringProperty COGNOME;
    private final SimpleStringProperty EMAIL;
    private final SimpleStringProperty DATA_NASCITA;
    private final SimpleStringProperty PASSWORD;
    private final SimpleStringProperty RUOLO;
    private final SimpleStringProperty STATO;

    public UTENTI(SimpleStringProperty idUtente, SimpleStringProperty nome, SimpleStringProperty cognome,
                  SimpleStringProperty email, SimpleStringProperty dataNascita, SimpleStringProperty password,
                  SimpleStringProperty ruolo, SimpleStringProperty stato) {
        ID_UTENTE = idUtente;
        NOME = nome;
        COGNOME = cognome;
        EMAIL = email;
        DATA_NASCITA = dataNascita;
        PASSWORD = password;
        RUOLO = ruolo;
        STATO = stato;
    }

    public String getID_UTENTE() {
        return ID_UTENTE.get();
    }

    public SimpleStringProperty ID_UTENTEProperty() {
        return ID_UTENTE;
    }

    public String getNOME() {
        return NOME.get();
    }

    public SimpleStringProperty NOMEProperty() {
        return NOME;
    }

    public String getCOGNOME() {
        return COGNOME.get();
    }

    public SimpleStringProperty COGNOMEProperty() {
        return COGNOME;
    }

    public String getEMAIL() {
        return EMAIL.get();
    }

    public SimpleStringProperty EMAILProperty() {
        return EMAIL;
    }

    public String getDATA_NASCITA() {
        return DATA_NASCITA.get();
    }

    public SimpleStringProperty DATA_NASCITAProperty() {
        return DATA_NASCITA;
    }

    public String getPASSWORD() {
        return PASSWORD.get();
    }

    public SimpleStringProperty PASSWORDProperty() {
        return PASSWORD;
    }

    public String getRUOLO() {
        return RUOLO.get();
    }

    public SimpleStringProperty RUOLOProperty() {
        return RUOLO;
    }

    public String getSTATO() {
        return STATO.get();
    }

    public SimpleStringProperty STATOProperty() {
        return STATO;
    }

}
