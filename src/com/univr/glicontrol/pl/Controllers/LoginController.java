package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.UtenteSessione;
import com.univr.glicontrol.pl.Models.Login;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class LoginController implements Controller {
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
        loginButton.setDefaultButton(true);
    }

    public void login(ActionEvent event) throws IOException {
        String codiceFiscale = codiceFiscaleTF.getText();
        String password = passwordPF.getText();

        if (codiceFiscale.isEmpty() || password.isEmpty()) {
            insertLabel.setText("Inserisci le tue credenziali per accedere al portale");
        }

        Login accedi = new Login(codiceFiscale, password, ruolo);

        if (accedi.getLogin()) {
            // Crea l'istanza dell'utente connesso
            switch (ruolo) {
                case "MEDICO" -> UtenteSessione.getInstance().creaMedicoConnesso(codiceFiscale, password);
                case "PAZIENTE" -> UtenteSessione.getInstance().creaPazienteConnesso(codiceFiscale, password);
                case "ADMIN" -> UtenteSessione.getInstance().creaAdminConnesso(codiceFiscale, password);
                default -> throw new IllegalStateException("Unexpected value: " + ruolo);
            }

            // muovi verso il portale relativo al ruolo selezionato
            // Carica la scena del portale in base al ruolo
            String fxmlFile = getFXMLForRole(ruolo);
            if (fxmlFile != null) {
                caricaPortale(event, fxmlFile);
            }
        } else {
            Alert utenteNonValidoAlert = new Alert(Alert.AlertType.ERROR);
            utenteNonValidoAlert.setTitle("System Information Service");
            utenteNonValidoAlert.setHeaderText("Credenziali non valide");
            utenteNonValidoAlert.setContentText("Controlla il codice fiscale e la password inseriti. Riprova");
            utenteNonValidoAlert.showAndWait();
        }
    }

    private void caricaPortale(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlFile)));
        Parent root = loader.load();
        root.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Object controller = loader.getController();
        if (controller instanceof PortaleAdminController pac) {
            pac.logout(stage); // 💡 Attiva listener sulla chiusura con X rossa
        } else if (controller instanceof PortalePazienteController ppc) {
            ppc.logout(stage);
        } else if (controller instanceof PortaleMedicoController pmc) {
            pmc.logout(stage);
        }
        stage.setScene(new Scene(root, 1200, 820));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    private String getFXMLForRole(String ruolo) {
        return switch (ruolo) {
            case "MEDICO" -> "../uiElements/PortaleMedico.fxml";
            case "PAZIENTE" -> {
                if (UtenteSessione.getInstance().getPazienteSessione().getPrimoAccesso() == 1) {
                    yield "../uiElements/BenvenutoPaziente.fxml";
                } else {
                    yield "../uiElements/PortalePaziente.fxml";
                }
            }
            case "ADMIN" -> "../uiElements/PortaleAdmin.fxml";
            default -> null;
        };
    }

    public void tornaAllaHome(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../uiElements/MainAccess.fxml")));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 800, 520));
        stage.show();
    }
}
