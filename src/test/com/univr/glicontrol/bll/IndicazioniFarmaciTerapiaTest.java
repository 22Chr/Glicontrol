package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndicazioniFarmaciTerapiaTest {

    @Test
    void testCostruttoreEGetter() {
        IndicazioniFarmaciTerapia terapia = new IndicazioniFarmaciTerapia(1, 50, 10.0f, "2 volte al die", "08:00, 20:00");

        assertEquals(1, terapia.getIdTerapiaDiabeteAnnessa());
        assertEquals(50, terapia.getIdFarmacoAnnesso());
        assertEquals(10.0f, terapia.getDosaggio());
        assertEquals("2 volte al die", terapia.getFrequenzaAssunzione());
        assertEquals("08:00, 20:00", terapia.getOrariAssunzione());
    }

    @Test
    void testSetter() {
        IndicazioniFarmaciTerapia terapia = new IndicazioniFarmaciTerapia(1, 50, 10.0f, "una volta al die", "12:00");

        terapia.setIdIndicazioniFarmaci(10);
        terapia.setDosaggio(30.0f);
        terapia.setFrequenzaAssunzione("tre volte al die");
        terapia.setOrariAssunzione("08:00, 14:00, 20:00");

        assertEquals(10, terapia.getIdIndicazioniFarmaci());
        assertEquals(30.0f, terapia.getDosaggio());
        assertEquals("tre volte al die", terapia.getFrequenzaAssunzione());
        assertEquals("08:00, 14:00, 20:00", terapia.getOrariAssunzione());
    }

    @Test
    void testEqualsEHashCode() {
        IndicazioniFarmaciTerapia terapia1 = new IndicazioniFarmaciTerapia(1, 50, 20.0f, "2 volte al die", "08:00, 20:00");
        IndicazioniFarmaciTerapia terapia2 = new IndicazioniFarmaciTerapia(1, 50, 20.0f, "2 volte al die", "08:00, 20:00");

        terapia1.setIdIndicazioniFarmaci(5);
        terapia2.setIdIndicazioniFarmaci(5);

        assertEquals(terapia1, terapia2);
        assertEquals(terapia1.hashCode(), terapia2.hashCode());
    }

    @Test
    void testNotEquals() {
        IndicazioniFarmaciTerapia terapia1 = new IndicazioniFarmaciTerapia(1, 101, 20.5f, "2 volte al die", "08:00, 20:00");
        IndicazioniFarmaciTerapia terapia2 = new IndicazioniFarmaciTerapia(2, 102, 10.5f, "1 volta al die", "10:00");

        terapia1.setIdIndicazioniFarmaci(5);
        terapia2.setIdIndicazioniFarmaci(6);

        assertNotEquals(terapia1, terapia2);
    }

    @Test
    void testEqualsConOggettoNull() {
        IndicazioniFarmaciTerapia terapia = new IndicazioniFarmaciTerapia(1, 50, 20.5f, "2 volte al die", "08:00, 20:00");
        terapia.setIdIndicazioniFarmaci(5);
        assertNotEquals(null, terapia);
    }

    @Test
    void testEqualsConClasseDiversa() {
        IndicazioniFarmaciTerapia terapia = new IndicazioniFarmaciTerapia(1, 50, 20.0f, "2 volte al die", "08:00, 20:00");
        terapia.setIdIndicazioniFarmaci(5);
        assertNotEquals(terapia, "stringa di esempio");
    }

}