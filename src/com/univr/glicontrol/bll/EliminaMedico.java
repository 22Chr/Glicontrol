package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class EliminaMedico {
    private final Medico medico;

    public EliminaMedico(Medico medico) {
        this.medico = medico;
    }

    public boolean deleteMedico() {
        return new AccessoListaUtentiImpl().deleteMedico(medico.getIdUtente());
    }

    public boolean notificaEliminazioneMedico(String email) {
        MailService ms = new MailService();
        boolean status;
        try {
            ms.sendEmail(email, "Revoca accesso Glicontrol", "Il tuo account di accesso al portale medico è stato eliminato e non ti sarà più possibile accedere al sistema.");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }

        return status;
    }
}
