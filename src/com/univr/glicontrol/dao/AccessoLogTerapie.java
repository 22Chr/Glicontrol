package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.LogTerapia;

import java.util.List;

public interface AccessoLogTerapie {
    List<LogTerapia> getListaLogTerapie();
    boolean insertLogTerapia(int idTerapia, int idMedico, String descrizioneModifiche, String notePaziente);
}
