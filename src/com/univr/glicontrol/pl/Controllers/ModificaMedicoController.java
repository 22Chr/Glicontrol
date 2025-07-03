package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.AggiornaMedico;
import com.univr.glicontrol.bll.EliminaMedico;
import com.univr.glicontrol.bll.InputChecker;
import com.univr.glicontrol.bll.Medico;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class ModificaMedicoController implements Controller {
    private Medico m;

    @FXML
    private TextField CFMedicoTF;

    @FXML
    private TextField nomeMedicoTF;

    @FXML
    private TextField cognomeMedicoTF;

    @FXML
    private TextField emailMedicoTF;

    @FXML
    private TextField passwordMedicoTF;

    @FXML
    private Button saveButton;

    @FXML
    private Button eliminaMedicoB;

    private PortaleAdminController pac;

    private String defaultPassword;

    public void setMedico(Medico medico) {

        this.m = medico;
        CFMedicoTF.setText(m.getCodiceFiscale());
        nomeMedicoTF.setText(m.getNome());
        cognomeMedicoTF.setText(m.getCognome());
        emailMedicoTF.setText(m.getEmail());
        passwordMedicoTF.setText(m.getPassword());
        defaultPassword = m.getPassword();
    }

    public void setInstance(PortaleAdminController pac) {
        this.pac = pac;
    }

    public void salvaMedico() {
        m.setNome(nomeMedicoTF.getText());
        m.setCognome(cognomeMedicoTF.getText());
        m.setEmail(emailMedicoTF.getText());
        m.setCodiceFiscale(CFMedicoTF.getText());
        m.setPassword(passwordMedicoTF.getText());

        if (!InputChecker.getInstance().allCheckForMedico(m.getNome(), m.getCognome(), m.getCodiceFiscale(), m.getPassword(), m.getEmail())) {
            Alert inputSbagliatiAlert = new Alert(Alert.AlertType.ERROR);
            inputSbagliatiAlert.setTitle("System Notification Service");
            inputSbagliatiAlert.setHeaderText("Dati mancanti");
            inputSbagliatiAlert.setContentText("Per modificare i dati di un medico è necessario che tutti i campi siano compilati correttamente. Riprova");
            inputSbagliatiAlert.showAndWait();
        } else {

            AggiornaMedico aggiornaMedico = new AggiornaMedico(m);

            if (aggiornaMedico.updateMedico()) {
                Alert modificaMedicoAlert = new Alert(Alert.AlertType.INFORMATION);
                modificaMedicoAlert.setTitle("System Notification Service");
                modificaMedicoAlert.setHeaderText("Successo modifica");
                modificaMedicoAlert.setContentText("Le modifiche sono state salvate con successo");
                modificaMedicoAlert.showAndWait();

                pac.resetListViewMedici();

                if (!m.getPassword().equals(defaultPassword)) {
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event -> {
                        if (aggiornaMedico.inviaCredenzialiAggiornateMedico(m.getEmail(), passwordMedicoTF.getText())) {
                            Alert invioNotificheModificaAlert = new Alert(Alert.AlertType.INFORMATION);
                            invioNotificheModificaAlert.setTitle("System Notification Service");
                            invioNotificheModificaAlert.setHeaderText("Gestore credenziali");
                            invioNotificheModificaAlert.setContentText("Le credenziali aggiornate sono state inviate con successo");
                            invioNotificheModificaAlert.show();
                        } else {
                            Alert erroreInvioNotificheModificaAlert = new Alert(Alert.AlertType.ERROR);
                            erroreInvioNotificheModificaAlert.setTitle("System Notification Service");
                            erroreInvioNotificheModificaAlert.setHeaderText("Errore invio credenziali");
                            erroreInvioNotificheModificaAlert.setContentText("Si è verificato un errore durante l'invio delle notifiche di aggiornamento delle credenziali");
                            erroreInvioNotificheModificaAlert.show();
                        }
                    });
                    pause.play();
                }

                Window currentWindow = saveButton.getScene().getWindow();
                if (currentWindow instanceof Stage) {
                    ((Stage) currentWindow).close();
                }
            } else {
                Alert erroreModificaMedicoAlert = new Alert(Alert.AlertType.ERROR);
                erroreModificaMedicoAlert.setTitle("System Notification Service");
                erroreModificaMedicoAlert.setHeaderText("Errore salvataggio modifiche");
                erroreModificaMedicoAlert.setContentText("Si è verificato un errore durante il salvataggio delle modifiche");
                erroreModificaMedicoAlert.showAndWait();
            }
        }
    }

    public void eliminaMedico(){
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("System Notification Service");
        confirmAlert.setHeaderText("Sei sicuro di voler eliminare questo medico");
        confirmAlert.setContentText("L'operazione è irreversibile");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                EliminaMedico em = new EliminaMedico(m);
                int status = em.deleteMedico();
                if (status == 1) {
                    Alert successEliminazioneAlert = new Alert(Alert.AlertType.INFORMATION);
                    successEliminazioneAlert.setTitle("System Notification Service");
                    successEliminazioneAlert.setHeaderText("Successo eliminazione");
                    successEliminazioneAlert.setContentText("Il medico è stato eliminato con successo");
                    successEliminazioneAlert.showAndWait();

                    pac.resetListViewMedici();

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event -> {
                        if(em.notificaEliminazioneMedico(m.getEmail())) {
                            Alert invioNotificheEliminazioneAlert = new Alert(Alert.AlertType.INFORMATION);
                            invioNotificheEliminazioneAlert.setTitle("System Notification Service");
                            invioNotificheEliminazioneAlert.setHeaderText("La notifica di eliminazione è stata inviata con successo");
                            invioNotificheEliminazioneAlert.setContentText("Il medico selezionato non potrà più accedere al sistema");
                            invioNotificheEliminazioneAlert.show();
                        } else {
                            Alert erroreInvioNotificheEliminazioneAlert = new Alert(Alert.AlertType.ERROR);
                            erroreInvioNotificheEliminazioneAlert.setTitle("System Notification Service");
                            erroreInvioNotificheEliminazioneAlert.setHeaderText("Errore durante l'invio del messaggio");
                            erroreInvioNotificheEliminazioneAlert.setContentText("Si è verificato un errore durante l'invio della notifica di eliminazione al medico");
                            erroreInvioNotificheEliminazioneAlert.show();
                        }
                    });
                    pause.play();

                    Window currentWindow = eliminaMedicoB.getScene().getWindow();
                    if (currentWindow instanceof Stage) {
                        ((Stage) currentWindow).close();
                    }
                } else if (status == 0) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("System Notification Service");
                    errorAlert.setHeaderText("Errore eliminazione medico");
                    errorAlert.setContentText("Si è verificato un errore durante l'eliminazione del medico selezionato");
                    errorAlert.showAndWait();
                } else {
                    Alert pazientiAssociatiAlMedicoAlert = new Alert(Alert.AlertType.ERROR);
                    pazientiAssociatiAlMedicoAlert.setTitle("System Notification Service");
                    pazientiAssociatiAlMedicoAlert.setHeaderText("Errore associazione referente");
                    pazientiAssociatiAlMedicoAlert.setContentText("Non è possibile eliminare un medico che abbia ancora pazienti associati.\nRimuovi tutti i pazienti associati a questo medico e riprova");
                    pazientiAssociatiAlMedicoAlert.showAndWait();
                }
            }
        });
    }

    @FXML
    private void initialize() {

        nomeMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                nomeMedicoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaNome(newValue)) {
               nomeMedicoTF.setStyle("-fx-border-color: #43a047");
            } else {
                nomeMedicoTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        cognomeMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                cognomeMedicoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaCognome(newValue)) {
                cognomeMedicoTF.setStyle("-fx-border-color: #43a047");
            } else {
                cognomeMedicoTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        emailMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue.isEmpty()) {
               emailMedicoTF.setStyle("");
           } else if (InputChecker.getInstance().verificaEmail(newValue)) {
               emailMedicoTF.setStyle("-fx-border-color: #43a047");
           } else {
               emailMedicoTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
           }
        });

        passwordMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue.isEmpty()) {
               passwordMedicoTF.setStyle("");
           } else if (InputChecker.getInstance().verificaPassword(newValue)) {
               passwordMedicoTF.setStyle("-fx-border-color: #43a047");
           } else {
               passwordMedicoTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
           }
        });

        CFMedicoTF.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue.isEmpty()) {
               CFMedicoTF.setStyle("");
           } else if (InputChecker.getInstance().verificaCodiceFiscale(newValue)) {
               CFMedicoTF.setStyle("-fx-border-color: #43a047");
           } else {
               CFMedicoTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
           }
        });
    }
}
