package com.univr.glicontrol.bll;

import java.sql.Date;

public class Paziente extends Utente {
    private final int medicoRiferimento;
    private final Date dataNascita;
    private final String sesso;
    private final String email;
    private final String allergie;


    public Paziente(int idPaziente, String codiceFiscale, String nome, String cognome, String ruolo, int medicoRiferimento, Date dataNascita, String sesso, String email, String allergie) {
        super(idPaziente, codiceFiscale, nome, cognome, ruolo);
        this.medicoRiferimento = medicoRiferimento;
        this.dataNascita = dataNascita;
        this.sesso = sesso;
        this.email = email;
        this.allergie = allergie;
    }

    public int getMedicoRiferimento() {
        return medicoRiferimento;
    }

    public Date getDataNascita() {
        return dataNascita;
    }

    public String getSesso() {
        return sesso;
    }

    public String getEmail() {
        return email;
    }

    public String getAllergie() {
        return allergie;
    }
}
