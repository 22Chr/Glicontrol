package com.univr.glicontrol.bll;

import java.sql.Timestamp;

public class LogSistema {
    private final int idLog;
    private final int idMedico;
    private final String descrizione;
    private final Timestamp timestamp;

    public LogSistema(int idLog, int idMedico, String descrizione, Timestamp timestamp) {
        this.idLog = idLog;
        this.idMedico = idMedico;
        this.descrizione = descrizione;
        this.timestamp = timestamp;
    }

    public int getIdLog() {
        return idLog;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
