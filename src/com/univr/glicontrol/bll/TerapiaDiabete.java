package com.univr.glicontrol.bll;

import java.sql.Date;
import java.util.List;

public class TerapiaDiabete implements Terapia {
    private final int idTerapiaDiabete;
    private final int idPaziente;
    private int idMedicoUltimaModifica;
    private final Date dataInizio;
    private Date dataFine;
    private String dosaggi;
    private String frequenza;
    private String orari;
    private List<Farmaco> farmaci;

    public TerapiaDiabete(int idTerapiaDiabete, int idPaziente, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, String dosaggi, String frequenza, String orari, List<Farmaco> farmaci) {
        this.idTerapiaDiabete = idTerapiaDiabete;
        this.idPaziente = idPaziente;
        this.idMedicoUltimaModifica = idMedicoUltimaModifica;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.dosaggi = dosaggi;
        this.frequenza = frequenza;
        this.orari = orari;
        this.farmaci = farmaci;
    }

    public int getIdTerapiaDiabete() {
        return idTerapiaDiabete;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public int getIdMedicoUltimaModifica() {
        return idMedicoUltimaModifica;
    }
    public void setIdMedicoUltimaModifica(int idMedicoUltimaModifica) {
        this.idMedicoUltimaModifica = idMedicoUltimaModifica;
    }

    public Date getDataFine() {
        return dataFine;
    }
    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public String getDosaggi() {
        return dosaggi;
    }
    public void setDosaggi(String dosaggi) {
        this.dosaggi = dosaggi;
    }

    public String getFrequenza() {
        return frequenza;
    }
    public void setFrequenza(String frequenza) {
        this.frequenza = frequenza;
    }

    public String getOrari() {
        return orari;
    }
    public void setOrari(String orari) {
        this.orari = orari;
    }

    public String getNome() {
        return "Terapia diabete";
    }

    public List<Farmaco> getFarmaciTerapiaDiabete() {
        return farmaci;
    }
    public void setFarmaciTerapiaDiabete(List<Farmaco> farmaci) {
        this.farmaci = farmaci;
    }

}
