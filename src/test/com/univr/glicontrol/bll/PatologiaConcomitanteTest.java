package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class PatologiaConcomitanteTest {

    @Test
    void testCostruttoreEGetter() {
        int idPatologia = 1;
        int idPaziente = 2;
        String nomePatologia = "Ipertensione";
        String descrizione = "Pressione alta cronica";
        Date dataInizio = Date.valueOf("2023-01-01");
        Date dataFine = Date.valueOf("2024-01-01");

        PatologiaConcomitante patologia = new PatologiaConcomitante(
                idPatologia, idPaziente, nomePatologia, descrizione, dataInizio, dataFine);

        assertEquals(idPatologia, patologia.getIdPatologia());
        assertEquals(idPaziente, patologia.getIdPaziente());
        assertEquals(nomePatologia, patologia.getNomePatologia());
        assertEquals(descrizione, patologia.getDescrizione());
        assertEquals(dataInizio, patologia.getDataInizio());
        assertEquals(dataFine, patologia.getDataFine());
    }

    @Test
    void testSetDescrizione() {
        PatologiaConcomitante patologia = new PatologiaConcomitante(
                1, 2, "Asma", "Forma lieve", Date.valueOf("2023-05-10"), null);

        String nuovaDescrizione = "Forma moderata";
        patologia.setDescrizione(nuovaDescrizione);

        assertEquals(nuovaDescrizione, patologia.getDescrizione());
    }

    @Test
    void testSetDataFine() {
        PatologiaConcomitante patologia = new PatologiaConcomitante(
                1, 2, "Asma", "Forma lieve", Date.valueOf("2023-05-10"), null);

        Date nuovaDataFine = Date.valueOf("2025-07-06");
        patologia.setDataFine(nuovaDataFine);

        assertEquals(nuovaDataFine, patologia.getDataFine());
    }

}