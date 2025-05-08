package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.AggiornaMedico;
import com.univr.glicontrol.bll.AggiornaPaziente;
import com.univr.glicontrol.bll.EliminaMedico;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.pl.Models.SalvaModificheMedico;
import jakarta.mail.MessagingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ModificaMedicoController {
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

    public void setMedico(Medico medico) {
       this.m = medico;
       CFMedicoTF.setText(m.getCodiceFiscale());
       nomeMedicoTF.setText(m.getNome());
       cognomeMedicoTF.setText(m.getCognome());
       emailMedicoTF.setText(m.getEmail());
    }

    public void setInstance(PortaleAdminController pac) {
        this.pac = pac;
    }

    public void salvaMedico() throws MessagingException {
        m.setNome(nomeMedicoTF.getText());
        m.setCognome(cognomeMedicoTF.getText());
        m.setEmail(emailMedicoTF.getText());
        m.setCodiceFiscale(CFMedicoTF.getText());

        SalvaModificheMedico smv = new SalvaModificheMedico(m);

        if (smv.medicoAggiornato(passwordMedicoTF.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Modifica effettuata con successo!");
            alert.showAndWait();

            pac.resetListViewMedici();

            AggiornaMedico aggiornaMedico = new AggiornaMedico(m);
            if(aggiornaMedico.inviaCredenzialiAggiornateMedico(m.getEmail(), passwordMedicoTF.getText())){
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Successo");
                alert2.setHeaderText(null);
                alert2.setContentText("Invio delle aggiornate credenziali al medico avvenuto con successo!");
                alert2.showAndWait();
            } else {
                // Invia le credenziali al server
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
            }

            Window currentWindow = saveButton.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Errore durante il salvataggio del medico.");
            alert.showAndWait();
        }
    }

    public void eliminaMedico(ActionEvent event){
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma eliminazione");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Sei sicuro di voler eliminare questo medico?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                EliminaMedico em = new EliminaMedico(m);
                if (em.deleteMedico()) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Successo");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Medico eliminato con successo.");
                    successAlert.showAndWait();

                    pac.resetListViewMedici();

                    Window currentWindow = eliminaMedicoB.getScene().getWindow();
                    if (currentWindow instanceof Stage) {
                        ((Stage) currentWindow).close();
                    }
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Errore");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Errore durante l'eliminazione del medico.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
}
