package com.univr.glicontrol.bll;

import java.sql.Date;
import java.util.List;

public class TerapiaDiabete implements Terapia {
    private final int idTerapiaDiabete;
    private final int idPaziente;
    private int idMedicoUltimaModifica;
    private final Date dataInizio;
    private Date dataFine;
    private List<Farmaco> farmaci;

    public TerapiaDiabete(int idTerapiaDiabete, int idPaziente, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<Farmaco> farmaci) {
        this.idTerapiaDiabete = idTerapiaDiabete;
        this.idPaziente = idPaziente;
        this.idMedicoUltimaModifica = idMedicoUltimaModifica;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
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

    public String getNome() {
        return "Terapia diabete";
    }

    public List<Farmaco> getListaFarmaciTerapia() {
        return farmaci;
    }
    public void setListaFarmaciTerapia(List<Farmaco> farmaci) {
        this.farmaci = farmaci;
    }

    public int getIdPatologiaConcomitante() {
        return -5; //codice associato a TerapiaDiabete per alludere alla mancanza di patologie concomitanti
    }
}
