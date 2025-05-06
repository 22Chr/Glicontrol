package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.sql.Date;
import java.util.List;


public interface AccessoListaUtenti {
    List<Medico> recuperaTuttiIMedici();
    List<Paziente> recuperaTuttiIPazienti();
    boolean updateListaMedici();
    boolean updateListaPazienti();
    boolean insertNuovoMedico(String codiceFiscale, String nome, String cognome, String email, String password);
    boolean insertNuovoPaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, int peso);
    boolean deleteMedico();
    boolean deletePaziente();
}
