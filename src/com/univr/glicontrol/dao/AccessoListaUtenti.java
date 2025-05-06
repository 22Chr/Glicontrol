package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import java.util.List;


public interface AccessoListaUtenti {
    List<Medico> recuperaTuttiIMedici();
    List<Paziente> recuperaTuttiIPazienti();
    boolean updateListaMedici();
    boolean updateListaPazienti();
    boolean insertNuovoMedico();
    boolean insertNuovoPaziente();
    boolean deleteMedico();
    boolean deletePaziente();
}
