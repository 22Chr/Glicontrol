package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class InputCheckerTest {
    private final InputChecker checker = InputChecker.getInstance();

    @Test
    void testVerificaCodiceFiscaleValido() {
        assertTrue(checker.verificaCodiceFiscale("MRTNNA04A62H612X"));
    }

    @Test
    void testVerificaCodiceFiscaleNonValido() {
        assertFalse(checker.verificaCodiceFiscale("123M456L789"));
    }

    @Test
    void testVerificaEmailValida() {
        assertTrue(checker.verificaEmail("anna.martini78@gmail.com"));
    }

    @Test
    void testVerificaEmailNonValida() {
        assertFalse(checker.verificaEmail("anna@@gmail..com"));
    }

    @Test
    void testVerificaPesoValido() {
        assertTrue(checker.verificaPeso("75.5 kg"));
    }

    @Test
    void testVerificaPesoNonValido() {
        assertFalse(checker.verificaPeso("0 kg"));
    }

    @Test
    void testVerificaDataNascitaValida() {
        Date nascita = Date.valueOf("2004-01-22");
        assertTrue(checker.verificaNascita(nascita));
    }

    @Test
    void testVerificaDataNascitaFutura() {
        Date dataFutura = Date.valueOf("2999-01-01");
        assertFalse(checker.verificaNascita(dataFutura));
    }

    @Test
    void testVerificaNome() {
        assertTrue(checker.verificaNome("Anna"));
        assertFalse(checker.verificaNome("anna"));
    }

    @Test
    void testVerificaCognome() {
        assertTrue(checker.verificaCognome("Martini"));
        assertFalse(checker.verificaCognome("martini"));
    }

    @Test
    void testVerificaOrariTerapiaValidi() {
        assertTrue(checker.verificaOrariTerapia("08:00, 12:30, 18:45"));
    }

    @Test
    void testVerificaOrariTerapiaInvalidi() {
        assertFalse(checker.verificaOrariTerapia("25:00"));
    }

    @Test
    void testVerificaInputGlicemiaValida() {
        assertTrue(checker.verificaInputGlicemia("100"));
        assertFalse(checker.verificaInputGlicemia("009"));
    }

    @Test
    void testCampoVuoto() {
        assertTrue(checker.campoVuoto("non vuoto"));
        assertFalse(checker.campoVuoto(""));
    }
}