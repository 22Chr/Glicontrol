package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoNotifiche;
import com.univr.glicontrol.dao.AccessoNotificheImpl;

import java.util.List;

public class GestioneNotifiche {
    private final Paziente paziente;
    private final AccessoNotifiche accessoNotifiche = new AccessoNotificheImpl();
    private List<Notifica> notificheNonVisualizzate;

    public GestioneNotifiche(Paziente paziente) {
        this.paziente = paziente;
        notificheNonVisualizzate = accessoNotifiche.getNotificheNonVisualizzate(paziente);
    }

    public List<Notifica> getNotificheNonVisualizzate() {
        aggiornaListaNotificheNonVisualizzate();
        return notificheNonVisualizzate;
    }

    private void aggiornaListaNotificheNonVisualizzate() {
        notificheNonVisualizzate.clear();
        notificheNonVisualizzate = accessoNotifiche.getNotificheNonVisualizzate(paziente);
    }

    public void inserisciNuovaNotifica(Notifica nuovaNotifica) {
        accessoNotifiche.insertNuovaNotifica(nuovaNotifica);
    }

    public boolean aggiornaStatoNotifica(Notifica notifica) {
        return accessoNotifiche.updateStatoNotifica(notifica);
    }

}
