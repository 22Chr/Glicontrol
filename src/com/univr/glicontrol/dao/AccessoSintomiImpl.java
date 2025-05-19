package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.Sintomo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoSintomiImpl implements AccessoSintomi{
    private final Paziente paziente;

    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    public AccessoSintomiImpl(Paziente paziente) {
        this.paziente = paziente;
    }

    @Override
    public List<Sintomo> recuperaSintomiPerPaziente() {

        List<Sintomo> sintomi = new ArrayList<>();

        String recuperSintomiSql = "select * from Sintomo where id_paziente_sintomatico = ?";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperSintomiStmt = conn.prepareStatement(recuperSintomiSql);
            recuperSintomiStmt.setInt(1, paziente.getIdUtente());

            try (ResultSet rs = recuperSintomiStmt.executeQuery()) {
                while (rs.next()) {
                    sintomi.add(new Sintomo(
                            rs.getInt("id_sintomo"),
                            rs.getInt("id_paziente_sintomatico"),
                            rs.getString("descrizione"),
                            rs.getDate("data"),
                            rs.getTime("ora")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO SINTOMI]: " + e.getMessage());
        }

        return sintomi;
    }

    @Override
    public boolean insertSintomo(String descrizione, Date data, Time ora) {
        boolean success = false;

        String insertSintomoSql = "insert into Sintomo (id_paziente_sintomatico, descrizione, data, ora) value (?, ?, ?, ?)";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);

            PreparedStatement insertSintomoStmt = conn.prepareStatement(insertSintomoSql);
            insertSintomoStmt.setInt(1, paziente.getIdUtente());
            insertSintomoStmt.setString(2, descrizione);
            insertSintomoStmt.setDate(3, data);
            insertSintomoStmt.setTime(4, ora);

            if (insertSintomoStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT SINTOMO]: Impossibile inserire il nuovo sintomo nel database");
            }

            insertSintomoStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT SINTOMO]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean deleteSintomo(int idSintomo) {
        boolean success = false;

        String deleteSintomoSql = "delete from Sintomo where id_sintomo = ?";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);

            PreparedStatement deleteSintomoStmt = conn.prepareStatement(deleteSintomoSql);
            deleteSintomoStmt.setInt(1, idSintomo);

            if (deleteSintomoStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE DELETE SINTOMO]: Impossibile eliminare il sintomo selezionato dal database");
            }

            deleteSintomoStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE SINTOMO]: " + e.getMessage());
        }

        return success;
    }
}
