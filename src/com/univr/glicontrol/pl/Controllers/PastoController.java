package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.GestionePasti;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.UtenteSessione;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.sql.Time;

public class PastoController {
    @FXML
    private ComboBox<String> pastoCB,oraCB, minutiCB; //ora e minuti verranno convertiti in int per DB

    private final UtilityPortalePaziente upp = new UtilityPortalePaziente();
    private final Paziente paziente = upp.getPazienteSessione();

    public void initialize(){
        pastoCB.getItems().addAll("Colazione", "Pranzo", "Cena", "Merenda");
        oraCB.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutiCB.getItems().addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
    }

    private GestionePasti gp = new GestionePasti(paziente);

    public void confermaPastoeOrario(){
        String pasto = pastoCB.getValue();
        int ora = oraCB.getValue().equals("00") ? 0 : Integer.parseInt(oraCB.getValue());
        int minuti = minutiCB.getValue().equals("00") ? 0 : Integer.parseInt(minutiCB.getValue());
        Time orario = upp.convertiOraPasto(ora, minuti);
        gp.inserisciPasto(pasto, orario);
    }

}
