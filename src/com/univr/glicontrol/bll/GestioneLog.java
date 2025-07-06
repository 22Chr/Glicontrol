package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoLog;
import com.univr.glicontrol.dal.AccessoLogImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GestioneLog {
    private List<LogTerapia> listaLogTerapia = new ArrayList<>();
    private final AccessoLog accessoLog = new AccessoLogImpl();
    private List<LogPatologie> listaLogPatologie = new ArrayList<>();
    private List<LogInfoPaziente> listaLogInfoPaziente = new ArrayList<>();

    private GestioneLog() {
        aggiornaListaLogTerapie();
    }

    private static class Holder {
        private static final GestioneLog INSTANCE = new GestioneLog();
    }

    public static GestioneLog getInstance() {
        return Holder.INSTANCE;
    }


    //METODI PER LE TERAPIE
    private void aggiornaListaLogTerapie() {
        this.listaLogTerapia = accessoLog.getListaLogTerapie();
    }

   public List<LogTerapia> getListaLogTerapia() {
        aggiornaListaLogTerapie();
        return listaLogTerapia.reversed();
   }

    public List<LogTerapia> ottieniLogPerTerapiaSpecifica(int idTerapia) {
        List<LogTerapia> listaLogTerapiaSpecifica = new ArrayList<>();

        for (LogTerapia logTerapia : listaLogTerapia) {
            if (logTerapia.getIdTerapia() == idTerapia) {
                listaLogTerapiaSpecifica.add(logTerapia);
            }
        }

        return listaLogTerapiaSpecifica.reversed();
    }

    private String generaDescrizioneTerapia(Terapia terapia, Medico medico, Paziente paziente, boolean nuovaTerapia, boolean inseritaDalMedico) {
        StringBuilder descrizioneModificheLogTerapia = new StringBuilder("[LOG TERAPIE] - DESCRIZIONE:\n");

        if (nuovaTerapia) {
            if (inseritaDalMedico) {
                descrizioneModificheLogTerapia.append("Il medico ").append(medico.getNome()).append(" ").append(medico.getCognome()).append(" (").append(medico.getCodiceFiscale()).append(") ha inserito la terapia ").append(terapia.getNome()).append("\n");
            } else {
                descrizioneModificheLogTerapia.append("Il paziente ").append(paziente.getNome()).append(" ").append(paziente.getCognome()).append(" (").append(paziente.getCodiceFiscale()).append(") ha inserito la terapia ").append(terapia.getNome()).append("\n");
            }
            descrizioneModificheLogTerapia.append("- Data inizio: ").append(terapia.getDataInizio()).append("\n");
            if (terapia.getDataFine() == null) {
                descrizioneModificheLogTerapia.append("- Data fine: in corso\n");
            } else {
                descrizioneModificheLogTerapia.append("- Data fine: ").append(terapia.getDataFine()).append("\n");
            }
        } else {
            descrizioneModificheLogTerapia.append("Il medico ").append(medico.getNome()).append(" ").append(medico.getCognome()).append(" (").append(medico.getCodiceFiscale()).append(") ha apportato delle modifiche alla terapia ").append(terapia.getNome()).append(":\n");
        }

        if (terapia.getDataFine() != null) {
            descrizioneModificheLogTerapia.append("- Data fine: ").append(terapia.getDataFine()).append("\n");
        }
        descrizioneModificheLogTerapia.append("\nFARMACI CON RELATIVE INDICAZIONI:\n");
        List<FarmacoTerapia> ft = terapia.getListaFarmaciTerapia();
        for (FarmacoTerapia f : ft) {
            descrizioneModificheLogTerapia.append(f.getFarmaco().getNome()).append(":\n");
            descrizioneModificheLogTerapia.append("  - Dosaggio complessivo: ").append(f.getIndicazioni().getDosaggio()).append(" ").append(f.getFarmaco().getUnitaMisura()).append("\n");
            descrizioneModificheLogTerapia.append("  - Frequenza di assunzione: ").append(f.getIndicazioni().getFrequenzaAssunzione()).append("\n");
            descrizioneModificheLogTerapia.append("  - Orari di assunzione: ").append(f.getIndicazioni().getOrariAssunzione()).append("\n");
            descrizioneModificheLogTerapia.append("\n");
        }

        descrizioneModificheLogTerapia.append("\n");

        descrizioneModificheLogTerapia.append("NOTE:\n").append(terapia.getNoteTerapia()).append("\n\n");

        descrizioneModificheLogTerapia.append("(").append(LocalDate.now()).append(" - ").append(LocalTime.now()).append(")\n\n\n\n");

        return descrizioneModificheLogTerapia.toString();
    }

    public boolean generaLogTerapia(Terapia terapia, Medico medico, Paziente paziente, boolean nuovaTerapia, boolean inseritaDalMedico) {
        // Verifica quali modifiche sono state apportate alla terapia rispetto all'ultimo log per quella terapia
        // Se non ci sono modifiche, imposta il campo come stringa vuota

        LogTerapia ultimoLogPerTerapia = null;
        try {
             ultimoLogPerTerapia = ottieniLogPerTerapiaSpecifica(terapia.getIdTerapia()).getFirst();
        } catch (NoSuchElementException _) {}

        String descrizioneModifiche = generaDescrizioneTerapia(terapia, medico, paziente, nuovaTerapia, inseritaDalMedico);
        if (ultimoLogPerTerapia != null && ultimoLogPerTerapia.getDescrizione().equals(descrizioneModifiche)) {
            descrizioneModifiche = "Non è stata apportata alcuna modifica rispetto all'ultimo log";
        }

        boolean success = accessoLog.insertLogTerapia(terapia.getIdTerapia(), terapia.getIdMedicoUltimaModifica(), descrizioneModifiche, terapia.getNoteTerapia());

        if (success) {
            aggiornaListaLogTerapie();
        }

        return success;
    }


    //METODI PER LE PATOLOGIE
    private void aggiornaListaLogPatologie() {
        this.listaLogPatologie = accessoLog.getListaLogPatologie();
    }

    public List<LogPatologie> getListaLogPatologie() {
        aggiornaListaLogPatologie();
        return listaLogPatologie.reversed();
    }

    public List<LogPatologie> ottieniLogPerPatologiaSpecifica(int idPatologia) {
        List<LogPatologie> listaLogPatologiaSpecifica = new ArrayList<>();

        for (LogPatologie logPatologie : listaLogPatologie) {
            if (logPatologie.getIdPatologia() == idPatologia) {
                listaLogPatologiaSpecifica.add(logPatologie);
            }
        }

        return listaLogPatologiaSpecifica.reversed();
    }

    private String generaDescrizionePatologia(PatologiaConcomitante patologia, Medico medico, Paziente paziente, boolean nuova, boolean inseritaDalMedico) {
        StringBuilder descrizioneLogPatologia = new StringBuilder("[LOG PATOLOGIE] - DESCRIZIONE:\n");

        if (nuova) {
            if (inseritaDalMedico) {
                descrizioneLogPatologia.append("Il medico ").append(medico.getNome()).append(" ").append(medico.getCognome()).append(" (").append(medico.getCodiceFiscale()).append(") ha inserito la patologia ").append(patologia.getNomePatologia()).append(" per il paziente ").append(paziente.getNome()).append(" ").append(paziente.getCognome()).append("( ").append(paziente.getCodiceFiscale()).append(")\n");
            } else {
                descrizioneLogPatologia.append("Il paziente ").append(paziente.getNome()).append(" ").append(paziente.getCognome()).append(" (").append(paziente.getCodiceFiscale()).append(") ha inserito la patologia ").append(patologia.getNomePatologia()).append("\n");
            }
            descrizioneLogPatologia.append("- Data inizio: ").append(patologia.getDataInizio()).append("\n");
            if (patologia.getDataFine() == null) {
                descrizioneLogPatologia.append("- Data fine: in corso\n\n");
            } else {
                descrizioneLogPatologia.append("- Data fine: ").append(patologia.getDataFine()).append("\n\n");
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataFine = LocalDate.now();
            String dataFineFormattata = formatter.format(dataFine);

            descrizioneLogPatologia.append("Il medico ").append(medico.getNome()).append(" ").append(medico.getCognome()).append(" (").append(medico.getCodiceFiscale()).append(") ha segnalato la patologia ").append(patologia.getNomePatologia()).append(" come conclusa in data ").append(dataFineFormattata).append("\n\n");
        }

        descrizioneLogPatologia.append("(").append(LocalDate.now()).append(" - ").append(LocalTime.now()).append(")\n\n\n\n");

        return descrizioneLogPatologia.toString();
    }

    public boolean generaLogPatologia(PatologiaConcomitante patologia, Medico medico, Paziente paziente, boolean nuovaPatologia, boolean inseritaDalMedico) {
        // Verifica quali modifiche sono state apportate alla patologia rispetto all'ultimo log per quella patologia
        // Se non ci sono modifiche, imposta il campo come stringa vuota

        LogPatologie ultimoLogPerPatologia = null;
        try {
            ultimoLogPerPatologia = ottieniLogPerPatologiaSpecifica(patologia.getIdPatologia()).getFirst();
        } catch (NoSuchElementException _) {}

        String descrizioneModifiche = generaDescrizionePatologia(patologia, medico, paziente, nuovaPatologia, inseritaDalMedico);

        if (ultimoLogPerPatologia != null && ultimoLogPerPatologia.getDescrizione().equals(descrizioneModifiche)) {
            descrizioneModifiche = "Non è stata apportata alcuna modifica rispetto all'ultimo log";
        }

        boolean success = accessoLog.insertLogPatologie(patologia.getIdPatologia(), medico.getIdUtente(), descrizioneModifiche);

        if (success) {
            aggiornaListaLogPatologie();
        }

        return success;
    }


    //METODI PER INFO PAZIENTE
    private void aggiornaListaLogInfoPaziente() {
        this.listaLogInfoPaziente = accessoLog.getListaLogInfoPaziente();
    }

    public List<LogInfoPaziente> getListaLogInfoPaziente() {
        aggiornaListaLogInfoPaziente();
        return listaLogInfoPaziente.reversed();
    }

    public List<LogInfoPaziente> ottieniLogPerInfoPazienteSpecificata(int idPaziente) {
        List<LogInfoPaziente> listaLogInfoPazienteSpecifica = new ArrayList<>();

        for (LogInfoPaziente logInfoPaziente : listaLogInfoPaziente) {
            if (logInfoPaziente.getIdPaziente() == idPaziente) {
                listaLogInfoPazienteSpecifica.add(logInfoPaziente);
            }
        }

        return listaLogInfoPazienteSpecifica.reversed();
    }

    private String generaDescrizioneInfoPaziente(Medico medico, Paziente paziente, boolean inseriteDalMedico) {
        StringBuilder descrizioneLogInfoPaziente = new StringBuilder("[LOG INFORMAZIONI PAZIENTI] - DESCRIZIONE:\n");
        FattoriRischio fattoriRischio = new GestioneFattoriRischio().getFattoriRischio(paziente.getIdUtente());
        GestionePasti gp = new GestionePasti(paziente);

        if (inseriteDalMedico) {
            descrizioneLogInfoPaziente.append("Il medico ").append(medico.getNome()).append(" ").append(medico.getCognome()).append(" (").append(medico.getCodiceFiscale()).append(") ").append("ha modificato le informazioni del paziente:\n\n");
        } else {
            descrizioneLogInfoPaziente.append("Il paziente ").append(paziente.getNome()).append(" ").append(paziente.getCognome()).append(" (").append(paziente.getCodiceFiscale()).append(") ").append("ha modificato le proprie informazioni personali:\n\n");
        }

        descrizioneLogInfoPaziente.append("ANAGRAFICA:\n");
        descrizioneLogInfoPaziente.append("Nome: ").append(paziente.getNome()).append("\n");
        descrizioneLogInfoPaziente.append("Cognome: ").append(paziente.getCognome()).append("\n");
        descrizioneLogInfoPaziente.append("Codice fiscale: ").append(paziente.getCodiceFiscale()).append("\n");
        descrizioneLogInfoPaziente.append("Email: ").append(paziente.getEmail()).append("\n");
        descrizioneLogInfoPaziente.append("Altezza: ").append(paziente.getAltezza()).append(" cm\n");
        descrizioneLogInfoPaziente.append("Peso: ").append(paziente.getPeso()).append(" kg\n\n");

        descrizioneLogInfoPaziente.append("FATTORI DI RISCHIO PRINCIPALI:\n");
        if (fattoriRischio.getFumatore() == 1) descrizioneLogInfoPaziente.append("Fumatore\n");
        if (fattoriRischio.getProblemiAlcol() == 1) descrizioneLogInfoPaziente.append("Problemi di alcolismo\n");
        if (fattoriRischio.getFamiliarita() == 1) descrizioneLogInfoPaziente.append("Familiarità con il diabete\n");
        if (fattoriRischio.getSedentarieta() == 1) descrizioneLogInfoPaziente.append("Vita sedentaria\n");
        if (fattoriRischio.getAlimentazioneScorretta() == 1) descrizioneLogInfoPaziente.append("Alimentazione scorretta\n");
        if (fattoriRischio.getObesita()) descrizioneLogInfoPaziente.append("Obesità\n\n");

        if (!paziente.getAllergie().isEmpty()) {
            descrizioneLogInfoPaziente.append("\nALLERGIE A FARMACI:\n");
            descrizioneLogInfoPaziente.append(paziente.getAllergie()).append("\n\n");
        }

        descrizioneLogInfoPaziente.append("PASTI:\n");
        for (Pasto pasto : gp.getPasti()) {
            descrizioneLogInfoPaziente.append(pasto.getNomePasto()).append(" - ").append(pasto.getOrario()).append("\n\n");
        }

        descrizioneLogInfoPaziente.append("(").append(LocalDate.now()).append(" - ").append(LocalTime.now()).append(")\n\n\n\n");

        return descrizioneLogInfoPaziente.toString();
    }

    public boolean generaLogInfoPaziente(Medico medico, Paziente paziente, boolean inseriteDalMedico) {

        LogInfoPaziente ultimoLogInfo = null;
        try {
            ultimoLogInfo = ottieniLogPerInfoPazienteSpecificata(paziente.getIdUtente()).getFirst();
        } catch (NoSuchElementException _) {}

        String descrizioneInfoPaziente = generaDescrizioneInfoPaziente(medico, paziente, inseriteDalMedico);

        if (ultimoLogInfo != null && ultimoLogInfo.getDescrizione().equals(descrizioneInfoPaziente)) {
            descrizioneInfoPaziente = "Non è stata apportata alcuna modifica rispetto all'ultimo log";
        }

        boolean success = accessoLog.insertLogInfoPaziente(medico.getIdUtente(), paziente.getIdUtente(), descrizioneInfoPaziente);

        if (success) {
            aggiornaListaLogInfoPaziente();
        }

        return success;
    }
}
