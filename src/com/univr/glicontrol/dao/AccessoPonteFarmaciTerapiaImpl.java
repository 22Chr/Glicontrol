package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.PonteFarmaciTerapia;
import com.univr.glicontrol.bll.Farmaco;
import com.univr.glicontrol.bll.GestioneFarmaci;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoPonteFarmaciTerapiaImpl implements AccessoPonteFarmaciTerapia {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<Farmaco> getListaFarmaciPerTerapia(int idTerapiaDiabete) {
        List<PonteFarmaciTerapia> listaCompletaPonteFarmaciTerapiaDiabete = getListaCompletaFarmaciTerapia(idTerapiaDiabete);
        List<Farmaco> listaFarmaci = new ArrayList<>();

        for (PonteFarmaciTerapia ftd : listaCompletaPonteFarmaciTerapiaDiabete) {
            listaFarmaci.add(GestioneFarmaci.getInstance().getFarmacoById(ftd.getIdFarmaco()));
        }

        return listaFarmaci;
    }

    @Override
    public List<PonteFarmaciTerapia> getListaCompletaFarmaciTerapia(int idTerapiaDiabete) {
        List<PonteFarmaciTerapia> listaPonteFarmaciTerapiaDiabete = new ArrayList<>();
        String recuperaFarmaciTerapiaDiabeteSql = "select * from FarmaciTerapia where id_terapia_riferimento = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaFarmaciTerapiaDiabeteStmt = conn.prepareStatement(recuperaFarmaciTerapiaDiabeteSql);
            recuperaFarmaciTerapiaDiabeteStmt.setInt(1, idTerapiaDiabete);

            try (ResultSet rs = recuperaFarmaciTerapiaDiabeteStmt.executeQuery()) {
                while (rs.next()) {
                    listaPonteFarmaciTerapiaDiabete.add(new PonteFarmaciTerapia(
                            rs.getInt("id_farmaco_terapia"),
                            rs.getInt("id_terapia_riferimento"),
                            rs.getInt("id_farmaco_riferimento")
                    ));
                }
            }

            recuperaFarmaciTerapiaDiabeteStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: " + e.getMessage());
        }

        return listaPonteFarmaciTerapiaDiabete;
    }

    @Override
    public boolean insertFarmaciTerapia(Connection conn, int idTerapiaDiabete, List<Farmaco> farmaci) {
        boolean success = true;
        String inserisciFarmaciTerapiaSql = "insert into FarmaciTerapia (id_terapia_riferimento, id_farmaco_riferimento) values (?, ?)";


        try (PreparedStatement inserisciFarmaciTerapiaStmt = conn.prepareStatement(inserisciFarmaciTerapiaSql)) {

            for (Farmaco f : farmaci) {
                inserisciFarmaciTerapiaStmt.setInt(1, idTerapiaDiabete);
                inserisciFarmaciTerapiaStmt.setInt(2, f.getIdFarmaco());

                if (inserisciFarmaciTerapiaStmt.executeUpdate() == 0) {
                    success = false;
                    break;
                }
            }

            if (!success) {
                System.out.println("[ERRORE INSERT NUOVA ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: Impossibile inserire la nuova entry nella tabella associativa");
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT NUOVA ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: " + e.getMessage());
            success = false;
        }

        return success;
    }

    @Override
    public boolean deleteFarmaciTerapia(Connection conn, int idTerapiaDiabete, int idFarmaco) {
        boolean success = true;
        String rimuoviFarmaciTerapiaDiabeteSql = "delete from FarmaciTerapia where id_terapia_riferimento = ? and id_farmaco_riferimento = ?";

        try {
            PreparedStatement rimuoviFarmaciTerapiaDiabeteStmt = conn.prepareStatement(rimuoviFarmaciTerapiaDiabeteSql);
            rimuoviFarmaciTerapiaDiabeteStmt.setInt(1, idTerapiaDiabete);
            rimuoviFarmaciTerapiaDiabeteStmt.setInt(2, idFarmaco);

            if(rimuoviFarmaciTerapiaDiabeteStmt.executeUpdate() == 0) {
                success = false;
            }

            if (!success) {
                System.out.println("[ERRORE RIMOZIONE ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: Impossibile eliminare la entry nella tabella associativa");
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE RIMOZIONE ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: " + e.getMessage());
            success = false;
        }

        return success;
    }
}
