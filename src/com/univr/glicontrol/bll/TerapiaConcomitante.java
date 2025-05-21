package com.univr.glicontrol.bll;

import java.sql.Date;

public class TerapiaConcomitante implements Terapia {
    private final int idTerapiaConcomitante;
    private final int idPaziente;
    private int idPatologiaConcomitante;
    private int idMedicoUltimaModifica;
    private final int idFarmacoTerapia;
    private final Date dataInizio;
    private Date dataFine;
    private float dosaggio;
    private String frequenza;
    private String orari;

    public TerapiaConcomitante(int idTerapiaConcomitante, int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, int idFarmacoTerapia, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari) {
        this.idTerapiaConcomitante = idTerapiaConcomitante;
        this.idPaziente = idPaziente;
        this.idPatologiaConcomitante = idPatologiaConcomitante;
        this.idMedicoUltimaModifica = idMedicoUltimaModifica;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.idFarmacoTerapia = idFarmacoTerapia;
        this.dosaggio = dosaggio;
        this.frequenza = frequenza;
        this.orari = orari;
    }

    public int getIdTerapiaConcomitante() {
        return idTerapiaConcomitante;
    }
    public void setIdPatologiaConcomitante(int idPatologiaConcomitante) {
        this.idPatologiaConcomitante = idPatologiaConcomitante;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public int getIdPatologiaConcomitante() {
        return idPatologiaConcomitante;
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

    public int getIdFarmacoTerapia() {
        return idFarmacoTerapia;
    }

    public float getDosaggio() {
        return dosaggio;
    }
    public void setDosaggio(float dosaggio) {
        this.dosaggio = dosaggio;
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
        GestionePatologieConcomitanti gpc = new GestionePatologieConcomitanti(UtenteSessione.getInstance().getPazienteSessione());
        return "Terapia " + gpc.getPatologiaConcomitante(idPatologiaConcomitante);
    }

}
