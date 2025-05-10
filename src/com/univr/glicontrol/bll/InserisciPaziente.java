package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.sql.Date;

public class InserisciPaziente {

    public int insertPaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, double peso) {
        ListaPazienti listaPazienti = new ListaPazienti();
        if (listaPazienti.pazienteEsiste(codiceFiscale)) {
            return -1;
        }

        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        return accessoListaUtenti.insertNuovoPaziente(codiceFiscale, nome, cognome, password, medico, nascita, sesso, email, allergie, peso) ? 1 : 0;
    }

    public boolean inviaCredenzialiPaziente(String email, String pwd) {
        MailService ms = new MailService();
        boolean status;
        try {
            ms.sendEmail(email, "Benvenuto su Glicontrol", "Benvenuto nel sistema medico Glicontrol, l'alleato numero uno per la gestione del diabete.\nPuoi ora accedere al portale inserendo il tuo codice fiscale come username e la seguente password: " + pwd + "\n\nGlicontrol Medical System\n\nCordiali saluti,\n\nGlicontrol Medical System");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }
        return status;
    }
}
