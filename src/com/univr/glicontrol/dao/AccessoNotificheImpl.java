package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Notifica;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.TipologiaNotifica;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AccessoNotificheImpl implements AccessoNotifiche {

    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";

    @Override
    public List<Notifica> getNotificheNonVisualizzate(Paziente paziente) {

        ListaPazienti listaPazienti = new ListaPazienti();

        List<Notifica> notificheNonVisualizzate = new ArrayList<>();
        String getNotificheNonVisualizzateSql = "select * from Notifiche where visualizzato = false and paziente_associato = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement recuperaNotificheNonVisualizzateStmt = conn.prepareStatement(getNotificheNonVisualizzateSql);
            recuperaNotificheNonVisualizzateStmt.setInt(1, paziente.getIdUtente());

            try (ResultSet rs = recuperaNotificheNonVisualizzateStmt.executeQuery()) {
                while (rs.next()) {
                    int idNotifiche = rs.getInt("id_notifica");
                    Notifica notifica = new Notifica(
                            rs.getString("titolo"),
                            rs.getString("messaggio"),
                            listaPazienti.getPazientePerId(rs.getInt("paziente_associato")),
                            rs.getTimestamp("data_notifica").toLocalDateTime(),
                            TipologiaNotifica.valueOf(rs.getString("tipo")),
                            rs.getBoolean("visualizzato")
                    );

                    notifica.setIdNotifica(idNotifiche);
                    notificheNonVisualizzate.add(notifica);
                }
            }

            recuperaNotificheNonVisualizzateStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE RECUPERO NOTIFICHE NON VISUALIZZATE]: " + e.getMessage());
        }

        return notificheNonVisualizzate;
    }

    @Override
    public boolean insertNuovaNotifica(Notifica nuovaNotifica) {
        String insertNuovaNotificaSql = "insert into Notifiche (titolo, messaggio, paziente_associato, data_notifica, tipo, visualizzato) values (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement insertNuovaNotificaStmt = conn.prepareStatement(insertNuovaNotificaSql);
            insertNuovaNotificaStmt.setString(1, nuovaNotifica.getTitolo());
            insertNuovaNotificaStmt.setString(2, nuovaNotifica.getMessaggio());
            insertNuovaNotificaStmt.setInt(3, nuovaNotifica.getPazienteAssociato().getIdUtente());
            insertNuovaNotificaStmt.setTimestamp(4, Timestamp.valueOf(nuovaNotifica.getDataNotifica()));
            insertNuovaNotificaStmt.setString(5, nuovaNotifica.getTipoNotifica());
            insertNuovaNotificaStmt.setBoolean(6, nuovaNotifica.isVisualizzato());

            if (insertNuovaNotificaStmt.executeUpdate() != 0) {
                return true;
            } else {
                System.err.println("[ERRORE INSERT NUOVA NOTIFICA]: Impossibile inserire la nuova notifica nel DB");
            }

            insertNuovaNotificaStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE INSERT NUOVA NOTIFICA]: ]" + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateStatoNotifica(Notifica nuovaNotifica) {
        boolean success = false;
        String updateStatoNotificaSql = "update Notifiche set visualizzato = true where id_notifica = ?";

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement updateStatoNotificaStmt = conn.prepareStatement(updateStatoNotificaSql);
            updateStatoNotificaStmt.setInt(1, nuovaNotifica.getIdNotifica());

            if (updateStatoNotificaStmt.executeUpdate() != 0) {
                conn.commit();
                success = true;
            } else {
                System.err.println("[ERRORE UPDATE STATO NOTIFICA]: Impossibile aggiornare lo stato della notifica selezionata nel DB");
            }

            conn.setAutoCommit(true);
            updateStatoNotificaStmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("[ERRORE UPDATE STATO NOTIFICA]: " + e.getMessage());
        }

        return success;
    }

}
