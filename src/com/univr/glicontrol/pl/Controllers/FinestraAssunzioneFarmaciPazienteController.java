package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

public class FinestraAssunzioneFarmaciPazienteController implements Controller {
    private UtilityPortali up;
    private Paziente paziente;
    private GestioneAssunzioneFarmaci gaf;
    private PortaleMedicoController pmc = null;

    @FXML
    private HBox mainPage;
    @FXML
    private VBox detailPage;
    @FXML
    private ListView<String> farmaciAssuntiOggiLV, farmaciTerapieConcomitantiLV, farmaciTerapiaDiabeteLV;
    @FXML
    private ComboBox<String> listaFarmaciDaAssumereCB, oraFarmacoCB, minutiFarmacoCB;
    @FXML
    private DatePicker dataFarmacoPazienteDP;
    @FXML
    private TextArea descrizioneEstesaTA;
    @FXML
    private TextField dosaggioTF;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private HBox loadingPage, mainPagePortaleMedico;

    public void initialize() {
        // Mostra l'indicatore di caricamento
        loadingPage.setVisible(true);
        loadingIndicator.setVisible(true);
        loadingIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        // Inizializzazione immediata dei componenti statici
        oraFarmacoCB.getItems().addAll(
                "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
        );
        minutiFarmacoCB.getItems().addAll(
                "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
        );

        farmaciAssuntiOggiLV.setCellFactory(lv -> {
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

        dosaggioTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                dosaggioTF.setStyle("");
            } else if (InputChecker.getInstance().verificaInputDosaggioFarmaco(newValue)) {
                dosaggioTF.setStyle("-fx-border-color: #43a047;");
            } else {
                dosaggioTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });
    }

    public void registraAssunzioneFarmaco() {
        Date data;
        if (dataFarmacoPazienteDP.getValue() == null) {
            data = null;
        } else {
            data = Date.valueOf(dataFarmacoPazienteDP.getValue());
        }

        if (listaFarmaciDaAssumereCB.getValue() == null ||
                oraFarmacoCB.getValue() == null ||
                minutiFarmacoCB.getValue() == null ||
                data == null ||
                dosaggioTF.getText().isEmpty() ||
                !InputChecker.getInstance().verificaInputDosaggioFarmaco(dosaggioTF.getText())) {

            Alert datiMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Notification Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per poter registrare l'assunzione di un farmaco devi scegliere un farmaco e precisare il dosaggio, la data e l'ora di assunzione.\nInserisci tutti i dati e riprova");
            datiMancantiAlert.showAndWait();
            return;
        }

        Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(listaFarmaciDaAssumereCB.getValue());
        Time oraAssunzione = getOra();

        int status = gaf.registraAssunzioneFarmaco(farmaco, data, oraAssunzione, Float.parseFloat(dosaggioTF.getText()));
        if (status != 0) {
            Alert successoInserimentoFarmacoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoFarmacoAlert.setTitle("System Notification Service");
            successoInserimentoFarmacoAlert.setHeaderText("Farmaco registrato con successo");
            successoInserimentoFarmacoAlert.setContentText("L'assunzione del farmaco è stata registrata correttamente");
            successoInserimentoFarmacoAlert.showAndWait();

        } else {
            Alert erroreInserimentoFarmacoAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoFarmacoAlert.setTitle("System Notification Service");
            erroreInserimentoFarmacoAlert.setHeaderText("Errore durante l'inserimento del farmaco");
            erroreInserimentoFarmacoAlert.setContentText("Non è stato possibile registrare l'assunzione del farmaco.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoFarmacoAlert.showAndWait();
            return;
        }

        if (status == -1) {
            Alert eccessoDosaggioFarmacoAlert = new Alert(Alert.AlertType.WARNING);
            eccessoDosaggioFarmacoAlert.setTitle("System Notification Service");
            eccessoDosaggioFarmacoAlert.setHeaderText("Dosaggio eccessivo");
            eccessoDosaggioFarmacoAlert.setContentText("Il quantitativo che hai assunto per questo farmaco supera quello giornaliero previsto dalla tua terapia.\nLo staff medico ne sarà informato");
            eccessoDosaggioFarmacoAlert.showAndWait();

        }

        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(event -> {
            if (!GlicontrolCoreSystem.getInstance().verificaCoerenzaOrarioAssunzione(paziente, farmaco.getNome(), oraAssunzione)) {
                Platform.runLater(() -> {
                    ServizioNotifiche.getInstance().notificaMancataAderenzaOrariFarmaciTerapia();
                });
            }
        });
        pause.play();

        resetListViewFarmaciAssuntiOggi();

        PauseTransition pausaAggiornamentoListaFarmaciDaAssumere = new PauseTransition(javafx.util.Duration.seconds(1.5));
        pausaAggiornamentoListaFarmaciDaAssumere.setOnFinished(event -> resetListViewFarmaciDaAssumere());
        pausaAggiornamentoListaFarmaciDaAssumere.play();
    }

    public void cambiaPagina() {
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
            mainPage.setVisible(false);
            mainPagePortaleMedico.setVisible(false);
        }
    }

    public void eliminaAssunzioneFarmaco() {
        int idAssunzione = 0;
        int idFarmaco = up.getFarmacoPerNomeFormattato(farmaciAssuntiOggiLV.getSelectionModel().getSelectedItem()).getIdFarmaco();
        String orarioAssunzioneFormattato = getOraAssunzioneDaNomeAssunzioneFormattato(farmaciAssuntiOggiLV.getSelectionModel().getSelectedItem());

        for (AssunzioneFarmaco af : gaf.getListaAssunzioneFarmaci()) {
            if (af.getIdFarmaco() == idFarmaco &&
                    af.getData().equals(Date.valueOf(LocalDate.now())) &&
                    orarioAssunzioneFormattato.equals(af.getOra().toString().substring(0, 5))) {

                idAssunzione = af.getIdAssunzioneFarmaco();
                break;

            }
        }

        if (idAssunzione == 0) {
            throw new RuntimeException("Errore: non è stato possibile trovare l'id dell'assunzione del farmaco");
        }

        if (gaf.eliminaAssunzioneFarmaco(idAssunzione)) {
            Alert successoEliminazioneFarmacoAlert = new Alert(Alert.AlertType.INFORMATION);
            successoEliminazioneFarmacoAlert.setTitle("System Information Service");
            successoEliminazioneFarmacoAlert.setHeaderText("Registrazione eliminata con successo");
            successoEliminazioneFarmacoAlert.setContentText("L'assunzione del farmaco è stata eliminata correttamente");
            successoEliminazioneFarmacoAlert.showAndWait();

            cambiaPagina();
            resetListViewFarmaciAssuntiOggi();

            PauseTransition pausaAggiornamentoListaFarmaciDaAssumere = new PauseTransition(javafx.util.Duration.seconds(0.8));
            pausaAggiornamentoListaFarmaciDaAssumere.setOnFinished(event -> resetListViewFarmaciDaAssumere());
            pausaAggiornamentoListaFarmaciDaAssumere.play();

        } else {
            Alert erroreEliminazioneFarmacoAlert = new Alert(Alert.AlertType.ERROR);
            erroreEliminazioneFarmacoAlert.setTitle("System Information Service");
            erroreEliminazioneFarmacoAlert.setHeaderText("Errore durante l'eliminazione dell'assunzione");
            erroreEliminazioneFarmacoAlert.setContentText("Si è verificato un errore durante l'eliminazione dell'assunzione del farmaco. Riprova");
            erroreEliminazioneFarmacoAlert.showAndWait();
        }
    }

    private void resetListViewFarmaciAssuntiOggi() {
        UtilityPortali newUp = new UtilityPortali(paziente);
        ObservableList<String> newFarmaciAssuntiOggi = FXCollections.observableArrayList();
        newFarmaciAssuntiOggi.addAll(newUp.getListaFarmaciAssuntiOggi());
        farmaciAssuntiOggiLV.setItems(newFarmaciAssuntiOggi);
        dataFarmacoPazienteDP.setValue(null);
        oraFarmacoCB.setValue(null);
        oraFarmacoCB.setPromptText("ora");
        minutiFarmacoCB.setValue(null);
        minutiFarmacoCB.setPromptText("min");
        dataFarmacoPazienteDP.requestFocus();
        listaFarmaciDaAssumereCB.setValue(null);
        listaFarmaciDaAssumereCB.requestFocus();
        dosaggioTF.clear();
    }

    private void resetListViewFarmaciDaAssumere() {
        UtilityPortali newUp = new UtilityPortali();
        ObservableList<String> farmaciDaAssumere = FXCollections.observableArrayList();
        farmaciDaAssumere.addAll(newUp.getListaFarmaciDaAssumere());
        listaFarmaciDaAssumereCB.setItems(farmaciDaAssumere);
    }

    private Time getOra() {
        int ora = oraFarmacoCB.getValue().equals("00") ? 0 : Integer.parseInt(oraFarmacoCB.getValue());
        int minuti = minutiFarmacoCB.getValue().equals("00") ? 0 : Integer.parseInt(minutiFarmacoCB.getValue());
        return up.convertiOra(ora, minuti);
    }

    private String getOraAssunzioneDaNomeAssunzioneFormattato(String nomeAssunzione) {
        String check = " alle ";
        int limit = nomeAssunzione.indexOf(check);
        if (limit == -1) {
            return nomeAssunzione;
        } else {
            return nomeAssunzione.substring(limit + 6, limit + 11);
        }
    }

    public void setInstance(Portale portale, Paziente paziente) {
        this.paziente = paziente;
        up = new UtilityPortali(paziente);
        gaf = new GestioneAssunzioneFarmaci(paziente);

        if (portale instanceof PortaleMedicoController) {
            this.pmc = (PortaleMedicoController)  portale;
            Platform.runLater(this::caricaListaPortaleMedico);
        } else {
            Platform.runLater(this::caricaListePortalePaziente);
        }

    }

    private void caricaListaPortaleMedico() {
        Task<Void> loadListaPortaleMedico = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> listaFarmaciDiabeteAssuntiDalPaziente = FXCollections.observableArrayList();
                listaFarmaciDiabeteAssuntiDalPaziente.addAll(up.getListaCompletaAssunzioneFarmaciDiabete());
                Platform.runLater(() -> farmaciTerapiaDiabeteLV.setItems(listaFarmaciDiabeteAssuntiDalPaziente));

                ObservableList<String> listaFarmaciTerapieConcomitantiAssuntiDalPaziente = FXCollections.observableArrayList();
                listaFarmaciTerapieConcomitantiAssuntiDalPaziente.addAll(up.getListaCompletaAssunzioneFarmaciTerapieConcomitanti());
                Platform.runLater(() -> farmaciTerapieConcomitantiLV.setItems(listaFarmaciTerapieConcomitantiAssuntiDalPaziente));

                return null;
            }

            @Override
            protected void succeeded() {
                // Nasconde l'indicatore di caricamento quando ha finito
                loadingIndicator.setVisible(false);
                loadingPage.setVisible(false);
                mainPagePortaleMedico.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(250), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                loadingPage.setVisible(false);

                System.err.println("Si è verificato un errore durante il caricamento dei dati");

                Alert erroreCaricamentoListaAssunzioneFarmaciCompleta = new Alert(Alert.AlertType.ERROR);
                erroreCaricamentoListaAssunzioneFarmaciCompleta.setTitle("System Notification Service");
                erroreCaricamentoListaAssunzioneFarmaciCompleta.setHeaderText("Errore durante il caricamento dei dati");
                erroreCaricamentoListaAssunzioneFarmaciCompleta.setContentText("Si è verificato un errore durante il caricamento della lista complessiva dei farmaci assunti dal paziente.\nSe il problema dovesse persistere, riavvia l'applicazione e riprova");
                erroreCaricamentoListaAssunzioneFarmaciCompleta.showAndWait();
            }
        };

        new Thread(loadListaPortaleMedico).start();
    }

    private void caricaListePortalePaziente() {
        Task<Void> loadListePortalePaziente = new Task<>() {
            @Override
            protected Void call() {
                // Caricamento delle due liste (operazioni potenzialmente lente)
                ObservableList<String> farmaciDaAssumere = FXCollections.observableArrayList();
                for (String farmaco : up.getListaFarmaciDaAssumere()) {
                    if (!farmaciDaAssumere.contains(farmaco))
                        farmaciDaAssumere.add(farmaco);
                }

                ObservableList<String> farmaciAssuntiOggi = FXCollections.observableArrayList();
                farmaciAssuntiOggi.addAll(up.getListaFarmaciAssuntiOggi().reversed());

                Platform.runLater(() -> {
                    listaFarmaciDaAssumereCB.setItems(farmaciDaAssumere);
                    farmaciAssuntiOggiLV.setItems(farmaciAssuntiOggi);
                });

                return null;
            }

            @Override
            protected void succeeded() {
                // Nasconde l'indicatore di caricamento quando ha finito
                loadingIndicator.setVisible(false);
                loadingPage.setVisible(false);
                mainPage.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(250), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            @Override
            protected void failed() {
                loadingPage.setVisible(false);
                loadingIndicator.setVisible(false);

                System.err.println("Si è verificato un errore durante il caricamento dei dati.");
            }
        };

        new Thread(loadListePortalePaziente).start();
    }
}
