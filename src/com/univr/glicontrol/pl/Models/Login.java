package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.GestioneLogin;

// Si interfaccia alla UI mediante Controller e invoca GestioneLogin dalla BLL per creare l'utente e accedere all'app
public class Login {
    private final String codiceFiscale;
    private final String password;
    private final String ruolo;

    public Login(String codiceFiscale, String password, String ruolo) {
        this.codiceFiscale = codiceFiscale;
        this.password = password;
        this.ruolo = ruolo;
    }

    private final GestioneLogin gestioneLogin = new GestioneLogin();

    public boolean getLogin() {
        return gestioneLogin.verificaUtente(codiceFiscale, password, ruolo) != null;
    }
}
