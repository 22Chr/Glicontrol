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
        boolean success = false;
        String addLogInfoPazienteSql = "insert into LogInfoPaziente (id_log_info, id_medico, id_paziente, descrizione_modifiche_info, timestamp) values (?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement addLogInfoPazienteStmt = conn.prepareStatement(addLogInfoPazienteSql);
            addLogInfoPazienteStmt.setInt(1, idMedico);
            addLogInfoPazienteStmt.setInt(2, idPaziente);
            addLogInfoPazienteStmt.setString(3, descrizione);
            addLogInfoPazienteStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            if (addLogInfoPazienteStmt.executeUpdate() != 0) {
                success = true;
            } else {
                System.out.println("[ERRORE INSERT LOG INFO PAZIENTE]: Impossibile inserire i log per questa info paziente");
            }

            addLogInfoPazienteStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public List<LogPatologie> getListaLogPatologie() {
        List<LogPatologie> listaLogPatologie = new ArrayList<>();
        String getListaLogPatologieSql = "select * from LogPatologie";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaLogPatologieStmt = conn.prepareStatement(getListaLogPatologieSql);

            try (ResultSet rs = recuperaLogPatologieStmt.executeQuery()) {
                while (rs.next()) {
                    listaLogPatologie.add(new LogPatologie(
                            rs.getInt("id_log_patologie"),
                            rs.getInt("id_patologia_inserita"),
                            rs.getInt("id_medico"),
                            rs.getString("descrizione_creazione"),
                            rs.getTimestamp("timestamp")
                    ));
                }
            }

            recuperaLogPatologieStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE RECUPERO TABELLA LOG PATOLOGIE]: " + e.getMessage());
        }

        // ritorna la lista capovolta, quindi con i log più recenti in cima
        return listaLogPatologie.reversed();
    }

    @Override
    public boolean insertLogPatologie(int idPatologia, int idMedico, String descrizione) {
        boolean success = false;
        String addLogPatologieSql = "insert into LogPatologie (id_patologia_inserita, id_medico, descrizione_creazione, timestamp) values (?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement addLogPatologieStmt = conn.prepareStatement(addLogPatologieSql);
            addLogPatologieStmt.setInt(1, idPatologia);
            addLogPatologieStmt.setInt(2, idMedico);
            addLogPatologieStmt.setString(3, descrizione);
            addLogPatologieStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            if (addLogPatologieStmt.executeUpdate() != 0) {
                success = true;
            } else {
                System.out.println("[ERRORE INSERT LOG PATOLOGIA]: Impossibile inserire i log per questa patologia");
            }

            addLogPatologieStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT]: " + e.getMessage());
        }

        return success;
    }

}
