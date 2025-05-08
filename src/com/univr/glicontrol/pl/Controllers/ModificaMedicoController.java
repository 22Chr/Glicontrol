package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.pl.Models.SalvaModificheMedico;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

    public void setMedico(Medico medico) {
       this.m = medico;
       CFMedicoTF.setText(m.getCodiceFiscale());
       nomeMedicoTF.setText(m.getNome());
       cognomeMedicoTF.setText(m.getCognome());
       emailMedicoTF.setText(m.getEmail());
    }

    public void salvaMedico(ActionEvent actionEvent) {
        m.setNome(nomeMedicoTF.getText());
        m.setCognome(cognomeMedicoTF.getText());
        m.setEmail(emailMedicoTF.getText());
        m.setCodiceFiscale(CFMedicoTF.getText());
        SalvaModificheMedico smv = new SalvaModificheMedico(m);
        if (smv.medicoAggiornato(passwordMedicoTF.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setContentText("Modifica effettuata con successo!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Errore durante il salvataggio del medico.");
            alert.showAndWait();
        }
    }
}
