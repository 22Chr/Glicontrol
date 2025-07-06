package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.Farmaco;

import java.util.List;

public interface AccessoFarmaci {
    List<Farmaco> recuperaTuttiFarmaci();
}
