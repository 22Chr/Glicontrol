package com.univr.glicontrol.bll;

import java.sql.Date;
import java.util.List;

public interface Terapia {
    String getNome();

    List<Farmaco> getListaFarmaciTerapia();
    void setListaFarmaciTerapia(List<Farmaco> farmaci);

    int getIdPaziente();

    int getIdPatologiaConcomitante();

    int getIdMedicoUltimaModifica();
    void setIdMedicoUltimaModifica(int idMedico);

    Date getDataInizio();

    Date getDataFine();
    void setDataFine(Date datafine);
}
