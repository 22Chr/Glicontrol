package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;
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
    private ListView<String> listaPazienti;
    @FXML
    private ListView<String> listaMedici;

    @FXML
    private void initialize() {
        //POPOLAZIONE DELLE LISTE
        // Medico
        GetListaPortaleAdmin glpaMedico = new GetListaPortaleAdmin();
        List<String> mediciList = glpaMedico.getListaMediciPortaleAdmin();

        ObservableList<String> medici = FXCollections.observableArrayList();
        medici.addAll(mediciList);
        listaMedici.setItems(medici);

        listaMedici.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    String selectedItem = cell.getItem();
                    glpaMedico.updateListaMediciPortaleAdmin();
                    int idMedico = glpaMedico.getIdMedico(selectedItem);
                    apriFinestraModifica("../uiElements/ModificaMedico.fxml", "MEDICO", idMedico);
                }
            });

            return cell;
        });

        // Paziente
        GetListaPortaleAdmin glpaPaziente = new GetListaPortaleAdmin();
        List<String> pazientiList = glpaPaziente.getListaPazientiPortaleAdmin();

        ObservableList<String> pazienti = FXCollections.observableArrayList();
        pazienti.addAll(pazientiList);
        listaPazienti.setItems(pazienti);

        listaPazienti.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    String selectedItem = cell.getItem();
                    glpaPaziente.updateListaPazientiPortaleAdmin();
                    int idPaziente = glpaPaziente.getIdPaziente(selectedItem);
                    apriFinestraModifica("../uiElements/ModificaPaziente.fxml", "PAZIENTE", idPaziente);
                }
            });

            return cell;
        });
    }

    public void resetListViewMedici() {
        GetListaPortaleAdmin newGlpaMedico = new GetListaPortaleAdmin();
        List<String> newMediciList = newGlpaMedico.getListaMediciPortaleAdmin();
        ObservableList<String> newMedici = FXCollections.observableArrayList();
        newMedici.addAll(newMediciList);
        listaMedici.setItems(newMedici);
    }

    public void resetListViewPazienti() {
        GetListaPortaleAdmin newGlpaPaziente = new GetListaPortaleAdmin();
        List<String> newPazientiList = newGlpaPaziente.getListaPazientiPortaleAdmin();
        ObservableList<String> newPazienti = FXCollections.observableArrayList();
        newPazienti.addAll(newPazientiList);
        listaPazienti.setItems(newPazienti);
    }

    private void apriFinestraModifica(String fxmlPath, String ruolo, int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (ruolo != null) {
                if (ruolo.equals("MEDICO")) {
                    ModificaMedicoController modificaMedicoController = loader.getController();
                    modificaMedicoController.setInstance(this);
                    ListaMedici getMedico = new ListaMedici();
                    modificaMedicoController.setMedico(getMedico.ottieniMedicoPerId(id));
                } else {
                    ModificaPazienteController modificaPazienteController = loader.getController();
                    modificaPazienteController.setInstance(this);
                    ListaPazienti getPaziente = new ListaPazienti();
                    modificaPazienteController.setPaziente(getPaziente.ottieniPazientePerId(id));
                }
            } else {
                throw new IOException("Impossibile caricare la finestra");
            }

            Stage stage = new Stage();
            stage.setTitle("Modifica");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


    //LOGOUT
    @FXML
    public void logout(ActionEvent event) {
        //mostra finestra di conferma
        Stage currentStage = (Stage) logoutB.getScene().getWindow();
        boolean isFullScreen = currentStage.isFullScreen();
        if (isFullScreen) {
            currentStage.setFullScreen(false);
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Logout");
        alert.setHeaderText("Sei sicuro di voler uscire?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            //Chiudi finestra corrente e con essa l'app
            Window currentWindow = logoutB.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }
        }
    }

    //MEDICO MENUITEM
    @FXML
    public void inserisciNuovoMedico(ActionEvent event) {
        apriFinestraInserisci("/InserisciNuovoMedico.fxml", "Inserisci medico");
    }

    //PAZIENTE MENUITEM
    @FXML
    public void inserisciNuovoPaziente(ActionEvent event) {
        apriFinestraInserisci("/view/InserisciNuovoPaziente.fxml", "Inserisci paziente");
    }
    
    private void apriFinestraInserisci(String fxmlPath, String titoloFinestra) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titoloFinestra);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Blocca interazione con la finestra principale
            stage.showAndWait(); // Attende la chiusura della finestra

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setStageAndSetupListeners(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume(); // Blocca la chiusura automatica

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Sei sicuro di voler uscire?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                stage.close(); // chiude davvero la finestra
            }
        });
    }
}
