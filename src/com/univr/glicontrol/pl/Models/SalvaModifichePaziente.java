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

    public boolean pazienteAggiornato() {
        return aggiornaPaziente.updatePaziente(paz.getCodiceFiscale(), paz.getNome(), paz.getCognome(),
                paz.getPassword(), paz.getMedicoRiferimento(), paz.getDataNascita(),paz.getSesso(), paz.getEmail(),
                paz.getAllergie(), paz.getPeso()) != null;
    }
}
