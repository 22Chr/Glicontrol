package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GlicontrolCoreSystem;
import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class PortaleMedicoController implements Portale {

    private final UtilityPortali upm = new UtilityPortali();
    private final Medico medico = upm.getMedicoSessione();
    private Paziente pazienteSelezionato;

    @FXML
    private Circle badgeC;

    @FXML
    private ListView<String> pazientiReferenteLV, pazientiGenericiLV;

    @FXML
    private TextField pazienteSelezionatoTF;

    @FXML
    private void initialize(){

        // Inizializza l'avatar con le iniziali del medico
        badgeC.setFill(new ImagePattern(upm.getBadge()));
        badgeC.setSmooth(true);
        badgeC.setStyle("-fx-border-color: #ff0404;");

        //Inizializzazione delle due listview
        ObservableList<String> pazientiReferenti = FXCollections.observableArrayList();
        pazientiReferenti.addAll(upm.getPazientiAssociatiAlReferente(medico.getIdUtente()));
        pazientiReferenteLV.setItems(pazientiReferenti);

        ObservableList<String> pazientiGenerici = FXCollections.observableArrayList();
        pazientiGenerici.addAll(upm.getPazientiNonAssociatiAlReferente(medico.getIdUtente()));
        pazientiGenericiLV.setItems(pazientiGenerici);

        //Se si schiaccia due volte su un paziente succedono cose
        pazientiReferenteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    pazienteSelezionatoTF.setText(cell.getItem()); //carica il nome al centro
                    pazienteSelezionato = upm.getPazienteAssociatoDaNomeFormattato(pazientiReferenteLV.getSelectionModel().getSelectedItem());
                }
            });

            return cell;
        });

        pazientiGenericiLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    pazienteSelezionatoTF.setText(cell.getItem()); //carica il nome al centro
                    pazienteSelezionato = upm.getPazienteNonAssociatoDaNomeFormattato(pazientiGenericiLV.getSelectionModel().getSelectedItem());
                }
            });

            return cell;
        });

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

    public void openGestoreTerapie() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraTerapiePaziente.fxml"));
            Parent root = fxmlLoader.load();

            FinestraTerapiePazienteController gestoreTerapiaController = fxmlLoader.getController();
            gestoreTerapiaController.setInstance(this);
            gestoreTerapiaController.setNomeBottoneInserimentoTerapia();

            Stage gestoreTerapiePaziente = new Stage();
            gestoreTerapiePaziente.setTitle("Terapie paziente");
            gestoreTerapiePaziente.setScene(new Scene(root));

            gestoreTerapiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sayHello() {
        System.out.println("Hello World!");
    }
}
