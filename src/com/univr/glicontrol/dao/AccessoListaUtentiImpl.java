package com.univr.glicontrol.dao;

import com.univr.glicontrol.Main;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoListaUtentiImpl implements AccessoListaUtenti {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";


    @Override
    public List<Medico> recuperaTuttiIMedici() {
        String sql = "select * from Medico m join Utente u on m.id_medico = u.id";
        List<Medico> Medici = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement stmt = conn.prepareStatement(sql);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medico med = new Medico(
                            rs.getInt("id_medico"),
                            rs.getString("codice_fiscale"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("ruolo"),
                            rs.getString("email")
                    );
                    Medici.add(med);
                }
            }


        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO MEDICI]: " + e.getMessage());
        }

        return Medici;
    }

    @Override
    public List<Paziente> recuperaTuttiIPazienti() {
        String sql = "select * from Paziente p join Utente u on p.id_paziente = u.id";
        List<Paziente> Pazienti = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement stmt = conn.prepareStatement(sql);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paziente paz = new Paziente(
                            rs.getInt("id_paziente"),
                            rs.getString("codice_fiscale"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("ruolo"),
                            rs.getInt("medico_riferimento"),
                            rs.getDate("data_nascita"),
                            rs.getString("sesso"),
                            rs.getString("email"),
                            rs.getString("allergie"),
                            rs.getDouble("peso")
                    );
                    Pazienti.add(paz);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO PAZIENTI]: " + e.getMessage());
        }

        return Pazienti;
    }

    @Override
    public boolean updateListaMedici() {
        return false;
    }

    @Override
    public boolean updateListaPazienti() {
        return false;
    }

    @Override
    public boolean insertNuovoMedico() {
        return false;
    }

    @Override
    public boolean insertNuovoPaziente() {
        return false;
    }

    @Override
    public boolean deleteMedico() {
        return false;
    }

    @Override
    public boolean deletePaziente() {
        return false;
    }
}
