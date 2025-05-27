package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestionePasti;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.Time;

public class InserisciPastoController {
    @FXML
    private ComboBox<String> pastoCB,oraCB, minutiCB; //ora e minuti verranno convertiti in int per DB

    @FXML
    private Button confermaPastoeOrarioB;

    private final UtilityPortalePaziente upp = new UtilityPortalePaziente();
    private final Paziente paziente = upp.getPazienteSessione();

    private BenvenutoPazienteController bpc = null;
    private ModificaInformazioniPazienteController mipc = null;

    public void initialize(){
        pastoCB.getItems().addAll("Colazione", "Pranzo", "Cena", "Merenda");
        oraCB.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutiCB.getItems().addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");

        confermaPastoeOrarioB.requestFocus();
    }

    private final GestionePasti gp = new GestionePasti(paziente);

    public void confermaPastoeOrario(){
        String pasto;
        int ora;
        int minuti;
        Time orario;

        if (pastoCB.getValue() == null || oraCB.getValue() == null || minutiCB.getValue() == null) {
            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Information Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per inserire un nuovo pasto è necessario che tutti i campi siano compilati correttamente. Riprova");
            datiMancantiAlert.showAndWait();
            return;
        } else {
            pasto = pastoCB.getValue();
            ora = oraCB.getValue().equals("00") ? 0 : Integer.parseInt(oraCB.getValue());
            minuti = minutiCB.getValue().equals("00") ? 0 : Integer.parseInt(minutiCB.getValue());
            orario = upp.convertiOraPasto(ora, minuti);

        }

        if (gp.inserisciPasto(pasto, orario)) {
            Alert successoInserimentoPastoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoPastoAlert.setTitle("System Information Service");
            successoInserimentoPastoAlert.setHeaderText("Pasto inserito con successo");
            successoInserimentoPastoAlert.setContentText("Il pasto è stato inserito con successo");
            successoInserimentoPastoAlert.showAndWait();

            if (bpc != null) {
                bpc.resetListViewPasti();
            }
            if (mipc != null) {
                mipc.resetListViewPasti();
            }

            Window currentWindow = confermaPastoeOrarioB.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert erroreInserimentoPastoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoPastoAlert.setTitle("System Information Service");
            erroreInserimentoPastoAlert.setHeaderText("Errore durante l'inserimento del pasto");
            erroreInserimentoPastoAlert.setContentText("Non è stato possibile inserire il pasto, riprova");
            erroreInserimentoPastoAlert.showAndWait();
        }
    }

    // Setta l'istanza del controllore sottostante
    public void setInstance(InserimentoPastiController controller){
        if (controller instanceof BenvenutoPazienteController) {
            this.bpc = (BenvenutoPazienteController) controller;
        } else if (controller instanceof ModificaInformazioniPazienteController) {
            this.mipc = (ModificaInformazioniPazienteController) controller;
        } else {
            throw new IllegalArgumentException("Controller non valido");
        }
    }

}
