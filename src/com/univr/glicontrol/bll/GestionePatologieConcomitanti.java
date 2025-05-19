package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoPatologiaConcomitante;
import com.univr.glicontrol.dao.AccessoPatologiaConcomitanteImpl;

import java.sql.Date;
import java.util.List;

public class GestionePatologieConcomitanti {
    private final Paziente paziente;
    private final AccessoPatologiaConcomitante apc = new AccessoPatologiaConcomitanteImpl();
    private List<PatologiaConcomitante> listaPatologieConcomitanti;

    public GestionePatologieConcomitanti(Paziente paziente) {
        this.paziente = paziente;
    }

    public List<PatologiaConcomitante> getListaPatologieConcomitanti() {
        listaPatologieConcomitanti = apc.recuperaPatologiePerPaziente(paziente.getIdUtente());
        return listaPatologieConcomitanti;
    }

    public void updateListaPatologieConcomitanti() {
        getListaPatologieConcomitanti();
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

    public boolean eliminaPatologiaConcomitante(int idPatologiaConcomitante) {
        return apc.deletePatologiaConcomitante(idPatologiaConcomitante);
    }

}
