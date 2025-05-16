package com.univr.glicontrol.bll;

import java.time.LocalDate;
import java.time.Period;

public class FattoriRischio {
    private final Paziente paziente;
    private final int idPaziente;
    private int familiarita;
    private int eta;
    private int obesita;
    private int sedentarieta;
    private int alimentazioneScorretta;
    private int fumatore;
    private int problemiAlcol;

    public FattoriRischio(Paziente paziente, int familiarita, int obesita, int sedentarieta, int alimentazioneScorretta, int fumatore, int problemiAlcol) {
        this.paziente = paziente;
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

    public int getObesita() {
        return obesita;
    }
    public void setObesita(int obesita) {
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
}
