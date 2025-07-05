package com.univr.glicontrol.bll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FarmacoTest {
    private Farmaco farmaco;
    private List<Float> dosaggi;

    @BeforeEach
    void setUp() {
        dosaggi = Arrays.asList(5.0f, 10.0f, 20.0f);
        farmaco = new Farmaco(
                50,
                "Tachipirina 500 mg",
                "Paracetamolo",
                dosaggi,
                "mg",
                "Angelini",
                "orale",
                "rash cutanei, rari casi di epatossicità",
                "alcol, warfarin",
                "Analgesico"
        );
    }

    @Test
    void testCostruttoreEGetter() {
        assertEquals(50, farmaco.getIdFarmaco());
        assertEquals("Tachipirina 500 mg", farmaco.getNome());
        assertEquals("Paracetamolo", farmaco.getPrincipioAttivo());
        assertEquals(dosaggi, farmaco.getDosaggio());
        assertEquals("mg", farmaco.getUnitaMisura());
        assertEquals("Angelini", farmaco.getProduttore());
        assertEquals("orale", farmaco.getViaSomministrazione());
        assertEquals("rash cutanei, rari casi di epatossicità", farmaco.getEffettiCollaterali());
        assertEquals("alcol, warfarin", farmaco.getInterazioniNote());
        assertEquals("Analgesico", farmaco.getTipologia());
    }

    @Test
    void testSetter() {
        farmaco.setNome("Brufen 600 mg");
        farmaco.setPrincipioAttivo("Ibuprofene");
        farmaco.setDosaggio(Arrays.asList(200.0f, 400.0f));
        farmaco.setUnitaMisura("mg");
        farmaco.setProduttore("Reckitt Benckiser");
        farmaco.setViaSomministrazione("orale");
        farmaco.setEffettiCollaterali("disturbi GI, vertigini, edema");
        farmaco.setInterazioniNote("warfarin, ASA, SSRIs, ACE-inibitori");
        farmaco.setTipologia("FANS");

        assertEquals("Brufen 600 mg", farmaco.getNome());
        assertEquals("Ibuprofene", farmaco.getPrincipioAttivo());
        assertEquals(Arrays.asList(200.0f, 400.0f), farmaco.getDosaggio());
        assertEquals("mg", farmaco.getUnitaMisura());
        assertEquals("Reckitt Benckiser", farmaco.getProduttore());
        assertEquals("orale", farmaco.getViaSomministrazione());
        assertEquals("disturbi GI, vertigini, edema", farmaco.getEffettiCollaterali());
        assertEquals("warfarin, ASA, SSRIs, ACE-inibitori", farmaco.getInterazioniNote());
        assertEquals("FANS", farmaco.getTipologia());
    }

    @Test
    // controlla che due oggetti uguali siano considerati uguali
    void testEqualsAndHashCode() {
        List<Float> sameDosaggi = Arrays.asList(5.0f, 10.0f, 20.0f);
        Farmaco nuovoFarmaco = new Farmaco(
                50,
                "Tachipirina 500 mg",
                "Paracetamolo",
                sameDosaggi,
                "mg",
                "Angelini",
                "orale",
                "rash cutanei, rari casi di epatossicità",
                "alcol, warfarin",
                "Analgesico"
        );

        assertEquals(farmaco, nuovoFarmaco);
        assertEquals(farmaco.hashCode(), nuovoFarmaco.hashCode());
    }

    @Test
    void testEqualsConOggettoDiverso() {
        Farmaco diverso = new Farmaco(
                40,
                "Aspirina Cardio 100 mg",
                "Acido acetilsalicilico",
                Arrays.asList(100.0f),
                "mg",
                "Bayer",
                "orale",
                "dispepsia, sanguinamenti gastroenterici",
                "FANS",
                "Antiaggregante piastrinico"
        );

        assertNotEquals(farmaco, diverso);
    }

}