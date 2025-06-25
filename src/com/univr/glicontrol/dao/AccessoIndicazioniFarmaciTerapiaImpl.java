package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.IndicazioniFarmaciTerapia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoIndicazioniFarmaciTerapiaImpl implements AccessoIndicazioniFarmaciTerapia {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<IndicazioniFarmaciTerapia> getListaIndicazioniFarmaci(int idTerapiaDiabeteAnnessa) {
        List<IndicazioniFarmaciTerapia> indicazioniFarmaciTerapia = new ArrayList<>();
        String getListaIndicazioniFarmaciSql = "select * from IndicazioniFarmaciTerapia where id_terapia_annessa = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement getListaIndicazioniFarmaciStmt = conn.prepareStatement(getListaIndicazioniFarmaciSql);
            getListaIndicazioniFarmaciStmt.setInt(1, idTerapiaDiabeteAnnessa);

            try (ResultSet rs = getListaIndicazioniFarmaciStmt.executeQuery()) {
                while (rs.next()) {
                    IndicazioniFarmaciTerapia indicazione = new IndicazioniFarmaciTerapia(
                            rs.getInt("id_terapia_annessa"),
                            rs.getInt("id_farmaco_annesso"),
                            rs.getFloat("dosaggio"),
                            rs.getString("frequenza_assunzione"),
                            rs.getString("orari_assunzione")
                    );
                    indicazione.setIdIndicazioniFarmaci(rs.getInt("id_indicazioni_farmaci"));
                    indicazioniFarmaciTerapia.add(indicazione);
                }
            }

            getListaIndicazioniFarmaciStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO INDICAZIONI FARMACI]: " + e.getMessage());
        }

        return indicazioniFarmaciTerapia;
    }

    @Override
    public boolean insertIndicazioniFarmaci(Connection conn, int idTerapiaAnnessa, int idFarmaco, float dosaggio, String frequenza, String orari) {
        boolean success = true;
        String insertIndicazioniFarmaciSql = "insert into IndicazioniFarmaciTerapia (id_terapia_annessa, id_farmaco_annesso, dosaggio, frequenza_assunzione, orari_assunzione) values (?, ?, ?, ?, ?)";

        try {
            PreparedStatement insertIndicazioniFarmaciStmt = conn.prepareStatement(insertIndicazioniFarmaciSql);
            insertIndicazioniFarmaciStmt.setInt(1, idTerapiaAnnessa);
            insertIndicazioniFarmaciStmt.setInt(2, idFarmaco);
            insertIndicazioniFarmaciStmt.setFloat(3, dosaggio);
            insertIndicazioniFarmaciStmt.setString(4, frequenza);
            insertIndicazioniFarmaciStmt.setString(5, orari);

            if (insertIndicazioniFarmaciStmt.executeUpdate() == 0) {
                success = false;
            }

            insertIndicazioniFarmaciStmt.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT INDICAZIONI FARMACI]: " + e.getMessage());
            success = false;
        }

        return success;
    }

    @Override
    public boolean deleteIndicazioniFarmaci(Connection conn, int idIndicazioniFarmaci) {
        boolean success = true;
        String deleteIndicazioniFarmaciSql = "delete from IndicazioniFarmaciTerapia where id_indicazioni_farmaci = ?";

        try {
            PreparedStatement deleteIndicazioniFarmaciStmt = conn.prepareStatement(deleteIndicazioniFarmaciSql);
            deleteIndicazioniFarmaciStmt.setInt(1, idIndicazioniFarmaci);

            if (deleteIndicazioniFarmaciStmt.executeUpdate() == 0) {
                success = false;
            }

            deleteIndicazioniFarmaciStmt.close();
            //conn.setAutoCommit(true);
            //conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE INDICAZIONI FARMACI]: " + e.getMessage());
            success = false;
        }

        return success;
    }

    @Override
    public boolean updateIndicazioniFarmaci(Connection conn, IndicazioniFarmaciTerapia indicazioniFarmaciTerapia) {
        boolean success = true;
        String updateIndicazioniFarmaciSql = "update IndicazioniFarmaciTerapia set dosaggio = ?, frequenza_assunzione = ?, orari_assunzione = ? where id_indicazioni_farmaci = ?";

        try {
            PreparedStatement updateIndicazioniFarmaciStmt = conn.prepareStatement(updateIndicazioniFarmaciSql);
            updateIndicazioniFarmaciStmt.setFloat(1, indicazioniFarmaciTerapia.getDosaggio());
            updateIndicazioniFarmaciStmt.setString(2, indicazioniFarmaciTerapia.getFrequenzaAssunzione());
            updateIndicazioniFarmaciStmt.setString(3, indicazioniFarmaciTerapia.getOrariAssunzione());
            updateIndicazioniFarmaciStmt.setInt(4, indicazioniFarmaciTerapia.getIdIndicazioniFarmaci());

            if (updateIndicazioniFarmaciStmt.executeUpdate() == 0) {
                success = false;
            }

            updateIndicazioniFarmaciStmt.close();
            //conn.setAutoCommit(true);
            //conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE INDICAZIONI FARMACI]: " + e.getMessage());
            success = false;
        }

        return success;
    }
}
