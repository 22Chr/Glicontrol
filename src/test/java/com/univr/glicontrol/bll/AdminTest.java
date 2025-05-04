package com.univr.glicontrol.bll;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AdminTest {

    Admin admin = new Admin(1, "CHCCRS03L22F861M", "Christian", "Checchetti", "ADMIN");

    @Test
    void getRuolo() {
        assertEquals("ADMIN", admin.getRuolo());
    }

    @Test
    void getIdUtente() {
        assertEquals(1, admin.getIdUtente());
    }

    @Test
    void getCodiceFiscale() {
        assertEquals("CHCCRS03L22F861M", admin.getCodiceFiscale());
    }

    @Test
    void getNome() {
        assertEquals("Christian", admin.getNome());
    }

    @Test
    void getCognome() {
        assertEquals("Checchetti", admin.getCognome());
    }
}
