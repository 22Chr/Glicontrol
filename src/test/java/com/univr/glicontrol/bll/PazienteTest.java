package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class PazienteTest {

    Date dataNascita = Date.valueOf("2004-01-22");
    Paziente paziente = new Paziente(2, "MRTNNA04A62H612X", "Anna", "Martini", "PAZIENTE", 3, dataNascita, "F", "anna.martini04@gmail.com", "null");

    @Test
    void getMedicoRiferimento() {
        assertEquals(3, paziente.getMedicoRiferimento());
    }

    @Test
    void getDataNascita() {
        assertEquals(Date.valueOf("2004-01-22"), paziente.getDataNascita());
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
    void getRuolo() {
        assertEquals("PAZIENTE", paziente.getRuolo());
    }

    @Test
    void getIdUtente() {
        assertEquals(2, paziente.getIdUtente());
    }

    @Test
    void getCodiceFiscale() {
        assertEquals("MRTNNA04A62H612X", paziente.getCodiceFiscale());
    }

    @Test
    void getNome() {
        assertEquals("Anna", paziente.getNome());
    }

    @Test
    void getCognome() {
        assertEquals("Martini", paziente.getCognome());
    }

}