package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.InserisciMedico;
import com.univr.glicontrol.bll.InserisciPaziente;
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

    private int id;

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

        String nome = nomeNuovoPazienteTF.getText();
        String cognome = cognomeNuovoPazienteTF.getText();
        String email = emailNuovoPazienteTF.getText();
        String CF = CFNuovoPazienteTF.getText();
        Date dataNascita = Date.valueOf(dataNascitaNuovoPazienteTF.getText());
        String sesso = sessoNuovoPazienteTF.getText();
        String password = passwordNuovoPazienteTF.getText();

        InserisciPaziente inserisciPaziente = new InserisciPaziente();

        boolean success = inserisciPaziente.insertPaziente(CF, nome, cognome, password, id, dataNascita, sesso, email, "", 0.0);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Inserimento effettuato con successo!");
            alert.showAndWait();

            // Ricarica la lista dei medici nel controller principale
            pac.resetListViewPazienti();

            if (inserisciPaziente.inviaCredenzialiPaziente(email, password)) {
                // Invia le credenziali al server
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Successo");
                alert2.setHeaderText(null);
                alert2.setContentText("Invio delle credenziali al server avvenuto con successo!");
                alert2.showAndWait();
            } else {
                // Invia le credenziali al server
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
            }

            // Chiudi la finestra di inserimento
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
        id = glpa.getIdMedico(selezionato);
    }
}
