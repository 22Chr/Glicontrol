package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.RilevazioneGlicemica;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                            rs.getInt("valore"),
                            rs.getString("pasto"),
                            rs.getString("indicazioni_temporali"),
                            rs.getBoolean("gestito")
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
    public List<RilevazioneGlicemica> recuperaRilevazioniPazienteNonGestite(int idPaziente) {
        List<RilevazioneGlicemica> rilevazioniNonGestite = new ArrayList<>();

        String recuperaRilevazioniNonGestiteSql = "select * from LivelloGlicemia where id_paziente_glicemico = ? and gestito = false";
        try {
            java.sql.Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaRilevazioniStmt = conn.prepareStatement(recuperaRilevazioniNonGestiteSql);
            recuperaRilevazioniStmt.setInt(1, idPaziente);

            try (ResultSet rs = recuperaRilevazioniStmt.executeQuery()) {
                while (rs.next()) {
                    rilevazioniNonGestite.add(new RilevazioneGlicemica(
                            rs.getInt("id_glicemia"),
                            rs.getInt("id_paziente_glicemico"),
                            rs.getDate("data"),
                            rs.getTime("ora"),
                            rs.getInt("valore"),
                            rs.getString("pasto"),
                            rs.getString("indicazioni_temporali"),
                            rs.getBoolean("gestito")
                    ));
                }
            }

            recuperaRilevazioniStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO RILEVAZIONI]: " + e.getMessage());
        }

        return rilevazioniNonGestite;
    }

    @Override
    public List<RilevazioneGlicemica> recuperaRilevazioniPerDataNonGestite(int idPaziente, LocalDate data) {
        List<RilevazioneGlicemica> rilevazioniNonGestite = new ArrayList<>();

        String RecuperaRilevazioniGlicemicheNonGestitePerDatasql = "select * from livelloglicemia where id_paziente_glicemico = ? and data = ? and  gestito = false";

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(RecuperaRilevazioniGlicemicheNonGestitePerDatasql)) {

            ps.setInt(1, idPaziente);
            ps.setDate(2, Date.valueOf(data));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RilevazioneGlicemica r = new RilevazioneGlicemica(
                        rs.getInt("id_glicemia"),
                        rs.getInt("id_paziente_glicemico"),
                        rs.getDate("data"),
                        rs.getTime("ora"),
                        rs.getInt("valore"),
                        rs.getString("pasto"),
                        rs.getString("indicazioni_temporali"),
                        rs.getBoolean("gestito")
                );
                rilevazioniNonGestite.add(r);
            }
        } catch (SQLException e) {
            System.out.println("[ERRORE RILEVAZIONI GIORNALIERE]: " + e.getMessage());

        }

        return rilevazioniNonGestite;
    }

    @Override
    public boolean insertRilevazioneGlicemica(int idPaziente, Date data, Time ora, float valore, String pasto, String indicazioniTemporali) {
        boolean success = false;
        String insertRilevazioneSql = "insert into LivelloGlicemia (id_paziente_glicemico, data, ora, valore, pasto, indicazioni_temporali, gestito) value (?, ?, ?, ?, ?, ?, ?)";

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
            insertRilevazioneStmt.setBoolean(7, false);

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

    // Media giornaliera per Settimana (utile per visualizzazione settimanale)
    public Map<String, Double> recuperaMediaGiornalieraPerSettimanaGlicemia(int idPaziente, int anno, int numeroSettimana) {
        Map<String, Double> mediaGiornaliera = new LinkedHashMap<>();
        String sql = "SELECT DATE(data) AS giorno, AVG(valore) AS media " +
                "FROM livelloglicemia WHERE id_paziente_glicemico = ? " +
                "AND YEAR(data) = ? AND WEEK(data, 1) = ? " +
                "GROUP BY giorno ORDER BY giorno";
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPaziente);
            stmt.setInt(2, anno);
            stmt.setInt(3, numeroSettimana);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mediaGiornaliera.put(rs.getString("giorno"), rs.getDouble("media"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRORE MEDIA GIORNALIERA PER SETTIMANA]: " + e.getMessage());
        }
        return mediaGiornaliera;
    }


    // Media settimanale per mese (utile per visualizzazione mensile)
    public Map<String, Double> recuperaMediaMensileGlicemiaPerMeseCorrente(int idPaziente, int anno, int mese) {
        Map<String, Double> mediaMensile = new LinkedHashMap<>();
        String sql = "SELECT YEAR(data) AS anno, WEEK(data, 1) AS settimana, AVG(valore) AS media " +
                "FROM livelloglicemia " +
                "WHERE id_paziente_glicemico = ? AND YEAR(data) = ? AND MONTH(data) = ? " +
                "GROUP BY anno, settimana " +
                "ORDER BY anno, settimana";

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPaziente);
            stmt.setInt(2, anno);
            stmt.setInt(3, mese);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int week = rs.getInt("settimana");
                int year = rs.getInt("anno");
                String label = "Settimana " + week;
                mediaMensile.put(label, rs.getDouble("media"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRORE MEDIA MENSILE FILTRATA]: " + e.getMessage());
        }

        return mediaMensile;
    }

    @Override
    public boolean updateStatoRilevazioneGlicemica(int idRilevazioneGlicemica) {
        boolean success = false;
        String updateRilevazioneSql = "update LivelloGlicemia set gestito = true where id_glicemia = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement updateStatoRilevazioneStmt = conn.prepareStatement(updateRilevazioneSql);
            updateStatoRilevazioneStmt.setInt(1, idRilevazioneGlicemica);

            if (updateStatoRilevazioneStmt.executeUpdate() != 0) {
                success = true;
            } else {
                System.err.println("[ERRORE UPDATE STATO RILEVAZIONE]: Non è stato possibile aggiornare la rilevazione selezionata");
            }

            updateStatoRilevazioneStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE UPDATE STATO RILEVAZIONE]: " + e.getMessage());
        }

        return success;
    }

    public List<RilevazioneGlicemica> recuperaRilevazioniPerData(int idPaziente, LocalDate data) {
        List<RilevazioneGlicemica> lista = new ArrayList<>();
        String recuperRilevazioniPerDatasql = "SELECT * FROM livelloglicemia WHERE id_paziente_glicemico = ? AND data = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(recuperRilevazioniPerDatasql)) {

            ps.setInt(1, idPaziente);
            ps.setDate(2, Date.valueOf(data));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RilevazioneGlicemica r = new RilevazioneGlicemica(
                        rs.getInt("id_glicemia"),
                        rs.getInt("id_paziente_glicemico"),
                        rs.getDate("data"),
                        rs.getTime("ora"),
                        rs.getInt("valore"),
                        rs.getString("pasto"),
                        rs.getString("indicazioni_temporali"),
                        rs.getBoolean("gestito")
                );
                lista.add(r);
            }
        } catch (SQLException e) {
            System.out.println("[ERRORE RILEVAZIONI GIORNALIERE]: " + e.getMessage());

        }

        return lista;
    }
}
