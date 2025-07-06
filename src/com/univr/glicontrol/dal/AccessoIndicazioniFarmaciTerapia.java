package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.IndicazioniFarmaciTerapia;

import java.sql.Connection;
import java.util.List;

public interface AccessoIndicazioniFarmaciTerapia {
    List<IndicazioniFarmaciTerapia> getListaIndicazioniFarmaci(int idTerapiaDiabeteAnnessa);
    boolean insertIndicazioniFarmaci(Connection conn, int idTerapiaDiabeteAnnessa, int idFarmaco, float dosaggio, String frequenza, String orari);
    boolean deleteIndicazioniFarmaci(Connection conn, int idIndicazioniFarmaci);
    boolean updateIndicazioniFarmaci(Connection conn, IndicazioniFarmaciTerapia indicazioniFarmaciTerapia);
}
