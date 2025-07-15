package com.univr.glicontrol.bll;

import java.sql.Timestamp;

public class LogTerapia extends LogSistema {

    private final int idTerapia;

    public LogTerapia(int idLogTerapia, int idTerapia, int idMedico, String descrizioneModifiche, Timestamp timestamp) {
        super(idLogTerapia, idMedico, descrizioneModifiche, timestamp);
        this.idTerapia = idTerapia;
    }

    public int getIdTerapia() {
        return idTerapia;
    }
}
