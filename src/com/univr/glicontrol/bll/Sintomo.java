package com.univr.glicontrol.bll;

import java.sql.Date;
import java.sql.Time;

public class Sintomo {
    private final int idSintomo;
    private final int idPaziente;
    private final String descrizione;
    private final Date data;
    private final Time ora;

    public Sintomo(int idSintomo, int idPaziente, String descrizione, Date data, Time ora) {
        this.idSintomo = idSintomo;
        this.idPaziente = idPaziente;
        this.descrizione = descrizione;
        this.data = data;
        this.ora = ora;
    }

    public int getIdSintomo() {
        return idSintomo;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Date getData() {
        return data;
    }

    public Time getOra() {
        return ora;
    }
}
