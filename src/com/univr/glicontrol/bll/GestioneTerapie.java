package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoTerapie;
import com.univr.glicontrol.dao.AccessoTerapieImpl;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GestioneTerapie {
    private final Paziente paziente;
    private final List<Terapia> terapiePaziente = new ArrayList<>();
    private final AccessoTerapie accessoTerapie = new AccessoTerapieImpl();

    List<TerapiaDiabete> terapiaDiabete;
    List<TerapiaConcomitante> terapiaConcomitante;

    public GestioneTerapie(Paziente paziente) {
        this.paziente = paziente;
        generaListaTerapieComune();
    }

    private void generaListaTerapieComune() {
        terapiePaziente.clear();
        terapiaDiabete = accessoTerapie.getTerapieDiabetePaziente(paziente.getIdUtente());
        terapiaConcomitante = accessoTerapie.getTerapieConcomitantiPaziente(paziente.getIdUtente());
        if (terapiaDiabete != null) {
            terapiePaziente.addAll(terapiaDiabete);
        }
        if (terapiaConcomitante != null) {
            terapiePaziente.addAll(terapiaConcomitante);
        }
    }

    public List<Terapia> getTerapiePaziente() {
        generaListaTerapieComune();
        return terapiePaziente;
    }

    private void aggiornaListaTerapieDiabete() {
        terapiaDiabete = accessoTerapie.getTerapieDiabetePaziente(paziente.getIdUtente());
    }

    private void aggiornaListaTerapieConcomitanti() {
        terapiaConcomitante = accessoTerapie.getTerapieConcomitantiPaziente(paziente.getIdUtente());
    }

    private void aggiornaListaTerapie() {
        terapiePaziente.clear();
        aggiornaListaTerapieDiabete();
        aggiornaListaTerapieConcomitanti();
        terapiePaziente.addAll(terapiaDiabete);
        terapiePaziente.addAll(terapiaConcomitante);
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
        // Verificare successivamente se inserire un controllo sui duplicati
        aggiornaListaTerapie();
        for (TerapiaDiabete terapia : terapiaDiabete) {
            if (terapia.getDataInizio().equals(dataInizio) && terapia.getListaFarmaciTerapia().equals(farmaci) && terapia.getIdPaziente() == paziente.getIdUtente()) {
                return -1;
            }
        }

        return accessoTerapie.insertTerapiaDiabete(paziente.getIdUtente(), idMedicoUltimaModifica, dataInizio, dataFine, farmaci) ? 1 : 0;
    }

    public int inserisciTerapiaConcomitante(int idPatologia, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, List<FarmacoTerapia> farmaci, String nomePatologia) {
        aggiornaListaTerapie();
        UtilityPortalePaziente upp = new UtilityPortalePaziente();

        String check = " (";
        int limit = nomePatologia.indexOf(check);

        for (TerapiaConcomitante terapia : terapiaConcomitante) {
            if (terapia.getDataInizio().equals(dataInizio) && terapia.getListaFarmaciTerapia().equals(farmaci) && terapia.getIdPaziente() == paziente.getIdUtente()) {
                return -1;
            }

            if (terapia.getIdPatologiaConcomitante() == upp.getPatologiaConcomitantePerNomeFormattata(nomePatologia).getIdPatologia()) {
                return -1;
            }
        }

        return accessoTerapie.insertTerapiaConcomitante(paziente.getIdUtente(), idPatologia, idMedicoUltimaModifica, dataInizio, dataFine, farmaci) ? 1 : 0;
    }

    public boolean aggiornaTerapiaDiabete(TerapiaDiabete terapia) {
        return accessoTerapie.updateTerapiaDiabete(terapia);
    }

    public boolean aggiornaTerapiaConcomitante(TerapiaConcomitante terapia) {
        return accessoTerapie.updateTerapiaConcomitante(terapia);
    }


    // Verificare se potranno servire in futuro, probabilmente no
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

    private List<FarmacoTerapia> ft = new ArrayList<>();

    public boolean generaFarmaciTerapia(Farmaco farmaco, IndicazioniFarmaciTerapia indicazioni){
        List<Farmaco> farmaciCaricati =  new ArrayList<>();

        for(FarmacoTerapia cache: ft) {
            farmaciCaricati.add(cache.getFarmaco());
        }
        // Verifica che il farmaco non sia già presenta nella terapia
        // Se arriva un farmaco non valido si scartano anche le indicazioni

        for(Farmaco f:  farmaciCaricati){
            if(f.equals(farmaco)){
                return false;
            }
        }

        ft.add(new FarmacoTerapia(farmaco, indicazioni));
        return true;
    }

    public List<FarmacoTerapia> getFarmaciTerapia() {
        return ft;
    }
}
