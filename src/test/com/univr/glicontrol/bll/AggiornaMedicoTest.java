package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AggiornaMedicoTest {

    @Test
    void updateMedico() {
        Medico medico = new Medico(11, "ABCDEF12G34H567I", "Giuseppe", "Verdi", "1234567890", "MEDICO", "anna.martini2004@gmail.com");
        AggiornaMedico aggiorna = new AggiornaMedico(medico);

        assertTrue(aggiorna.updateMedico(), "Il medico dovrebbe essere stato aggiornato nel database");
    }

    @Test
    void inviaCredenzialiAggiornateMedico() {
        Medico medico = new Medico(11, "ABCDEF12G34H567I", "Giuseppe", "Verdi", "1234567890", "MEDICO", "anna.martini2004@gmail.com");
        AggiornaMedico aggiorna = new AggiornaMedico(medico);

        assertTrue(aggiorna.inviaCredenzialiAggiornateMedico(medico.getEmail(), medico.getPassword()), "L'email dovrebbe essere stata inviata correttamente");
    }
}