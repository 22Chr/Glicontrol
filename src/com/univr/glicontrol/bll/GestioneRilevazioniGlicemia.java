package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoRilevazioniGlicemia;
import com.univr.glicontrol.dal.AccessoRilevazioniGlicemiaImpl;

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
        aggiornaListaRilevazioni();
    }

    public List<RilevazioneGlicemica> getRilevazioni() {
        return rilevazioni;
    }

    private void aggiornaListaRilevazioni() {
        rilevazioni = accessoRilevazioniGlicemia.recuperaRilevazioniPaziente(paziente.getIdUtente());
    }

    public int inserisciRilevazione(Date data, Time ora, int valore, String pasto, String indicazioniTemporali) {
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

    public List<RilevazioneGlicemica> getRilevazioniGlicemicheNonGestitePaziente() {
        return accessoRilevazioniGlicemia.recuperaRilevazioniPazienteNonGestite(paziente.getIdUtente());
    }

    public List<RilevazioneGlicemica> getRilevazioniGlicemicheNonGestitePerData(LocalDate date) {
        return accessoRilevazioniGlicemia.recuperaRilevazioniPerDataNonGestite(paziente.getIdUtente(), date);
    }

    public boolean updateStatoRilevazioneGlicemica(RilevazioneGlicemica rilevazione) {
        return accessoRilevazioniGlicemia.updateStatoRilevazioneGlicemica(rilevazione.getIdRilevazione());
    }
}
