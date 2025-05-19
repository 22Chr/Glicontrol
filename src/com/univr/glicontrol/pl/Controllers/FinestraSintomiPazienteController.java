package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestioneSintomi;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;


public class FinestraSintomiPazienteController {
    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    Paziente paziente = upp.getPazienteSessione();

    GestioneSintomi gs = new GestioneSintomi(paziente);

    @FXML
    private TextArea descrizioneTA, descrizioneEstesaTA;
    @FXML
    private ListView<String> sintomiPazienteLV;
    @FXML
    private HBox mainPage;
    @FXML
    private VBox detailPage;

    @FXML
    private void initialize() {
        ObservableList<String> sintomi = FXCollections.observableArrayList();
        sintomi.addAll(upp.getListaSintomiPazienti());
        sintomiPazienteLV.setItems(sintomi);

        sintomiPazienteLV.setCellFactory(lv -> {
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

    public void resetListViewSintomi() {
        UtilityPortalePaziente newUpp = new UtilityPortalePaziente();
        ObservableList<String> newSintomi = FXCollections.observableArrayList();
        newSintomi.addAll(newUpp.getListaSintomiPazienti());
        sintomiPazienteLV.setItems(newSintomi);
        descrizioneTA.clear();
        descrizioneTA.requestFocus();
    }

    public void inserisciNuovoSintomo() {
        if(gs.inserisciSintomo(descrizioneTA.getText())) {
            Alert successoInserimentoSintomoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoSintomoAlert.setTitle("System Information Service");
            successoInserimentoSintomoAlert.setHeaderText("Sintomo inserito con successo");
            successoInserimentoSintomoAlert.setContentText("Il nuovo sintomo è stato inserito con successo");
            successoInserimentoSintomoAlert.showAndWait();

            resetListViewSintomi();
        } else {
            Alert erroreInserimentoSintomoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoSintomoAlert.setTitle("System Information Service");
            erroreInserimentoSintomoAlert.setHeaderText("Errore durante l'inserimento del nuovo sintomo");
            erroreInserimentoSintomoAlert.setContentText("Non è stato possibile inserire il nuovo sintomo, riprova");
            erroreInserimentoSintomoAlert.showAndWait();
        }
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

    public void eliminaSintomo() {
        String sintomoFormattato = sintomiPazienteLV.getSelectionModel().getSelectedItem();
        if (gs.eliminaSintomo(upp.getSintomoPerDescrizioneFormattata(sintomoFormattato).getIdSintomo())) {
            Alert successoEliminazioneSintomoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoEliminazioneSintomoAlert.setTitle("System Information Service");
            successoEliminazioneSintomoAlert.setHeaderText("Sintomo eliminato con successo");
            successoEliminazioneSintomoAlert.setContentText("Il sintomo è stato eliminato con successo");
            successoEliminazioneSintomoAlert.showAndWait();

            cambiaPagina();
            resetListViewSintomi();
        }
    }
}
