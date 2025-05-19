package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoSintomi;
import com.univr.glicontrol.dao.AccessoSintomiImpl;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class GestioneSintomi {

    private final List<Sintomo> sintomi;
    private Paziente paziente;
    private AccessoSintomi accessoSintomi;

    public GestioneSintomi(Paziente paziente) {
        this.paziente = paziente;
        accessoSintomi = new AccessoSintomiImpl(paziente);
        sintomi = accessoSintomi.recuperaSintomiPerPaziente();
    }

    public List<Sintomo> getSintomi() {
        return sintomi;
    }

    public boolean inserisciSintomo(String descrizione) {
        Date data = new Date(System.currentTimeMillis());
        Time ora = new Time(System.currentTimeMillis());
        return accessoSintomi.insertSintomo(descrizione, data, ora);
    }

    public boolean eliminaSintomo(int idSintomo) {
        return accessoSintomi.deleteSintomo(idSintomo);
    }
}
