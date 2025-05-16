package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetListaUtenti {

    Map<String, Medico> mappaMedici = new HashMap<>();
    public List<String> getListaMediciPortaleAdmin() {
        ListaMedici listaMedici = new ListaMedici();
        // Mappa usata per mantenere l'associazione tra referenza del medico ottenuta facendo click sul nome del medico a livello UI
        // e l'oggetto Medico corrispondente, in modo da poter successivamente recuperare l'id
        String nomeMedico;

        List<String> referenzeMedici = new ArrayList<>();
        for (Medico m : listaMedici.getListaCompletaMedici()) {
            nomeMedico = m.getCognome() + " " + m.getNome() + " - " + m.getCodiceFiscale();
            referenzeMedici.add(nomeMedico);
            mappaMedici.put(nomeMedico, m);
        }

        return referenzeMedici;
    }

    public void updateListaMediciPortaleAdmin() {
        getListaMediciPortaleAdmin();
    }

    Map<String, Paziente> mappaPazienti = new HashMap<>();
    public List<String> getListaPazientiPortaleAdmin() {
        ListaPazienti listaPazienti = new ListaPazienti();
        // Mappa usata analogamente per ottenere l'id del paziente
        String nomePaziente;

        List<String> referenzePazienti = new ArrayList<>();
        for (Paziente p : listaPazienti.getListaCompletaPazienti()) {
            nomePaziente = p.getCognome() + " " + p.getNome() + " - " + p.getCodiceFiscale();
            referenzePazienti.add(nomePaziente);
            mappaPazienti.put(nomePaziente, p);
        }

        return referenzePazienti;
    }

    public void updateListaPazientiPortaleAdmin() {
        getListaPazientiPortaleAdmin();
    }

    //aggiungere i metodi per ottenere l'id del medico e del paziente mediante associazione delle mappe
    //ritorna -1 in caso di fallimento, id in caso di successo
    public int getIdMedico(String referenceMedico) {
        int id = -1;
        Medico m = mappaMedici.get(referenceMedico);
        if (m != null) {
            id = m.getIdUtente();
        }

        return id;
    }

    public int getIdPaziente(String referencePaziente) {
        int id = -1;
        Paziente p = mappaPazienti.get(referencePaziente);
        if (p != null) {
            id = p.getIdUtente();
        }

        return id;
    }
}
