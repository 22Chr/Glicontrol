package com.univr.glicontrol.bll;

import com.mysql.cj.log.Log;
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

    public boolean aggiungiLogTerapia(LogTerapia cache, int idTerapia, int idMedico, String descrizioneModifiche, String notePaziente) {
        // Verifica quali modifiche sono state apportate alla terapia rispetto all'ultimo log per quella terapia
        // Se non ci sono modifiche, imposta il campo come stringa vuota
        if (descrizioneModifiche.equals(cache.getDescrizioneModifiche())) {
            descrizioneModifiche = "";
        }

        if (notePaziente.equals(cache.getNotePaziente())) {
            notePaziente = "";
        }

        boolean success = accessoLogTerapie.insertLogTerapia(idTerapia, idMedico, descrizioneModifiche, notePaziente);

        if (success) {
            aggiornaListaTerapie();
        }

        return success;
    }

    public String generaDescrizioneLogTerapia(Terapia terapia, boolean nuova) {
        ListaMedici lm = new ListaMedici();
        Medico medico = lm.ottieniMedicoPerId(terapia.getIdMedicoUltimaModifica());
        StringBuilder descrizioneLogTerapia = new StringBuilder();
        descrizioneLogTerapia.append("DESCRIZIONE:\n");
        if (nuova) {
            descrizioneLogTerapia = new StringBuilder("Il medico " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ") ha inserito la terapia " + terapia.getNome() + "\n");
            descrizioneLogTerapia.append("- Data inizio: ").append(terapia.getDataInizio()).append("\n");
            if (terapia.getDataFine() == null) {
                descrizioneLogTerapia.append("- Data fine: in corso\n");
            } else {
                descrizioneLogTerapia.append("- Data fine: ").append(terapia.getDataFine()).append("\n");
            }
        } else {
            descrizioneLogTerapia = new StringBuilder("Il medico " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ") ha apportato le seguenti modifiche alla terapia " + terapia.getNome() + "\n");
        }

        if (terapia.getDataFine() != null) {
            descrizioneLogTerapia.append("- Data fine: ").append(terapia.getDataFine()).append("\n");
        }
        descrizioneLogTerapia.append("\nFarmaci con relative indicazioni:\n");
        List<FarmacoTerapia> ft = terapia.getListaFarmaciTerapia();
        for (FarmacoTerapia f : ft) {
            descrizioneLogTerapia.append("- ").append(f.getFarmaco().getNome()).append(":\n");
            descrizioneLogTerapia.append("  - Dosaggio: ").append(f.getIndicazioni().getDosaggio()).append(" ").append(f.getFarmaco().getUnitaMisura()).append("\n");
            descrizioneLogTerapia.append("  - Frequenza di assunzione: ").append(f.getIndicazioni().getFrequenzaAssunzione()).append("\n");
            descrizioneLogTerapia.append("  - Orari di assunzione: ").append(f.getIndicazioni().getOrariAssunzione()).append("\n");
        }

        return descrizioneLogTerapia.toString();
    }

    public String generaNotePazienteLogTerapia(Medico medico, String note) {
        return "NOTE:\n" +
                "- Medico: " + medico.getNome() + " " + medico.getCognome() + " (" + medico.getCodiceFiscale() + ")\n" +
                "- Note: \n" + note + "\n";
    }
}
