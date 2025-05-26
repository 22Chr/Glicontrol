package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessoTerapieImpl implements AccessoTerapie {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    private final AccessoPonteFarmaciTerapia accessoPonteFarmaciTerapia = new AccessoPonteFarmaciTerapiaImpl();
    private final AccessoIndicazioniFarmaciTerapia accessoIndicazioniFarmaciTerapia = new AccessoIndicazioniFarmaciTerapiaImpl();

    @Override
    public List<TerapiaDiabete> getTerapieDiabetePaziente(int idPaziente) {
        List<TerapiaDiabete> terapiaDiabete = new ArrayList<>();
        String recuperaTerapiaDiabeteSql = "select * from TerapiaDiabete where id_paziente_connesso = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement recuperaTerapiaDiabeteStmt = conn.prepareStatement(recuperaTerapiaDiabeteSql)) {

            recuperaTerapiaDiabeteStmt.setInt(1, idPaziente);

            try (ResultSet rs = recuperaTerapiaDiabeteStmt.executeQuery()) {
                while (rs.next()) {
                    int idTerapiaDiabete = rs.getInt("id_terapia_diabete");
                    TerapiaDiabete t = new TerapiaDiabete (rs.getInt("id_paziente_connesso"),
                            rs.getInt("id_medico_ultima_modifica"),
                            rs.getDate("data_inizio"),
                            rs.getDate("data_fine"),
                            getListaFarmaciPerTerapia(
                                    accessoPonteFarmaciTerapia.getListaFarmaciPerTerapia(idTerapiaDiabete),
                                    idTerapiaDiabete
                            ));
                    t.setIdTerapiaDiabete(idTerapiaDiabete);
                    terapiaDiabete.add(t);
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TERAPIA DIABETE]: " + e.getMessage());
        }

        return terapiaDiabete;
    }

    @Override
    public List<TerapiaConcomitante> getTerapieConcomitantiPaziente(int idPaziente) {
        List<TerapiaConcomitante> terapieConcomitanti = new ArrayList<>();
        String recuperaTerapieConcomitantiSql = "select * from TerapiaConcomitante where id_paziente_terapia_concomitante = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement recuperaTerapieConcomitantiStmt = conn.prepareStatement(recuperaTerapieConcomitantiSql)) {

            recuperaTerapieConcomitantiStmt.setInt(1, idPaziente);

            try (ResultSet rs = recuperaTerapieConcomitantiStmt.executeQuery()) {
                while (rs.next()) {
                    int idTerapiaConcomitante = rs.getInt("id_terapia_concomitante");
                    TerapiaConcomitante t = new TerapiaConcomitante (rs.getInt("id_paziente_terapia_concomitante"),
                            rs.getInt("id_patologia_comorbidita"),
                            rs.getInt("id_medico_ultima_modifica"),
                            rs.getDate("data_inizio"),
                            rs.getDate("data_fine"),
                            getListaFarmaciPerTerapia(
                                    accessoPonteFarmaciTerapia.getListaFarmaciPerTerapia(idTerapiaConcomitante),
                                    idTerapiaConcomitante
                            ));
                    t.setIdTerapiaConcomitante(idTerapiaConcomitante);
                    terapieConcomitanti.add(t);
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO TERAPIE CONCOMITANTI]: " + e.getMessage());
        }

        return terapieConcomitanti;
    }

    @Override
    public boolean insertTerapiaDiabete(int idPaziente, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaciTerapia) {
        boolean success = false;
        String insertTerapiaDiabeteSql = "insert into TerapiaDiabete (id_terapia_diabete, id_paziente_connesso, id_medico_ultima_modifica, data_inizio, data_fine) value (?, ?, ?, ?, ?)";
        List<Farmaco> farmaciDiabete = ottieniListaFarmaciDaFarmacoTerapia(farmaciTerapia);
        List<IndicazioniFarmaciTerapia> indicazioni = ottieniListaIndicazioniFarmaciDaFarmacoTerapia(farmaciTerapia);

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement insertTerapiaDiabeteStmt = conn.prepareStatement(insertTerapiaDiabeteSql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            String generaTerapia = "insert into Terapia () values ()";
            try (PreparedStatement generaTerapiaStmt = conn.prepareStatement(generaTerapia, Statement.RETURN_GENERATED_KEYS)) {
                generaTerapiaStmt.executeUpdate();
                try (ResultSet rs = generaTerapiaStmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile ottenere l'id generato per la nuova terapia");
                        conn.rollback();
                        return false;
                    }
                    int idTerapia = rs.getInt(1);
                    insertTerapiaDiabeteStmt.setInt(1, idTerapia);
                }
            }

            insertTerapiaDiabeteStmt.setInt(2, idPaziente);
            insertTerapiaDiabeteStmt.setInt(3, idMedicoUltimaModifica);
            insertTerapiaDiabeteStmt.setDate(4, dataInizio);
            insertTerapiaDiabeteStmt.setDate(5, dataFine);

            if (insertTerapiaDiabeteStmt.executeUpdate() == 0) {
                System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile inserire la terapia diabete nel database");
                conn.rollback();
                return false;
            }

            try (ResultSet rs = insertTerapiaDiabeteStmt.getGeneratedKeys()) {
                if (!rs.next()) {
                    System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile ottenere l'id generato");
                    conn.rollback();
                    return false;
                }

                int idTerapia = rs.getInt(1);

                if (!accessoPonteFarmaciTerapia.insertFarmaciTerapia(conn, idTerapia, farmaciDiabete)) {
                    System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile inserire i farmaci selezionati per la terapia");
                    conn.rollback();
                    return false;
                }

                if (indicazioni.size() != farmaciDiabete.size()) {
                    System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Dimensione indicazioni e farmaci discordante");
                    conn.rollback();
                    return false;
                }

                for (int i = 0; i < indicazioni.size(); i++) {
                    if (!accessoIndicazioniFarmaciTerapia.insertIndicazioniFarmaci(
                            conn,
                            idTerapia,
                            farmaciDiabete.get(i).getIdFarmaco(),
                            indicazioni.get(i).getDosaggio(),
                            indicazioni.get(i).getFrequenzaAssunzione(),
                            indicazioni.get(i).getOrariAssunzione())) {
                        System.out.println("[ERRORE INSERT TERAPIA DIABETE]: Impossibile inserire indicazioni farmaco id " + farmaciDiabete.get(i).getIdFarmaco());
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                success = true;
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean insertTerapiaConcomitante(int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaciTerapia) {
        boolean success = false;
        String insertTerapiaConcomitanteSql = "insert into TerapiaConcomitante (id_terapia_concomitante, id_paziente_terapia_concomitante, id_patologia_comorbidita, id_medico_ultima_modifica, data_inizio, data_fine) values (?, ?, ?, ?, ?, ?)";
        List<Farmaco> farmaciConcomitante = ottieniListaFarmaciDaFarmacoTerapia(farmaciTerapia);
        List<IndicazioniFarmaciTerapia> indicazioni = ottieniListaIndicazioniFarmaciDaFarmacoTerapia(farmaciTerapia);

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement insertTerapiaConcomitanteStmt = conn.prepareStatement(insertTerapiaConcomitanteSql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);
            String generaTerapia = "insert into Terapia () values ()";
            try (PreparedStatement generaTerapiaStmt = conn.prepareStatement(generaTerapia, Statement.RETURN_GENERATED_KEYS)) {
                generaTerapiaStmt.executeUpdate();
                try (ResultSet rs = generaTerapiaStmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTE]: Impossibile ottenere l'id generato per la nuova terapia");
                        conn.rollback();
                        return false;
                    }
                    int idTerapiaConcomitante = rs.getInt(1);
                    insertTerapiaConcomitanteStmt.setInt(1, idTerapiaConcomitante);
                }
            }

            insertTerapiaConcomitanteStmt.setInt(2, idPaziente);
            insertTerapiaConcomitanteStmt.setInt(3, idPatologiaConcomitante);
            insertTerapiaConcomitanteStmt.setInt(4, idMedicoUltimaModifica);
            insertTerapiaConcomitanteStmt.setDate(5, dataInizio);
            insertTerapiaConcomitanteStmt.setDate(6, dataFine);

            if (insertTerapiaConcomitanteStmt.executeUpdate() == 0) {
                System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTE]: Impossibile inserire la terapia concomitante nel database");
                conn.rollback();
                return false;
            }

            try (ResultSet rs = insertTerapiaConcomitanteStmt.getGeneratedKeys()) {
                if (!rs.next()) {
                    System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTE]: Impossibile ottenere l'id generato");
                    conn.rollback();
                    return false;
                }

                int idTerapiaConcomitante = rs.getInt(1);

                if (!accessoPonteFarmaciTerapia.insertFarmaciTerapia(conn, idTerapiaConcomitante, farmaciConcomitante)) {
                    System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTE]: Impossibile inserire i farmaci selezionati per la terapia");
                    conn.rollback();
                    return false;
                }

                if (indicazioni.size() != farmaciConcomitante.size()) {
                    System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTE]: Dimensione indicazioni e farmaci discordante");
                    conn.rollback();
                    return false;
                }

                for (int i = 0; i < indicazioni.size(); i++) {
                    if (!accessoIndicazioniFarmaciTerapia.insertIndicazioniFarmaci(
                            conn,
                            idTerapiaConcomitante,
                            farmaciConcomitante.get(i).getIdFarmaco(),
                            indicazioni.get(i).getDosaggio(),
                            indicazioni.get(i).getFrequenzaAssunzione(),
                            indicazioni.get(i).getOrariAssunzione())) {
                        System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTE]: Impossibile inserire indicazioni farmaco id " + farmaciConcomitante.get(i).getIdFarmaco());
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                success = true;
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE INSERT TERAPIA CONCOMITANTI]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updateTerapiaDiabete(TerapiaDiabete terapia) {
        boolean success = false;
        String updateTerapiaDiabeteSql = "update TerapiaDiabete set id_medico_ultima_modifica = ?, data_fine = ? where id_terapia_diabete = ?";

        // Recupera la terapia non modificata attualmente nel DB
        List<TerapiaDiabete> terapieDiabeteAttive = getTerapieDiabetePaziente(terapia.getIdPaziente());
        TerapiaDiabete terapiaNonModificata = null;
        for (TerapiaDiabete t : terapieDiabeteAttive) {
            if (t.getIdTerapiaDiabete() == terapia.getIdTerapiaDiabete()) {
                terapiaNonModificata = t;
                break;
            }
        }
        if (terapiaNonModificata == null) {
            System.out.println("[ERRORE UPDATE TERAPIA DIABETE]: Terapia non trovata nel DB");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement updateTerapiaDiabeteStmt = conn.prepareStatement(updateTerapiaDiabeteSql)) {

            conn.setAutoCommit(false);

            updateTerapiaDiabeteStmt.setInt(1, terapia.getIdMedicoUltimaModifica());
            updateTerapiaDiabeteStmt.setDate(2, terapia.getDataFine());
            updateTerapiaDiabeteStmt.setInt(3, terapia.getIdTerapiaDiabete());

            if (updateTerapiaDiabeteStmt.executeUpdate() == 0) {
                System.out.println("[ERRORE UPDATE TERAPIA DIABETE]: Impossibile aggiornare la terapia diabete nel database");
                conn.rollback();
                return false;
            }

            // Farmaci e indicazioni attuali nel DB (non modificati)
            List<Farmaco> farmaciTerapiaNonModificati = new ArrayList<>();
            List<IndicazioniFarmaciTerapia> indicazioniTerapiaNonModificata = new ArrayList<>();
            for (FarmacoTerapia ft : terapiaNonModificata.getListaFarmaciTerapia()) {
                farmaciTerapiaNonModificati.add(ft.getFarmaco());
                indicazioniTerapiaNonModificata.add(ft.getIndicazioni());
            }
            // Mappa usata per l'aggiornamento delle indicazioni nel DB
            Map<Integer, IndicazioniFarmaciTerapia> mappaIndicazioniDB = indicazioniTerapiaNonModificata.stream()
                    .collect(Collectors.toMap(
                            IndicazioniFarmaciTerapia::getIdIndicazioniFarmaci,
                            i -> i
                    ));

            // Farmaci aggiornati (locali)
            List<Farmaco> listaFarmaciDiabeteLocale = new ArrayList<>();
            // Indicazioni aggiornate (locali)
            List<IndicazioniFarmaciTerapia> indicazioniDiabeteLocale = new ArrayList<>();
            for (FarmacoTerapia ft : terapia.getListaFarmaciTerapia()) {
                listaFarmaciDiabeteLocale.add(ft.getFarmaco());
                indicazioniDiabeteLocale.add(ft.getIndicazioni());
            }

            // Nuovi farmaci da aggiungere
            List<Farmaco> farmaciDiabeteDaAggiungere = new ArrayList<>();
            for (Farmaco farmaco : listaFarmaciDiabeteLocale) {
                if (!farmaciTerapiaNonModificati.contains(farmaco)) {
                    farmaciDiabeteDaAggiungere.add(farmaco);
                }
            }
            if (!farmaciDiabeteDaAggiungere.isEmpty()) {
                if (!accessoPonteFarmaciTerapia.insertFarmaciTerapia(conn, terapia.getIdTerapiaDiabete(), farmaciDiabeteDaAggiungere)) {
                    conn.rollback();
                    throw new SQLException("Impossibile aggiornare i nuovi farmaci inseriti per questa terapia");
                }
            }

            // Nuove indicazioni da aggiungere
            List<IndicazioniFarmaciTerapia> indicazioniDiabeteDaAggiungere = new ArrayList<>();
            for (IndicazioniFarmaciTerapia indicazioni : indicazioniDiabeteLocale) {
                if (!indicazioniTerapiaNonModificata.contains(indicazioni)) {
                    indicazioniDiabeteDaAggiungere.add(indicazioni);
                }
            }
            if (!indicazioniDiabeteDaAggiungere.isEmpty()) {
                for (IndicazioniFarmaciTerapia indicazioniFarmaciTerapia : indicazioniDiabeteDaAggiungere) {
                    if (!accessoIndicazioniFarmaciTerapia.insertIndicazioniFarmaci(
                            conn,
                            terapia.getIdTerapiaDiabete(),
                            indicazioniFarmaciTerapia.getIdFarmacoAnnesso(),
                            indicazioniFarmaciTerapia.getDosaggio(),
                            indicazioniFarmaciTerapia.getFrequenzaAssunzione(),
                            indicazioniFarmaciTerapia.getOrariAssunzione())) {
                        conn.rollback();
                        throw new SQLException("Impossibile aggiornare le nuove indicazioni inserite per questa terapia");
                    }
                }
            }

            // Farmaci da rimuovere
            List<Farmaco> farmaciDiabeteDaRimuovere = new ArrayList<>();
            for (Farmaco farmaco : farmaciTerapiaNonModificati) {
                if (!listaFarmaciDiabeteLocale.contains(farmaco)) {
                    farmaciDiabeteDaRimuovere.add(farmaco);
                }
            }
            for (Farmaco farmacoDaRimuovere : farmaciDiabeteDaRimuovere) {
                if (!accessoPonteFarmaciTerapia.deleteFarmaciTerapia(conn, terapia.getIdTerapiaDiabete(), farmacoDaRimuovere.getIdFarmaco())) {
                    conn.rollback();
                    throw new SQLException("Impossibile rimuovere uno o più farmaci non più previsti dalla terapia");
                }
            }

            // Indicazioni da rimuovere
            List<IndicazioniFarmaciTerapia> indicazioniDiabeteDaRimuovere = new ArrayList<>();
            for (IndicazioniFarmaciTerapia indicazioni : indicazioniTerapiaNonModificata) {
                if (!indicazioniDiabeteLocale.contains(indicazioni)) {
                    indicazioniDiabeteDaRimuovere.add(indicazioni);
                }
            }
            for (IndicazioniFarmaciTerapia indicazioniFarmaciTerapia : indicazioniDiabeteDaRimuovere) {
                if (!accessoIndicazioniFarmaciTerapia.deleteIndicazioniFarmaci(
                        conn,
                        indicazioniFarmaciTerapia.getIdIndicazioniFarmaci())) {
                    conn.rollback();
                    throw new SQLException("Impossibile rimuovere le indicazioni non più previste dalla terapia");
                }
            }

            // Verifica aggiornamento su indicazioni che si trovano già nel DB ma sono state aggiornate
            for (IndicazioniFarmaciTerapia indicazioniLocali : indicazioniDiabeteLocale) {
                IndicazioniFarmaciTerapia indicazioniDB = mappaIndicazioniDB.get(indicazioniLocali.getIdIndicazioniFarmaci());
                if (indicazioniDB != null && !indicazioniLocali.equals(indicazioniDB)) {
                    if (!accessoIndicazioniFarmaciTerapia.updateIndicazioniFarmaci(conn, indicazioniLocali)) {
                        conn.rollback();
                        throw new SQLException("Impossibile aggiornare una delle indicazioni della terapia");
                    }
                }
            }

            conn.commit();
            success = true;

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE TERAPIA DIABETE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updateTerapiaConcomitante(TerapiaConcomitante terapia) {
        boolean success = false;
        String updateTerapiaConcomitanteSql = "update TerapiaConcomitante set id_patologia_comorbidita = ?,  id_medico_ultima_modifica = ?, data_fine = ? where id_terapia_concomitante = ?";

        // Recupera la terapia non modificata attualmente nel DB
        List<TerapiaConcomitante> terapieConcomitantiAttive = getTerapieConcomitantiPaziente(terapia.getIdPaziente());
        TerapiaConcomitante terapiaNonModificata = null;
        for (TerapiaConcomitante t : terapieConcomitantiAttive) {
            if (t.getIdTerapiaConcomitante() == terapia.getIdTerapiaConcomitante()) {
                terapiaNonModificata = t;
                break;
            }
        }
        if (terapiaNonModificata == null) {
            System.out.println("[ERRORE UPDATE TERAPIA CONCOMITANTE]: Terapia non trovata nel DB");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement updateTerapiaConcomitanteStmt = conn.prepareStatement(updateTerapiaConcomitanteSql)) {

            conn.setAutoCommit(false);

            updateTerapiaConcomitanteStmt.setInt(1, terapia.getIdTerapiaConcomitante());
            updateTerapiaConcomitanteStmt.setInt(2, terapia.getIdMedicoUltimaModifica());
            updateTerapiaConcomitanteStmt.setDate(3, terapia.getDataFine());
            updateTerapiaConcomitanteStmt.setInt(4, terapia.getIdTerapiaConcomitante());

            if (updateTerapiaConcomitanteStmt.executeUpdate() == 0) {
                System.out.println("[ERRORE UPDATE TERAPIA CONCOMITANTE]: Impossibile aggiornare la terapia concomitante nel database");
                conn.rollback();
                return false;
            }

            // Farmaci e indicazioni attuali nel DB (non modificati)
            List<Farmaco> farmaciTerapiaNonModificati = new ArrayList<>();
            List<IndicazioniFarmaciTerapia> indicazioniTerapiaNonModificata = new ArrayList<>();
            for (FarmacoTerapia ft : terapiaNonModificata.getListaFarmaciTerapia()) {
                farmaciTerapiaNonModificati.add(ft.getFarmaco());
                indicazioniTerapiaNonModificata.add(ft.getIndicazioni());
            }
            // Mappa usata per l'aggiornamento delle indicazioni nel DB
            Map<Integer, IndicazioniFarmaciTerapia> mappaIndicazioniDB = indicazioniTerapiaNonModificata.stream()
                    .collect(Collectors.toMap(
                            IndicazioniFarmaciTerapia::getIdIndicazioniFarmaci,
                            i -> i
                    ));


            // Farmaci aggiornati (locali)
            List<Farmaco> listaFarmaciConcomitanteLocale = new ArrayList<>();
            // Indicazioni aggiornate (locali)
            List<IndicazioniFarmaciTerapia> indicazioniConcomitanteLocale = new ArrayList<>();
            for (FarmacoTerapia ft : terapia.getListaFarmaciTerapia()) {
                listaFarmaciConcomitanteLocale.add(ft.getFarmaco());
                indicazioniConcomitanteLocale.add(ft.getIndicazioni());
            }

            // Nuovi farmaci da aggiungere
            List<Farmaco> farmaciConcomitanteDaAggiungere = new ArrayList<>();
            for (Farmaco farmaco : listaFarmaciConcomitanteLocale) {
                if (!farmaciTerapiaNonModificati.contains(farmaco)) {
                    farmaciConcomitanteDaAggiungere.add(farmaco);
                }
            }
            if (!farmaciConcomitanteDaAggiungere.isEmpty()) {
                if (!accessoPonteFarmaciTerapia.insertFarmaciTerapia(conn, terapia.getIdTerapiaConcomitante(), farmaciConcomitanteDaAggiungere)) {
                    conn.rollback();
                    throw new SQLException("Impossibile aggiornare i nuovi farmaci inseriti per questa terapia");
                }
            }

            // Nuove indicazioni da aggiungere
            List<IndicazioniFarmaciTerapia> indicazioniConcomitanteDaAggiungere = new ArrayList<>();
            for (IndicazioniFarmaciTerapia indicazioni : indicazioniConcomitanteLocale) {
                if (!indicazioniTerapiaNonModificata.contains(indicazioni)) {
                    indicazioniConcomitanteDaAggiungere.add(indicazioni);
                }
            }
            if (!indicazioniConcomitanteDaAggiungere.isEmpty()) {
                for (IndicazioniFarmaciTerapia indicazioniFarmaciTerapia : indicazioniConcomitanteDaAggiungere) {
                    if (!accessoIndicazioniFarmaciTerapia.insertIndicazioniFarmaci(
                            conn,
                            terapia.getIdTerapiaConcomitante(),
                            indicazioniFarmaciTerapia.getIdFarmacoAnnesso(),
                            indicazioniFarmaciTerapia.getDosaggio(),
                            indicazioniFarmaciTerapia.getFrequenzaAssunzione(),
                            indicazioniFarmaciTerapia.getOrariAssunzione()
                    )) {
                        conn.rollback();
                        throw new SQLException("Impossibile aggiornare le nuove indicazioni inserite per questa terapia");
                    }
                }
            }

            // Farmaci da rimuovere
            List<Farmaco> farmaciConcomitanteDaRimuovere = new ArrayList<>();
            for (Farmaco farmaco : farmaciTerapiaNonModificati) {
                if (!listaFarmaciConcomitanteLocale.contains(farmaco)) {
                    farmaciConcomitanteDaRimuovere.add(farmaco);
                }
            }
            for (Farmaco farmacoDaRimuovere : farmaciConcomitanteDaRimuovere) {
                if (!accessoPonteFarmaciTerapia.deleteFarmaciTerapia(conn, terapia.getIdTerapiaConcomitante(), farmacoDaRimuovere.getIdFarmaco())) {
                    conn.rollback();
                    throw new SQLException("Impossibile rimuovere uno o più farmaci non più previsti dalla terapia");
                }
            }

            // Indicazioni da rimuovere
            List<IndicazioniFarmaciTerapia> indicazioniConcomitanteDaRimuovere = new ArrayList<>();
            for (IndicazioniFarmaciTerapia indicazioni : indicazioniTerapiaNonModificata) {
                if (!indicazioniConcomitanteLocale.contains(indicazioni)) {
                    indicazioniConcomitanteDaRimuovere.add(indicazioni);
                }
            }
            for (IndicazioniFarmaciTerapia indicazioniFarmaciTerapia : indicazioniConcomitanteDaRimuovere) {
                if (!accessoIndicazioniFarmaciTerapia.deleteIndicazioniFarmaci(
                        conn,
                        indicazioniFarmaciTerapia.getIdIndicazioniFarmaci())) {
                    conn.rollback();
                }
            }

            // Verifica aggiornamento su indicazioni che si trovano già nel DB ma sono state aggiornate
            for (IndicazioniFarmaciTerapia indicazioniLocali : indicazioniConcomitanteLocale) {
                IndicazioniFarmaciTerapia indicazioniDB = mappaIndicazioniDB.get(indicazioniLocali.getIdIndicazioniFarmaci());
                if (indicazioniDB != null && !indicazioniLocali.equals(indicazioniDB)) {
                    if (!accessoIndicazioniFarmaciTerapia.updateIndicazioniFarmaci(conn, indicazioniLocali)) {
                        conn.rollback();
                        throw new SQLException("Impossibile aggiornare una delle indicazioni della terapia");
                    }
                }
            }

            conn.commit();
            success = true;

        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE TERAPIA CONCOMITANTI]: " + e.getMessage());
        }

        return success;
    }


    // Metodi di utilità supplementari
    private List<FarmacoTerapia> getListaFarmaciPerTerapia(List<Farmaco> farmaci, int idTerapia) {
        List<FarmacoTerapia> listaFarmaci = new ArrayList<>();
        List<IndicazioniFarmaciTerapia> indicazioni = accessoIndicazioniFarmaciTerapia.getListaIndicazioniFarmaci(idTerapia);

        if (farmaci == null || indicazioni == null) {
            return null;
        }

        for (int i = 0; i < farmaci.size(); i++) {
            listaFarmaci.add(new FarmacoTerapia(farmaci.get(i), indicazioni.get((i))));
        }

        return listaFarmaci;
    }

    private List<Farmaco> ottieniListaFarmaciDaFarmacoTerapia(List<FarmacoTerapia> farmaciTerapia) {
        List<Farmaco> listaFarmaci = new ArrayList<>();
        for (FarmacoTerapia farmacoTerapia : farmaciTerapia) {
            listaFarmaci.add(farmacoTerapia.getFarmaco());
        }

        return listaFarmaci;
    }

    private List<IndicazioniFarmaciTerapia> ottieniListaIndicazioniFarmaciDaFarmacoTerapia(List<FarmacoTerapia> farmaciTerapia) {
        List<IndicazioniFarmaciTerapia> listaIndicazioni = new ArrayList<>();
        for (FarmacoTerapia farmacoTerapia : farmaciTerapia) {
            listaIndicazioni.add(farmacoTerapia.getIndicazioni());
        }

        return listaIndicazioni;
    }
}
