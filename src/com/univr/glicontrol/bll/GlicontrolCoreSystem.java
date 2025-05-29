package com.univr.glicontrol.bll;

import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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



    private float getDosaggioDaNomeFarmaco(String nomeFarmaco) {
        StringBuilder stringaDosaggio = new StringBuilder();
        for (int i = 0; i < nomeFarmaco.length(); i++) {
            if (Character.isDigit(nomeFarmaco.charAt(i))) {
                stringaDosaggio.append(nomeFarmaco.charAt(i));
            }
        }

        if (stringaDosaggio.isEmpty()) {
            return -1;
        }

        return Float.parseFloat(stringaDosaggio.toString());
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
}
