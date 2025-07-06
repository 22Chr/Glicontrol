package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoFarmaci;
import com.univr.glicontrol.dal.AccessoFarmaciImpl;

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
        aggiornaListaFarmaci();
        return farmaci;
    }

    // Restituisce un farmaco a partire dal suo id
    public Farmaco getFarmacoById(int idFarmaco) {
        aggiornaListaFarmaci();
        for (Farmaco farmaco : farmaci) {
            if (farmaco.getIdFarmaco() == idFarmaco) {
                return farmaco;
            }
        }

        return null;
    }

    // Restituisce un farmaco a partire dal suo nome
    public Farmaco getFarmacoByName(String nomeFarmaco) {
        aggiornaListaFarmaci();
        for (Farmaco farmaco : farmaci) {
            if (farmaco.getNome().equals(nomeFarmaco)) {
                return farmaco;
            }
        }

        return null;
    }

    private void aggiornaListaFarmaci() {
        farmaci.clear();
        farmaci.addAll(inizializzaFarmaci());
    }
}
