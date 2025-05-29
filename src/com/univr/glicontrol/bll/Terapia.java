package com.univr.glicontrol.bll;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

public interface Terapia {
    String getNome();

    List<FarmacoTerapia> getListaFarmaciTerapia();
    void setListaFarmaciTerapia(List<FarmacoTerapia> farmaci);

    int getIdPaziente();

    int getIdPatologiaConcomitante();

    int getIdMedicoUltimaModifica();
    void setIdMedicoUltimaModifica(int idMedico);

    Date getDataInizio();

    Date getDataFine();
    void setDataFine(Date datafine);

    float getDosaggioPerFarmaco(String nomeFarmaco);

    String getFrequenzaPerFarmaco(String nomeFarmaco);

    String getOrarioPerFarmaco(String nomeFarmaco);
}
