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

    public Medico ottieniMedicoPerId(int idMedico) {
        for (Medico m : listaMedici) {
            if (m.getIdUtente() == idMedico) {
                return m;
            }
        }
        return null;
    }

    public int ottienIdPerMedico(Medico med) {
        for (Medico m : listaMedici) {
            if (m.getIdUtente() == med.getIdUtente()) {
                return m.getIdUtente();
            }
        }
        return -1;
    }

    public List<Medico> getListaMedici() {
        return listaMedici;
    }

    //aggiungere metodi a seconda delle necessità
}
