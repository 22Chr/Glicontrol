package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class InserisciMedico {
    
    public boolean insertMedico(String codiceFiscale, String nome, String cognome, String email, String password) {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        return accessoListaUtenti.insertNuovoMedico(codiceFiscale, nome, cognome, email, password);
    }
}
