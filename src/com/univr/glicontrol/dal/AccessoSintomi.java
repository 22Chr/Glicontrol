package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.Sintomo;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface AccessoSintomi {
    List<Sintomo> recuperaSintomiPerPaziente();
    boolean insertSintomo(String descrizione, Date data, Time ora);
    boolean deleteSintomo(int idSintomo);
}
