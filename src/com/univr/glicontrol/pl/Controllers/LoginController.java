package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.pl.Models.Login;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private Label insertLabel;
    @FXML
    private TextField codiceFiscaleTF;
    @FXML
    private PasswordField passwordPF;
    @FXML
    private Button loginButton;
    @FXML
    private Label portalLabel;

    private String ruolo;

    // Usato da MainAccessController per impostare il ruolo e caricare la label di benvenuto corretta
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
        portalLabel.setText("ACCESSO AL PORTALE " + ruolo);
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> loginButton.requestFocus());
    }

    public void login(ActionEvent event) throws IOException {
        String codiceFiscale = codiceFiscaleTF.getText();
        String password = passwordPF.getText();

        if (codiceFiscale.isEmpty() || password.isEmpty()) {
            insertLabel.setText("Inserisci le tue credenziali per accedere al portale");
        }

        Login accedi = new Login(codiceFiscale, password, ruolo);

        if (accedi.getLogin()) {
            // muovi verso il portale relativo al ruolo selezionato
            insertLabel.setText("Sei connesso al portale " + ruolo.toLowerCase());
        } else {
            insertLabel.setText("Credenziali non valide, riprova");
        }
    }
}
