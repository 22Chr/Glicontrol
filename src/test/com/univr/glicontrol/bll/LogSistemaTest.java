package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LogSistemaTest {
    @Test
    void testCostruttoreEGetter() {
        int idLog = 1;
        int idMedico = 42;
        String descrizione = "[LOG SOMETHING]";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        LogSistema log = new LogSistema(idLog, idMedico, descrizione, timestamp);

        assertEquals(idLog, log.getIdLog());
        assertEquals(idMedico, log.getIdMedico());
        assertEquals(descrizione, log.getDescrizione());
        assertEquals(timestamp, log.getTimestamp());
    }

}