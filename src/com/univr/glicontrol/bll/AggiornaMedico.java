package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;
import jakarta.mail.MessagingException;

public class AggiornaMedico {
    private final Medico medico;

    public AggiornaMedico(Medico medico) {
        this.medico = medico;
    }

    public Medico updateMedico(String codiceFiscale, String nome, String cognome, String password, String email) {
        AccessoListaUtenti modificaMedico = new AccessoListaUtentiImpl();
        return modificaMedico.updateMedico(medico.getIdUtente(), codiceFiscale, nome, cognome, password, email) ?
                new Medico(medico.getIdUtente(), codiceFiscale, nome, cognome, password, "MEDICO", email) :
                null;
    }

    public boolean inviaCredenzialiAggiornateMedico(String email, String pwd) {
        MailService ms = new MailService();
        boolean status;
        try {
            ms.sendEmail(email, "MODIFICA CREDENZIALI DI ACCESSO AL PORTALE MEDICO GLICONTROL", "La tua password di accesso al portale è stata modificata con la seguente: " + pwd);
            status = true;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            status = false;
        }
        return status;
    }
}
