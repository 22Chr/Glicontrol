package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestionePatologieConcomitanti;
import com.univr.glicontrol.bll.GestioneRilevazioniGlicemia;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.RilevazioneGlicemica;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FinestraRilevazioniGlicemichePazienteController {

    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    Paziente paziente = upp.getPazienteSessione();
    GestioneRilevazioniGlicemia grg = new GestioneRilevazioniGlicemia(paziente);

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
    private ListView<String> glicemiaPazienteLV;
    @FXML
    private TextArea descrizioneEstesaTA;

    @FXML
    private void initialize(){
        oraGlicemiaCB.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutiGlicemiaCB.getItems().addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
        primaODopoCB.getItems().addAll("Prima", "Dopo");
        pastoGlicemiaCB.getItems().addAll("Colazione", "Pranzo", "Cena", "Merenda");

        RilevazioneGlicemica rilevazioneGlicemica;

        ObservableList<String> rilevazioni = FXCollections.observableArrayList();
        rilevazioni.addAll(upp.getListaRilevazioniGlicemichePazienti());
        glicemiaPazienteLV.setItems(rilevazioni);

        glicemiaPazienteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    descrizioneEstesaTA.setText(cell.getText());
                    cambiaPagina();
                }
            });

            return cell;
        });
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


        if (valoreGlicemiaTF.getText().isEmpty() || data == null || oraGlicemiaCB.getValue() == null || minutiGlicemiaCB.getValue() == null || primaODopoCB.getValue() == null || pastoGlicemiaCB.getValue() == null) {
            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Information Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per poter inserire una rilevazione glicemica devi precisarne la data, l'ora, il valore e l'associazione rispetto al pasto di riferimento.\nInserisci tutti i dati e riprova");
            datiMancantiAlert.showAndWait();
            return;
        }

        int ora = oraGlicemiaCB.getValue().equals("00") ? 0 : Integer.parseInt(oraGlicemiaCB.getValue());
        int minuti = minutiGlicemiaCB.getValue().equals("00") ? 0 : Integer.parseInt(minutiGlicemiaCB.getValue());
        Time orario = upp.convertiOraPasto(ora, minuti);

        int status = grg.inserisciRilevazione(data, orario, Float.parseFloat(valoreGlicemiaTF.getText()), pastoGlicemiaCB.getValue(), primaODopoCB.getValue());

        if (status == -1 ) {
            Alert duplicazioneRilevazioneGlicemiaAlert = new Alert(Alert.AlertType.ERROR);
            duplicazioneRilevazioneGlicemiaAlert.setTitle("System Information Service");
            duplicazioneRilevazioneGlicemiaAlert.setHeaderText("Errore durante l'inserimento della rilevazione glicemica");
            duplicazioneRilevazioneGlicemiaAlert.setContentText("La rilevazione glicemica è già stata inserita con successo.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            duplicazioneRilevazioneGlicemiaAlert.showAndWait();
        } else if (status == 0) {
            Alert erroreInserimentoRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoRilevazioneGlicemicaAlert.setTitle("System Information Service");
            erroreInserimentoRilevazioneGlicemicaAlert.setHeaderText("Errore durante l'inserimento della rilevazione glicemica");
            erroreInserimentoRilevazioneGlicemicaAlert.setContentText("Non è stato possibile inserire la rilevazione glicemica.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoRilevazioneGlicemicaAlert.showAndWait();
        } else {
            Alert successoInserimentoRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoRilevazioneGlicemicaAlert.setTitle("System Information Service");
            successoInserimentoRilevazioneGlicemicaAlert.setHeaderText("Rilevazione glicemica inserita con successo");
            successoInserimentoRilevazioneGlicemicaAlert.setContentText("La nuova rilevazione glicemica è stata inserita con successo");
            successoInserimentoRilevazioneGlicemicaAlert.showAndWait();

            resetListViewRilevazioniGlicemiche();

            // Ripulisci i campi per l'inserimento di nuove rilevazioni
            dataGlicemiaDP.setValue(null);
            oraGlicemiaCB.setValue(null);
            minutiGlicemiaCB.setValue(null);
            primaODopoCB.setValue(null);
            pastoGlicemiaCB.setValue(null);
            valoreGlicemiaTF.clear();
            valoreGlicemiaTF.requestFocus();
        }
    }

    private void resetListViewRilevazioniGlicemiche(){
        UtilityPortalePaziente newUpp = new UtilityPortalePaziente();
        ObservableList<String> newRilevazioni = FXCollections.observableArrayList();
        newRilevazioni.addAll(newUpp.getListaRilevazioniGlicemichePazienti());
        glicemiaPazienteLV.setItems(newRilevazioni);
    }

    public void eliminaRilevazione() {
        String rilevazioneFormattata = glicemiaPazienteLV.getSelectionModel().getSelectedItem();
        RilevazioneGlicemica rilevazione = upp.getRilevazioneGlicemicaPerValoreFormattata(rilevazioneFormattata);

        if (grg.eliminaRilevazione(rilevazione.getIdRilevazione())) {
            Alert successoEliminazioneRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoEliminazioneRilevazioneGlicemicaAlert.setTitle("System Information Service");
            successoEliminazioneRilevazioneGlicemicaAlert.setHeaderText("Rilevazione glicemica eliminata con successo");
            successoEliminazioneRilevazioneGlicemicaAlert.setContentText("La rilevazione glicemica è stata eliminata con successo");
            successoEliminazioneRilevazioneGlicemicaAlert.showAndWait();

            cambiaPagina();
            resetListViewRilevazioniGlicemiche();

        } else {
            Alert erroreEliminazioneRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.ERROR);
            erroreEliminazioneRilevazioneGlicemicaAlert.setTitle("System Information Service");
            erroreEliminazioneRilevazioneGlicemicaAlert.setHeaderText("Errore nell'eliminazione della rilevazione glicemica");
            erroreEliminazioneRilevazioneGlicemicaAlert.setContentText("Si è verificato un errore durante l'eliminazione della rilevazione glicemica selezionata. Riprova");
            erroreEliminazioneRilevazioneGlicemicaAlert.showAndWait();
        }
    }
}
