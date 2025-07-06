package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoPasti;
import com.univr.glicontrol.dal.AccessoPastiImpl;

import java.sql.Time;
import java.util.List;

public class GestionePasti {
    private final Paziente paziente;
    private final AccessoPasti accessoPasti = new AccessoPastiImpl();
    private List<Pasto> pasti;

    public GestionePasti(Paziente paziente) {
        this.paziente = paziente;
        pasti = accessoPasti.recuperaPastiPerUtente(paziente.getIdUtente());
    }

    public List<Pasto> getPasti() {
        aggiornaListaPasti();
        return pasti;
    }

    private void aggiornaListaPasti() {
        pasti.clear();
        pasti = accessoPasti.recuperaPastiPerUtente(paziente.getIdUtente());
    }

    public boolean inserisciPasto(String nomePasto, Time orario) {
        return accessoPasti.insertPasto(paziente.getIdUtente(), nomePasto, orario);
    }

    public boolean eliminaPasto(int idPasto) {
        return accessoPasti.deletePasto(idPasto);
    }

    public boolean aggiornaPasto(Pasto pasto) {
        return accessoPasti.updatePasto(pasto);
    }

}
