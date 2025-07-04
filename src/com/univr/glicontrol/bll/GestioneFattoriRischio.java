package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoFattoriRischio;
import com.univr.glicontrol.dao.AccessoFattoriRischioImpl;

public class GestioneFattoriRischio {

    public boolean inserisciFattoriRischi(String codiceFiscale) {
        Paziente paziente = GestionePazienti.getInstance().getPazientePerCodiceFiscale(codiceFiscale);
        FattoriRischio fattoreRischioDefault = new FattoriRischio(paziente, 0, GlicontrolCoreSystem.getInstance().isObeso(paziente), 0, 0, 0, 0);
        AccessoFattoriRischio accessoFattori = new AccessoFattoriRischioImpl(paziente);

        return accessoFattori.insertFattoreRischio(fattoreRischioDefault);
    }

    public boolean eliminaFattoriRischio(int idPaziente) {
        AccessoFattoriRischio eliminaFattore = new AccessoFattoriRischioImpl(GestionePazienti.getInstance().getPazientePerId(idPaziente));
        return eliminaFattore.deleteFattoreRischio(idPaziente);
    }

    public boolean aggiornaFattoriRischio(FattoriRischio fattoreRischio) {
        AccessoFattoriRischio aggiornaFattore = new AccessoFattoriRischioImpl(GestionePazienti.getInstance().getPazientePerId(fattoreRischio.getIdPaziente()));
        return aggiornaFattore.updateFattoreRischio(fattoreRischio);
    }

    public FattoriRischio getFattoriRischio(int idPaziente) {
        AccessoFattoriRischio recuperaFattore = new AccessoFattoriRischioImpl(GestionePazienti.getInstance().getPazientePerId(idPaziente));
        return recuperaFattore.recuperaFattoreRischio(idPaziente);
    }
}
