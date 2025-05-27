package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilityPortalePaziente {
    private final Paziente paziente;
    private final Map<String, Pasto> mappaPasti = new HashMap<>();
    private final Map<String, Sintomo> mappaSintomi = new HashMap<>();
    private final Map<String, PatologiaConcomitante> mappaPatologieConcomitanti = new HashMap<>();
    private final Map<String, RilevazioneGlicemica> mappaRilevazioniGlicemia = new HashMap<>();
    private final Map<String, Terapia> mappaTerapie = new HashMap<>();

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
            String pastoFormattato = pasto.getNomePasto() + " - " + pasto.getOrario().toString().substring(0, 5);
            listaPasti.add(pastoFormattato);
            mappaPasti.put(pastoFormattato, pasto);
        }

        return listaPasti;
    }

    private void aggiornaListaPasti() {
        getListaPasti();
    }

    public String getNomePastoPerPastoFormattato(String pastoFormattato) {
        aggiornaListaPasti();
        for (Pasto pasto : mappaPasti.values()) {
            String check = " - ";
            int limit = pastoFormattato.indexOf(check);

            if (limit == -1) {
                return null;
            }

            if (pasto.getNomePasto().equals(pastoFormattato.substring(0, limit))) {
                return pasto.getNomePasto();
            }
        }

        return null;
    }

    public Paziente getPazienteSessione() {
        return paziente;
    }

    public Image getBadge() {
        double width = 40;
        double height = 40;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Sfondo rosso
        gc.setFill(Color.valueOf("#ff0404"));
        gc.fillRect(0, 0, width, height);

        // Font grande e grassetto
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Avenir Next", FontWeight.BOLD, 20));

        // Iniziali (gestione sicura)
        String iniziali = "";
        if (paziente.getNome() != null && !paziente.getNome().isEmpty()) {
            iniziali += paziente.getNome().substring(0, 1).toUpperCase();
        }
        if (paziente.getCognome() != null && !paziente.getCognome().isEmpty()) {
            iniziali += paziente.getCognome().substring(0, 1).toUpperCase();
        }

        // Calcolo per centrare il testo
        Text text = new Text(iniziali);
        text.setFont(gc.getFont());
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        double x = (width - textWidth) / 2;
        double y = (height + textHeight / 2) / 2;

        // Disegno testo
        gc.fillText(iniziali, x, y);

        return canvas.snapshot(null, null);
    }


    // Ottiene la lista dei sintomi dei pazienti
    public List<String> getListaSintomiPazienti() {
        List<String> listaSintomi = new ArrayList<>();

        GestioneSintomi gs = new GestioneSintomi(paziente);
        for (Sintomo sintomo : gs.getSintomi()) {
            LocalDate data = sintomo.getData().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = data.format(formatter);
            String sintomoFormattato = sintomo.getDescrizione() + "   (inserito il " + dataFormattata + " alle " + sintomo.getOra().toString().substring(0, 5) + ")";
            listaSintomi.add(sintomoFormattato);
            mappaSintomi.put(sintomoFormattato, sintomo);
        }

        return listaSintomi;
    }

    // Aggiorna la lista dei sintomi
    private void aggiornaListaSintomiPazienti() {
        getListaSintomiPazienti();
    }

    // Restituisce il sintomo a partire dalla sua voce grafica di tipo String
    public Sintomo getSintomoPerDescrizioneFormattata(String descrizione) {
        aggiornaListaSintomiPazienti();
        for (Sintomo s : mappaSintomi.values()) {
            String check = "   (inserito";
            int limit = descrizione.indexOf(check);

            if (limit == -1) {
                return null;
            }

            if (s.getDescrizione().equals(descrizione.substring(0, limit))) {
                return s;
            }
        }
        return null;
    }


    // Restituisce la lista di tipo String delle patologie concomitanti del paziente
    public List<String> getListaPatologieConcomitantiPazienti() {
        List<String> listaPatologieConcomitanti = new ArrayList<>();

        GestionePatologieConcomitanti gpc = new GestionePatologieConcomitanti(paziente);
        for (PatologiaConcomitante p : gpc.getListaPatologieConcomitanti()) {
            LocalDate dataInizio = p.getDataInizio().toLocalDate();
            LocalDate dataFine = p.getDataFine() == null ? null : p.getDataFine().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInizioFormattata = dataInizio.format(formatter);
            String dataFineFormattata;
            if (dataFine != null) {
                dataFineFormattata = dataFine.format(formatter);
            } else {
                dataFineFormattata = "in corso";
            }

            String patologiaConcomitanteFormattata = p.getNomePatologia() + "   (" + dataInizioFormattata + " - " + dataFineFormattata + ")";
            listaPatologieConcomitanti.add(patologiaConcomitanteFormattata);
            mappaPatologieConcomitanti.put(patologiaConcomitanteFormattata, p);
        }

        return listaPatologieConcomitanti;
    }

    // Aggiorna la lista delle patologie concomitanti
    private void aggiornaListaPatologieConcomitantiPazienti() {
        getListaPatologieConcomitantiPazienti();
    }

    // Restituisce la patologia concomitante a partire dalla sua voce grafica di tipo String
    public PatologiaConcomitante getPatologiaConcomitantePerNomeFormattata(String nomePatologiaFormattato) {
        aggiornaListaPatologieConcomitantiPazienti();
        for (PatologiaConcomitante p : mappaPatologieConcomitanti.values()) {
            LocalDate dataInizio = p.getDataInizio().toLocalDate();
            LocalDate dataFine = p.getDataFine() == null ? null : p.getDataFine().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInizioFormattata = dataInizio.format(formatter);
            String dataFineFormattata;
            if (dataFine != null) {
                dataFineFormattata = dataFine.format(formatter);
            } else {
                dataFineFormattata = "in corso";
            }

            if ((p.getNomePatologia() + "   (" + dataInizioFormattata + " - " + dataFineFormattata + ")").equals(nomePatologiaFormattato)) {
                return p;
            }
        }
        return null;
    }


    // Restituisce la lista formattata di tipo String delle rilevazioni glicemiche del paziente
    public List<String> getListaRilevazioniGlicemichePazienti() {
        List<String> listaRilevazioniGlicemiche = new ArrayList<>();
        GestioneRilevazioniGlicemia grg = new GestioneRilevazioniGlicemia(paziente);
        for (RilevazioneGlicemica rg : grg.getRilevazioni()) {
            LocalDate data = rg.getData().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = data.format(formatter);
            String rilevazioneGlicemicaFormattata = rg.getValore() + " mg/dl   (" + dataFormattata + " -  (" + rg.getIndicazioniTemporali() + ") " + rg.getPasto() + " - " + rg.getOra().toString().substring(0, 5) + ")";
            listaRilevazioniGlicemiche.add(rilevazioneGlicemicaFormattata);
            mappaRilevazioniGlicemia.put(rilevazioneGlicemicaFormattata, rg);
        }

        return listaRilevazioniGlicemiche;
    }

    // Aggiorna la lista delle rilevazioni glicemiche
    private void aggiornaListaRilevazioniGlicemiaPazienti() {
        getListaRilevazioniGlicemichePazienti();
    }

    // Restituisce la rilevazione glicemica a partire dalla sua voce di tipo String a livello UI
    public RilevazioneGlicemica getRilevazioneGlicemicaPerValoreFormattata(String valoreFormattato) {
        aggiornaListaRilevazioniGlicemiaPazienti();
        for (RilevazioneGlicemica rg : mappaRilevazioniGlicemia.values()) {
            LocalDate data = rg.getData().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = data.format(formatter);
            if ((rg.getValore() + " mg/dl   (" + dataFormattata + " -  (" + rg.getIndicazioniTemporali() + ") " + rg.getPasto() + " - " + rg.getOra().toString().substring(0, 5) + ")").equals(valoreFormattato)) {
                return rg;
            }
        }

        return null;
    }

    // Chiama la funzione di inserimento della terapia a seconda del tipo passato in input
//    public int inserisciTerapia(String tipoTerapia, int idPatologia, int idMedicoUltimaModifica, Date dataInizio, Date dataFine, String dosaggio, String frequenza, String orari, List<Farmaco> farmaci) {
//        GestioneTerapie gt = new GestioneTerapie(paziente);
//        if (tipoTerapia.equals("Terapia Diabete")) {
//            return gt.inserisciTerapiaDiabete(idMedicoUltimaModifica, dataInizio, dataFine, dosaggio, frequenza, orari, farmaci);
//        } else {
//            return gt.inserisciTerapiaConcomitante(idPatologia, idMedicoUltimaModifica, dataInizio, dataFine, dosaggio, frequenza, orari, farmaci);
//        }
//    }

    // Restituisce la lista di tipo String formattata per le terapie dei pazienti
    public List<String> getListaTerapiePaziente() {
        List<String> listaTerapie = new ArrayList<>();
        GestioneTerapie gt = new GestioneTerapie(paziente);
        for (Terapia t : gt.getTerapiePaziente()) {
            String terapiaFormattata = t.getNome();
            listaTerapie.add(terapiaFormattata);
            mappaTerapie.put(terapiaFormattata, t);
        }

        return listaTerapie;
    }

    // Restituisce la terapia a partire dal suo nome nella lista di tipo String
    private void aggiornaListaTerapiePaziente() {
        getListaTerapiePaziente();
    }

    public Terapia getTerapiaPerNomeFormattata(String nomeTerapiaFormattato) {
        aggiornaListaTerapiePaziente();
        for (Terapia t : mappaTerapie.values()) {
            if (t.getNome().equals(nomeTerapiaFormattato)) {
                return t;
            }
        }

        return null;
    }

    public String getIndicazioniTemporaliTerapia(Terapia terapia) {
        String indicazioniTemporali = "";

        if (terapia instanceof TerapiaDiabete td) {
            LocalDate dataInizio = td.getDataInizio().toLocalDate();
            LocalDate dataFine = td.getDataFine() == null ? null : td.getDataFine().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInizioFormattata = dataInizio.format(formatter);
            String dataFineFormattata;
            if (dataFine != null) {
                dataFineFormattata = dataFine.format(formatter);
            } else {
                dataFineFormattata = "in corso";
            }

            indicazioniTemporali = dataInizioFormattata + " - " + dataFineFormattata;
        } else if (terapia instanceof TerapiaConcomitante tc) {
            LocalDate dataInizio = tc.getDataInizio().toLocalDate();
            LocalDate dataFine = tc.getDataFine() == null ? null : tc.getDataFine().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInizioFormattata = dataInizio.format(formatter);
            String dataFineFormattata;
            if (dataFine != null) {
                dataFineFormattata = dataFine.format(formatter);
            } else {
                dataFineFormattata = "in corso";
            }

            indicazioniTemporali = dataInizioFormattata + " - " + dataFineFormattata;
        }

        return indicazioniTemporali;
    }

    public List<String> getListaFarmaciFormattatiCompleta() {
        List<String> listaFarmaciFormattati = new ArrayList<>();
        for(Farmaco f: GestioneFarmaci.getInstance().getListaFarmaci()) {
            listaFarmaciFormattati.add(f.getNome());
        }
        return listaFarmaciFormattati;
    }


}
