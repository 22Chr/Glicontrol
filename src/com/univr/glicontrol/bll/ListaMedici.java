package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.util.List;

public class ListaMedici {
    private List<Medico> listaMedici;

    public ListaMedici() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        List<Medico> listaMedici = accessoListaUtenti.recuperaTuttiIMedici();
    }

    Medico ottieniMedicoPerId(int id) {
        for (Medico m : listaMedici) {
            if (m.getIdUtente() == id) {
                return m;
            }
        }
        return null;
    }

    //aggiungere metodi a seconda delle necessità
}
