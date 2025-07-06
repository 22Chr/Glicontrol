package com.univr.glicontrol.pl.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainAccessController implements Controller {
    private String ruolo;

    public void apriLoginMedico(ActionEvent event) throws IOException {
        ruolo = "MEDICO";
        cambiaScena(event, ruolo);
    }

    public void apriLoginPaziente(ActionEvent event) throws IOException {
        ruolo = "PAZIENTE";
        cambiaScena(event, ruolo);
    }

    public void apriLoginAdmin(ActionEvent event) throws IOException {
        ruolo = "ADMIN";
        cambiaScena(event, ruolo);
    }

    private void cambiaScena(ActionEvent event, String ruolo) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../uiElements/Login.fxml")));
        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setRuolo(ruolo);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 800, 520));
        stage.setResizable(false);
        stage.show();
    }
}
