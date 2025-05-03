package com.univr.glicontrol.bll;

public abstract class Utente {
    private final int idUtente;
    private final String codiceFiscale;
    private final String nome;
    private final String cognome;
    private final String ruolo;


    protected Utente(int idUtente, String codiceFiscale, String nome, String cognome, String ruolo) {
        this.idUtente = idUtente;
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getRuolo() {
        return ruolo;
    }
}
