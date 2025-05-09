package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoUtenteSessione;
import com.univr.glicontrol.dao.AccessoUtenteSessioneImpl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteSessione {

    // Costruttore privato per impedire istanziazione esterna
    private UtenteSessione() {}

    // Classe statica interna che contiene l'istanza Singleton
    private static class Holder {
        private static final UtenteSessione INSTANCE = new UtenteSessione();
    }

    // Metodo di accesso pubblico
    public static UtenteSessione getInstance() {
        return Holder.INSTANCE;
    }

    // Metodo di autenticazione utente
    public boolean verificaUtente(String codiceFiscale, String pwd, String ruolo) {
        AccessoUtenteSessione access = new AccessoUtenteSessioneImpl(pwd);

        try (ResultSet utente = access.recuperaUtente(codiceFiscale, ruolo)) {
            return utente != null && utente.next();

        } catch (SQLException e) {
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return false;
    }

    public Admin creaAdminConnesso(String codiceFiscale, String pwd, String ruolo) {
        AccessoUtenteSessioneImpl adminAccess = new AccessoUtenteSessioneImpl(pwd);

        try (ResultSet admin = adminAccess.recuperaUtente(codiceFiscale, ruolo)) {

            if (admin != null && admin.next()) {
                int id = admin.getInt("id_utente");
                String nome = admin.getString("nome");
                String cognome = admin.getString("cognome");
                String password = admin.getString("password");
                String cf = admin.getString("codice_fiscale");
                String r = admin.getString("ruolo");

                return new Admin(id, cf, nome, cognome, password, r);
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }

    public Medico creaMedicoConnesso(String codiceFiscale, String pwd, String ruolo) {
        AccessoUtenteSessioneImpl medicoAccess = new AccessoUtenteSessioneImpl(pwd);

        try (ResultSet medico = medicoAccess.recuperaUtente(codiceFiscale, ruolo)) {

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
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }

    public Paziente creaPazienteConnesso(String codiceFiscale, String pwd, String ruolo) {
        AccessoUtenteSessioneImpl pazienteAccess = new AccessoUtenteSessioneImpl(pwd);

        try (ResultSet paziente = pazienteAccess.recuperaUtente(codiceFiscale, ruolo)) {

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
                double p = paziente.getDouble("peso");

                return new Paziente(
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
                        p
                );
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }
}
