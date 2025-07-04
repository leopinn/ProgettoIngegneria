package com.univr.javfx_scene.Classi;

public class CANZONE {

    private final int ID_CANZONE;
    private final String TITOLO;
    private final String AUTORE;
    private final String GENERE;
    private final int ANNO_COMPOSIZIONE;
    private final String LINK_YOUTUBE;
    private final String PERCORSO_MUSICA;
    private final String PERCORSO_PDF;
    private final String PERCORSO_COPERTINA;
    private final String UTENTE_INS;
    private final int ID_UTENTE;

    public CANZONE(int idCanzone, String titolo, String autore, String genere, int annoComposizione, String linkYoutube, String percorsoMusica , String percorsoPdf, String percorsoCopertina,
                   String utenteIns, int idUtente) {
        this.ID_CANZONE = idCanzone;
        this.TITOLO = titolo;
        this.AUTORE = autore;
        this.GENERE = genere;
        this.ANNO_COMPOSIZIONE = annoComposizione;
        this.LINK_YOUTUBE = linkYoutube;
        this.PERCORSO_MUSICA = percorsoMusica;
        this.PERCORSO_PDF = percorsoPdf;
        this.PERCORSO_COPERTINA = percorsoCopertina;
        this.UTENTE_INS = utenteIns;
        this.ID_UTENTE = idUtente;
    }

    public String getUTENTE_INS() {
        return UTENTE_INS;
    }

    public int getID_UTENTE() {
        return ID_UTENTE;
    }

    public String getPERCORSO_COPERTINA() {
        return PERCORSO_COPERTINA;
    }

    public String getPERCORSO_PDF() {
        return PERCORSO_PDF;
    }

    public String getPERCORSO_MUSICA() {
        return PERCORSO_MUSICA;
    }

    public String getLINK_YOUTUBE() {
        return LINK_YOUTUBE;
    }

    public int getANNO_COMPOSIZIONE() {
        return ANNO_COMPOSIZIONE;
    }

    public String getGENERE() {
        return GENERE;
    }

    public String getAUTORE() {
        return AUTORE;
    }

    public String getTITOLO() {
        return TITOLO;
    }

    public int getID_CANZONE() {
        return ID_CANZONE;
    }
}
