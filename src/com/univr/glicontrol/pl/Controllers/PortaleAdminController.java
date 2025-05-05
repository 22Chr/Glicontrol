package com.univr.glicontrol.pl.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class PortaleAdminController {

    @FXML
    private Button logoutB;
    @FXML
    private MenuItem medicoMI, pazienteMI;

    @FXML
    private void initialize() {
        // Puoi inizializzare dati o logica qui
    }

    //LOGOUT
    private void logout(ActionEvent event) {
        //mostra finestra di conferma
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Logout");
        alert.setHeaderText("Sei sicuro di voler uscire?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            //Chiudi finestra corrente
            Window currentWindow = logoutB.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }

            //Apri schermata di login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.setTitle("Login");
                loginStage.setScene(new Scene(root));
                loginStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //MEDICO MENUITEM
    private void inserisciNuovoMedico(ActionEvent event) {
        apriFinestra("/InserisciNuovoMedico.fxml", "Inserisci medico");
    }

    //PAZIENTE MENUITEM
    private void inserisciNuovoPaziente(ActionEvent event) {
        apriFinestra("/view/InserisciNuovoPaziente.fxml", "Inserisci paziente");
    }
    //DA CREAREEEEEEEEE
    private void apriFinestra(String fxmlPath, String titoloFinestra) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titoloFinestra);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Blocca interazione con la finestra principale
            stage.showAndWait(); // Attende la chiusura della finestra

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
