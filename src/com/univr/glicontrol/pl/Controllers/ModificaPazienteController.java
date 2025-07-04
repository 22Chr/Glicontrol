package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.GetListaUtenti;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.sql.Date;
import java.util.List;

public class ModificaPazienteController implements Controller {
    private Paziente p;

    @FXML
    private TextField CFPazienteTF;
    @FXML
    private TextField nomePazienteTF;
    @FXML
    private TextField cognomePazienteTF;
    @FXML
    private TextField emailPazienteTF;
    @FXML
    private TextField passwordPazienteTF;
    @FXML
    private DatePicker dataNascitaPazienteDP;
    @FXML
    private ComboBox sessoPazienteCB;
    @FXML
    private Button saveButton;
    @FXML
    private ComboBox<String> medicoRifCB;
    @FXML
    private Button eliminaPazienteB;

    private PortaleAdminController pac;
    private String defaultPassword;
    private int defaultMed;

    GetListaUtenti glpa = new GetListaUtenti();

    @FXML
    private void initialize(){
        List<String> medici = glpa.getListaMediciPortaleAdmin();
        medicoRifCB.setItems(FXCollections.observableArrayList(medici));

        sessoPazienteCB.setItems(FXCollections.observableArrayList("M", "F"));

        nomePazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue.isEmpty()) {
               nomePazienteTF.setStyle("");
           } else if (InputChecker.getInstance().verificaNome(newValue)) {
               nomePazienteTF.setStyle("-fx-border-color: #43a047");
           }  else {
               nomePazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
           }
        });

        cognomePazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                cognomePazienteTF.setStyle("");
            } else if (InputChecker.getInstance().verificaCognome(newValue)) {
               cognomePazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               cognomePazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        emailPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                emailPazienteTF.setStyle("");
            } else if (InputChecker.getInstance().verificaEmail(newValue)) {
               emailPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               emailPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        CFPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                CFPazienteTF.setStyle("");
            } else if (InputChecker.getInstance().verificaCodiceFiscale(newValue)) {
               CFPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               CFPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        passwordPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                passwordPazienteTF.setStyle("");
            } else if (InputChecker.getInstance().verificaPassword(newValue)) {
               passwordPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               passwordPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        dataNascitaPazienteDP.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                dataNascitaPazienteDP.setStyle("");
            } else if (InputChecker.getInstance().verificaNascita(Date.valueOf(newValue))) {
                dataNascitaPazienteDP.setStyle("-fx-border-color: #43a047");
            } else {
               dataNascitaPazienteDP.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });
    }

    public void setPaziente(Paziente paziente) {
        this.p = paziente;
        Medico medico = GestioneMedici.getInstance().getMedicoPerId(p.getMedicoRiferimento());
        CFPazienteTF.setText(p.getCodiceFiscale());
        nomePazienteTF.setText(p.getNome());
        cognomePazienteTF.setText(p.getCognome());
        emailPazienteTF.setText(p.getEmail());
        dataNascitaPazienteDP.setValue(p.getDataNascita().toLocalDate());
        sessoPazienteCB.setValue(p.getSesso());
        passwordPazienteTF.setText(p.getPassword());
        defaultPassword = p.getPassword();
        defaultMed = p.getMedicoRiferimento();
        medicoRifCB.setValue(medico.getCognome() + " " + medico.getNome() + " - " + medico.getCodiceFiscale());

    }

    public void setInstance(PortaleAdminController pac) {
        this.pac = pac;
    }

    public void salvaPaziente() {

        p.setNome(nomePazienteTF.getText());
        p.setCognome(cognomePazienteTF.getText());
        p.setEmail(emailPazienteTF.getText());
        p.setCodiceFiscale(CFPazienteTF.getText());
        p.setDataNascita(Date.valueOf(dataNascitaPazienteDP.getValue()));
        p.setSesso(sessoPazienteCB.getValue().toString());
        p.setPassword(passwordPazienteTF.getText());

        if (!InputChecker.getInstance().allCheckForPaziente(p.getNome(), p.getCognome(), p.getCodiceFiscale(), p.getPassword(), p.getEmail(), p.getSesso(), p.getDataNascita())) {
            Alert inputSbagliatiAlert = new Alert(Alert.AlertType.ERROR);
            inputSbagliatiAlert.setTitle("System Notification Service");
            inputSbagliatiAlert.setHeaderText("Dati mancanti");
            inputSbagliatiAlert.setContentText("Per modificare i dati di un paziente è necessario che tutti i campi siano compilati correttamente. Riprova");
            inputSbagliatiAlert.showAndWait();
        } else {

            PauseTransition pause = new PauseTransition(Duration.seconds(1));

            if (GestionePazienti.getInstance().aggiornaPaziente(p)) {
                Alert aggiornaPazienteAlert = new Alert(Alert.AlertType.INFORMATION);
                aggiornaPazienteAlert.setTitle("System Notification Service");
                aggiornaPazienteAlert.setHeaderText("Modifica eseguita con successo");
                aggiornaPazienteAlert.setContentText("I dati del paziente sono stati aggiornati correttamente");
                aggiornaPazienteAlert.showAndWait();

                pac.resetListViewPazienti();

                if (!p.getPassword().equals(defaultPassword)) {
                    pause.setOnFinished(event -> {
                        if (GestionePazienti.getInstance().inviaCredenzialiAggiornatePaziente(p.getEmail(), passwordPazienteTF.getText())) {
                            Alert notificaModifichePazienteAlert = new Alert(Alert.AlertType.INFORMATION);
                            notificaModifichePazienteAlert.setTitle("System Notification Service");
                            notificaModifichePazienteAlert.setHeaderText("Gestore credenziali");
                            notificaModifichePazienteAlert.setContentText("Le credenziali aggiornate sono state inviate con successo");
                            notificaModifichePazienteAlert.show();
                        } else {
                            Alert erroreNotificaModifichePazienteAlert = new Alert(Alert.AlertType.ERROR);
                            erroreNotificaModifichePazienteAlert.setTitle("System Notification Service");
                            erroreNotificaModifichePazienteAlert.setHeaderText("Errore invio credenziali");
                            erroreNotificaModifichePazienteAlert.setContentText("Si è verificato un errore durante l'invio delle notifiche di aggiornamento delle credenziali");
                            erroreNotificaModifichePazienteAlert.show();
                        }
                    });
                    pause.play();
                }

                // Invia all'eventuale nuovo medico di riferimento una notifica per avvisarlo circa la presa in carico del nuovo paziente
                if (defaultMed != p.getMedicoRiferimento()) {
                    pause.setOnFinished(event -> {
                        String emailMedicoRiferimento = GestioneMedici.getInstance().getMedicoPerId(p.getMedicoRiferimento()).getEmail();
                        String identificativoPaziente = p.getCognome() + " " + p.getNome() + " - " + p.getCodiceFiscale();
                        GestionePazienti.getInstance().informaMedicoAssociato(identificativoPaziente, emailMedicoRiferimento);
                    });
                }
                pause.play();

                Window currentWindow = saveButton.getScene().getWindow();
                if (currentWindow instanceof Stage) {
                    ((Stage) currentWindow).close();
                }
            } else {
                Alert erroreModificaPazienteAlert = new Alert(Alert.AlertType.ERROR);
                erroreModificaPazienteAlert.setTitle("System Notification Service");
                erroreModificaPazienteAlert.setHeaderText("Errore salvataggio");
                erroreModificaPazienteAlert.setContentText("Si è verificato un errore durante il salvataggio delle modifiche");
                erroreModificaPazienteAlert.showAndWait();
            }
        }
    }

    @FXML
    private void selezionaMedicoRiferimento() {
        String selezionato = medicoRifCB.getValue();
        p.setMedicoRiferimento(glpa.getIdMedico(selezionato));
    }

    public void eliminaPaziente(){
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("System Notification Service");
        confirmAlert.setHeaderText("Confermi di voler eliminare il paziente selezionato");
        confirmAlert.setContentText("L'operazione è irreversibile");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                if (GestionePazienti.getInstance().eliminaPaziente(p)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("System Notification Service");
                    successAlert.setHeaderText("Eliminazione avvenuta con successo");
                    successAlert.setContentText("Il paziente è stato correttamente eliminato dal sistema");
                    successAlert.showAndWait();

                    pac.resetListViewPazienti();

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event -> {
                        if (GestionePazienti.getInstance().notificaEliminazionePaziente(p.getEmail())) {
                            Alert invioNotificheEliminazioneAlert = new Alert(Alert.AlertType.INFORMATION);
                            invioNotificheEliminazioneAlert.setTitle("System Notification Service");
                            invioNotificheEliminazioneAlert.setHeaderText("Notifica inviata con successo");
                            invioNotificheEliminazioneAlert.setContentText("La notifica di eliminazione è stata inviata con successo");
                            invioNotificheEliminazioneAlert.show();
                        } else {
                            Alert erroreInvioNotificheEliminazioneAlert = new Alert(Alert.AlertType.ERROR);
                            erroreInvioNotificheEliminazioneAlert.setTitle("System Notification Service");
                            erroreInvioNotificheEliminazioneAlert.setHeaderText("Errore invio notifiche");
                            erroreInvioNotificheEliminazioneAlert.setContentText("Si è verificato un errore durante l'invio della notifica di eliminazione");
                            erroreInvioNotificheEliminazioneAlert.show();
                        }
                    });
                    pause.play();

                    Window currentWindow = eliminaPazienteB.getScene().getWindow();
                    if (currentWindow instanceof Stage) {
                        ((Stage) currentWindow).close();
                    }
                } else {
                    Alert erroreEliminazionePazienteAlert = new Alert(Alert.AlertType.ERROR);
                    erroreEliminazionePazienteAlert.setTitle("System Notification Service");
                    erroreEliminazionePazienteAlert.setHeaderText("Errore eliminazione paziente");
                    erroreEliminazionePazienteAlert.setContentText("Si è verificato un errore durante l'eliminazione del paziente selezionato");
                    erroreEliminazionePazienteAlert.showAndWait();
                }
            }
        });
    }
}
