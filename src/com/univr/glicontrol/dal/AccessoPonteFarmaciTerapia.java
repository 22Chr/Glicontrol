package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.PonteFarmaciTerapia;
import com.univr.glicontrol.bll.Farmaco;

import java.sql.Connection;
import java.util.List;

public interface AccessoPonteFarmaciTerapia {
    List<Farmaco> getListaFarmaciPerTerapia(int idTerapiaDiabete);
    List<PonteFarmaciTerapia> getListaCompletaFarmaciTerapia(int idTerapiaDiabete);
    boolean insertFarmaciTerapia(Connection conn, int idTerapiaDiabete, List<Farmaco> farmaci);
    boolean deleteFarmaciTerapia(Connection conn, int idTerapiaDiabete, int idFarmaco);
}
