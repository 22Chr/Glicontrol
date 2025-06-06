package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoTerapie;
import com.univr.glicontrol.dao.AccessoTerapieImpl;
import com.univr.glicontrol.pl.Models.UtilityPortali;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GestioneTerapie {
    private final Paziente pazienteSessione;
    private List<Terapia> terapiePaziente = new ArrayList<>();
    private List<Terapia> listaCompletaTerapie = new ArrayList<>();
    private final AccessoTerapie accessoTerapie = new AccessoTerapieImpl();
    private final ListaPazienti listaPazienti = new ListaPazienti();

    List<TerapiaDiabete> terapiaDiabete = new ArrayList<>();
    List<TerapiaConcomitante> terapiaConcomitante = new ArrayList<>();

    public GestioneTerapie(Paziente pazienteSessione) {
        this.pazienteSessione = pazienteSessione;
    }

    public GestioneTerapie() {
        this.pazienteSessione = null;
    }

    public List<Terapia> getTerapiePaziente() {
        aggiornaListaTerapiePaziente();
        return terapiePaziente;
    }

    public List<Terapia> getListaCompletaTerapie() {
        aggiornaListaTerapieCompleta();
        return listaCompletaTerapie;
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
        aggiornaListaTerapieDiabete(pazienteSessione);
        aggiornaListaTerapieConcomitanti(pazienteSessione);
        if (terapiaDiabete != null) {
            terapiePaziente.addAll(terapiaDiabete);
        }
        if (terapiaConcomitante != null) {
            terapiePaziente.addAll(terapiaConcomitante);
        }
    }

    private void aggiornaListaTerapieCompleta() {
        listaCompletaTerapie.clear();
        for (Paziente p : listaPazienti.getListaCompletaPazienti()) {
            aggiornaListaTerapieDiabete(p);
            aggiornaListaTerapieConcomitanti(p);
            listaCompletaTerapie.addAll(accessoTerapie.getTerapieConcomitantiPaziente(p.getIdUtente()));
            listaCompletaTerapie.addAll(accessoTerapie.getTerapieDiabetePaziente(p.getIdUtente()));
        }
    }

    public boolean aggiornaTerapia(Terapia terapia) {
        if (terapia instanceof TerapiaDiabete) {
            return accessoTerapie.updateTerapiaDiabete((TerapiaDiabete) terapia);
        } else if (terapia instanceof TerapiaConcomitante) {
            return accessoTerapie.updateTerapiaConcomitante((TerapiaConcomitante) terapia);
        } else {
            return false;
        }
    }

    public int inserisciTerapiaDiabete(int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaci) {
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

        return accessoTerapie.insertTerapiaDiabete(pazienteSessione.getIdUtente(), idMedicoUltimaModifica, dataInizio, dataFine, farmaci) ? 1 : 0;
    }

    public int inserisciTerapiaConcomitante(int idPatologia, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaci, String nomePatologia) {
        aggiornaListaTerapiePaziente();
        UtilityPortali upp = new UtilityPortali(pazienteSessione);

        for (TerapiaConcomitante terapia : terapiaConcomitante) {
            if (terapia.getDataInizio().equals(dataInizio)
                    && terapia.getListaFarmaciTerapia().equals(farmaci)
                    && terapia.getIdPaziente() == pazienteSessione.getIdUtente()) {
                return -1;
            }

            PatologiaConcomitante pat = upp.getPatologiaConcomitantePerNomeFormattata(nomePatologia);
            if (pat != null && terapia.getIdPatologiaConcomitante() == pat.getIdPatologia()) {
                return -1;
            }
        }

        return accessoTerapie.insertTerapiaConcomitante(pazienteSessione.getIdUtente(), idPatologia, idMedicoUltimaModifica, dataInizio, dataFine, farmaci) ? 1 : 0;
    }

    public boolean aggiornaTerapiaDiabete(TerapiaDiabete terapia) {
        return accessoTerapie.updateTerapiaDiabete(terapia);
    }

    public boolean aggiornaTerapiaConcomitante(TerapiaConcomitante terapia) {
        return accessoTerapie.updateTerapiaConcomitante(terapia);
    }

    public TerapiaDiabete getTerapiaDiabete(int idTerapia) {
        for (Terapia terapia : terapiePaziente) {
            if (terapia instanceof TerapiaDiabete td && td.getIdTerapiaDiabete() == idTerapia) {
                return td;
            }
        }

        return null;
    }

    public TerapiaConcomitante getTerapieConcomitante(int idTerapia) {
        for (Terapia terapia : terapiePaziente) {
            if (terapia instanceof TerapiaConcomitante tc && tc.getIdTerapiaConcomitante() == idTerapia) {
                return tc;
            }
        }

        return null;
    }

    public Terapia getTerapiaById(int idTerapia) {
        for (Terapia terapia : listaCompletaTerapie) {
            if (terapia instanceof TerapiaDiabete td && td.getIdTerapiaDiabete() == idTerapia) {
                return td;
            } else if (terapia instanceof TerapiaConcomitante tc && tc.getIdTerapiaConcomitante() == idTerapia) {
                return tc;
            }
        }

        return null;
    }

    private List<FarmacoTerapia> ft = new ArrayList<>();

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