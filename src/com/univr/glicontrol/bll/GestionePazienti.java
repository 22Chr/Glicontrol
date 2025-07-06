package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoListaUtenti;
import com.univr.glicontrol.dal.AccessoListaUtentiImpl;

import java.sql.Date;
import java.util.List;

public class GestionePazienti {
    private List<Paziente> pazienti;
    private final AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();

    private GestionePazienti() {
        pazienti = accessoListaUtenti.recuperaTuttiIPazienti();
    }

    private static class HOLDER {
        private static final GestionePazienti INSTANCE = new GestionePazienti();
    }

    public static GestionePazienti getInstance() {
        return HOLDER.INSTANCE;
    }

    private void aggiornaListaPazienti() {
        pazienti.clear();
        pazienti = accessoListaUtenti.recuperaTuttiIPazienti();
    }

    public List<Paziente> getListaPazienti() {
        return pazienti;
    }

    public List<Paziente> getListaPazientiPerMedico(int idMedico) {
        List<Paziente> listaPazientiPerMedico = null;
        for (Paziente p : pazienti) {
            if (p.getMedicoRiferimento() == idMedico) {
                assert false;
                listaPazientiPerMedico.add(p);
            }
        }
        return listaPazientiPerMedico;
    }

    public boolean pazienteGiaPresente(String codiceFiscale) {
        for (Paziente p : pazienti) {
            if (p.getCodiceFiscale().equals(codiceFiscale)) {
                return true;
            }
        }
        return false;
    }

    public Paziente getPazientePerCodiceFiscale(String codiceFiscale) {
        aggiornaListaPazienti();

        for (Paziente p : pazienti) {
            if (p.getCodiceFiscale().equals(codiceFiscale)) {
                return p;
            }
        }
        return null;
    }

    public Paziente getPazientePerId(int idPaziente) {
        for (Paziente p : pazienti) {
            if (p.getIdUtente() == idPaziente) {
                return p;
            }
        }
        return null;
    }


    // AGGIORNA PAZIENTE
    public boolean aggiornaPaziente(Paziente paziente) {
        AccessoListaUtenti modificaPaziente = new AccessoListaUtentiImpl();
        boolean status = modificaPaziente.updatePaziente(paziente.getIdUtente(), paziente.getCodiceFiscale(), paziente.getNome(), paziente.getCognome(), paziente.getPassword(), paziente.getMedicoRiferimento(), paziente.getDataNascita(), paziente.getSesso(), paziente.getEmail(), paziente.getAllergie(), paziente.getAltezza(), paziente.getPeso(), paziente.getPrimoAccesso());

        if(status) {
            aggiornaListaPazienti();
        }

        return status;
    }

    public boolean inviaCredenzialiAggiornatePaziente(String email, String pwd) {
        boolean status;
        try {
            ServizioNotifiche.getInstance().sendEmail(email, "Modifica accesso Glicontrol", "La tua password di accesso al portale è stata modificata.\n\nUtilizza la nuova password: " + pwd + " per continuare ad utilizzare il tuo miglior alleato contro il diabete.\n\n\nGlicontrol Medical System");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }
        return status;
    }


    // INSERISCI PAZIENTE
    public int inserisciPaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie) {
        int status;
        if (pazienteGiaPresente(codiceFiscale)) {
            return -1;
        }

        // Imposta il valore di default a 1 per indicare che il paziente effettuerà il suo primo accesso al portale post inserimento
        int primoAccesso = 1;

        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        status = accessoListaUtenti.insertNuovoPaziente(codiceFiscale, nome, cognome, password, medico, nascita, sesso, email, allergie, primoAccesso) ? 1 : 0;
        if (status == 1) {
            boolean success = new GestioneFattoriRischio().inserisciFattoriRischi(codiceFiscale);

            if (success) {
                aggiornaListaPazienti();
            }

            status = success ? 1 : 0;
        }

        return status;
    }

    public boolean inviaCredenzialiPaziente(String email, String pwd) {
        boolean status;
        try {
            ServizioNotifiche.getInstance().sendEmail(email, "Benvenuto su Glicontrol", "Benvenuto nel sistema medico Glicontrol, l'alleato numero uno per la gestione del diabete.\n\nPuoi ora accedere al portale inserendo il tuo codice fiscale come username e la seguente password: " + pwd + ".\n\n\nGlicontrol Medical System");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }

        return status;
    }

    public void informaMedicoAssociato(String nuovoPaziente, String mailMedico) {

        try {
            ServizioNotifiche.getInstance().sendEmail(mailMedico, "Hai un nuovo paziente", "Un nuovo paziente registrato su Glicontrol ti è stato affidato in cura.\n\nIdentificativo paziente:\n" + nuovoPaziente + "\n\n\nGlicontrol Medical System");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    // ELIMINA PAZIENTE
    public boolean eliminaPaziente(Paziente paziente) {
        boolean success;

        // Veirifica eliminazione fattori di rischio associati al paziente
        success = new GestioneFattoriRischio().eliminaFattoriRischio(paziente.getIdUtente());
        if (success) {
            // In caso di successo procede con l'eliminazione del paziente
            success = new AccessoListaUtentiImpl().deletePaziente(paziente.getIdUtente());
        }

        if (success) {
            aggiornaListaPazienti();
        }

        return success;
    }

    public boolean notificaEliminazionePaziente(String email) {
        boolean status;
        try {
            ServizioNotifiche.getInstance().sendEmail(email, "Revoca accesso Glicontrol", "Il tuo account di accesso al portale paziente è stato eliminato e non ti sarà più possibile accedere al sistema.\n\nGlicontrol Medical System");
            status = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = false;
        }

        return status;
    }
}
