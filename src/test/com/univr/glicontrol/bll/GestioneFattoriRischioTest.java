package com.univr.glicontrol.bll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class GestioneFattoriRischioTest {
    private GestioneFattoriRischio gft;
    private Paziente paziente;

    @BeforeEach
    void setUp() {
        gft = new GestioneFattoriRischio();

        GestionePazienti.getInstance().inserisciPaziente("MRTNNA04A62H612X",
                "Anna",
                "Martini",
                "CarloDiana",
                8,
                Date.valueOf("2004-01-22"),
                "F",
                "anna.martini2004@gmail.com",
                "amoxicillina");
        paziente = GestionePazienti.getInstance().getPazientePerCodiceFiscale("MRTNNA04A62H612X");
    }

    @Test
    void testInserisciFattoriRischio() {
        boolean result = gft.inserisciFattoriRischi(paziente.getCodiceFiscale());
        assertTrue(result, "Inserimento fattori di rischio fallito");

        FattoriRischio fr = gft.getFattoriRischio(paziente.getIdUtente());
        assertNotNull(fr);
        assertEquals(paziente.getIdUtente(), fr.getIdPaziente());
    }

    @Test
    void testAggiornaFattoriRischio() {
        gft.inserisciFattoriRischi(paziente.getCodiceFiscale());
        FattoriRischio fr = gft.getFattoriRischio(paziente.getIdUtente());

        fr.setSedentarieta(1);
        fr.setFamiliarita(1);

        boolean result = gft.aggiornaFattoriRischio(fr);
        assertTrue(result, "Aggiornamento fattori rischio fallito");

        FattoriRischio aggiornato = gft.getFattoriRischio(paziente.getIdUtente());
        assertEquals(1, aggiornato.getSedentarieta());
        assertEquals(1, aggiornato.getFamiliarita());
    }

    //INFINITO
    @Test
    void testEliminaFattoriRischio() {
        gft.inserisciFattoriRischi(paziente.getCodiceFiscale());

        boolean deleted = gft.eliminaFattoriRischio(paziente.getIdUtente());
        assertTrue(deleted, "Eliminazione fattori rischio fallita");

        FattoriRischio fr = gft.getFattoriRischio(paziente.getIdUtente());
        assertNull(fr, "I fattori di rischio dovrebbero essere null dopo l'eliminazione");
    }


}