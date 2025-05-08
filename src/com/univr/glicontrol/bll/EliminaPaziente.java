package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class EliminaPaziente {

    private final Paziente paziente;

    public EliminaPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public boolean deletePaziente() {
        return new AccessoListaUtentiImpl().deletePaziente(paziente.getIdUtente());
    }
}
