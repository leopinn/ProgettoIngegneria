package com.univr.javfx_scene;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class ObjSql {
    private static ObjSql instance;
    private static final String DB_URL = "jdbc:sqlite:database.db";

    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    // Costruttore privato
    private ObjSql() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            logger.info("Connessione al database stabilita.");
        } catch (SQLException e) {
            logger.warning("Errore connessione: " + e.getMessage());
        }
    }

    // Metodo statico per ottenere l'istanza
    public static ObjSql oggettoSql() {
        if (instance == null) {
            instance = new ObjSql();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public int aggiorna(String nomeTabella, String where, Map<String, Object> row) {
        if (row == null || row.isEmpty()) return 0;

        StringBuilder colonne = new StringBuilder();
        List<Object> listaValori = new ArrayList<>();

        for (String column : row.keySet()) {
            if (colonne.length() > 0) {
                colonne.append(", ");
            }
            colonne.append(column).append(" = ?");
            listaValori.add(row.get(column));
        }

        String sql = "UPDATE " + nomeTabella + " SET " + colonne + " " + where;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < listaValori.size(); i++) {
                pstmt.setObject(i + 1, listaValori.get(i));
            }

            return pstmt.executeUpdate(); // restituisce il numero di righe aggiornate

        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
            return -1;
        }
    }

    public int inserisci(String nomeTabella, Map<String, Object> row) {
        if (row == null || row.isEmpty()) return 0;

        StringBuilder colonne = new StringBuilder();
        StringBuilder spazio = new StringBuilder();
        List<Object> listaValori = new ArrayList<>();

        for (String column : row.keySet()) {
            if (colonne.length() > 0) {
                colonne.append(", ");
                spazio.append(", ");
            }
            colonne.append(column);
            spazio.append("?");
            listaValori.add(row.get(column));
        }

        String sql = "INSERT INTO " + nomeTabella + " (" + colonne + ") VALUES (" + spazio + ")";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < listaValori.size(); i++) {
                pstmt.setObject(i + 1, listaValori.get(i));
            }

            pstmt.executeUpdate();
            return 1;   // Inserimento andato a buon fine

        } catch (SQLException e) {
            System.err.println("Errore: " + e.getMessage());
            return e.getErrorCode();   // Inserimento fallito
        }
    }

    public Map<String, Object> leggi(String par_query){
        Map<String, Object> row=null;

        try (PreparedStatement statement = connection.prepareStatement(par_query)) {

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                row=passaRiga(rs);
            }
            logger.info(par_query+"\n->eseguita con successo!");

        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return row;
    }

    private static Map<String, Object> passaRiga(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numeroColonne = metaData.getColumnCount();

        // LinkedHashMap mantiene l'ordine delle colonne come nel ResultSet
        Map<String, Object> riga = new LinkedHashMap<>();

        for (int i = 1; i <= numeroColonne; i++) {
            String nomeColonna = metaData.getColumnName(i); // Recupera il nome della colonna
            Object valoreColonna = resultSet.getObject(i);  // Recupera il valore della colonna
            riga.put(nomeColonna, valoreColonna);           // Aggiungi la coppia (colonna, valore) alla mappa
        }

        return riga;
    }
}
