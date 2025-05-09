package com.univr.glicontrol.bll;

public class Admin extends Utente {
    public Admin(int idUtente, String codiceFiscale, String nome, String cognome, String password, String ruolo) {
        super(idUtente, codiceFiscale, nome, cognome, password, ruolo);
    }
}
