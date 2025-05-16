package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Pasto;

import java.sql.Time;
import java.util.List;

public interface AccessoPasti {
    List<Pasto> recuperaPastiPerUtente(int idUtente);
    boolean insertPasto(int idPaziente, String nomePasto, Time orario);
    boolean deletePasto(int idPasto);
    boolean updatePasto(Pasto pasto);
}
