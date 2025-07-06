package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class PastoTest {
    @Test
    void testCostruttoreEGetter() {
        int idPasto = 10;
        int idPaziente = 5;
        String nomePasto = "Cena";
        Time orario = Time.valueOf(LocalTime.of(20, 0));

        Pasto pasto = new Pasto(idPasto, idPaziente, nomePasto, orario);

        assertEquals(idPasto, pasto.getIdPasto());
        assertEquals(idPaziente, pasto.getIdPaziente());
        assertEquals(nomePasto, pasto.getNomePasto());
        assertEquals(orario, pasto.getOrario());
    }

    @Test
    void testSetOrario() {
        Time orarioIniziale = Time.valueOf(LocalTime.of(12, 0));
        Time nuovoOrario = Time.valueOf(LocalTime.of(13, 30));
        Pasto pasto = new Pasto(1, 2, "Pranzo", orarioIniziale);

        pasto.setOrario(nuovoOrario);

        assertEquals(nuovoOrario, pasto.getOrario());
    }

}