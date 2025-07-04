package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.pl.Models.GetListaUtenti;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FiltriLogController {

    @FXML
    private ComboBox<String> tipoLogCB, medicoCB, pazienteCB;

    private void caricaListaPazienti() {

        Task<Void> caricaListeTask = new Task<>() {

            @Override
            protected Void call() {
                // Pazienti
                ObservableList<String> pazienti = FXCollections.observableArrayList();
                List<String> listaPazienti = new GetListaUtenti().getListaPazientiCompleta();
                pazienti.addAll(listaPazienti);

                // Medici
                ObservableList<String> medici = FXCollections.observableArrayList();
                List<String> listaMedico = new GetListaUtenti().getListaMediciPortaleAdmin();
                medici.addAll(listaMedico);

                // Tipo
                ObservableList<String> tipo = FXCollections.observableArrayList();
                String[] tipi = {"TERAPIA", "PATOLOGIA_CONCOMITANTE", "INFO_PAZIENTE"};
                List<String> listaTipi = new ArrayList<>(Arrays.asList(tipi));
                tipo.addAll(listaTipi);

                Platform.runLater(() -> {
                   tipoLogCB.setItems(tipo);
                   medicoCB.setItems(medici);
                   pazienteCB.setItems(pazienti);
                });

                return null;
            }
        };
        new Thread(caricaListeTask).start();
    }

    @FXML
    private void initialize() {
        caricaListaPazienti();
    }
}

/* TODO: implementare meccanismo di ricerca nelle combobox come visto per i farmaci inseribili nelle terapie
*   Per medici e pazienti la ricerca deve poter essere fatta per nome, cognome e codice fiscale
*   Per le patologie la ricerca viene fatta per nome
*   Quando si applicano i filtri, vanno chiamati dei metodi da definire in PortaleAdminController per rigenerare la listview sulla base dei parametri specificati
*   Il pulsante di reset dei filtri dovrà invece richiamare il metodo popolaListaLog() del suddetto portale amministratore
* */