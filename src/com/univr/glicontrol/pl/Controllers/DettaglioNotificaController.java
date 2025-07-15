package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestioneNotifiche;
import com.univr.glicontrol.bll.Notifica;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DettaglioNotificaController {

    @FXML
    private TextArea dettaglioNotificaTA;

    private PortaleMedicoController pmc;
    private Paziente paziente;
    private UtilityPortali up;

    public void setInstance(PortaleMedicoController pmc, String notificaFormattata) {
        this.pmc = pmc;
        this.dettaglioNotificaTA.setText(notificaFormattata);
        up = new UtilityPortali();
        this.paziente = up.getNotificaNonVisualizzateDaNomeFormattato(notificaFormattata).getPazienteAssociato();
    }

    public void chiudiDettaglioNotifica() {
        Window currentWindow = dettaglioNotificaTA.getScene().getWindow();
        if (currentWindow instanceof Stage) {
            ((Stage) currentWindow).close();
        }
    }

    public void segnaNotificaComeVisualizzata() {
        GestioneNotifiche gn = new GestioneNotifiche(paziente);
        up = new UtilityPortali();
        Notifica notifica = up.getNotificaNonVisualizzateDaNomeFormattato(dettaglioNotificaTA.getText());

        if (gn.aggiornaStatoNotifica(notifica)) {
            Platform.runLater(() -> {
                pmc.resetListaNotifiche();
                chiudiDettaglioNotifica();
            });
        }
    }
}
