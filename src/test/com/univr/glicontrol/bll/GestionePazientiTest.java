package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GestionePazientiTest {
    private static final String cf = "MRTNNA04A62H612F";
    private static final String email = "anna.potterhead107@gmail.com";
    private static final String password = "CarloDiana";
    private static int idInserito;

    @Test
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
        idInserito = p.getIdUtente();
    }

    @Test
    void testPazienteGiaPresente() {
        boolean presente = GestionePazienti.getInstance().pazienteGiaPresente(cf);
        assertTrue(presente);
    }

    @Test
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
    void testEliminaPaziente() {
        GestionePazienti gp = GestionePazienti.getInstance();
        Paziente p = gp.getPazientePerCodiceFiscale(cf);
        boolean success = gp.eliminaPaziente(p);
        assertTrue(success);

        Paziente dopoEliminazione = gp.getPazientePerCodiceFiscale(cf);
        assertNull(dopoEliminazione);
    }

}