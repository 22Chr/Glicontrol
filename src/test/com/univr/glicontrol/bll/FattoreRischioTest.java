package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class FattoreRischioTest {

    FattoriRischio fattoreRischioTest = new FattoriRischio(new Paziente(1, "CHCCRS03L22F861M", "Christian", "Checchetti", "Spring_?12", "PAZIENTE", 2, Date.valueOf("2003-07-22"), "M", "chris22checchetti@gmail.com", null, 70, 1), 1, 1, 1, 1, 1, 1);

    @Test
    void getEta() {
        assertEquals(21, fattoreRischioTest.getEta());
    }

    @Test
    void getEtaSbagliata() {
        assertNotEquals(25, fattoreRischioTest.getEta());
    }
}