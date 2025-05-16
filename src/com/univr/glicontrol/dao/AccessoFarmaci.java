package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Farmaco;

import java.util.List;

public interface AccessoFarmaci {
    List<Farmaco> recuperaTuttiFarmaci();
    boolean insertFarmaco(Farmaco farmaco);
    boolean deleteFarmaco(int idFarmaco);
    boolean updateFarmaco(Farmaco farmaco);
}
