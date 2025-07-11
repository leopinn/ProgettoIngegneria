package com.univr.javfx_scene.Classi;

public class DATI_AGGIUNTIVI {

    private final int ID_DATI;
    private final int ID_CANZONE;
    private final String NOME_FILE;
    private final String LINK_YOUTUBE;
    private final int ID_UTENTE;
    private final String UTENTE_INS;
    private final String DATA_INSERIMENTO;

     //public ModificaCanzone(int idDati, int idCanzone, String titolo, String autore, String genere, Integer anno, String youtube, int idUtente, String insUtente, String data) {
     public DATI_AGGIUNTIVI(int idDati, int idCanzone, String nomeFile, String youtube, String insUtente, int idUtente, String data) {
        this.ID_DATI = idDati;
        this.ID_CANZONE = idCanzone;
        this.NOME_FILE = nomeFile;
        this.LINK_YOUTUBE = youtube;
        this.ID_UTENTE = idUtente;
        this.UTENTE_INS = insUtente;
        this.DATA_INSERIMENTO = data;
    }

    public int getID_DATI() {
        return ID_DATI;
    }

    public int getID_CANZONE() {
        return ID_CANZONE;
    }

    public String getNOME_FILE() {
        return NOME_FILE;
    }

    public String getYOUTUBE() {
        return LINK_YOUTUBE;
    }

    public int getID_UTENTE() {
        return ID_UTENTE;
    }

    public String getUTENTE_INS() {
        return UTENTE_INS;
    }

    public String getDATA() {
        return DATA_INSERIMENTO;
    }
}