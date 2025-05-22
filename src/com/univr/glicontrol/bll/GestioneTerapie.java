package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoTerapie;
import com.univr.glicontrol.dao.AccessoTerapieImpl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GestioneTerapie {
    private final Paziente paziente;
    private final List<Terapia> terapiePaziente = new ArrayList<>();
    private final AccessoTerapie accessoTerapie = new AccessoTerapieImpl();

    List<TerapiaDiabete> terapiaDiabete;
    List<TerapiaConcomitante> terapiaConcomitante;

    public GestioneTerapie(Paziente paziente) {
        this.paziente = paziente;
        generaListaTerapieComune();
    }

    private void generaListaTerapieComune() {
        terapiaDiabete = accessoTerapie.getTerapieDiabetePaziente(paziente.getIdUtente());
        terapiaConcomitante = accessoTerapie.getTerapieConcomitantiPaziente(paziente.getIdUtente());
        assert terapiaDiabete != null;
        terapiePaziente.addAll(terapiaDiabete);
        assert terapiaConcomitante != null;
        terapiePaziente.addAll(terapiaConcomitante);
    }

    public List<Terapia> getTerapiePaziente() {
        generaListaTerapieComune();
        return terapiePaziente;
    }

    private void aggiornaListaTerapieDiabete() {
        terapiaDiabete = accessoTerapie.getTerapieDiabetePaziente(paziente.getIdUtente());
    }

    private void aggiornaListaTerapieConcomitanti() {
        terapiaConcomitante = accessoTerapie.getTerapieConcomitantiPaziente(paziente.getIdUtente());
    }

    private void aggiornaListaTerapie() {
        terapiePaziente.clear();
        aggiornaListaTerapieDiabete();
        aggiornaListaTerapieConcomitanti();
        terapiePaziente.addAll(terapiaDiabete);
        terapiePaziente.addAll(terapiaConcomitante);
    }

    public boolean aggiornaTerapia(Terapia terapia) {
        if (terapia instanceof TerapiaDiabete) {
            return accessoTerapie.updateTerapiaDiabete((TerapiaDiabete) terapia);
        } else if (terapia instanceof TerapiaConcomitante) {
            return accessoTerapie.updateTerapiaConcomitante((TerapiaConcomitante) terapia);
        } else {
            return false;
        }
    }

    public int inserisciTerapiaDiabete(int idMedicoUltimaModifica, Date dataInizio, Date dataFine, String dosaggio, String frequenza, String orari, List<Farmaco> farmaci) {
        // Verificare successivamente se inserire un controllo sui duplicati
        aggiornaListaTerapie();
        for (TerapiaDiabete terapia : terapiaDiabete) {
            if (terapia.getDataInizio().equals(dataInizio) && terapia.getFarmaciTerapiaDiabete().equals(farmaci) && terapia.getIdPaziente() == paziente.getIdUtente()) {
                return -1;
            }
        }

        return accessoTerapie.insertTerapiaDiabete(paziente.getIdUtente(), idMedicoUltimaModifica, dataInizio, dataFine, dosaggio, frequenza, orari, farmaci) ? 1 : 0;
    }

    public int inserisciTerapiaConcomitante(int idPatologia, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari, int idFarmaco) {
        // Come sopra
        return accessoTerapie.insertTerapiaConcomitante(paziente.getIdUtente(), idPatologia, idMedicoUltimaModifica, dataInizio, dataFine, dosaggio, frequenza, orari, idFarmaco) ? 1 : 0;
    }

    public TerapiaDiabete getTerapiaDiabete(int idTerapia) {
        for (Terapia terapia : terapiePaziente) {
            if (terapia instanceof TerapiaDiabete td && td.getIdTerapiaDiabete() == idTerapia) {
                return td;
            }
        }

        return null;
    }
}
