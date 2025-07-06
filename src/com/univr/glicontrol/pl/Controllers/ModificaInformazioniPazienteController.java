package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ModificaInformazioniPazienteController implements InserimentoPastiController, Controller {

    @FXML
    private CheckBox fumatoreCB, alcolismoCB, familiaritaCB, sedentarietaCB, alimentazioneCB, obesitaCB;

    @FXML
    private TextField nomeTF, cognomeTF, codFisTF, emailTF, pesoTF, altezzaTF;

    @FXML
    private TextArea allergieTA;

    @FXML
    private Button salvaModifiche;

    @FXML
    private ListView<String> pastiLV;

    @FXML
    private Button aggiungiPastoB;

    private Paziente paziente;
    private Medico medico = null;
    private final GestioneFattoriRischio gestioneFattoriRischio = new GestioneFattoriRischio();
    private FattoriRischio fattoriRischioAggiornati;
    private UtilityPortali up;
    String pastoDaModificare;
    private PortalePazienteController ppc = null;
    private PortaleMedicoController pmc = null;
    private boolean inseriteDalMedico = false;

    @FXML
    private void initialize() {
        salvaModifiche.requestFocus();

        // Verifica attiva dei campi
        emailTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                emailTF.setStyle("");
            } else if (InputChecker.getInstance().verificaEmail(newValue))
                emailTF.setStyle("-fx-border-color: #43a047;");
            else {
                emailTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        pesoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                pesoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaPeso(newValue))
                pesoTF.setStyle("-fx-border-color: #43a047;");
            else {
                pesoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        altezzaTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                altezzaTF.setStyle("");
            } else if (InputChecker.getInstance().verificaAltezza(newValue)) {
                altezzaTF.setStyle("-fx-border-color: #43a047;");
            }  else {
                altezzaTF.setStyle("-fx-border-color: #ff0000;  -fx-border-width: 3px;");
            }
        });
    }

    private void modificaPasto(String pastoDaModificare) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/ModificaPasto.fxml"));
            Parent root = loader.load();

            // Ottieni il controller associato
            ModificaPastoController controller = loader.getController();
            controller.setInstance(this, pastoDaModificare); // Passa il dato al controller

            // Crea e mostra la nuova scena/finestra
            Stage stage = new Stage();
            stage.setTitle("Modifica il pasto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocca finestra principale
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aggiungiPasto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/InserisciNuovoPasto.fxml"));
            Parent root = loader.load();

            InserisciPastoController ipc = loader.getController();
            ipc.setInstance(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi un nuovo pasto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocca finestra principale
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void salvaModificheInformazioni() {

        if (InputChecker.getInstance().verificaPeso(pesoTF.getText()) && InputChecker.getInstance().verificaEmail(emailTF.getText())) {
            paziente.setEmail(emailTF.getText());
            paziente.setAltezza(Integer.parseInt(altezzaTF.getText().substring(0, altezzaTF.getText().length() - 3)));
            paziente.setPeso(Float.parseFloat(pesoTF.getText().substring(0, pesoTF.getText().length() - 3)));
        } else {
            Alert inputSbagliatiAlert = new Alert(Alert.AlertType.ERROR);
            inputSbagliatiAlert.setTitle("System Notification Service");
            inputSbagliatiAlert.setHeaderText("Dati mancanti");
            inputSbagliatiAlert.setContentText("Per modificare le informazioni del paziente è necessario che tutti i campi siano compilati correttamente.\nVerifica peso e email e riprova");
            inputSbagliatiAlert.showAndWait();
            return;
        }

        paziente.setAllergie(allergieTA.getText());
        fattoriRischioAggiornati.setFumatore(fumatoreCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setProblemiAlcol(alcolismoCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setFamiliarita(familiaritaCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setSedentarieta(sedentarietaCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setAlimentazioneScorretta(alimentazioneCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setObesita(GlicontrolCoreSystem.getInstance().isObeso(paziente));

        if (GestionePazienti.getInstance().aggiornaPaziente(paziente) && gestioneFattoriRischio.aggiornaFattoriRischio(fattoriRischioAggiornati)) {
            Alert aggiornaPazienteAlert = new Alert(Alert.AlertType.INFORMATION);
            aggiornaPazienteAlert.setTitle("System Notification Service");
            aggiornaPazienteAlert.setHeaderText("Salvataggio avvenuto con successo");
            aggiornaPazienteAlert.setContentText("I tuoi dati sono stati aggiornati correttamente");
            aggiornaPazienteAlert.showAndWait();

            GlicontrolCoreSystem.getInstance().generaLog(Log.INFO_PAZIENTE, paziente, medico, false, inseriteDalMedico);

            Window currentWindow = salvaModifiche.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert erroreModificaPazienteAlert = new Alert(Alert.AlertType.ERROR);
            erroreModificaPazienteAlert.setTitle("System Notification Service");
            erroreModificaPazienteAlert.setHeaderText("Errore salvataggio");
            erroreModificaPazienteAlert.setContentText("Si è verificato un errore durante il salvataggio delle nuove informazioni.\nVerifica che tutti i dati siano corretti e riprova");
            erroreModificaPazienteAlert.showAndWait();
        }
    }

    public void resetListViewPasti() {
        UtilityPortali newUp = new UtilityPortali();
        ObservableList<String> newPasti = FXCollections.observableArrayList();
        newPasti.addAll(newUp.getListaPasti());
        pastiLV.setItems(newPasti);
    }

    public void setInstance(Portale portale, Paziente paziente) {
        this.paziente = paziente;
        fattoriRischioAggiornati = gestioneFattoriRischio.getFattoriRischio(paziente.getIdUtente());
        up = new UtilityPortali(paziente);

        if (portale instanceof PortalePazienteController) {
            this.ppc = (PortalePazienteController) portale;
            medico = GestioneMedici.getInstance().getMedicoPerId(paziente.getMedicoRiferimento());
        } else {
            this.pmc = (PortaleMedicoController)  portale;
            medico = new UtilityPortali().getMedicoSessione();
            inseriteDalMedico = true;
        }

        Platform.runLater(this::caricaInfoPaziente);
    }

    private void caricaInfoPaziente() {
        Task<Void> loadInfoTask = new Task<>() {
            @Override
            protected Void call() {
                if (pmc != null) {
                    aggiungiPastoB.setVisible(false);
                }

                //inizializzazione delle checkbox al loro stato attuale
                int fumatore = fattoriRischioAggiornati.getFumatore();
                int alcolismo = fattoriRischioAggiornati.getProblemiAlcol();
                int familiarita = fattoriRischioAggiornati.getFamiliarita();
                int sedentarieta = fattoriRischioAggiornati.getSedentarieta();
                int alimentazione = fattoriRischioAggiornati.getAlimentazioneScorretta();
                fattoriRischioAggiornati.setObesita(GlicontrolCoreSystem.getInstance().isObeso(paziente));
                boolean obesita =  fattoriRischioAggiornati.getObesita();

                if (fumatore == 1) {
                    fumatoreCB.setSelected(true);
                }
                if (alcolismo == 1) {
                    alcolismoCB.setSelected(true);
                }
                if (familiarita == 1) {
                    familiaritaCB.setSelected(true);
                }
                if (sedentarieta == 1) {
                    sedentarietaCB.setSelected(true);
                }
                if (alimentazione == 1) {
                    alimentazioneCB.setSelected(true);
                }
                obesitaCB.setSelected(obesita);

                //inizializza le informazioni del paziente
                nomeTF.setText(paziente.getNome());
                nomeTF.setEditable(false); //rende immodificabile il campo
                cognomeTF.setText(paziente.getCognome());
                cognomeTF.setEditable(false);
                codFisTF.setText(paziente.getCodiceFiscale());
                codFisTF.setEditable(false);
                emailTF.setText(paziente.getEmail());
                pesoTF.setText(paziente.getPeso() + " kg");
                allergieTA.setText(paziente.getAllergie());
                altezzaTF.setText(paziente.getAltezza() + " cm");

                ObservableList<String> pasti = FXCollections.observableArrayList();
                pasti.addAll(up.getListaPasti());
                pastiLV.setItems(pasti);


                if (ppc != null) {
                    pastiLV.setCellFactory(lv -> {
                        ListCell<String> cell = new ListCell<>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? null : item);
                            }
                        };

                        cell.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2 && !cell.isEmpty()) {
                                pastoDaModificare = cell.getItem();
                                modificaPasto(pastoDaModificare);
                            }
                        });

                        return cell;
                    });
                } else {
                    pastiLV.setMouseTransparent(true);
                    pastiLV.setFocusTraversable(false);
                }

                return null;
            }
        };

        new Thread(loadInfoTask).start();
    }
}
