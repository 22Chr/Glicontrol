package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.pl.Models.SalvaModificheMedico;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

public class InserisciMedicoController {
    private Medico me;

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
        me = new Medico(me.getIdUtente(), CF, nome, cognome, "MEDICO", email);
        SalvaModificheMedico smv = new SalvaModificheMedico(me);
        if (smv.medicoAggiornato(passwordNuovoMedicoTF.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Inserimento effettuato con successo!");
            alert.showAndWait();

            pac.resetListViewMedici();

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
