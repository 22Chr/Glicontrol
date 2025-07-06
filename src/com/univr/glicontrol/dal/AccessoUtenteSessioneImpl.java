package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.Admin;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.sql.*;

public class AccessoUtenteSessioneImpl implements AccessoUtenteSessione {

    private final String pwd;

    public AccessoUtenteSessioneImpl(String pwd) {
        this.pwd = pwd;
    }

    private ResultSet recuperaUtente(String codiceFiscale, String ruolo) {
        String sql = """
            select 
                u.id AS id_utente, u.nome, u.cognome, u.codice_fiscale, u.password, u.ruolo,
                p.data_nascita, p.sesso, p.email AS email_paziente, p.allergie, p.medico_riferimento, p.altezza, p.peso, p.primo_accesso,
                m.email AS email_medico
            from Utente u
            left join Paziente p on u.id = p.id_paziente
            left join Medico m on u.id = m.id_medico
            where u.codice_fiscale = ? and u.password = ? and u.ruolo = ?
        """;

        String url = "jdbc:mysql://localhost:3306/glicontrol";

        try {
            Connection conn = DriverManager.getConnection(url, "root", "Sitecom12");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, codiceFiscale);
            stmt.setString(2, pwd);
            stmt.setString(3, ruolo);

            return stmt.executeQuery();

        } catch (SQLException e) {
            System.err.println("[ERRORE ACCESSO DB]: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean verificaUtente(String codiceFiscale, String ruolo) {

        try (ResultSet utente = recuperaUtente(codiceFiscale, ruolo)) {
            return utente!= null && utente.next();
        } catch (SQLException e) {
            System.err.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return false;
    }

    @Override
    public Admin getAdmin(String codiceFiscale) {
        try (ResultSet admin = recuperaUtente(codiceFiscale, "ADMIN")) {

            if (admin != null && admin.next()) {
                int id = admin.getInt("id_utente");
                String cf = admin.getString("codice_fiscale");
                String nome = admin.getString("nome");
                String cognome = admin.getString("cognome");
                String r = admin.getString("ruolo");
                String password = admin.getString("password");

                return new Admin(id, cf, nome, cognome, password, r);
            }
        } catch (SQLException e) {
            System.err.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Medico getMedico(String codiceFiscale) {
        try (ResultSet medico = recuperaUtente(codiceFiscale, "MEDICO")) {

            if (medico != null && medico.next()) {
                int id = medico.getInt("id_utente");
                String nome = medico.getString("nome");
                String cognome = medico.getString("cognome");
                String password = medico.getString("password");
                String cf = medico.getString("codice_fiscale");
                String r = medico.getString("ruolo");
                String emailMedico = medico.getString("email_medico");

                return new Medico(id, cf, nome, cognome, password, r, emailMedico);
            }

        } catch (SQLException e) {
            System.err.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Paziente getPaziente(String codiceFiscale) {
        try (ResultSet paziente = recuperaUtente(codiceFiscale, "PAZIENTE")) {

            if (paziente != null && paziente.next()) {
                int id = paziente.getInt("id_utente");
                String nome = paziente.getString("nome");
                String cognome = paziente.getString("cognome");
                String password = paziente.getString("password");
                String cf = paziente.getString("codice_fiscale");
                String r = paziente.getString("ruolo");
                int medicoRiferimento = paziente.getInt("medico_riferimento");
                Date dataNascita = paziente.getDate("data_nascita");
                String sesso = paziente.getString("sesso");
                String emailPaziente = paziente.getString("email_paziente");
                String allergie = paziente.getString("allergie");
                int altezza = paziente.getInt("altezza");
                float p = paziente.getFloat("peso");
                int primoAccesso = paziente.getInt("primo_accesso");

                Paziente pazienteSessione = new Paziente(
                        id,
                        cf,
                        nome,
                        cognome,
                        password,
                        r,
                        medicoRiferimento,
                        dataNascita,
                        sesso,
                        emailPaziente,
                        allergie,
                        primoAccesso
                );
                pazienteSessione.setAltezza(altezza);
                pazienteSessione.setPeso(p);

                return pazienteSessione;

            }

        } catch (SQLException e) {
            System.err.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }
}
