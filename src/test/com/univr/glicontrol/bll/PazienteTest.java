package com.univr.glicontrol.bll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PazienteTest {
    private Paziente paziente;

    @BeforeEach
    void setUp() throws Exception {
        paziente = new Paziente(
6,
                "MRTNNA04A62H612X",
                "Anna",
                "Martini",
                "CarloDiana",
                "Paziente",
                8,
                Date.valueOf("2004-01-22"),
                "F",
                "anna.martini2004@gmail.com",
                "amoxicillina",
                0

        );
        paziente.setAltezza(165);
        paziente.setPeso(60);
    }

    @Test
    void testGettersEreditatiDaUtente() {
        assertEquals(6, paziente.getIdUtente());
        assertEquals("MRTNNA04A62H612X", paziente.getCodiceFiscale());
        assertEquals("Anna", paziente.getNome());
        assertEquals("Martini", paziente.getCognome());
        assertEquals("CarloDiana", paziente.getPassword());
        assertEquals("Paziente", paziente.getRuolo());
    }

    @Test
    void testGettersPaziente() {
        assertEquals(8, paziente.getMedicoRiferimento());
        assertEquals(Date.valueOf("2004-01-22"), paziente.getDataNascita());
        assertEquals("F", paziente.getSesso());
        assertEquals("anna.martini2004@gmail.com", paziente.getEmail());
        assertEquals("amoxicillina", paziente.getAllergie());
        assertEquals(165, paziente.getAltezza());
        assertEquals(60, paziente.getPeso(), 0.001);
        assertEquals(0, paziente.getPrimoAccesso());
    }

    @Test
    void testSetters() {
        paziente.setNome("Franco");
        paziente.setCognome("Rossi");
        paziente.setCodiceFiscale("RSSFRC96A78G981X");
        paziente.setPassword("password10");
        paziente.setRuolo("Paziente");
        paziente.setEmail("franco.rossi876@gmail.com");
        paziente.setAllergie("amoxicillina");
        paziente.setAltezza(180);
        paziente.setPeso(75.2f);
        paziente.setPrimoAccesso(0);
        paziente.setSesso("M");
        paziente.setDataNascita(Date.valueOf("1985-06-15"));
        paziente.setMedicoRiferimento(2);

        assertEquals("Franco", paziente.getNome());
        assertEquals("Rossi", paziente.getCognome());
        assertEquals("RSSFRC96A78G981X", paziente.getCodiceFiscale());
        assertEquals("password10", paziente.getPassword());
        assertEquals("Paziente", paziente.getRuolo());
        assertEquals("franco.rossi876@gmail.com", paziente.getEmail());
        assertEquals("amoxicillina", paziente.getAllergie());
        assertEquals(180, paziente.getAltezza());
        assertEquals(75.2f, paziente.getPeso(), 0.001);
        assertEquals(0, paziente.getPrimoAccesso());
        assertEquals("M", paziente.getSesso());
        assertEquals(Date.valueOf("1985-06-15"), paziente.getDataNascita());
        assertEquals(2, paziente.getMedicoRiferimento());
    }
}
