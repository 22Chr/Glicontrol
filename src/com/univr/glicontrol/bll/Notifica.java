package com.univr.glicontrol.bll;

import java.time.LocalDateTime;

public class Notifica {

    private int idNotifica;
    private final String titolo;
    private final String messaggio;
    private final Paziente pazienteAssociato;
    private final LocalDateTime dataNotifica;
    private final String tipoNotifica;
    private boolean visualizzato;

    public Notifica(String titolo, String messaggio, Paziente pazienteAssociato, LocalDateTime data, TipologiaNotifica tipologiaNotifica, boolean visualizzato) {
        this.titolo = titolo;
        this.messaggio = messaggio;
        this.pazienteAssociato = pazienteAssociato;
        this.dataNotifica = LocalDateTime.now();
        this.tipoNotifica = tipologiaNotifica.toString();
        this.visualizzato = visualizzato;
    }

    public int getIdNotifica() {
        return idNotifica;
    }
    public void setIdNotifica(int idNotifica) {
        this.idNotifica = idNotifica;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public Paziente getPazienteAssociato() {
        return pazienteAssociato;
    }

    public LocalDateTime getDataNotifica() {
        return dataNotifica;
    }

    public String getTipoNotifica() {
        return tipoNotifica;
    }

    public boolean isVisualizzato() {
        return visualizzato;
    }
    public void setVisualizzato() {
        this.visualizzato = true;
    }

}
