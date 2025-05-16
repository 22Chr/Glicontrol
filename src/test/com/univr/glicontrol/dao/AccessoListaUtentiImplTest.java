package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccessoListaUtentiImplTest {

    @Test
    void recuperaTuttiIMedici() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        List<Medico> Medici = accessoListaUtenti.recuperaTuttiIMedici();
        assertEquals(2, Medici.size());

        for (Medico m : Medici) {
            assertNotNull(m);
            assertInstanceOf(Medico.class, m);
            assertNotNull(m.getCodiceFiscale());
            assertNotNull(m.getNome());
            assertNotNull(m.getCognome());
            assertNotNull(m.getRuolo());
            assertNotNull(m.getEmail());
        }
    }

    @Test
    void recuperaTuttiIPazienti() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        List<Paziente> Pazienti = accessoListaUtenti.recuperaTuttiIPazienti();
        assertEquals(1, Pazienti.size());

        for (Paziente p : Pazienti) {
            assertNotNull(p);
            assertInstanceOf(Paziente.class, p);
            assertNotNull(p.getCodiceFiscale());
            assertNotNull(p.getNome());
            assertNotNull(p.getCognome());
            assertNotNull(p.getRuolo());
            assertNotNull(p.getEmail());
            assertNotEquals(0, p.getPeso());
        }
    }

    @Test
    void insertNuovoMedico() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.insertNuovoMedico("TestMedico", "TestMedico", "TestMedico", "TestMedico", "TestMedico");
        assertTrue(success);
    }

    @Test
    void insertNuovoPaziente() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.insertNuovoPaziente("TestPaziente", "TestPaziente", "TestPaziente", "TestPazien", 4, Date.valueOf("1972-03-20"), "M", "TestPaziente", "TestPaziente", 30, 1);
        assertTrue(success);
    }

    @Test
    void failInsertNuovoMedico() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.insertNuovoMedico("TestMedico", "TestMedico", "TestMedico", "TestMedico", "TestMedico15");
        assertFalse(success);
    }

    @Test
    void failInsertNuovoPaziente() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.insertNuovoPaziente("TestPaziente", "TestPaziente", "TestPaziente", "TestPaziente", 1, Date.valueOf("1972-03-20"), "M", "TestPaziente", "TestPaziente", 300, 1);
        assertFalse(success);
    }

    @Test
    void failUpdateListaMedici() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.updateMedico(1, "TestMedico", "TestMedico", "TestMedico", "TestMedico153", "TestMedico");
        assertFalse(success);
    }

    @Test
    void failUpdateListaPazienti() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.updatePaziente(1, "TestPaziente", "TestPaziente", "TestPaziente", "TestPaziente153", 1, Date.valueOf("1972-03-20"), "M", "TestPaziente", "TestPaziente", 300, 1);
        assertFalse(success);
    }

    @Test
    void failDeleteMedico() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.deleteMedico(40);
        assertFalse(success);
    }

    @Test
    void failDeletePaziente() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        boolean success = accessoListaUtenti.deletePaziente(40);
        assertFalse(success);
    }
}