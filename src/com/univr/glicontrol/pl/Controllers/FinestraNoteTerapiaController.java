package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.Terapia;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FinestraNoteTerapiaController implements Controller {

    @FXML
    private TextArea noteTerapiaTA;
    @FXML
    private Button salvaNoteTerapiaB;

    private InserisciNuovaTerapiaController intc = null;
    private FinestraTerapiePazienteController ftpc = null;
    private Terapia terapia = null;

    public void setInstance(Controller controller, boolean modificabile) {
        if (controller instanceof InserisciNuovaTerapiaController) {
            this.intc = (InserisciNuovaTerapiaController) controller;
        }  else {
            this.ftpc = (FinestraTerapiePazienteController) controller;

            Platform.runLater(() -> noteTerapiaTA.setText(terapia.getNoteTerapia()));

            if (modificabile) {
                noteTerapiaTA.setEditable(true);
                salvaNoteTerapiaB.setVisible(true);
            } else {
                noteTerapiaTA.setEditable(false);
                salvaNoteTerapiaB.setVisible(false);
            }
        }
    }

    public void setTerapia(Terapia terapia) {
        this.terapia = terapia;
    }

    public void salvaNoteTerapia() {
        if (intc != null) {
            intc.setNoteTerapia(noteTerapiaTA.getText());
        } else {
            ftpc.setNoteTerapia(noteTerapiaTA.getText());
        }

        Window currentWindow = noteTerapiaTA.getScene().getWindow();
        ((Stage) currentWindow).close();
    }
}
