package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.InserisciMedico;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.pl.Models.SalvaModificheMedico;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

public class InserisciMedicoController {

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

        InserisciMedico inserisciMedico = new InserisciMedico();

        boolean success = inserisciMedico.insertMedico(CF, nome, cognome, email, password);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Inserimento effettuato con successo!");
            alert.showAndWait();

            // Ricarica la lista dei medici nel controller principale
            pac.resetListViewMedici();

            // Chiudi la finestra di inserimento
            Window currentWindow = saveNuovoMedicoB.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Errore durante l'inserimento del medico.");
            alert.showAndWait();
        }
    }
}
