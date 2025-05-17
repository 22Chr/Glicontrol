package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.ListaMedici;
import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

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

    public void openProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../uiElements/ModificaInformazioniPaziente.fxml")));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 800, 520));
        stage.show();
    }


}
