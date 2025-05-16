package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class InserisciPazienteTest {

    InserisciPaziente ip = new InserisciPaziente();

    @Test
    void insertPazienteFalliscePerPresenzaDellUtente() {
        assertEquals(-1, ip.insertPaziente("MRTNNA04A62H612X", "Anna", "Martini", "CalDigit04", 1, Date.valueOf("2004-01-22"), "F", "anna.martini2004@gmail.com", null, 60.2f));
    }

    @Test
    void inserisciPaziente() {
        assertEquals(1, ip.insertPaziente("LNIMRC05L12D887G", "Marco", "Leoni", "Ruggito123", 3, Date.valueOf("1987-05-12"), "M", "bohnondeveesistere@gmail.com", null, 38.1f));
    }

    @Test
    void fallisciInserisciPaziente() {
        assertEquals(0, ip.insertPaziente("LNIMRC05L12D887H", "Marco", "Lioni", "Stanley12333546", 1, Date.valueOf("1987-05-12"), "M", "bohnondeveesistere@gmail.com", null, 38.1f));
    }
}