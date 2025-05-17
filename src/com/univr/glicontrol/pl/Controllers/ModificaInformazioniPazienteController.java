package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.AggiornaPaziente;
import com.univr.glicontrol.bll.FattoriRischio;
import com.univr.glicontrol.bll.GestioneFattoriRischio;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

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

    private GestioneFattoriRischio gestioneFattoriRischio = new GestioneFattoriRischio();
    Paziente paziente = new UtilityPortalePaziente().getPazienteSessione();
    private FattoriRischio fattoriRischioAggiornati = gestioneFattoriRischio.getFattoriRischio(paziente.getIdUtente());

    @FXML
    private void initialize() {

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
        pesoTF.setText(String.valueOf(p.getPeso()) + " kg");
        allergieTA.setText(p.getAllergie());
    }

    public void salvaModificheInformazioni() {
        p.setEmail(emailTF.getText());
        try {
            p.setPeso((float) Double.parseDouble(pesoTF.getText()));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Valore non valido");
            alert.setHeaderText("Errore nel campo Peso");
            alert.setContentText("Inserisci un numero valido (es. 72.5).");

            alert.showAndWait();
        }
        p.setAllergie(allergieTA.getText());
        fattoriRischioAggiornati.setFumatore(fumatoreCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setProblemiAlcol(alcolismoCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setFamiliarita(familiaritaCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setObesita(sedentarietaCB.isSelected() ? 1 : 0);
        fattoriRischioAggiornati.setAlimentazioneScorretta(alimentazioneCB.isSelected() ? 1 : 0);

        AggiornaPaziente aggiornaPaziente = new AggiornaPaziente(p);

        if (aggiornaPaziente.aggiornaPaziente()) {
            Alert aggiornaPazienteAlert = new Alert(Alert.AlertType.INFORMATION);
            aggiornaPazienteAlert.setTitle("Successo");
            aggiornaPazienteAlert.setHeaderText(null);
            aggiornaPazienteAlert.setContentText("Modifica effettuata con successo");
            aggiornaPazienteAlert.showAndWait();

            Window currentWindow = salvaModifiche.getScene().getWindow();
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
