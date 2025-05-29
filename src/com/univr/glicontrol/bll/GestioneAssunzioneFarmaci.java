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

    public int registraAssunzioneFarmaco(Farmaco farmaco, Date data, Time ora) {
        StringBuilder buildDosaggio = new StringBuilder();
        for (int i = 0; i < farmaco.getNome().length(); i++) {
            if (Character.isDigit(farmaco.getNome().charAt(i))) {
                buildDosaggio.append(farmaco.getNome().charAt(i));
            }
        }
        float dosaggio = Float.parseFloat(buildDosaggio.toString());

        int checkDosaggio = GlicontrolCoreSystem.getInstance().verificaCoerenzaDosaggioFarmaci(paziente, farmaco.getNome()) ? 1 : 0;
        boolean success = asf.insertAssunzioneFarmaci(paziente.getIdUtente(), farmaco.getIdFarmaco(), data, ora, dosaggio);
        if (success && checkDosaggio == 1) {
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

    private void aggiornaListaFarmaciAssunzione() {
        assunzioni = asf.getListaFarmaciAssunti(paziente.getIdUtente());
    }

    public boolean eliminaAssunzioneFarmaco(int idFarmaco) {
        return asf.deleteAssunzioneFarmaco(idFarmaco);
    }

}
