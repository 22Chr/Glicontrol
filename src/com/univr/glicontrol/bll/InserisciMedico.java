package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;
import jakarta.mail.MessagingException;

public class InserisciMedico {
    
    public boolean insertMedico(String codiceFiscale, String nome, String cognome, String email, String password) {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        return accessoListaUtenti.insertNuovoMedico(codiceFiscale, nome, cognome, email, password);
    }

    public int inviaCredenzialiMedico(String email, String pwd) throws MessagingException {
        MailService ms = new MailService();
        int status;
        try {
            ms.sendEmail(email, "CREDENZIALI DI ACCESSO AL PORTALE MEDICO GLICONTROL", "Puoi ora accedere al portale inserendo il tuo codice fiscale come username e la seguente password: " + pwd);
            status = 1;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            status = -1;
        }
        return status;
    }
}
