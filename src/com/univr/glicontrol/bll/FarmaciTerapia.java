package com.univr.glicontrol.bll;

public abstract class FarmaciTerapia {
    private final int idFarmaciTerapia;
    private int idFarmaco;

    public FarmaciTerapia(int idFarmaciTerapia, int idFarmaco) {
        this.idFarmaciTerapia = idFarmaciTerapia;
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

}
