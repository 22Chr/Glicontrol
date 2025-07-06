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

    public void setCodiceFiscale(String newCF) {
        codiceFiscale = newCF;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String newNome) {
        nome = newNome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String newCognome) {
        cognome = newCognome;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String newRuolo) {
        ruolo = newRuolo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
    }

}
