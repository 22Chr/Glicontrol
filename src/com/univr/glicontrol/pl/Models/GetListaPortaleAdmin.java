package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.util.List;

public class GetListaPortaleAdmin {

    public List<Medico> getListaMediciPortaleAdmin() {
        ListaMedici listaMedici = new ListaMedici();
        return listaMedici.getListaMedici();
    }

    public List<Paziente> getListaPazientiPortaleAdmin() {
        ListaPazienti listaPazienti = new ListaPazienti();
        return listaPazienti.getListaPazienti();
    }

    //modificare in modo da ritornare solo nome e cognome di ogni medico e paziente
}
