package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.GetListaUtenti;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.sql.Date;
import java.util.List;

public class InserisciPazienteController {
    private Paziente pa;

    @FXML
    private TextField CFNuovoPazienteTF;

    @FXML
    private TextField nomeNuovoPazienteTF;

    @FXML
    private TextField cognomeNuovoPazienteTF;

    @FXML
    private TextField emailNuovoPazienteTF;

    @FXML
    private TextField passwordNuovoPazienteTF;

    @FXML
    private DatePicker dataNascitaNuovoPazienteDP;

    @FXML
    private ComboBox<String> sessoNuovoPazienteCB;

    @FXML
    private Button saveNuovoPazienteB;

    @FXML
    private ComboBox<String> medicoRifNuovoPazCB;

    private PortaleAdminController pac;

    private final InputChecker valueChecker = new InputChecker();

    private int id;

    //carica la lista di medici nel controller p
    GetListaUtenti glpa = new GetListaUtenti();
    @FXML
    private void initialize(){
        Platform.runLater(() -> saveNuovoPazienteB.requestFocus());
        saveNuovoPazienteB.setDefaultButton(true);

        List<String> medici = glpa.getListaMediciPortaleAdmin(); // ad es. "Mario Rossi - CF1234"
        medicoRifNuovoPazCB.setItems(FXCollections.observableArrayList(medici));

        sessoNuovoPazienteCB.setItems(FXCollections.observableArrayList("M", "F"));

        nomeNuovoPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
           if (valueChecker.verificaNome(newValue) && nomeNuovoPazienteTF != null) {
               nomeNuovoPazienteTF.setStyle("-fx-border-color: #43a047");
           }  else {
               assert nomeNuovoPazienteTF != null;
               nomeNuovoPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
           }
        });

        cognomeNuovoPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaCognome(newValue) && cognomeNuovoPazienteTF != null) {
               cognomeNuovoPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
                assert cognomeNuovoPazienteTF != null;
               cognomeNuovoPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        emailNuovoPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaEmail(newValue) && emailNuovoPazienteTF != null) {
               emailNuovoPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
                assert emailNuovoPazienteTF != null;
                emailNuovoPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        CFNuovoPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaCodiceFiscale(newValue) && CFNuovoPazienteTF != null) {
               CFNuovoPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
                assert CFNuovoPazienteTF != null;
                CFNuovoPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        passwordNuovoPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaPassword(newValue) && passwordNuovoPazienteTF != null) {
               passwordNuovoPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
                assert passwordNuovoPazienteTF != null;
                passwordNuovoPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        dataNascitaNuovoPazienteDP.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaNascita(Date.valueOf(newValue)) && dataNascitaNuovoPazienteDP != null) {
                dataNascitaNuovoPazienteDP.setStyle("-fx-border-color: #43a047");
            } else {
                assert dataNascitaNuovoPazienteDP != null;
                dataNascitaNuovoPazienteDP.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });
    }

    public void setInstance(PortaleAdminController pac) {
        this.pac = pac;
    }

    public void salvaNuovoPaziente() {

        String nome = nomeNuovoPazienteTF.getText();
        String cognome = cognomeNuovoPazienteTF.getText();
        String email = emailNuovoPazienteTF.getText();
        String CF = CFNuovoPazienteTF.getText();
        Date dataNascita = Date.valueOf(dataNascitaNuovoPazienteDP.getValue());
        String sesso = sessoNuovoPazienteCB.getValue();
        String password = passwordNuovoPazienteTF.getText();

        if (!valueChecker.allCheckForPaziente(nome, cognome, CF, password, email, sesso, dataNascita)) {
            Alert inputPazienteSbagliati = new Alert(Alert.AlertType.ERROR);
            inputPazienteSbagliati.setTitle("System Information Service");
            inputPazienteSbagliati.setHeaderText(null);
            inputPazienteSbagliati.setContentText("Per inserire un nuovo paziente è necessario che tutti i campi siano compilati correttamente. Riprova");
            inputPazienteSbagliati.showAndWait();
        } else {

            InserisciPaziente inserisciPaziente = new InserisciPaziente();
            PauseTransition pause = new PauseTransition(Duration.seconds(1));

            int success = inserisciPaziente.insertPaziente(CF, nome, cognome, password, id, dataNascita, sesso, email, null, 1.0f);

            if (success == 1) {
                Alert inserimentoPazienteAlert = new Alert(Alert.AlertType.INFORMATION);
                inserimentoPazienteAlert.setTitle("System Information Service");
                inserimentoPazienteAlert.setHeaderText(null);
                inserimentoPazienteAlert.setContentText("Inserimento effettuato con successo");
                inserimentoPazienteAlert.showAndWait();

                // Ricarica la lista dei medici nel controller principale
                pac.resetListViewPazienti();
                PauseTransition pauseInvioNotificaPaziente = new PauseTransition(Duration.seconds(1));

                pauseInvioNotificaPaziente.setOnFinished(event -> {
                    if (inserisciPaziente.inviaCredenzialiPaziente(email, password)) {

                        Alert notificaInserimentoPazienteAlert = new Alert(Alert.AlertType.INFORMATION);
                        notificaInserimentoPazienteAlert.setTitle("System Information Service");
                        notificaInserimentoPazienteAlert.setHeaderText(null);
                        notificaInserimentoPazienteAlert.setContentText("Invio delle credenziali al paziente avvenuto con successo");
                        notificaInserimentoPazienteAlert.show();

                    } else {

                        Alert erroreNotificaInserimentoPazienteAlert = new Alert(Alert.AlertType.ERROR);
                        erroreNotificaInserimentoPazienteAlert.setTitle("System Information Service");
                        erroreNotificaInserimentoPazienteAlert.setHeaderText(null);
                        erroreNotificaInserimentoPazienteAlert.setContentText("Si è verificato un errore durante l'invio delle credenziali al paziente");
                        erroreNotificaInserimentoPazienteAlert.show();

                    }
                });
                pauseInvioNotificaPaziente.play();

                // Chiudi la finestra di inserimento
                Window currentWindow = saveNuovoPazienteB.getScene().getWindow();
                if (currentWindow instanceof Stage) {
                    ((Stage) currentWindow).close();
                }

                pause.setOnFinished(event -> {
                    // Avvisa il medico di riferimento circa la presa in carico di un nuovo paziente
                    ListaMedici medicoRiferimento = new ListaMedici();
                    String emailMedicoRiferimento = medicoRiferimento.ottieniMedicoPerId(id).getEmail();
                    String identificativoPaziente = cognome + " " + nome + " - " + CF;
                    inserisciPaziente.informaMedicoAssociato(identificativoPaziente, emailMedicoRiferimento);
                });
                pause.play();

            } else if (success == 0) {

                Alert erroreInserimentoPazienteAlert = new Alert(Alert.AlertType.ERROR);
                erroreInserimentoPazienteAlert.setTitle("System Information Service");
                erroreInserimentoPazienteAlert.setHeaderText(null);
                erroreInserimentoPazienteAlert.setContentText("Errore durante l'inserimento del paziente");
                erroreInserimentoPazienteAlert.showAndWait();

            } else {

                Alert pazienteEsistenteAlert = new Alert(Alert.AlertType.ERROR);
                pazienteEsistenteAlert.setTitle("System Information Service");
                pazienteEsistenteAlert.setHeaderText(null);
                pazienteEsistenteAlert.setContentText("Il paziente che stai cercando di inserire è già presente nel sistema");
                pazienteEsistenteAlert.showAndWait();

            }
        }
    }

    @FXML
    private void selezionaMedicoRiferimentoNuovo() {

        String selezionato = medicoRifNuovoPazCB.getValue();
        id = glpa.getIdMedico(selezionato);

    }
}
