package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class PortalePazienteController {
    //ultimeRilevazioniLW dovrà contenere il sunto delle ultime rilevazioni
    //andamentoGlicemia dovrà mostrare una rappresentazione grafica -> cercare come fare
    //l'avatar deve mostrare le iniziali dell'utente
    //i vari textfield e la text area contengono le info sul medico di riferimento
    //il profileB deve rimandare a una paginetta con le info del paziente e il bottone di logout

    private final UtilityPortalePaziente upp = new UtilityPortalePaziente();
    private final Paziente paziente = upp.getPazienteSessione();
    private final Medico medicoRiferimento = new ListaMedici().ottieniMedicoPerId(paziente.getMedicoRiferimento());

    @FXML
    private TextField nomeMedicoRiferimentoTF, cognomeMedicoRiferimentoTF, emailMedicoRiferimentoTF;

    @FXML
    private Circle badgeCircle;

    @FXML
    private void initialize() {
        // Popola box con i dati di contatto del medico di riferimento
        nomeMedicoRiferimentoTF.setText(medicoRiferimento.getNome());
        cognomeMedicoRiferimentoTF.setText(medicoRiferimento.getCognome());
        emailMedicoRiferimentoTF.setText(medicoRiferimento.getEmail());

        // Inizializza l'avatar con le iniziali del paziente
        badgeCircle.setFill(new ImagePattern(upp.getBadge()));
        badgeCircle.setSmooth(true);
        badgeCircle.setStyle("-fx-border-color: red;");
        // cercare metodo per risolvere il problema
    }

    public void openProfile() {
        // Carica il file FXML della nuova finestra
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/ModificaInformazioniPaziente.fxml"));
            Parent root = fxmlLoader.load();

            // Crea una nuova finestra (Stage)
            Stage infoPaziente = new Stage();
            infoPaziente.setTitle("Informazioni personali");
            infoPaziente.setScene(new Scene(root));

            // Mostra la nuova finestra SENZA chiudere quella attuale
            infoPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void openSintomi() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraSintomiPaziente.fxml"));
            Parent root = fxmlLoader.load();

            Stage sintomiPaziente = new Stage();
            sintomiPaziente.setTitle("I miei sintomi");
            sintomiPaziente.setScene(new Scene(root));

            sintomiPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logout(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Sei sicuro di voler uscire?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                stage.close();
            }
        });
    }

    public void openPatologie() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraPatologieConcomitantiPaziente.fxml"));
            Parent root = fxmlLoader.load();

            Stage patologiePaziente = new Stage();
            patologiePaziente.setTitle("Le mie patologie");
            patologiePaziente.setScene(new Scene(root));

            patologiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
