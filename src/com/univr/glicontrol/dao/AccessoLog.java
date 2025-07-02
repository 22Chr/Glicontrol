package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.LogInfoPaziente;
import com.univr.glicontrol.bll.LogPatologie;
import com.univr.glicontrol.bll.LogTerapia;

import java.util.List;

public interface AccessoLog {
    List<LogTerapia> getListaLogTerapie();
    boolean insertLogTerapia(int idTerapia, int idMedico, String descrizioneModifiche, String notePaziente);

    List<LogInfoPaziente> getListaLogInfoPaziente();
    boolean insertLogInfoPaziente(int idMedico, int idPaziente, String descrizione);

    List<LogPatologie> getListaLogPatologie();
    boolean insertLogPatologie(int idPatologia, int idMedico, String descrizione);
}
