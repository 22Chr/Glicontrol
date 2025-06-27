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
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;


public class FinestraTerapiePazienteController implements Controller {
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
    private HBox loadingPage, mainPage, aggiungiEliminaHB;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button aggiungiTerapiaButton, salvaModificheTerapiaB;

    private PortaleMedicoController pmc = null;
    private PortalePazienteController ppc = null;
    private Paziente paziente;
    UtilityPortali upp;
    private Terapia terapia = null;
    private GestioneTerapie gt = null;

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
                    clearScreen();
                    Platform.runLater(() -> {
                        terapia = upp.getTerapiaPerNomeFormattata(terapiePazienteLV.getSelectionModel().getSelectedItem());
                        mostraFarmaciTerapia();
                    });
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
                    salvaModificheTerapiaB.setVisible(false);
                    mostraIndicazioniFarmaciTerapia();
                }
            });

            return cell;
        });
    }

    public void resetListViewTerapie(){
        UtilityPortali newUpp = new UtilityPortali(paziente);
        ObservableList<String> newTerapie = FXCollections.observableArrayList();
        newTerapie.addAll(newUpp.getListaTerapiePaziente());
        terapiePazienteLV.setItems(newTerapie);
    }

    public void aggiungiTerapia() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/InserisciNuovaTerapia.fxml"));
            Parent root = fxmlLoader.load();

            InserisciNuovaTerapiaController intc = fxmlLoader.getController();

            String ruoloAccesso;
            if (pmc != null) {
                ruoloAccesso = "medico";
            } else {
                ruoloAccesso = "paziente";
            }

            intc.setInstance(this, ruoloAccesso, paziente);

            Stage terapiePaziente = new Stage();
            terapiePaziente.setTitle("Aggiungi terapia");
            terapiePaziente.setScene(new Scene(root));

            terapiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearScreen() {
        if (indicazioniFarmacoGP.isVisible()) {
            FadeTransition fadeOutIndicazioniFarmaci = new FadeTransition(Duration.millis(300), indicazioniFarmacoGP);
            fadeOutIndicazioniFarmaci.setFromValue(1.0);
            fadeOutIndicazioniFarmaci.setToValue(0.0);
            fadeOutIndicazioniFarmaci.play();
            indicazioniFarmacoGP.setVisible(false);
        }

        infoTerapiaVB.setVisible(false);
    }

    private void mostraFarmaciTerapia() {

        Task<Void> loadingTask = new Task<>() {

            @Override
            protected Void call() {
                upp = new UtilityPortali(paziente);
                String dataTerapia = upp.getIndicazioniTemporaliTerapia(terapia);

                // Popola la lista dei farmaci associati alla terapia visualizzata
                ObservableList<String> farmaci = FXCollections.observableArrayList();
                List<FarmacoTerapia> listaFarmaci = terapia.getListaFarmaciTerapia();

                for (FarmacoTerapia farmaco : listaFarmaci) {
                    farmaci.add(farmaco.getFarmaco().getNome());
                }

                Platform.runLater(() -> {
                    nomeTerapiaTF.setText(terapia.getNome());
                    dateTerapiaTF.setText(dataTerapia);
                    farmaciTerapiaLV.setItems(farmaci);

                    if (pmc != null) {
                        aggiungiEliminaHB.setVisible(true);
                    }
                });

                return null;
            }

            @Override
            protected void succeeded() {
                infoTerapiaVB.setVisible(true);
            }

            @Override
            protected void failed() {
                infoTerapiaVB.setVisible(false);
                System.err.println("Errore durante il caricamento dei dati della terapia selezionata");
            }
        };

        new Thread(loadingTask).start();

    }

    private void mostraIndicazioniFarmaciTerapia() {

        if (pmc != null) {
            dosaggiTerapiaTA.setEditable(true);
            frequenzaTerapiaTA.setEditable(true);
            orariTerapiaTA.setEditable(true);
        }

        Task<Void> loadIndicazioniFarmaciTerapie = new Task<>() {
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
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), indicazioniFarmacoGP);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                dosaggiTerapiaTA.textProperty().addListener((observable, oldValue, newValue) -> {
                    mostraBottoneSalvataggio(newValue);
                });
                frequenzaTerapiaTA.textProperty().addListener((observable, oldValue, newValue) -> {
                    mostraBottoneSalvataggio(newValue);
                });
                orariTerapiaTA.textProperty().addListener((observable, oldValue, newValue) -> {
                    mostraBottoneSalvataggio(newValue);
                });
            }

            @Override
            protected void failed() {
                indicazioniFarmacoGP.setVisible(false);
                System.err.println("Errore durante il caricamento dei dati.");
            }
        };

        new Thread(loadIndicazioniFarmaciTerapie).start();
    }

    private void mostraBottoneSalvataggio(String newValue) {
        if (pmc != null) {
            boolean shouldBeVisible = !newValue.trim().isEmpty();
            if (salvaModificheTerapiaB.isVisible() != shouldBeVisible) {
                salvaModificheTerapiaB.setVisible(true);
                FadeTransition salvaBFadeIn = new FadeTransition(Duration.millis(100), salvaModificheTerapiaB);
                salvaBFadeIn.setFromValue(0.0);
                salvaBFadeIn.setToValue(1.0);
                salvaBFadeIn.play();
            }
        }
    }

    public void setInstance(Portale controller, Paziente pazienteSelezionato) {
        if (controller instanceof PortaleMedicoController) {
            pmc = (PortaleMedicoController) controller;
        } else if  (controller instanceof PortalePazienteController) {
            ppc = (PortalePazienteController) controller;
        } else {
            throw new IllegalArgumentException("Nessun controller valido selezionato");
        }

        this.paziente = pazienteSelezionato;
        upp = new UtilityPortali(paziente);
        gt = new GestioneTerapie(paziente);

        Platform.runLater(this::caricaTerapiePaziente);
    }

    public void setNomeBottoneInserimentoTerapia() {
        if (pmc != null) {
            aggiungiTerapiaButton.setText("Aggiungi Terapia");
        }
    }

    private void caricaTerapiePaziente() {
        loadingPage.setVisible(true);
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Task<Void> loadingTerapieTask = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> terapie = FXCollections.observableArrayList();
                terapie.addAll(upp.getListaTerapiePaziente());
                Platform.runLater(() -> terapiePazienteLV.setItems(terapie));

                return null;
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                loadingPage.setVisible(false);
                mainPage.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                loadingPage.setVisible(false);
                System.err.println("Errore durante il caricamento dei dati.");

                Alert erroreCaricamentoTerapie = new Alert(Alert.AlertType.ERROR);
                erroreCaricamentoTerapie.setTitle("System Notification Service");
                erroreCaricamentoTerapie.setHeaderText("Errore durante il caricamento dei dati");
                erroreCaricamentoTerapie.setContentText("Si è verificato un errore durante il caricamento delle terapie.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
                erroreCaricamentoTerapie.showAndWait();
            }
        };

        new Thread(loadingTerapieTask).start();
    }


    public void aggiungiFarmaciAllaTerapia() {
        try {
            FXMLLoader aggiungiFarmaciLoader = new FXMLLoader(getClass().getResource("../uiElements/DettaglioNuovoFarmaco.fxml"));
            Parent root = aggiungiFarmaciLoader.load();

            DettaglioNuovoFarmacoController dnfc = aggiungiFarmaciLoader.getController();
            dnfc.setInstance(this, paziente, gt);

            Stage stage = new Stage();
            stage.setTitle("Inserisci Farmaci");
            stage.setScene(new Scene(root));

            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Si è verificato un erorre durante il caricamto del controller");
        }

        List<FarmacoTerapia> farmaciAttualiTerapia = terapia.getListaFarmaciTerapia();
        farmaciAttualiTerapia.addAll(gt.getFarmaciSingolaTerapia());
        terapia.setListaFarmaciTerapia(farmaciAttualiTerapia);

        mostraFarmaciTerapia();
        salvaModificheTerapiaB.setVisible(true);
    }

    public void salvaModificheTerapia() {
        // modificare in modo tale che si possa inserire un array di farmaci

        if (indicazioniFarmacoGP.isVisible()) {
            Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(farmaciTerapiaLV.getSelectionModel().getSelectedItem());
            int index = -1;
            for (int i = 0; i < terapia.getListaFarmaciTerapia().size(); i++) {
                if (terapia.getListaFarmaciTerapia().get(i).getFarmaco().equals(farmaco)) {
                    index = i;
                    break;
                }
            }

            IndicazioniFarmaciTerapia ift = terapia.getListaFarmaciTerapia().get(index).getIndicazioni();
            ift.setDosaggio(upp.convertiDosaggio(dosaggiTerapiaTA.getText()));
            ift.setOrariAssunzione(orariTerapiaTA.getText());
            ift.setFrequenzaAssunzione(frequenzaTerapiaTA.getText());

            terapia.getListaFarmaciTerapia().get(index).setIndicazioni(ift);
        }

        if(gt.aggiornaTerapia(terapia)){
            Alert successoAggiornamentoTerapiaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoAggiornamentoTerapiaAlert.setTitle("System Notification Service");
            successoAggiornamentoTerapiaAlert.setHeaderText("Aggiornamento avvenuto con successo");
            successoAggiornamentoTerapiaAlert.setContentText("La terapia è stata aggiornata correttamente");
            successoAggiornamentoTerapiaAlert.showAndWait();

            //TODO: inserire log modifiche terapia

        }else{
            Alert erroreAggiornamentoTerapiaAlert = new Alert(Alert.AlertType.ERROR);
            erroreAggiornamentoTerapiaAlert.setTitle("System Notification Service");
            erroreAggiornamentoTerapiaAlert.setHeaderText("Errore durante l'aggiornamento");
            erroreAggiornamentoTerapiaAlert.setContentText("Si è verificato un errore durante l'aggiornamento della terapia.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
            erroreAggiornamentoTerapiaAlert.showAndWait();
        }
    }
}
