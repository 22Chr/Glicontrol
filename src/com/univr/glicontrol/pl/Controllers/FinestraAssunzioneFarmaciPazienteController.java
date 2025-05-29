package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.AssunzioneFarmaco;
import com.univr.glicontrol.bll.GestioneAssunzioneFarmaci;
import com.univr.glicontrol.bll.GestioneFarmaci;
import com.univr.glicontrol.bll.Paziente;
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

public class FinestraAssunzioneFarmaciPazienteController {
    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    Paziente paziente = upp.getPazienteSessione();
    GestioneAssunzioneFarmaci gaf = new GestioneAssunzioneFarmaci(paziente);

    @FXML
    private HBox mainPage;
    @FXML
    private VBox detailPage;
    @FXML
    private ListView<String> farmaciAssuntiOggiLV;
    @FXML
    private ComboBox<String> listaFarmaciDaAssumereCB, oraFarmacoCB, minutiFarmacoCB;
    @FXML
    private DatePicker dataFarmacoPazienteDP;
    @FXML
    private TextArea descrizioneEstesaTA;

    @FXML
    private void initialize() {
        ObservableList<String> farmaciDaAssumere = FXCollections.observableArrayList();
        for (String farmaco : upp.getListaFarmaciDaAssumere()) {
            if (!farmaciDaAssumere.contains(farmaco))
                farmaciDaAssumere.add(farmaco);
        }
        listaFarmaciDaAssumereCB.setItems(farmaciDaAssumere);

        ObservableList<String> farmaciAssuntiOggi = FXCollections.observableArrayList();
        farmaciAssuntiOggi.addAll(upp.getListaFarmaciAssuntiOggi());
        farmaciAssuntiOggiLV.setItems(farmaciAssuntiOggi);

        oraFarmacoCB.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutiFarmacoCB.getItems().addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");

        farmaciAssuntiOggiLV.setCellFactory(lv -> {
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

    public void inserisciNuovoFarmaco() {
        Date data;
        if (dataFarmacoPazienteDP.getValue() == null) {
            data = null;
        } else {
            data = Date.valueOf(dataFarmacoPazienteDP.getValue());
        }

        if (listaFarmaciDaAssumereCB.getValue() == null || oraFarmacoCB.getValue() == null || minutiFarmacoCB.getValue() == null || data == null) {
            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Information Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per poter registrare l'assunzione di un farmaco devi scegliere un farmaco e precisare la data e l'ora di assunzione.\nInserisci tutti i dati e riprova");
            datiMancantiAlert.showAndWait();
            return;
        }

        int status = gaf.registraAssunzioneFarmaco(GestioneFarmaci.getInstance().getFarmacoByName(listaFarmaciDaAssumereCB.getValue()), data, getOra());
        if (status != 0) {
            Alert successoInserimentoFarmacoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoFarmacoAlert.setTitle("System Information Service");
            successoInserimentoFarmacoAlert.setHeaderText("Farmaco registrato con successo");
            successoInserimentoFarmacoAlert.setContentText("L'assunzione del farmaco è stata registrata correttamente");
            successoInserimentoFarmacoAlert.showAndWait();

            resetListViewFarmaciAssuntiOggi();

        } else {
            Alert erroreInserimentoFarmacoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoFarmacoAlert.setTitle("System Information Service");
            erroreInserimentoFarmacoAlert.setHeaderText("Errore durante l'inserimento del farmaco");
            erroreInserimentoFarmacoAlert.setContentText("Non è stato possibile registrare l'assunzione del farmaco.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoFarmacoAlert.showAndWait();
            return;
        }

        if (status == -1) {
            Alert eccessoDosaggioFarmacoAlert = new Alert(Alert.AlertType.WARNING);
            eccessoDosaggioFarmacoAlert.setTitle("System Information Service");
            eccessoDosaggioFarmacoAlert.setHeaderText("Dosaggio eccessivo");
            eccessoDosaggioFarmacoAlert.setContentText("Il quantitativo che hai assunto per questo farmaco supera quello giornaliero previsto dalla tua terapia.\nLo staff medico ne sarà informato");
            eccessoDosaggioFarmacoAlert.showAndWait();
        }

        resetListViewFarmaciAssuntiOggi();
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

    public void eliminaFarmaco() {
        int idAssunzione = 0;
        int idFarmaco = upp.getFarmacoPerNomeFormattato(farmaciAssuntiOggiLV.getSelectionModel().getSelectedItem()).getIdFarmaco();
        String orarioAssunzioneFormattato = getOraAssunzioneDaNomeAssunzioneFormattato(farmaciAssuntiOggiLV.getSelectionModel().getSelectedItem());

        for (AssunzioneFarmaco af : gaf.getListaAssunzioneFarmaci()) {
            if (af.getIdFarmaco() == idFarmaco &&
                    af.getData().equals(Date.valueOf(LocalDate.now())) &&
                    orarioAssunzioneFormattato.equals(af.getOra().toString().substring(0, 5))) {

                idAssunzione = af.getIdAssunzioneFarmaco();
                break;

            }
        }

        if (idAssunzione == 0) {
            throw new RuntimeException("Errore: non è stato possibile trovare l'id dell'assunzione del farmaco");
        }

        if (gaf.eliminaAssunzioneFarmaco(idAssunzione)) {
            Alert successoEliminazioneFarmacoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoEliminazioneFarmacoAlert.setTitle("System Information Service");
            successoEliminazioneFarmacoAlert.setHeaderText("Registrazione eliminata con successo");
            successoEliminazioneFarmacoAlert.setContentText("L'assunzione del farmaco è stata eliminata correttamente");
            successoEliminazioneFarmacoAlert.showAndWait();

            cambiaPagina();
            resetListViewFarmaciAssuntiOggi();

        } else {
            Alert erroreEliminazioneFarmacoAlert = new Alert(Alert.AlertType.ERROR);
            erroreEliminazioneFarmacoAlert.setTitle("System Information Service");
            erroreEliminazioneFarmacoAlert.setHeaderText("Errore durante l'eliminazione dell'assunzione");
            erroreEliminazioneFarmacoAlert.setContentText("Si è verificato un errore durante l'eliminazione dell'assunzione del farmaco. Riprova");
            erroreEliminazioneFarmacoAlert.showAndWait();
        }
    }

    private void resetListViewFarmaciAssuntiOggi() {
        UtilityPortalePaziente newUpp = new UtilityPortalePaziente();
        ObservableList<String> newFarmaciAssuntiOggi = FXCollections.observableArrayList();
        newFarmaciAssuntiOggi.addAll(newUpp.getListaFarmaciAssuntiOggi());
        farmaciAssuntiOggiLV.setItems(newFarmaciAssuntiOggi);
        dataFarmacoPazienteDP.setValue(null);
        oraFarmacoCB.setValue(null);
        minutiFarmacoCB.setValue(null);
        dataFarmacoPazienteDP.requestFocus();
        listaFarmaciDaAssumereCB.setValue(null);
        listaFarmaciDaAssumereCB.requestFocus();
    }

    private Time getOra() {
        int ora = oraFarmacoCB.getValue().equals("00") ? 0 : Integer.parseInt(oraFarmacoCB.getValue());
        int minuti = minutiFarmacoCB.getValue().equals("00") ? 0 : Integer.parseInt(minutiFarmacoCB.getValue());
        return upp.convertiOra(ora, minuti);
    }

    private String getOraAssunzioneDaNomeAssunzioneFormattato(String nomeAssunzione) {
        String check = " alle ";
        int limit = nomeAssunzione.indexOf(check);
        if (limit == -1) {
            return nomeAssunzione;
        } else {
            return nomeAssunzione.substring(limit + 6, limit + 11);
        }
    }
}
