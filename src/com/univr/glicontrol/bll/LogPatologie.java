package com.univr.glicontrol.bll;

import java.sql.Timestamp;

public class LogPatologie extends LogSistema {
    private final int idPatologia;

    public LogPatologie(int idLog, int idPatologia, int idMedico, String descrizione, Timestamp timestamp) {
        super(idLog, idMedico, descrizione, timestamp);
        this.idPatologia = idPatologia;
    }

    public int getIdPatologia() {
        return idPatologia;
    }
}
