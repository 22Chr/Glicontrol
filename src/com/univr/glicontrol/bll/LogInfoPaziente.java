package com.univr.glicontrol.bll;

import java.sql.Timestamp;

public class LogInfoPaziente extends LogSistema {
    private final int idPaziente;

    public LogInfoPaziente(int idLog, int idMedico, int idPaziente, String descrizione, Timestamp timestamp) {
        super(idLog, idMedico, descrizione, timestamp);
        this.idPaziente = idPaziente;
    }

    public int getIdPaziente() {
        return idPaziente;
    }
}
