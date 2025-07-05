package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class GeneratoreNotificheTest {
    //TEST INTERESSANTE IN QUANTO SINGLETON
    @Test
    void testNotificaGlicemiaCriticaPrePasto() {
        Paziente paziente = new Paziente(6, "MRTNNA04A62H612X", "Anna", "Martini",
                "CarloDiana", "PAZIENTE", 8, Date.valueOf("2004-01-22"), "F",
                "anna.martini2004@gmail.com", "Amoxicillina", 0);

        GeneratoreNotifiche generatore = GeneratoreNotifiche.getInstance();

        Notifica notifica = generatore.generaNotificaLivelliGlicemiciAlterati(paziente, -3, 300);

        assertEquals("ANOMALIA LIVELLI GLICEMICI", notifica.getTitolo());
        assertTrue(notifica.getMessaggio().contains("critico\nprecedente al pasto"));
        assertEquals(paziente, notifica.getPazienteAssociato());
        assertFalse(notifica.isVisualizzato());
        assertNotNull(notifica.getDataNotifica());
    }
}