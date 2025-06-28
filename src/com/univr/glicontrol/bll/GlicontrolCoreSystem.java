package com.univr.glicontrol.bll;

import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.application.Platform;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

public class GlicontrolCoreSystem {
//    TODO
//     Verifica dei dosaggi dell'assunzione dei farmaci rispetto a quanto prescritto nella terapia. Invia alert in caso di assunzione incoerente
//     Invitare il paziente ad inserire i farmaci sulla base degli orari delle terapie. Definiti 30 minuti come tempo limite oltre cui scetterà l'alert
//     Verificare che il paziente non abbia sospeso i farmaci per più di tre giorni: in tal caso, inviare mail al referente + alert al paziente e a tutti i medici (quindi in portale medico)
//     Verifica i livelli di glicemia e:
//     1) Invia alert ai medici con codici differenti a seconda della gravità
//     2) Colora con tinte diverse i valori nelle liste delle rilevazioni (sia lato paziente che medico): verde ok, arancione insomma, rosso male

    private final List<Paziente> listaPazienti;
    private GestioneTerapie gestioneTerapie = null;
    private GestioneAssunzioneFarmaci gestioneAssunzioneFarmaci = null;
    private final UtilityPortali up = new UtilityPortali();
    private GestionePasti gestionePasti = null;
    private GestioneRilevazioniGlicemia gestioneRilevazioniGlicemia = null;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private GlicontrolCoreSystem() {
        ListaPazienti utilityListaPazienti = new ListaPazienti();
        listaPazienti = utilityListaPazienti.getListaCompletaPazienti();
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

        // Recupera il dosaggio già assunto per quel farmaco nel corso della giornata
        gestioneAssunzioneFarmaci = new GestioneAssunzioneFarmaci(paziente);
        float doseGiaAssunta = 0;
        List<AssunzioneFarmaco> farmaciAssunti = gestioneAssunzioneFarmaci.getListaAssunzioneFarmaci();
        for (AssunzioneFarmaco af : farmaciAssunti) {
            if (GestioneFarmaci.getInstance().getFarmacoById(af.getIdFarmaco()).getNome().equals(nomeFarmaco) && af.getData().equals(Date.valueOf(LocalDate.now()))) {
                doseGiaAssunta += af.getDose();
            }
        }

        // Sommiamo la dose complessiva già assunta per questo farmaco nell'arco della giornata
        dosaggio += doseGiaAssunta;

        // Verifichiamo che il dosaggio sia minore o uguale al dosaggio complessivo quotidiano per quel farmaco
        return dosaggio <= getDosaggioComplessivoQuotidianoPerFarmaco(paziente, nomeFarmaco);
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
        List<AssunzioneFarmaco> farmaciAssuntiOggi = gestioneAssunzioneFarmaci
                .getListaFarmaciAssuntiOggi(Date.valueOf(LocalDate.now()), nomeFarmaco);

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

        // Se non è stato assunto, e siamo entro i 15 minuti prima dell’orario o oltre, segnala che deve essere assunto
        return oraAttuale.isBefore(inizioFinestra); // Deve essere assunto (o è in ritardo)

    }


    public boolean presenzaFarmaciNonRegistrati(Paziente paziente) {

        List<Farmaco> farmaciPaziente = new ArrayList<>();
        gestioneTerapie = new GestioneTerapie(paziente);

        // Ottieni tutta la lista di farmaci per il paziente
        for (Terapia t : gestioneTerapie.getTerapiePaziente()) {
            for (FarmacoTerapia f : t.getListaFarmaciTerapia()) {
                farmaciPaziente.add(f.getFarmaco());
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
                        ServizioNotifiche notificheAssunzione = new ServizioNotifiche();
                        notificheAssunzione.mostraNotifichePromemoriaAssunzioneFarmaci();
                    });
                }
            } catch (Exception e) {
                System.err.println("Errore durante il controllo periodico delle assunzioni dei farmaci: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void stopScheduler() {
        scheduler.shutdownNow();
    }


    // Verifica se il paziente passato come parametro non sta assumendo la terapia farmacologica prescritta
    private boolean verificaSospensioneFarmaci(Paziente paziente) {
        gestioneTerapie = new GestioneTerapie(paziente);
        gestioneAssunzioneFarmaci = new GestioneAssunzioneFarmaci(paziente);
        List<List<FarmacoTerapia>> farmaciTerapie = new ArrayList<>();
        List<Farmaco> farmaciPrescritti = new ArrayList<>();

        for (Terapia t : gestioneTerapie.getTerapiePaziente()) {
            farmaciTerapie.add(t.getListaFarmaciTerapia());
        }
        for (List<FarmacoTerapia> l : farmaciTerapie) {
            for (FarmacoTerapia f : l) {
                if (!farmaciPrescritti.contains(f.getFarmaco())) {
                    farmaciPrescritti.add(f.getFarmaco());
                }
            }
        }

        for (Farmaco farmaco : farmaciPrescritti) {
            List<AssunzioneFarmaco> assunzioniDiQuestoFarmaco = new ArrayList<>();
            for (AssunzioneFarmaco af : gestioneAssunzioneFarmaci.getListaAssunzioneFarmaci()) {
                if (af.getIdFarmaco() == farmaco.getIdFarmaco() && af.getIdPaziente() == paziente.getIdUtente()) {
                    assunzioniDiQuestoFarmaco.add(af);
                }
            }
            Duration intervallo = Duration.between((assunzioniDiQuestoFarmaco.getLast().getData().toLocalDate()), LocalDate.now()).abs();
            if (intervallo.toDays() > 3) {
                return true;
            }
        }

        return false;
    }


    // Task automatico che verifica se uno dei pazienti presenti nel sistema non sta assumendo i suoi farmaci da più di 3 giorni
    public void monitoraSospensioneFarmaci() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (Paziente paziente : listaPazienti) {
                    if (verificaSospensioneFarmaci(paziente)) {
                        Platform.runLater(() -> {
                            ServizioNotifiche notificheSospensione = new ServizioNotifiche();
                            notificheSospensione.notificaSospensioneFarmacoTerapia(paziente);
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("Errore durante il controllo periodico delle sospensioni dei farmaci: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    // Recupera gli orari dei pasti per il paziente selezionato al fine di inviargli promemoria circa la registrazione dei valori glicemici
    public void promemoriaRegistrazioneGlicemica(Paziente paziente) {
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
                        ServizioNotifiche notifichePasti = new ServizioNotifiche();
                        notifichePasti.notificaPromemoriaRegistrazioneGlicemia();
                    });
                }

            } catch (Exception e) {
                System.err.println("Errore nel caricamento del promemoria per la registrazione della glicemia: " + e.getMessage());
            }
        };

        long millisNow = System.currentTimeMillis();
        long delayMillis = 60000 - (millisNow % 60000); // sincronizzazione al minuto esatto

        scheduler.scheduleAtFixedRate(task, delayMillis, 60000, TimeUnit.MILLISECONDS);
    }

    // Verifica i livelli glicemici in relazione ai pasti e classifica i diversi livelli in base alla gravità
    public List<Integer> verificaLivelliGlicemici(Paziente paziente, boolean odierne) {
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

        gestioneRilevazioniGlicemia = new GestioneRilevazioniGlicemia(paziente);
        List<RilevazioneGlicemica> rilevazioniComplessive = gestioneRilevazioniGlicemia.getRilevazioni();
        List<RilevazioneGlicemica> rilevazioniOdierne = gestioneRilevazioniGlicemia.getRilevazioniPerData(LocalDate.now());
        List<Integer> codiciLivelliOdierni = new ArrayList<>();
        List<Integer> codiciLivelliComplessivi = new ArrayList<>();

        if (rilevazioniComplessive.isEmpty() && !odierne) return null;
        if (rilevazioniOdierne.isEmpty() && odierne) return null;

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
    public void monitoraLivelliGlicemici() {
        List<Boolean> giaNotificato = new ArrayList<>();
        for (int i = 0; i < listaPazienti.size(); i++) {
            giaNotificato.add(false);
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (int i = 0; i < listaPazienti.size(); i++) {
                    if (verificaLivelliGlicemici(listaPazienti.get(i), true) != null && !giaNotificato.get(i)) {
                        int livelloGlicemico = verificaLivelliGlicemici(listaPazienti.get(i), true).get(i);
                        if (livelloGlicemico != 0) {
                            int index = i;
                            Platform.runLater(() -> {
                                ServizioNotifiche notificheLivelli = new ServizioNotifiche();
                                notificheLivelli.notificaLivelliGlicemici(listaPazienti.get(index), livelloGlicemico);
                                giaNotificato.set(index, true);
                            });
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Errore nel controllo dei livelli glicemici: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);
    }


    // Verifica l'obesità come fattore di rischio per il paziente passato in input
    public boolean isObeso(Paziente paziente) {
        float altezzaInMetri = (float)paziente.getAltezza() / 100;
        return (paziente.getPeso() / (altezzaInMetri * altezzaInMetri)) >= 30;
    }
}
