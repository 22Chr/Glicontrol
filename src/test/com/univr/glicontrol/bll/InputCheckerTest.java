package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class InputCheckerTest {

    @Test
    void verificaCodiceFiscale() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaCodiceFiscale("CHCCRS03L22F861M"));
        assertFalse(ic.verificaCodiceFiscale("CHCCRS03L22F861M1"));
        assertFalse(ic.verificaCodiceFiscale("CH3CCRS03L22F861"));
    }

    @Test
    void verificaPassword() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaPassword("PinkFloyd!"));
        assertFalse(ic.verificaPassword("1234"));
    }

    @Test
    void verificaEmail() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaEmail("chris22checchetti@gmail.com"));
        assertFalse(ic.verificaEmail("test.com"));
        assertFalse(ic.verificaEmail("test"));
        assertFalse(ic.verificaEmail("test@"));
        assertFalse(ic.verificaEmail("test@gmail@gmail.com"));
    }

    @Test
    void verificaSesso() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaSesso("M"));
        assertTrue(ic.verificaSesso("F"));
        assertTrue(ic.verificaSesso("m"));
        assertTrue(ic.verificaSesso("f"));
        assertFalse(ic.verificaSesso("N"));
        assertFalse(ic.verificaSesso("Elicottero"));
    }

    @Test
    void verificaMedico() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaMedico(5));
        assertFalse(ic.verificaMedico(0));
        assertFalse(ic.verificaMedico(-1));
    }

    @Test
    void verificaNascita() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaNascita(Date.valueOf("1972-03-20")));
        assertThrows(IllegalArgumentException.class, () -> ic.verificaNascita(Date.valueOf("1972-22-05")));
        assertFalse(ic.verificaNascita(Date.valueOf("1872-03-05")));
        assertFalse(ic.verificaNascita(Date.valueOf("2026-03-05")));
    }

    @Test
    void verificaNome() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaNome("Maria"));
        assertFalse(ic.verificaNome(""));
        assertFalse(ic.verificaNome("123"));
    }

    @Test
    void verificaCognome() {
        InputChecker ic = new InputChecker();
        assertTrue(ic.verificaCognome("Rossi"));
        assertFalse(ic.verificaCognome(""));
        assertFalse(ic.verificaCognome("123"));
    }
}