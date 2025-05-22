package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Farmaco;
import com.univr.glicontrol.bll.GestioneFarmaci;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class AccessoTerapieImplTest {
    private final AccessoTerapie at = new AccessoTerapieImpl();

    @Test
    void insertTerapia() {
        List<Farmaco> farmaci = new ArrayList<>(GestioneFarmaci.getInstance().getListaFarmaci());
        assertTrue(at.insertTerapiaDiabete(76, 3, Date.valueOf(LocalDate.now()), null, "120g, 12g, 27g", "a", "b", farmaci));
    }
}