package com.univr.glicontrol.bll;

import com.univr.glicontrol.dao.AccessoLogTerapie;
import com.univr.glicontrol.dao.AccessoLogTerapieImpl;

import java.util.ArrayList;
import java.util.List;

public class GestioneLogTerapie {
    private List<LogTerapia> listaLogTerapia = new ArrayList<>();
    private final AccessoLogTerapie accessoLogTerapie = new AccessoLogTerapieImpl();

    public GestioneLogTerapie() {
        aggiornaListaTerapie();
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

    public boolean generaLogTerapia(Terapia terapia, int idMedico, String descrizioneModifiche, String notePaziente) {
        // Verifica quali modifiche sono state apportate alla terapia rispetto all'ultimo log per quella terapia
        // Se non ci sono modifiche, imposta il campo come stringa vuota

        boolean success = accessoLogTerapie.insertLogTerapia(terapia.getIdTerapia(), idMedico, descrizioneModifiche, notePaziente);

        if (success) {
            aggiornaListaTerapie();
        }

        return success;
    }

    private String generaDescrizioneModifiche(Terapia terapia, boolean nuova) {
        ListaMedici lm = new ListaMedici();
        Medico medico = lm.ottieniMedicoPerId(terapia.getIdMedicoUltimaModifica());
        StringBuilder descrizioneModificheLogTerapia = new StringBuilder();
        descrizioneModificheLogTerapia.append("DESCRIZIONE:\n");
        if (nuova) {
            descrizioneModificheLogTerapia = new StringBuilder("Il medico " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ") ha inserito la terapia " + terapia.getNome() + "\n");
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

        return descrizioneModificheLogTerapia.toString();
    }

    public String generaNotePazienteLogTerapia(Medico medico, String note) {
        return "NOTE:\n" +
                "- Medico: " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ")\n" +
                "- Note: \n" + note + "\n";
    }
}
