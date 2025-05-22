package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Farmaco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccessoFarmaciImpl implements AccessoFarmaci {

    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";


    @Override
    public boolean insertFarmaco(Farmaco farmaco) {
        String insertFarmacoSql = "insert into Farmaco (nome, principio_attivo, dosaggio, unita_di_misura, produttore, via_di_somministrazione, effetti_collaterali, interazioni_note, tipologia) value (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        boolean success = false;
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertFarmacoStmt = conn.prepareStatement(insertFarmacoSql);
            insertFarmacoStmt.setString(1, farmaco.getNome());
            insertFarmacoStmt.setString(2, farmaco.getPrincipioAttivo());
            insertFarmacoStmt.setString(3, parseDosaggiToString(farmaco.getDosaggio()));
            insertFarmacoStmt.setString(4, farmaco.getUnitaMisura());
            insertFarmacoStmt.setString(5, farmaco.getProduttore());
            insertFarmacoStmt.setString(6, farmaco.getViaSomministrazione());
            insertFarmacoStmt.setString(7, farmaco.getEffettiCollaterali());
            insertFarmacoStmt.setString(8, farmaco.getInterazioniNote());
            insertFarmacoStmt.setString(9, farmaco.getTipologia());

            if (insertFarmacoStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT FARMACO]: Impossibile inserire il farmaco nel database");
            }

            insertFarmacoStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT FARMACO]: " + e.getMessage());
        }

        return success;
    }

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

    @Override
    public boolean deleteFarmaco(int idFarmaco) {
        boolean success = false;
        String deleteFarmacoSql = "delete from Farmaco where id_farmaco = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement deleteFarmacoStmt = conn.prepareStatement(deleteFarmacoSql);
            deleteFarmacoStmt.setInt(1, idFarmaco);
            conn.setAutoCommit(false);

            if (deleteFarmacoStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE DELETE FARMACO]: Impossibile eliminare il farmaco selezionato nel database");
            }

            deleteFarmacoStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE FARMACO]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updateFarmaco(Farmaco farmaco) {
        boolean success = false;
        String updateFarmacoSql = "update Farmaco set nome = ?, principio_attivo = ?, dosaggio = ?, unita_di_misura = ?, produttore = ?, via_di_somministrazione = ?, effetti_collaterali = ?, interazioni_note = ?, tipologia = ? where id_farmaco = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updateFarmacoStmt = conn.prepareStatement(updateFarmacoSql);
            updateFarmacoStmt.setString(1, farmaco.getNome());
            updateFarmacoStmt.setString(2, farmaco.getPrincipioAttivo());
            //updateFarmacoStmt.setString(3, farmaco.getDosaggi());
            parseDosaggiToString(farmaco.getDosaggio());
            updateFarmacoStmt.setString(4, farmaco.getUnitaMisura());
            updateFarmacoStmt.setString(5, farmaco.getProduttore());
            updateFarmacoStmt.setString(6, farmaco.getViaSomministrazione());
            updateFarmacoStmt.setString(7, farmaco.getEffettiCollaterali());
            updateFarmacoStmt.setString(8, farmaco.getInterazioniNote());
            updateFarmacoStmt.setString(9, farmaco.getTipologia());
            updateFarmacoStmt.setInt(10, farmaco.getIdFarmaco());

            if (updateFarmacoStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE FARMACO]: Impossibile aggiornare il farmaco selezionato nel database");
            }

            updateFarmacoStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE FARMACO]: " + e.getMessage());
        }

        return success;
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

    // Operazione inversa per trasformare la lista dei dosaggi in una stringa
    private String parseDosaggiToString(List<Float> listaDosaggi) {
        return  listaDosaggi.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
    }
}
