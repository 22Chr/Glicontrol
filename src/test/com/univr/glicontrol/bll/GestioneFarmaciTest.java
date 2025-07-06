package com.univr.glicontrol.bll;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //fa rispettare ordine dei test
class GestioneFarmaciTest {
    private static Farmaco farmacoTest;
    private static List<Float> dosaggi =  Arrays.asList(5.0f, 10.0f, 20.0f);

    @BeforeAll
    static void setup() {
        farmacoTest = new Farmaco(
                0,
                "farmacoDiTest",
                "principioAttivoTest",
                dosaggi,
                "mg",
                "casaFarmaceuticaTest",
                "orale",
                "mal di testa",
                "niente",
                "Antiepilettico"
        );
    }

    @Test
    @Order(1)
    void testInserisciFarmaco() {
        GestioneFarmaci gf = GestioneFarmaci.getInstance();

        int result = gf.inserisciNuovoFarmaco(farmacoTest);
        assertEquals(1, result);

        Farmaco recuperato = gf.getFarmacoByName("farmacoDiTest");
        assertNotNull(recuperato);
        farmacoTest = recuperato;
    }

    @Test
    @Order(2)
    void testGetFarmacoById() {
        GestioneFarmaci gf = GestioneFarmaci.getInstance();

        Farmaco trovato = gf.getFarmacoById(farmacoTest.getIdFarmaco());
        assertNotNull(trovato);
        assertEquals("farmacoDiTest", trovato.getNome());
    }

    @Test
    @Order(3)
    void testAggiornaFarmaco() {
        GestioneFarmaci gf = GestioneFarmaci.getInstance();

        farmacoTest.setProduttore("Angelini");
        boolean updated = gf.aggiornaFarmaco(farmacoTest);
        assertTrue(updated);

        Farmaco aggiornato = gf.getFarmacoById(farmacoTest.getIdFarmaco());
        assertEquals("Angelini", aggiornato.getProduttore());
    }

    @Test
    @Order(4)
    void testEliminaFarmaco() {
        GestioneFarmaci gf = GestioneFarmaci.getInstance();

        boolean deleted = gf.eliminaFarmaco(farmacoTest.getIdFarmaco());
        assertTrue(deleted);

        Farmaco shouldBeNull = gf.getFarmacoById(farmacoTest.getIdFarmaco());
        assertNull(shouldBeNull);
    }

    @Test
    @Order(5)
    void testListaFarmaciNotNull() {
        List<Farmaco> farmaci = GestioneFarmaci.getInstance().getListaFarmaci();
        assertNotNull(farmaci);
    }

}