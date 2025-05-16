package com.univr.glicontrol;

import jakarta.mail.MessagingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("./pl/uiElements/MainAccess.fxml")));
            Scene scene = new Scene(root, 800, 520);
            stage.setScene(scene);
            stage.setTitle("Glicontrol");
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws MessagingException {

        launch(args);
    }
}