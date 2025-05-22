package com.univr.glicontrol.bll;

import java.sql.Date;
import java.util.List;

public class TerapiaConcomitante implements Terapia {
    private final int idTerapiaConcomitante;
    private final int idPaziente;
    private int idPatologiaConcomitante;
    private int idMedicoUltimaModifica;
    private final Date dataInizio;
    private Date dataFine;
    private String dosaggi;
    private String frequenza;
    private String orari;
    private List<Farmaco> farmaci;

    public TerapiaConcomitante(int idTerapiaConcomitante, int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, String dosaggi, String frequenza, String orari, List<Farmaco> farmaci) {
        this.idTerapiaConcomitante = idTerapiaConcomitante;
        this.idPaziente = idPaziente;
        this.idPatologiaConcomitante = idPatologiaConcomitante;
        this.idMedicoUltimaModifica = idMedicoUltimaModifica;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.dosaggi = dosaggi;
        this.frequenza = frequenza;
        this.orari = orari;
        this.farmaci = farmaci;
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
        GestionePatologieConcomitanti gpc = new GestionePatologieConcomitanti(UtenteSessione.getInstance().getPazienteSessione());
        return "Terapia " + gpc.getPatologiaConcomitante(idPatologiaConcomitante);
    }

    public List<Farmaco> getListaFarmaciTerapia() {
        return farmaci;
    }
    public void setListaFarmaciTerapia(List<Farmaco> farmaci) {
        this.farmaci = farmaci;
    }

}
