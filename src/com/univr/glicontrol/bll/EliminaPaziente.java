package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class EliminaPaziente {

    private final Paziente paziente;

    public EliminaPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public boolean deletePaziente() {
        boolean success;

        // Veirifica eliminazione fattori di rischio associati al paziente
        success = new GestioneFattoriRischio().eliminaFattoriRischio(paziente.getIdUtente());
        if (success) {
            // In caso di successo procede con l'eliminazione del paziente
            success = new AccessoListaUtentiImpl().deletePaziente(paziente.getIdUtente());
        }

        return success;
    }

    public boolean notificaEliminazionePaziente(String email) {
        ServizioNotificheMail ms = new ServizioNotificheMail();
        boolean status;
        try {
            ms.sendEmail(email, "Revoca accesso Glicontrol", "Il tuo account di accesso al portale paziente è stato eliminato e non ti sarà più possibile accedere al sistema.\n\nGlicontrol Medical System");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }

        return status;
    }
}
