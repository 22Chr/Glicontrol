package com.univr.glicontrol.bll;

public abstract class Utente {
    private final int idUtente;
    private String codiceFiscale;
    private String nome;
    private String cognome;
    private String ruolo;
    private String password;


    protected Utente(int idUtente, String codiceFiscale, String nome, String cognome, String password, String ruolo) {
        this.idUtente = idUtente;
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
        this.password = password;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String setCodiceFiscale(String newCF) {
        return codiceFiscale = newCF;
    }

    public String getNome() {
        return nome;
    }

    public String setNome(String newNome) {
        return nome = newNome;
    }

    public String getCognome() {
        return cognome;
    }

    public String setCognome(String newCognome) {
        return cognome = newCognome;
    }

    public String getRuolo() {
        return ruolo;
    }

    public String setRuolo(String newRuolo) {
        return ruolo = newRuolo;
    }

    public String getPassword() {
        return password;
    }

    public String setPassword(String newPassword) {
        return password = newPassword;
    }

}
