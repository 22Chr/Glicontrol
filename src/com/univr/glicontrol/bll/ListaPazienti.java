package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.util.List;

public class ListaPazienti {
    private List<Paziente> listaPazienti;

    public ListaPazienti() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        listaPazienti = accessoListaUtenti.recuperaTuttiIPazienti();
    }

    Paziente ottieniPazientePerId(int id) {
        for (Paziente p : listaPazienti) {
            if (p.getIdUtente() == id) {
                return p;
            }
        }
        return null;
    }

    //aggiungere poi altri metodi utili
}
