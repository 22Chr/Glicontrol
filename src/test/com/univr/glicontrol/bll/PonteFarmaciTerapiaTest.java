package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PonteFarmaciTerapiaTest {

    @Test
    void testCostruttoreEGetterSetter() {
        int idFarmaciTerapia = 1;
        int idTerapia = 10;
        int idFarmaco = 100;

        PonteFarmaciTerapia ponte = new PonteFarmaciTerapia(idFarmaciTerapia, idTerapia, idFarmaco);

        assertEquals(idTerapia, ponte.getIdTerapia());
        assertEquals(idFarmaco, ponte.getIdFarmaco());

        ponte.setIdFarmaco(200);
        assertEquals(200, ponte.getIdFarmaco());
    }

}