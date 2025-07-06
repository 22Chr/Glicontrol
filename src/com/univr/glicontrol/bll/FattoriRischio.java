package com.univr.glicontrol.bll;

import java.time.LocalDate;
import java.time.Period;

public class FattoriRischio {
    private final int idPaziente;
    private int familiarita;
    private final int eta;
    private boolean obesita;
    private int sedentarieta;
    private int alimentazioneScorretta;
    private int fumatore;
    private int problemiAlcol;

    public FattoriRischio(Paziente paziente, int familiarita, boolean obesita, int sedentarieta, int alimentazioneScorretta, int fumatore, int problemiAlcol) {
        this.idPaziente = paziente.getIdUtente();
        this.familiarita = familiarita;
        this.obesita = obesita;
        this.sedentarieta = sedentarieta;
        this.alimentazioneScorretta = alimentazioneScorretta;
        this.fumatore = fumatore;
        this.problemiAlcol = problemiAlcol;

        LocalDate dataNascita = LocalDate.parse(paziente.getDataNascita().toString());
        Period period = Period.between(dataNascita, LocalDate.now());
        this.eta = period.getYears();
    }

    public int getEta() {
        return eta;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public boolean getObesita() {
        return obesita;
    }
    public void setObesita(boolean obesita) {
        this.obesita = obesita;
    }

    public int getSedentarieta() {
        return sedentarieta;
    }
    public void setSedentarieta(int sedentarieta) {
        this.sedentarieta = sedentarieta;
    }

    public int getAlimentazioneScorretta() {
        return alimentazioneScorretta;
    }
    public void setAlimentazioneScorretta(int alimentazioneScorretta) {
        this.alimentazioneScorretta = alimentazioneScorretta;
    }

    public int getFumatore() {
        return fumatore;
    }
    public void setFumatore(int fumatore) {
        this.fumatore = fumatore;
    }

    public int getProblemiAlcol() {
        return problemiAlcol;
    }
    public void setProblemiAlcol(int problemiAlcol) {
        this.problemiAlcol = problemiAlcol;
    }

    public int getFamiliarita() {
        return familiarita;
    }
    public void setFamiliarita(int familiarita) {
        this.familiarita = familiarita;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof FattoriRischio other)) return false;
        return this.idPaziente == other.idPaziente &&
                this.familiarita == other.familiarita &&
                this.eta == other.eta &&
                this.obesita == other.obesita &&
                this.sedentarieta == other.sedentarieta &&
                this.alimentazioneScorretta == other.alimentazioneScorretta &&
                this.fumatore == other.fumatore &&
                this.problemiAlcol == other.problemiAlcol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result *= prime + idPaziente;
        result *= prime + familiarita;
        result *= prime + eta;
        result *= prime + sedentarieta;
        result *= prime + alimentazioneScorretta;
        result *= prime + fumatore;
        result *= prime + problemiAlcol;
        return result;
    }
}
