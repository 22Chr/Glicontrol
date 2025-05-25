package com.univr.glicontrol.bll;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TerapiaConcomitante implements Terapia {
    private final int idTerapiaConcomitante;
    private final int idPaziente;
    private int idPatologiaConcomitante;
    private int idMedicoUltimaModifica;
    private final Date dataInizio;
    private Date dataFine;
    private List<FarmacoTerapia> farmaci;

    public TerapiaConcomitante(int idTerapiaConcomitante, int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaci) {
        this.idTerapiaConcomitante = idTerapiaConcomitante;
        this.idPaziente = idPaziente;
        this.idPatologiaConcomitante = idPatologiaConcomitante;
        this.idMedicoUltimaModifica = idMedicoUltimaModifica;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
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
        GestionePatologieConcomitanti gpc = new GestionePatologieConcomitanti(UtenteSessione.getInstance().getPazienteSessione());
        return "Terapia " + gpc.getPatologiaConcomitante(idPatologiaConcomitante).getNomePatologia();
    }

    public List<FarmacoTerapia> getListaFarmaciTerapia() {
        return farmaci;
    }
    public void setListaFarmaciTerapia(List<FarmacoTerapia> farmaci) {
        this.farmaci = farmaci;
    }

}
