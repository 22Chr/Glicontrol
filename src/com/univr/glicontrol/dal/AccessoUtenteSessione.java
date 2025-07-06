package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.Admin;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

public interface AccessoUtenteSessione {
    boolean verificaUtente(String codiceFiscale, String ruolo);
    Admin getAdmin(String codiceFiscale);
    Paziente getPaziente(String codiceFiscale);
    Medico getMedico(String codiceFiscale);
}
