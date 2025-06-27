package com.univr.javfx_scene.Classi;
//package com.univr.javfx_scene;

public class ModificaCanzone {
    private final String titolo;
    private final String autore;
    private final String genere;
    private final Integer anno;
    private final String youtube;
    private final String utente;
    private final String data;

    public ModificaCanzone(String titolo, String autore, String genere, Integer anno, String youtube, String utente, String data) {
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
        this.anno = anno;
        this.youtube = youtube;
        this.utente = utente;
        this.data = data;
    }

    public String getTitolo() { return titolo; }
    public String getAutore() { return autore; }
    public String getGenere() { return genere; }
    public Integer getAnno() { return anno; }
    public String getYoutube() { return youtube; }
    public String getUtente() { return utente; }
    public String getData() { return data; }
}

