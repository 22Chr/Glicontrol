package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.FattoriRischio;
import com.univr.glicontrol.bll.Paziente;

import java.sql.*;

public class AccessoFattoriRischioImpl implements AccessoFattoriRischio {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    private final Paziente paziente;

    public AccessoFattoriRischioImpl(Paziente paziente) {
        this.paziente = paziente;
    }


    @Override
    public FattoriRischio recuperaFattoreRischio(int idPaziente) {
        String recuperaFattoreRischioSql = "select * from FattoriRischio where id_paziente_fattore_rischio = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement stmt = conn.prepareStatement(recuperaFattoreRischioSql);
            stmt.setInt(1, idPaziente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new FattoriRischio(
                            paziente,
                            rs.getInt("familiarita"),
                            rs.getBoolean("obesita"),
                            rs.getInt("sedentarieta"),
                            rs.getInt("alimentazione_scorretta"),
                            rs.getInt("fumatore"),
                            rs.getInt("problemi_alcol")
                    );
                }
            }

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO FATTORI DI RISCHIO]: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean updateFattoreRischio(FattoriRischio fattore) {
        boolean status = false;
        String updateFattoriRischioSql = "update FattoriRischio set eta = ?, familiarita = ?, obesita = ?, sedentarieta = ?, alimentazione_scorretta = ?, fumatore = ?, problemi_alcol = ? where id_paziente_fattore_rischio = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updateStmt = conn.prepareStatement(updateFattoriRischioSql);
            updateStmt.setInt(1, fattore.getEta());
            updateStmt.setInt(2, fattore.getFamiliarita());
            updateStmt.setBoolean(3, fattore.getObesita());
            updateStmt.setInt(4, fattore.getSedentarieta());
            updateStmt.setInt(5, fattore.getAlimentazioneScorretta());
            updateStmt.setInt(6, fattore.getFumatore());
            updateStmt.setInt(7, fattore.getProblemiAlcol());
            updateStmt.setInt(8, fattore.getIdPaziente());

            if (updateStmt.executeUpdate() != 0) {
                conn.commit();
                status = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE FATTORI DI RISCHIO]: Impossibile aggiornare i fattori di rischio del paziente nel database");
            }

            updateStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE FATTORI DI RISCHIO]: " + e.getMessage());
        }

        return status;
    }

    @Override
    public boolean insertFattoreRischio(FattoriRischio fattore) {
        boolean status = false;
        String insertFattoreRischioSql = "insert into FattoriRischio (id_paziente_fattore_rischio, eta, familiarita, obesita, sedentarieta, alimentazione_scorretta, fumatore, problemi_alcol) values (?, ?, ?, ?, ?, ?, ?, ?) ";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertStmt = conn.prepareStatement(insertFattoreRischioSql);
            insertStmt.setInt(1, fattore.getIdPaziente());
            insertStmt.setInt(2, fattore.getEta());
            insertStmt.setInt(3, fattore.getFamiliarita());
            insertStmt.setBoolean(4, fattore.getObesita());
            insertStmt.setInt(5, fattore.getSedentarieta());
            insertStmt.setInt(6, fattore.getAlimentazioneScorretta());
            insertStmt.setInt(7, fattore.getFumatore());
            insertStmt.setInt(8, fattore.getProblemiAlcol());

            if (insertStmt.executeUpdate() != 0) {
                conn.commit();
                status = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT FATTORI DI RISCHIO]: Impossibile inserire i fattori di rischio del paziente nel database");
            }

            insertStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT FATTORI DI RISCHIO]: " + e.getMessage());
        }

        return status;
    }

    @Override
    public boolean deleteFattoreRischio(int idPaziente) {
        boolean status = false;
        String deleteFattoreRischioSql = "delete from FattoriRischio where id_paziente_fattore_rischio = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement deleteStmt = conn.prepareStatement(deleteFattoreRischioSql);
            deleteStmt.setInt(1, idPaziente);

            if (deleteStmt.executeUpdate() != 0) {
                conn.commit();
                status = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE DELETE FATTORI DI RISCHIO]: Impossibile eliminare i fattori di rischio del paziente nel database");
            }

            deleteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE FATTORI DI RISCHIO]: " + e.getMessage());
        }

        return status;
    }

}
