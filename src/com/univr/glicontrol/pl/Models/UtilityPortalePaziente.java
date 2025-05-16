package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.GestionePasti;
import com.univr.glicontrol.bll.Pasto;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.UtenteSessione;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilityPortalePaziente {
    private final Paziente paziente;
    private Map<String, Pasto> mappaPasti = new HashMap<>();

    public UtilityPortalePaziente() {
        this.paziente = UtenteSessione.getInstance().getPazienteSessione();
    }

    public Time convertiOraPasto(int ora, int minuti) {
        LocalTime localTime = LocalTime.of(ora, minuti);
        return Time.valueOf(localTime);
    }

    public List<String> getListaPasti() {
        List<String> listaPasti = new ArrayList<>();
        GestionePasti gp = new GestionePasti(paziente);
        for (Pasto pasto : gp.getPasti()) {
            String pastoFormattato = pasto.getNomePasto() + " - " + pasto.getOrario().toString();
            listaPasti.add(pastoFormattato);
            mappaPasti.put(pastoFormattato, pasto);
        }

        return listaPasti;
    }

    public Paziente getPazienteSessione() {
        return paziente;
    }

}
