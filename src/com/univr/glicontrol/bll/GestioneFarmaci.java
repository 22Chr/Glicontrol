package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoFarmaci;
import com.univr.glicontrol.dao.AccessoFarmaciImpl;

import java.util.List;

public class GestioneFarmaci {
    private final List<Farmaco> farmaci;
    private final AccessoFarmaci accessoFarmaci = new AccessoFarmaciImpl();

    // Pattern Singleton
    private GestioneFarmaci() {
        farmaci = inizializzaFarmaci();
    }

    // Crea l'istanza Singleton
    private static class Holder {
        private static final GestioneFarmaci INSTANCE = new GestioneFarmaci();
    }

    // Consente all'esterno di accedere all'istanza Singleton
    public static GestioneFarmaci getInstance() {
        return Holder.INSTANCE;
    }

    private List<Farmaco> inizializzaFarmaci() {
        return accessoFarmaci.recuperaTuttiFarmaci();
    }

    public List<Farmaco> getListaFarmaci() {
        return farmaci;
    }

    // I farmaci vengono aggiunti sia nel DB che in locale
    public int inserisciNuovoFarmaco(Farmaco farmaco) {
        if (farmaci.contains(farmaco)) {
            return -1;
        }

        int status = 0;
        if (accessoFarmaci.insertFarmaco(farmaco)) {
            farmaci.add(farmaco);
            status = 1;
        }

        return status;
    }

    // I farmaci vengono rimossi sia nel DB che in locale
    public boolean eliminaFarmaco(int idFarmaco) {
        boolean status = accessoFarmaci.deleteFarmaco(idFarmaco);

        if (status) {
            farmaci.removeIf(farmaco -> farmaco.getIdFarmaco() == idFarmaco);
        }

        return status;
    }

    // I farmaci vengono modificati sia nel DB che in locale
    public boolean aggiornaFarmaco(Farmaco farmaco) {
        boolean status = accessoFarmaci.updateFarmaco(farmaco);

        if (status) {
            int index = farmaci.indexOf(farmaco);
            farmaci.set(index, farmaco);
        }

        return status;
    }

    // Restituisce un farmaco a partire dal suo id
    public Farmaco getFarmacoById(int idFarmaco) {
        for (Farmaco farmaco : farmaci) {
            if (farmaco.getIdFarmaco() == idFarmaco) {
                return farmaco;
            }
        }

        return null;
    }
}
