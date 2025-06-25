package com.univr.glicontrol;

import jakarta.mail.MessagingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("./pl/uiElements/MainAccess.fxml")));
            Scene scene = new Scene(root, 800, 520);

            setMacDockIcon();

            stage.setScene(scene);
            stage.setTitle("Glicontrol");
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setMacDockIcon() {
        // Esegui solo su macOS
        if (!System.getProperty("os.name").toLowerCase().contains("mac")) return;

        try {
            // Carica l'immagine dal classpath
            Image image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("./pl/uiElements/glicontrol.png")));

            // Imposta l'icona del Dock usando l’API Apple proprietaria
            com.apple.eawt.Application.getApplication().setDockIconImage(image);

        } catch (IOException | NullPointerException e) {
            System.err.println("Impossibile impostare l'icona Dock: " + e.getMessage());
        }
    }


    public static void main(String[] args) throws MessagingException {

        launch(args);
    }
}