package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.AggiornaPaziente;
import com.univr.glicontrol.bll.EliminaMedico;
import com.univr.glicontrol.bll.EliminaPaziente;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.GetListaPortaleAdmin;
import com.univr.glicontrol.pl.Models.SalvaModifichePaziente;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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

    @FXML
    private Button eliminaPazienteB;

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

            AggiornaPaziente aggiornaPaziente = new AggiornaPaziente(p);
            if(aggiornaPaziente.inviaCredenzialiAggiornatePaziente(p.getEmail(), passwordPazienteTF.getText())){
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Successo");
                alert2.setHeaderText(null);
                alert2.setContentText("Invio delle credenziali aggiornate al paziente avvenuto con successo!");
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
            alert.setContentText("Errore durante il salvataggio del paziente.");
            alert.showAndWait();
        }
    }

    @FXML
    private void selezionaMedicoRiferimento() {
        String selezionato = medicoRifCB.getValue();
        p.setMedicoRiferimento(glpa.getIdMedico(selezionato));
    }

    public void eliminaPaziente(ActionEvent event){
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
                    successAlert.setContentText("Paziente eliminato con successo.");
                    successAlert.showAndWait();

                    pac.resetListViewPazienti();

                    Window currentWindow = eliminaPazienteB.getScene().getWindow();
                    if (currentWindow instanceof Stage) {
                        ((Stage) currentWindow).close();
                    }
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Errore");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Errore durante l'eliminazione del paziente.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
}
