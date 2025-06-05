package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.pl.Models.GetListaUtenti;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class PortaleAdminController {

    @FXML
    private Button logoutB;
    @FXML
    private ListView<String> listaPazienti;
    @FXML
    private ListView<String> listaMedici;

    @FXML
    private void initialize() {
        //POPOLAZIONE DELLE LISTE
        // Medico
        GetListaUtenti glpaMedico = new GetListaUtenti();
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
        GetListaUtenti glpaPaziente = new GetListaUtenti();
        List<String> pazientiList = glpaPaziente.getListaPazientiCompleta();

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
        GetListaUtenti newGlpaMedico = new GetListaUtenti();
        List<String> newMediciList = newGlpaMedico.getListaMediciPortaleAdmin();
        ObservableList<String> newMedici = FXCollections.observableArrayList();
        newMedici.addAll(newMediciList);
        listaMedici.setItems(newMedici);
    }

    public void resetListViewPazienti() {
        GetListaUtenti newGlpaPaziente = new GetListaUtenti();
        List<String> newPazientiList = newGlpaPaziente.getListaPazientiCompleta();
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
        alert.setTitle("System Information Service");
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
        apriFinestraInserisci("../uiElements/InserisciNuovoMedico.fxml", "MEDICO");
    }

    //PAZIENTE MENUITEM
    @FXML
    public void inserisciNuovoPaziente(ActionEvent event) {
        apriFinestraInserisci("../uiElements/InserisciNuovoPaziente.fxml", "PAZIENTE");
    }
    
    private void apriFinestraInserisci(String fxmlPath, String ruolo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (ruolo != null) {
                if (ruolo.equals("MEDICO")) {
                    InserisciMedicoController inserisciMedicoController = loader.getController();
                    inserisciMedicoController.setInstance(this);
                } else {
                    InserisciPazienteController inserisciPazienteController = loader.getController();
                    inserisciPazienteController.setInstance(this);
                }
            } else {
                new Alert(Alert.AlertType.ERROR);
                throw new IOException("Impossibile caricare la finestra");
            }

            Stage stage = new Stage();
            stage.setTitle("Inserisci utente");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logout(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume(); // Blocca la chiusura automatica

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("System Information Service");
            alert.setHeaderText("Sei sicuro di voler uscire?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                stage.close(); // chiude davvero la finestra
            }
        });
    }
}
