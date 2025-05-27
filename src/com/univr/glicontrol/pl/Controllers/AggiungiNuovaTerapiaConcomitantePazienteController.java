package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.util.List;


public class AggiungiNuovaTerapiaConcomitantePazienteController {
    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    private final Paziente paziente = upp.getPazienteSessione();
    GestioneTerapie gt = new GestioneTerapie(paziente);

    @FXML
    private ComboBox<String>  patologiaCB;
    @FXML
    private DatePicker dataInizioDP, dataFineDP;

    @FXML
    private void initialize(){
        patologiaCB.getItems().addAll(upp.getListaPatologieConcomitantiPazienti());

    }


    public void inserisciFarmaco(){
        if(patologiaCB.getSelectionModel().getSelectedItem() == null || dataInizioDP.getValue() == null){
            Alert datiTerapiaMancantiAlert = new Alert(Alert.AlertType.INFORMATION);
            datiTerapiaMancantiAlert.setTitle("System Notification Service");
            datiTerapiaMancantiAlert.setHeaderText("Dati mancanti");
            datiTerapiaMancantiAlert.setContentText("Per procedere all'inserimento dei farmaci è necessario selezionare patologie e data di inizio.\nRiprova");
            datiTerapiaMancantiAlert.showAndWait();
        } else {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/DettaglioNuovoFarmaco.fxml"));
                Parent root = loader.load();

                DettaglioNuovoFarmacoController dettaglioNuovoFarmacoController = loader.getController();
                dettaglioNuovoFarmacoController.setInstance(this);
                dettaglioNuovoFarmacoController.setGestioneTerapie(gt);

                Stage dettaglio = new Stage();
                dettaglio.setTitle("Aggiungi farmaco");
                dettaglio.setScene(new Scene(root));
                dettaglio.show();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void aggiungiNuovaTerapia(){
        String nomePatologia = patologiaCB.getSelectionModel().getSelectedItem();
        Date dataInizio = Date.valueOf(dataInizioDP.getValue());
        Date dataFine;
        if(dataFineDP.getValue() == null){
            dataFine = null;
        }else{
            dataFine = Date.valueOf(dataFineDP.getValue());
        }
        // da rivedere per il null del medico
        int idPatologia = upp.getPatologiaConcomitantePerNomeFormattata(nomePatologia).getIdPatologia();
        List<FarmacoTerapia> farmaciConIndicazioni = gt.getFarmaciTerapia();
        int status = gt.inserisciTerapiaConcomitante(idPatologia, 0, dataInizio, dataFine, farmaciConIndicazioni);
        if(status == 1){
            Alert successoInserimentoTerapiaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoTerapiaAlert.setTitle("System Information Service");
            successoInserimentoTerapiaAlert.setHeaderText("Terapia inserita con successo");
            successoInserimentoTerapiaAlert.setContentText("La nuova terapia è stata inserita con successo");
            successoInserimentoTerapiaAlert.showAndWait();

            dataInizioDP.setValue(null);
            dataFineDP.setValue(null);


        } else if (status == 0) {
            Alert erroreInserimentoTerapiaAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoTerapiaAlert.setTitle("System Information Service");
            erroreInserimentoTerapiaAlert.setHeaderText("Errore durante l'inserimento della nuova terapia");
            erroreInserimentoTerapiaAlert.setContentText("Non è stato possibile inserire la nuova terapia.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoTerapiaAlert.showAndWait();
        } else {
            Alert erroreDuplicazioneTerapiaAlert = new Alert(Alert.AlertType.ERROR);
            erroreDuplicazioneTerapiaAlert.setTitle("System Information Service");
            erroreDuplicazioneTerapiaAlert.setHeaderText("Errore durante l'inserimento della nuova terapia");
            erroreDuplicazioneTerapiaAlert.setContentText("La terapia che stai cercando di inserire esiste già nel sistema");
            erroreDuplicazioneTerapiaAlert.showAndWait();
            dataInizioDP.setValue(null);
            dataFineDP.setValue(null);
        }
    }

    public GestioneTerapie getGestioneTerapie() {
        return gt;
    }



}
