package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.TerapiaConcomitante;
import com.univr.glicontrol.bll.TerapiaDiabete;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoTerapieImpl implements AccessoTerapie {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<TerapiaDiabete> getTerapieDiabetePaziente(int idPaziente) {
        List<TerapiaDiabete> terapiaDiabete = new ArrayList<>();
        String recuperaTerapiadiabeteSql = "select * from TerapiaDiabete where id_paziente_connesso = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaTerapiaDiabeteStmt = conn.prepareStatement(recuperaTerapiadiabeteSql);
            recuperaTerapiaDiabeteStmt.setInt(1, idPaziente);
            try (ResultSet rs = recuperaTerapiaDiabeteStmt.executeQuery()) {
                while (rs.next()) {
                    terapiaDiabete.add(new TerapiaDiabete(
                            rs.getInt("id_terapia_diabete"),
                            rs.getInt("id_paziente_connesso"),
                            rs.getInt("id_medico_ultima_modifica"),
                            rs.getInt("id_farmaco_terapia"),
                            rs.getDate("data_inizio"),
                            rs.getDate("data_fine"),
                            rs.getFloat("dosaggio"),
                            rs.getString("frequenza"),
                            rs.getString("orari")
                    ));
                }
            }

            recuperaTerapiaDiabeteStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TERAPIA DIABETE]: " + e.getMessage());
        }

        return terapiaDiabete;
    }

    @Override
    public List<TerapiaConcomitante> getTerapieConcomitantiPaziente(int idPaziente) {
        List<TerapiaConcomitante> terapieConcomitanti = new ArrayList<>();
        String recuperaTerapieConcomitantiSql = "select * from TerapiaConcomitante where id_paziente_terapia_concomitante = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaTerapieConcomitantiStmt = conn.prepareStatement(recuperaTerapieConcomitantiSql);
            recuperaTerapieConcomitantiStmt.setInt(1, idPaziente);
            try (ResultSet rs = recuperaTerapieConcomitantiStmt.executeQuery()) {
                while (rs.next()) {
                    terapieConcomitanti.add(new TerapiaConcomitante(
                            rs.getInt("id_terapia_concomitante"),
                            rs.getInt("id_paziente_terapia_concomitante"),
                            rs.getInt("id_patologia_comorbidita"),
                            rs.getInt("id_medico_ultima_modifica"),
                            rs.getInt("id_farmaco_terapia"),
                            rs.getDate("data_inizio"),
                            rs.getDate("data_fine"),
                            rs.getFloat("dosaggio"),
                            rs.getString("frequenza"),
                            rs.getString("orari")
                    ));
                }
            }

            recuperaTerapieConcomitantiStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TERAPIE CONCOMITANTI]: " + e.getMessage());
        }

        return terapieConcomitanti;
    }

    @Override
    public boolean insertTerapiaDiabete(int idPaziente, int idMedicoUltimaModifica,  int idFarmacoTerapia, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari) {
        boolean success = false;
        String insertTerapiaDiabeteSql = "insert into TerapiaDiabete (id_paziente_connesso, id_medico_ultima_modifica, id_farmaco_terapia_connesso, data_inizio, data_fine, dosaggio, frequenza, orari) value (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertTerapiaDiabeteStmt = conn.prepareStatement(insertTerapiaDiabeteSql);
            insertTerapiaDiabeteStmt.setInt(1, idPaziente);
            insertTerapiaDiabeteStmt.setInt(2, idMedicoUltimaModifica);
            insertTerapiaDiabeteStmt.setInt(3, idFarmacoTerapia);
            insertTerapiaDiabeteStmt.setDate(4, dataInizio);
            insertTerapiaDiabeteStmt.setDate(5, dataFine);
            insertTerapiaDiabeteStmt.setFloat(6, dosaggio);
            insertTerapiaDiabeteStmt.setString(7, frequenza);
            insertTerapiaDiabeteStmt.setString(8, orari);

            if (insertTerapiaDiabeteStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile inserire la terapia diabete nel database");
            }

            insertTerapiaDiabeteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean insertTerapiaConcomitante(int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, int idFarmacoTerapia, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari) {
        boolean success = false;
        String insertTerapiaConcomitanteSql = "insert into TerapiaConcomitante (id_paziente_terapia_concomitante, id_patologia_comorbidita, id_medico_ultima_modifica, id_farmaco_terapia, data_inizio, data_fine, dosaggio, frequenza, orari) value (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertTerapiaConcomitanteStmt = conn.prepareStatement(insertTerapiaConcomitanteSql);
            insertTerapiaConcomitanteStmt.setInt(1, idPaziente);
            insertTerapiaConcomitanteStmt.setInt(2, idPatologiaConcomitante);
            insertTerapiaConcomitanteStmt.setInt(3, idMedicoUltimaModifica);
            insertTerapiaConcomitanteStmt.setInt(4, idFarmacoTerapia);
            insertTerapiaConcomitanteStmt.setDate(5, dataInizio);
            insertTerapiaConcomitanteStmt.setDate(6, dataFine);
            insertTerapiaConcomitanteStmt.setFloat(7, dosaggio);
            insertTerapiaConcomitanteStmt.setString(8, frequenza);
            insertTerapiaConcomitanteStmt.setString(9, orari);

            if (insertTerapiaConcomitanteStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTI]: Impossibile inserire la terapia concomitante nel database");
            }

            insertTerapiaConcomitanteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTI]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updateTerapiaDiabete(TerapiaDiabete terapia) {
        boolean success = false;
        String updateTerapiaDiabeteSql = "update TerapiaDiabete set id_medico_ultima_modifica = ?, data_fine = ?, dosaggio = ?, frequenza = ?, orari = ? where id_terapia_diabete = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updateTerapiaDiabeteStmt = conn.prepareStatement(updateTerapiaDiabeteSql);
            updateTerapiaDiabeteStmt.setInt(1, terapia.getIdMedicoUltimaModifica());
            updateTerapiaDiabeteStmt.setDate(2, terapia.getDataFine());
            updateTerapiaDiabeteStmt.setFloat(3, terapia.getDosaggio());
            updateTerapiaDiabeteStmt.setString(4, terapia.getFrequenza());
            updateTerapiaDiabeteStmt.setString(5, terapia.getOrari());
            updateTerapiaDiabeteStmt.setInt(6, terapia.getIdTerapiaDiabete());

            if (updateTerapiaDiabeteStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE TERAPIA DIABETE]: Impossibile aggiornare la terapia diabete nel database");
            }

            updateTerapiaDiabeteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updateTerapiaConcomitante(TerapiaConcomitante terapia) {
        boolean success = false;
        String updateTerapiaConcomitanteSql = "update TerapiaConcomitante set id_patologia_comorbidita = ?,  id_medico_ultima_modifica = ?, data_fine = ?, dosaggio = ?, frequenza = ?, orari = ? where id_terapia_concomitante = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updateTerapiaConcomitanteStmt = conn.prepareStatement(updateTerapiaConcomitanteSql);
            updateTerapiaConcomitanteStmt.setInt(1, terapia.getIdTerapiaConcomitante());
            updateTerapiaConcomitanteStmt.setInt(2, terapia.getIdMedicoUltimaModifica());
            updateTerapiaConcomitanteStmt.setDate(3, terapia.getDataFine());
            updateTerapiaConcomitanteStmt.setFloat(4, terapia.getDosaggio());
            updateTerapiaConcomitanteStmt.setString(5, terapia.getFrequenza());
            updateTerapiaConcomitanteStmt.setString(6, terapia.getOrari());
            updateTerapiaConcomitanteStmt.setInt(7, terapia.getIdTerapiaConcomitante());

            if (updateTerapiaConcomitanteStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE TERAPIA CONCOMITANTI]: Impossibile aggiornare la terapia concomitante nel database");
            }

            updateTerapiaConcomitanteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE TERAPIA CONCOMITANTI]: " + e.getMessage());
        }

        return success;
    }
}
