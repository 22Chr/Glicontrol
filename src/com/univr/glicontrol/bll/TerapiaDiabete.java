package com.univr.glicontrol.bll;

import java.sql.Date;
import java.util.List;

public class TerapiaDiabete implements Terapia {
    private int idTerapiaDiabete;
    private final int idPaziente;
    private int idMedicoUltimaModifica;
    private final Date dataInizio;
    private Date dataFine;
    private List<FarmacoTerapia> farmaci;

    public TerapiaDiabete(int idPaziente, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaci) {
        this.idPaziente = idPaziente;
        this.idMedicoUltimaModifica = idMedicoUltimaModifica;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.farmaci = farmaci;
    }

    public int getIdTerapiaDiabete() {
        return idTerapiaDiabete;
    }
    public void setIdTerapiaDiabete(int idTerapiaDiabete) {
        this.idTerapiaDiabete = idTerapiaDiabete;
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

    @Override
    public float getDosaggioPerFarmaco(String nomeFarmaco) {
        for (FarmacoTerapia farmaco : farmaci) {
            if (farmaco.getFarmaco().getNome().equals(nomeFarmaco)) {
                return farmaco.getIndicazioni().getDosaggio();
            }
        }
        return 0;
    }

    @Override
    public String getFrequenzaPerFarmaco(String nomeFarmaco) {
        for (FarmacoTerapia farmaco : farmaci) {
            if (farmaco.getFarmaco().getNome().equals(nomeFarmaco)) {
                return farmaco.getIndicazioni().getFrequenzaAssunzione();
            }
        }
        return "";
    }

    @Override
    public String getOrarioPerFarmaco(String nomeFarmaco) {
        for (FarmacoTerapia farmaco : farmaci) {
            if (farmaco.getFarmaco().getNome().equals(nomeFarmaco)) {
                return farmaco.getIndicazioni().getOrariAssunzione();
            }
        }
        return "";
    }


    public Date getDataInizio() {
        return dataInizio;
    }

    public String getNome() {
        return "Terapia diabete";
    }

    public List<FarmacoTerapia> getListaFarmaciTerapia() {
        return farmaci;
    }
    public void setListaFarmaciTerapia(List<FarmacoTerapia> farmaci) {
        this.farmaci = farmaci;
    }

    public int getIdPatologiaConcomitante() {
        return -5; //codice associato a TerapiaDiabete per alludere alla mancanza di patologie concomitanti
    }
}
