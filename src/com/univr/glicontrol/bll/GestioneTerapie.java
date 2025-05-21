package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoTerapie;
import com.univr.glicontrol.dao.AccessoTerapieImpl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GestioneTerapie {
    private final Paziente paziente;
    private List<Terapia> terapiePaziente = new ArrayList<>();
    private final AccessoTerapie accessoTerapie = new AccessoTerapieImpl();

    public GestioneTerapie(Paziente paziente) {
        this.paziente = paziente;
        generaListaTerapieComune();
    }

    private void generaListaTerapieComune() {
        List<TerapiaDiabete> terapiaDiabete = accessoTerapie.getTerapieDiabetePaziente(paziente.getIdUtente());
        List<TerapiaConcomitante> terapiaConcomitante = accessoTerapie.getTerapieConcomitantiPaziente(paziente.getIdUtente());
        terapiePaziente.addAll(terapiaDiabete);
        terapiePaziente.addAll(terapiaConcomitante);
    }

    public List<Terapia> getTerapiePaziente() {
        generaListaTerapieComune();
        return terapiePaziente;
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

    public int inserisciTerapiaDiabete(int idMedicoUltimaModifica, int idFarmacoTerapia, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari) {
        for (Terapia terapia : terapiePaziente) {
            if (terapia instanceof TerapiaDiabete td && td.getIdFarmacoTerapia() == idFarmacoTerapia) {
                    return -1;
            }
        }

        return accessoTerapie.insertTerapiaDiabete(paziente.getIdUtente(), idMedicoUltimaModifica, idFarmacoTerapia, dataInizio, dataFine, dosaggio, frequenza, orari) ? 1 : 0;
    }

    public int inserisciTerapiaConcomitante(int idPatologia, int idMedicoUltimaModifica, int idFarmacoTerapia, Date dataInizio, Date dataFine, float dosaggio, String frequenza, String orari) {
        for (Terapia terapia : terapiePaziente) {
            if (terapia instanceof TerapiaConcomitante tc && tc.getIdFarmacoTerapia() == idFarmacoTerapia) {
                    return -1;
            }
        }

        return accessoTerapie.insertTerapiaConcomitante(paziente.getIdUtente(), idPatologia, idMedicoUltimaModifica, idFarmacoTerapia, dataInizio, dataFine, dosaggio, frequenza, orari) ? 1 : 0;
    }
}
