package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class RilevazioneGlicemicaTest {
    @Test
    void testCostruttoreEGetter() {

        int idRilevazione = 1;
        int idPaziente = 100;
        Date data = Date.valueOf("2025-07-06");
        Time ora = Time.valueOf("08:30:00");
        int valore = 110;
        String pasto = "Colazione";
        String indicazioniTemporali = "Prima del pasto";

        RilevazioneGlicemica rilevazione = new RilevazioneGlicemica(
                idRilevazione,
                idPaziente,
                data,
                ora,
                valore,
                pasto,
                indicazioniTemporali,
                false
        );

        assertEquals(idRilevazione, rilevazione.getIdRilevazione());
        assertEquals(idPaziente, rilevazione.getIdPaziente());
        assertEquals(data, rilevazione.getData());
        assertEquals(ora, rilevazione.getOra());
        assertEquals(valore, rilevazione.getValore());
        assertEquals(pasto, rilevazione.getPasto());
        assertEquals(indicazioniTemporali, rilevazione.getIndicazioniTemporali());
    }

}