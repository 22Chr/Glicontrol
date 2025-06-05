package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.ListaPazienti;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class PortaleMedicoController {

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
                    pazienteSelezionato = upm.getPazienteAssociatoDaNomeFormattato(pazientiReferenteLV.getSelectionModel().getSelectedItem(), medico.getIdUtente());
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
                    pazienteSelezionato = upm.getPazienteNonAssociatoDaNomeFormattato(pazientiGenericiLV.getSelectionModel().getSelectedItem(), medico.getIdUtente());
                }
            });

            return cell;
        });

    }


}
