package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetListaPortaleAdmin {

    public List<String> getListaMediciPortaleAdmin() {
        ListaMedici listaMedici = new ListaMedici();
        // Mappa usata per mantenere l'associazione tra referenza del medico ottenuta facendo click sul nome del medico a livello UI
        // e l'oggetto Medico corrispondente, in modo da poter successivamente recuperare l'id
        Map<String, Medico> mappaMedici = new HashMap<>();
        String nomeMedico;

        List<String> referenzeMedici = new ArrayList<>();
        for (Medico m : listaMedici.getListaMedici()) {
            nomeMedico = m.getCognome() + " " + m.getNome() + " - " + m.getCodiceFiscale();
            referenzeMedici.add(nomeMedico);
            mappaMedici.put(nomeMedico, m);
        }

        return referenzeMedici;
    }

    public List<String> getListaPazientiPortaleAdmin() {
        ListaPazienti listaPazienti = new ListaPazienti();
        // Mappa usata analogamente per ottenere l'id del paziente
        Map<String, Paziente> mappaPazienti = new HashMap<>();
        String nomePaziente;

        List<String> referenzePazienti = new ArrayList<>();
        for (Paziente p : listaPazienti.getListaPazientiCompleta()) {
            nomePaziente = p.getCognome() + " " + p.getNome() + " - " + p.getCodiceFiscale();
            referenzePazienti.add(nomePaziente);
            mappaPazienti.put(nomePaziente, p);
        }

        return referenzePazienti;
    }
}
