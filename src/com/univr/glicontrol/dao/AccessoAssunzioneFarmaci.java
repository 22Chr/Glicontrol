package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.AssunzioneFarmaco;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface AccessoAssunzioneFarmaci {
    List<AssunzioneFarmaco> getListaFarmaciAssunti(int idPaziente);
    boolean insertAssunzioneFarmaci(int idPaziente, int idFarmaco, Date data, Time ora, float dose);
    boolean deleteAssunzioneFarmaco(int idAssunzioneFarmaco);
}
