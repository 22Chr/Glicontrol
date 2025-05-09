package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.AggiornaMedico;
import com.univr.glicontrol.bll.Medico;

public class SalvaModificheMedico {

    private final Medico med;
    private final AggiornaMedico aggiornaMedico;

    public SalvaModificheMedico(Medico medico) {
        this.med = medico;
        aggiornaMedico = new AggiornaMedico(med);
    }

    public boolean medicoAggiornato() {
        return aggiornaMedico.updateMedico(med.getCodiceFiscale(), med.getNome(), med.getCognome(), med.getPassword(), med.getEmail()) != null;
    }
}
