package com.univr.glicontrol.bll;

public class Medico extends Utente {
    private final String email;

    public Medico(int idUtente, String codiceFiscale, String nome, String cognome, String ruolo, String email) {
        super(idUtente, codiceFiscale, nome, cognome, ruolo);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
