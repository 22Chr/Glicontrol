package com.univr.glicontrol.bll;

import java.time.LocalDateTime;

public class Notifica {
    private final String titolo;
    private final String messaggio;
    private final LocalDateTime dataNotifica;
    private final String tipoNotifica;
    private boolean visualizzato;

    public Notifica(String titolo, String messaggio) {
        this.titolo = titolo;
        this.messaggio = messaggio;
        this.dataNotifica = LocalDateTime.now();
        this.tipoNotifica = "WARNING";
        visualizzato = false;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getMessaggio() {
        return messaggio;
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
