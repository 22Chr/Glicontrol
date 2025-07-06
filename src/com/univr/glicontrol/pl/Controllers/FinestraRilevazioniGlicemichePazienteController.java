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
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class FinestraRilevazioniGlicemichePazienteController implements Controller {

    UtilityPortali up;
    Paziente paziente;
    GestioneRilevazioniGlicemia grg;
    private PortalePazienteController ppc = null;
    private PortaleMedicoController pmc = null;

    @FXML
    private ComboBox<String> oraGlicemiaCB, minutiGlicemiaCB, primaODopoCB, pastoGlicemiaCB;
    @FXML
    private VBox detailPage;
    @FXML
    private HBox mainPage, mainPagePortaleMedico;
    @FXML
    private TextField valoreGlicemiaTF;
    @FXML
    private DatePicker dataGlicemiaDP;
    @FXML
    private ListView<String> glicemiaPazienteLV, rilevazioniGlicemichePortaleMedicoLV;
    @FXML
    private TextArea descrizioneEstesaTA;
    @FXML
    private Button indietroPortaleMedicoB, indietroB, eliminaB;

    @FXML
    private void initialize(){
        oraGlicemiaCB.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutiGlicemiaCB.getItems().addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
        primaODopoCB.getItems().addAll("Prima", "Dopo");
        pastoGlicemiaCB.getItems().addAll("Colazione", "Pranzo", "Cena", "Merenda");

        glicemiaPazienteLV.setCellFactory(lv -> {
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

        rilevazioniGlicemichePortaleMedicoLV.setCellFactory(lv -> {
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

        valoreGlicemiaTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                valoreGlicemiaTF.setStyle("");
            } else if (InputChecker.getInstance().verificaInputGlicemia(newValue)) {
                valoreGlicemiaTF.setStyle("-fx-border-color: #43a047;");
            } else {
                valoreGlicemiaTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });
    }

    public void cambiaPagina(){
        if(detailPage.isVisible()) {
            detailPage.setVisible(false);
            if (pmc != null) {
                mainPage.setVisible(false);
                mainPagePortaleMedico.setVisible(true);
            } else {
                mainPage.setVisible(true);
            }
        } else {
            detailPage.setVisible(true);
            if (pmc != null) {
                indietroB.setVisible(false);
                eliminaB.setVisible(false);
                indietroPortaleMedicoB.setVisible(true);
            }
            mainPage.setVisible(false);
            mainPagePortaleMedico.setVisible(false);
        }
    }

    public void inserisciNuovaRilevazioneGlicemica(){
        Date data;
        if (dataGlicemiaDP.getValue() == null) {
            data = null;
        } else {
            data = Date.valueOf(dataGlicemiaDP.getValue());
        }


        if (valoreGlicemiaTF.getText().isEmpty() || data == null || oraGlicemiaCB.getValue() == null || minutiGlicemiaCB.getValue() == null || primaODopoCB.getValue() == null || pastoGlicemiaCB.getValue() == null) {
            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Notification Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per poter inserire una rilevazione glicemica devi precisarne la data, l'ora, il valore e l'associazione rispetto al pasto di riferimento.\nInserisci tutti i dati e riprova");
            datiMancantiAlert.showAndWait();

            return;
        }

        if (!InputChecker.getInstance().verificaInputGlicemia(valoreGlicemiaTF.getText())) {
            Alert tipoNonValidoAlert = new Alert(Alert.AlertType.ERROR);
            tipoNonValidoAlert.setTitle("System Notification Service");
            tipoNonValidoAlert.setHeaderText("Input non valido");
            tipoNonValidoAlert.setContentText("Sono ammessi solamente valori numerici per i valori glicemici.\nRiprova");
            tipoNonValidoAlert.showAndWait();

            return;
        }

        int ora = oraGlicemiaCB.getValue().equals("00") ? 0 : Integer.parseInt(oraGlicemiaCB.getValue());
        int minuti = minutiGlicemiaCB.getValue().equals("00") ? 0 : Integer.parseInt(minutiGlicemiaCB.getValue());
        Time orario = up.convertiOra(ora, minuti);

        int status = grg.inserisciRilevazione(data, orario, Float.parseFloat(valoreGlicemiaTF.getText()), pastoGlicemiaCB.getValue(), primaODopoCB.getValue());

        if (status == -1 ) {
            Alert duplicazioneRilevazioneGlicemiaAlert = new Alert(Alert.AlertType.ERROR);
            duplicazioneRilevazioneGlicemiaAlert.setTitle("System Notification Service");
            duplicazioneRilevazioneGlicemiaAlert.setHeaderText("Errore durante l'inserimento della rilevazione glicemica");
            duplicazioneRilevazioneGlicemiaAlert.setContentText("La rilevazione glicemica è già stata inserita con successo.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            duplicazioneRilevazioneGlicemiaAlert.showAndWait();
        } else if (status == 0) {
            Alert erroreInserimentoRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoRilevazioneGlicemicaAlert.setTitle("System Notification Service");
            erroreInserimentoRilevazioneGlicemicaAlert.setHeaderText("Errore durante l'inserimento della rilevazione glicemica");
            erroreInserimentoRilevazioneGlicemicaAlert.setContentText("Non è stato possibile inserire la rilevazione glicemica.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoRilevazioneGlicemicaAlert.showAndWait();
        } else {
            Alert successoInserimentoRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoRilevazioneGlicemicaAlert.setTitle("System Notification Service");
            successoInserimentoRilevazioneGlicemicaAlert.setHeaderText("Rilevazione glicemica inserita con successo");
            successoInserimentoRilevazioneGlicemicaAlert.setContentText("La nuova rilevazione glicemica è stata inserita con successo");
            successoInserimentoRilevazioneGlicemicaAlert.showAndWait();

            resetListViewRilevazioniGlicemiche();
            ppc.aggiornaListaRilevazioniGlicemicheOdierne();
            ppc.aggiornaGrafico();

            // Ripulisci i campi per l'inserimento di nuove rilevazioni
            dataGlicemiaDP.setValue(null);
            oraGlicemiaCB.setValue(null);
            minutiGlicemiaCB.setValue(null);
            primaODopoCB.setValue(null);
            pastoGlicemiaCB.setValue(null);
            valoreGlicemiaTF.clear();
            valoreGlicemiaTF.requestFocus();
        }
    }

    private void resetListViewRilevazioniGlicemiche(){
        UtilityPortali newUp = new UtilityPortali(paziente);
        ObservableList<String> newRilevazioni = FXCollections.observableArrayList();
        newRilevazioni.addAll(newUp.getListaRilevazioniGlicemichePazienti());
        glicemiaPazienteLV.setItems(newRilevazioni);

    }


    public void eliminaRilevazione() {
        String rilevazioneFormattata = glicemiaPazienteLV.getSelectionModel().getSelectedItem();
        RilevazioneGlicemica rilevazione = up.getRilevazioneGlicemicaPerValoreFormattata(rilevazioneFormattata);

        if (grg.eliminaRilevazione(rilevazione.getIdRilevazione())) {
            Alert successoEliminazioneRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoEliminazioneRilevazioneGlicemicaAlert.setTitle("System Notification Service");
            successoEliminazioneRilevazioneGlicemicaAlert.setHeaderText("Rilevazione glicemica eliminata con successo");
            successoEliminazioneRilevazioneGlicemicaAlert.setContentText("La rilevazione glicemica è stata eliminata con successo");
            successoEliminazioneRilevazioneGlicemicaAlert.showAndWait();

            cambiaPagina();
            resetListViewRilevazioniGlicemiche();
            ppc.aggiornaListaRilevazioniGlicemicheOdierne();
            ppc.aggiornaGrafico();

        } else {
            Alert erroreEliminazioneRilevazioneGlicemicaAlert = new Alert(Alert.AlertType.ERROR);
            erroreEliminazioneRilevazioneGlicemicaAlert.setTitle("System Notification Service");
            erroreEliminazioneRilevazioneGlicemicaAlert.setHeaderText("Errore nell'eliminazione della rilevazione glicemica");
            erroreEliminazioneRilevazioneGlicemicaAlert.setContentText("Si è verificato un errore durante l'eliminazione della rilevazione glicemica selezionata. Riprova");
            erroreEliminazioneRilevazioneGlicemicaAlert.showAndWait();
        }
    }

    public void setInstance(Portale portale, Paziente paziente) {
        this.paziente = paziente;
        up = new UtilityPortali(paziente);
        grg = new GestioneRilevazioniGlicemia(paziente);

        if (portale instanceof PortaleMedicoController) {
            this.pmc = (PortaleMedicoController)  portale;
            mainPage.setVisible(false);
            mainPagePortaleMedico.setVisible(true);
        } else {
            this.ppc = (PortalePazienteController) portale;
        }


        Platform.runLater(this::caricaRilevazioniGlicemiche);
    }

    private void caricaRilevazioniGlicemiche() {
        Task<Void> loadRilevazioniGlicemiche = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> rilevazioni = FXCollections.observableArrayList();
                rilevazioni.addAll(up.getListaRilevazioniGlicemichePazienti());

                if (ppc != null) {
                    Platform.runLater(() -> {
                        glicemiaPazienteLV.setItems(rilevazioni);

                        GlicontrolCoreSystem.getInstance().setFinestraRilevazioniGlicemicheIsOpen();
                        Stage stage = (Stage) glicemiaPazienteLV.getScene().getWindow();
                        stage.setOnCloseRequest(event -> GlicontrolCoreSystem.getInstance().setFinestraRilevazioniGlicemicheIsClose());
                    });
                } else {
                    Platform.runLater(() -> rilevazioniGlicemichePortaleMedicoLV.setItems(rilevazioni));
                    Platform.runLater(() -> coloraAnomalieRilevazioniGlicemiche());
                }

                return null;
            }

            @Override
            protected void failed() {
                System.err.println("Si è verificato un errore durante il caricamento delle rilevazioni glicemiche");

                Alert erroreCaricamentoRilevazioniGlicemiche = new Alert(Alert.AlertType.ERROR);
                erroreCaricamentoRilevazioniGlicemiche.setTitle("System Information Service");
                erroreCaricamentoRilevazioniGlicemiche.setHeaderText("Errore durante il caricamento dei dati");
                erroreCaricamentoRilevazioniGlicemiche.setContentText("Si è verificato un errore durante il caricamento delle rilevazioni glicemiche.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
                erroreCaricamentoRilevazioniGlicemiche.showAndWait();
            }
        };

        new Thread(loadRilevazioniGlicemiche).start();
    }

    // colora i campi nella listview vista dal medico per rilevazioni glicemiche
    private void coloraAnomalieRilevazioniGlicemiche(){
        UtilityPortali newUp = new UtilityPortali(paziente);
        ObservableList<String> newRilevazioni = FXCollections.observableArrayList();
        newRilevazioni.addAll(newUp.getListaRilevazioniGlicemichePazienti());
        rilevazioniGlicemichePortaleMedicoLV.getItems().setAll(newRilevazioni);

        List<Integer> codiciRilevazioni = GlicontrolCoreSystem.getInstance().verificaLivelliGlicemici(paziente, false, false);
        rilevazioniGlicemichePortaleMedicoLV.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);

                int index = getIndex();
                if (index >= 0 && index < codiciRilevazioni.size()) {
                    int severity = codiciRilevazioni.get(index);
                    String style;

                    switch (Math.abs(severity)) {
                        case 0 -> style = "";
                        case 1 -> style = "-fx-background-color: #ffdd00; -fx-text-fill: black; -fx-border-color: whitesmoke;";
                        case 2 -> style = "-fx-background-color: #ff9900; -fx-text-fill: black; -fx-border-color: whitesmoke;";
                        case 3 -> style = "-fx-background-color: #ff0000; -fx-text-fill: white; -fx-border-color: whitesmoke;";
                        case 4 -> style = "-fx-background-color: #6b0c8a; -fx-text-fill: white; -fx-border-color: whitesmoke;";
                        default -> style = "";
                    }

                    setStyle(style);
                } else {
                    setStyle("");
                }
            }
        });
    }

}
