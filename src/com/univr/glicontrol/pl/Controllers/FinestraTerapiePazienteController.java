package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
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

    @FXML
    private Button aggiungiTerapiaButton;

    UtilityPortali upp = new UtilityPortali();
    private PortaleMedicoController pmc = null;
    private PortalePazienteController ppc = null;
    private Paziente pazienteSelezionato;

    @FXML
    private void initialize() {

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
        UtilityPortali newUpp = new UtilityPortali();
        ObservableList<String> newTerapie = FXCollections.observableArrayList();
        newTerapie.addAll(newUpp.getListaTerapiePaziente(pazienteSelezionato));
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
        FadeTransition fadeOutIndicazioniFarmaci = new FadeTransition(javafx.util.Duration.millis(150), indicazioniFarmacoGP);
        fadeOutIndicazioniFarmaci.setFromValue(1.0);
        fadeOutIndicazioniFarmaci.setToValue(0.0);
        fadeOutIndicazioniFarmaci.play();
        indicazioniFarmacoGP.setVisible(false);

        FadeTransition fadeOutInfoTerapia = new FadeTransition(javafx.util.Duration.millis(150), infoTerapiaVB);
        fadeOutInfoTerapia.setFromValue(1.0);
        fadeOutInfoTerapia.setToValue(0.0);
        fadeOutInfoTerapia.play();
        infoTerapiaVB.setVisible(false);

        String nomeTerapia = terapiePazienteLV.getSelectionModel().getSelectedItem();

        Task<Void> loadingTask = new Task<>() {

            @Override
            protected Void call() {
                Terapia terapia = upp.getTerapiaPerNomeFormattata(nomeTerapia, pazienteSelezionato);
                String dataTerapia = upp.getIndicazioniTemporaliTerapia(terapia);

                // Popola la lista dei farmaci associati alla terapia visualizzata
                ObservableList<String> farmaci = FXCollections.observableArrayList();
                List<FarmacoTerapia> listaFarmaci = terapia.getListaFarmaciTerapia();

                for (FarmacoTerapia farmaco : listaFarmaci) {
                    farmaci.add(farmaco.getFarmaco().getNome());
                }

                Platform.runLater(() -> {
                    nomeTerapiaTF.setText(nomeTerapia);
                    dateTerapiaTF.setText(dataTerapia);
                    farmaciTerapiaLV.setItems(farmaci);
                });

                return null;
            }

            @Override
            protected void succeeded() {
                infoTerapiaVB.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(100), infoTerapiaVB);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            @Override
            protected void failed() {
                infoTerapiaVB.setVisible(false);
                System.err.println("Errore durante il caricamento dei dati della terapia selezionata");
            }
        };

        new Thread(loadingTask).start();

    }

    private void mostraIndicazinoiFarmaciTerapia() {
        Terapia terapia = upp.getTerapiaPerNomeFormattata(nomeTerapiaTF.getText(),  pazienteSelezionato);

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
                FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(100), indicazioniFarmacoGP);
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

    public void setInstance(Portale controller, Paziente pazienteSelezionato) {
        if (controller instanceof PortaleMedicoController) {
            pmc = (PortaleMedicoController) controller;
        } else if  (controller instanceof PortalePazienteController) {
            ppc = (PortalePazienteController) controller;
        } else {
            throw new IllegalArgumentException("Nessun controller valido selezionato");
        }

        this.pazienteSelezionato = pazienteSelezionato;

        Platform.runLater(this::caricaPatologiePaziente);
    }

    public void setNomeBottoneInserimentoTerapia() {
        if (pmc != null) {
            aggiungiTerapiaButton.setText("Aggiungi Terapia");
        }
    }

    private void caricaPatologiePaziente() {
        loadingPage.setVisible(true);
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Task<Void> loadingTerapieTask = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> terapie = FXCollections.observableArrayList();
                terapie.addAll(upp.getListaTerapiePaziente(pazienteSelezionato));
                Platform.runLater(() -> terapiePazienteLV.setItems(terapie));

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

        new Thread(loadingTerapieTask).start();
    }
}
