package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestioneSintomi;
import com.univr.glicontrol.bll.InputChecker;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class FinestraSintomiPazienteController implements Controller {
    UtilityPortali upp;
    Paziente paziente;
    GestioneSintomi gs;
    PortaleMedicoController pmc = null;
    PortalePazienteController ppc = null;

    @FXML
    private TextArea descrizioneTA, descrizioneEstesaTA;
    @FXML
    private ListView<String> sintomiPazienteLV, sintomiPazientePortaleMedicoLV;
    @FXML
    private HBox mainPage, pageSintomiPerMedico;
    @FXML
    private VBox detailPage;

    @FXML
    private void initialize() {

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

        sintomiPazientePortaleMedicoLV.setCellFactory(lv -> {
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

        descrizioneTA.textProperty().addListener((observable, oldValue, newValue) -> {
            if(InputChecker.getInstance().campoVuoto(descrizioneTA.getText()) && descrizioneTA.getText() != null){
                descrizioneTA.setStyle("-fx-background-color: #43a047;");
            }else{
                descrizioneTA.setStyle("-fx-background-color: #ff0000; -fx-border-width: 3px");
            }
        });
    }

    public void resetListViewSintomi() {
        UtilityPortali newUpp = new UtilityPortali(paziente);
        ObservableList<String> newSintomi = FXCollections.observableArrayList();
        newSintomi.addAll(newUpp.getListaSintomiPazienti());
        if(pmc == null){
            sintomiPazienteLV.setItems(newSintomi);
        }else{
            sintomiPazientePortaleMedicoLV.setItems(newSintomi);
        }
        descrizioneTA.clear();
        descrizioneTA.requestFocus();
    }

    public void inserisciNuovoSintomo() {

        if (descrizioneTA.getText().isEmpty() || !InputChecker.getInstance().campoVuoto(descrizioneTA.getText())) {
            Alert campoVuotoAlert = new  Alert(Alert.AlertType.ERROR);
            campoVuotoAlert.setTitle("System Information Service");
            campoVuotoAlert.setHeaderText("Dati mancanti");
            campoVuotoAlert.setContentText("Per inserire un sintomo è necessario fornirne una descrizione valida");
            campoVuotoAlert.showAndWait();

        } else {

            if (gs.inserisciSintomo(descrizioneTA.getText())) {
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

    public void cambiaPagina() {
        if(detailPage.isVisible()) {
            detailPage.setVisible(false);
            if (pmc == null) {
                mainPage.setVisible(true);
            } else {
                pageSintomiPerMedico.setVisible(true);
            }
        } else {
            detailPage.setVisible(true);
            mainPage.setVisible(false);
            pageSintomiPerMedico.setVisible(false);
        }
    }

    public void eliminaSintomo() {
        String sintomoFormattato;
        if(pmc == null) {
            sintomoFormattato = sintomiPazienteLV.getSelectionModel().getSelectedItem();
        }else{
            sintomoFormattato = sintomiPazientePortaleMedicoLV.getSelectionModel().getSelectedItem();
        }
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

    public void setInstance(Portale portale, Paziente paziente) {
        if(portale instanceof PortaleMedicoController){
            this.pmc = (PortaleMedicoController) portale;
            mainPage.setVisible(false);
            pageSintomiPerMedico.setVisible(true);
        } else {
            this.ppc = (PortalePazienteController) portale;
            mainPage.setVisible(true);
            pageSintomiPerMedico.setVisible(false);
        }

        this.paziente = paziente;
        upp = new UtilityPortali(paziente);
        gs = new GestioneSintomi(paziente);
        Platform.runLater(this::caricaSintomi);
    }

    private void caricaSintomi() {

        Task<Void> loadSintomiTask = new Task<>() {
            @Override
            protected Void call(){
                ObservableList<String> sintomi = FXCollections.observableArrayList();
                sintomi.addAll(upp.getListaSintomiPazienti());

                if(pmc == null) {
                    Platform.runLater(() -> sintomiPazienteLV.setItems(sintomi));
                }else{
                    Platform.runLater(() -> sintomiPazientePortaleMedicoLV.setItems(sintomi));
                }

                return null;
            }

            @Override
            protected void failed() {
                System.err.println("Si è verificato un errore durante il caricamento dei dati");

                Alert erroreCaricamentoSintomi = new Alert(Alert.AlertType.ERROR);
                erroreCaricamentoSintomi.setTitle("System Notification Service");
                erroreCaricamentoSintomi.setHeaderText("Errore durante il caricamento dei dati");
                erroreCaricamentoSintomi.setContentText("Si è verificato un errore durante il caricamento dei sintomi del paziente.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
                erroreCaricamentoSintomi.showAndWait();
            }
        };

        new Thread(loadSintomiTask).start();
    }
}
