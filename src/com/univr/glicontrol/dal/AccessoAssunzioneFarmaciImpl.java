package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.AssunzioneFarmaco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoAssunzioneFarmaciImpl implements AccessoAssunzioneFarmaci {

    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<AssunzioneFarmaco> getListaFarmaciAssunti(int idPaziente) {
        List<AssunzioneFarmaco> assunzioneFarmaci = new ArrayList<>();
        String recuperaAssunzioneFarmaciSql = "select * from AssunzioneFarmaco where id_paziente_assumente = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaAssunzioneFarmaciStmt = conn.prepareStatement(recuperaAssunzioneFarmaciSql);
            recuperaAssunzioneFarmaciStmt.setInt(1, idPaziente);

            try (ResultSet rs = recuperaAssunzioneFarmaciStmt.executeQuery()) {
                while (rs.next()) {
                    assunzioneFarmaci.add(new AssunzioneFarmaco(
                            rs.getInt("id_assunzione"),
                            rs.getInt("id_paziente_assumente"),
                            rs.getInt("id_farmaco_assunto"),
                            rs.getDate("data"),
                            rs.getTime("ora"),
                            rs.getFloat("quantity")
                    ));
                }
            }

            recuperaAssunzioneFarmaciStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE RECUPERO TABELLA ASSUNZIONE FARMACI]: " + e.getMessage());
        }

        return assunzioneFarmaci;
    }

    @Override
    public List<AssunzioneFarmaco> getListaFarmaciAssuntiOggi(int idPaziente, Date data, int idFarmaco) {
        List<AssunzioneFarmaco> assunzioneFarmaciOdierna = new ArrayList<>();
        String recuperaAssunzioneFarmaciOdiernaSql = "select * from AssunzioneFarmaco where id_paziente_assumente = ? and data = ? and id_farmaco_assunto = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaAssunzioneFarmaciOdiernaStmt = conn.prepareStatement(recuperaAssunzioneFarmaciOdiernaSql);
            recuperaAssunzioneFarmaciOdiernaStmt.setInt(1, idPaziente);
            recuperaAssunzioneFarmaciOdiernaStmt.setDate(2, data);
            recuperaAssunzioneFarmaciOdiernaStmt.setInt(3, idFarmaco);

            try (ResultSet rs = recuperaAssunzioneFarmaciOdiernaStmt.executeQuery()) {
                while (rs.next()) {
                    assunzioneFarmaciOdierna.add(new AssunzioneFarmaco(
                            rs.getInt("id_assunzione"),
                            rs.getInt("id_paziente_assumente"),
                            rs.getInt("id_farmaco_assunto"),
                            rs.getDate("data"),
                            rs.getTime("ora"),
                            rs.getFloat("quantity")
                    ));
                }
            }

            recuperaAssunzioneFarmaciOdiernaStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE RECUPERO TABELLA ASSUNZIONE ODIERNA FARMACI]: " + e.getMessage());
        }

        return assunzioneFarmaciOdierna;
    }

    @Override
    public boolean insertAssunzioneFarmaci(int idPaziente, int idFarmaco, Date data, Time ora, float dose) {
        boolean success = false;
        String insertAssunzioneFarmaciSql = "insert into AssunzioneFarmaco (id_paziente_assumente, id_farmaco_assunto, data, ora, quantity) values (?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement insertAssunzioneFarmaciStmt = conn.prepareStatement(insertAssunzioneFarmaciSql);
            insertAssunzioneFarmaciStmt.setInt(1, idPaziente);
            insertAssunzioneFarmaciStmt.setInt(2, idFarmaco);
            insertAssunzioneFarmaciStmt.setDate(3, data);
            insertAssunzioneFarmaciStmt.setTime(4, ora);
            insertAssunzioneFarmaciStmt.setFloat(5, dose);

            if (insertAssunzioneFarmaciStmt.executeUpdate() != 0) {
                success = true;
            } else {
                System.err.println("[ERRORE INSERT ASSUNZIONE FARMACI]: Impossibile inserire l'assunzione del farmaco nel database");
            }

            insertAssunzioneFarmaciStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE INSERT ASSUNZIONE FARMACI]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean deleteAssunzioneFarmaco(int idAssunzioneFarmaco) {
        boolean success = false;
        String deleteAssunzioneFarmaciSql = "delete from AssunzioneFarmaco where id_assunzione = ?";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement deleteAssunzioneFarmaciStmt = conn.prepareStatement(deleteAssunzioneFarmaciSql);
            deleteAssunzioneFarmaciStmt.setInt(1, idAssunzioneFarmaco);

            if (deleteAssunzioneFarmaciStmt.executeUpdate() != 0) {
                success = true;
            } else {
                System.err.println("[ERRORE DELETE ASSUNZIONE FARMACI]: Impossibile eliminare l'assunzione del farmaco dal database");
            }

            deleteAssunzioneFarmaciStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE DELETE ASSUNZIONE FARMACI]: " + e.getMessage());
        }

        return success;
    }
}
