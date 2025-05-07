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

    public int ottieniIdPerMedico(Medico m) {
        if (listaMedici.contains(m)) {
            return m.getIdUtente();
        }

        return -1;
    }

    public List<Medico> getListaMedici() {
        return listaMedici;
    }

    //aggiungere metodi a seconda delle necessità
}
