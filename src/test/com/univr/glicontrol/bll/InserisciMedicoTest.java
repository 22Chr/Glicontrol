package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InserisciMedicoTest {

    private InserisciMedico im = new InserisciMedico();

    @Test
    void insertMedicoEsistente() {
        assertEquals(-1, im.insertMedico("MBRSFO03H51E349E", "Sofia", "Ambrosi", "sofiaambrosi670@gmail.com", "Gelato9876"));
    }

    @Test
    void insertNuovoMedico() {
        assertEquals(1, im.insertMedico("MRNGNN02A23F568A", "Giovanni", "Morandi", "gio.moraFalso@gmail.com", "1234567890"));
    }

    @Test
    void fallisceInserimentoMedico() {
        assertEquals(0, im.insertMedico("MRNGNN02A23F568G", "Gianni", "Morandi", "gio.moraFalso@gmail.com", "123456789034564"));
    }
}