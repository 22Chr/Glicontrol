package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.RilevazioneGlicemica;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AccessoRilevazioniGlicemia {
    List<RilevazioneGlicemica> recuperaRilevazioniPaziente(int idPaziente);
    List<RilevazioneGlicemica> recuperaRilevazioniPerData(int idPaziente, LocalDate data);
    List<RilevazioneGlicemica> recuperaRilevazioniPazienteNonGestite(int idPaziente);
    List<RilevazioneGlicemica> recuperaRilevazioniPerDataNonGestite(int idPaziente, LocalDate data);
    boolean deleteRilevazioneGlicemica(int idRilevazione);
    boolean insertRilevazioneGlicemica(int idPaziente, Date data, Time ora, float glicemia, String pasto, String indicazioniTemporali);
    Map<String, Double> recuperaMediaGiornalieraPerSettimanaGlicemia(int idPaziente, int anno, int numeroSettimana);
    Map<String, Double> recuperaMediaMensileGlicemiaPerMeseCorrente(int idPaziente, int anno, int mese);
    boolean updateStatoRilevazioneGlicemica(int idRilevazioneGlicemica);
}
