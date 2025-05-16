package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.FattoriRischio;
import com.univr.glicontrol.bll.GestioneFattoriRischio;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class ModificaInformazioniPazienteController {

    @FXML
    private CheckBox fumatoreCB, alcolismoCB, familiaritaCB, sedentarietaCB, alimentazioneCB;

    @FXML
    private TextField nomeTF, cognomeTF, codFisTF, emailTF, pesoTF;

    private Paziente p;

    private GestioneFattoriRischio gestioneFattoriRischio = new GestioneFattoriRischio();
    Paziente paziente = new UtilityPortalePaziente().getPazienteSessione();
    private FattoriRischio fattoriRischioAggiornati = gestioneFattoriRischio.getFattoriRischio(paziente.getIdUtente());

    @FXML
    private void initialize() {

        //inizializzazione delle checkbox al loro stato attuale
        int fumatore = fattoriRischioAggiornati.getFumatore();
        int alcolismo = fattoriRischioAggiornati.getProblemiAlcol();
        int familiarita = fattoriRischioAggiornati.getFamiliarita();
        int sedentarieta = fattoriRischioAggiornati.getSedentarieta();
        int alimentazione = fattoriRischioAggiornati.getAlimentazioneScorretta();

        if (fumatore == 1) {
            fumatoreCB.setSelected(true);
        }
        if (alcolismo == 1) {
            alcolismoCB.setSelected(true);
        }
        if (familiarita == 1) {
            familiaritaCB.setSelected(true);
        }
        if (sedentarieta == 1) {
            sedentarietaCB.setSelected(true);
        }
        if (alimentazione == 1) {
            alimentazioneCB.setSelected(true);
        }

        //inizializza le informazioni del paziente
        this.p = paziente;
        nomeTF.setText(p.getNome());
        cognomeTF.setText(p.getCognome());
        codFisTF.setText(p.getCodiceFiscale());
        emailTF.setText(p.getEmail());
        //pesoTF.setText(p.getPeso().toString);
    }

    public ModificaInformazioniPazienteController() {

    }
}
