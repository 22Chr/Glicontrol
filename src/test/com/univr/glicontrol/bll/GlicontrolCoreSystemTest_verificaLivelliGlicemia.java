package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlicontrolCoreSystemTest_verificaLivelliGlicemia {

    //il paziente deve avere delle rilevazioni odierne per funzionare
    GlicontrolCoreSystem gcs = GlicontrolCoreSystem.getInstance();
    Paziente paziente = new Paziente(6, "MRTNNA04A62H612X", "Anna", "Martini",
            "CarloDiana", "Paziente", 8,
            Date.valueOf("2004-01-22"),
            "F", "anna.martini2004@gmail.com", "Amoxicillina", 0);

    @Test
    void testVerificaLivelliOdierni() {

        List<Integer> codici = gcs.verificaLivelliGlicemici(paziente, true, false);

        assertNotNull(codici);
        assertFalse(codici.isEmpty());

        for (Integer codice : codici) {
            assertTrue(codice >= -4 && codice <= 4);
        }
    }

    @Test
    void testVerificaLivelliComplessivi() {

        List<Integer> codici = gcs.verificaLivelliGlicemici(paziente, false, false);

        assertNotNull(codici);
        assertFalse(codici.isEmpty());
    }
}