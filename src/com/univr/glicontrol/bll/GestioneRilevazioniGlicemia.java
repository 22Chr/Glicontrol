package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoRilevazioniGlicemia;
import com.univr.glicontrol.dao.AccessoRilevazioniGlicemiaImpl;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GestioneRilevazioniGlicemia {
    private final Paziente paziente;
    private final AccessoRilevazioniGlicemia accessoRilevazioniGlicemia = new AccessoRilevazioniGlicemiaImpl();
    private List<RilevazioneGlicemica> rilevazioni;

    public GestioneRilevazioniGlicemia(Paziente paziente) {
        this.paziente = paziente;
        rilevazioni = accessoRilevazioniGlicemia.recuperaRilevazioniPaziente(paziente.getIdUtente());
    }

    public List<RilevazioneGlicemica> getRilevazioni() {
        return rilevazioni;
    }

    public void aggiornaListaRilevazioni() {
        rilevazioni = accessoRilevazioniGlicemia.recuperaRilevazioniPaziente(paziente.getIdUtente());
    }

    public int inserisciRilevazione(Date data, Time ora, float valore, String pasto, String indicazioniTemporali) {
        aggiornaListaRilevazioni();
        for (RilevazioneGlicemica rilevazione : rilevazioni) {
            if (rilevazione.getData().equals(data) && rilevazione.getOra().equals(ora)) {
                return -1;
            }
        }

        return accessoRilevazioniGlicemia.insertRilevazioneGlicemica(paziente.getIdUtente(), data, ora, valore, pasto, indicazioniTemporali) ? 1 : 0;
    }

    public boolean eliminaRilevazione(int idRilevazione) {
        return accessoRilevazioniGlicemia.deleteRilevazioneGlicemica(idRilevazione);
    }

    public Map<String, Double> getMediaGiornalieraGlicemia(int anno, int settimana) {
        return accessoRilevazioniGlicemia.recuperaMediaGiornalieraPerSettimanaGlicemia(paziente.getIdUtente(), anno, settimana);
    }

    public Map<String, Double> getMediaMensileGlicemiaPerMeseCorrente(int anno, int mese) {
        return accessoRilevazioniGlicemia.recuperaMediaMensileGlicemiaPerMeseCorrente(paziente.getIdUtente(), anno, mese);
    }

    public List<RilevazioneGlicemica> getRilevazioniPerData(LocalDate data) {
        AccessoRilevazioniGlicemiaImpl dao = new AccessoRilevazioniGlicemiaImpl();
        return dao.recuperaRilevazioniPerData(paziente.getIdUtente(), data);
    }
}
