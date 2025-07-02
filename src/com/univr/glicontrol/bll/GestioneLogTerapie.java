package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoLogTerapie;
import com.univr.glicontrol.dao.AccessoLogTerapieImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GestioneLogTerapie {
    private List<LogTerapia> listaLogTerapia = new ArrayList<>();
    private final AccessoLogTerapie accessoLogTerapie = new AccessoLogTerapieImpl();

    private GestioneLogTerapie() {
        aggiornaListaTerapie();
    }

    private static class Holder {
        private static final GestioneLogTerapie INSTANCE = new GestioneLogTerapie();
    }

    public static GestioneLogTerapie getInstance() {
        return Holder.INSTANCE;
    }

    private void aggiornaListaTerapie() {
        this.listaLogTerapia = accessoLogTerapie.getListaLogTerapie();
    }

   public List<LogTerapia> getListaLogTerapia() {
        aggiornaListaTerapie();
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

    public boolean generaLogTerapia(Terapia terapia, int idMedico, boolean nuovaTerapia, boolean inseritaDalMedico) {
        // Verifica quali modifiche sono state apportate alla terapia rispetto all'ultimo log per quella terapia
        // Se non ci sono modifiche, imposta il campo come stringa vuota

        LogTerapia ultimoLogPerTerapia = null;
        try {
             ultimoLogPerTerapia = ottieniLogPerTerapiaSpecifica(terapia.getIdTerapia()).getLast();
        } catch (NoSuchElementException e) {
            System.out.println("Non è ancora presente alcun log per la terapia selezionata");
        }

        String descrizioneModifiche = generaDescrizioneModifiche(terapia, nuovaTerapia, inseritaDalMedico);
        if (ultimoLogPerTerapia != null && ultimoLogPerTerapia.getDescrizioneModifiche().equals(descrizioneModifiche)) {
            descrizioneModifiche = "";
        }

        boolean success = accessoLogTerapie.insertLogTerapia(terapia.getIdTerapia(), idMedico, descrizioneModifiche, terapia.getNoteTerapia());

        if (success) {
            aggiornaListaTerapie();
        }

        return success;
    }

    private String generaDescrizioneModifiche(Terapia terapia, boolean nuovaTerapia, boolean inseritaDalMedico) {
        ListaMedici lm = new ListaMedici();
        ListaPazienti lp = new ListaPazienti();
        Medico medico = lm.ottieniMedicoPerId(terapia.getIdMedicoUltimaModifica());
        StringBuilder descrizioneModificheLogTerapia = new StringBuilder();
        descrizioneModificheLogTerapia.append("DESCRIZIONE:\n");
        if (nuovaTerapia) {
            if (inseritaDalMedico) {
                descrizioneModificheLogTerapia = new StringBuilder("Il medico " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ") ha inserito la terapia " + terapia.getNome() + "\n");
            } else {
                Paziente paziente = lp.getPazientePerId(terapia.getIdPaziente());
                descrizioneModificheLogTerapia = new StringBuilder("Il paziente " + paziente.getNome() + " " + paziente.getCognome() + " (" + paziente.getCodiceFiscale() + ") ha inserito la terapia " + terapia.getNome() + "\n");
            }
            descrizioneModificheLogTerapia.append("- Data inizio: ").append(terapia.getDataInizio()).append("\n");
            if (terapia.getDataFine() == null) {
                descrizioneModificheLogTerapia.append("- Data fine: in corso\n");
            } else {
                descrizioneModificheLogTerapia.append("- Data fine: ").append(terapia.getDataFine()).append("\n");
            }
        } else {
            descrizioneModificheLogTerapia = new StringBuilder("Il medico " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ") ha apportato le seguenti modifiche alla terapia " + terapia.getNome() + "\n");
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
}
