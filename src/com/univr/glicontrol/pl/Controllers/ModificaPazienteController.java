package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.GetListaUtenti;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.sql.Date;
import java.util.List;

public class ModificaPazienteController {
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
    private TextField dataNascitaPazienteTF;

    @FXML
    private TextField sessoPazienteTF;

    @FXML
    private Button saveButton;

    @FXML
    private ComboBox<String> medicoRifCB;

    @FXML
    private Button eliminaPazienteB;

    private PortaleAdminController pac;

    private String defaultPassword;
    private int defaultMed;

    private final InputChecker valueChecker = new InputChecker();

    GetListaUtenti glpa = new GetListaUtenti();

    @FXML
    private void initialize(){
        List<String> medici = glpa.getListaMediciPortaleAdmin();
        medicoRifCB.setItems(FXCollections.observableArrayList(medici));

        nomePazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
           if (valueChecker.verificaNome(newValue)) {
               nomePazienteTF.setStyle("-fx-border-color: #43a047");
           }  else {
               nomePazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
           }
        });

        cognomePazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaCognome(newValue)) {
               cognomePazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               cognomePazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        emailPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaEmail(newValue)) {
               emailPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               emailPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        CFPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaCodiceFiscale(newValue)) {
               CFPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               CFPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        passwordPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaPassword(newValue)) {
               passwordPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               passwordPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        dataNascitaPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaNascita(Date.valueOf(newValue))) {
                dataNascitaPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               dataNascitaPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });

        sessoPazienteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaSesso(newValue)) {
               sessoPazienteTF.setStyle("-fx-border-color: #43a047");
            } else {
               sessoPazienteTF.setStyle("-fx-border-color: #ff1744; -fx-border-width: 3px");
            }
        });
    }

    public void setPaziente(Paziente paziente) {

        this.p = paziente;
        CFPazienteTF.setText(p.getCodiceFiscale());
        nomePazienteTF.setText(p.getNome());
        cognomePazienteTF.setText(p.getCognome());
        emailPazienteTF.setText(p.getEmail());
        dataNascitaPazienteTF.setText(p.getDataNascita().toString());
        sessoPazienteTF.setText(p.getSesso());
        passwordPazienteTF.setText(p.getPassword());
        defaultPassword = p.getPassword();
        defaultMed = p.getMedicoRiferimento();

    }

    public void setInstance(PortaleAdminController pac) {
        this.pac = pac;
    }

    public void salvaPaziente() {

        p.setNome(nomePazienteTF.getText());
        p.setCognome(cognomePazienteTF.getText());
        p.setEmail(emailPazienteTF.getText());
        p.setCodiceFiscale(CFPazienteTF.getText());
        p.setDataNascita(Date.valueOf(dataNascitaPazienteTF.getText()));
        p.setSesso(sessoPazienteTF.getText());
        p.setPassword(passwordPazienteTF.getText());

        if (!valueChecker.allCheckForPaziente(p.getNome(), p.getCognome(), p.getCodiceFiscale(), p.getPassword(), p.getEmail(), p.getSesso(), p.getDataNascita())) {
            Alert inputSbagliatiAlert = new Alert(Alert.AlertType.ERROR);
            inputSbagliatiAlert.setTitle("Errore dati utente");
            inputSbagliatiAlert.setHeaderText(null);
            inputSbagliatiAlert.setContentText("Per modificare i dati di un paziente è necessario che tutti i campi siano compilati correttamente. Riprova");
            inputSbagliatiAlert.showAndWait();
        } else {

            AggiornaPaziente aggiornaPaziente = new AggiornaPaziente(p);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));

            if (aggiornaPaziente.aggiornaPaziente()) {
                Alert aggiornaPazienteAlert = new Alert(Alert.AlertType.INFORMATION);
                aggiornaPazienteAlert.setTitle("Successo");
                aggiornaPazienteAlert.setHeaderText(null);
                aggiornaPazienteAlert.setContentText("Modifica effettuata con successo");
                aggiornaPazienteAlert.showAndWait();

                pac.resetListViewPazienti();

                if (!p.getPassword().equals(defaultPassword)) {
                    pause.setOnFinished(event -> {
                        if (aggiornaPaziente.inviaCredenzialiAggiornatePaziente(p.getEmail(), passwordPazienteTF.getText())) {
                            Alert notificaModifichePazienteAlert = new Alert(Alert.AlertType.INFORMATION);
                            notificaModifichePazienteAlert.setTitle("Notification Service - Success");
                            notificaModifichePazienteAlert.setHeaderText(null);
                            notificaModifichePazienteAlert.setContentText("Le credenziali aggiornate sono state inviate con successo");
                            notificaModifichePazienteAlert.show();
                        } else {
                            Alert erroreNotificaModifichePazienteAlert = new Alert(Alert.AlertType.ERROR);
                            erroreNotificaModifichePazienteAlert.setTitle("Notification Service - Error");
                            erroreNotificaModifichePazienteAlert.setHeaderText(null);
                            erroreNotificaModifichePazienteAlert.setContentText("Si è verificato un errore durante l'invio delle notifiche di aggiornamento delle credenziali");
                            erroreNotificaModifichePazienteAlert.show();
                        }
                    });
                    pause.play();
                }

                // Invia all'eventuale nuovo medico di riferimento una notifica per avvisarlo circa la presa in carico del nuovo paziente
                if (defaultMed != p.getMedicoRiferimento()) {
                    pause.setOnFinished(event -> {
                        ListaMedici medicoRiferimento = new ListaMedici();
                        String emailMedicoRiferimento = medicoRiferimento.ottieniMedicoPerId(p.getMedicoRiferimento()).getEmail();
                        String identificativoPaziente = p.getCognome() + " " + p.getNome() + " - " + p.getCodiceFiscale();
                        InserisciPaziente inserisciPaziente = new InserisciPaziente();
                        inserisciPaziente.informaMedicoAssociato(identificativoPaziente, emailMedicoRiferimento);
                    });
                }
                pause.play();

                Window currentWindow = saveButton.getScene().getWindow();
                if (currentWindow instanceof Stage) {
                    ((Stage) currentWindow).close();
                }
            } else {
                Alert erroreModificaPazienteAlert = new Alert(Alert.AlertType.ERROR);
                erroreModificaPazienteAlert.setTitle("Errore");
                erroreModificaPazienteAlert.setHeaderText(null);
                erroreModificaPazienteAlert.setContentText("Errore durante il salvataggio delle modifiche");
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
        confirmAlert.setTitle("Conferma eliminazione");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Sei sicuro di voler eliminare questo paziente?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                EliminaPaziente ep = new EliminaPaziente(p);
                if (ep.deletePaziente()) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Successo");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Paziente eliminato con successo");
                    successAlert.showAndWait();

                    pac.resetListViewPazienti();

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event -> {
                        if (ep.notificaEliminazionePaziente(p.getEmail())) {
                            Alert invioNotificheEliminazioneAlert = new Alert(Alert.AlertType.INFORMATION);
                            invioNotificheEliminazioneAlert.setTitle("Notification Service - Success");
                            invioNotificheEliminazioneAlert.setHeaderText(null);
                            invioNotificheEliminazioneAlert.setContentText("La notifica di eliminazione è stata inviata con successo");
                            invioNotificheEliminazioneAlert.show();
                        } else {
                            Alert erroreInvioNotificheEliminazioneAlert = new Alert(Alert.AlertType.ERROR);
                            erroreInvioNotificheEliminazioneAlert.setTitle("Notification Service - Error");
                            erroreInvioNotificheEliminazioneAlert.setHeaderText(null);
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
                    erroreEliminazionePazienteAlert.setTitle("Errore");
                    erroreEliminazionePazienteAlert.setHeaderText(null);
                    erroreEliminazionePazienteAlert.setContentText("Errore durante l'eliminazione del paziente");
                    erroreEliminazionePazienteAlert.showAndWait();
                }
            }
        });
    }

}
