package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class SintomoTest {
    @Test
    void testCostruttoreEGetter() {
        int idSintomo = 1;
        int idPaziente = 6;
        String descrizione = "Mal di testa";
        Date data = Date.valueOf("2024-06-01");
        Time ora = Time.valueOf("15:30:00");

        Sintomo sintomo = new Sintomo(idSintomo, idPaziente, descrizione, data, ora);

        assertEquals(idSintomo, sintomo.getIdSintomo());
        assertEquals(idPaziente, sintomo.getIdPaziente());
        assertEquals(descrizione, sintomo.getDescrizione());
        assertEquals(data, sintomo.getData());
        assertEquals(ora, sintomo.getOra());
    }

}