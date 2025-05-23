package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.Farmaco;
import com.univr.glicontrol.bll.GestioneTerapie;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.Terapia;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;


public class FinestraTerapiePazienteController {
    //all'inizio mostra pageStorico e pageButton: si schiaccia su PageStorico si nasconde pageButton e si apre pageTerapia
    //se si schiacca pageButton si apre la schermata per aggiungere una nuova terapia
    //inizializzare la list View
    //inizializzare i campi in pageTerapia
    @FXML
    private ListView<String> terapiePazienteLV, farmaciTerapiaLV;

    @FXML
    private VBox infoTerapiaVB;

    @FXML
    private TextField nomeTerapiaTF, dateTerapiaTF;

    @FXML
    private TextArea dosaggiTerapiaTA, frequenzaTerapiaTA, orariTerapiaTA;


    UtilityPortalePaziente upp = new UtilityPortalePaziente();
    Paziente paziente = upp.getPazienteSessione();
    GestioneTerapie gt = new GestioneTerapie(paziente);

    @FXML
    private void initialize() {
        ObservableList<String> terapie = FXCollections.observableArrayList();
        terapie.addAll(upp.getListaTerapiePaziente());
        terapiePazienteLV.setItems(terapie);

        terapiePazienteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    mostraTerapia();
                }
            });

            return cell;
        });
    }

    public void aggiungiTerapiaConcomitante() {

    }

    private void mostraTerapia() {
        infoTerapiaVB.setVisible(true);
        nomeTerapiaTF.setText(terapiePazienteLV.getSelectionModel().getSelectedItem());
        Terapia terapia = upp.getTerapiaPerNomeFormattata(nomeTerapiaTF.getText());
        dateTerapiaTF.setText(upp.getIndicazioniTemporaliTerapia(terapia));

        // Popola la lista dei farmaci associati alla terapia visualizzata
        ObservableList<String> farmaci = FXCollections.observableArrayList();
        List<Farmaco> listaFarmaci = terapia.getListaFarmaciTerapia();
        for (Farmaco farmaco : listaFarmaci) {
            farmaci.add(farmaco.getNome());
        }
        farmaciTerapiaLV.setItems(farmaci);


    }
}
