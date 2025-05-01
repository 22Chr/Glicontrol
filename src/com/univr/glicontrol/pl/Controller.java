package com.univr.glicontrol.pl;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    public void apriLoginMedico(ActionEvent event) throws IOException {
        cambiaScena(event, "/login-medico.fxml");
    }

    public void apriLoginPaziente(ActionEvent event) throws IOException {
        cambiaScena(event, "/login-paziente.fxml");
    }

    public void apriLoginAdmin(ActionEvent event) throws IOException {
        cambiaScena(event, "/login-admin.fxml");
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
