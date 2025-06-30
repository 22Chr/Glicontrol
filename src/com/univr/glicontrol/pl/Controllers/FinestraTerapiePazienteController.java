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
import java.util.Optional;

public class FinestraTerapiePazienteController implements Controller {
    @FXML private ListView<String> terapiePazienteLV, farmaciTerapiaLV;
    @FXML private VBox infoTerapiaVB, boxTerapieVB;
    @FXML private TextField nomeTerapiaTF, dateTerapiaTF, medicoUltimaModificaTF;
    @FXML private TextArea dosaggiTerapiaTA, frequenzaTerapiaTA, orariTerapiaTA;
    @FXML private GridPane indicazioniFarmacoGP;
    @FXML private HBox loadingPage, mainPage, aggiungiEliminaHB;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button aggiungiTerapiaButton, salvaModificheTerapiaB;

    private PortaleMedicoController pmc = null;
    private PortalePazienteController ppc = null;
    private Paziente paziente;
    UtilityPortali upp;
    private Terapia terapia = null;
    private GestioneTerapie gt = null;
    private boolean aggiornamentoProgrammatico = false;

    @FXML
    private void initialize() {
        terapiePazienteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
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
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    mostraIndicazioniFarmaciTerapia();
                }
            });
            return cell;
        });

        dosaggiTerapiaTA.textProperty().addListener((obs, oldVal, newVal) -> {
            if (InputChecker.getInstance().verificaDosaggioFarmaco(newVal, farmaciTerapiaLV.getSelectionModel().getSelectedItem(), false) && dosaggiTerapiaTA != null) {
                dosaggiTerapiaTA.setStyle("-fx-border-color: #43a047;");
            } else {
                dosaggiTerapiaTA.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }

            if (!aggiornamentoProgrammatico) mostraBottoneSalvataggio(newVal);
        });

        frequenzaTerapiaTA.textProperty().addListener((obs, oldVal, newVal) -> {
            if (InputChecker.getInstance().campoVuoto(newVal)) {
                frequenzaTerapiaTA.setStyle("-fx-border-color: #43a047;");
            } else {
                frequenzaTerapiaTA.setStyle("-fx-border-color: #ff0000;  -fx-border-width: 3px;");
            }

            if (!aggiornamentoProgrammatico) mostraBottoneSalvataggio(newVal);
        });

        orariTerapiaTA.textProperty().addListener((obs, oldVal, newVal) -> {
            if (InputChecker.getInstance().verificaOrariTerapia(newVal) && orariTerapiaTA != null) {
                orariTerapiaTA.setStyle("-fx-border-color: #43a047;");
            } else {
                orariTerapiaTA.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }

            if (!aggiornamentoProgrammatico) mostraBottoneSalvataggio(newVal);
        });
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

            terapiePaziente.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearScreen() {
        if (indicazioniFarmacoGP.isVisible()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), indicazioniFarmacoGP);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.play();
            indicazioniFarmacoGP.setVisible(false);
            salvaModificheTerapiaB.setVisible(false);
        }
        infoTerapiaVB.setVisible(false);
    }

    private void mostraFarmaciTerapia() {
        Task<Void> task = new Task<>() {
            @Override protected Void call() {
                upp = new UtilityPortali(paziente);
                String data = upp.getIndicazioniTemporaliTerapia(terapia);
                ObservableList<String> farmaci = FXCollections.observableArrayList();
                for (FarmacoTerapia ft : terapia.getListaFarmaciTerapia()) farmaci.add(ft.getFarmaco().getNome());

                Platform.runLater(() -> {
                    nomeTerapiaTF.setText(terapia.getNome());
                    dateTerapiaTF.setText(data);
                    farmaciTerapiaLV.setItems(farmaci);
                    if (pmc != null) aggiungiEliminaHB.setVisible(true);
                });
                return null;
            }
            @Override protected void succeeded() {
                infoTerapiaVB.setVisible(true);
            }
        };
        new Thread(task).start();
    }

    private void mostraIndicazioniFarmaciTerapia() {
        salvaModificheTerapiaB.setVisible(false);

        if (pmc != null) {
            dosaggiTerapiaTA.setEditable(true);
            frequenzaTerapiaTA.setEditable(true);
            orariTerapiaTA.setEditable(true);
        }

        Task<Void> task = new Task<>() {
            @Override protected Void call() {
                String nome = farmaciTerapiaLV.getSelectionModel().getSelectedItem();
                String dosaggio = terapia.getDosaggioPerFarmaco(nome) + " " + GestioneFarmaci.getInstance().getFarmacoByName(nome).getUnitaMisura();
                String frequenza = terapia.getFrequenzaPerFarmaco(nome);
                String orari = terapia.getOrarioPerFarmaco(nome);

                Platform.runLater(() -> {
                    aggiornamentoProgrammatico = true;
                    dosaggiTerapiaTA.setText(dosaggio);
                    frequenzaTerapiaTA.setText(frequenza);
                    orariTerapiaTA.setText(orari);
                    aggiornamentoProgrammatico = false;
                    salvaModificheTerapiaB.setVisible(false);
                });
                return null;
            }
            @Override protected void succeeded() {
                indicazioniFarmacoGP.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), indicazioniFarmacoGP);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }
        };
        new Thread(task).start();
    }

    private void mostraBottoneSalvataggio(String newVal) {
        if (pmc != null && !newVal.trim().isEmpty() && !salvaModificheTerapiaB.isVisible()) {
            salvaModificheTerapiaB.setVisible(true);
            FadeTransition fade = new FadeTransition(Duration.millis(100), salvaModificheTerapiaB);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    public void setInstance(Portale controller, Paziente pazienteSelezionato) {
        if (controller instanceof PortaleMedicoController) {
            pmc = (PortaleMedicoController) controller;

            medicoUltimaModificaTF.setVisible(true);
            Medico m = new UtilityPortali().getMedicoSessione();
            medicoUltimaModificaTF.setText("Modificata l'ultima volta dal medico " + m.getCognome() + " " + m.getNome() + " (" + m.getCodiceFiscale() + ")");

        } else if  (controller instanceof PortalePazienteController) {
            ppc = (PortalePazienteController) controller;
        } else {
            throw new IllegalArgumentException("Nessun controller valido selezionato");
        }

        this.paziente = pazienteSelezionato;
        gt = new GestioneTerapie(paziente);

        Platform.runLater(this::caricaTerapiePaziente);
    }

    public void setNomeBottoneInserimentoTerapia() {
        if (pmc != null) {
            aggiungiTerapiaButton.setText("Aggiungi Terapia");
        }
    }

    public void caricaTerapiePaziente() {
        loadingPage.setVisible(true);
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Task<Void> loadingTerapieTask = new Task<>() {
            ObservableList<String> terapie = FXCollections.observableArrayList();

            @Override protected Void call() {
                upp = new UtilityPortali(paziente);
                terapie.addAll(upp.getListaTerapiePaziente());
                return null;
            }

            @Override protected void succeeded() {
                progressIndicator.setVisible(false);
                loadingPage.setVisible(false);
                mainPage.setVisible(true);
                boxTerapieVB.setVisible(true);

                terapiePazienteLV.setItems(terapie);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(250), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }
        };
        new Thread(loadingTerapieTask).start();
    }

    public void aggiungiFarmaciAllaTerapia() {
        if (indicazioniFarmacoGP.isVisible()) {
            indicazioniFarmacoGP.setVisible(false);
        }

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
        int quantitaFarmaci = farmaciAttualiTerapia.size();
        for (FarmacoTerapia ft : gt.getFarmaciSingolaTerapia()) {
            if (!farmaciAttualiTerapia.contains(ft)) {
                farmaciAttualiTerapia.add(ft);
            }
        }
        terapia.setListaFarmaciTerapia(farmaciAttualiTerapia);

        if (farmaciAttualiTerapia.size() != quantitaFarmaci) {
            salvaModificheTerapiaB.setVisible(true);
        }
        mostraFarmaciTerapia();
    }

    public void salvaModificheTerapia() {
        if (indicazioniFarmacoGP.isVisible()) {

            String nomeFarmaco = farmaciTerapiaLV.getSelectionModel().getSelectedItem();

            // verifica che i dati siano corretti
            if (!InputChecker.getInstance().verificaOrariTerapia(orariTerapiaTA.getText()) || !InputChecker.getInstance().verificaDosaggioFarmaco(dosaggiTerapiaTA.getText(), nomeFarmaco, false)) {
                Alert campiNonValidi = new Alert(Alert.AlertType.ERROR);
                campiNonValidi.setTitle("System Notification Service");
                campiNonValidi.setHeaderText("Dati non validi");
                campiNonValidi.setContentText("I dati che hai fornito non sono validi.\nAssicurati che orari e dosaggio siano corretti e riprova");
                campiNonValidi.showAndWait();

                return;
            }

            Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(nomeFarmaco);
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

            terapia.setIdMedicoUltimaModifica(upp.getMedicoSessione().getIdUtente());
        }

        if(gt.aggiornaTerapia(terapia)){
            Alert successoAggiornamentoTerapiaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoAggiornamentoTerapiaAlert.setTitle("System Notification Service");
            successoAggiornamentoTerapiaAlert.setHeaderText("Aggiornamento avvenuto con successo");
            successoAggiornamentoTerapiaAlert.setContentText("La terapia è stata aggiornata correttamente");
            successoAggiornamentoTerapiaAlert.showAndWait();

            salvaModificheTerapiaB.setVisible(false);

        }else{
            Alert erroreAggiornamentoTerapiaAlert = new Alert(Alert.AlertType.ERROR);
            erroreAggiornamentoTerapiaAlert.setTitle("System Notification Service");
            erroreAggiornamentoTerapiaAlert.setHeaderText("Errore durante l'aggiornamento");
            erroreAggiornamentoTerapiaAlert.setContentText("Si è verificato un errore durante l'aggiornamento della terapia.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
            erroreAggiornamentoTerapiaAlert.showAndWait();
        }
    }

    public void eliminaFarmacoTerapia() {

        Alert confermaOperazione = new Alert(Alert.AlertType.CONFIRMATION);
        confermaOperazione.setTitle("System Notification Service");
        confermaOperazione.setHeaderText("Sei sicuro di voler eliminare il farmaco selezionato?");
        confermaOperazione.setContentText("L'operazione è irreversibile");

        Optional<ButtonType> result = confermaOperazione.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(farmaciTerapiaLV.getSelectionModel().getSelectedItem());
            int index = -1;
            for (int i = 0; i < terapia.getListaFarmaciTerapia().size(); i++) {
                if (terapia.getListaFarmaciTerapia().get(i).getFarmaco().equals(farmaco)) {
                    index = i;
                    break;
                }
            }

            boolean status = false;

            if (index != -1) {
                terapia.getListaFarmaciTerapia().remove(index);
                status = gt.aggiornaTerapia(terapia);
            }

            if (status) {
                Alert farmacoRimossoCorrettamenteAlert = new Alert(Alert.AlertType.INFORMATION);
                farmacoRimossoCorrettamenteAlert.setTitle("System Notification Service");
                farmacoRimossoCorrettamenteAlert.setHeaderText("Eliminazione avvenuta con successo");
                farmacoRimossoCorrettamenteAlert.setContentText("Il farmaco selezionato è stato rimosso correttamente dalla terapia");
                farmacoRimossoCorrettamenteAlert.showAndWait();

                salvaModificheTerapiaB.setVisible(false);
                indicazioniFarmacoGP.setVisible(false);
                mostraFarmaciTerapia();

            } else {
                Alert erroreRimozioneFarmacoTerapia = new Alert(Alert.AlertType.ERROR);
                erroreRimozioneFarmacoTerapia.setTitle("System Notification Service");
                erroreRimozioneFarmacoTerapia.setHeaderText("Errore durante la rimozione");
                erroreRimozioneFarmacoTerapia.setContentText("Si è verificato un errore durante la rimozione del farmaco.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
                erroreRimozioneFarmacoTerapia.showAndWait();
            }
        }
    }
}