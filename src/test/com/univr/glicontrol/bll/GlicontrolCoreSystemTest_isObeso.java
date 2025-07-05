package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class GlicontrolCoreSystemTest_isObeso {

    //TEST BASILARE
    @Test
    void testIsObeso_true() {
        Paziente p = new Paziente(7, "MRTNNA04A62H612X", "Anna", "Martini",
                "CarloDiana", "Paziente", 2,
                Date.valueOf("2004-01-22"),
                "F", "anna.martini2004@gmail.com", "Amoxicillina", 1);
        p.setAltezza(170);
        p.setPeso(88f);

        boolean risultato = GlicontrolCoreSystem.getInstance().isObeso(p);
        assertTrue(risultato);
    }

    @Test
    void testIsObeso_false() {
        Paziente p = new Paziente(7, "MRTNNA04A62H612X", "Anna", "Martini",
                "CarloDiana", "Paziente", 2,
                Date.valueOf("2004-01-22"),
                "F", "anna.martini2004@gmail.com", "Amoxicillina", 1);
        p.setAltezza(165);
        p.setPeso(60f);

        boolean risultato = GlicontrolCoreSystem.getInstance().isObeso(p);
        assertFalse(risultato);
    }

}