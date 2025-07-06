package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.Map;

public class DettaglioNuovoFarmacoController implements Controller {

    private GestioneTerapie gt;
    private UtilityPortali up;

    @FXML
    private ComboBox<String> listaFarmaciCompletaCB;
    @FXML
    private TextArea dosaggioTA, frequenzaTA, orariTA;

    public void setInstance(Paziente paziente, GestioneTerapie gt) {
        this.up = new UtilityPortali(paziente);
        this.gt = gt;

        Platform.runLater(this::caricaListaFarmaci);
    }

    private void caricaListaFarmaci() {
        Task<Void> loadListaFarmaci = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> farmaci = FXCollections.observableArrayList(up.getListaFarmaciFormattatiCompleta());

                Map<String, String> nomePrincipioMap = new HashMap<>();
                for (String nome : farmaci) {
                    Farmaco f = GestioneFarmaci.getInstance().getFarmacoByName(nome);
                    nomePrincipioMap.put(nome, f.getPrincipioAttivo());
                }

                // Filtro dinamico ottimizzato
                FilteredList<String> filteredItems = new FilteredList<>(farmaci, p -> true);
                listaFarmaciCompletaCB.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
                    final TextField editor = listaFarmaciCompletaCB.getEditor();
                    final String nomeSelezionato = listaFarmaciCompletaCB.getSelectionModel().getSelectedItem();
                    final String principioAttivoSelezionato = nomePrincipioMap.getOrDefault(nomeSelezionato, "");

                    listaFarmaciCompletaCB.show();

                    if (nomeSelezionato == null || !nomeSelezionato.equals(editor.getText()) || !principioAttivoSelezionato.equalsIgnoreCase(editor.getText())) {
                        filteredItems.setPredicate(item -> {
                            if (newValue == null || newValue.isEmpty()) return true;

                            String filtro = newValue.toLowerCase();
                            String nome = item.toLowerCase();
                            String principio = nomePrincipioMap.getOrDefault(item, "").toLowerCase();

                            return nome.contains(filtro) || principio.contains(filtro);
                        });
                        listaFarmaciCompletaCB.setItems(filteredItems);
                    }
                });

                Platform.runLater(() -> {
                    listaFarmaciCompletaCB.setItems(farmaci);
                    listaFarmaciCompletaCB.setItems(filteredItems);
                });

                return null;
            }

            @Override
            protected void failed() {
                System.err.println("Si è verificato un erorre durante il caricamto dei dati");


            }
        };

        new  Thread(loadListaFarmaci).start();
    }

    public void aggiungiFarmaco() {
        String nomeFarmaco = listaFarmaciCompletaCB.getSelectionModel().getSelectedItem();

        if (!InputChecker.getInstance().campoVuoto(frequenzaTA.getText()) || !InputChecker.getInstance().verificaDosaggioFarmaco(dosaggioTA.getText(), nomeFarmaco, true) || !InputChecker.getInstance().verificaOrariTerapia(orariTA.getText())) {
            Alert datiMancantiAlert =  new Alert(Alert.AlertType.ERROR);
            datiMancantiAlert.setTitle("System Notification Service");
            datiMancantiAlert.setHeaderText("Dati mancanti");
            datiMancantiAlert.setContentText("Per inserire un farmaco è necessario specificarne le modalità d'assunzione");
            datiMancantiAlert.showAndWait();

        } else {

            Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(nomeFarmaco);
            IndicazioniFarmaciTerapia indicazioni = new IndicazioniFarmaciTerapia(0, farmaco.getIdFarmaco(), up.convertiDosaggio(dosaggioTA.getText()), frequenzaTA.getText(), orariTA.getText());

            dosaggioTA.setText(dosaggioTA.getText() + " " + farmaco.getUnitaMisura());

            if (gt.generaFarmaciTerapia(farmaco, indicazioni)) {
                Alert successoInserimentoFarmacoAlert = new Alert(Alert.AlertType.INFORMATION);
                successoInserimentoFarmacoAlert.setTitle("System Information Service");
                successoInserimentoFarmacoAlert.setHeaderText("Farmaco inserito con successo");
                successoInserimentoFarmacoAlert.setContentText("Il farmaco è stato inserito con successo");
                successoInserimentoFarmacoAlert.showAndWait();

                Window currentWindow = dosaggioTA.getScene().getWindow();
                if (currentWindow instanceof Stage) {
                    ((Stage) currentWindow).close();
                }

            } else {
                Alert erroreInserimentoFarmacoAlert = new Alert(Alert.AlertType.ERROR);
                erroreInserimentoFarmacoAlert.setTitle("System Information Service");
                erroreInserimentoFarmacoAlert.setHeaderText("Errore durante l'inserimento del farmaco");
                erroreInserimentoFarmacoAlert.setContentText("Non è stato possibile inserire il farmaco, riprova");
                erroreInserimentoFarmacoAlert.showAndWait();
            }
        }
    }

    @FXML
    private void initialize() {
        dosaggioTA.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                dosaggioTA.setStyle("");
            } else if (InputChecker.getInstance().verificaDosaggioFarmaco(newVal, listaFarmaciCompletaCB.getSelectionModel().getSelectedItem(), true)) {
                dosaggioTA.setStyle("-fx-border-color: #43a047;");
            } else {
                dosaggioTA.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        frequenzaTA.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                frequenzaTA.setStyle("");
            } else if (InputChecker.getInstance().campoVuoto(newVal)) {
                frequenzaTA.setStyle("-fx-border-color: #43a047;");
            } else {
                frequenzaTA.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }
        });

        orariTA.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                orariTA.setStyle("");
            } else if (InputChecker.getInstance().verificaOrariTerapia(newVal)) {
                orariTA.setStyle("-fx-border-color: #43a047;");
            } else {
                orariTA.setStyle("-fx-border-color: #ff0000; -fx-border-width: 3px;");
            }

        });
    }

}