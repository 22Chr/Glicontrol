package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;

public class FinestraPatologieConcomitantiPazienteController implements Controller {

    private UtilityPortali up;
    private Paziente paziente;
    private Medico medico = null;
    private GestionePatologieConcomitanti gpc;
    private PortaleMedicoController pmc = null;
    private boolean inseritaDalMedico = false;

    @FXML
    private TextField nomePatologiaTF;
    @FXML
    private TextArea descrizionePatologiaTA, descrizioneEstesaTA;
    @FXML
    private DatePicker dataInizioDP, dataFineDP;
    @FXML
    private ListView<String> patologiePazienteLV;
    @FXML
    private HBox mainPage;
    @FXML
    private VBox detailPage;
    @FXML
    private Button indietroPortaleMedicoB, indietroPortalePazienteB, terminaPatologiaB;
    @FXML
    private Label descriviPatologiaLabel;

    public void setInstance(Portale portale, Paziente paziente) {

        this.paziente = paziente;
        gpc = new GestionePatologieConcomitanti(paziente);
        up = new UtilityPortali(paziente);

        if (portale instanceof PortaleMedicoController) {
            this.pmc = (PortaleMedicoController) portale;
            descriviPatologiaLabel.setText("Descrivi la patologia del paziente");
            inseritaDalMedico = true;
            medico = new UtilityPortali().getMedicoSessione();
        }

        Platform.runLater(this::caricaPatologieConcomitanti);
    }

    @FXML
    private void initialize(){
        indietroPortaleMedicoB.setVisible(false);
        terminaPatologiaB.setVisible(false);


        patologiePazienteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    descrizioneEstesaTA.setText(up.getPatologiaConcomitantePerNomeFormattata(cell.getText()).getDescrizione());
                    cambiaPagina();
                }
            });

            return cell;
        });

        descrizionePatologiaTA.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                descrizionePatologiaTA.setStyle("");
            } else if (InputChecker.getInstance().campoVuoto(newValue)) {
                descrizionePatologiaTA.setStyle("-fx-border-color: #43a047;");
            } else {
                descrizionePatologiaTA.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        nomePatologiaTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                nomePatologiaTF.setStyle("");
            } else if (InputChecker.getInstance().campoVuoto(newValue)) {
                nomePatologiaTF.setStyle("-fx-border-color: #43a047;");
            } else {
                nomePatologiaTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });
    }

    public void cambiaPagina() {
        if(detailPage.isVisible()) {
            detailPage.setVisible(false);
            mainPage.setVisible(true);
        } else {
            detailPage.setVisible(true);
            mainPage.setVisible(false);
            if (pmc != null) {
                terminaPatologiaB.setVisible(true);
                indietroPortaleMedicoB.setVisible(true);
                indietroPortalePazienteB.setVisible(false);
            }
        }
    }

    public void resetListViewPatologie(){
        UtilityPortali newUp = new UtilityPortali(paziente);
        ObservableList<String> newPatologie = FXCollections.observableArrayList();
        newPatologie.addAll(newUp.getListaPatologieConcomitantiPaziente());
        patologiePazienteLV.setItems(newPatologie);
        descrizionePatologiaTA.clear();
        descrizionePatologiaTA.requestFocus();
    }

    public void inserisciNuovaPatologia(){
        Date dataInizio;
        Date dataFine;

        if (dataFineDP.getValue() == null) {
            dataFine = null;
        } else {
            dataFine = Date.valueOf(dataFineDP.getValue());
        }

        if (dataInizioDP.getValue() == null) {
            dataInizio = null;
        } else {
            dataInizio = Date.valueOf(dataInizioDP.getValue());
        }

        if (descrizionePatologiaTA.getText().isEmpty() ||
                nomePatologiaTF.getText().isEmpty() ||
                dataInizio == null ||
                !InputChecker.getInstance().campoVuoto(descrizionePatologiaTA.getText()) ||
                !InputChecker.getInstance().campoVuoto(nomePatologiaTF.getText())) {

            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Notification Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per poter inserire una patologia devi precisarne il nome, la data di inizio e fornirne una descrizione.\nInserisci tutti i dati e riprova");
            datiMancantiAlert.showAndWait();
            return;
        }

        int status = gpc.inserisciPatologiaConcomitante(nomePatologiaTF.getText(), descrizionePatologiaTA.getText(), dataInizio, dataFine);

        if(status == 1){
            Alert successoInserimentoSintomoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoSintomoAlert.setTitle("System Notification Service");
            successoInserimentoSintomoAlert.setHeaderText("Patologia concomitante inserita con successo");
            successoInserimentoSintomoAlert.setContentText("La nuova patologia concomitante è stata inserita con successo");
            successoInserimentoSintomoAlert.showAndWait();

            resetListViewPatologie();

            GlicontrolCoreSystem.getInstance().generaLog(Log.PATOLOGIA_CONCOMITANTE, paziente, medico, true, inseritaDalMedico);

            // Ripulisci i campit per l'inserimento di nuove patologie
            nomePatologiaTF.clear();
            descrizionePatologiaTA.clear();
            dataInizioDP.setValue(null);
            dataFineDP.setValue(null);


        } else if (status == 0) {
            Alert erroreInserimentoSintomoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoSintomoAlert.setTitle("System Notification Service");
            erroreInserimentoSintomoAlert.setHeaderText("Errore durante l'inserimento della nuova patologia concomitante");
            erroreInserimentoSintomoAlert.setContentText("Non è stato possibile inserire la nuova patologia concomitante.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoSintomoAlert.showAndWait();
        } else {
            Alert erroreDuplicazioneSintomoAlert = new Alert(Alert.AlertType.ERROR);
            erroreDuplicazioneSintomoAlert.setTitle("System Notification Service");
            erroreDuplicazioneSintomoAlert.setHeaderText("Errore durante l'inserimento della nuova patologia concomitante");
            erroreDuplicazioneSintomoAlert.setContentText("La patologia che stai cercando di inserire esiste già nel sistema");
            erroreDuplicazioneSintomoAlert.showAndWait();
            nomePatologiaTF.clear();
            descrizionePatologiaTA.clear();
            dataInizioDP.setValue(null);
            dataFineDP.setValue(null);
        }
    }

    public void terminaPatologiaConcomitante() {
        PatologiaConcomitante p = up.getPatologiaConcomitantePerNomeFormattata(patologiePazienteLV.getSelectionModel().getSelectedItem());

        if (p.getDataFine() != null) {
            Alert patologiaGiaTerminataAlert = new Alert(Alert.AlertType.INFORMATION);
            patologiaGiaTerminataAlert.setTitle("System Notification Service");
            patologiaGiaTerminataAlert.setHeaderText("La patologia concomitante selezionata risulta già essere conclusa");
            patologiaGiaTerminataAlert.setContentText("Non è stata apportata alcuna modifica");
            patologiaGiaTerminataAlert.showAndWait();

            return;
        }

        p.setDataFine(Date.valueOf(LocalDate.now()));

        if (gpc.aggiornaPatologiaConcomitante(p)) {
            Alert conclusionePatologia = new Alert(Alert.AlertType.INFORMATION);
            conclusionePatologia.setTitle("System Notification Service");
            conclusionePatologia.setHeaderText("Patologia concomitante aggiornata con successo");
            conclusionePatologia.setContentText("La patologia è stata segnata come conclusa in data odierna.\n\nRicordati di segnare come conclusa anche l'eventuale terapia associata");
            conclusionePatologia.showAndWait();

            resetListViewPatologie();
            GlicontrolCoreSystem.getInstance().generaLog(Log.PATOLOGIA_CONCOMITANTE, paziente, medico, false, inseritaDalMedico);
            cambiaPagina();

        } else {
            Alert erroreConclusionePatologia = new Alert(Alert.AlertType.ERROR);
            erroreConclusionePatologia.setTitle("System Notification Service");
            erroreConclusionePatologia.setHeaderText("Si è verificato un errore durante l'operazione");
            erroreConclusionePatologia.setContentText("Non è stato possibile segnare la patologia come terminata");
            erroreConclusionePatologia.showAndWait();
        }
    }

    private void caricaPatologieConcomitanti() {
        Task<Void> loadingPatologieTask = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> patologie = FXCollections.observableArrayList();
                patologie.addAll(up.getListaPatologieConcomitantiPaziente());
                Platform.runLater(() -> patologiePazienteLV.setItems(patologie));

                return null;
            }

            @Override
            protected void failed() {
                System.err.println("Si è verificato un errore durante il caricamento dei dati");

                Alert erroreCaricamentoPatologie = new Alert(Alert.AlertType.ERROR);
                erroreCaricamentoPatologie.setTitle("System Notification Service");
                erroreCaricamentoPatologie.setHeaderText("Errore durante il caricamento dei dati");
                erroreCaricamentoPatologie.setContentText("Si è verificato un errore durante il caricamento delle patologie concomitanti.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
                erroreCaricamentoPatologie.showAndWait();
            }
        };

        new Thread(loadingPatologieTask).start();
    }

}
