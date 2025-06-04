package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    @FXML
    private HBox loadingPage, mainPage;

    @FXML
    private ProgressIndicator progressIndicator;


    UtilityPortalePaziente upp = new UtilityPortalePaziente();

    @FXML
    private void initialize() {
        loadingPage.setVisible(true);
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Task<Void> loadingTask = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> terapie = FXCollections.observableArrayList();
                terapie.addAll(upp.getListaTerapiePaziente());

                Platform.runLater(() -> {
                    terapiePazienteLV.setItems(terapie);
                });

                return null;
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                loadingPage.setVisible(false);
                mainPage.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(100), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                loadingPage.setVisible(false);
                System.err.println("Errore durante il caricamento dei dati.");
            }
        };

        new Thread(loadingTask).start();

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/InserisciNuovaTerapiaConcomitantePaziente.fxml"));
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
        indicazioniFarmacoGP.setVisible(false);

        FadeTransition fadeOut = new FadeTransition(javafx.util.Duration.millis(500), infoTerapiaVB);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();

        if (!(infoTerapiaVB.isVisible())) {
            infoTerapiaVB.setVisible(true);
        }
        nomeTerapiaTF.setText(terapiePazienteLV.getSelectionModel().getSelectedItem());
        Terapia terapia = upp.getTerapiaPerNomeFormattata(nomeTerapiaTF.getText());
        dateTerapiaTF.setText(upp.getIndicazioniTemporaliTerapia(terapia));
        FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(750), infoTerapiaVB);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

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

        Task<Void> loadingTask = new Task<>() {
            @Override
            protected Void call() {
                String dosaggioTerapia = terapia.getDosaggioPerFarmaco(farmaciTerapiaLV.getSelectionModel().getSelectedItem()) + " " +
                        GestioneFarmaci.getInstance().getFarmacoByName(farmaciTerapiaLV.getSelectionModel().getSelectedItem()).getUnitaMisura();
                String frequenzaTerapia = terapia.getFrequenzaPerFarmaco(farmaciTerapiaLV.getSelectionModel().getSelectedItem());
                String orariTerapia = terapia.getOrarioPerFarmaco(farmaciTerapiaLV.getSelectionModel().getSelectedItem());

                Platform.runLater(() -> {
                    dosaggiTerapiaTA.setText(dosaggioTerapia);
                    frequenzaTerapiaTA.setText(frequenzaTerapia);
                    orariTerapiaTA.setText(orariTerapia);
                });

                return null;
            }

            @Override
            protected void succeeded() {
                indicazioniFarmacoGP.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(250), indicazioniFarmacoGP);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            @Override
            protected void failed() {
                indicazioniFarmacoGP.setVisible(false);
                System.err.println("Errore durante il caricamento dei dati.");
            }
        };

        new Thread(loadingTask).start();
    }
}
