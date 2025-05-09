package com.univr.glicontrol.bll;

public class Medico extends Utente {
    private String email;

    public Medico(int idUtente, String codiceFiscale, String nome, String cognome, String password, String ruolo, String email) {
        super(idUtente, codiceFiscale, nome, cognome, password, ruolo);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
