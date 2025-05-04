package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicoTest {

    Medico medico = new Medico(3, "CHCSMN04M26F861G", "Simone", "Checchetti", "MEDICO", "simo26checchetti@gmail.com");

    @Test
    void getEmailGiusta() {
        assertEquals("simo26checchetti@gmail.com", medico.getEmail());
    }

    @Test
    void getIdGiusta() {
        assertEquals(3, medico.getIdUtente());
    }

    @Test
    void getCodiceFiscaleGiusto() {
        assertEquals("CHCSMN04M26F861G", medico.getCodiceFiscale());
    }

    @Test
    void getNomeGiusto() {
        assertEquals("Simone", medico.getNome());
    }

    @Test
    void getCognomeGiusto() {
        assertEquals("Checchetti", medico.getCognome());
    }

    @Test
    void getRuoloGiusto() {
        assertEquals("MEDICO", medico.getRuolo());
    }

}