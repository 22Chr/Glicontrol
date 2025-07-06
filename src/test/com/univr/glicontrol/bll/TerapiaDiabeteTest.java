package com.univr.glicontrol.bll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TerapiaDiabeteTest {
    private TerapiaDiabete terapia;
    private Farmaco farmaco1;
    private Farmaco farmaco2;
    private List<Float> dosaggi;

    @BeforeEach
    void setUp() {
        dosaggi = Arrays.asList(5.0f, 10.0f, 20.0f);
        farmaco1 = new Farmaco(28, "Tritace 1.25 mg", "Ramipril",dosaggi , "mg", "King/Farmacos", "orale", "tosse secca persistente, ipotensione", "Elettroliti, litio", "Antipertensivo");
        farmaco2 = new Farmaco(29, "Norvasc 5 mg", "Amlodipina", dosaggi, "mg", "Pfizer", "orale", "cefalea, capogiri, edemi periferici, nausea", "inibitori CYP3A4, statine", "Antipertensivo");

        IndicazioniFarmaciTerapia indicazioni1 = new IndicazioniFarmaciTerapia(1,28,20.0f, "2 volte al die", "08:00, 20:00");
        IndicazioniFarmaciTerapia indicazioni2 = new IndicazioniFarmaciTerapia(3, 29,500.0f, "1 volta al die", "10:00");

        FarmacoTerapia ft1 = new FarmacoTerapia(farmaco1, indicazioni1);
        FarmacoTerapia ft2 = new FarmacoTerapia(farmaco2, indicazioni2);

        List<FarmacoTerapia> farmaci = Arrays.asList(ft1, ft2);

        terapia = new TerapiaDiabete(
                6,
                2,
                Date.valueOf("2024-01-01"),
                Date.valueOf("2024-12-31"),
                farmaci
        );

        terapia.setNoteTerapia("Monitorare il paziente");
        terapia.setIdTerapiaDiabete(99);
    }

    @Test
    void testGettersBase() {
        assertEquals(6, terapia.getIdPaziente());
        assertEquals(2, terapia.getIdMedicoUltimaModifica());
        assertEquals("Monitorare il paziente", terapia.getNoteTerapia());
        assertEquals(99, terapia.getIdTerapia());
        assertEquals("Terapia diabete", terapia.getNome());
        assertEquals(-5, terapia.getIdPatologiaConcomitante());
    }

    @Test
    void testSetMedicoUltimaModifica() {
        terapia.setIdMedicoUltimaModifica(8);
        assertEquals(8, terapia.getIdMedicoUltimaModifica());
    }

    @Test
    void testSetDataFine() {
        Date nuovaDataFine = Date.valueOf("2025-01-01");
        terapia.setDataFine(nuovaDataFine);
        assertEquals(nuovaDataFine, terapia.getDataFine());
    }

    @Test
    void testFarmaciAccessoSingolo() {
        assertEquals(20.0f, terapia.getDosaggioPerFarmaco("Tritace 1.25 mg"));
        assertEquals("2 volte al die", terapia.getFrequenzaPerFarmaco("Tritace 1.25 mg"));
        assertEquals("08:00, 20:00", terapia.getOrarioPerFarmaco("Tritace 1.25 mg"));

        assertEquals(500.0f, terapia.getDosaggioPerFarmaco("Norvasc 5 mg"));
        assertEquals("1 volta al die", terapia.getFrequenzaPerFarmaco("Norvasc 5 mg"));
        assertEquals("10:00", terapia.getOrarioPerFarmaco("Norvasc 5 mg"));
    }

    @Test
    void testFarmacoNonPresente() {
        assertEquals(0.0f, terapia.getDosaggioPerFarmaco("FarmacoInesistente"));
        assertEquals("", terapia.getFrequenzaPerFarmaco("FarmacoInesistente"));
        assertEquals("", terapia.getOrarioPerFarmaco("FarmacoInesistente"));
    }

    @Test
    void testSetListaFarmaciTerapia() {
        Farmaco farmaco3 = new Farmaco(34, "Tricor 145 mg", "Fenofibrato", dosaggi, "mg", "Abbott/Recordati", "orale", "disturbi GI, eruzioni cutanee, iperuricemia", "statine, warfarin, ciclosporina", "Ipolipemizzante");
        IndicazioniFarmaciTerapia ind = new IndicazioniFarmaciTerapia(3, 34, 145.0f,  "1 volta al die", "14:00");
        FarmacoTerapia nuovaFT = new FarmacoTerapia(farmaco3, ind);

        terapia.setListaFarmaciTerapia(List.of(nuovaFT));

        assertEquals(145.0f, terapia.getDosaggioPerFarmaco("Tricor 145 mg"));
        assertEquals("1 volta al die", terapia.getFrequenzaPerFarmaco("Tricor 145 mg"));
        assertEquals("14:00", terapia.getOrarioPerFarmaco("Tricor 145 mg"));
    }

}