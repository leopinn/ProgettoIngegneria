package com.univr.javfx_scene.Classi;

public class ModificaCanzone {

    private final int ID_DATI;
    private final int ID_CANZONE;
    private final String TITOLO;
    private final String AUTORE;
    private final String GENERE;
    private final int ANNO_COMPOSIZIONE;
    private final String LINK_YOUTUBE;
    //private final int ID_UTENTE;
    private final String UTENTE_INS;
    private final String DATA_INSERIMENTO;

     //public ModificaCanzone(int idDati, int idCanzone, String titolo, String autore, String genere, Integer anno, String youtube, int idUtente, String insUtente, String data) {
     public ModificaCanzone(int idDati, int idCanzone, String titolo, String autore, String genere, Integer anno, String youtube, String insUtente, String data) {
        this.ID_DATI = idDati;
        this.ID_CANZONE = idCanzone;
        this.TITOLO = titolo;
        this.AUTORE = autore;
        this.GENERE = genere;
        this.ANNO_COMPOSIZIONE = (anno != null ? anno : 0);  // fallback su 0 se null
        this.LINK_YOUTUBE = youtube;
        //this.ID_UTENTE = idUtente;
        this.UTENTE_INS = insUtente;
        this.DATA_INSERIMENTO = data;
    }

    public int getID_DATI() {
        return ID_DATI;
    }

    public int getID_CANZONE() {
        return ID_CANZONE;
    }

    public String getTITOLO() {
        return TITOLO;
    }

    public String getAUTORE() {
        return AUTORE;
    }

    public String getGENERE() {
        return GENERE;
    }

    public int getANNO() {
        return ANNO_COMPOSIZIONE;
    }

    public String getYOUTUBE() {
        return LINK_YOUTUBE;
    }
/*
    public int getID_UTENTE() {
        return ID_UTENTE;
    }
*/
    public String getUTENTE_INS() {
        return UTENTE_INS;
    }

    public String getDATA() {
        return DATA_INSERIMENTO;
    }
}