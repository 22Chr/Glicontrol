package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.LogInfoPaziente;
import com.univr.glicontrol.bll.LogPatologie;
import com.univr.glicontrol.bll.LogTerapia;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccessoLogImpl implements AccessoLog {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";


    @Override
    public List<LogTerapia> getListaLogTerapie() {
        List<LogTerapia> listaLogTerapia = new ArrayList<>();
        String getListaLogTerapieSql = "select * from LogTerapie";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaLogTerapiaStmt = conn.prepareStatement(getListaLogTerapieSql);

            try (ResultSet rs = recuperaLogTerapiaStmt.executeQuery()) {
                while (rs.next()) {
                    listaLogTerapia.add(new LogTerapia(
                            rs.getInt("id_log"),
                            rs.getInt("id_terapia_modificata"),
                            rs.getInt("id_medico"),
                            rs.getString("descrizione_modifiche"),
                            rs.getString("note_paziente"),
                            rs.getTimestamp("timestamp")
                    ));
                }
            }

            recuperaLogTerapiaStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE RECUPERO TABELLA LOG TERAPIE]: " + e.getMessage());
        }

        // ritorna la lista capovolta, quindi con i log più recenti in cima
        return listaLogTerapia.reversed();
    }

    @Override
    public boolean insertLogTerapia(int idTerapia, int idMedico, String descrizioneModifiche, String notePaziente) {
        boolean success = false;
        String addLogTerapiaSql = "insert into LogTerapie (id_terapia_modificata, id_medico, descrizione_modifiche, note_paziente, timestamp) values (?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement addLogTerapiaStmt = conn.prepareStatement(addLogTerapiaSql);
            addLogTerapiaStmt.setInt(1, idTerapia);
            addLogTerapiaStmt.setInt(2, idMedico);
            addLogTerapiaStmt.setString(3, descrizioneModifiche);
            addLogTerapiaStmt.setString(4, notePaziente);
            addLogTerapiaStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            if (addLogTerapiaStmt.executeUpdate() != 0) {
                success = true;
            } else {
                System.out.println("[ERRORE INSERT LOG TERAPIA]: Impossibile inserire i log per questa terapia");
            }

            addLogTerapiaStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public List<LogInfoPaziente> getListaLogInfoPaziente() {
        List<LogInfoPaziente> listaLogInfoPaziente = new ArrayList<>();
        String getListaLogInfoPazienteSql = "select * from LogInfoPaziente";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaLogInfoPazientiStmt = conn.prepareStatement(getListaLogInfoPazienteSql);

            try (ResultSet rs = recuperaLogInfoPazientiStmt.executeQuery()) {
                while (rs.next()) {
                    listaLogInfoPaziente.add(new LogInfoPaziente(
                            rs.getInt("id_log_info"),
                            rs.getInt("id_medico"),
                            rs.getInt("id_paziente"),
                            rs.getString("descrizione_modifiche_info"),
                            rs.getTimestamp("timestamp")
                    ));
                }
            }

            recuperaLogInfoPazientiStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE RECUPERO TABELLA LOG INFO PAZIENTI]: " + e.getMessage());
        }

        return listaLogInfoPaziente.reversed();
    }

    @Override
    public boolean insertLogInfoPaziente(int idMedico, int idPaziente, String descrizione) {
        // TODO
        return false;
    }

    @Override
    public List<LogPatologie> getListaLogPatologie() {
        // TODO
        return List.of();
    }

    @Override
    public boolean insertLogPatologie(int idPatologia, int idMedico, String descrizione) {
        // TODO
        return false;
    }

}
