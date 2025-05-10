package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.sql.Date;

public class AggiornaPaziente {
    private final Paziente paziente;

    public AggiornaPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public boolean updatePaziente() {
        AccessoListaUtenti modificaPaziente = new AccessoListaUtentiImpl();
        return modificaPaziente.updatePaziente(paziente.getIdUtente(), paziente.getCodiceFiscale(), paziente.getNome(), paziente.getCognome(), paziente.getPassword(), paziente.getMedicoRiferimento(), paziente.getDataNascita(), paziente.getSesso(), paziente.getEmail(), paziente.getAllergie(), paziente.getPeso());
    }

    public boolean inviaCredenzialiAggiornatePaziente(String email, String pwd) {
        MailService ms = new MailService();
        boolean status;
        try {
            ms.sendEmail(email, "MODIFICA CREDENZIALI DI ACCESSO AL PORTALE PAZIENTE GLICONTROL", "La tua password di accesso al portale è stata modificata con la seguente: " + pwd);
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }
        return status;
    }
}
