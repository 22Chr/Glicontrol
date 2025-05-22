package com.univr.glicontrol.bll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ServizioNotificheInterne {
    private List<Notifica> notifiche = new ArrayList<>();
    private final List<Consumer<Notifica>> listeners = new ArrayList<>();

    private ServizioNotificheInterne() {}

    private static class Holder {
        private static final ServizioNotificheInterne INSTANCE = new ServizioNotificheInterne();
    }

    public static ServizioNotificheInterne getInstance() {
        return Holder.INSTANCE;
    }

    public void post(Notifica nuovaNotifica) {
        notifiche.add(nuovaNotifica);
        listeners.forEach(listener -> listener.accept(nuovaNotifica));
    }

    public void subscribe(Consumer<Notifica> listener) {
        listeners.add(listener);
    }

    public List<Notifica> getTutteLeNotifiche() {
        return Collections.unmodifiableList(notifiche);
    }

}
