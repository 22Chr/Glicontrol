package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.sql.Date;
import java.util.List;


public interface AccessoListaUtenti {
    List<Medico> recuperaTuttiIMedici();
    List<Paziente> recuperaTuttiIPazienti();
    boolean updateMedico(int idMedico, String codiceFiscale, String nome, String cognome, String password, String email);
    boolean updatePaziente(int idPaziente, String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, float altezza, float peso, int primoAccesso);
    boolean insertNuovoMedico(String codiceFiscale, String nome, String cognome, String email, String password);
    boolean insertNuovoPaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, int primoAccesso);
    boolean deleteMedico(int idMedico);
    boolean deletePaziente(int idPaziente);
}
