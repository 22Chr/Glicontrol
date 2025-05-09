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
            ms.sendEmail(email, "ELIMINAZIONE ACCOUNT", "Il tuo account di accesso al portale medico è stato eliminato e non ti sarà più possibile accedere al sistema.");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }

        return status;
    }
}
