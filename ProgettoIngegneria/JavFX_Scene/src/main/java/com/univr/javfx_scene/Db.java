package com.univr.javfx_scene;

import com.univr.javfx_scene.classi.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Db {

    public Connection connection;
    public Logger logger = Logger.getLogger(this.getClass().getName());

    public void getConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");
                logger.info("Opened database successfully");
            }

        }catch (SQLException e){
            logger.info(e.toString());
        }
    }

    // SELECT da Brano
    public List<Brano> selectBrani() {
        getConnection();

        String query = "SELECT * FROM BRANO";
        List<Brano> brani = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int idBrano = rs.getInt("ID_BRANO");
                String titolo = rs.getString("TITOLO");
                String annoComposizione = rs.getString("ANNO_COMPOSIZIONE");
                String testo = rs.getString("TESTO");
                String spartito  = rs.getString("SPARTITO");
                String audio  = rs.getString("AUDIO");
                String video  = rs.getString("VIDEO");
                int idAutore = rs.getInt("ID_AUTORE");
                int idGenere = rs.getInt("ID_GENERE");

                brani.add(new Brano(idBrano, titolo, annoComposizione, testo, spartito, audio, video, idAutore, idGenere));
            }

            logger.info("Select di Brano avvenuta con successo");

        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return brani;
    }

}
