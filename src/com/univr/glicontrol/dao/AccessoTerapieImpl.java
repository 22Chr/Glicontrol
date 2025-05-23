package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoTerapieImpl implements AccessoTerapie {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    private final AccessoFarmaciTerapia accessoFarmaciTerapia = new AccessoFarmaciTerapiaImpl();

    @Override
    public List<TerapiaDiabete> getTerapieDiabetePaziente(int idPaziente) {
        List<TerapiaDiabete> terapiaDiabete = new ArrayList<>();
        String recuperaTerapiadiabeteSql = "select * from TerapiaDiabete where id_paziente_connesso = ?";

        AccessoFarmaciTerapia accessoFarmaciTerapia = new AccessoFarmaciTerapiaImpl();

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaTerapiaDiabeteStmt = conn.prepareStatement(recuperaTerapiadiabeteSql);
            recuperaTerapiaDiabeteStmt.setInt(1, idPaziente);
            try (ResultSet rs = recuperaTerapiaDiabeteStmt.executeQuery()) {
                while (rs.next()) {
                    terapiaDiabete.add(new TerapiaDiabete(
                            rs.getInt("id_terapia_diabete"),
                            rs.getInt("id_paziente_connesso"),
                            rs.getInt("id_medico_ultima_modifica"),
                            rs.getDate("data_inizio"),
                            rs.getDate("data_fine"),
                            accessoFarmaciTerapia.getListaFarmaciPerTerapia(rs.getInt("id_terapia_diabete"))
                    ));
                }
            }

            recuperaTerapiaDiabeteStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TERAPIA DIABETE]: " + e.getMessage());
        }



        return terapiaDiabete;
    }

    @Override
    public List<TerapiaConcomitante> getTerapieConcomitantiPaziente(int idPaziente) {
        List<TerapiaConcomitante> terapieConcomitanti = new ArrayList<>();
        String recuperaTerapieConcomitantiSql = "select * from TerapiaConcomitante where id_paziente_terapia_concomitante = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaTerapieConcomitantiStmt = conn.prepareStatement(recuperaTerapieConcomitantiSql);
            recuperaTerapieConcomitantiStmt.setInt(1, idPaziente);
            try (ResultSet rs = recuperaTerapieConcomitantiStmt.executeQuery()) {
                while (rs.next()) {
                    terapieConcomitanti.add(new TerapiaConcomitante(
                            rs.getInt("id_terapia_concomitante"),
                            rs.getInt("id_paziente_terapia_concomitante"),
                            rs.getInt("id_patologia_comorbidita"),
                            rs.getInt("id_medico_ultima_modifica"),
                            rs.getDate("data_inizio"),
                            rs.getDate("data_fine"),
                            accessoFarmaciTerapia.getListaFarmaciPerTerapia(rs.getInt("id_terapia_concomitante"))
                    ));
                }
            }

            recuperaTerapieConcomitantiStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TERAPIE CONCOMITANTI]: " + e.getMessage());
        }

        return terapieConcomitanti;
    }

    @Override
    public boolean insertTerapiaDiabete(int idPaziente, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<Farmaco> farmaci) {
        boolean success = false;
        String insertTerapiaDiabeteSql = "insert into TerapiaDiabete (id_paziente_connesso, id_medico_ultima_modifica, data_inizio, data_fine) value (?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertTerapiaDiabeteStmt = conn.prepareStatement(insertTerapiaDiabeteSql, Statement.RETURN_GENERATED_KEYS);

            insertTerapiaDiabeteStmt.setInt(1, idPaziente);
            insertTerapiaDiabeteStmt.setInt(2, idMedicoUltimaModifica);
            insertTerapiaDiabeteStmt.setDate(3, dataInizio);
            insertTerapiaDiabeteStmt.setDate(4, dataFine);

            if (insertTerapiaDiabeteStmt.executeUpdate() != 0) {
                conn.commit();

                ResultSet rs = insertTerapiaDiabeteStmt.getGeneratedKeys();
                if (rs.next()) {
                    if (accessoFarmaciTerapia.insertFarmaciTerapia(rs.getInt(1), farmaci)) {
                        success = true;
                    } else {
                        conn.rollback();
                        System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile inserire i farmaci selezionati per la terapia");
                    }
                }


            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile inserire la terapia diabete nel database");
            }

            insertTerapiaDiabeteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean insertTerapiaConcomitante(int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<Farmaco> farmaci) {
        boolean success = false;
        String insertTerapiaConcomitanteSql = "insert into TerapiaConcomitante (id_paziente_terapia_concomitante, id_patologia_comorbidita, id_medico_ultima_modifica, id_farmaco_terapia, data_inizio, data_fine) value (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement insertTerapiaConcomitanteStmt = conn.prepareStatement(insertTerapiaConcomitanteSql, Statement.RETURN_GENERATED_KEYS);
            insertTerapiaConcomitanteStmt.setInt(1, idPaziente);
            insertTerapiaConcomitanteStmt.setInt(2, idPatologiaConcomitante);
            insertTerapiaConcomitanteStmt.setInt(3, idMedicoUltimaModifica);
            insertTerapiaConcomitanteStmt.setDate(4, dataInizio);
            insertTerapiaConcomitanteStmt.setDate(5, dataFine);

            if (insertTerapiaConcomitanteStmt.executeUpdate() != 0) {
                conn.commit();

                ResultSet rs = insertTerapiaConcomitanteStmt.getGeneratedKeys();
                if (rs.next()) {
                    if (accessoFarmaciTerapia.insertFarmaciTerapia(rs.getInt(1), farmaci)) {
                        success = true;
                    } else {
                        conn.rollback();
                        System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTI]: Impossibile inserire i farmaci selezionati per la terapia");
                    }
                }

            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTI]: Impossibile inserire la terapia concomitante nel database");
            }

            insertTerapiaConcomitanteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTI]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updateTerapiaDiabete(TerapiaDiabete terapia) {
        boolean success = false;
        String updateTerapiaDiabeteSql = "update TerapiaDiabete set id_medico_ultima_modifica = ?, data_fine = ? where id_terapia_diabete = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updateTerapiaDiabeteStmt = conn.prepareStatement(updateTerapiaDiabeteSql);
            updateTerapiaDiabeteStmt.setInt(1, terapia.getIdMedicoUltimaModifica());
            updateTerapiaDiabeteStmt.setDate(2, terapia.getDataFine());

            if (updateTerapiaDiabeteStmt.executeUpdate() != 0) {
                conn.commit();

                List<TerapiaDiabete> terapieDiabeteAttive = getTerapieDiabetePaziente(terapia.getIdPaziente());
                TerapiaDiabete terapiaNonModificata = null;
                for (TerapiaDiabete terapiaDiabete : terapieDiabeteAttive) {
                    if (terapiaDiabete.getIdTerapiaDiabete() == terapia.getIdTerapiaDiabete()) {
                        terapiaNonModificata = terapiaDiabete;
                    }
                }

                // Verifica la presenza di nuovi farmaci aggiornati nella terapia che ancora non si trovano nel DB
                List<Farmaco> farmaciDiabeteDaAggiungere = new ArrayList<>();
                for (Farmaco farmaco : terapia.getListaFarmaciTerapia()) {
                    if (!terapiaNonModificata.getListaFarmaciTerapia().contains(farmaco)) {
                        farmaciDiabeteDaAggiungere.add(farmaco);
                    }
                }
                if (!accessoFarmaciTerapia.insertFarmaciTerapia(terapia.getIdTerapiaDiabete(), farmaciDiabeteDaAggiungere)) {
                    throw new SQLException("Impossibile aggiornare i nuovi farmaci inseriti per questa terapia");
                }

                // Verifica la presenza di farmaci rimossi dalla terapia che si trovano ancora nel DB
                List<Farmaco> farmaciDiabeteDaRimuovere = new ArrayList<>();
                for (Farmaco farmaco : terapiaNonModificata.getListaFarmaciTerapia()) {
                    if (!terapia.getListaFarmaciTerapia().contains(farmaco)) {
                        farmaciDiabeteDaRimuovere.add(farmaco);
                    }
                }
                for (Farmaco farmacoDaRimuovere : farmaciDiabeteDaRimuovere) {
                    if (!accessoFarmaciTerapia.deleteFarmaciTerapia(terapia.getIdTerapiaDiabete(), farmacoDaRimuovere.getIdFarmaco())) {
                        throw new SQLException("Impossibile rimuovere uno o più farmaci non più previsti dalla terapia");
                    }
                }

                success = true;

            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE TERAPIA DIABETE]: Impossibile aggiornare la terapia diabete nel database");
            }

            updateTerapiaDiabeteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updateTerapiaConcomitante(TerapiaConcomitante terapia) {
        boolean success = false;
        String updateTerapiaConcomitanteSql = "update TerapiaConcomitante set id_patologia_comorbidita = ?,  id_medico_ultima_modifica = ?, data_fine = ? where id_terapia_concomitante = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updateTerapiaConcomitanteStmt = conn.prepareStatement(updateTerapiaConcomitanteSql);
            updateTerapiaConcomitanteStmt.setInt(1, terapia.getIdTerapiaConcomitante());
            updateTerapiaConcomitanteStmt.setInt(2, terapia.getIdMedicoUltimaModifica());
            updateTerapiaConcomitanteStmt.setDate(3, terapia.getDataFine());


            if (updateTerapiaConcomitanteStmt.executeUpdate() != 0) {
                conn.commit();

                List<TerapiaConcomitante> terapieConcomitantiAttive = getTerapieConcomitantiPaziente(terapia.getIdPaziente());
                TerapiaConcomitante terapiaNonModificata = null;
                for (TerapiaConcomitante terapiaConcomitante : terapieConcomitantiAttive) {
                    if (terapiaConcomitante.getIdTerapiaConcomitante() == terapia.getIdTerapiaConcomitante()) {
                        terapiaNonModificata = terapiaConcomitante;
                    }
                }

                // Verifica la presenza di nuovi farmaci aggiornati nella terapia che ancora non si trovano nel DB
                List<Farmaco> farmaciConcomitanteDaAggiungere = new ArrayList<>();
                for (Farmaco farmaco : terapia.getListaFarmaciTerapia()) {
                    if (!terapiaNonModificata.getListaFarmaciTerapia().contains(farmaco)) {
                        farmaciConcomitanteDaAggiungere.add(farmaco);
                    }
                }
                if (!accessoFarmaciTerapia.insertFarmaciTerapia(terapia.getIdTerapiaConcomitante(), farmaciConcomitanteDaAggiungere)) {
                    throw new SQLException("Impossibile aggiornare i nuovi farmaci inseriti per questa terapia");
                }

                // Verifica la presenza di farmaci rimossi dalla terapia che si trovano ancora nel DB
                List<Farmaco> farmaciConcomitanteDaRimuovere = new ArrayList<>();
                for (Farmaco farmaco : terapiaNonModificata.getListaFarmaciTerapia()) {
                    if (!terapia.getListaFarmaciTerapia().contains(farmaco)) {
                        farmaciConcomitanteDaRimuovere.add(farmaco);
                    }
                }
                for (Farmaco farmacoDaRimuovere : farmaciConcomitanteDaRimuovere) {
                    if (!accessoFarmaciTerapia.deleteFarmaciTerapia(terapia.getIdTerapiaConcomitante(), farmacoDaRimuovere.getIdFarmaco())) {
                        throw new SQLException("Impossibile rimuovere uno o più farmaci non più previsti dalla terapia");
                    }
                }

                success = true;

            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE TERAPIA CONCOMITANTI]: Impossibile aggiornare la terapia concomitante nel database");
            }

            updateTerapiaConcomitanteStmt.close();
            conn.setAutoCommit(true);
            conn.close();

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE TERAPIA CONCOMITANTI]: " + e.getMessage());
        }

        return success;
    }
}
