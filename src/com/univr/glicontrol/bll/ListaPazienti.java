package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoListaUtenti;
import com.univr.glicontrol.dao.AccessoListaUtentiImpl;

import java.util.ArrayList;
import java.util.List;

public class ListaPazienti {
    private List<Paziente> listaPazienti;
    private final AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();

    public ListaPazienti() {
        listaPazienti = accessoListaUtenti.recuperaTuttiIPazienti();
    }

    public Paziente ottieniPazientePerId(int idPaziente) {
        for (Paziente p : listaPazienti) {
            if (p.getIdUtente() == idPaziente) {
                return p;
            }
        }
        return null;
    }

    public List<Paziente> getListaCompletaPazienti() {
        aggiornaListaPazienti();
        return listaPazienti;
    }

    // Restituisce la lista di tutti i pazienti associati ad un dato medico
    public List<Paziente> getListaPazientiPerMedico(int idMedico) {
        List<Paziente> listaPazientiPerMedico = new ArrayList<>();
        listaPazientiPerMedico = null;
        for (Paziente p : listaPazienti) {
            if (p.getMedicoRiferimento() == idMedico) {
                assert false;
                listaPazientiPerMedico.add(p);
            }
        }
        return listaPazientiPerMedico;
    }

    // Verifica se il paziente esiste già nel sistema
    public boolean pazienteEsiste(String codiceFiscale) {
        for (Paziente p : listaPazienti) {
            if (p.getCodiceFiscale().equals(codiceFiscale)) {
                return true;
            }
        }
        return false;
    }

    // restituisce il paziente per codice fiscale
    public Paziente getPazientePerCodiceFiscale(String codiceFiscale) {
        for (Paziente p : listaPazienti) {
            if (p.getCodiceFiscale().equals(codiceFiscale)) {
                return p;
            }
        }
        return null;
    }

    // restituisce il paziente per id
    public Paziente getPazientePerId(int id) {
        for (Paziente p : listaPazienti) {
            if (p.getIdUtente() == id) {
                return p;
            }
        }
        return null;
    }

    public void aggiornaListaPazienti() {
        listaPazienti = accessoListaUtenti.recuperaTuttiIPazienti();
    }
}
