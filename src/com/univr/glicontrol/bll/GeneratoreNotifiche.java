package com.univr.glicontrol.bll;
import java.time.LocalDateTime;

public class GeneratoreNotifiche {
    LocalDateTime dataNotifica = LocalDateTime.now();

    private GeneratoreNotifiche() {}

    private static class Holder {
        private static final GeneratoreNotifiche INSTANCE = new GeneratoreNotifiche();
    }

    public static GeneratoreNotifiche getInstance() {
        return Holder.INSTANCE;
    }

    public Notifica generaNotificaLivelliGlicemiciAlterati(Paziente pazienteAssociato, int indiceSeverity, float livelloGlicemico) {
        String titolo = "ANOMALIA LIVELLI GLICEMICI";

        String messaggio = "Il paziente presenta ";
        switch(indiceSeverity) {
            case -1 -> messaggio += "una lieve anomalia nei livell\nglicemici precedenti al pasto: " + livelloGlicemico + " mg/dL";
            case 1 -> messaggio += "una lieve anomalia nei livelli\nglicemici post-prandiali: " + livelloGlicemico + " mg/dL";
            case -2 -> messaggio += "un livello glicemico precedente\nal pasto moderatamente critico: " + livelloGlicemico + " mg/dL.\nSi consiglia di tenere\nmonitorato l'andamento";
            case 2 -> messaggio += "un livello glicemico\npost-prandialemoderatamente critico: " + livelloGlicemico + " mg/dL.\nSi consiglia di tenere\nmonitorato l'andamento";
            case -3 -> messaggio += " un livello glicemico critico\nprecedente al pasto: " +  livelloGlicemico + " mg/dL.\nSi consiglia di rivedere la\ndieta e/o la terapia farmacologica";
            case 3 -> messaggio += " un livello glicemico\npost-prandiale critico: " +  livelloGlicemico + " mg/dL.\nSi consiglia di rivedere la\ndieta e/o la terapia farmacologica";
            case -4 -> messaggio += " un livello glicemico\nestremamente critico precedente al pasto: " + livelloGlicemico + " mg/dL.\nÈ necessario un intervento\nmedico immediato";
            case 4 -> messaggio += " un livello glicemico\npost-prandiale estremamente critico: " + livelloGlicemico + " mg/dL.\nÈ necessario un intervento\nmedico immediato";
        }

        return new Notifica(titolo, messaggio, pazienteAssociato, dataNotifica, false);
    }

    public Notifica generaNotificaAssunzioneSovradosaggioFarmaci(Paziente paziente, float doseAssunta, float dosePrescritta, Farmaco farmaco) {
        String titolo = "ASSUNZIONE SOVRADOSAGGIO FARMACO";
        String messaggio = "Il paziente ha assunto una dose eccessiva\ndel farmaco " + farmaco.getNome() + ".\nHa assunto " + (doseAssunta - dosePrescritta) + " " + farmaco.getUnitaMisura() + " in eccesso.\nSi consiglia di tener monitorato il paziente";
        return new Notifica(titolo, messaggio, paziente, dataNotifica, false);
    }

    public Notifica generaNotificaSospensioneFarmaci(Paziente paziente, Farmaco farmaco) {
        String titolo = "SOSPENSIONE ASSUNZIONE FARMACI";
        String messaggio = "Il paziente non assume il farmaco " + farmaco.getNome() + " da 3 giorni.\nSi consiglia di contattare il paziente";
        return new Notifica(titolo, messaggio, paziente, dataNotifica, false);
    }
}
