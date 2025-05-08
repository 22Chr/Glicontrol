package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class EliminaMedico {
    private final Medico medico;

    public EliminaMedico(Medico medico) {
        this.medico = medico;
    }

    public boolean deleteMedico() {
        return new AccessoListaUtentiImpl().deleteMedico(medico.getIdUtente());
    }
}
