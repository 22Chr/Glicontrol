package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Notifica;
import com.univr.glicontrol.bll.Paziente;

import java.util.List;
import java.util.Map;

public interface AccessoNotifiche {
    List<Notifica> getNotificheNonVisualizzate(Paziente paziente);
    boolean insertNuovaNotifica(Notifica nuovaNotifica);
    boolean updateStatoNotifica(Notifica nuovaNotifica);
}
