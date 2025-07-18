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
        } catch (SQLException e) {
            e.printStackTrace();
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

    // Per cancellare una row di dati
    public void cancella(String parTabella, String parChiave){
        String locQuery="DELETE FROM " + parTabella + " WHERE " + parChiave;
        try (PreparedStatement statement = connection.prepareStatement(locQuery)) {
            statement.executeUpdate();
            //logger.info(locQuery+"\n->eseguita con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Per ritornare una row di dati
    public Map<String, Object> leggi(String par_query){
        Map<String, Object> row=null;

        try (PreparedStatement statement = connection.prepareStatement(par_query)) {

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                row=passaRiga(rs);
            }
        } catch (SQLException e) {
          e.printStackTrace();
        }

        return row;
    }

    // Per ritornare una lista di dati
    public List<Map<String, Object>> leggiLista(String par_query){
        List<Map<String, Object>> lista=new ArrayList<>();
        Map<String, Object> row;

        try (PreparedStatement statement = connection.prepareStatement(par_query)) {

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                row=passaRiga(rs);
                lista.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
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

    public boolean eseguiQuery(String sql) {
        try (Connection conn = this.getConnection();  // Metodo interno per ottenere connessione
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean aggiorna(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> leggiTutti(String query) {
        List<Map<String, Object>> risultati = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> riga = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    riga.put(meta.getColumnName(i), rs.getObject(i));
                }
                risultati.add(riga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultati;
    }

    public int cancellaCondizione(String tabella, String condizione) {
        String query = "DELETE FROM " + tabella + " WHERE " + condizione;
        try {
            java.sql.Statement stmt = connection.createStatement();
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
