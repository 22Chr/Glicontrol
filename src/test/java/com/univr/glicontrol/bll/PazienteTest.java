package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;

class PazienteTest {
    Date dataNascita = Date.valueOf("2004-01-22");
    Paziente paziente = new Paziente(2, "MRTNNA04A62H612X", "Anna", "Martini", "PAZIENTE", 3, dataNascita, "F", "anna.martini04@gmail.com", "null");

    @Test
    void getMedicoRiferimento() {
        assertEquals(3, paziente.getMedicoRiferimento());
    }

    @Test
    void getDataNascita() {
        assertEquals(dataNascita, paziente.getDataNascita());
    }

    @Test
    void getSesso() {
        assertEquals("F", paziente.getSesso());
    }

    @Test
    void getEmail() {
        assertEquals("anna.martini04@gmail.com", paziente.getEmail());
    }

    @Test
    void getAllergie() {
        assertEquals("null", paziente.getAllergie());
    }

    @Test
    void getIdGiusto() {
        assertEquals(2, paziente.getIdUtente());
    }

    @Test
    void getCodiceFiscaleGiusto() {
        assertEquals("MRTNNA04A62H612X", paziente.getCodiceFiscale());
    }

    @Test
    void getNomeGiusto() {
        assertEquals("Anna", paziente.getNome());
    }

    @Test
    void getCognomeGiusto() {
        assertEquals("Martini", paziente.getCognome());
    }

    @Test
    void getRuoloGiusto() {
        assertEquals("PAZIENTE", paziente.getRuolo());
    }

}