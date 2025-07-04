package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestioneMedici;
import com.univr.glicontrol.bll.InputChecker;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class InserisciMedicoController implements Controller {

    @FXML
    private TextField CFNuovoMedicoTF;

    @FXML
    private TextField nomeNuovoMedicoTF;

    @FXML
    private TextField cognomeNuovoMedicoTF;

    @FXML
    private TextField emailNuovoMedicoTF;

    @FXML
    private TextField passwordNuovoMedicoTF;

    @FXML
    private Button saveNuovoMedicoB;

    private PortaleAdminController pac;


    public void setInstance(PortaleAdminController pac) {
        this.pac = pac;
    }

    public void salvaNuovoMedico() {

        String nome = nomeNuovoMedicoTF.getText();
        String cognome = cognomeNuovoMedicoTF.getText();
        String email = emailNuovoMedicoTF.getText();
        String CF = CFNuovoMedicoTF.getText();
        String password = passwordNuovoMedicoTF.getText();

        if (!InputChecker.getInstance().allCheckForMedico(nome, cognome, CF, password, email)) {

            Alert inputSbagliatiAlert = new Alert(Alert.AlertType.ERROR);
            inputSbagliatiAlert.setTitle("Errore dati utente");
            inputSbagliatiAlert.setHeaderText(null);
            inputSbagliatiAlert.setContentText("Per inserire un nuovo medico è necessario che tutti i campi siano compilati correttamente. Riprova");
            inputSbagliatiAlert.showAndWait();

        } else {

            int success = GestioneMedici.getInstance().inserisciMedico(CF, nome, cognome, email, password);

            if (success == 1) {
                Alert inserimentoMedicoAlert = new Alert(Alert.AlertType.INFORMATION);
                inserimentoMedicoAlert.setTitle("Successo");
                inserimentoMedicoAlert.setHeaderText(null);
                inserimentoMedicoAlert.setContentText("Inserimento effettuato con successo");
                inserimentoMedicoAlert.showAndWait();

                // Ricarica la lista dei medici nel controller principale
                pac.resetListViewMedici();

                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    if (GestioneMedici.getInstance().inviaCredenzialiMedico(email, password)) {
                        Alert notificaInserimentoMedicoAlert = new Alert(Alert.AlertType.INFORMATION);
                        notificaInserimentoMedicoAlert.setTitle("System Notification Service");
                        notificaInserimentoMedicoAlert.setHeaderText("Gestore credenziali");
                        notificaInserimentoMedicoAlert.setContentText("Le credenziali sono state inviate al nuovo medico");
                        notificaInserimentoMedicoAlert.show();
                    } else {
                        Alert erroreInserimentoMedicoAlert = new Alert(Alert.AlertType.ERROR);
                        erroreInserimentoMedicoAlert.setTitle("System Notification Service");
                        erroreInserimentoMedicoAlert.setHeaderText("Errore invio credenziali");
                        erroreInserimentoMedicoAlert.setContentText("Si è verificato un errore durante l'invio delle credenziali al nuovo medico");
                        erroreInserimentoMedicoAlert.show();
                    }
                });
                pause.play();

                // Chiudi la finestra di inserimento
                Window currentWindow = saveNuovoMedicoB.getScene().getWindow();
                if (currentWindow instanceof Stage) {
                    ((Stage) currentWindow).close();
                }

            } else if (success == 0) {
                Alert erroreInserimentoMedicoAlert = new Alert(Alert.AlertType.ERROR);
                erroreInserimentoMedicoAlert.setTitle("System Notification Service");
                erroreInserimentoMedicoAlert.setHeaderText("Errore inserimento");
                erroreInserimentoMedicoAlert.setContentText("Si è verificato un errore durante l'inserimento del nuovo medico");
                erroreInserimentoMedicoAlert.showAndWait();
            } else {
                Alert medicoEsistenteAlert = new Alert(Alert.AlertType.ERROR);
                medicoEsistenteAlert.setTitle("System Notification Service");
                medicoEsistenteAlert.setHeaderText("Gestione duplicati");
                medicoEsistenteAlert.setContentText("Il medico che stai cercando di inserire è già presente nel sistema");
                medicoEsistenteAlert.showAndWait();
            }
        }
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> saveNuovoMedicoB.requestFocus());
        saveNuovoMedicoB.setDefaultButton(true);


        nomeNuovoMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                nomeNuovoMedicoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaNome(newValue))
                nomeNuovoMedicoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert nomeNuovoMedicoTF != null;
                nomeNuovoMedicoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        cognomeNuovoMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if  (newValue.isEmpty()) {
                cognomeNuovoMedicoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaCognome(newValue))
                cognomeNuovoMedicoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert cognomeNuovoMedicoTF != null;
                cognomeNuovoMedicoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        emailNuovoMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                emailNuovoMedicoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaEmail(newValue))
                emailNuovoMedicoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert emailNuovoMedicoTF != null;
                emailNuovoMedicoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        CFNuovoMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                CFNuovoMedicoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaCodiceFiscale(newValue))
                CFNuovoMedicoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert CFNuovoMedicoTF != null;
                CFNuovoMedicoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        passwordNuovoMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
               passwordNuovoMedicoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaPassword(newValue))
                passwordNuovoMedicoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert passwordNuovoMedicoTF != null;
                passwordNuovoMedicoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });
    }
}
