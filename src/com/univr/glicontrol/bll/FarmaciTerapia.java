package com.univr.glicontrol.bll;

public class FarmaciTerapia {
    private final int idFarmaciTerapia;
    private final int idTerapia;
    private int idFarmaco;

    public FarmaciTerapia(int idFarmaciTerapia, int idTerapia, int idFarmaco) {
        this.idFarmaciTerapia = idFarmaciTerapia;
        this.idTerapia = idTerapia;
        this.idFarmaco = idFarmaco;
    }

    public int getIdFarmaciTerapia() {
        return idFarmaciTerapia;
    }

    public int getIdFarmaco() {
        return idFarmaco;
    }
    public void setIdFarmaco(int idFarmaco) {
        this.idFarmaco = idFarmaco;
    }

    public int getIdTerapia() {
        return idTerapia;
    }
}
