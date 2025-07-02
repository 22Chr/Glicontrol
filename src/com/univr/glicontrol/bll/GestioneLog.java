package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoLog;
import com.univr.glicontrol.dao.AccessoLogImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GestioneLog {
    private List<LogTerapia> listaLogTerapia = new ArrayList<>();
    private final AccessoLog accessoLog = new AccessoLogImpl();

    private GestioneLog() {
        aggiornaListaLogTerapie();
    }

    private static class Holder {
        private static final GestioneLog INSTANCE = new GestioneLog();
    }

    public static GestioneLog getInstance() {
        return Holder.INSTANCE;
    }

    private void aggiornaListaLogTerapie() {
        this.listaLogTerapia = accessoLog.getListaLogTerapie();
    }

   public List<LogTerapia> getListaLogTerapia() {
        aggiornaListaLogTerapie();
        return listaLogTerapia;
   }

    public List<LogTerapia> ottieniLogPerTerapiaSpecifica(int idTerapia) {
        List<LogTerapia> listaLogTerapiaSpecifica = new ArrayList<>();

        for (LogTerapia logTerapia : listaLogTerapia) {
            if (logTerapia.getIdTerapia() == idTerapia) {
                listaLogTerapiaSpecifica.add(logTerapia);
            }
        }

        return listaLogTerapiaSpecifica;
    }

    private String generaDescrizioneModifiche(Terapia terapia, Medico medico, Paziente paziente, boolean nuovaTerapia, boolean inseritaDalMedico) {
        StringBuilder descrizioneModificheLogTerapia = new StringBuilder();
        descrizioneModificheLogTerapia.append("DESCRIZIONE:\n");
        if (nuovaTerapia) {
            if (inseritaDalMedico) {
                descrizioneModificheLogTerapia = new StringBuilder("Il medico " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ") ha inserito la terapia " + terapia.getNome() + "\n");
            } else {
                descrizioneModificheLogTerapia = new StringBuilder("Il paziente " + paziente.getNome() + " " + paziente.getCognome() + " (" + paziente.getCodiceFiscale() + ") ha inserito la terapia " + terapia.getNome() + "\n");
            }
            descrizioneModificheLogTerapia.append("- Data inizio: ").append(terapia.getDataInizio()).append("\n");
            if (terapia.getDataFine() == null) {
                descrizioneModificheLogTerapia.append("- Data fine: in corso\n");
            } else {
                descrizioneModificheLogTerapia.append("- Data fine: ").append(terapia.getDataFine()).append("\n");
            }
        } else {
            descrizioneModificheLogTerapia = new StringBuilder("Il medico " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ") ha apportato le seguenti modifiche alla terapia " + terapia.getNome() + ":\n");
        }

        if (terapia.getDataFine() != null) {
            descrizioneModificheLogTerapia.append("- Data fine: ").append(terapia.getDataFine()).append("\n");
        }
        descrizioneModificheLogTerapia.append("\nFarmaci con relative indicazioni:\n");
        List<FarmacoTerapia> ft = terapia.getListaFarmaciTerapia();
        for (FarmacoTerapia f : ft) {
            descrizioneModificheLogTerapia.append("- ").append(f.getFarmaco().getNome()).append(":\n");
            descrizioneModificheLogTerapia.append("  - Dosaggio: ").append(f.getIndicazioni().getDosaggio()).append(" ").append(f.getFarmaco().getUnitaMisura()).append("\n");
            descrizioneModificheLogTerapia.append("  - Frequenza di assunzione: ").append(f.getIndicazioni().getFrequenzaAssunzione()).append("\n");
            descrizioneModificheLogTerapia.append("  - Orari di assunzione: ").append(f.getIndicazioni().getOrariAssunzione()).append("\n");
        }

        descrizioneModificheLogTerapia.append("NOTE: ").append(terapia.getNoteTerapia()).append("\n\n");

        return descrizioneModificheLogTerapia.toString();
    }

    public boolean generaLogTerapia(Terapia terapia, Medico medico, Paziente paziente, boolean nuovaTerapia, boolean inseritaDalMedico) {
        // Verifica quali modifiche sono state apportate alla terapia rispetto all'ultimo log per quella terapia
        // Se non ci sono modifiche, imposta il campo come stringa vuota

        LogTerapia ultimoLogPerTerapia = null;
        try {
             ultimoLogPerTerapia = ottieniLogPerTerapiaSpecifica(terapia.getIdTerapia()).getFirst();
        } catch (NoSuchElementException _) {}

        String descrizioneModifiche = generaDescrizioneModifiche(terapia, medico, paziente, nuovaTerapia, inseritaDalMedico);
        if (ultimoLogPerTerapia != null && ultimoLogPerTerapia.getDescrizione().equals(descrizioneModifiche)) {
            descrizioneModifiche = "Non è stata apportata alcuna modifica rispetto all'ultimo log";
        }

        boolean success = accessoLog.insertLogTerapia(terapia.getIdTerapia(), terapia.getIdMedicoUltimaModifica(), descrizioneModifiche, terapia.getNoteTerapia());

        if (success) {
            aggiornaListaLogTerapie();
        }

        return success;
    }


}
