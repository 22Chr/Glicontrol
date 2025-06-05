package com.univr.glicontrol.bll;

import java.sql.Timestamp;

public class LogTerapia {
    private final int idLogTerapia;
    private final int idTerapia;
    private int idMedico;
    private String descrizioneModifiche;
    private String notePaziente;
    private Timestamp timestamp;

    public LogTerapia(int idLogTerapia, int idTerapia, int idMedico, String descrizioneModifiche, String notePaziente, Timestamp timestamp) {
        this.idLogTerapia = idLogTerapia;
        this.idTerapia = idTerapia;
        this.idMedico = idMedico;
        this.descrizioneModifiche = descrizioneModifiche;
        this.notePaziente = notePaziente;
        this.timestamp = timestamp;
    }

    public int getIdLogTerapia() {
        return idLogTerapia;
    }

    public int getIdTerapia() {
        return idTerapia;
    }

    public int getIdMedico() {
        return idMedico;
    }
    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getDescrizioneModifiche() {
        return descrizioneModifiche;
    }
    public void setDescrizioneModifiche(String descrizioneModifiche) {
        this.descrizioneModifiche = descrizioneModifiche;
    }

    public String getNotePaziente() {
        return notePaziente;
    }
    public void setNotePaziente(String notePaziente) {
        this.notePaziente = notePaziente;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
