package com.univr.javfx_scene.Classi;

import javafx.beans.property.SimpleStringProperty;

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
        RUOLO = ruolo;  // 0=utente, 1=amministratore
        STATO = stato;  // 0=da autorizzare, 1=autorizzato, 2=sospeso
    }

    public String getID_UTENTE() {
        return ID_UTENTE.get();
    }
    public void setID_UTENTE(String idUtente) {
        this.ID_UTENTE.set(idUtente);
    }
    public SimpleStringProperty ID_UTENTEProperty() {
        return ID_UTENTE;
    }

    public String getNOME() {
        return NOME.get();
    }
    public void setNOME(String nome) {
        this.NOME.set(nome);
    }
    public SimpleStringProperty NOMEProperty() {
        return NOME;
    }

    public String getCOGNOME() {
        return COGNOME.get();
    }
    public void setCOGNOME(String cognome) {
        this.COGNOME.set(cognome);
    }
    public SimpleStringProperty COGNOMEProperty() {
        return COGNOME;
    }

    public String getEMAIL() {
        return EMAIL.get();
    }
    public void setEMAIL(String email) {
        this.EMAIL.set(email);
    }
    public SimpleStringProperty EMAILProperty() {
        return EMAIL;
    }

    public String getDATA_NASCITA() {
        return DATA_NASCITA.get();
    }
    public void setDATA_NASCITA(String dataNascita) {
        this.DATA_NASCITA.set(dataNascita);
    }
    public SimpleStringProperty DATA_NASCITAProperty() {
        return DATA_NASCITA;
    }

    public String getPASSWORD() {
        return PASSWORD.get();
    }
    public void setPASSWORD(String password) {
        this.PASSWORD.set(password);
    }
    public SimpleStringProperty PASSWORDProperty() {
        return PASSWORD;
    }

    public String getRUOLO() {
        return RUOLO.get();
    }
    public void setRUOLO(String ruolo) {
        this.RUOLO.set(ruolo);
    }
    public SimpleStringProperty RUOLOProperty() {
        return RUOLO;
    }

    public String getSTATO() {
        return STATO.get();
    }
    public void setSTATO(String stato) {
        this.STATO.set(stato);
    }
    public SimpleStringProperty STATOProperty() {
        return STATO;
    }
}
