package com.univr.glicontrol.bll;

import java.sql.Timestamp;

public class LogTerapia extends LogSistema {

    private final int idTerapia;
    private String notePaziente;

    public LogTerapia(int idLogTerapia, int idTerapia, int idMedico, String descrizioneModifiche, String notePaziente, Timestamp timestamp) {
        super(idLogTerapia, idMedico, descrizioneModifiche, timestamp);
        this.idTerapia = idTerapia;
        this.notePaziente = notePaziente;
    }

    public int getIdTerapia() {
        return idTerapia;
    }

    public String getNotePaziente() {
        return notePaziente;
    }
    public void setNotePaziente(String notePaziente) {
        this.notePaziente = notePaziente;
    }
}
