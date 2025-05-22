package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.FarmaciTerapiaDiabete;
import com.univr.glicontrol.bll.Farmaco;

import java.util.List;

public interface AccessoFarmaciTerapiaDiabete {
    List<Farmaco> getListaFarmaciPerTerapiaDiabete(int idTerapiaDiabete);
    List<FarmaciTerapiaDiabete> getListaCompletaFarmaciTerapiaDiabete(int idTerapiaDiabete);
    boolean insertFarmaciTerapiaDiabete(int idTerapiaDiabete, List<Farmaco> farmaci);
    boolean deleteFarmaciTerapiaDiabete(int idTerapiaDiabete, int idFarmaco);
}
