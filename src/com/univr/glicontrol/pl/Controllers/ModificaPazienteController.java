package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.GetListaPortaleAdmin;
import com.univr.glicontrol.pl.Models.SalvaModifichePaziente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

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

    private PortaleAdminController pac;

    GetListaPortaleAdmin glpa = new GetListaPortaleAdmin();
    @FXML
    private void initialize(){
        List<String> medici = glpa.getListaMediciPortaleAdmin(); // ad es. "Mario Rossi - CF1234"
        medicoRifCB.setItems(FXCollections.observableArrayList(medici));
    }

    public void setPaziente(Paziente paziente) {
        this.p = paziente;
        CFPazienteTF.setText(p.getCodiceFiscale());
        nomePazienteTF.setText(p.getNome());
        cognomePazienteTF.setText(p.getCognome());
        emailPazienteTF.setText(p.getEmail());
        dataNascitaPazienteTF.setText(p.getDataNascita().toString());
        sessoPazienteTF.setText(p.getSesso());
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

        SalvaModifichePaziente smv = new SalvaModifichePaziente(p);
        if (smv.pazienteAggiornato(passwordPazienteTF.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Modifica effettuata con successo!");
            alert.showAndWait();

            pac.resetListViewPazienti();

            Window currentWindow = saveButton.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Errore durante il salvataggio del paziente.");
            alert.showAndWait();
        }
    }

    @FXML
    private void selezionaMedicoRiferimento() {
        String selezionato = medicoRifCB.getValue();
        p.setMedicoRiferimento(glpa.getIdMedico(selezionato));
    }
}
