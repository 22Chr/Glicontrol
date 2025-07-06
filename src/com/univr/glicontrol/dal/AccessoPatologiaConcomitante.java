package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.PatologiaConcomitante;

import java.sql.Date;
import java.util.List;

public interface AccessoPatologiaConcomitante {
    List<PatologiaConcomitante> recuperaPatologiePerPaziente(int idPaziente);
    boolean insertPatologiaConcomitante(int idPaziente, String nomePatologia, String descrizione, Date dataInizio, Date dataFine);
    boolean deletePatologiaConcomitante(int idPatologiaConcomitante);
    boolean updatePatologiaConcomitante(PatologiaConcomitante patologiaConcomitante);
}
