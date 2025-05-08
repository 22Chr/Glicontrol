package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.AggiornaPaziente;
import com.univr.glicontrol.bll.Paziente;

public class SalvaModifichePaziente {
    private final Paziente paz;
    private final AggiornaPaziente aggiornaPaziente;

    public SalvaModifichePaziente(Paziente paziente) {
        this.paz = paziente;
        aggiornaPaziente = new AggiornaPaziente(paz);
    }

    public boolean pazienteAggiornato(String pwd) {
        return aggiornaPaziente.updatePaziente(paz.getCodiceFiscale(), paz.getNome(), paz.getCognome(),
                pwd, paz.getMedicoRiferimento(), paz.getDataNascita(),paz.getSesso(), paz.getEmail(),
                paz.getAllergie(), paz.getPeso()) != null;
    }
}
