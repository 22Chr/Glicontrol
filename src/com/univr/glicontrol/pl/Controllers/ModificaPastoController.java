package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestionePasti;
import com.univr.glicontrol.bll.Pasto;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.Time;


public class ModificaPastoController {
    private final UtilityPortalePaziente upp = new UtilityPortalePaziente();
    private final Paziente paziente = upp.getPazienteSessione();
    private final GestionePasti gp = new GestionePasti(paziente);
    private ModificaInformazioniPazienteController mipc;
    @FXML
    private TextField nomePastoTF;
    @FXML
    private ComboBox<String> oraCB, minutiCB;
    @FXML
    private Button confermaPastoeOrarioB;

    private Pasto pasto = null;

    @FXML
    private void initialize() {
        oraCB.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutiCB.getItems().addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");

        confermaPastoeOrarioB.requestFocus();
    }

    //inizializza il textfield correttamente
    public void setInstance(ModificaInformazioniPazienteController mipc, String pastoDaModificare) {
        this.mipc = mipc;
        for (Pasto p : gp.getPasti()) {
            if (p.getNomePasto().equals(upp.getNomePastoPerPastoFormattato(pastoDaModificare))) {
                this.pasto = p;
            }
        }
        nomePastoTF.setText(pastoDaModificare);
    }

    public void eliminaPastoEOrario() {
        if (gp.eliminaPasto(pasto.getIdPasto())) {
            Alert successoInserimentoPastoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoPastoAlert.setTitle("System Information Service");
            successoInserimentoPastoAlert.setHeaderText("Pasto eliminato con successo");
            successoInserimentoPastoAlert.setContentText("Il pasto è stato eliminato con successo");
            successoInserimentoPastoAlert.showAndWait();

            mipc.resetListViewPasti();

            Window currentWindow = confermaPastoeOrarioB.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert erroreInserimentoPastoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoPastoAlert.setTitle("System Information Service");
            erroreInserimentoPastoAlert.setHeaderText("Errore durante l'eliminazione del pasto");
            erroreInserimentoPastoAlert.setContentText("Non è stato possibile eliminare il pasto, riprova");
            erroreInserimentoPastoAlert.showAndWait();
        }
    }

    public void setInstance(ModificaInformazioniPazienteController mipc){
        this.mipc = mipc;
    }

    public void confermaPastoEOrario() {
        int ora;
        int minuti;
        Time orario;

        if (oraCB.getValue() == null || minutiCB.getValue() == null) {
            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Information Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per inserire un nuovo pasto è necessario che tutti i campi siano compilati correttamente. Riprova");
            datiMancantiAlert.showAndWait();
            return;
        } else {
            ora = oraCB.getValue().equals("00") ? 0 : Integer.parseInt(oraCB.getValue());
            minuti = minutiCB.getValue().equals("00") ? 0 : Integer.parseInt(minutiCB.getValue());
            orario = upp.convertiOraPasto(ora, minuti);
            pasto.setOrario(orario);
        }

        if (gp.aggiornaPasto(pasto)) {
            Alert successoInserimentoPastoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoPastoAlert.setTitle("System Information Service");
            successoInserimentoPastoAlert.setHeaderText("Pasto modificato con successo");
            successoInserimentoPastoAlert.setContentText("Il pasto è stato modificato con successo");
            successoInserimentoPastoAlert.showAndWait();

            mipc.resetListViewPasti();

            Window currentWindow = confermaPastoeOrarioB.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert erroreInserimentoPastoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoPastoAlert.setTitle("System Information Service");
            erroreInserimentoPastoAlert.setHeaderText("Errore durante la modifica del pasto");
            erroreInserimentoPastoAlert.setContentText("Non è stato possibile modificare il pasto, riprova");
            erroreInserimentoPastoAlert.showAndWait();
        }
    }
}


