package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.util.List;

public class ListaMedici {
    private List<Medico> listaMedici;
    private final AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
    private final List<Medico> listaMediciRecuperati = accessoListaUtenti.recuperaTuttiIMedici();

    public ListaMedici() {
        listaMedici = listaMediciRecuperati;
    }

    public Medico getMedicoPerId(int idMedico) {
        for (Medico m : listaMedici) {
            if (m.getIdUtente() == idMedico) {
                return m;
            }
        }
        return null;
    }

    public int getIdPerMedico(Medico med) {
        for (Medico m : listaMedici) {
            if (m.getIdUtente() == med.getIdUtente()) {
                return m.getIdUtente();
            }
        }
        return -1;
    }

    public List<Medico> getListaCompletaMedici() {
        return listaMedici;
    }

    // Verifica se il medico esiste già nel sistema
    public boolean medicoEsiste(String codiceFiscale) {
        for (Medico m : listaMedici) {
            if (m.getCodiceFiscale().equals(codiceFiscale)) {
                return true;
            }
        }
        return false;
    }

    //aggiungere metodi a seconda delle necessità
}
