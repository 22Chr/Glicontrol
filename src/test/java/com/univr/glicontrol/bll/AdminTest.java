package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    Admin admin = new Admin(1, "CHCCRS03L22F861M", "Christian", "Checchetti", "ADMIN");

    @Test
    void testGetNome() {
        assertEquals("Christian", admin.getNome());
    }

    @Test
    void testGetIdUtente() {
        assertEquals(1, admin.getIdUtente());
    }

    @Test
    void testGetCognome() {
        assertEquals("Checchetti", admin.getCognome());
    }

    @Test
    void testGetCodiceFiscale() {
        assertEquals("CHCCRS03L22F861M", admin.getCodiceFiscale());
    }

    @Test
    void testGetRuolo() {
        assertEquals("ADMIN", admin.getRuolo());
    }
}