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
    void testGetFarmacoById() {
        GestioneFarmaci gf = GestioneFarmaci.getInstance();

        Farmaco trovato = gf.getFarmacoById(farmacoTest.getIdFarmaco());
        assertNotNull(trovato);
        assertEquals("farmacoDiTest", trovato.getNome());
    }

    @Test
    @Order(2)
    void testListaFarmaciNotNull() {
        List<Farmaco> farmaci = GestioneFarmaci.getInstance().getListaFarmaci();
        assertNotNull(farmaci);
    }

}