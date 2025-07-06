package com.univr.glicontrol.bll;

import java.sql.Date;

public class PatologiaConcomitante {
    private final int idPatologia;
    private final int idPaziente;
    private final String nomePatologia;
    private String descrizione;
    private final Date dataInizio;
    private Date dataFine;

    public PatologiaConcomitante(int idPatologia, int idPaziente, String nomePatologia, String descrizione, Date dataInizio, Date dataFine) {
        this.idPatologia = idPatologia;
        this.idPaziente = idPaziente;
        this.nomePatologia = nomePatologia;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public int getIdPatologia() {
        return idPatologia;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public String getNomePatologia() {
        return nomePatologia;
    }

    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }
    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }

}
