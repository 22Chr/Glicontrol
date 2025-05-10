package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;
import jakarta.mail.MessagingException;

public class InserisciMedico {
    
    public int insertMedico(String codiceFiscale, String nome, String cognome, String email, String password) {
        ListaMedici listaMedici = new ListaMedici();

        if (listaMedici.medicoEsiste(codiceFiscale)) {
            return -1;
        }

        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        return accessoListaUtenti.insertNuovoMedico(codiceFiscale, nome, cognome, email, password) ? 1 : 0;
    }

    public boolean inviaCredenzialiMedico(String email, String pwd) {
        MailService ms = new MailService();
        boolean status;
        try {
            ms.sendEmail(email, "CREDENZIALI DI ACCESSO AL PORTALE MEDICO GLICONTROL", "Puoi ora accedere al portale inserendo il tuo codice fiscale come username e la seguente password: " + pwd);
            status = true;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            status = false;
        }
        return status;
    }
}
