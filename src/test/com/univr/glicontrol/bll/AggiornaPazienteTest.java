package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class AggiornaPazienteTest {

    @Test
    void updatePazientePortaleAdmin() {
        Paziente paziente = new Paziente(10, "TRLTRS04A76Y542T", "Teresa", "Turella", "CiaoTeresa", "PAZIENTE", 8, Date.valueOf("2004-08-06"), "F", "teresa.turella@gmail.com", null, 87 , 1);

        AggiornaPaziente aggiorna = new AggiornaPaziente(paziente);

        assertTrue(aggiorna.aggiornaPaziente(), "L'aggiornamento del paziente dovrebbe andare a buon fine");
    }

    @Test
    void inviaCredenzialiAggiornatePaziente() {
        AggiornaPaziente aggiorna = new AggiornaPaziente(null);

        String emailDestinatario = "anna.martini2004@gmail.com";
        String nuovaPassword = "test123456";

        assertTrue(aggiorna.inviaCredenzialiAggiornatePaziente(emailDestinatario, nuovaPassword), "L'invio dell'email dovrebbe andare a buon fine");
    }
}