package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.RilevazioneGlicemica;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Map;

public interface AccessoRilevazioniGlicemia {
    List<RilevazioneGlicemica> recuperaRilevazioniPaziente(int idPaziente);
    boolean deleteRilevazioneGlicemica(int idRilevazione);
    boolean insertRilevazioneGlicemica(int idPaziente, Date data, Time ora, float glicemia, String pasto, String indicazioniTemporali);
    Map<String, Double> recuperaMediaGiornalieraPerSettimanaGlicemia(int idPaziente, int anno, int numeroSettimana);
    Map<String, Double> recuperaMediaMensileGlicemiaPerMeseCorrente(int idPaziente, int anno, int mese);
}
