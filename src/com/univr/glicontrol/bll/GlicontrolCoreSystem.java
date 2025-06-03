package com.univr.glicontrol.bll;

import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlicontrolCoreSystem {
//    TODO
//     Verifica dei dosaggi dell'assunzione dei farmaci rispetto a quanto prescritto nella terapia. Invia alert in caso di assunzione incoerente
//     Invitare il paziente ad inserire i farmaci sulla base degli orari delle terapie. Definiti 30 minuti come tempo limite oltre cui scetterà l'alert
//     Verificare che il paziente non abbia sospeso i farmaci per più di tre giorni: in tal caso, inviare mail al referente + alert al paziente e a tutti i medici (quindi in portale medico)
//     Verifica i livelli di glicemia e:
//     1) Invia alert ai medici con codici differenti a seconda della gravità
//     2) Colora con tinte diverse i valori nelle liste delle rilevazioni (sia lato paziente che medico): verde ok, arancione insomma, rosso male

    private final ListaPazienti utilityListaPazienti = new ListaPazienti();
    private List<Paziente> listaPazienti = null;
    private GestioneTerapie gestioneTerapie = null;
    private GestioneAssunzioneFarmaci gestioneAssunzioneFarmaci = null;
    private final UtilityPortalePaziente utilityPortalePaziente = new UtilityPortalePaziente();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private GlicontrolCoreSystem() {
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
        if (!oraAttuale.isBefore(inizioFinestra)) {
            return false; // Deve essere assunto (o è in ritardo)
        }

        // Troppo presto, non mostrare ancora
        return true;
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
                    ServizioNotifiche notificheAssunzione = new ServizioNotifiche();
                    notificheAssunzione.mostraNotifichePromemoriaAssunzioneFarmaci();
                }
            } catch (Exception e) {
                System.err.println("Errore durante il controllo periodico delle assunzioni dei farmaci: " + e.getMessage());
            }
        }, 0, 10, TimeUnit.MINUTES);
    }

}
