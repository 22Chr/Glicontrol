package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;
import jakarta.mail.MessagingException;

import java.util.List;

public class GestioneMedici {
    private List<Medico> medici;
    private final AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();

    private GestioneMedici() {
        medici = accessoListaUtenti.recuperaTuttiIMedici();
    }

    private static class Holder {
        private static final GestioneMedici INSTANCE = new GestioneMedici();
    }

    public static GestioneMedici getInstance() {
        return Holder.INSTANCE;
    }

    private void aggiornaListaMedici() {
        medici.clear();
        medici = accessoListaUtenti.recuperaTuttiIMedici();
    }

    public int getIdPerMedico(Medico medico) {
        for (Medico m : medici) {
            if (m.getIdUtente() == medico.getIdUtente()) {
                return m.getIdUtente();
            }
        }
        return -1;
    }

    public Medico getMedicoPerId(int idMedico) {
        for (Medico m : medici) {
            if (m.getIdUtente() == idMedico) {
                return m;
            }
        }
        return null;
    }

    public List<Medico> getListaMedici() {
        return medici;
    }

    public boolean medicoGiaPresente(String codiceFiscale) {
        for (Medico m : medici) {
            if (m.getCodiceFiscale().equals(codiceFiscale)) {
                return true;
            }
        }
        return false;
    }

    // AGGIORNA MEDICO
    public boolean aggiornaMedico(Medico medico) {
        boolean status = accessoListaUtenti.updateMedico(medico.getIdUtente(), medico.getCodiceFiscale(), medico.getNome(), medico.getCognome(), medico.getPassword(), medico.getEmail());

        if(status) {
            aggiornaListaMedici();
        }

        return status;
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

    public int eliminaMedico(Medico medico) {
        if (GestionePazienti.getInstance().getListaPazientiPerMedico(medico.getIdUtente()) != null) {
            return -1;
        }

        boolean status = accessoListaUtenti.deleteMedico(medico.getIdUtente());
        if(status) {
            aggiornaListaMedici();
        }

        return status ? 1 : 0;
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

    public int inserisciMedico(String codiceFiscale, String nome, String cognome, String email, String password) {

        if (GestioneMedici.getInstance().medicoGiaPresente(codiceFiscale)) {
            return -1;
        }

        boolean status = accessoListaUtenti.insertNuovoMedico(codiceFiscale, nome, cognome, email, password);
        if(status) {
            aggiornaListaMedici();
        }

        return status ? 1 : 0;
    }

    public boolean inviaCredenzialiMedico(String email, String pwd) {
        ServizioNotifiche ms = new ServizioNotifiche();
        boolean status;
        try {
            ms.sendEmail(email, "Benvenuto su Glicontrol", "Benvenuto nel sistema medico Glicontrol, l'alleato numero uno per la gestione del diabete.\n\nPuoi ora accedere al portale inserendo il tuo codice fiscale come username e la seguente password: " + pwd + ".\n\n\nGlicontrol Medical System");
            status = true;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            status = false;
        }
        return status;
    }
}
