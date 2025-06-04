package com.univr.javfx_scene.Classi;

public class CANZONE {

    private final int ID_CANZONE;
    private final String AUTORE;
    private final String GENERE;
    private final int ANNO_COMPOSIZIONE;
    private final String LINK_YOUTUBE;
    private final String PERCORSO_MUSICA;
    private final String PERCORSO_PDF;
    private final String PERCORSO_COPERTINA;

    public CANZONE(int idCanzone, String titolo, String autore, String genere, int annoComposizione, String linkYoutube, String percorsoMusica , String percorsoPdf, String percorsoCopertina) {
        this.ID_CANZONE = idCanzone;
        this.AUTORE = autore;
        this.GENERE = genere;
        this.ANNO_COMPOSIZIONE = annoComposizione;
        this.LINK_YOUTUBE = linkYoutube;
        this.PERCORSO_MUSICA = percorsoMusica;
        this.PERCORSO_PDF = percorsoPdf;
        this.PERCORSO_COPERTINA = percorsoCopertina;
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

    public int getID_CANZONE() {
        return ID_CANZONE;
    }
}
