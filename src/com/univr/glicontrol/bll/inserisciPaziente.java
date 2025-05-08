package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.sql.Date;

public class inserisciPaziente {

    public boolean insertPaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, double peso) {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        return accessoListaUtenti.insertNuovoPaziente(codiceFiscale, nome, cognome, password, medico, nascita, sesso, email, allergie, peso);
    }
}
