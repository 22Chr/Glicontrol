package com.univr.glicontrol.bll;

import java.sql.Date;

public class Paziente extends Utente {
    private int medicoRiferimento;
    private Date dataNascita;
    private String sesso;
    private String email;
    private String allergie;
    private float peso;
    private int primoAccesso;


    public Paziente(int idPaziente, String codiceFiscale, String nome, String cognome, String password, String ruolo, int medicoRiferimento, Date dataNascita, String sesso, String email, String allergie, float peso, int primoAccesso) {
        super(idPaziente, codiceFiscale, nome, cognome, password, ruolo);
        this.medicoRiferimento = medicoRiferimento;
        this.dataNascita = dataNascita;
        this.sesso = sesso;
        this.email = email;
        this.allergie = allergie;
        this.peso = peso;
        this.primoAccesso = primoAccesso;
    }

    public int getMedicoRiferimento() {
        return medicoRiferimento;
    }
    public void setMedicoRiferimento(int medicoRiferimento) {
        this.medicoRiferimento = medicoRiferimento;
    }

    public Date getDataNascita() {
        return dataNascita;
    }
    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getSesso() {
        return sesso;
    }
    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAllergie() {
        return allergie;
    }
    public void setAllergie(String allergie) {
        this.allergie = allergie;
    }

    public float getPeso() {
        return peso;
    }
    public void setPeso(float peso) {
        this.peso = peso;
    }

    public int getPrimoAccesso() {
        return primoAccesso;
    }
    public void setPrimoAccesso(int primoAccesso) {
        this.primoAccesso = primoAccesso;
    }
}
