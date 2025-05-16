package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

public class EliminaMedico {
    private final Medico medico;

    public EliminaMedico(Medico medico) {
        this.medico = medico;
    }

    public int deleteMedico() {
        ListaPazienti listaPazienti = new ListaPazienti();
        if (listaPazienti.getListaPazientiPerMedico(medico.getIdUtente()) != null) {
            return -1;
        }

        return new AccessoListaUtentiImpl().deleteMedico(medico.getIdUtente()) ? 1 : 0;
    }

    public boolean notificaEliminazioneMedico(String email) {
        ServizioNotifiche ms = new ServizioNotifiche();
        boolean status;
        try {
            ms.sendEmail(email, "Revoca accesso Glicontrol", "Il tuo account di accesso al portale medico è stato eliminato e non ti sarà più possibile accedere al sistema. \n\nGlicontrol Medical System");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }

        return status;
    }
}
