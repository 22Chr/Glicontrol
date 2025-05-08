package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.sql.Date;

public class AggiornaPaziente {
    private final Paziente paziente;

    public AggiornaPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public Paziente updatePaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, double peso) {
        AccessoListaUtenti modificaPaziente = new AccessoListaUtentiImpl();
        return modificaPaziente.updatePaziente(paziente.getIdUtente(), codiceFiscale, nome, cognome, password, medico, nascita, sesso, email, allergie, peso) ?
                new Paziente(paziente.getIdUtente(), codiceFiscale, nome, cognome, "PAZIENTE", medico, nascita, sesso, email, allergie, peso) :
                null;
    }
}
