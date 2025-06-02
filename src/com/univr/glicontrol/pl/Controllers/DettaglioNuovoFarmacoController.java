package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.Map;

public class DettaglioNuovoFarmacoController {

    private InserisciNuovaTerapiaConcomitantePazienteController antcpc;
    private GestioneTerapie gt;
    private final UtilityPortalePaziente upp = new UtilityPortalePaziente();

    @FXML
    private ComboBox<String> listaFarmaciCompletaCB;
    @FXML
    private TextArea dosaggioTA, frequenzaTA, orariTA;

    public void setInstance(InserisciNuovaTerapiaConcomitantePazienteController antcpc) {
        this.antcpc = antcpc;
    }

    public void setGestioneTerapie(GestioneTerapie gt) {
        this.gt = gt;
    }

    @FXML
    private void initialize() {
        ObservableList<String> farmaci = FXCollections.observableArrayList(upp.getListaFarmaciFormattatiCompleta());
        listaFarmaciCompletaCB.setItems(farmaci);


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
    }

    public void aggiungiFarmaco() {
        Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(listaFarmaciCompletaCB.getSelectionModel().getSelectedItem());
        IndicazioniFarmaciTerapia indicazioni = new IndicazioniFarmaciTerapia(0, farmaco.getIdFarmaco(), convertiDosaggio(dosaggioTA.getText()), frequenzaTA.getText(), orariTA.getText());

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

    private float convertiDosaggio(String dosaggio) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < dosaggio.length(); i++) {
            if (Character.isDigit(dosaggio.charAt(i))) {
                result.append(dosaggio.charAt(i));
            }
        }
        return Float.parseFloat(result.toString());
    }
}
