package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Farmaco;
import com.univr.glicontrol.bll.FarmacoTerapia;
import com.univr.glicontrol.bll.TerapiaConcomitante;
import com.univr.glicontrol.bll.TerapiaDiabete;

import java.sql.Date;
import java.util.List;

public interface AccessoTerapie {
    List<TerapiaDiabete> getTerapieDiabetePaziente(int idPaziente);
    List<TerapiaConcomitante> getTerapieConcomitantiPaziente(int idPaziente);

    boolean insertTerapiaDiabete(int idPaziente, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaciTerapia);
    boolean insertTerapiaConcomitante(int idPaziente, int idPatologiaConcomitante, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaci);

    boolean updateTerapiaDiabete(TerapiaDiabete terapia);
    boolean updateTerapiaConcomitante(TerapiaConcomitante terapia);
}
