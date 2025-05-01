package com.univr.glicontrol.pl.uiElements;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainAccess extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/MainAccess.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false); // o true se vuoi finestra adattabile
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
