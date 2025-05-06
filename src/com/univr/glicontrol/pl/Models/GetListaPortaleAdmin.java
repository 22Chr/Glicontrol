package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.util.ArrayList;
import java.util.List;

public class GetListaPortaleAdmin {

    public List<String> getListaMediciPortaleAdmin() {
        ListaMedici listaMedici = new ListaMedici();
        String nomeMedico;

        List<String> referenzeMedici = new ArrayList<>();
        for (Medico m : listaMedici.getListaMedici()) {
            nomeMedico = m.getCognome() + " " + m.getNome() + " - " + m.getCodiceFiscale();
            referenzeMedici.add(nomeMedico);
        }

        return referenzeMedici;
    }

    public List<String> getListaPazientiPortaleAdmin() {
        ListaPazienti listaPazienti = new ListaPazienti();
        String nomePaziente;

        List<String> referenzePazienti = new ArrayList<>();
        for (Paziente p : listaPazienti.getListaPazienti()) {
            nomePaziente = p.getCognome() + " " + p.getNome() + " - " + p.getCodiceFiscale();
            referenzePazienti.add(nomePaziente);
        }

        return referenzePazienti;
    }
}
