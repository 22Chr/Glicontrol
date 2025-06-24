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

    private Admin adminSessione;
    private Medico medicoSessione;
    private Paziente pazienteSessione;

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

    public Admin creaAdminConnesso(String codiceFiscale, String pwd) {
        AccessoUtenteSessioneImpl adminAccess = new AccessoUtenteSessioneImpl(pwd);

        try (ResultSet admin = adminAccess.recuperaUtente(codiceFiscale, "ADMIN")) {

            if (admin != null && admin.next()) {
                int id = admin.getInt("id_utente");
                String cf = admin.getString("codice_fiscale");
                String nome = admin.getString("nome");
                String cognome = admin.getString("cognome");
                String r = admin.getString("ruolo");
                String password = admin.getString("password");

                adminSessione = new Admin(id, cf, nome, cognome, password, r);
                return adminSessione;
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }

    public Medico creaMedicoConnesso(String codiceFiscale, String pwd) {
        AccessoUtenteSessioneImpl medicoAccess = new AccessoUtenteSessioneImpl(pwd);

        try (ResultSet medico = medicoAccess.recuperaUtente(codiceFiscale, "MEDICO")) {

            if (medico != null && medico.next()) {
                int id = medico.getInt("id_utente");
                String nome = medico.getString("nome");
                String cognome = medico.getString("cognome");
                String password = medico.getString("password");
                String cf = medico.getString("codice_fiscale");
                String r = medico.getString("ruolo");
                String emailMedico = medico.getString("email_medico");

                medicoSessione = new Medico(id, cf, nome, cognome, password, r, emailMedico);
                return medicoSessione;
            }

        } catch (SQLException e) {
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }

    public Paziente creaPazienteConnesso(String codiceFiscale, String pwd) {
        AccessoUtenteSessioneImpl pazienteAccess = new AccessoUtenteSessioneImpl(pwd);

        try (ResultSet paziente = pazienteAccess.recuperaUtente(codiceFiscale, "PAZIENTE")) {

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

                pazienteSessione = new Paziente(
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
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }

        return null;
    }

    // Restituisce gli utenti connessi alla loro sessione per categoria
    // ADMIN
    public Admin getAdminSessione() {
        return adminSessione;
    }

    // MEDICO
    public Medico getMedicoSessione() {
        return medicoSessione;
    }

    // PAZIENTE
    public Paziente getPazienteSessione() {
        return pazienteSessione;
    }
}
