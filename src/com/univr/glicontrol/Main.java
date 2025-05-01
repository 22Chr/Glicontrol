package com.univr.glicontrol;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            stage.setTitle("Glicontrol");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
