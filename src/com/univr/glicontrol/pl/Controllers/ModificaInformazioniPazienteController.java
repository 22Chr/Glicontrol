package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ModificaInformazioniPazienteController {

    @FXML
    private CheckBox fumatoreCB, alcolismoCB, familiaritaCB, sedentarietaCB, alimentazioneCB;

    @FXML
    private TextField nomeTF, cognomeTF, codFisTF, emailTF, pesoTF;

    @FXML
    private TextArea allergieTA;

    @FXML
    private Button salvaModifiche;

    private Paziente p;

    private final GestioneFattoriRischio gestioneFattoriRischio = new GestioneFattoriRischio();
    Paziente paziente = new UtilityPortalePaziente().getPazienteSessione();
    private final FattoriRischio fattoriRischioAggiornati = gestioneFattoriRischio.getFattoriRischio(paziente.getIdUtente());
    private final InputChecker valueChecker = new InputChecker();

    @FXML
    private void initialize() {
        salvaModifiche.requestFocus();

        //inizializzazione delle checkbox al loro stato attuale
        int fumatore = fattoriRischioAggiornati.getFumatore();
        int alcolismo = fattoriRischioAggiornati.getProblemiAlcol();
        int familiarita = fattoriRischioAggiornati.getFamiliarita();
        int sedentarieta = fattoriRischioAggiornati.getSedentarieta();
        int alimentazione = fattoriRischioAggiornati.getAlimentazioneScorretta();

        if (fumatore == 1) {
            fumatoreCB.setSelected(true);
        }
        if (alcolismo == 1) {
            alcolismoCB.setSelected(true);
        }
        if (familiarita == 1) {
            familiaritaCB.setSelected(true);
        }
        if (sedentarieta == 1) {
            sedentarietaCB.setSelected(true);
        }
        if (alimentazione == 1) {
            alimentazioneCB.setSelected(true);
        }

        //inizializza le informazioni del paziente
        this.p = paziente;
        nomeTF.setText(p.getNome());
        nomeTF.setEditable(false); //rende immodificabile il campo
        cognomeTF.setText(p.getCognome());
        cognomeTF.setEditable(false);
        codFisTF.setText(p.getCodiceFiscale());
        codFisTF.setEditable(false);
        emailTF.setText(p.getEmail());
        pesoTF.setText(p.getPeso() + " kg");
        allergieTA.setText(p.getAllergie());


        // Verifica attiva dei campi
        emailTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaEmail(newValue) && emailTF != null)
                emailTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert emailTF != null;
                emailTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        pesoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (valueChecker.verificaPeso(newValue) && pesoTF != null)
                pesoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert pesoTF != null;
                pesoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });
    }

    public void salvaModificheInformazioni() {

        if (valueChecker.verificaPeso(pesoTF.getText()) && valueChecker.verificaEmail(emailTF.getText())) {
            p.setEmail(emailTF.getText());
            p.setPeso(Float.parseFloat(pesoTF.getText().substring(0, pesoTF.getText().length() - 3)));
        } else {
            Alert inputSbagliatiAlert = new Alert(Alert.AlertType.ERROR);
            inputSbagliatiAlert.setTitle("System Information Service");
            inputSbagliatiAlert.setHeaderText(null);
            inputSbagliatiAlert.setContentText("Per modificare le informazioni del paziente è necessario che tutti i campi siano compilati correttamente.\nVerifica peso e email e riprova");
            inputSbagliatiAlert.showAndWait();
            return;
        }

        p.setAllergie(allergieTA.getText());
        fattoriRischioAggiornati.setFumatore(fumatoreCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setProblemiAlcol(alcolismoCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setFamiliarita(familiaritaCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setObesita(sedentarietaCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setAlimentazioneScorretta(alimentazioneCB.isSelected() ? 1 : 0);

        AggiornaPaziente aggiornaPaziente = new AggiornaPaziente(p);

        if (aggiornaPaziente.aggiornaPaziente() && gestioneFattoriRischio.aggiornaFattoriRischio(fattoriRischioAggiornati)) {
            Alert aggiornaPazienteAlert = new Alert(Alert.AlertType.INFORMATION);
            aggiornaPazienteAlert.setTitle("System Information Service");
            aggiornaPazienteAlert.setHeaderText(null);
            aggiornaPazienteAlert.setContentText("I tuoi dati sono stati aggiornati correttamente");
            aggiornaPazienteAlert.showAndWait();

            Window currentWindow = salvaModifiche.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        } else {
            Alert erroreModificaPazienteAlert = new Alert(Alert.AlertType.ERROR);
            erroreModificaPazienteAlert.setTitle("System Information Service");
            erroreModificaPazienteAlert.setHeaderText(null);
            erroreModificaPazienteAlert.setContentText("Si è verificato un errore durante il salvataggio delle nuove informazioni.\nVerifica che tutti i dati siano corretti e riprova");
            erroreModificaPazienteAlert.showAndWait();
        }
    }
}
