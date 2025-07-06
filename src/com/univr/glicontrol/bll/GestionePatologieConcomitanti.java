package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoPatologiaConcomitante;
import com.univr.glicontrol.dal.AccessoPatologiaConcomitanteImpl;

import java.sql.Date;
import java.util.List;

public class GestionePatologieConcomitanti {
    private final Paziente paziente;
    private final AccessoPatologiaConcomitante apc = new AccessoPatologiaConcomitanteImpl();
    private List<PatologiaConcomitante> listaPatologieConcomitanti;

    public GestionePatologieConcomitanti(Paziente paziente) {
        this.paziente = paziente;
        listaPatologieConcomitanti = apc.recuperaPatologiePerPaziente(paziente.getIdUtente());
    }

    public List<PatologiaConcomitante> getListaPatologieConcomitanti() {
        listaPatologieConcomitanti = apc.recuperaPatologiePerPaziente(paziente.getIdUtente());
        return listaPatologieConcomitanti;
    }


    public int inserisciPatologiaConcomitante(String nomePatologia, String descrizione, Date dataInizio, Date dataFine) {
        // Verifica che la patologia non esista già
        for (PatologiaConcomitante patologia : listaPatologieConcomitanti) {
            if (patologia.getNomePatologia().equals(nomePatologia) && patologia.getDataInizio().equals(dataInizio)) {
                return -1;
            }
        }

        return apc.insertPatologiaConcomitante(paziente.getIdUtente(), nomePatologia, descrizione, dataInizio, dataFine) ? 1 : 0;
    }

    public PatologiaConcomitante getPatologiaConcomitante(int idPatologiaConcomitante) {
        for (PatologiaConcomitante patologia : listaPatologieConcomitanti) {
            if (patologia.getIdPatologia() == idPatologiaConcomitante) {
                return patologia;
            }
        }

        return null;
    }

    public boolean aggiornaPatologiaConcomitante(PatologiaConcomitante patologia) {
        return apc.updatePatologiaConcomitante(patologia);
    }
}
