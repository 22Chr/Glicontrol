package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.TerapiaConcomitante;
import com.univr.glicontrol.bll.TerapiaDiabete;

import java.sql.Date;
import java.util.List;

public interface AccessoTerapie {
    List<TerapiaDiabete> getTerapieDiabetePaziente(int idPaziente);
    List<TerapiaConcomitante> getTerapieConcomitantiPaziente(int idPaziente);

    boolean insertTerapiaDiabete(int idPaziente, int idMedicoUltimaModifica, int idFarmacoTerapia, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari);
    boolean insertTerapiaConcomitante(int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, int idFarmacoTerapia, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari);

    boolean updateTerapiaDiabete(TerapiaDiabete terapia);
    boolean updateTerapiaConcomitante(TerapiaConcomitante terapia);
}
