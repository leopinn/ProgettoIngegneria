package com.univr.javfx_scene;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.*;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ObjGenerici {
    private static ObjGenerici instance;

    private int ID_UTENTE;
    private String UTENTE_NOME, UTENTE_EMAIL;

    int a;

    // Inizializzo il costruttore
    public ObjGenerici(int idUtente, String utenteNome, String utenteEmail) {
        ID_UTENTE = idUtente;
        UTENTE_NOME = utenteNome;
        UTENTE_EMAIL = utenteEmail;

        instance=this;
    }

    // Metodo statico per ottenere l'istanza
    public static ObjGenerici oggettoGenerico() {
        return instance;
    }

    /* ---------- Inizio - UTILIZZO VARIABILI GLOBALI ----------*/

    public int getID_UTENTE() {
        return ID_UTENTE;
    }

    public String getUTENTE_NOME() {
        return UTENTE_NOME;
    }

    public String getUTENTE_EMAIL() {
        return UTENTE_EMAIL;
    }

    public void putID_UTENTE(int parID_UTENTE) {
        ID_UTENTE=parID_UTENTE;
    }

    public void putUTENTE_NOME(String parUTENTE_NOME) {
        UTENTE_NOME=parUTENTE_NOME;
    }

    public void putUTENTE_EMAIL(String parUTENTE_EMAIL) {
        UTENTE_EMAIL=parUTENTE_EMAIL;
    }
    /* ---------- Fine - UTILIZZO VARIABILI GLOBALI ----------*/


    /* ---------- Inizio - GESTIONE POPUP ----------*/

    public static void mostraPopupSuccesso(Node parNodo, String parMsg) {
        Label contenuto = new Label(parMsg);
        contenuto.setStyle("""
        -fx-background-color: #28a745;
        -fx-text-fill: white;
        -fx-padding: 12px 24px;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);
    """);

        generaPopUp(contenuto, parNodo);
    }

    public static void mostraPopupErrore(Node parNodo, String parMsg) {
        Label contenuto = new Label(parMsg);
        contenuto.setStyle("""
        -fx-background-color: #dc3545;
        -fx-text-fill: white;
        -fx-padding: 12px 24px;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);
    """);

        generaPopUp(contenuto, parNodo);
    }


    private static void generaPopUp(Label contenuto, Node parNodo){
        Popup popup = new Popup();
        popup.getContent().add(contenuto);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Window finestra = parNodo.getScene().getWindow();
        popup.show(finestra);

        // Posiziona in basso al centro
        double popupWidth = popup.getWidth();

        popup.setX(finestra.getX() + (finestra.getWidth() - popupWidth) / 2);
        popup.setY(finestra.getY() + finestra.getHeight() - 160);       // Immediatamente sopra al player

        FadeTransition fade = new FadeTransition(Duration.seconds(3), contenuto);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2));

        fade.play();
    }

    public static boolean yesNoMessage(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);

        // Creo i pulsanti per scegliere si o no
        ButtonType buttonYes = new ButtonType("✔ Sì");
        ButtonType buttonNo = new ButtonType("✖ No");
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        // Applico lo stile
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(PaginaAvvio.class.getResource("Applicazione.css").toExternalForm());
        dialogPane.getStyleClass().add("yesNoMessage");

        // Prendo il risultato e lo restituisco
        Optional<ButtonType> risultato = alert.showAndWait();
        return risultato.isPresent() && risultato.get() == buttonYes;
    }


    /* ---------- Fine - GESTIONE POPUP ----------*/


    public static String ritornaCopertina(int parId){
        String locPath = "upload/copertine/" + parId + ".jpg";
        if(!new File(locPath).exists())
            locPath = "upload/copertine/" + parId + ".png";
        if(!new File(locPath).exists())
            locPath = "upload/copertine/" + parId + ".jpeg";
        if(!new File(locPath).exists())
            locPath="";
        return locPath;
    }

    public static String ritornaFotoProfilo(String parNome){
        String locPath = "impostazioni/fotoProfilo/" + parNome + ".jpg";
        if(!new File(locPath).exists())
            locPath = "impostazioni/fotoProfilo/" + parNome + ".png";
        if(!new File(locPath).exists())
            locPath = "impostazioni/fotoProfilo/" + parNome + ".jpeg";
        if(!new File(locPath).exists())
            locPath="";
        return locPath;
    }


    /* ---------- Inizio - DOWNLOAD MUSICA ----------*/



    public static void scaricaFileCanzone(Node parNodo, ObjSql parSql, int idCanzone) {
        String basePath = "upload/"; // Cartella locale dei file sorgenti

        // Percorsi dei file sorgente (basati sull'ID della canzone)
        File audioFile = new File(basePath + "musiche/" + idCanzone + ".mp3");
        File testoFile = new File(basePath + "pdf/" + idCanzone + ".txt");
        File videoFile = new File(basePath + "video/" + idCanzone + ".mp4");

        // Recupera il titolo del brano dal database
        String titoloBrano = "Brano";
        List<Map<String, Object>> risultato = parSql.leggiLista(
                "SELECT TITOLO FROM CANZONE WHERE ID_CANZONE = " + idCanzone
        );
        if (!risultato.isEmpty()) {
            titoloBrano = (String) risultato.get(0).get("TITOLO");
        }

        // Finestra di selezione cartella per far scegliere all'utente dove salvare
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Scegli cartella per salvare: " + titoloBrano);
        File targetFolder = chooser.showDialog(parNodo.getScene().getWindow());

        if (targetFolder == null) {
            return; // Utente ha annullato la selezione
        }

        // Crea una sottocartella con il nome del brano (sanificando caratteri illegali)
        String safeName = titoloBrano.replaceAll("[\\\\/:*?\"<>|]", "_");
        File destinazioneBrano = new File(targetFolder, safeName);
        destinazioneBrano.mkdirs(); // Crea la cartella se non esiste

        try {
            // Copia e rinomina i file solo se esistono
            if (audioFile.exists()) {
                copiaFile(audioFile, new File(destinazioneBrano, safeName + ".mp3"));
            }

            if (testoFile.exists()) {
                copiaFile(testoFile, new File(destinazioneBrano, safeName + ".txt"));
            }

            if (videoFile.exists()) {
                copiaFile(videoFile, new File(destinazioneBrano, safeName + ".mp4"));
            }

            // Mostra notifica popup di conferma
            mostraPopupSuccesso(parNodo, "Download completato: " + titoloBrano);

        } catch (IOException e) {
            // Errore durante la copia dei file
            e.printStackTrace();
            mostraPopupSuccesso(parNodo, "Errore durante il download di: " + titoloBrano);
        }
    }

    public static void copiaFile(File sorgente, File destinazione) throws IOException {
        if (sorgente.exists()) {
            java.nio.file.Files.copy(
                    sorgente.toPath(),
                    destinazione.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
        } else {
            // Se il file sorgente non esiste, stampa un messaggio in console
            System.out.println("File non trovato: " + sorgente.getAbsolutePath());
        }
    }


    /* ---------- Fine - DOWNLOAD MUSICA ----------*/

}
