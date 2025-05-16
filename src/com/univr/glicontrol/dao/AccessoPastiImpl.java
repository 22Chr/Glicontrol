package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Pasto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoPastiImpl implements AccessoPasti{
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<Pasto> recuperaPastiPerUtente(int idPaziente) {
        String recuperaPastiSql = "select * from Pasto where id_paziente = ?";
        List<Pasto> pasti = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperPastiStmt = conn.prepareStatement(recuperaPastiSql);
            recuperPastiStmt.setInt(1, idPaziente);

            try (ResultSet rs = recuperPastiStmt.executeQuery()) {
                while (rs.next()) {
                    pasti.add(new Pasto(
                            rs.getInt("id_pasto"),
                            rs.getInt("id_utente"),
                            rs.getString("nome_pasto"),
                            rs.getTime("ora")
                    ));
                }
            }

            recuperPastiStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO PASTI]: " + e.getMessage());
        }

        return pasti;
    }

    @Override
    public boolean insertPasto(int idPaziente, String nomePasto, Time orario) {
        String insertPastoSql = "insert into Pasto (id_paziente, nome_pasto, ora) value (?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement insertPastoStmt = conn.prepareStatement(insertPastoSql);
            insertPastoStmt.setInt(1, idPaziente);
            insertPastoStmt.setString(2, nomePasto);
            insertPastoStmt.setTime(3, orario);

            if (insertPastoStmt.executeUpdate() != 0) {
                return true;
            } else {
                System.out.println("[ERRORE INSERT PASTO]: Impossibile inserire il pasto nel database");
            }

            insertPastoStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT PASTO]: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deletePasto(int idPasto) {
        boolean success = false;
        String deletePastoSql = "delete from Pasto where id_pasto = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement deletePastoStmt = conn.prepareStatement(deletePastoSql);
            deletePastoStmt.setInt(1, idPasto);
            conn.setAutoCommit(false);

            if (deletePastoStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                System.out.println("[ERRORE DELETE PASTO]: Impossibile eliminare il pasto selezionato nel database");
            }

            conn.setAutoCommit(true);
            deletePastoStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE PASTO]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updatePasto(Pasto pasto) {
        boolean success = false;
        String updatePastoSql = "update Pasto set nome_pasto = ?, ora = ? where id_pasto = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updatePastoStmt = conn.prepareStatement(updatePastoSql);
            updatePastoStmt.setString(1, pasto.getNomePasto());
            updatePastoStmt.setTime(2, pasto.getOrario());

            if (updatePastoStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                System.out.println("[ERRORE UPDATE PASTO]: Impossibile aggiornare il pasto selezionato nel database");
            }

            conn.setAutoCommit(true);
            updatePastoStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE PASTO]: " + e.getMessage());
        }

        return success;
    }
}
