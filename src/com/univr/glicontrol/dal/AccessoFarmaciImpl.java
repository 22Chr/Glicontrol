package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.Farmaco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoFarmaciImpl implements AccessoFarmaci {

    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<Farmaco> recuperaTuttiFarmaci() {
        String recuperaListaFarmaciSql = "select * from Farmaco";
        List<Farmaco> listaFarmaci = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperListaFarmaciStmt = conn.prepareStatement(recuperaListaFarmaciSql, PreparedStatement.RETURN_GENERATED_KEYS);
            try (ResultSet rs = recuperListaFarmaciStmt.executeQuery()) {
                while (rs.next()) {
                    listaFarmaci.add(new Farmaco(
                            rs.getInt("id_farmaco"),
                            rs.getString("nome"),
                            rs.getString("principio_attivo"),
                            parseDosaggiToFloat(rs.getString("dosaggio")),
                            rs.getString("unita_di_misura"),
                            rs.getString("produttore"),
                            rs.getString("via_di_somministrazione"),
                            rs.getString("effetti_collaterali"),
                            rs.getString("interazioni_note"),
                            rs.getString("tipologia")
                    ));
                }
            }

            recuperListaFarmaciStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO LISTA FARMACI]: " + e.getMessage());
        }

        return listaFarmaci;
    }

    // Parsing della stringa dei dosaggi come lista di float
    private List<Float> parseDosaggiToFloat(String dosaggi) {
        List<Float> listaDosaggi = new ArrayList<>();
        String[] arrayDosaggi = dosaggi.split(",");
        for (String s : arrayDosaggi) {
            listaDosaggi.add(Float.parseFloat(s));
        }
        return listaDosaggi;
    }
}
