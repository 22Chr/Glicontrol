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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class BenvenutoPazienteController implements InserimentoPastiController, Controller {

    @FXML
    private VBox page0, page1, page2, page3, page4, page5;
    @FXML
    private RadioButton famSi, famNo;
    @FXML
    private CheckBox fumoCB, alcoolCB, alimentCB, sedentCB;
    @FXML
    private ListView<String> listaPasti;
    @FXML
    private Button salvaB;
    @FXML
    private TextField pesoTF, altezzaTF;
    @FXML
    private TextArea allergieTA;

    private int paginaCorrente = 0; //page 0

    private GestioneFattoriRischio gestioneFattoriRischio = new GestioneFattoriRischio();
    Paziente paziente = new UtilityPortali().getPazienteSessione();
    private FattoriRischio fattoriRischioAggiornati = gestioneFattoriRischio.getFattoriRischio(paziente.getIdUtente());

    // ==============================
    //GESTIONE DEI BOTTONI AVANTI E INDIETRO
    // ==============================
    @FXML
    private void nextPage() { //metodo dei bottoni Avanti
        nascondiPaginaCorrente();
        paginaCorrente++;
        mostraPaginaCorrente();
    }


    @FXML
    private void previousPage() { //metodo dei bottoni Indietro
        if (paginaCorrente > 0) {
            nascondiPaginaCorrente();
            paginaCorrente--;
            mostraPaginaCorrente();
        }
    }

    private void nascondiPaginaCorrente() { //rende invisibile pagina corrente
        switch (paginaCorrente) {
            case 0 -> page0.setVisible(false);
            case 1 -> page1.setVisible(false);
            case 2 -> page2.setVisible(false);
            case 3 -> page3.setVisible(false);
            case 4 -> page4.setVisible(false);
            case 5 -> page5.setVisible(false);
        }
    }

    private void mostraPaginaCorrente() { //rende visibile pagina corrente
        switch (paginaCorrente) {
            case 0 -> page0.setVisible(true);
            case 1 -> page1.setVisible(true);
            case 2 -> page2.setVisible(true);
            case 3 -> page3.setVisible(true);
            case 4 -> page4.setVisible(true);
            case 5 -> page5.setVisible(true);
        }
    }

    // ==============================
    //GESTIONE DEI BOTTONI FAMILIARITA'
    // ==============================
    public void gestisciFamiliarita() {
        if(famSi.isSelected()){
            fattoriRischioAggiornati.setFamiliarita(1);
        }
        else if (famNo.isSelected()) {
            fattoriRischioAggiornati.setFamiliarita(0);
        }
    }

    // ==============================
    //GESTIONE DEI CHECKBOX FUMO E ALCOOL
    // ==============================

    public void gestisciFumoAlcool() {
        boolean fumo = fumoCB.isSelected();
        boolean alcol = alcoolCB.isSelected();

        if (fumo) {
            fattoriRischioAggiornati.setFumatore(1);
        } else {
            fattoriRischioAggiornati.setFumatore(0);
        }

        if (alcol) {
            fattoriRischioAggiornati.setProblemiAlcol(1);
        } else {
            fattoriRischioAggiornati.setProblemiAlcol(0);
        }
    }

    // ==============================
    //GESTIONE ALTRE CATTIVE ABITUDINI
    // ==============================

    public void gestisciAltreCattiveAbitudini() {
        boolean alimentazione = alimentCB.isSelected();
        boolean sedentarieta = sedentCB.isSelected();

        if (alimentazione) {
            fattoriRischioAggiornati.setAlimentazioneScorretta(1);
        } else {
            fattoriRischioAggiornati.setAlimentazioneScorretta(0);
        }

        if (sedentarieta) {
            fattoriRischioAggiornati.setSedentarieta(1);
        } else {
            fattoriRischioAggiornati.setSedentarieta(0);
        }


    }

    // Salva i dati nel DB

    // ==============================
    //GESTIONE PESO E ALLERGIE
    // ==============================


    // ==============================
    //GESTIONE DEI PASTI: bottone per inserirne di nuovi, lista che si aggiorna
    // ==============================

    public void inserisciNuovoPasto() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/InserisciNuovoPasto.fxml"));
        Parent root = loader.load();

        InserisciPastoController ipc = loader.getController();
        ipc.setInstance(this);

        Stage stage = new Stage();
        stage.setTitle("Inserisci un nuovo pasto");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
        // Popola lista pasti
        UtilityPortali up = new UtilityPortali();
        ObservableList<String> pasti = FXCollections.observableArrayList();
        pasti.addAll(up.getListaPasti());
        listaPasti.setItems(pasti);

        salvaB.requestFocus();

        altezzaTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                altezzaTF.setStyle("");
            } else if (InputChecker.getInstance().verificaAltezza(newValue)) {
                altezzaTF.setStyle("-fx-border-color: #43a047;");
            }  else {
                assert altezzaTF != null;
                altezzaTF.setStyle("-fx-border-color: #ff0000;  -fx-border-width: 3px;");
            }
        });

        pesoTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                pesoTF.setStyle("");
            } else if (InputChecker.getInstance().verificaPeso(newValue) && pesoTF != null)
                pesoTF.setStyle("-fx-border-color: #43a047;");
            else {
                assert pesoTF != null;
                pesoTF.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });
    }

    public void resetListViewPasti() {
        UtilityPortali newUp = new UtilityPortali();
        ObservableList<String> newPasti = FXCollections.observableArrayList();
        newPasti.addAll(newUp.getListaPasti());
        listaPasti.setItems(newPasti);
    }

    public void salvaDatiPrimoAccesso() {
        paziente.setPrimoAccesso(0);
        paziente.setAltezza(Integer.parseInt(altezzaTF.getText().substring(0, altezzaTF.getText().length() - 3)));
        paziente.setPeso(Integer.parseInt(pesoTF.getText().substring(0, pesoTF.getText().length() - 3)));
        paziente.setAllergie(allergieTA.getText());

        if (!InputChecker.getInstance().verificaAltezza(altezzaTF.getText()) || !InputChecker.getInstance().verificaPeso(pesoTF.getText())) {
            Alert pesoEAltezzaNonValidiAlert = new Alert(Alert.AlertType.ERROR);
            pesoEAltezzaNonValidiAlert.setTitle("System Notification Service");
            pesoEAltezzaNonValidiAlert.setHeaderText("Dati non validi");
            pesoEAltezzaNonValidiAlert.setContentText("Assicurati di inserire correttamente peso e altezza.\nIl peso deve essere espresso in kg, mentre l'altezza in cm, ambedue indicando la relativa unità di misura");
            pesoEAltezzaNonValidiAlert.showAndWait();

            return;
        }

        if (GestionePazienti.getInstance().aggiornaPaziente(paziente) && gestioneFattoriRischio.aggiornaFattoriRischio(fattoriRischioAggiornati)) {
            Alert notificaSalvataggioAlert = new Alert(Alert.AlertType.INFORMATION);
            notificaSalvataggioAlert.setTitle("System Notification Service");
            notificaSalvataggioAlert.setHeaderText("Le tue informazioni sono state salvate con successo");
            notificaSalvataggioAlert.showAndWait();

            // Indirizza l'utente al suo portale
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/PortalePaziente.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Portale paziente");
                stage.setScene(new Scene(root, 1200, 700));
                stage.setResizable(false);
                Stage currentStage = (Stage) salvaB.getScene().getWindow();
                currentStage.close();
                stage.show();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            Alert erroreSalvataggioDatiAlert = new Alert(Alert.AlertType.ERROR);
            erroreSalvataggioDatiAlert.setTitle("System Notification Service");
            erroreSalvataggioDatiAlert.setHeaderText("Si è verificato un errore durante il salvataggio dei dati. Riprova");
            erroreSalvataggioDatiAlert.showAndWait();
        }
    }
}