package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.Medico;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ModificaMedicoController {
    private Medico m;

    @FXML
    private TextField CFMedicoTF;

    @FXML
    private TextField nomeMedicoTF;

    @FXML
    private TextField cognomeMedicoTF;

    @FXML
    private TextField emailMedicoTF;


    public void setMedico(Medico medico) {
       this.m = medico;
       CFMedicoTF.setText(m.getCodiceFiscale());
       nomeMedicoTF.setText(m.getNome());
       cognomeMedicoTF.setText(m.getCognome());
       emailMedicoTF.setText(m.getEmail());
    }
}
