package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestionePatologieConcomitanti;
import com.univr.glicontrol.bll.PatologiaConcomitante;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class FinestraPatologieConcomitantiPazienteController {

    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    Paziente paziente = upp.getPazienteSessione();
    GestionePatologieConcomitanti gpc = new GestionePatologieConcomitanti(paziente);

    @FXML
    private TextField nomePatologiaTF;
    @FXML
    private TextArea descrizionePatologiaTA, descrizioneEstesaTA;
    @FXML
    private DatePicker dataInizioDP, dataFineDP;
    @FXML
    private ListView<String> patologiePazienteLV;
    @FXML
    private HBox mainPage;
    @FXML
    private VBox detailPage;

    @FXML
    private void initialize(){
        ObservableList<String> patologie = FXCollections.observableArrayList();
        patologie.addAll(upp.getListaPatologieConcomitantiPazienti());
        patologiePazienteLV.setItems(patologie);

        patologiePazienteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    descrizioneEstesaTA.setText(upp.getPatologiaConcomitantePerNomeFormattata(cell.getText()).getDescrizione());
                    cambiaPagina();
                }
            });

            return cell;
        });
    }

    public void cambiaPagina() {
        if(detailPage.isVisible()) {
            detailPage.setVisible(false);
            mainPage.setVisible(true);
        } else {
            detailPage.setVisible(true);
            mainPage.setVisible(false);
        }
    }

    public void resetListViewPatologie(){
        UtilityPortalePaziente newUpp = new UtilityPortalePaziente();
        ObservableList<String> newPatologie = FXCollections.observableArrayList();
        newPatologie.addAll(newUpp.getListaPatologieConcomitantiPazienti());
        patologiePazienteLV.setItems(newPatologie);
        descrizionePatologiaTA.clear();
        descrizionePatologiaTA.requestFocus();
    }

    public void inserisciNuovaPatologia(){
        Date dataInizio;
        Date dataFine;

        if (dataFineDP.getValue() == null) {
            dataFine = null;
        } else {
            dataFine = Date.valueOf(dataFineDP.getValue());
        }

        if (dataInizioDP.getValue() == null) {
            dataInizio = null;
        } else {
            dataInizio = Date.valueOf(dataInizioDP.getValue());
        }

        if (descrizionePatologiaTA.getText().isEmpty() || nomePatologiaTF.getText().isEmpty() || dataInizio == null) {
            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Information Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per poter inserire una patologia devi precisarne il nome, la data di inizio e fornirne una descrizione.\nInserisci tutti i dati e riprova");
            datiMancantiAlert.showAndWait();
            return;
        }

        int status = gpc.inserisciPatologiaConcomitante(nomePatologiaTF.getText(), descrizionePatologiaTA.getText(), dataInizio, dataFine);

        if(status == 1){
            Alert successoInserimentoSintomoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoSintomoAlert.setTitle("System Information Service");
            successoInserimentoSintomoAlert.setHeaderText("Patologia concomitante inserita con successo");
            successoInserimentoSintomoAlert.setContentText("La nuova patologia concomitante è stata inserita con successo");
            successoInserimentoSintomoAlert.showAndWait();

            resetListViewPatologie();

            // Ripulisci i campit per l'inserimento di nuove patologie
            nomePatologiaTF.clear();
            descrizionePatologiaTA.clear();
            dataInizioDP.setValue(null);
            dataFineDP.setValue(null);


        } else if (status == 0) {
            Alert erroreInserimentoSintomoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoSintomoAlert.setTitle("System Information Service");
            erroreInserimentoSintomoAlert.setHeaderText("Errore durante l'inserimento della nuova patologia concomitante");
            erroreInserimentoSintomoAlert.setContentText("Non è stato possibile inserire la nuova patologia concomitante.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoSintomoAlert.showAndWait();
        } else {
            Alert erroreDuplicazioneSintomoAlert = new Alert(Alert.AlertType.ERROR);
            erroreDuplicazioneSintomoAlert.setTitle("System Information Service");
            erroreDuplicazioneSintomoAlert.setHeaderText("Errore durante l'inserimento della nuova patologia concomitante");
            erroreDuplicazioneSintomoAlert.setContentText("La patologia che stai cercando di inserire esiste già nel sistema");
            erroreDuplicazioneSintomoAlert.showAndWait();
            nomePatologiaTF.clear();
            descrizionePatologiaTA.clear();
            dataInizioDP.setValue(null);
            dataFineDP.setValue(null);
        }
    }

    public void terminaPatologiaConcomitante() {
        PatologiaConcomitante patologia = upp.getPatologiaConcomitantePerNomeFormattata(patologiePazienteLV.getSelectionModel().getSelectedItem());
        if (patologia.getDataFine() != null) {
            Alert terminazioneGiaAvvenutaAlert = new Alert(Alert.AlertType.ERROR);
            terminazioneGiaAvvenutaAlert.setTitle("System Information Service");
            terminazioneGiaAvvenutaAlert.setHeaderText("Patologia concomitante già terminata");
            String dataFormattata = new SimpleDateFormat("dd/MM/yyyy").format(patologia.getDataFine());
            terminazioneGiaAvvenutaAlert.setContentText("La patologia concomitante è già stata terminata in data: " + dataFormattata);
            terminazioneGiaAvvenutaAlert.showAndWait();
            return;
        }

        patologia.setDataFine(Date.valueOf(LocalDate.now()));

        if (gpc.aggiornaPatologiaConcomitante(patologia)) {
            Alert successoTerminazionePatologiaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoTerminazionePatologiaAlert.setTitle("System Information Service");
            successoTerminazionePatologiaAlert.setHeaderText("Patologia concomitante terminata con successo");
            successoTerminazionePatologiaAlert.setContentText("La patologia concomitante è stata segnata come conclusa in data odierna");
            successoTerminazionePatologiaAlert.showAndWait();

            resetListViewPatologie();
            cambiaPagina();
        }
    }
}
