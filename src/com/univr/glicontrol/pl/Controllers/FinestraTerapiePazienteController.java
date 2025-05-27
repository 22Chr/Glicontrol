package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
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

    @FXML
    private GridPane indicazioniFarmacoGP;


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
                    mostraFarmaciTerapia();
                }
            });

            return cell;
        });

        farmaciTerapiaLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    mostraIndicazinoiFarmaciTerapia();
                }
            });

            return cell;
        });
    }

    public void resetListViewTerapie(){
        UtilityPortalePaziente newUpp = new UtilityPortalePaziente();
        ObservableList<String> newTerapie = FXCollections.observableArrayList();
        newTerapie.addAll(newUpp.getListaTerapiePaziente());
        terapiePazienteLV.setItems(newTerapie);
    }

    public void aggiungiTerapiaConcomitante() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/AggiungiNuovaTerapiaConcomitantePaziente.fxml"));
            Parent root = fxmlLoader.load();

            InserisciNuovaTerapiaConcomitantePazienteController intcpc = fxmlLoader.getController();
            intcpc.setInstance(this);

            Stage terapiePaziente = new Stage();
            terapiePaziente.setTitle("Aggiungi terapia");
            terapiePaziente.setScene(new Scene(root));

            terapiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void mostraFarmaciTerapia() {
        infoTerapiaVB.setVisible(true);
        nomeTerapiaTF.setText(terapiePazienteLV.getSelectionModel().getSelectedItem());
        Terapia terapia = upp.getTerapiaPerNomeFormattata(nomeTerapiaTF.getText());
        dateTerapiaTF.setText(upp.getIndicazioniTemporaliTerapia(terapia));

        // Popola la lista dei farmaci associati alla terapia visualizzata
        ObservableList<String> farmaci = FXCollections.observableArrayList();
        List<FarmacoTerapia> listaFarmaci = terapia.getListaFarmaciTerapia();

        for (FarmacoTerapia farmaco : listaFarmaci) {
            farmaci.add(farmaco.getFarmaco().getNome());
        }
        farmaciTerapiaLV.setItems(farmaci);


    }

    private void mostraIndicazinoiFarmaciTerapia() {

        Terapia terapia = upp.getTerapiaPerNomeFormattata(nomeTerapiaTF.getText());

        indicazioniFarmacoGP.setVisible(true);

        dosaggiTerapiaTA.setText(
                terapia.getDosaggioPerFarmaco(farmaciTerapiaLV.getSelectionModel().getSelectedItem()) + " " +
                GestioneFarmaci.getInstance().getFarmacoByName(farmaciTerapiaLV.getSelectionModel().getSelectedItem()).getUnitaMisura()
        );
        frequenzaTerapiaTA.setText(terapia.getFrequenzaPerFarmaco(farmaciTerapiaLV.getSelectionModel().getSelectedItem()));
        orariTerapiaTA.setText(terapia.getOrarioPerFarmaco(farmaciTerapiaLV.getSelectionModel().getSelectedItem()));
    }
}
