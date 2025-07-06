package com.univr.glicontrol.bll;

import com.univr.glicontrol.pl.Controllers.PortaleMedicoController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

public class GlicontrolCoreSystem {

    private final List<Paziente> listaPazienti;
    private GestioneTerapie gestioneTerapie = null;
    private GestioneAssunzioneFarmaci gestioneAssunzioneFarmaci = null;
    private GestionePasti gestionePasti = null;
    private GestioneNotifiche gestioneNotifiche = null;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    PortaleMedicoController pmc  = null;
    private boolean connessoComeMedico = false;
    private boolean statoCentroNotifiche = false; //true = aperto, false = chiuso
    private final Map<Paziente, List<Farmaco>> farmaciNonAssuntiDa3Giorni = new HashMap<>();
    private boolean finestraRilevazioniGlicemicheAperta = false;

    private GlicontrolCoreSystem() {
        listaPazienti = GestionePazienti.getInstance().getListaPazienti();
    }

    // Crea l'istanza Singleton
    private static class Holder {
        private static final GlicontrolCoreSystem INSTANCE = new GlicontrolCoreSystem();
    }

    // Consente all'esterno di accedere all'istanza Singleton
    public static GlicontrolCoreSystem getInstance() {
        return GlicontrolCoreSystem.Holder.INSTANCE;
    }

    // VERIFICA IL RISPETTO DEI DOSAGGI DEI FARMACI RISPETTO AL SINGOLO UTENTE (LIVELLO PORTALE PAZIENTE)
    public boolean verificaCoerenzaDosaggioFarmaci(Paziente paziente, String nomeFarmaco, float dosaggio) {
        gestioneNotifiche = new GestioneNotifiche(paziente);

        // Recupera il dosaggio già assunto per quel farmaco nel corso della giornata
        gestioneAssunzioneFarmaci = new GestioneAssunzioneFarmaci(paziente);
        float doseGiaAssunta = 0;
        List<AssunzioneFarmaco> farmaciAssunti = gestioneAssunzioneFarmaci.getListaAssunzioneFarmaci();
        Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(nomeFarmaco);

        if (!farmaciAssunti.isEmpty()) {
            for (AssunzioneFarmaco af : farmaciAssunti) {
                if (GestioneFarmaci.getInstance().getFarmacoById(af.getIdFarmaco()).getNome().equals(nomeFarmaco) && af.getData().equals(Date.valueOf(LocalDate.now()))) {
                    doseGiaAssunta += af.getDose();
                }
            }
        }

        // Sommiamo la dose complessiva già assunta per questo farmaco nell'arco della giornata
        dosaggio += doseGiaAssunta;

        // Verifichiamo che il dosaggio sia minore o uguale al dosaggio complessivo quotidiano per quel farmaco
        float doseComplessivaQuotidianaPrescritta = getDosaggioComplessivoQuotidianoPerFarmaco(paziente, nomeFarmaco);
        boolean check = (dosaggio <= doseComplessivaQuotidianaPrescritta);
        if(!check) {
            gestioneNotifiche.inserisciNuovaNotifica(GeneratoreNotifiche.getInstance().generaNotificaAssunzioneSovradosaggioFarmaci(paziente, dosaggio, doseComplessivaQuotidianaPrescritta, farmaco));
        }

        return check;
    }


    private float getDosaggioComplessivoQuotidianoPerFarmaco(Paziente paziente, String nomeFarmaco) {
        gestioneTerapie = new GestioneTerapie(paziente);
        List<Terapia> terapiePaziente = gestioneTerapie.getTerapiePaziente();
        List<List<FarmacoTerapia>> listaFarmaciConIndicazioni = new ArrayList<>();
        float dosaggio = 0;

        for (Terapia t : terapiePaziente) {
            listaFarmaciConIndicazioni.add(t.getListaFarmaciTerapia());
        }

        for (List<FarmacoTerapia> l : listaFarmaciConIndicazioni) {
            for (FarmacoTerapia f : l) {
                if (f.getFarmaco().getNome().equals(nomeFarmaco)) {
                    dosaggio += f.getIndicazioni().getDosaggio();
                }
            }
        }

        return dosaggio != 0 ? dosaggio : -1;
    }


    public boolean verificaCoerenzaOrarioAssunzione(Paziente paziente, String nomeFarmaco, Time oraRegistrata) {

        List<LocalTime> orariPrevisti = getOrariPrevisti(paziente, nomeFarmaco);

        LocalTime oraAttuale = LocalTime.now();
        LocalTime orarioPiuVicino = getOrarioPiuVicino(oraAttuale, orariPrevisti);

        LocalTime oraRegistrataLocal = oraRegistrata.toLocalTime();

        // Calcola differenza tra ora registrata e orario previsto più vicino
        Duration intervallo = Duration.between(oraRegistrataLocal, orarioPiuVicino).abs();

        return intervallo.toMinutes() <= 15;
    }

    private List<LocalTime> generaListaOrariAssunzione(String orariAssunzioneString) {
        List<LocalTime> orari = new ArrayList<>();
        if (orariAssunzioneString == null || orariAssunzioneString.isEmpty()) return orari;
        String[] orariString = orariAssunzioneString.split(", ");
        for (String ora : orariString) {
            orari.add(LocalTime.parse(ora));
        }
        return orari;
    }

    private LocalTime getOrarioPiuVicino(LocalTime riferimento, List<LocalTime> orari) {
        LocalTime orarioPiuVicino = null;
        Duration durataMinima = Duration.ofHours(24);

        for (LocalTime o : orari) {
            Duration durata = Duration.between(riferimento, o).abs(); // differenza assoluta
            if (durata.compareTo(durataMinima) < 0) {
                durataMinima = durata;
                orarioPiuVicino = o;
            }
        }

        return orarioPiuVicino;
    }

    private List<LocalTime> getOrariPrevisti(Paziente paziente, String nomeFarmaco) {
        gestioneTerapie = new GestioneTerapie(paziente);
        List<Terapia> terapiePaziente = gestioneTerapie.getTerapiePaziente();
        StringBuilder orariAssunzioneString = new StringBuilder();

        // Estrai gli orari di assunzione per il farmaco specifico
        for (Terapia t : terapiePaziente) {
            for (FarmacoTerapia f : t.getListaFarmaciTerapia()) {
                if (f.getFarmaco().getNome().equals(nomeFarmaco)) {
                    if (!orariAssunzioneString.isEmpty()) {
                        orariAssunzioneString.append(", ");
                    }

                    orariAssunzioneString.append(f.getIndicazioni().getOrariAssunzione());
                }
            }
        }

        return generaListaOrariAssunzione(orariAssunzioneString.toString());
    }


    // Verifica che il paziente abbia assunto il farmaco entro i 15 minuti prima o dopo rispetto all'orario di assunzione previsto
    // In caso di assunzione, ritorna true e il metodo verrà usato per rimuovere il farmaco dalla lista di visualizzazione dei farmaci registrabili finché non mancheranno
    // nuovamente 15 minuti alla prossima assunzione
    // In caso di mancata assunzione, il metodo ritornerà false e verrà utilizzato per segnalare al paziente la necessità di assumere i farmaci
    public boolean verificaAssunzioneRispettoAllOrario(Paziente paziente, String nomeFarmaco) {
        gestioneAssunzioneFarmaci = new GestioneAssunzioneFarmaci(paziente);
        List<AssunzioneFarmaco> farmaciAssuntiOggi = gestioneAssunzioneFarmaci.getListaFarmaciAssuntiOggi(Date.valueOf(LocalDate.now()), nomeFarmaco);

        // Recupera gli orari di assunzione previsti per il farmaco
        List<LocalTime> orariPrevisti = getOrariPrevisti(paziente, nomeFarmaco);
        if (orariPrevisti.isEmpty()) return false;

        // Trova l'orario più vicino rispetto all'orario attuale
        LocalTime oraAttuale = LocalTime.now();
        LocalTime orarioPiuVicino = getOrarioPiuVicino(oraAttuale, orariPrevisti);

        // Calcola la finestra di assunzione valida: -15 minuti fino a +∞
        LocalTime inizioFinestra = orarioPiuVicino.minusMinutes(15);

        // Estrai l’orario dell’ultima registrazione (se presente)
        Time oraUltimaRegistrazione = null;
        for (AssunzioneFarmaco af : farmaciAssuntiOggi) {
            if (GestioneFarmaci.getInstance().getFarmacoByName(nomeFarmaco).getNome().equals(nomeFarmaco)) {
                if (oraUltimaRegistrazione == null || af.getOra().after(oraUltimaRegistrazione)) {
                    oraUltimaRegistrazione = af.getOra(); // tiene l'assunzione più recente
                }
            }
        }

        // Se il farmaco è stato assunto dopo l’inizio della finestra per questo orario, consideralo "assunto"
        if (oraUltimaRegistrazione != null &&
                !oraUltimaRegistrazione.toLocalTime().isBefore(inizioFinestra)) {
            return true;
        }

        // Se non è presente alcuna registrazione odierna per questo farmaco, significa che non è stato assunto
        if (oraUltimaRegistrazione == null) return false;

        // Se non è stato assunto, e siamo entro i 15 minuti prima dell’orario o oltre, segnala che deve essere assunto
        return oraAttuale.isBefore(inizioFinestra); // Deve essere assunto (o è in ritardo)

    }


    public boolean presenzaFarmaciNonRegistrati(Paziente paziente) {

        List<Farmaco> farmaciPaziente = new ArrayList<>();
        gestioneTerapie = new GestioneTerapie(paziente);

        // Ottieni tutta la lista di farmaci per il paziente
        for (Terapia t : gestioneTerapie.getTerapiePaziente()) {
            if (t.getDataFine() == null) {
                for (FarmacoTerapia f : t.getListaFarmaciTerapia()) {
                    farmaciPaziente.add(f.getFarmaco());
                }
            }
        }

        int resultAssunzioni = 1;
        // Cicla sui farmaci e verifica se qualcuno tra questi non sia stato registrato come assunto
        for (Farmaco f : farmaciPaziente) {
            if (!verificaAssunzioneRispettoAllOrario(paziente, f.getNome())) {
                resultAssunzioni = 0;
                break;
            }
        }

        return resultAssunzioni == 0;
    }

    public void monitoraAssunzioneFarmaci(Paziente paziente) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (presenzaFarmaciNonRegistrati(paziente)) {
                    Platform.runLater(() -> {
                        ServizioNotifiche.getInstance().mostraNotifichePromemoriaAssunzioneFarmaci();
                    });
                }
            } catch (Exception e) {
                System.err.println("Errore durante il controllo periodico delle assunzioni dei farmaci: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void stopScheduler() {
        scheduler.shutdownNow();
        schedulerLivelliGlicemici.shutdownNow();
    }


    // Verifica se il paziente passato come parametro non stia assumendo la terapia farmacologica prescritta
    private boolean verificaSospensioneFarmaci(Paziente paziente) {
        gestioneTerapie = new GestioneTerapie(paziente);
        gestioneAssunzioneFarmaci = new GestioneAssunzioneFarmaci(paziente);
        List<List<FarmacoTerapia>> farmaciTerapie = new ArrayList<>();
        List<Farmaco> farmaciPrescritti = new ArrayList<>();

        for (Terapia t : gestioneTerapie.getTerapiePaziente()) {
            farmaciTerapie.add(t.getListaFarmaciTerapia());
        }

        if (farmaciTerapie.isEmpty()) {
            return false;
        }

        for (List<FarmacoTerapia> l : farmaciTerapie) {
            for (FarmacoTerapia f : l) {
                if (!farmaciPrescritti.contains(f.getFarmaco())) {
                    farmaciPrescritti.add(f.getFarmaco());
                }
            }
        }

        if (farmaciPrescritti.isEmpty()) {
            return false;
        }

        int farmaciNonAssuntiCounter = 0;
        List<Farmaco> farmaciNonAssunti = new ArrayList<>();

        for (Farmaco farmaco : farmaciPrescritti) {
            List<AssunzioneFarmaco> assunzioniDiQuestoFarmaco = new ArrayList<>();

            for (AssunzioneFarmaco af : gestioneAssunzioneFarmaci.getListaAssunzioneFarmaci()) {
                if (af.getIdFarmaco() == farmaco.getIdFarmaco() && af.getIdPaziente() == paziente.getIdUtente()) {
                    assunzioniDiQuestoFarmaco.add(af);
                }
            }

            if (!assunzioniDiQuestoFarmaco.isEmpty()) {
                LocalDate ultimaAssunzione = assunzioniDiQuestoFarmaco.getLast().getData().toLocalDate();
                long giorni = ChronoUnit.DAYS.between(ultimaAssunzione, LocalDate.now());

                if (giorni > 3) {
                    farmaciNonAssunti.add(farmaco);
                    farmaciNonAssuntiCounter++;
                }
            }
        }

        farmaciNonAssuntiDa3Giorni.put(paziente, farmaciNonAssunti);

        return farmaciNonAssuntiCounter != 0;
    }


    // Task automatico che verifica se uno dei pazienti presenti nel sistema non sta assumendo i suoi farmaci da più di 3 giorni
    public void monitoraSospensioneFarmaci() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (Paziente paziente : listaPazienti) {
                    if (verificaSospensioneFarmaci(paziente)) {
                        Platform.runLater(() -> {
                            ServizioNotifiche.getInstance().notificaSospensioneFarmacoTerapia(paziente);

                            for (Farmaco f : farmaciNonAssuntiDa3Giorni.get(paziente)) {
                                gestioneNotifiche.inserisciNuovaNotifica(GeneratoreNotifiche.getInstance().generaNotificaSospensioneFarmaci(paziente, f));
                            }
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("Errore durante il controllo periodico delle sospensioni dei farmaci: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.MINUTES);
    }

    // Recupera gli orari dei pasti per il paziente selezionato al fine di inviargli promemoria circa la registrazione dei valori glicemici
    public void monitoraRegistrazioneGlicemica(Paziente paziente) {
        Runnable task = () -> {
            try {
                gestionePasti = new GestionePasti(paziente);
                List<Pasto> pastiPaziente = gestionePasti.getPasti();
                List<LocalTime> orariPasti = new ArrayList<>();

                for (Pasto p : pastiPaziente) {
                    LocalTime orario = p.getOrario().toLocalTime();
                    orariPasti.add(orario.minusMinutes(20));  // 20 minuti prima del pasto
                    orariPasti.add(orario.plusHours(2));   // 2 ore dopo il pasto
                }

                LocalTime oraAttuale = LocalTime.now().withSecond(0).withNano(0); // allineato al minuto
                LocalTime orarioTarget = getOrarioPiuVicino(oraAttuale, orariPasti);

                if (orarioTarget.equals(oraAttuale)) {
                    Platform.runLater(() -> {
                        ServizioNotifiche.getInstance().notificaPromemoriaRegistrazioneGlicemia();
                    });
                }

                List<RilevazioneGlicemica> rilevazioniOdierne = new ArrayList<>(new GestioneRilevazioniGlicemia(paziente).getRilevazioniPerData(LocalDate.now()));

                if (!finestraRilevazioniGlicemicheAperta) {
                    if (rilevazioniOdierne.isEmpty()) {
                        Platform.runLater(() -> ServizioNotifiche.getInstance().notificaPromemoriaRegistrazioneGlicemia());
                    } else if (rilevazioniOdierne.getLast().getOra().toLocalTime().isBefore(orarioTarget)) {
                        Platform.runLater(() -> ServizioNotifiche.getInstance().notificaPromemoriaRegistrazioneGlicemia());
                    }
                }

            } catch (Exception e) {
                System.err.println("Errore nel caricamento del promemoria per la registrazione della glicemia: " + e.getMessage());
            }
        };

        long millisNow = System.currentTimeMillis();
        long delayMillis = 60000 - (millisNow % 60000); // sincronizzazione al minuto esatto

        scheduler.scheduleAtFixedRate(task, delayMillis, 60000, TimeUnit.MILLISECONDS);
    }

    public void setFinestraRilevazioniGlicemicheIsOpen() {
        finestraRilevazioniGlicemicheAperta = true;
    }

    public void setFinestraRilevazioniGlicemicheIsClose() {
        finestraRilevazioniGlicemicheAperta = false;
    }

    // Verifica i livelli glicemici in relazione ai pasti e classifica i diversi livelli in base alla gravità
    public List<Integer> verificaLivelliGlicemici(Paziente paziente, boolean odierne, boolean chiamatoDaMonitoraLivelliGlicemici) {
        // Legenda codici:
        //  0: normale (valido anche in caso di assenza di rilevazioniComplessive per la giornata)
        // -1: lieve anomalia a digiuno
        //  1: lieve anomalia post-prandiale
        // -2: moderata criticità a digiuno
        //  2: moderata criticità post-prandiale
        // -3: alta criticità a digiuno
        //  3: alta criticità post-prandiale
        // -4: emergenza medica a digiuno
        //  4: emergenza medica post-prandiale

        GestioneRilevazioniGlicemia gestioneRilevazioniGlicemia = new GestioneRilevazioniGlicemia(paziente);
        List<RilevazioneGlicemica> rilevazioniComplessive;
        List<RilevazioneGlicemica> rilevazioniOdierne;
        if (connessoComeMedico && chiamatoDaMonitoraLivelliGlicemici) {
            rilevazioniComplessive = gestioneRilevazioniGlicemia.getRilevazioniGlicemicheNonGestitePaziente();
            rilevazioniOdierne = gestioneRilevazioniGlicemia.getRilevazioniGlicemicheNonGestitePerData(LocalDate.now());
        } else {
            rilevazioniComplessive = gestioneRilevazioniGlicemia.getRilevazioni();
            rilevazioniOdierne = gestioneRilevazioniGlicemia.getRilevazioniPerData(LocalDate.now());
        }
        List<Integer> codiciLivelliOdierni = new ArrayList<>();
        List<Integer> codiciLivelliComplessivi = new ArrayList<>();

        if (rilevazioniComplessive == null && !odierne) return null;
        if (rilevazioniOdierne == null && odierne) return null;

        if (odierne) {
            for (RilevazioneGlicemica r : rilevazioniOdierne) {
                if (r.getIndicazioniTemporali().equals("prima")) {
                    if (r.getValore() >= 80 && r.getValore() <= 130) {
                        codiciLivelliOdierni.add(0);
                    } else if (r.getValore() >= 70 && r.getValore() < 80 || r.getValore() > 130 && r.getValore() <= 150) {
                        codiciLivelliOdierni.add(-1);
                    } else if (r.getValore() >= 60 && r.getValore() < 70 || r.getValore() > 150 && r.getValore() <= 180) {
                        codiciLivelliOdierni.add(-2);
                    } else if (r.getValore() >= 50 && r.getValore() < 60 || r.getValore() > 180 && r.getValore() <= 250) {
                        codiciLivelliOdierni.add(-3);
                    } else {
                        codiciLivelliOdierni.add(-4);
                    }
                } else {
                    if (r.getValore() >= 90 && r.getValore() < 180) {
                        codiciLivelliOdierni.add(0);
                    } else if (r.getValore() >= 80 && r.getValore() < 90 || r.getValore() >= 180 && r.getValore() < 200) {
                        codiciLivelliOdierni.add(1);
                    } else if (r.getValore() >= 70 && r.getValore() < 80 || r.getValore() >= 200 && r.getValore() < 250) {
                        codiciLivelliOdierni.add(2);
                    } else if (r.getValore() >= 60 && r.getValore() < 70 || r.getValore() >= 250 && r.getValore() < 300) {
                        codiciLivelliOdierni.add(3);
                    } else {
                        codiciLivelliOdierni.add(4);
                    }
                }
            }
        } else {
            for (RilevazioneGlicemica r : rilevazioniComplessive) {
                if (r.getIndicazioniTemporali().equals("prima")) {
                    if (r.getValore() >= 80 && r.getValore() <= 130) {
                        codiciLivelliComplessivi.add(0);
                    } else if (r.getValore() >= 70 && r.getValore() < 80 || r.getValore() > 130 && r.getValore() <= 150) {
                        codiciLivelliComplessivi.add(-1);
                    } else if (r.getValore() >= 60 && r.getValore() < 70 || r.getValore() > 150 && r.getValore() <= 180) {
                        codiciLivelliComplessivi.add(-2);
                    } else if (r.getValore() >= 50 && r.getValore() < 60 || r.getValore() > 180 && r.getValore() <= 250) {
                        codiciLivelliComplessivi.add(-3);
                    } else {
                        codiciLivelliComplessivi.add(-4);
                    }
                } else {
                    if (r.getValore() >= 90 && r.getValore() < 180) {
                        codiciLivelliComplessivi.add(0);
                    } else if (r.getValore() >= 80 && r.getValore() < 90 || r.getValore() >= 180 && r.getValore() < 200) {
                        codiciLivelliComplessivi.add(1);
                    } else if (r.getValore() >= 70 && r.getValore() < 80 || r.getValore() >= 200 && r.getValore() < 250) {
                        codiciLivelliComplessivi.add(2);
                    } else if (r.getValore() >= 60 && r.getValore() < 70 || r.getValore() >= 250 && r.getValore() < 300) {
                        codiciLivelliComplessivi.add(3);
                    } else {
                        codiciLivelliComplessivi.add(4);
                    }
                }
            }
        }

        return odierne ? codiciLivelliOdierni.reversed() : codiciLivelliComplessivi.reversed();
    }

    // Task in background per monitorare i livelli glicemici dei pazienti (su base giornaliera) e inviare alert ai medici
    private final ScheduledExecutorService schedulerLivelliGlicemici = Executors.newScheduledThreadPool(1);

    public void monitoraLivelliGlicemici() {
        schedulerLivelliGlicemici.scheduleAtFixedRate(() -> {
            List<Boolean> giaNotificato = new ArrayList<>(Collections.nCopies(listaPazienti.size(), false));

            for (int i = 0; i < listaPazienti.size(); i++) {
                try {
                    Paziente paziente = listaPazienti.get(i);
                    gestioneNotifiche = new GestioneNotifiche(paziente);
                    List<Integer> codici = verificaLivelliGlicemici(paziente, true, true);

                    if (codici != null && !codici.isEmpty() && !giaNotificato.get(i)) {
                        while (!codici.isEmpty()) {
                            GestioneRilevazioniGlicemia grg = new GestioneRilevazioniGlicemia(paziente);
                            List<RilevazioneGlicemica> rilevazioniNonGestite = grg.getRilevazioniGlicemicheNonGestitePerData(LocalDate.now());

                            if (rilevazioniNonGestite.isEmpty()) {
                                codici.removeFirst();
                                continue;
                            }

                            int severityCode = codici.getFirst();

                            if (severityCode != 0) {
                                CountDownLatch latch = new CountDownLatch(1);
                                final int index = i;
                                Platform.runLater(() -> {
                                    ServizioNotifiche.getInstance().notificaLivelliGlicemici(paziente, severityCode);
                                    gestioneNotifiche.inserisciNuovaNotifica(GeneratoreNotifiche.getInstance().generaNotificaLivelliGlicemiciAlterati(paziente, severityCode, rilevazioniNonGestite.getFirst().getValore()));
                                    pmc.resetListaNotifiche();

                                    giaNotificato.set(index, true);

                                    latch.countDown();
                                });
                                try {
                                    latch.await();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    System.err.println("Interrupted while waiting for UI update: " + e.getMessage());
                                }
                            }

                            if (!grg.updateStatoRilevazioneGlicemica(rilevazioniNonGestite.getFirst())) {
                                Alert erroreAggiornamentoStatoRilevazione = new Alert(Alert.AlertType.ERROR);
                                erroreAggiornamentoStatoRilevazione.setTitle("System Notification Service");
                                erroreAggiornamentoStatoRilevazione.setHeaderText("Errore aggiornamento dati");
                                erroreAggiornamentoStatoRilevazione.setContentText("Si è verificato un errore durante l'aggiornamento dello stato della rilevazione glicemica");
                                erroreAggiornamentoStatoRilevazione.showAndWait();
                            }

                            rilevazioniNonGestite.removeFirst();
                            codici.removeFirst();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel controllo della glicemia del paziente " + listaPazienti.get(i).getCodiceFiscale() + ": " + e.getMessage());
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }


    public void setConnessoComeMedico() {
        connessoComeMedico = true;
    }


    public void setPortaleMedicoInstance(PortaleMedicoController pmc) {
        this.pmc = pmc;
    }


    // Verifica l'obesità come fattore di rischio per il paziente passato in input
    public boolean isObeso(Paziente paziente) {
        float altezzaInMetri = (float)paziente.getAltezza() / 100;
        return (paziente.getPeso() / (altezzaInMetri * altezzaInMetri)) >= 30;
    }


    // VERIFICA LA PRESENZA DI NOTIFICHE NON LETTE E SEGNALA L'EVENTUALE PRESENZA AL MEDICO OGNI 5 MINUTI, NEL CASO IN CUI IL CENTRO NOTIFICHE FOSSE CHIUSO
    public void centroNotificheIsOpened() {
        statoCentroNotifiche = true;
    }

    public void centroNotificheIsClosed() {
        statoCentroNotifiche = false;
    }

    public void monitoraPresenzaNotificheNonVisualizzate() {

        scheduler.scheduleAtFixedRate(() -> {
            if (!statoCentroNotifiche) {
                for (Paziente paziente : GestionePazienti.getInstance().getListaPazienti()) {
                    GestioneNotifiche gestioneNotificheMonitor = new GestioneNotifiche(paziente);
                    if (!gestioneNotificheMonitor.getNotificheNonVisualizzate().isEmpty()) {
                        Platform.runLater(()->{
                            ServizioNotifiche.getInstance().notificaPresenzaNotificheNonVisualizzate();
                            pmc.aggiornaListaPazientiReferenteNotifiche();
                        });
                    }
                }
            }
        }, 0, 5, TimeUnit.MINUTES);
    }


    // GENERA I LOG DI SISTEMA PER TENER TRACCIA DI QUALE MEDICO ABBIA MODIFICATO COSA
    public void generaLog(Log tipoLog, Paziente paziente, Medico medico, boolean nuovo, boolean inseritaDalMedico) {

        switch (tipoLog) {
            case TERAPIA -> {
                GestioneTerapie gt = new GestioneTerapie(paziente);
                Terapia terapiaSelezionata = gt.getTerapiePaziente().getLast();

                Task<Void> creaLogTerapieTask = new Task<>() {

                    @Override
                    protected Void call() {
                        boolean success = GestioneLog.getInstance().generaLogTerapia(terapiaSelezionata, medico, paziente, nuovo, inseritaDalMedico);
                        if (!success) {
                            System.err.println("Si è verificato un errore durante la generazione del log per la terapia selezionata");
                        }

                        return null;
                    }
                };
                new Thread(creaLogTerapieTask).start();
            }


            case PATOLOGIA_CONCOMITANTE -> {
                GestionePatologieConcomitanti gpc = new GestionePatologieConcomitanti(paziente);
                PatologiaConcomitante patologiaSelezionata = gpc.getListaPatologieConcomitanti().getLast();

                Task<Void> creaLogPatologieTask = new Task<>() {

                    @Override
                    protected Void call() {
                        boolean success = GestioneLog.getInstance().generaLogPatologia(patologiaSelezionata, medico, paziente, nuovo, inseritaDalMedico);
                        if (!success) {
                            System.err.println("Si è verificato un errore durante la generazione del log per la patologia selezionata");
                        }

                        return null;
                    }
                };
                new Thread(creaLogPatologieTask).start();
            }


            case INFO_PAZIENTE -> {
                Task<Void> creaLogPazienteTask = new Task<>() {
                    @Override
                    protected Void call() {
                        boolean success = GestioneLog.getInstance().generaLogInfoPaziente(medico, paziente, inseritaDalMedico);
                        if (!success) {
                            System.err.println("Si è verificato un errore durante la generazione del log inerente le info del paziente selezionate");
                        }

                        return null;
                    }
                };
                new Thread(creaLogPazienteTask).start();
            }
        }
    }
}
