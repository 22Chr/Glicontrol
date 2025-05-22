package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.FarmaciTerapiaDiabete;
import com.univr.glicontrol.bll.Farmaco;
import com.univr.glicontrol.bll.GestioneFarmaci;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoFarmaciTerapiaDiabeteImpl implements AccessoFarmaciTerapiaDiabete {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<Farmaco> getListaFarmaciPerTerapiaDiabete(int idTerapiaDiabete) {
        List<FarmaciTerapiaDiabete> listaCompletaFarmaciTerapiaDiabete = getListaCompletaFarmaciTerapiaDiabete(idTerapiaDiabete);
        List<Farmaco> listaFarmaci = new ArrayList<>();

        for (FarmaciTerapiaDiabete ftd : listaCompletaFarmaciTerapiaDiabete) {
            listaFarmaci.add(GestioneFarmaci.getInstance().getFarmacoById(ftd.getIdFarmaco()));
        }

        return listaFarmaci;
    }

    @Override
    public List<FarmaciTerapiaDiabete> getListaCompletaFarmaciTerapiaDiabete(int idTerapiaDiabete) {
        List<FarmaciTerapiaDiabete> listaFarmaciTerapiaDiabete = new ArrayList<>();
        String recuperaFarmaciTerapiaDiabeteSql = "select * from FarmaciTerapiaDiabete where id_terapia_diabete_riferimento = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaFarmaciTerapiaDiabeteStmt = conn.prepareStatement(recuperaFarmaciTerapiaDiabeteSql);
            recuperaFarmaciTerapiaDiabeteStmt.setInt(1, idTerapiaDiabete);

            try (ResultSet rs = recuperaFarmaciTerapiaDiabeteStmt.executeQuery()) {
                while (rs.next()) {
                    listaFarmaciTerapiaDiabete.add(new FarmaciTerapiaDiabete(
                            rs.getInt("id_farmaco_terapia"),
                            rs.getInt("id_farmaco_riferimento"),
                            rs.getInt("id_terapia_diabete_riferimento")
                    ));
                }
            }

            recuperaFarmaciTerapiaDiabeteStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: " + e.getMessage());
        }

        return listaFarmaciTerapiaDiabete;
    }

    @Override
    public boolean insertFarmaciTerapiaDiabete(int idTerapiaDiabete, List<Farmaco> farmaci) {
        boolean success = false;
        String inserisciFarmaciTerapiaDiabeteSql = "insert into FarmaciTerapiaDiabete (id_terapia_diabete_riferimento, id_farmaco_riferimento) value (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, pwd)) {
            conn.setAutoCommit(false);
            try (PreparedStatement inserisciFarmaciTerapiaDiabeteStmt = conn.prepareStatement(inserisciFarmaciTerapiaDiabeteSql)) {

                for (Farmaco f : farmaci) {
                    inserisciFarmaciTerapiaDiabeteStmt.setInt(1, idTerapiaDiabete);
                    inserisciFarmaciTerapiaDiabeteStmt.setInt(2, f.getIdFarmaco());

                    if (inserisciFarmaciTerapiaDiabeteStmt.executeUpdate() != 0) {
                        success = true;
                    } else {
                        success = false;
                        break;
                    }
                }

                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                    System.out.println("[ERRORE INSERT NUOVA ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: Impossibile inserire la nuova entry nella tabella associativa");
                }

            } catch (SQLException e) {
                System.out.println("[ERRORE INSERT NUOVA ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT NUOVA ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean deleteFarmaciTerapiaDiabete(int idTerapiaDiabete, int idFarmaco) {
        boolean success = false;
        String rimuoviFarmaciTerapiaDiabeteSql = "delete from FarmaciTerapiaDiabete where id_terapia_diabete_riferimento = ? and id_farmaco_riferimento = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement rimuoviFarmaciTerapiaDiabeteStmt = conn.prepareStatement(rimuoviFarmaciTerapiaDiabeteSql);
            rimuoviFarmaciTerapiaDiabeteStmt.setInt(1, idTerapiaDiabete);
            rimuoviFarmaciTerapiaDiabeteStmt.setInt(2, idFarmaco);

            if(rimuoviFarmaciTerapiaDiabeteStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE RIMOZIONE ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: Impossibile eliminare la entry nella tabella associativa");
            }

            rimuoviFarmaciTerapiaDiabeteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RIMOZIONE ENTRY NELLA TABELLA ASSOCIATIVA FARMACI-TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }
}
