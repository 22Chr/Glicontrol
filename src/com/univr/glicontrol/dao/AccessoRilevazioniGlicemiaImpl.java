package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.RilevazioneGlicemica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoRilevazioniGlicemiaImpl implements AccessoRilevazioniGlicemia {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<RilevazioneGlicemica> recuperaRilevazioniPaziente(int idPaziente) {
        List<RilevazioneGlicemica> rilevazioni = new ArrayList<>();

        String recuperaRilevazioniSql = "select * from LivelloGlicemia where id_paziente_glicemico = ?";
        try {
            java.sql.Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaRilevazioniStmt = conn.prepareStatement(recuperaRilevazioniSql);
            recuperaRilevazioniStmt.setInt(1, idPaziente);
            try (ResultSet rs = recuperaRilevazioniStmt.executeQuery()) {
                while (rs.next()) {
                    rilevazioni.add(new RilevazioneGlicemica(
                            rs.getInt("id_glicemia"),
                            rs.getInt("id_paziente_glicemico"),
                            rs.getDate("data"),
                            rs.getTime("ora"),
                            rs.getFloat("valore"),
                            rs.getString("pasto"),
                            rs.getString("indicazioni_temporali")
                    ));
                }
            }

            recuperaRilevazioniStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO RILEVAZIONI]: " + e.getMessage());
        }

        return rilevazioni;
    }

    @Override
    public boolean deleteRilevazioneGlicemica(int idRilevazione) {
        boolean success = false;
        String deleteRilevazioneSql = "delete from LivelloGlicemia where id_glicemia = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement deleteRilevazioneStmt = conn.prepareStatement(deleteRilevazioneSql);
            deleteRilevazioneStmt.setInt(1, idRilevazione);

            if (deleteRilevazioneStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE DELETE RILEVAZIONE]: Impossibile eliminare la rilevazione selezionata dal database");
            }

            deleteRilevazioneStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE RILEVAZIONE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean insertRilevazioneGlicemica(int idPaziente, Date data, Time ora, float valore, String pasto, String indicazioniTemporali) {
        boolean success = false;
        String insertRilevazioneSql = "insert into LivelloGlicemia (id_paziente_glicemico, data, ora, valore, pasto, indicazioni_temporali) value (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertRilevazioneStmt = conn.prepareStatement(insertRilevazioneSql);
            insertRilevazioneStmt.setInt(1, idPaziente);
            insertRilevazioneStmt.setDate(2, data);
            insertRilevazioneStmt.setTime(3, ora);
            insertRilevazioneStmt.setFloat(4, valore);
            insertRilevazioneStmt.setString(5, pasto);
            insertRilevazioneStmt.setString(6, indicazioniTemporali);

            if (insertRilevazioneStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT RILEVAZIONE]: Impossibile inserire la nuova rilevazione nel database");
            }

            insertRilevazioneStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT RILEVAZIONE]: " + e.getMessage());
        }

        return success;
    }
}
