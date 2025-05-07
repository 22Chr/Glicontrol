package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class AggiornaMedico {
    private Medico medico;

    public AggiornaMedico(Medico medico) {
        this.medico = medico;
    }

    public Medico updateMedico(String codiceFiscale, String nome, String cognome, String password, String email) {
        AccessoListaUtenti modificaMedico = new AccessoListaUtentiImpl();
        return modificaMedico.updateMedico(medico.getIdUtente(), codiceFiscale, nome, cognome, password, email) ?
                new Medico(medico.getIdUtente(), codiceFiscale, nome, cognome, "MEDICO", email) :
                null;
    }
}
