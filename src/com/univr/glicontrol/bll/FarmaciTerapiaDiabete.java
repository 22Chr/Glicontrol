package com.univr.glicontrol.bll;

public class FarmaciTerapiaDiabete extends FarmaciTerapia {
    private final int idTerapiaDiabete;

    public FarmaciTerapiaDiabete(int idFarmaciTerapia, int idFarmaco, int idTerapiaDiabete) {
        super(idFarmaciTerapia, idFarmaco);
        this.idTerapiaDiabete = idTerapiaDiabete;
    }

    public int getIdTerapiaDiabete() {
        return idTerapiaDiabete;
    }
}
