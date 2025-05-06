package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.GetListaPortaleAdmin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class PortaleAdminController {

    @FXML
    private Button logoutB;
    @FXML
    private MenuItem medicoMI, pazienteMI;
    @FXML
    private ListView<Paziente> listaPazienti;
    @FXML
    private ListView<Medico> listaMedici;

    @FXML
    private void initialize() {
        // Popola le liste
        // Medico
        GetListaPortaleAdmin glpaMedico = new GetListaPortaleAdmin();
        List<Medico> mediciList = glpaMedico.getListaMediciPortaleAdmin();
        for (int i = 0; i < mediciList.size(); i++) {
            if (mediciList.get(i) == null) {
                System.out.println("Found null at index " + i + " in list of Medici");
            }
        }

        ObservableList<Medico> medici = FXCollections.observableArrayList();
        medici.addAll(mediciList);
        listaMedici.setItems(medici);

        // Paziente
        GetListaPortaleAdmin glpaPaziente = new GetListaPortaleAdmin();
        List<Paziente> pazientiList = glpaPaziente.getListaPazientiPortaleAdmin();

        ObservableList<Paziente> pazienti = FXCollections.observableArrayList();
        pazienti.addAll(pazientiList);
        listaPazienti.setItems(pazienti);
    }


    //LOGOUT
    @FXML
    public void logout(ActionEvent event) {
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
    @FXML
    public void inserisciNuovoMedico(ActionEvent event) {
        apriFinestra("/InserisciNuovoMedico.fxml", "Inserisci medico");
    }

    //PAZIENTE MENUITEM
    @FXML
    public void inserisciNuovoPaziente(ActionEvent event) {
        apriFinestra("/view/InserisciNuovoPaziente.fxml", "Inserisci paziente");
    }
    //! NECESSARIO CREARE GLI FXML
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
