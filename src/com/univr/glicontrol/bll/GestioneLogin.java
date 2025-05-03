package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoUtente;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GestioneLogin {

    private final AccessoUtente access = new AccessoUtente();

    public Utente verificaUtente(String codiceFiscale, String pwd, String ruolo) {
        try(ResultSet utente = access.recuperaUtente(codiceFiscale, pwd, ruolo)) {
               if (utente != null && utente.next()) {
                   int id = utente.getInt("id_utente");
                   String nome = utente.getString("nome");
                   String cognome = utente.getString("cognome");
                   String cf = utente.getString("codice_fiscale");
                   String r = utente.getString("ruolo");


                   return switch (ruolo) {
                       case "PAZIENTE" -> new Paziente(id, nome, cognome, cf, r,
                                   utente.getInt("medico_riferimento"),
                                   utente.getDate("data_nascita"),
                                   utente.getString("sesso"),
                                   utente.getString("email_paziente"),
                                   utente.getString("allergie") );

                       case "MEDICO" -> new Medico(id, nome, cognome, cf, r, utente.getString("email_medico"));

                       default -> new Admin(id, nome, cognome, cf, r);
                   };
               }

        } catch (SQLException e) {
            System.out.println("[ERRORE LOGIN BLL]: " + e.getMessage());
        }
        return null;
    }
}
