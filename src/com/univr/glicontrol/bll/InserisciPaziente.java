package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.sql.Date;

public class InserisciPaziente {

    private final ServizioNotificheMail ms = new ServizioNotificheMail();

    public int insertPaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, float peso) {
        int status;
        ListaPazienti listaPazienti = new ListaPazienti();
        if (listaPazienti.pazienteEsiste(codiceFiscale)) {
            return -1;
        }

        // Imposta il valore di default a 1 per indicare che il paziente effettuerà il suo primo accesso al portale post inserimento
        int primoAccesso = 1;

        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        status = accessoListaUtenti.insertNuovoPaziente(codiceFiscale, nome, cognome, password, medico, nascita, sesso, email, allergie, peso, primoAccesso) ? 1 : 0;
        if (status == 1) {
            GestioneFattoriRischio inizializzaFattoriPaziente = new GestioneFattoriRischio();
            status = inizializzaFattoriPaziente.inserisciFattoriRischi(codiceFiscale) ? 1 : 0;
        }

        return status;
    }

    public boolean inviaCredenzialiPaziente(String email, String pwd) {
        boolean status;
        try {
            ms.sendEmail(email, "Benvenuto su Glicontrol", "Benvenuto nel sistema medico Glicontrol, l'alleato numero uno per la gestione del diabete.\n\nPuoi ora accedere al portale inserendo il tuo codice fiscale come username e la seguente password: " + pwd + ".\n\n\nGlicontrol Medical System");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }

        return status;
    }

    public void informaMedicoAssociato(String nuovoPaziente, String mailMedico) {
        try {
            ms.sendEmail(mailMedico, "Hai un nuovo paziente", "Un nuovo paziente registrato su Glicontrol ti è stato affidato in cura.\n\nIdentificativo paziente:\n" + nuovoPaziente + "\n\n\nGlicontrol Medical System");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
