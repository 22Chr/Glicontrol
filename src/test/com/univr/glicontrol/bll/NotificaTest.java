package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificaTest {

    @Test
    void testCostruttoreEGetter() {
        String titolo = "Titolo Notifica Test";
        String messaggio = "Messaggio Notifica Test";
        Paziente paziente = new Paziente(0,
                "MRTNNA04A62H612X",
                "Anna",
                "Martini",
                "CarloDiana",
                "Paziente",
                8,
                Date.valueOf("2004-01-22"),
                "F",
                "anna.martini2004@gmail.com",
                "amoxicillina",
                0);
        LocalDateTime data = LocalDateTime.of(2025, 7, 6, 18, 0);
        boolean visualizzato = false;

        Notifica notifica = new Notifica(titolo, messaggio, paziente, data, visualizzato);

        assertEquals(titolo, notifica.getTitolo());
        assertEquals(messaggio, notifica.getMessaggio());
        assertEquals(paziente, notifica.getPazienteAssociato());
        assertEquals(data, notifica.getDataNotifica());
        assertFalse(notifica.isVisualizzato());
    }

    @Test
    void testSetIdNotifica() {
        Notifica notifica = new Notifica("Titolo", "Messaggio", new Paziente(0,
                "MRTNNA04A62H612X",
                "Anna",
                "Martini",
                "CarloDiana",
                "Paziente",
                8,
                Date.valueOf("2004-01-22"),
                "F",
                "anna.martini2004@gmail.com",
                "amoxicillina",
                0),
                LocalDateTime.now(), false);

        int idAtteso = 42;
        notifica.setIdNotifica(idAtteso);

        assertEquals(idAtteso, notifica.getIdNotifica());
    }

}