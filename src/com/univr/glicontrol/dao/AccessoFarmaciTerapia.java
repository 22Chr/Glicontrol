package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.FarmaciTerapia;
import com.univr.glicontrol.bll.Farmaco;

import java.util.List;

public interface AccessoFarmaciTerapia {
    List<Farmaco> getListaFarmaciPerTerapia(int idTerapiaDiabete);
    List<FarmaciTerapia> getListaCompletaFarmaciTerapia(int idTerapiaDiabete);
    boolean insertFarmaciTerapia(int idTerapiaDiabete, List<Farmaco> farmaci);
    boolean deleteFarmaciTerapia(int idTerapiaDiabete, int idFarmaco);
}
