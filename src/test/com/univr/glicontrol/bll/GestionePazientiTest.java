package com.univr.glicontrol.bll;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //fa rispettare ordine dei test

class GestionePazientiTest {
    private static final String cf = "MRTNNA04A62H612F";
    private static final String email = "anna.potterhead107@gmail.com";
    private static final String password = "CarloDiana";

    @Test
    @Order(1)
    void testInserimentoPaziente() {
        GestionePazienti gp = GestionePazienti.getInstance();

        int result = gp.inserisciPaziente(
                cf,
                "Anna",
                "Frisinghelli",
                password,
                2,
                Date.valueOf(LocalDate.of(2004, 3, 24)),
                "F",
                email,
                ""
        );

        assertEquals(1, result);
        Paziente p = gp.getPazientePerCodiceFiscale(cf);
        assertNotNull(p);
    }

    @Test
    @Order(2)
    void testPazienteGiaPresente() {
        boolean presente = GestionePazienti.getInstance().pazienteGiaPresente(cf);
        assertTrue(presente);
    }

    @Test
    @Order(3)
    void testAggiornaPaziente() {
        GestionePazienti gp = GestionePazienti.getInstance();
        Paziente p = gp.getPazientePerCodiceFiscale(cf);
        p.setNome("Maria");
        p.setCognome("Bianchi");
        boolean success = gp.aggiornaPaziente(p);
        assertTrue(success);

        Paziente aggiornato = gp.getPazientePerCodiceFiscale(cf);
        assertEquals("Maria", aggiornato.getNome());
        assertEquals("Bianchi", aggiornato.getCognome());
    }

    @Test
    @Order(4)
    void testEliminaPaziente() {
        GestionePazienti gp = GestionePazienti.getInstance();
        Paziente p = gp.getPazientePerCodiceFiscale(cf);
        boolean success = gp.eliminaPaziente(p);
        assertTrue(success);

        Paziente dopoEliminazione = gp.getPazientePerCodiceFiscale(cf);
        assertNull(dopoEliminazione);
    }

}