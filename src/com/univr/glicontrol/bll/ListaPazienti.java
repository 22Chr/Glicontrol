package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.util.List;

public class ListaPazienti {
    private List<Paziente> listaPazienti;
    private final AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
    private final List<Paziente> listaPazientiRecuperati = accessoListaUtenti.recuperaTuttiIPazienti();

    public ListaPazienti() {
        listaPazienti = listaPazientiRecuperati;
    }

    Paziente ottieniPazientePerId(int id) {
        for (Paziente p : listaPazienti) {
            if (p.getIdUtente() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Paziente> getListaPazienti() {
        return listaPazienti;
    }

    //aggiungere poi altri metodi utili
}
