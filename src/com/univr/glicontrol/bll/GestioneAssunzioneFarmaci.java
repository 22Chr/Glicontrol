package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoAssunzioneFarmaci;
import com.univr.glicontrol.dao.AccessoAssunzioneFarmaciImpl;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class GestioneAssunzioneFarmaci {
    private final Paziente paziente;
    private final AccessoAssunzioneFarmaci asf = new AccessoAssunzioneFarmaciImpl();
    private List<AssunzioneFarmaco> assunzioni;

    public GestioneAssunzioneFarmaci(Paziente paziente) {
        this.paziente = paziente;
        assunzioni = asf.getListaFarmaciAssunti(paziente.getIdUtente());
    }

    public int registraAssunzioneFarmaco(Farmaco farmaco, Date data, Time ora, float dosaggio) {

        boolean checkDosaggio = GlicontrolCoreSystem.getInstance().verificaCoerenzaDosaggioFarmaci(paziente, farmaco.getNome(), dosaggio);
        boolean success = asf.insertAssunzioneFarmaci(paziente.getIdUtente(), farmaco.getIdFarmaco(), data, ora, dosaggio);
        if (success && checkDosaggio) {
            return 1;
        } else if (success) {
            return -1;
        } else {
            return 0;
        }
    }

    public List<AssunzioneFarmaco> getListaAssunzioneFarmaci() {
        aggiornaListaFarmaciAssunzione();
        return assunzioni;
    }

    public List<AssunzioneFarmaco> getListaFarmaciAssuntiOggi(Date data, String nomeFarmaco) {
        return asf.getListaFarmaciAssuntiOggi(paziente.getIdUtente(), data, GestioneFarmaci.getInstance().getFarmacoByName(nomeFarmaco).getIdFarmaco());
    }

    private void aggiornaListaFarmaciAssunzione() {
        assunzioni = asf.getListaFarmaciAssunti(paziente.getIdUtente());
    }

    public boolean eliminaAssunzioneFarmaco(int idFarmaco) {
        return asf.deleteAssunzioneFarmaco(idFarmaco);
    }

}
