package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoTerapie;
import com.univr.glicontrol.dal.AccessoTerapieImpl;
import com.univr.glicontrol.pl.Models.UtilityPortali;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GestioneTerapie {
    private final Paziente pazienteSessione;
    private List<Terapia> terapiePaziente = new ArrayList<>();
    private final AccessoTerapie accessoTerapie = new AccessoTerapieImpl();

    private List<TerapiaDiabete> terapiaDiabete = new ArrayList<>();
    private List<TerapiaConcomitante> terapiaConcomitante = new ArrayList<>();

    public GestioneTerapie(Paziente pazienteSessione) {
        this.pazienteSessione = pazienteSessione;
    }

    public List<Terapia> getTerapiePaziente() {
        aggiornaListaTerapiePaziente();
        return terapiePaziente;
    }

    private void aggiornaListaTerapieDiabete(Paziente pazienteSelezionato) {
        terapiaDiabete.clear();
        terapiaDiabete = accessoTerapie.getTerapieDiabetePaziente(pazienteSelezionato.getIdUtente());
    }

    private void aggiornaListaTerapieConcomitanti(Paziente pazienteSelezionato) {
        terapiaConcomitante.clear();
        terapiaConcomitante = accessoTerapie.getTerapieConcomitantiPaziente(pazienteSelezionato.getIdUtente());
    }

    private void aggiornaListaTerapiePaziente() {

        terapiePaziente = new ArrayList<>();
        assert pazienteSessione != null;
        aggiornaListaTerapieDiabete(pazienteSessione);
        aggiornaListaTerapieConcomitanti(pazienteSessione);
        if (terapiaDiabete != null) {
            terapiePaziente.addAll(terapiaDiabete);
        }
        if (terapiaConcomitante != null) {
            terapiePaziente.addAll(terapiaConcomitante);
        }
    }

    public boolean aggiornaTerapia(Terapia terapia) {
        boolean status = false;
        if (terapia instanceof TerapiaDiabete) {
            status = aggiornaTerapiaDiabete((TerapiaDiabete) terapia);
        } else if (terapia instanceof TerapiaConcomitante) {
            status = aggiornaTerapiaConcomitante((TerapiaConcomitante) terapia);
        }

        return status;
    }

    public int inserisciTerapiaDiabete(int idMedicoUltimaModifica, Date dataInizio, Date dataFine, String noteTerapia, List<FarmacoTerapia> farmaci) {
        assert pazienteSessione != null;
        aggiornaListaTerapiePaziente();
        List<TerapiaDiabete> terapieDelPaziente = new ArrayList<>();
        for (TerapiaDiabete terapia : terapiaDiabete) {
            if (terapia.getIdPaziente() == pazienteSessione.getIdUtente()) {
                terapieDelPaziente.add(terapia);
            }
        }

        if (!terapieDelPaziente.isEmpty()) {
            return -1;
        }

        return accessoTerapie.insertTerapiaDiabete(pazienteSessione.getIdUtente(), idMedicoUltimaModifica, dataInizio, dataFine, noteTerapia, farmaci) ? 1 : 0;
    }

    public int inserisciTerapiaConcomitante(PatologiaConcomitante pat, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, String noteTerapia, List<FarmacoTerapia> farmaci) {
        assert pazienteSessione != null;
        aggiornaListaTerapiePaziente();

        if (pat == null) {
            return 0;
        }

        for (TerapiaConcomitante terapia : terapiaConcomitante) {
            if (terapia.getDataInizio().equals(dataInizio)
                    && terapia.getListaFarmaciTerapia().equals(farmaci)
                    && terapia.getIdPaziente() == pazienteSessione.getIdUtente()) {
                return -1;
            }

            if (terapia.getIdPatologiaConcomitante() == pat.getIdPatologia()) {
                return -1;
            }
        }

        return accessoTerapie.insertTerapiaConcomitante(pazienteSessione.getIdUtente(), pat.getIdPatologia(), idMedicoUltimaModifica, dataInizio, dataFine, noteTerapia, farmaci) ? 1 : 0;
    }

    private boolean aggiornaTerapiaDiabete(TerapiaDiabete terapia) {
        return accessoTerapie.updateTerapiaDiabete(terapia);
    }

    private boolean aggiornaTerapiaConcomitante(TerapiaConcomitante terapia) {
        return accessoTerapie.updateTerapiaConcomitante(terapia);
    }

    public Terapia getTerapiaById(int idTerapia) {
        for (Terapia terapia : terapiePaziente) {
            if (terapia instanceof TerapiaDiabete td && td.getIdTerapia() == idTerapia) {
                return td;
            } else if (terapia instanceof TerapiaConcomitante tc && tc.getIdTerapia() == idTerapia) {
                return tc;
            }
        }

        return null;
    }

    private final List<FarmacoTerapia> ft = new ArrayList<>();

    public boolean generaFarmaciTerapia(Farmaco farmaco, IndicazioniFarmaciTerapia indicazioni) {
        List<Farmaco> farmaciCaricati = new ArrayList<>();

        for (FarmacoTerapia cache : ft) {
            farmaciCaricati.add(cache.getFarmaco());
        }

        for (Farmaco f : farmaciCaricati) {
            if (f.equals(farmaco)) {
                return false;
            }
        }

        ft.add(new FarmacoTerapia(farmaco, indicazioni));
        return true;
    }

    public List<FarmacoTerapia> getFarmaciSingolaTerapia() {
        return ft;
    }
}