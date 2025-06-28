package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilityPortali {
    private final Paziente paziente;
    private final Medico medico;
    private final Map<String, Pasto> mappaPasti = new HashMap<>();
    private final Map<String, Sintomo> mappaSintomi = new HashMap<>();
    private final Map<String, PatologiaConcomitante> mappaPatologieConcomitanti = new HashMap<>();
    private final Map<String, RilevazioneGlicemica> mappaRilevazioniGlicemia = new HashMap<>();
    private final Map<String, Terapia> mappaTerapie = new HashMap<>();
    private final Map<String, LogTerapia> mappaLogTerapia = new HashMap<>();
    private final Map<String, Paziente> mappaPazientiAssociatiAlReferente = new HashMap<>();
    private final Map<String, Paziente> mappaPazientiNonAssociatiAlReferente = new HashMap<>();
    private final Map<String, Notifica> mappaNotifiche = new HashMap<>();

    public UtilityPortali(Paziente paziente) {
        this.paziente = paziente;
        this.medico = UtenteSessione.getInstance().getMedicoSessione();
    }
    public UtilityPortali() {
        this.paziente = UtenteSessione.getInstance().getPazienteSessione();
        this.medico = UtenteSessione.getInstance().getMedicoSessione();
    }

    public Time convertiOra(int ora, int minuti) {
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

    public Medico getMedicoSessione() {return medico;}

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
        if(paziente != null) {
            if (paziente.getNome() != null && !paziente.getNome().isEmpty()) {
                iniziali += paziente.getNome().substring(0, 1).toUpperCase();
            }
            if (paziente.getCognome() != null && !paziente.getCognome().isEmpty()) {
                iniziali += paziente.getCognome().substring(0, 1).toUpperCase();
            }
        }else{
            if(medico.getNome() != null && !medico.getNome().isEmpty()) {
                iniziali += medico.getNome().substring(0, 1).toUpperCase();
            }
            if(medico.getCognome() != null && !medico.getCognome().isEmpty()) {
                iniziali += medico.getCognome().substring(0, 1).toUpperCase();
            }
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
        String check = "   (inserito";
        for (Sintomo s : mappaSintomi.values()) {
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
    public List<String> getListaPatologieConcomitantiPaziente() {
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
        getListaPatologieConcomitantiPaziente();
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

        return listaRilevazioniGlicemiche.reversed();
    }

    public List<String> getListaRilevazioniGlicemicheOdierne(){
        List<String> listaRilevazioniGlicemiche = new ArrayList<>();
        for(String r : getListaRilevazioniGlicemichePazienti()){
            if(getRilevazioneGlicemicaPerValoreFormattata(r).getData().equals(Date.valueOf(LocalDate.now()))){
                listaRilevazioniGlicemiche.add(r);
            }
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


    public List<String> getListaFarmaciDaAssumere() {
        List<String> listaFarmaciDaAssumere = new ArrayList<>();
        GestioneTerapie gt = new GestioneTerapie(paziente);
        List<TerapiaDiabete> listaTd = new ArrayList<>();
        List<TerapiaConcomitante> listaTc = new ArrayList<>();

        for (Terapia t : gt.getTerapiePaziente()) {
            if (t instanceof TerapiaDiabete td) {
                listaTd.add(td);
            } else if (t instanceof TerapiaConcomitante tc) {
                listaTc.add(tc);
            }
        }

        for (TerapiaDiabete td : listaTd) {
            List<FarmacoTerapia> ft = td.getListaFarmaciTerapia();
            for (FarmacoTerapia f : ft) {
                listaFarmaciDaAssumere.add(f.getFarmaco().getNome());
            }
        }
        for (TerapiaConcomitante tc : listaTc) {
            List<FarmacoTerapia> ft = tc.getListaFarmaciTerapia();
            for (FarmacoTerapia f : ft) {
                listaFarmaciDaAssumere.add(f.getFarmaco().getNome());
            }
        }

        listaFarmaciDaAssumere.removeIf(farmaco -> GlicontrolCoreSystem.getInstance().verificaAssunzioneRispettoAllOrario(paziente, farmaco));

        return listaFarmaciDaAssumere;
    }

    public List<String> getListaFarmaciAssuntiOggi() {
        List<String> listaFarmaciAssuntiOggi = new ArrayList<>();
        GestioneAssunzioneFarmaci gaf = new GestioneAssunzioneFarmaci(paziente);
        for (AssunzioneFarmaco af : gaf.getListaAssunzioneFarmaci()) {
            LocalDate data = af.getData().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = data.format(formatter);
            if (dataFormattata.equals(LocalDate.now().format(formatter))) {
                String assunzioneFormattata = GestioneFarmaci.getInstance().getFarmacoById(af.getIdFarmaco()).getNome() + "   (assunto il " + dataFormattata + " alle " + af.getOra().toString().substring(0, 5) + ")";
                listaFarmaciAssuntiOggi.add(assunzioneFormattata);
            }
        }

        return listaFarmaciAssuntiOggi;
    }

    // lista completa farmaci diabete per paziente
    public List<String> getListaCompletaAssunzioneFarmaciDiabete(){
        List<String> listaCompletaAssunzioneFarmaciDiabete = new ArrayList<>();
        GestioneAssunzioneFarmaci gaf = new GestioneAssunzioneFarmaci(paziente);
        Farmaco f;
        for(AssunzioneFarmaco af: gaf.getListaAssunzioneFarmaci()){
            LocalDate data = af.getData().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = data.format(formatter);
            f = GestioneFarmaci.getInstance().getFarmacoById(af.getIdFarmaco());
            if(f.getTipologia().equals("Ipoglicemizzante") || f.getTipologia().equals("Insulina")){
                String nomeFormattato = f.getNome() + "   (assunto il " + dataFormattata + " alle " + af.getOra().toString().substring(0, 5) + ")";
                listaCompletaAssunzioneFarmaciDiabete.add(nomeFormattato);
            }
        }

        return  listaCompletaAssunzioneFarmaciDiabete.reversed();
    }

    public List<String> getListaCompletaAssunzioneFarmaciTerapieConcomitanti(){
        List<String> listaCompletaAssunzioneFarmaciTerapieConcomitanti = new ArrayList<>();
        GestioneAssunzioneFarmaci gaf = new GestioneAssunzioneFarmaci(paziente);
        Farmaco f;
        for(AssunzioneFarmaco af: gaf.getListaAssunzioneFarmaci()){
            LocalDate data = af.getData().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = data.format(formatter);
            f = GestioneFarmaci.getInstance().getFarmacoById(af.getIdFarmaco());
            if(!f.getTipologia().equals("Ipoglicemizzante") && !f.getTipologia().equals("Insulina")){
                String nomeFormattato = f.getNome() + "   (assunto il " + dataFormattata + " alle " + af.getOra().toString().substring(0, 5) + ")";
                listaCompletaAssunzioneFarmaciTerapieConcomitanti.add(nomeFormattato);
            }
        }

        return listaCompletaAssunzioneFarmaciTerapieConcomitanti.reversed();
    }



    public Farmaco getFarmacoPerNomeFormattato(String nomeFarmacoFormattato) {
        String check = "   (assunto il";
        int limit = nomeFarmacoFormattato.indexOf(check);
        if (limit == -1) {
            return null;
        }

        return GestioneFarmaci.getInstance().getFarmacoByName(nomeFarmacoFormattato.substring(0, limit));
    }


    // genera la lista di tipo String formattata per la visualizzazione dei log in Portale Admin
    public List<String> getListaLogTerapieFormattata() {
        ListaPazienti lp = new ListaPazienti();
        ListaMedici lm = new ListaMedici();
        List<String> listaLogTerapieFormattata = new ArrayList<>();
        GestioneLogTerapie glt = new GestioneLogTerapie();
        GestioneTerapie gt = new GestioneTerapie();

        for (LogTerapia lt : glt.getListaLogTerapia()) {
            Terapia t = gt.getTerapiaById(lt.getIdTerapia());
            LocalDate data = lt.getTimestamp().toLocalDateTime().toLocalDate();
            LocalTime ora = lt.getTimestamp().toLocalDateTime().toLocalTime();
            String logFormattato = t.getNome() + " (" + lp.getPazientePerId(t.getIdPaziente()).getCodiceFiscale() + ") modificata dal medico " +
                    lm.ottieniMedicoPerId(lt.getIdMedico()).getCodiceFiscale() +
                    " in data " + data + " alle ore " + ora;
            listaLogTerapieFormattata.add(logFormattato);
            mappaLogTerapia.put(logFormattato, lt);
        }

        return listaLogTerapieFormattata;
    }

    private void aggiornaListaLogTerapieFormattata() {
        getListaLogTerapieFormattata();
    }

    public LogTerapia getLogTerapiaPerLogFormattato(String logFormattato) {
        aggiornaListaLogTerapieFormattata();
        ListaMedici lm = new ListaMedici();

        for (LogTerapia lt : mappaLogTerapia.values()) {
            String check = " in data " + lt.getTimestamp().toLocalDateTime().toLocalDate()
                    + " alle ore " + lt.getTimestamp().toLocalDateTime().toLocalTime();

            int limit = logFormattato.indexOf(check);

            if (limit != -1 && check.equals(logFormattato.substring(limit))) {
                return lt;
            }
        }

        return null;
    }


    // recuperiamo le liste dei pazienti partizionandoli tra pazienti associati ad un dato referente attualmente connesso e gli altri
    private final List<String> pazientiAssociatiAlReferente = new ArrayList<>();
    private final List<String> pazientiNonAssociatiAlReferente = new ArrayList<>();

    public List<String> getPazientiAssociatiAlReferente(int idReferente) {
        ListaPazienti listaPazienti = new ListaPazienti();
        for  (Paziente p : listaPazienti.getListaCompletaPazienti()) {
            if (p.getMedicoRiferimento() == idReferente) {
                String pazienteAssociatoFormattato = p.getCognome() + " " + p.getNome() + " (" + p.getCodiceFiscale() + ")";
                pazientiAssociatiAlReferente.add(pazienteAssociatoFormattato);
                mappaPazientiAssociatiAlReferente.put(pazienteAssociatoFormattato, p);
            }
        }

        return  pazientiAssociatiAlReferente;
    }

    public List<String> getPazientiNonAssociatiAlReferente(int idReferente) {
        ListaPazienti listaPazienti = new ListaPazienti();
        for(Paziente p : listaPazienti.getListaCompletaPazienti()) {
            if(p.getMedicoRiferimento() != idReferente) {
                String pazienteNonAssociatoFormattato = p.getCognome() + " " + p.getNome() + " (" + p.getCodiceFiscale() + ")";
                pazientiNonAssociatiAlReferente.add(pazienteNonAssociatoFormattato);
                mappaPazientiNonAssociatiAlReferente.put(pazienteNonAssociatoFormattato, p);
            }
        }

        return  pazientiNonAssociatiAlReferente;
    }

    public Paziente getPazienteAssociatoDaNomeFormattato(String nomePazienteFormattato) {
        for(Paziente p: mappaPazientiAssociatiAlReferente.values()){
            String check = p.getCognome() + " " + p.getNome() + " (" + p.getCodiceFiscale() + ")";
            if(nomePazienteFormattato.equals(check)) {
                return p;
            }
        }
        return null;
    }

    public Paziente getPazienteNonAssociatoDaNomeFormattato(String nomePazienteFormattato) {
        for(Paziente p: mappaPazientiNonAssociatiAlReferente.values()){
            String check = p.getCognome() + " " + p.getNome() + " (" + p.getCodiceFiscale() + ")";
            if(nomePazienteFormattato.equals(check)) {
                return p;
            }
        }
        return null;
    }

    public float convertiDosaggio(String dosaggio) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < dosaggio.length(); i++) {
            if (Character.isDigit(dosaggio.charAt(i)) || dosaggio.charAt(i) == '.') {
                result.append(dosaggio.charAt(i));
            }
        }
        return Float.parseFloat(result.toString());
    }


    // Restituisce la notifica (per il portale del medico) formattata
    public List<String> getNotificheFormattate() {
        ListaPazienti listaPazienti = new ListaPazienti();
        List<String> notificheFormattate = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        for (Paziente p : listaPazienti.getListaCompletaPazienti()) {
            GestioneNotifiche gn = new GestioneNotifiche(p);

            for (Notifica n : gn.getNotificheNonVisualizzate()) {
                String notifica =
                        "[" +
                        n.getTitolo() +
                        "]\n" +
                        "(" +
                        n.getPazienteAssociato().getCodiceFiscale() +
                        ")\n" +
                        n.getMessaggio() +
                        "\n" +
                        "(" +
                        n.getDataNotifica().format(formatter) +
                        ")\n\n\n";

                notificheFormattate.add(notifica);
                mappaNotifiche.put(notifica, n);
            }
        }

        return notificheFormattate;
    }

    public Notifica getNotificaNonVisualizzateDaNomeFormattato(String notificaFormattata) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        aggiornaListaNotificheNonVisualizzateFormattate();

        for (Notifica n: mappaNotifiche.values()){
            String notificaComparazione =
                    "[" +
                    n.getTitolo() +
                    "]\n" +
                    "(" +
                    n.getPazienteAssociato().getCodiceFiscale() +
                    ")\n" +
                    n.getMessaggio() +
                    "\n" +
                    "(" +
                    n.getDataNotifica().format(formatter) +
                    ")\n\n\n";

            if (notificaComparazione.equals(notificaFormattata)) {
                return n;
            }
        }

        return null;
    }

    private void aggiornaListaNotificheNonVisualizzateFormattate() {
        getNotificheFormattate();
    }
}
