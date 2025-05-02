package com.univr.glicontrol.pl.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    public void login(ActionEvent event) throws IOException {
        String codiceFiscale = codiceFiscaleTF.getText();
        String password = passwordPF.getText();

        //chiamare un metodo dentro a Model, contenente i segnaposti dei metodi della
        //BLL
        //poi dice se va bene o no

    }
}
