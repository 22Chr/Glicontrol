package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestionePatologieConcomitanti;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;

public class FinestraRilevazioniGlicemichePazienteController {

    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    Paziente paziente = upp.getPazienteSessione();
    //GestionePatologieConcomitanti gpc = new GestionePatologieConcomitanti(paziente);

    @FXML
    private ComboBox<String> oraGlicemiaCB, minutiGlicemiaCB, primaODopoCB, pastoGlicemiaCB;
    @FXML
    private VBox detailPage;
    @FXML
    private HBox mainPage;
    @FXML
    private TextField valoreGlicemiaTF;
    @FXML
    private DatePicker dataGlicemiaDP;

    @FXML
    private void initialize(){
        oraGlicemiaCB.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutiGlicemiaCB.getItems().addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
        primaODopoCB.getItems().addAll("Prima", "Dopo");
        pastoGlicemiaCB.getItems().addAll("Colazione", "Pranzo", "Cena", "Merenda");
    }

    public void cambiaPagina(){
        if(detailPage.isVisible()) {
            detailPage.setVisible(false);
            mainPage.setVisible(true);
        } else {
            detailPage.setVisible(true);
            mainPage.setVisible(false);
        }
    }

    public void inserisciNuovaRilevazioneGlicemica(){
        Date data;
        if (dataGlicemiaDP.getValue() == null) {
            data = null;
        } else {
            data = Date.valueOf(dataGlicemiaDP.getValue());
        }

        if (valoreGlicemiaTF.getText().isEmpty() || data == null) {
            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Information Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per poter inserire una rilevazione glicemica devi precisarne la data, l'ora, il valore e il pasto.\nInserisci tutti i dati e riprova");
            datiMancantiAlert.showAndWait();
            return;
        }
    }


}
