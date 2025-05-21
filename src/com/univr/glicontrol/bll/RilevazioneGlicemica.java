package com.univr.glicontrol.bll;

import java.sql.Date;
import java.sql.Time;

public class RilevazioneGlicemica {
    private final int idRilevazione;
    private final int idPaziente;
    private final Date data;
    private final Time ora;
    private final float valore;
    private final String pasto;
    private final String indicazioniTemporali;

    public RilevazioneGlicemica(int idRilevazione, int idPaziente, Date data, Time ora, float valore, String pasto, String indicazioniTemporali) {
        this.idRilevazione = idRilevazione;
        this.idPaziente = idPaziente;
        this.data = data;
        this.ora = ora;
        this.valore = valore;
        this.pasto = pasto;
        this.indicazioniTemporali = indicazioniTemporali;
    }

    public int getIdRilevazione() {
        return idRilevazione;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public Date getData() {
        return data;
    }

    public Time getOra() {
        return ora;
    }

    public float getValore() {
        return valore;
    }

    public String getPasto() {
        return pasto;
    }

    public String getIndicazioniTemporali() {
        return indicazioniTemporali;
    }

}
