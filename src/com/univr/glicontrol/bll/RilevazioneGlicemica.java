package com.univr.glicontrol.bll;

import java.sql.Date;
import java.sql.Time;

public class RilevazioneGlicemica {
    private final int idRilevazione;
    private final int idPaziente;
    private final Date data;
    private final Time ora;
    private final int valore;
    private final String pasto;
    private final String indicazioniTemporali;

    public RilevazioneGlicemica(int idRilevazione, int idPaziente, Date data, Time ora, int valore, String pasto, String indicazioniTemporali, boolean gestito) {
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

    public int getValore() {
        return valore;
    }

    public String getPasto() {
        return pasto;
    }

    public String getIndicazioniTemporali() {
        return indicazioniTemporali;
    }
}
