package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;

public class GetListaPortaleAdmin {

    public ListaMedici getListaMedici() {
        return new ListaMedici();
    }

    public ListaPazienti getListaPazienti() {
        return new ListaPazienti();
    }
}
