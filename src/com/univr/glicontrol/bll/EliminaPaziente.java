package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class EliminaPaziente {

    private final Paziente paziente;

    public EliminaPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public boolean deletePaziente() {
        return new AccessoListaUtentiImpl().deletePaziente(paziente.getIdUtente());
    }

    public boolean notificaEliminazionePaziente(String email) {
        MailService ms = new MailService();
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
