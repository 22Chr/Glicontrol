package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class DettaglioNuovoFarmacoController {

    private AggiungiNuovaTerapiaConcomitantePazienteController antcpc;
    private GestioneTerapie gt;
    private final UtilityPortalePaziente upp = new UtilityPortalePaziente();

    @FXML
    private ComboBox<String> listaFarmaciCompletaCB;
    @FXML
    private TextArea dosaggioTA, frequenzaTA, orariTA;

    public void setInstance(AggiungiNuovaTerapiaConcomitantePazienteController antcpc) {
        this.antcpc = antcpc;
    }

    public void setGestioneTerapie(GestioneTerapie gt) {
        this.gt = gt;
    }

    @FXML
    private void initialize(){
        listaFarmaciCompletaCB.getItems().addAll(upp.getListaFarmaciFormattatiCompleta());
    }

    //Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(listaFarmaciCompletaCB.getSelectionModel().getSelectedItem());
    //IndicazioniFarmaciTerapia indicazioni = new IndicazioniFarmaciTerapia()

}
