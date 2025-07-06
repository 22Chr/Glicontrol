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
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setMacDockIcon() {
        if (!System.getProperty("os.name").toLowerCase().contains("mac")) return;

        try {
            Image image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("./pl/uiElements/glicontrol.png")));

            // Usa la reflection per evitare l'import diretto di com.apple.eawt.Application
            Class<?> appClass = Class.forName("com.apple.eawt.Application");
            Object application = appClass.getMethod("getApplication").invoke(null);
            appClass.getMethod("setDockIconImage", java.awt.Image.class).invoke(application, image);

        } catch (Exception e) {
            System.err.println("Impossibile impostare l'icona Dock: " + e.getMessage());
        }
    }



    public static void main(String[] args) throws MessagingException {

        launch(args);
    }
}