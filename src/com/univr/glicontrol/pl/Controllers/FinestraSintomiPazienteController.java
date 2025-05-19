package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestioneSintomi;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class FinestraSintomiPazienteController {
    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    Paziente paziente = upp.getPazienteSessione();

    GestioneSintomi gs = new GestioneSintomi(paziente);

    @FXML
    private TextArea descrizioneTA;
    @FXML
    private ListView<String> sintomiPazienteLV;

    @FXML
    private void initialize() {
        ObservableList<String> sintomi = FXCollections.observableArrayList();
        sintomi.addAll(upp.getListaSintomiPazienti());
        sintomiPazienteLV.setItems(sintomi);
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
}
