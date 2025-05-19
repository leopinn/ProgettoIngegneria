package com.univr.javfx_scene.classi;

public class Brano {

    private int idBrano;
    private String titolo;
    private String annoComposizione;
    private String testo;
    private String spartito;
    private String audio;
    private String video;
    private int idAutore;
    private int idGenere;

    public Brano(int idBrano, String titolo, String annoComposizione, String testo, String spartito, String audio, String video, int idAutore, int idGenere) {
        this.idBrano = idBrano;
        this.titolo = titolo;
        this.annoComposizione = annoComposizione;
        this.testo = testo;
        this.spartito = spartito;
        this.audio = audio;
        this.video = video;
        this.idAutore = idAutore;
        this.idGenere = idGenere;
    }

    public int getIdBrano() {
        return idBrano;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getAnnoComposizione() {
        return annoComposizione;
    }

    public String getTesto() {
        return testo;
    }

    public String getSpartito() {
        return spartito;
    }

    public String getAudio() {
        return audio;
    }

    public String getVideo() {
        return video;
    }

    public int getIdAutore() {
        return idAutore;
    }

    public int getIdGenere() {
        return idGenere;
    }
}
