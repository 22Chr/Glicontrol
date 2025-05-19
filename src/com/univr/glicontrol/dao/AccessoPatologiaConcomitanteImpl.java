package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.PatologiaConcomitante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoPatologiaConcomitanteImpl implements AccessoPatologiaConcomitante {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<PatologiaConcomitante> recuperaPatologiePerPaziente(int idPaziente) {
        List<PatologiaConcomitante> listaPatologiePaziente = new ArrayList<>();
        String recuperaPatologieSql = "select * from PatologiaConcomitante where id_paziente_comorbidita = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaPatologieStmt = conn.prepareStatement(recuperaPatologieSql);
            recuperaPatologieStmt.setInt(1, idPaziente);

            try (ResultSet rs = recuperaPatologieStmt.executeQuery()) {
                while (rs.next()) {
                    listaPatologiePaziente.add(new PatologiaConcomitante(
                            rs.getInt("id_patologia_concomitante"),
                            rs.getInt("id_paziente_comorbidita"),
                            rs.getString("nome_patologia"),
                            rs.getString("descrizione"),
                            rs.getDate("data_inizio"),
                            rs.getDate("data_fine")
                    ));
                }
            }

            recuperaPatologieStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO PATOLOGIE CONCOMITANTI]: " + e.getMessage());
        }

        return listaPatologiePaziente;
    }

    @Override
    public boolean insertPatologiaConcomitante(int idPaziente, String nomePatologia, String descrizione, Date dataInizio, Date dataFine) {
        boolean success = false;
        String insertPatologiaSql = "insert into PatologiaConcomitante (id_paziente_comorbidita, nome_patologia, descrizione, data_inizio, data_fine) value (?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertPatologiaStmt = conn.prepareStatement(insertPatologiaSql);
            insertPatologiaStmt.setInt(1, idPaziente);
            insertPatologiaStmt.setString(2, nomePatologia);
            insertPatologiaStmt.setString(3, descrizione);
            insertPatologiaStmt.setDate(4, dataInizio);
            insertPatologiaStmt.setDate(5, dataFine);

            if (insertPatologiaStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT PATOLOGIA CONCOMITANTE]: Impossibile inserire la nuova patologia concomitante nel database");
            }

            insertPatologiaStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT PATOLOGIA CONCOMITANTE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean deletePatologiaConcomitante(int idPatologiaConcomitante) {
        boolean success = false;
        String deletePatologiaSql = "delete from PatologiaConcomitante where id_patologia = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement deletePatologiaStmt = conn.prepareStatement(deletePatologiaSql);
            deletePatologiaStmt.setInt(1, idPatologiaConcomitante);

            if (deletePatologiaStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE DELETE PATOLOGIA CONCOMITANTE]: Impossibile eliminare la patologia concomitante selezionata dal database");
            }

            conn.setAutoCommit(true);
            deletePatologiaStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE PATOLOGIA CONCOMITANTE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updatePatologiaConcomitante(PatologiaConcomitante patologiaConcomitante) {
        boolean success = false;
        String updatePatologiaSql = "update PatologiaConcomitante set nome_patologia = ?, descrizione = ?, data_fine = ? where id_patologia = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updatePatologiaStmt = conn.prepareStatement(updatePatologiaSql);
            updatePatologiaStmt.setString(1, patologiaConcomitante.getNomePatologia());
            updatePatologiaStmt.setString(2, patologiaConcomitante.getDescrizione());
            updatePatologiaStmt.setDate(3, patologiaConcomitante.getDataFine());
            updatePatologiaStmt.setInt(4, patologiaConcomitante.getIdPatologia());

            if (updatePatologiaStmt.executeUpdate() != 0) {
                conn.commit();
            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE PATOLOGIA CONCOMITANTE]: Impossibile aggiornare la patologia concomitante selezionata nel database");
            }

            conn.setAutoCommit(true);
            updatePatologiaStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE PATOLOGIA CONCOMITANTE]: " + e.getMessage());
        }

        return success;
    }
}
