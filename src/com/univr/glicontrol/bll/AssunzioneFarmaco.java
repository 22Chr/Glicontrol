package com.univr.glicontrol.bll;

import java.sql.Date;
import java.sql.Time;

public class AssunzioneFarmaco {
    private final int idAssunzioneFarmaco;
    private final int idPaziente;
    private final int idFarmaco;
    private final Date data;
    private final Time ora;
    private final float dose;

    public AssunzioneFarmaco(int idAssunzioneFarmaco, int idPaziente, int idFarmaco, Date data, Time ora, float dose) {
        this.idAssunzioneFarmaco = idAssunzioneFarmaco;
        this.idPaziente = idPaziente;
        this.idFarmaco = idFarmaco;
        this.data = data;
        this.ora = ora;
        this.dose = dose;
    }

    public int getIdAssunzioneFarmaco() {
        return idAssunzioneFarmaco;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public int getIdFarmaco() {
        return idFarmaco;
    }

    public Date getData() {
        return data;
    }

    public Time getOra() {
        return ora;
    }

    public float getDose() {
        return dose;
    }
}
