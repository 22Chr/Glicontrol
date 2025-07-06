package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LogTerapiaTest {
    @Test
    void testCostruttoreEGetter() {
        int idLogTerapia = 15;
        int idTerapia = 16;
        int idMedico = 2;
        String descrizioneModifiche = "[LOG TERAPIE] - DESCRIZIONE:\n" +
                "Il medico Christian Checchetti (CHCCRS03L22F861M) ha apportato delle modifiche alla terapia Terapia cardiopatia";
        String note = "Tenere monitorata la situazione";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        LogTerapia log = new LogTerapia(idLogTerapia, idTerapia, idMedico, descrizioneModifiche, note, timestamp);

        assertEquals(idLogTerapia, log.getIdLog());
        assertEquals(idTerapia, log.getIdTerapia());
        assertEquals(idMedico, log.getIdMedico());
        assertEquals(descrizioneModifiche, log.getDescrizione());
        assertEquals(note, log.getNotePaziente());
        assertEquals(timestamp, log.getTimestamp());
    }

    @Test
    void testSetNotePaziente() {
        LogTerapia log = new LogTerapia(15, 16, 2, "Descrizione", "Nota", new Timestamp(System.currentTimeMillis()));

        log.setNotePaziente("Nuove note");
        assertEquals("Nuove note", log.getNotePaziente());
    }

}