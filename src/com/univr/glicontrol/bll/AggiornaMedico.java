package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;
import jakarta.mail.MessagingException;

public class AggiornaMedico {
    private final Medico medico;

    public AggiornaMedico(Medico medico) {
        this.medico = medico;
    }

    public boolean updateMedico() {
        AccessoListaUtenti modificaMedico = new AccessoListaUtentiImpl();
        return modificaMedico.updateMedico(medico.getIdUtente(), medico.getCodiceFiscale(), medico.getNome(), medico.getCognome(), medico.getPassword(), medico.getEmail());
    }

    public boolean inviaCredenzialiAggiornateMedico(String email, String pwd) {
        ServizioNotifiche ms = new ServizioNotifiche();
        boolean status;
        try {
            ms.sendEmail(email, "Modifica accesso Glicontrol", "La tua password di accesso al portale è stata modificata.\n\nUtilizza la nuova password: " + pwd + " per continuare ad utilizzare il tuo miglior alleato contro il diabete.\n\n\nGlicontrol Medical System");
            status = true;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            status = false;
        }
        return status;
    }
}
