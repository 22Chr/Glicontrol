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
    private TextField dataNascitaNuovoPazienteTF;

    @FXML
    private TextField sessoNuovoPazienteTF;


    @FXML
    private Button saveNuovoPazienteB;

    @FXML
    private ComboBox<String> medicoRifNuovoPazCB;

    private PortaleAdminController pac;

    GetListaPortaleAdmin glpa = new GetListaPortaleAdmin();
    @FXML
    private void initialize(){
        List<String> medici = glpa.getListaMediciPortaleAdmin(); // ad es. "Mario Rossi - CF1234"
        medicoRifNuovoPazCB.setItems(FXCollections.observableArrayList(medici));
    }

    public void setInstance(PortaleAdminController pac) {
        this.pac = pac;
    }

    public void salvaNuovoPaziente() {

        pa.setNome(nomeNuovoPazienteTF.getText());
        pa.setCognome(cognomeNuovoPazienteTF.getText());
        pa.setEmail(emailNuovoPazienteTF.getText());
        pa.setCodiceFiscale(CFNuovoPazienteTF.getText());
        pa.setDataNascita(Date.valueOf(dataNascitaNuovoPazienteTF.getText()));
        pa.setSesso(sessoNuovoPazienteTF.getText());

        SalvaModifichePaziente smv = new SalvaModifichePaziente(pa);
        if (smv.pazienteAggiornato(passwordNuovoPazienteTF.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Inserimento effettuato con successo!");
            alert.showAndWait();

            pac.resetListViewPazienti();

            Window currentWindow = saveNuovoPazienteB.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Errore durante l'inserimento del paziente.");
            alert.showAndWait();
        }
    }

    @FXML
    private void selezionaMedicoRiferimentoNuovo() {
        String selezionato = medicoRifNuovoPazCB.getValue();
        pa.setMedicoRiferimento(glpa.getIdMedico(selezionato));
    }
}
