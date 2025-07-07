package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LogInfoPazienteTest {
    @Test
    public void testCostruttoreEGetter() {
        int idLog = 1;
        int idMedico = 2;
        int idPaziente = 7;
        String descrizione = "Test descrizione";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        LogInfoPaziente log = new LogInfoPaziente(idLog, idMedico, idPaziente, descrizione, timestamp);

        assertEquals(idLog, log.getIdLog());
        assertEquals(idMedico, log.getIdMedico());
        assertEquals(idPaziente, log.getIdPaziente());
        assertEquals(descrizione, log.getDescrizione());
        assertEquals(timestamp, log.getTimestamp());
    }

}