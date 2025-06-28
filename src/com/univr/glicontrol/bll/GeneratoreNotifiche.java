package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoNotifiche;
import com.univr.glicontrol.dao.AccessoNotificheImpl;

import java.time.LocalDateTime;

public class GeneratoreNotifiche {
    AccessoNotifiche accessoNotifiche = new AccessoNotificheImpl();

    private GeneratoreNotifiche() {}

    private static class Holder {
        private static final GeneratoreNotifiche INSTANCE = new GeneratoreNotifiche();
    }

    public static GeneratoreNotifiche getInstance() {
        return Holder.INSTANCE;
    }

    public void generaNotificaLivelliGlicemiciAlterati(Paziente pazienteAssociato, int indiceSeverity, float livelloGlicemico) {
        String titolo = "ANOMALIA LIVELLI GLICEMICI";

        String messaggio = "Il paziente presenta ";
        switch(indiceSeverity) {
            case -1 -> messaggio += "una lieve anomalia nei livell\nglicemici precedenti al pasto: " + livelloGlicemico + " mg/dL";
            case 1 -> messaggio += "una lieve anomalia nei livelli\nglicemici post-prandiali: " + livelloGlicemico + " mg/dL";
            case -2 -> messaggio += "un livello glicemico precedene\nal pasto moderatamente critico: " + livelloGlicemico + " mg/dL.\nSi consiglia di tenere\nmonitorato l'andamento";
            case 2 -> messaggio += "un livello glicemico\npost-prandialemoderatamente critico: " + livelloGlicemico + " mg/dL.\nSi consiglia di tenere\nmonitorato l'andamento";
            case -3 -> messaggio += " un livello glicemico critico\nprecedente al pasto: " +  livelloGlicemico + " mg/dL.\nSi consigla di rivedere la\ndieta e/o la terapia farmacologica";
            case 3 -> messaggio += " un livello glicemico\npost-prandiale critico: " +  livelloGlicemico + " mg/dL.\nSi consigla di rivedere la\ndieta e/o la terapia farmacologica";
            case -4 -> messaggio += " un livello glicemico\nestremamente critico precedente al pasto: " + livelloGlicemico + " mg/dL.\nÈ necessario un intervento\nmedico immediato";
            case 4 -> messaggio += " un livello glicemico\npost-prandialeestremamente critico: " + livelloGlicemico + " mg/dL.\nÈ necessario un intervento\nmedico immediato";
        }

        LocalDateTime dataNotifica = LocalDateTime.now();

        accessoNotifiche.insertNuovaNotifica(new Notifica(titolo, messaggio, pazienteAssociato, dataNotifica, false));
    }
}


// -1: lieve anomalia a digiuno
//  1: lieve anomalia post-prandiale
// -2: moderata criticità a digiuno
//  2: moderata criticità post-prandiale
// -3: alta criticità a digiuno
//  3: alta criticità post-prandiale
// -4: emergenza medica a digiuno
//  4: emergenza medica post-prandiale