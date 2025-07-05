package com.univr.glicontrol.bll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class AssunzioneFarmacoTest {

    //TEST FATTO IN QUANTO IN CAMPI SONO PRIVATE ED E' IMMODIFICABILE -> NO TEST DEI SETTER

    private AssunzioneFarmaco assunzione;
    private Date data;
    private Time ora;

    @BeforeEach
    void setUp() {
        data = Date.valueOf("2025-07-05");
        ora = Time.valueOf("08:30:00");
        assunzione = new AssunzioneFarmaco(
                24,
                7,
                50,
                data,
                ora,
                10.0f
        );
    }

    @Test
    void testGetIdAssunzioneFarmaco() {
        assertEquals(24, assunzione.getIdAssunzioneFarmaco());
    }

    @Test
    void testGetIdPaziente() {
        assertEquals(7, assunzione.getIdPaziente());
    }

    @Test
    void testGetIdFarmaco() {
        assertEquals(50, assunzione.getIdFarmaco());
    }

    @Test
    void testGetData() {
        assertEquals(data, assunzione.getData());
    }

    @Test
    void testGetOra() {
        assertEquals(ora, assunzione.getOra());
    }

    @Test
    void testGetDose() {
        assertEquals(10.0f, assunzione.getDose());
    }

}