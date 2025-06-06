package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ModificaInformazioniPazienteController implements InserimentoPastiController {

    @FXML
    private CheckBox fumatoreCB, alcolismoCB, familiaritaCB, sedentarietaCB, alimentazioneCB;

    @FXML
    private TextField nomeTF, cognomeTF, codFisTF, emailTF, pesoTF;

    @FXML
    private TextArea allergieTA;

    @FXML
    private Button salvaModifiche;

    @FXML
    private ListView<String> pastiLV;

    private Paziente p;

    private final GestioneFattoriRischio gestioneFattoriRischio = new GestioneFattoriRischio();
    Paziente paziente = new UtilityPortali().getPazienteSessione();
    private final FattoriRischio fattoriRischioAggiornati = gestioneFattoriRischio.getFattoriRischio(paziente.getIdUtente());
    private final UtilityPortali upp = new UtilityPortali();
    String pastoDaModificare;

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

        ObservableList<String> pasti = FXCollections.observableArrayList();
        pasti.addAll(upp.getListaPasti());
        pastiLV.setItems(pasti);

        pastiLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    pastoDaModificare = cell.getItem();
                    modificaPasto(pastoDaModificare);
                }
            });

            return cell;
        });


        // Verifica attiva dei campi
        emailTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (InputChecker.getInstance().verificaEmail(newValue) && emailTF != null)
                emailTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert emailTF != null;
                emailTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        pesoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (InputChecker.getInstance().verificaPeso(newValue) && pesoTF != null)
                pesoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert pesoTF != null;
                pesoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });
    }

    private void modificaPasto(String pastoDaModificare) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/ModificaPasto.fxml"));
            Parent root = loader.load();

            // Ottieni il controller associato
            ModificaPastoController controller = loader.getController();
            controller.setInstance(this, pastoDaModificare); // Passa il dato al controller

            // Crea e mostra la nuova scena/finestra
            Stage stage = new Stage();
            stage.setTitle("Modifica il pasto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocca finestra principale
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aggiungiPasto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/InserisciNuovoPasto.fxml"));
            Parent root = loader.load();

            InserisciPastoController ipc = loader.getController();
            ipc.setInstance(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi un nuovo pasto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocca finestra principale
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void salvaModificheInformazioni() {

        if (InputChecker.getInstance().verificaPeso(pesoTF.getText()) && InputChecker.getInstance().verificaEmail(emailTF.getText())) {
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

    public void resetListViewPasti() {
        UtilityPortali newUpp = new UtilityPortali();
        ObservableList<String> newPasti = FXCollections.observableArrayList();
        newPasti.addAll(newUpp.getListaPasti());
        pastiLV.setItems(newPasti);
    }
}
